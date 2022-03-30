/*
  Copyright 2019  Richard Robinson rrobinson@nhs.net

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

import ca.uhn.fhir.context.FhirVersionEnum;
import static ca.uhn.fhir.context.FhirVersionEnum.DSTU2;
import static ca.uhn.fhir.context.FhirVersionEnum.DSTU3;
import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.StringTokenizer;
import static java.util.logging.Level.SEVERE;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_TYPE_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.FHIR_XML_MIMETYPE_DSTU2;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.FHIR_XML_MIMETYPE_STU3;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.FILEDATEMASK;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SOAP_ACTION_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.XML_MIMETYPE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.FHIR_VERSION_PROPERTY;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertJson2Xml;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;

/**
 * Creates the directories and sub directories appropriate to the type of
 * request message received
 *
 * @author riro
 */
public class HttpLogFileGenerator {

    private static final String ITK_FORWARDER_DISCRIMINATOR = "tks.httpinterceptor.itk.discriminator";
    private static final String SPINE_FORWARDER_DISCRIMINATOR = "tks.httpinterceptor.spine.discriminator";
    private static final String HTTP_HEADER_DISCRIMINATOR = "tks.httpinterceptor.http.header.discriminator";

    private static final Object lock = new Object();
    private static SimpleDateFormat FILEDATE = new SimpleDateFormat(FILEDATEMASK);
    private static Configurator config;
    private static FhirVersionEnum fhirVersion;

