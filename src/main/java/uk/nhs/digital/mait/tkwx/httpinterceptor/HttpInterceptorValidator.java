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

import java.io.File;
import java.io.IOException;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceInterface;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertJson2Xml;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Schedule.deriveInteractionID;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.EvidenceInterfaceRegister;

/**
 * Class to execute inline validation of simulator requests in their own thread
 *
 * @author riro
 */
public class HttpInterceptorValidator extends Thread {

    private final HttpRequest clonedXmlHttpRequest;
    private final Configurator config;
    private final String service;

    // NB This means that property file entries will be overwritten with effectively hard coded class names
    private static final String SCHEMAVALIDATORPROPERTY = "tks.validator.check.schema";
    private static final String SPINE_SCHEMA_VALIDATOR_CLASS = "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineSchemaValidator";
    private static final String ITK_SCHEMA_VALIDATOR_CLASS = "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.SchemaValidator";

    private static final Object lock = new Object();
    private EvidenceInterface evidenceInterface = null;
    private HttpRequest httpRequest;
    private String subDir;

    public HttpInterceptorValidator(Configurator config, String service, HttpRequest clonedXmlHttpRequest) {
        this.config = config;
        this.service = service;
        this.clonedXmlHttpRequest = clonedXmlHttpRequest;
    }

    /**
     * Thread safe class to execute validations in a separate thread. Determines
     * type of validation required Performs any conversions required before
     * validation can be done
     *
     * @param httpRequest
     * @param subDir any subdirectory to be used in the logging structure
     */
    public void validateRequest(HttpRequest httpRequest, String subDir) {
        this.httpRequest = httpRequest;
        this.subDir = subDir;
        this.start();
    }

