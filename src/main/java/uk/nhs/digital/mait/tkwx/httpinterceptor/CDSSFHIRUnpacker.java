/*
  Copyright 2020 Richard Robinson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package uk.nhs.digital.mait.tkwx.httpinterceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.parser.IParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertJson2Xml;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_LENGTH_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_TYPE_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.FHIR_XML_MIMETYPE_STU3;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.FHIRCONVERSIONFAILURE;

/**
 * ACKNOWLEDGED HACK! This has been put together as a waypoint to developing a
 * CDSS/EMS validator which accepts a zip file and processes its contents It is
 * no longer required but may be useful for a staring point for something else
 *
 * @author Richard Robinson
 */
public class CDSSFHIRUnpacker {
    
    private static final FhirContext context = FhirContext.forDstu3();
    private static IParser xmlparser = context.newXmlParser();
    private static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    private final String fhirConvertJson2Xml = fhirConvertJson2Xml(null);
    
    public static HttpRequest unpack(HttpRequest httpRequest)
            throws Exception {
        System.out.println("UNPACK CALLED");
        // if the submission is a zipped list of fhir interactions
        // This has been implemented for CDS/EMS (Clinical Decision Service/Encounter Management Service)
        // 1. Deflate
        byte[] content = httpRequest.getBody();
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(content.length);
        //decode if necessary
        String contentTransferEncoding = httpRequest.getHeaderManager().getHttpHeaderValue(CONTENT_TRANSFER_ENCODING);
        if (contentTransferEncoding != null && contentTransferEncoding.toLowerCase().equals("base64")) {
            Base64 b64 = new Base64();
            content = b64.decode(content);
        }
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(content));
        ZipEntry entry = zipIn.getNextEntry();
        HashMap<String, String> zipHash = new HashMap<>();
        ArrayList<IBaseResource> theResources = new ArrayList<>();
        while (entry != null) {
            outputStream.reset();
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            zipIn.closeEntry();
            String zipContents = outputStream.toString();
            if (zipContents.trim().length() == 0) {
                entry = zipIn.getNextEntry();
                continue;
            } else if (zipContents.trim().startsWith("<")) {
                //Assume XML

            } else {
                // Assume json 
                byte[] xmlContents = fhirConvertJson2Xml(zipContents).getBytes();
                String xmlContentsStr = new String(xmlContents);
                if (!xmlContentsStr.contains(FHIRCONVERSIONFAILURE)) {
                    //Successful Conversion to XML from json
                    zipContents = xmlContentsStr;
                } else {
                    if (zipContents.trim().startsWith("{")) {
                        // Assume that this is json content but not FHIR json and we will ignore
                        Logger.getInstance().log(SEVERE, CDSSFHIRUnpacker.class.getName(), entry.getName() + ": non-FHIR json content detected and rejected from validation: " + xmlContentsStr);
                    } else {
                        Logger.getInstance().log(SEVERE, CDSSFHIRUnpacker.class.getName(), entry.getName() + ": invalid content detected: " + xmlContentsStr);
                    }
                    entry = zipIn.getNextEntry();
                    continue;
                }
            }
            zipHash.put(entry.getName(), zipContents);

//            System.out.println("zipentry= " + entry.getName());
            IBaseResource ibr = xmlparser.parseResource(zipContents);
            theResources.add(ibr);
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        outputStream.close();
        outputStream.flush();

        // 3. create FHIR Bundle
        BundleTypeEnum theBundleType = BundleTypeEnum.COLLECTION;
        Bundle myBundle = new Bundle();
        myBundle.setId(UUID.randomUUID().toString());
        myBundle.getMeta().setLastUpdated(new Date());
        myBundle.getTypeElement().setValueAsString(theBundleType.getCode());
        int bundleSize = 0;
        for (IBaseResource nextBaseRes : theResources) {
            Resource next = (Resource) nextBaseRes;
            String resourceType = context.getResourceDefinition(next).getName();
            switch (resourceType.toLowerCase()) {
                case "parameters":
                    continue;
                case "capabilitystatement":
                    continue;
                default:
                    break;
            }
            bundleSize++;
            BundleEntryComponent nextEntry = myBundle.addEntry();
            nextEntry.setResource(next);
            nextEntry.setFullUrl(UUID.randomUUID().toString());
        }
        // clone a request with xml content for the simulator to work on
        HttpRequest xmlRequest = new HttpRequest("From " + httpRequest.getRemoteAddr());
        byte[] xmlRequestBody = xmlparser.encodeResourceToString(myBundle).getBytes();
        
        xmlRequest.setInputStream(
                new ByteArrayInputStream(xmlRequestBody));
        xmlRequest.setRequestType(httpRequest.getRequestType());
        xmlRequest.setRequestContext(httpRequest.getContext());
        // clone http headers including the ssp-interaction id if there is one
        for (String headerName : httpRequest.getFieldNames()) {
            if (headerName.toLowerCase().equals(CONTENT_LENGTH_HEADER.toLowerCase())) {
                xmlRequest.setHeader(headerName, Integer.toString(xmlRequestBody.length));
            } else if (headerName.toLowerCase().equals(CONTENT_TYPE_HEADER.toLowerCase())) {
                xmlRequest.setHeader(headerName, FHIR_XML_MIMETYPE_STU3);
            } else if (headerName.toLowerCase().equals(CONTENT_TRANSFER_ENCODING.toLowerCase())) {
                // Do nothing - dont copy
            } else {
                xmlRequest.setHeader(headerName, httpRequest.getField(headerName));
            }
            
        }
        // set the length of the transformed xml

        xmlRequest.setLoggingFileOutputStream(httpRequest.getLoggingFileOutputStream());
        return xmlRequest;
        
    }
    
}