    static {
        try {
            config = Configurator.getConfigurator();
            fhirVersion = config.getConfiguration(FHIR_VERSION_PROPERTY) != null
                    ? FhirVersionEnum.valueOf(config.getConfiguration(FHIR_VERSION_PROPERTY).toUpperCase()) : DSTU3;

        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, HttpInterceptWorker.class.getName(), "Failure to retrieve forwarding endpoint properties - " + e.toString());
        }
    }

    /**
     * sets the logFile FileWrite field from the HttpRequest
     *
     * @param req
     * @param savedMessagesDir
     * @param subDir
     * @return String representation of the log directory
     * @throws Exception
     */
    public static String createLogFile(HttpRequest req, String savedMessagesDir, String subDir) throws Exception {
        String fileName = null;
        if (!Utils.isNullOrEmpty(req.getField(SOAP_ACTION_HEADER))) {
            fileName = req.getField(SOAP_ACTION_HEADER);
        } else {
            // concatenate the method with the ip address of the originating system
            fileName = req.getRequestType() + "_" + req.getRemoteAddr();
        }

        if (fileName == null) {
            throw new Exception("No SOAPaction HTTP header found in request");
        }
        return createLogFile(fileName, savedMessagesDir, subDir);
    }

    /**
     * sets the logFile FileWrite field for the required log file
     *
     * @param fileName
     * @param savedMessagesDir
     * @param subDir
     * @return String representation of the log directory
     * @throws Exception
     */
    public static String createLogFile(String fileName, String savedMessagesDir, String subDir) throws Exception {

        // trim then remove any surrounding double quotes from  soap action
        fileName = fileName.trim().replaceFirst("^\"(.*)\"$", "$1");
        if (fileName.isEmpty()) {
            throw new Exception("No File Name for Log file");
        }

        // Log the incoming message if we are responding
        synchronized (lock) {
            if (!Utils.isNullOrEmpty(subDir)) {

                // we can use Reconfigurable to set the dest folder for this service
                File destFolder = new File(savedMessagesDir + "/" + subDir);
                if (!destFolder.exists()) {
                    destFolder.mkdirs();
                }
                savedMessagesDir = destFolder.getCanonicalPath();
            }
        }
        StringBuilder sb = new StringBuilder(savedMessagesDir);
        sb.append("/");

        // replace the fwdslash in soapaction for '_'
        fileName = fileName.replace('/', '_');
        sb.append(fileName);
        int colon = -1;
        // replace all but the first colon (eg C:\) with _ sb starts with contents of smd
        while ((colon = sb.indexOf(":", savedMessagesDir.indexOf(":") + 1)) != -1) {
            sb.setCharAt(colon, '_');
        }
        sb.append("_");
        sb.append(FILEDATE.format(new Date()));
        sb.append(".log");

        String rmlog = sb.toString();

        return rmlog;
    }

    /**
     * Generates the directory name for each incoming request based upon a
     * header, ITK or Spine discriminator
     *
     * @param req
     * @param service
     * @return String representation of the Subfolder name
     * @throws java.lang.Exception
     */
    public static String generateSubFolderName(HttpRequest req, String service) throws Exception {
        String subDir = null;
        String headerDiscriminator = config.getConfiguration(HTTP_HEADER_DISCRIMINATOR);
        if (headerDiscriminator != null && !headerDiscriminator.trim().equals("")) {
            // syntax (case sensitive) is now 
            // parameter    encoding  pathtype  path         reg exp extract
            // default      none      none      none         ^(.*)$
            // <headername> ['b64'] ['jsonpath' <jsonpath>] [<regexp>]
            StringTokenizer st = new StringTokenizer(headerDiscriminator);
            if (st.countTokens() < 1 || st.countTokens() > 5) {
                throw new IllegalArgumentException("Error in discriminator line Illegal parameter count: " + headerDiscriminator);
            }
            String headerKey = st.nextToken();
            String headerEncoding = null;
            String headerPathType = null;
            String headerPath = null;
            String headerRegExp = "^(.*)$";
            
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                switch (token) {
                    case "b64":
                        headerEncoding = token;
                        break;
                    case "jsonpath":
                        headerPathType = token;
                        if (st.hasMoreTokens()) {
                            headerPath = st.nextToken();
                        } else {
                            throw new IllegalArgumentException("httpheaderdiscriminator missing "+ token);
                        }
                        break;
                    default:
                        headerRegExp = token;
                }
            }

            String headerContent = req.getField(headerKey);
            if (headerContent != null && headerContent.trim().length() > 0) {
                
                if (headerEncoding != null) {
                    switch (headerEncoding) {
                        case "b64":
                            headerContent = new String( Base64.getDecoder().decode(headerContent));
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported encoding type "+headerEncoding);
                    }
                }

                if (headerPathType != null) {
                    switch (headerPathType) {
                        case "jsonpath":
                            headerContent = JsonPath.read(headerContent,headerPath);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported path type "+headerPathType);
                    }
                }
                
                // Create a Pattern object
                Pattern r = Pattern.compile(headerRegExp);
                // Now create matcher object.
                Matcher m = r.matcher(headerContent);
                if (m.find()) {
                    // #1 handle case where no match group is defined ie no brackets in expression
                    if (m.groupCount() > 0) {
                        subDir = m.group(1);
                    } else {
                        subDir = m.group(0);
                    }
                    return subDir;
                }
            }  // if headerContent
        } // if headerDiscriminator
        
        String discriminatorXpath = null;
        boolean spineMessage = false;
        String soapAction = req.getField(SOAP_ACTION_HEADER);
        if (soapAction == null) {
            // this may have already been determined by the simulator call
            if (!Utils.isNullOrEmpty(service)) {
                soapAction = service;
            } else {
                // TODO generic_http is a misnomer its actually expecting a DE to be present
                // generic_ITK might be a better name
                soapAction = "generic_http";
            }
            discriminatorXpath = config.getConfiguration(ITK_FORWARDER_DISCRIMINATOR);

        } else {
            soapAction = soapAction.replaceAll("\"", "");
            // ITK or FHIR Messaging
            if (soapAction.startsWith("urn:nhs-itk:services:201005:") || soapAction.startsWith("urn:nhs:names:services:clinicals-sync")) {
                discriminatorXpath = config.getConfiguration(ITK_FORWARDER_DISCRIMINATOR);
            } else if (soapAction.startsWith("urn:nhs:names:services:")) {
                // get an xpath to an element naming the subfolder if required
                discriminatorXpath = config.getConfiguration(SPINE_FORWARDER_DISCRIMINATOR);
                spineMessage = true;
            }
        }
        if (discriminatorXpath == null || discriminatorXpath.trim().isEmpty()) {
            subDir = null;
            return subDir;
        }
        String contentType = req.getField(CONTENT_TYPE_HEADER);

        String requestContent = null;
        byte[] buffer = req.getBody();
        if (buffer != null && buffer.length > 0) {
            requestContent = new String(buffer);
        } else {
            subDir = getDirNameSafeRemoteAddress(req);
            return subDir;
        }
        String extractedRequestContent = null;
        String extractedContentType = null;
        if (spineMessage) {
            // content type starts with "multipart/related"
            SpineMessage sm = new SpineMessage(requestContent);
            extractedRequestContent = sm.getHL7Part();
            extractedContentType = XML_MIMETYPE;
        } else if (contentType != null) {
            if (contentType.toLowerCase().contains("xml")) {
                extractedRequestContent = requestContent;
                extractedContentType = contentType;
            } else if (HttpInterceptWorker.isJsonFhir(contentType)) {
                extractedRequestContent = fhirConvertJson2Xml(requestContent);
                if (fhirVersion == DSTU2) {
                    extractedContentType = FHIR_XML_MIMETYPE_DSTU2;
                } else if (fhirVersion.isNewerThan(DSTU2)) {
                    extractedContentType = FHIR_XML_MIMETYPE_STU3;
                }
            } else if (contentType.equals(HttpInterceptWorker.JSON_MIMETYPE)) {
                // There's an assumption here that if we get json then it must be an ers request
                extractedRequestContent = JsonXmlConverter.jsonToXmlString(new String(buffer).toCharArray());
                // its now in XML form validate as XML
                extractedContentType = XML_MIMETYPE;
            } else {
                // commented out because we need to process binary files and other types
                //throw new Exception("Cannot extract request: unrecognised content type " + contentType);
            }
        } else {
            throw new Exception("Cannot extract request: http method = " + req.getRequestType() + " null content type");
        }

        // to denote a sub folder for separating different reports
        // if possible get an identifier out of the request buffer
        if (extractedRequestContent != null && extractedContentType != null && !discriminatorXpath.trim().isEmpty() && extractedContentType.toLowerCase().contains("xml")) {
            subDir = xpathExtractor(discriminatorXpath, extractedRequestContent);
            if (subDir == null || subDir.trim().isEmpty()) {
                // default to source address
                subDir = getDirNameSafeRemoteAddress(req);
            }
        } else {
            // default to source address
            subDir = getDirNameSafeRemoteAddress(req);
        }
        return subDir;
    }

    private static String getDirNameSafeRemoteAddress(HttpRequest req) {
        String safeName = req.getRemoteAddr()
                .replace("/", "_")
                .replace(":", "_");
        return safeName;
    }
}