    /**
     * Reinstated the Threading after discovering that the validation was indeed
     * part of the request/response round trip, but was disguised by the fact
     * that the response was send on the open connection which wasn't closed
     * until the validation had ended. This fixes the issue that states the
     * socket output stream dead
     */
    @Override
    public void run() {
        try {
            // default to Validator
            String validatorServiceName = "Validator";
            boolean spineMessage = false;
            String schemaValidatorClass = config.getConfiguration(SCHEMAVALIDATORPROPERTY);
            String soapAction = httpRequest.getField(SOAP_ACTION_HEADER);
            if (soapAction == null) {
                // this may have already been determined by the simulator call
                if (!Utils.isNullOrEmpty(service)) {
                    soapAction = service;
                }
            } else {
                soapAction = soapAction.replaceAll("\"", "");
                // ITK or FHIR Messaging
                if (soapAction.startsWith("urn:nhs-itk:services:201005:") || soapAction.startsWith("urn:nhs:names:services:clinicals-sync")) {
                    if (!schemaValidatorClass.equals(ITK_SCHEMA_VALIDATOR_CLASS)) {
                        config.setConfiguration(SCHEMAVALIDATORPROPERTY, ITK_SCHEMA_VALIDATOR_CLASS);
                    }
                } else if (soapAction.startsWith("urn:nhs:names:services:")) {
                    if (!schemaValidatorClass.equals(SPINE_SCHEMA_VALIDATOR_CLASS)) {
                        config.setConfiguration(SCHEMAVALIDATORPROPERTY, SPINE_SCHEMA_VALIDATOR_CLASS);
                    }
                    spineMessage = true;
                    validatorServiceName = "SpineValidator";
                }
            }

            ToolkitService validatorService = ServiceManager.getInstance().getService(validatorServiceName);
            if (validatorService != null) {
                String contentType = httpRequest.getField(CONTENT_TYPE_HEADER);
                System.out.println("Validating message with context path: " + httpRequest.getContext() + "\r\ncontent type: " + (contentType != null ? contentType : "Not set"));

                String requestContent = null;
                byte[] buffer = httpRequest.getBody();
                if (buffer != null) {
                    requestContent = new String(buffer);
                }
                String extractedXmlRequestContent = null;
                if (spineMessage) {
                    // content type starts with "multipart/related"
                    SpineMessage sm = new SpineMessage(requestContent);
                    extractedXmlRequestContent = sm.getHL7Part();
                } else if (contentType != null) {
                    if (contentType.toLowerCase().contains("xml")) {
                        extractedXmlRequestContent = requestContent;
                    } else if (HttpInterceptWorker.isJsonFhir(contentType)) {
                        extractedXmlRequestContent = fhirConvertJson2Xml(requestContent);
                    } else if (contentType.equals(HttpInterceptWorker.JSON_MIMETYPE)) {
                        extractedXmlRequestContent = JsonXmlConverter.jsonToXmlString(new String(buffer).toCharArray());
                        if (soapAction == null) {
                            // try looking up the combination of method and context path re in the interaction map
                            soapAction = deriveInteractionID(httpRequest.getRequestType(), httpRequest.getContext());
                        }
                    } else if (clonedXmlHttpRequest != null && clonedXmlHttpRequest.getField(CONTENT_TYPE_HEADER).toLowerCase().contains("xml")) {
                        // In this case the payload has had to be converted from something else but it can be validated as it xml and the clonedXmlHttpRequest will be used
                        extractedXmlRequestContent = new String(clonedXmlHttpRequest.getBody());
                    } else {
                        //throw new Exception("Cannot extract request content: unrecognised content type " + contentType);
                    }
                }
//removed to allow all requests to validate e.g. Post plus no content type                
//                else // null content type
//                if (Arrays.asList("GET", "DELETE", "OPTIONS", "HEADER").contains(httpRequest.getRequestType())) {
//                    // its ok for the methods in the list above not to have content 
//                    //however because we may still need to check headers etc we will still validate
//                } else {
//                    throw new Exception("Cannot extract request: http method = " + httpRequest.getRequestType() + " null content type");
//                }

                if (soapAction == null) {
                    // TODO generic_http is a misnomer its actually expecting a DE to be present
                    // generic_ITK might be a better name
                    soapAction = "generic_http";
                }
                System.out.println("Service = " + soapAction + ", http method = " + httpRequest.getRequestType());

                Reconfigurable reconfigurable = (Reconfigurable) validatorService;
                String baseFolder = config.getConfiguration(VALIDATOR_REPORT_PROPERTY);
                String validationReportRoot = null;
                String reportFileName = null;
                synchronized (lock) {
                    if (!Utils.isNullOrEmpty(subDir)) {
                        // we can use Reconfigurable to set the dest folder for this service
                        File destFolder = new File(baseFolder + "/" + subDir);
                        if (!destFolder.exists()) {
                            destFolder.mkdirs();
                        }
                        reconfigurable.reconfigure(VALIDATOR_REPORT_PROPERTY, destFolder.getCanonicalPath());
                        validationReportRoot = destFolder.getCanonicalPath();
                    } else {
                        validationReportRoot = baseFolder;
                    }

                    // register with the Validator Service to be given a String representation of the HTML report
                    EvidenceInterfaceRegister eir = (EvidenceInterfaceRegister) validatorService;
                    eir.registerForReport(evidenceInterface);
                    // if there's a cloned xml version (converted from json) use that otherwise submit the original
                    ServiceResponse sr = validatorService.execute(new Object[]{soapAction, clonedXmlHttpRequest != null ? clonedXmlHttpRequest : httpRequest});

                    reportFileName = sr.getResponse();
                    if (Utils.isNullOrEmpty(reportFileName)) {
                        reportFileName = "fixme.log";
                    }

                    writeRequestLog(clonedXmlHttpRequest != null ? clonedXmlHttpRequest : httpRequest, extractedXmlRequestContent != null ? extractedXmlRequestContent : requestContent, validationReportRoot, reportFileName);
                    // may not be needed but better safe etc
                    reconfigurable.reconfigure(VALIDATOR_REPORT_PROPERTY, baseFolder);
                }

            } else {
                Logger.getInstance().log(WARNING, HttpInterceptWorker.class.getName(), "No Validation Service set");

            }
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, HttpInterceptWorker.class.getName(), "Validate request failed " + (e.getMessage() != null ? e.getMessage() : e));
        }
    }

    /**
     * reconstructed from the http request object NB THis may not be the same as
     * the simulator log esp if the request was json This log contains the xml
     * converted equivalent since validation does not work on json write the
     * request to the required log file name
     *
     * @param httpRequest
     * @param requestContent
     * @param baseFolder
     * @param subFolder
     * @param reportFileName
     * @throws IOException
     */
    private void writeRequestLog(HttpRequest httpRequest, String requestContent, String root, String reportFileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        //sb.append("VALIDATE-AS: SIMULATOR_LOG_REQUEST\r\n");
        sb.append(httpRequest.getRequestType()).append(" ").append(httpRequest.getContext()).append("\r\n");
        sb.append(httpRequest.getHeaderManager().getHttpHeaders()).append("\r\n");
        if (!Utils.isNullOrEmpty(requestContent)) {
            sb.append(requestContent);
        }
        sb.append("\r\n" + END_INBOUND_MARKER + "\r\n");
        Utils.writeString2File(root + "/"
                + reportFileName.replaceFirst("^validation_report_(.*).html", "$1.log"), sb.toString());
    }

    public void registerForReport(EvidenceInterface ei) {
        evidenceInterface = ei;
    }

}
