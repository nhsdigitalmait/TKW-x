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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.parser.IParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptorValidator;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceInterface;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertJson2Xml;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_LENGTH_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_TYPE_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.FHIR_XML_MIMETYPE_STU3;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SOAP_ACTION_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.FHIRCONVERSIONFAILURE;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * ACKNOWLEDGED HACK! This has been put together as a waypoint to developing a
 * CDSS/EMS validator which accepts a zip file and processes its contents It is
 * no longer required but may be useful for a staring point for something else
 *
 * @author Richard Robinson
 */
public class CDSInlineFHIRValidator
        extends RoutingActor implements EvidenceInterface {

    private static Configurator config;
    private String validationReport = null;
    private final FhirContext context = FhirContext.forDstu3();
    private IParser xmlparser = null;
    private static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    static {
        try {
            config = Configurator.getConfigurator();
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, CDSInlineFHIRValidator.class.getName(), "Configurator error - " + e.toString());
        }
    }

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public CDSInlineFHIRValidator()
            throws Exception {
        super();
        init();
        // Instantiate the converter so that it is ready when it is called
        fhirConvertJson2Xml(null);
        xmlparser = context.newXmlParser();
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        HttpRequest httpRequest = (HttpRequest) obj;

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
            // 2. XML or json? - assume json, but failover to XML 
            byte[] xmlContents = fhirConvertJson2Xml(zipContents).getBytes();
            String xmlContentsStr = new String(xmlContents);
            if (!xmlContentsStr.contains(FHIRCONVERSIONFAILURE)) {
                zipContents = xmlContentsStr;
            }
            zipHash.put(entry.getName(), zipContents);

            System.out.println("zipentry= " + entry.getName());
            IBaseResource ibr = xmlparser.parseResource(zipContents);
//            File f = new File(entry.getName());
//            String filename = f.getName().substring(0, f.getName().lastIndexOf("."));
//            Utils.writeString2File("/home/riro/TKW-5.0.5/TKW/config/FHIR_CDSS_EMS/validator_reports/"+filename+".xml", zipContents);
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
            if (resourceType.toLowerCase().equals("parameters")) {
                continue;
            } else if (resourceType.toLowerCase().equals("capabilitystatement")) {
                continue;
            }
            bundleSize++;
            BundleEntryComponent nextEntry = myBundle.addEntry();
            nextEntry.setResource(next);
            nextEntry.setFullUrl(UUID.randomUUID().toString());
//            nextEntry.getRequest().setUrl(new IdType(resourceType, next.getIdElement().getIdPart(), next.getIdElement().getVersionIdPart()).getValue());
        }
//        myBundle.getTotalElement().setValue(bundleSize);

        // clone a request with xml content for the simulator to work on
        HttpRequest xmlRequest = new HttpRequest("From " + httpRequest.getRemoteAddr());
        byte[] xmlRequestBody = xmlparser.encodeResourceToString(myBundle).getBytes();

        xmlRequest.setInputStream(new ByteArrayInputStream(xmlRequestBody));
        xmlRequest.setRequestType(httpRequest.getRequestType());
        xmlRequest.setRequestContext(httpRequest.getContext());
        // clone http headers including the ssp-interaction id if there is one
        for (String headerName : httpRequest.getFieldNames()) {
            if (!headerName.toLowerCase().equals(CONTENT_LENGTH_HEADER.toLowerCase())) {
                if (headerName.toLowerCase().equals(CONTENT_TYPE_HEADER.toLowerCase())) {
                    xmlRequest.setHeader(headerName, FHIR_XML_MIMETYPE_STU3);
                } else {
                    xmlRequest.setHeader(headerName, httpRequest.getField(headerName));
                }
            }

        }
        // set the length of the transformed xml

        xmlRequest.setContentLength(xmlRequestBody.length);
        xmlRequest.setLoggingFileOutputStream(httpRequest.getLoggingFileOutputStream());
        // 3. call simulator
        // Validate the Request if its not inhibited
        HttpInterceptorValidator hiv = new HttpInterceptorValidator(config, xmlRequest.getField(SOAP_ACTION_HEADER), null);

        // register this class to be updated with the validation report via the setValidationReport method
        hiv.registerForReport((EvidenceInterface) this);
        //call to validate
        hiv.validateRequest(xmlRequest, null);

        // create a fhir OperationOutcome response encapsulating the html validation report
        OperationOutcome oo = new OperationOutcome();
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.INFORMATION)
                .setCode(OperationOutcome.IssueType.INFORMATIONAL)
                .setDiagnostics(validationReport);

        /*
            oo.Meta meta = new Meta();
            Coding hapiVersion = new Coding();
            hapiVersion.setVersion(hfvEngine.getSoftwareVersion());
            hapiVersion.setSystem("urn:nhs:digital:fhir:validator:version");
            meta.addTag(hapiVersion);
            Coding profileVersion = new Coding();
            profileVersion.setVersion(hfvEngine.getProfileVersion());
            profileVersion.setSystem("urn:nhs:digital:fhir:profile:version");
            meta.addTag(profileVersion);
            Resource resource = (Resource) oo;
            resource.setMeta(meta);
         */
        String ooResults = context.newXmlParser().setPrettyPrint(true).encodeResourceToString(oo);

        // no need to convert to json if requested as the HTTPInterceptor framework already does this
        return ooResults;

    }

    @Override
    public void setValidationReport(String s) {
        validationReport = s;
    }

}
