/*
 Copyright 2014 Richard Robinon rrobinson@hscic.gov.uk

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor.HttpInterceptHandler;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Compressor.CompressionType;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.Compressor.compress;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertXml2Json;
import uk.nhs.digital.mait.tkwx.tk.internalservices.RuleService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ConditionalCompilationControls;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.FHIRCONVERSIONFAILURE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertJson2Xml;
import static uk.nhs.digital.mait.tkwx.util.HttpChunker.chunk;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.FUNCTION_PREFIX;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import uk.nhs.digital.mait.tkwx.httpinterceptor.spinemth.SpineAsynchronousSoapRequestHandler;
import uk.nhs.digital.mait.tkwx.httpinterceptor.spinemth.SpineSynchronousSoapRequestHandler;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Schedule.derivePseudoInteractionID;

/**
 * This class represents a http request. This may be incorporated into the
 * existing spine handler/worker in the future but as the manner in which any
 * rules will be applied to an incoming request will be significantly different
 * due to the fast response times required and also the interceptor requires
 * that a request be forwarded to spine, it is more aligned in structure with
 * the eRS interceptor
 *
 * @author Damian Murphy damian.murphy@hscic.gov.uk
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class HttpInterceptWorker {

    private static Configurator config;

    private ServiceResponse simulatorServiceResponse = null;
    private RuleService rulesService = null;
    private HttpInterceptHandler handler = null;
    private String service;
    private boolean restful = false;
    private boolean responseContentTypeSet; // used to log reports if the response content type has been set
    protected LoggingFileOutputStream logfile = null;
    private HttpRequest clonedXmlHttpRequest = null; //used for simulator and validator when request is json
    private static String fhirServiceLocation;

    private static String forwardingAddress = null;
    private static boolean inhibitValidation = false;

    // NB These are static http header values to be inserted in a simulator response
    private static final String HTTP_HEADER_RESPONSE_PROPERTY_PREFIX = "tks.httpinterceptor.httpheader.";

    private static final int DEFAULTFORWARDINGPORT = 5005;
    private static int forwardingPort = DEFAULTFORWARDINGPORT;

    public static final String COMPRESSION_GZIP = "gzip";
    public static final String COMPRESSION_DEFLATE = "deflate";

    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";

    // Mime types
    public static final String JSON_MIMETYPE = "application/json";


    private static FhirVersionEnum fhirVersion;
    protected EvidenceMetaDataHandler evidenceMetaDataHandler;
    private SpineAsynchronousSoapRequestHandler asynchronousSoapRequestHandler = null;
    private static List<String> syncSoapList = null;
    private static List<String> asyncSoapList = null;

    static {
        try {
            config = Configurator.getConfigurator();
            String sp = null;
            String sspForwardingAddress = System.getProperty("uk.nhs.digital.mait.tkwx.httpinterceptor.forwardingaddress");
            String sspForwardingPort = System.getProperty("uk.nhs.digital.mait.tkwx.httpinterceptor.forwardingport");
            if (!isNullOrEmpty(sspForwardingAddress) && !isNullOrEmpty(sspForwardingPort)) {
                forwardingAddress = sspForwardingAddress;
                sp = sspForwardingPort;
            } else {
                forwardingAddress = config.getConfiguration(FORWARDINGADDRESSPROPERTY);
                sp = config.getConfiguration(FORWARDINGPORTPROPERTY);
            }
            if (!isNullOrEmpty(sp)) {
                try {
                    forwardingPort = Integer.parseInt(sp);
                    System.out.println("Interceptor Forwarding Address: " + forwardingAddress + ":" + forwardingPort);
                } catch (NumberFormatException e) {
                    Logger.getInstance().log(SEVERE, HttpInterceptWorker.class.getName(), "Forwarding Port is not an integer - " + sp);
                }
            }
            inhibitValidation = isY(config.getConfiguration(INHIBITVALIDATION_PROPERTY));
            fhirVersion = config.getConfiguration(FHIR_VERSION_PROPERTY) != null
                    ? FhirVersionEnum.valueOf(config.getConfiguration(FHIR_VERSION_PROPERTY).toUpperCase()) : DSTU3;

            String sssr = config.getConfiguration(SPINESOAPSYNCREQUEST);
            if (!isNullOrEmpty(sssr)) {
                syncSoapList = Arrays.asList(sssr.split("\\s+"));
            }
            String ssar = config.getConfiguration(SPINESOAPASYNCREQUEST);
            if (!isNullOrEmpty(ssar)) {
                asyncSoapList = Arrays.asList(ssar.split("\\s+"));
            }
            
            // default value from constant
            fhirServiceLocation = FHIR_SERVICE_LOCATION;
            // get any override from the configurator;
            String fsl = config.getConfiguration(FHIR_SERVICE_LOCATION_PROPERTY);
            if (!isNullOrEmpty(fsl)) {
                fhirServiceLocation = fsl;
            }
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, HttpInterceptWorker.class.getName(), "Failure to retrieve forwarding endpoint properties - " + e.toString());
        }
    }

    /**
     * public constructor >
     *
     * @param request HttpRequest
     * @param h Interceptor hander instance
     */
    public HttpInterceptWorker(HttpRequest request, HttpInterceptHandler h) {
        handler = h;
        rulesService = (RuleService) ServiceManager.getInstance().getService("RulesEngine");
        restful = rulesService.isRestful();
        simulatorServiceResponse = null;

        invokeSimulator(request);
    }

    /**
     * parse the context path of the request to get request parameters &
     * separated url decoded value attribute pairs following the question mark
     * in the context path
     *
     * @param request HttpRequest
     * @return hashmap of array list of String of get request parameters
     * @throws UnsupportedEncodingException
     */
    private HashMap<String, ArrayList<String>> getRequestParameters(HttpRequest request) throws UnsupportedEncodingException {
        String context = request.getContext();
        return getRequestParametersFromCP(context);
    }

    /**
     * parse the context path string to get request parameters & separated url
     * decoded value attribute pairs following the question mark in the context
     * path
     *
     * @param contextPath
     * @return hashmap of array list of String of get request parameters
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, ArrayList<String>> getRequestParametersFromCP(String contextPath) throws UnsupportedEncodingException {
        String paramString = contextPath.replaceFirst("^.*?\\?", "");
        String[] params = paramString.split("\\&");
        HashMap<String, ArrayList<String>> hmParams = new HashMap<>();
        for (String param : params) {
            String paramName = param.replaceFirst("=.*$", "");
            if (hmParams.get(paramName) == null) {
                hmParams.put(paramName, new ArrayList<>());
            }
            hmParams.get(paramName).add(URLDecoder.decode(param.replaceFirst("^.*?=", ""), "UTF-8"));
        }
        return hmParams;
    }

    /**
     * may also set the service attribute
     *
     * @param httpRequest
     */
    private void invokeSimulator(HttpRequest httpRequest) {

        // only apply rules for non soap messaging eg FHIR
        String soapAction = httpRequest.getField(SOAP_ACTION_HEADER);
        if (Utils.isNullOrEmpty(soapAction) || soapAction.matches("^\"?urn:nhs:names:services:clinicals-sync.*\"?$")) {
            try {

                // perform conversions if required into standard xml format for comsumption by simulator
                String contentTypeHeader = httpRequest.getField(CONTENT_TYPE_HEADER);
                if (restful) {
                    if (isZip(contentTypeHeader)) {
                        // if it's a $evaluate request assume that it's a CDSS/EMS validation
                        if (httpRequest.getContext().toLowerCase().contains("$evaluate")) {
                            clonedXmlHttpRequest = CDSSFHIRUnpacker.unpack(httpRequest);
                            service = null;
                        }
                        // NB This is a content-type not an encoding type
                        //has been added to capture CDSS Clinical Decision Support validation request which arrive as B64 zip files
                        simulatorServiceResponse = rulesService.execute(httpRequest);
                    } else if (isJsonFhir(contentTypeHeader)) {
                        // this is json fhir
                        clonedXmlHttpRequest = cloneXmlRequest(httpRequest, true);
                        
                        deriveInteractionIDFromHttpRequest(clonedXmlHttpRequest);
                        
                        simulatorServiceResponse = rulesService.execute(clonedXmlHttpRequest);
                    } else if (!isNullOrEmpty(contentTypeHeader) && contentTypeHeader.toLowerCase().contains("json")) {
                        // this is json but not fhir
                        //clonedXmlHttpRequest = cloneXmlRequest(httpRequest, false);
                        // if we get here it's json but not as fhir knows it
                        //simulatorServiceResponse = rulesService.execute(cloneXmlRequest(httpRequest, false));
                        simulatorServiceResponse = rulesService.execute(httpRequest);
                    } else {
                        // defaulting to xml
                        deriveInteractionIDFromHttpRequest(httpRequest);
                        validateXmlAsFhir(contentTypeHeader, httpRequest); // do an initial conversion to see if its valid fhir

                        // assumes now valid xml
                        simulatorServiceResponse = rulesService.execute(httpRequest);
                    }
                } else { // not restful could be FHIR messaging
                    // NB If the FHIR message includes a SOAPaction (what??) we won't get here
                    // try to find the interaction id from the message header
                    String requestBodyStr = new String(httpRequest.getBody());
                    if (requestBodyStr.trim().startsWith("<")) {
                        service = XPathManager.xpathExtractor(fhirServiceLocation, requestBodyStr);
                    }

                    byte[] requestBody = httpRequest.getBody();
                    if (isJsonFhir(contentTypeHeader)) {
                        requestBody = fhirConvertJson2Xml(new String(requestBody)).getBytes();
                        requestBodyStr = new String(requestBody);
                        if (!requestBodyStr.contains(FHIRCONVERSIONFAILURE)) {
                            if (isNullOrEmpty(service) && !isNullOrEmpty(fhirServiceLocation) && !isNullOrEmpty(requestBodyStr)) {
                                service = XPathManager.xpathExtractor(fhirServiceLocation, requestBodyStr);
                            }
                            // can we get the interaction from the ssp header ?
                            if (isNullOrEmpty(service)) {
                                service = httpRequest.getField(SSP_INTERACTION_ID_HEADER);
                            }
                                    // can we derive the interaction from the http method and context path ?
                            if (isNullOrEmpty(service)) {
                                String method = httpRequest.getRequestType();
                                String cp = httpRequest.getContext();
                                service = derivePseudoInteractionID(method, cp);
                            }

                        } else {
                            Logger.getInstance().log(WARNING, HttpInterceptWorker.class.getName(),
                                    "Failed to convert fhir json -> xml " + requestBodyStr);
                        }
                    } else {
                        validateXmlAsFhir(contentTypeHeader, httpRequest);
                    }

                    // NB changed from inverted logic
                    if (!isNullOrEmpty(service)) {
                        simulatorServiceResponse = rulesService.execute(new String[]{service, requestBodyStr});
                    }
                }
            } catch (Exception e) {
                Logger.getInstance().log(WARNING, HttpInterceptWorker.class.getName(),
                        "Failed to get simulator response for null SOAPaction " + e.getMessage());
            }
        } else {
            try {
                if (syncSoapList.contains(httpRequest.getContext())) {
                    // hard coded to be synchronous - need to develop a patchbay approach
                    SpineSynchronousSoapRequestHandler sssrh = new SpineSynchronousSoapRequestHandler();
                    simulatorServiceResponse = sssrh.invoke(httpRequest);
                } else if (asyncSoapList.contains(httpRequest.getContext())) {
                    // hard coded to be asynchronous - need to develop a patchbay approach
                    asynchronousSoapRequestHandler = new SpineAsynchronousSoapRequestHandler();
                    simulatorServiceResponse = asynchronousSoapRequestHandler.invoke(httpRequest);
                } else {
                    throw new Exception("The context path (" + httpRequest.getContext() + ") is not in the configured list");
                }

            } catch (Exception e) {
                Logger.getInstance().log(WARNING, HttpInterceptWorker.class.getName(),
                        "Failed to get simulator response for non-null SOAPaction " + e.getMessage());

            }
        }
    }  // invokeSimulator

    /**
     * 
     * @param httpRequest
     * @throws Exception 
     */
    private void deriveInteractionIDFromHttpRequest(HttpRequest httpRequest) throws Exception {
        if (isNullOrEmpty(service) && !isNullOrEmpty(fhirServiceLocation) && httpRequest.getContentLength() > 0) {
            service = XPathManager.xpathExtractor(fhirServiceLocation, new String(httpRequest.getBody()));
        }
        // can we get the interaction from the ssp header ?
        if (isNullOrEmpty(service)) {
            service = httpRequest.getField(SSP_INTERACTION_ID_HEADER);
        }
        // can we derive the interaction from the http method and context path ?
        if (isNullOrEmpty(service)) {
            String method = httpRequest.getRequestType();
            String cp = httpRequest.getContext();
            service = derivePseudoInteractionID(method, cp);
        }
    }

    /**
     * Parse the xml as fhir and flag up an format conversion error if it fails
     *
     * @param contentTypeHeader
     * @param httpRequest
     * @throws Exception
     */
    private void validateXmlAsFhir(String contentTypeHeader, HttpRequest httpRequest) throws Exception {
        if (contentTypeHeader != null && !contentTypeHeader.trim().isEmpty()) {
            if (isXmlFhir(contentTypeHeader)) {
                // do an initial validation to if its valid fhir
                String jsonRequestBody = fhirConvertXml2Json(new String(httpRequest.getBody()));
                if (jsonRequestBody.contains(FHIRCONVERSIONFAILURE)) {
                    String errorText = JsonXmlConverter.jsonToXmlString(jsonRequestBody.toCharArray()).
                            replaceFirst("(?s)^.*<text>(.*)</text>.*$", "<fhirconversionfailure>$1</fhirconversionfailure>");
                    httpRequest.setInputStream(new ByteArrayInputStream(errorText.getBytes()));
                    //httpRequest.setContentLength(errorText.length());
                    httpRequest.setHeader(CONTENT_LENGTH_HEADER, "" + errorText.length());
                    Logger.getInstance().log(WARNING, HttpInterceptWorker.class
                            .getName(),
                            "Failed to convert fhir xml -> json " + jsonRequestBody);
                }
            }
        }
    }

    /**
     * Synthesise an xml version of the request If its json fhir then convert
     * using fhir converter else just do a vanilla conversion
     *
     * @param jsonHttpRequest json request
     * @return equivalent xml request
     * @throws Exception
     */
    private HttpRequest cloneXmlRequest(HttpRequest jsonHttpRequest, boolean isFhir) throws Exception {
        // clone a request with xml content for the simulator to work on
        HttpRequest xmlRequest = new HttpRequest("From " + jsonHttpRequest.getRemoteAddr());
        byte[] xmlRequestBody = null;
        if (isFhir) {
            xmlRequestBody = fhirConvertJson2Xml(new String(jsonHttpRequest.getBody())).getBytes();
        } else {
            if (jsonHttpRequest.getBody().length > 0) {
                xmlRequestBody = JsonXmlConverter.jsonToXmlString(new String(jsonHttpRequest.getBody()).toCharArray()).getBytes();
            } else {
                xmlRequestBody = "".getBytes();
            }
        }

        xmlRequest.setInputStream(
                new ByteArrayInputStream(xmlRequestBody));
        xmlRequest.setRequestType(jsonHttpRequest.getRequestType());
        xmlRequest.setRequestContext(jsonHttpRequest.getContext());
        // clone http headers including the ssp-interaction id if there is one
        for (String headerName
                : jsonHttpRequest.getFieldNames()) {
            if (!headerName.toLowerCase().equals(CONTENT_LENGTH_HEADER.toLowerCase())) {
                xmlRequest.setHeader(headerName, jsonHttpRequest.getField(headerName));
            }
        }
        // set the length of the transformed xml

        xmlRequest.setContentLength(xmlRequestBody.length);
        return xmlRequest;
    }

    /**
     * accessor to get the stringified ip address of the forwarding endpoint
     *
     * @return address of forwarding endpoint
     */
    public String getForwardingAddress() {
        return forwardingAddress;
    }

    /**
     * accessor to get the port number of the forwarding endpoint
     *
     * @return port of forwarding endpoint
     */
    public int getForwardingPort() {
        return forwardingPort;
    }

    /**
     * To process a request deciding whether to respond with a simulated
     * response based upon the context path of the request or to pass through to
     * the Forwarder. All messages are validated regardless of whether they have content.
     *
     * @param req HttpRequest object of the incoming HTTP request
     * @param resp HttpResponse object of the outgoing HTTP response
     * @throws Exception
     */
    public void process(HttpRequest req, HttpResponse resp)
            throws Exception {
        // Any request is not necessarily going to have any message body 
        //(e.g. get) so this must handle situations where this is the case

        byte[] buffer = null;

        String savedMessagesDir = handler.getSavedMessagesDirectory();
        String subDir = HttpLogFileGenerator.generateSubFolderName(req, service);
        String rmlog = HttpLogFileGenerator.createLogFile(req, savedMessagesDir, subDir);
// initialise the evidence Metatdata handler
        evidenceMetaDataHandler = new EvidenceMetaDataHandler(subDir, "Discriminator");

        logfile = new LoggingFileOutputStream(rmlog);
        logfile.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
        logfile.setMetaDataDescription("interaction-log", "Interaction Log");
        req.setLoggingFileOutputStream(logfile);
        //As there may have been a clone add the logging to that too 
        if (clonedXmlHttpRequest != null) {
            clonedXmlHttpRequest.setLoggingFileOutputStream(logfile);
        }

        int contentLength = req.getContentLength();
        if (contentLength > 0) {
            buffer = req.getBody();
        }
        if (simulatorServiceResponse != null) {
            // Read the response body and get the engine to instantiate it.
            // needed this for restful rules engine use
            if (restful) {
                simulatorServiceResponse = rulesService.instantiateResponse(simulatorServiceResponse, req);
            }

            // Set the response parameters
            resp.setStatus(simulatorServiceResponse.getCode(), simulatorServiceResponse.getCodePhrase());

            String responseStr = simulatorServiceResponse.getResponse();

            // TODO Need to check response code eg -1 => no rules found
            // response str will not contain a valid response when -1
            if (simulatorServiceResponse.getCode() == -1) {
                Logger.getInstance().log(WARNING, HttpInterceptWorker.class
                        .getName(),
                        "response code = -1");
            }
            // If there's no content do not set contentType
            if (responseStr != null && responseStr.trim().length() > 0) {
                determineReturnContentType(req, responseStr, resp);
            }
            // get any static http response header values from config
            try {
                Map hmResponseHeaders = config.getConfigurationMap("^" + HTTP_HEADER_RESPONSE_PROPERTY_PREFIX + ".*$");
                Iterator iter = hmResponseHeaders.keySet().iterator();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String headerName = key.replaceFirst("^" + HTTP_HEADER_RESPONSE_PROPERTY_PREFIX, "");
                    String value = (String) hmResponseHeaders.get(key);

                    if (value.startsWith(FUNCTION_PREFIX)) {
                        value = Utils.invokeJavaMethod(value);
                    }

                    // content type has its own accessor and mutator
                    if (headerName.toLowerCase().equals(CONTENT_TYPE_HEADER.toLowerCase())) {
                        resp.setContentType(value);
                    } else {
                        resp.setField(headerName, value);
                    }
                }

                // process any Simulator Rules provided headers in the service response
                HttpHeaderManager simulatorHttpHeaders = simulatorServiceResponse.getHttpHeaders();
                if (simulatorHttpHeaders != null) {
                    for (String httpHeaderName : simulatorHttpHeaders.getFieldNames()) {
                        resp.setField(httpHeaderName, simulatorHttpHeaders.getHttpHeaderValue(httpHeaderName));
                    }
                }

            } catch (ClassNotFoundException | IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.getInstance().log(SEVERE, HttpInterceptWorker.class
                        .getName(),
                        "Failed to get http response headers " + ex.getMessage());
            }

            byte[] responseBytes = responseStr.getBytes();

            // no need to set content type/encoding or fhir conversions if no content
            if (responseBytes != null && responseBytes.length > 0) {
                // see also determineReturnContentType
                responseBytes = performResponseContentTypeConversion(req, responseBytes, resp);

                // get the converted output for logging
                responseStr = new String(responseBytes);

                responseBytes = performResponseEncodingConversions(req, responseBytes, resp);
            }
            // set this for now gp connect specific
            int chunkSize = 0;
            try {
                String s = Configurator.getConfigurator().getConfiguration(CHUNKXMIT_PROPERTY);
                if (s != null && !s.trim().isEmpty()) {
                    chunkSize = Integer.parseInt(s);

                }
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE, HttpResponse.class
                        .getName(),
                        "Failed to get chunk size from configurator");
            }

            if (chunkSize > 0) {
                resp.setField(TRANSFER_ENCODING_HEADER, TRANSFER_ENCODING_CHUNKED);
                responseBytes = chunkResponse(responseBytes, chunkSize);
            }

            // remember the length as sent on the wire
            int lengthAsSent = responseBytes.length;
            try ( OutputStream os = resp.getOutputStream()) {
                os.write(responseBytes);
                // NB Don't delete the flush its critical to successful operation of the interceptor
                // because theres an override that causes the final response to be generated
                os.flush();
            }
            req.setHandled(true);

            // write the log file
            try ( ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                HttpHeaderManager hm = new HttpHeaderManager();
                hm.parseHttpHeaders(resp.getHttpHeader());
                if (lengthAsSent != responseStr.length()) {
                    hm.addHttpHeader("X-was-" + CONTENT_LENGTH_HEADER, "" + lengthAsSent);
                }
                hm.modifyHttpHeadersForLogging(responseStr.length());
                baos.write(hm.getFirstLine().getBytes());
                baos.write(hm.getHttpHeaders().getBytes());
                baos.write("\r\n".getBytes());

                if (ConditionalCompilationControls.LOG_RAW_RESPONSE) {
                    // this is as is on the wire and may be chunked
                    // its important that the log contains the response as sent
                    // rather than a decoded version otherwise the headers become inconsistent
                    baos.write(responseBytes);
                } else {
                    // string containing uncompressed message json or xml as returned by the simulator
                    baos.write(responseStr.getBytes());
                }

                req.log(buffer, baos.toByteArray());
            }
            if (asynchronousSoapRequestHandler != null && asynchronousSoapRequestHandler.hasAsyncResponse()) {
                asynchronousSoapRequestHandler.asynchronousResponse(simulatorServiceResponse, evidenceMetaDataHandler);
            }

        } else {
            // No response from the Simulator so use the Forwarder to forward the request to the forwarding endpoint
            HttpForwarder f = new HttpForwarder(this);
            f.forward(buffer, resp, req);
            req.setHandled(true);
        }
        // Validate the Request if its not inhibited
        if (!inhibitValidation) {
            HttpInterceptorValidator hiv = new HttpInterceptorValidator(config, service, clonedXmlHttpRequest);
            hiv.validateRequest(req, subDir);
        }
        // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
        evidenceMetaDataHandler.mainThreadEnded();
    }

    private byte[] chunkResponse(byte[] responseBytes, int chunkSize) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            chunk(responseBytes, chunkSize, baos);
            responseBytes = baos.toByteArray();

        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, HttpResponse.class
                    .getName(), "IO Exception writing chunked response " + ex.getMessage());
        }
        return responseBytes;
    }

    /**
     * perform outbound content-type conversions if required (most of this is
     * fhir specific) it has to be done here since simulators are not json aware
     * and only handle xml
     *
     * @param req http request object
     * @param responseBytes
     * @param resp http response object
     * @return byte array containing response body to be returned to client
     * @throws IOException
     */
    private byte[] performResponseContentTypeConversion(HttpRequest req, byte[] responseBytes, HttpResponse resp) throws IOException {

        modifyResponseContentTypeApplyingFormatParameter(req, resp);

        String responseString = new String(responseBytes);

        // convert back to json if required, dont convert if this is a binary payload
        if (isJsonFhir(resp.getContentType()) && !Utils.isBinaryPayloadString(responseString)) {
            responseBytes = fhirConvertXml2Json(responseString).getBytes();
        }
        return responseBytes;
    }

    /**
     * perform outbound repsonse encoding conversions if required
     * unpack base64 encoded binary if its an internal binary then
     * compress using gzip or deflate as per accept-encoding header
     *
     * @param req http request object
     * @param responseBytes byte array
     * @param resp http response object
     * @return byte array containing response body to be returned to client
     * @throws IOException
     */
    private byte[] performResponseEncodingConversions(HttpRequest req, byte[] responseBytes, HttpResponse resp) throws IOException {

        // handle encoding this is only compression at present
        // it must be done after content-type conversions
        // ifs its a wrapped binary then base64 decode it
        if (Utils.isBinaryPayloadString(new String(responseBytes))) {
            responseBytes = Utils.unwrapBinaryPayload(new String(responseBytes));
        }

        String encoding = req.getField(ACCEPT_ENCODING_HEADER);
        if (encoding != null) {
            String[] encodings = encoding.split(",");
            boolean compressed = false;
            // pick a compression, any compression
            for (String encodingType : encodings) {
                encodingType = encodingType.trim();
                switch (encodingType) {
                    case COMPRESSION_GZIP:
                    case COMPRESSION_DEFLATE:
                        resp.setField(CONTENT_ENCODING_HEADER, encodingType);
                        responseBytes = compress(responseBytes, CompressionType.valueOf("COMPRESSION_" + encodingType.toUpperCase()));
                        compressed = true;
                        break;
                    default:
                    // default is no compression
                }
                if (compressed) {
                    break;
                }
            }
        }
        return responseBytes;
    }

    /**
     * This method implements the rules surrounding situations where both an http accept header and a _format url parameter are present in the request
     * it can adjust an already set content type if the _format parameter is present
     * @param req HttpRequest object
     * @param resp HttpResponse object
     * @throws UnsupportedEncodingException
     */
    private void modifyResponseContentTypeApplyingFormatParameter(HttpRequest req, HttpResponse resp) throws UnsupportedEncodingException {

        // fhir specific stuff this is probably built into a "fhir server"
        HashMap<String, ArrayList<String>> hmParams = getRequestParameters(req);
        ArrayList<String> fhirFormatParameters = hmParams.get("_format");
        String fhirFormatParameter = fhirFormatParameters != null && !fhirFormatParameters.isEmpty() ? fhirFormatParameters.get(0) : null;
        
        
        if (resp.getContentType() != null && fhirFormatParameter == null) {
            // this is onyly true if there isn't an overriding format parameter as provisioned by fhir
            responseContentTypeSet = true;
        } else {
            // handle content-type
            String acceptHeader = req.getField(ACCEPT_HEADER);
            String requestContentTypeHeader = req.getField(CONTENT_TYPE_HEADER);

            // determine outbound content type
            responseContentTypeSet = false;
            if (acceptHeader == null) {
                // content type can be null for a GET
                if (isValidFhirFormatParameter(fhirFormatParameter) && (requestContentTypeHeader == null || isValidFhirContentType(requestContentTypeHeader))) {
                    trackedSetContentType(resp, determineFormatParameterContentType(fhirFormatParameter));
                } else if (fhirFormatParameter == null) {
                    if (requestContentTypeHeader == null || isValidFhirContentType(requestContentTypeHeader)) {
                        trackedSetContentType(resp, requestContentTypeHeader);
                    }
                }
            } else if (isValidFhirContentType(acceptHeader)) { // valid fhir accept
                if (isValidFhirFormatParameter(fhirFormatParameter)) {
                    // this says a valid _format overrides accept
                    trackedSetContentType(resp, determineFormatParameterContentType(fhirFormatParameter));
                } else {
                    // fall back on valid accept if _format is not valid
                    trackedSetContentType(resp, acceptHeader);
                }
            } else {
                // invalid accept but .. #11 handle a _format together with an invalid accept
                if (isValidFhirFormatParameter(fhirFormatParameter)) {
                    // this says a valid _format overrides an invalid accept
                    trackedSetContentType(resp, determineFormatParameterContentType(fhirFormatParameter));
                }
            }

            if (!responseContentTypeSet) {
                Logger.getInstance().log(WARNING, HttpInterceptWorker.class
                        .getName(),
                        "Response content-type not set. accept header: " + acceptHeader
                        + ", fhir _format parameter: " + fhirFormatParameter
                        + ", request content type: " + requestContentTypeHeader);
            }
        }
    }

    /**
     * determines and sets the required response content type
     * tracked in terms of always setting the boolean recording the event
     * @param resp HttpResponse object
     * @param httpValue String containing response content type
     */
    private void trackedSetContentType(HttpResponse resp, String httpValue) {
        resp.setContentType(determineContentType(httpValue));
        responseContentTypeSet = true;
    }

    /**
     *
     * @param httpValue http accept header value
     * @return boolean
     */
    private boolean isValidFhirContentType(String httpValue) {
        return httpValue != null && (isJsonFhir(httpValue) || isXmlFhir(httpValue));
    }

    /**
     * is the fhir url parameter _format in the correct form?
     * see https://www.hl7.org/fhir/http.html
     * @param parameterValue is a http get parameter
     * @return boolean
     */
    private boolean isValidFhirFormatParameter(String parameterValue) {
        if (parameterValue != null) {
            if (parameterValue.matches("^((xml|json)|(text/(xml|json))|(application/(xml|json))).*$")) {
                return true;
            } else if (isHapiVersionDstu2()) {
                return parameterValue.matches("^(application/(xml|json)\\+fhir).*$");
            } else if (isHapIVersionNewerThanDstu2()) {
                // accept dstu2 and stu3
                return (parameterValue.matches("^(application/(xml|json)\\+fhir).*$")
                        || parameterValue.matches("^(application/fhir\\+(xml|json)).*$"));
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * assumes this a valid fhir value ie application/fhir+(xml|json) tested by
     * isValidFhirValue
     *
     * @param httpValue must be a header value not an http get parameter
     * @return content type to set
     */
    private String determineContentType(String httpValue) {
        String contentType = null;
        if (isHapiVersionDstu2()) {
            contentType = isJsonFhir(httpValue) ? FHIR_JSON_MIMETYPE_DSTU2 : FHIR_XML_MIMETYPE_DSTU2;
        } else if (isHapIVersionNewerThanDstu2()) {
            contentType = isJsonFhir(httpValue) ? FHIR_JSON_MIMETYPE_STU3 : FHIR_XML_MIMETYPE_STU3;
        }
        return contentType;
    }

    /**
     * The format parameter can have a wider range of values than accept or
     * Content-type if its come from a _format parameter then its reasonable to
     * assume this is a fhir message
     *
     * @param httpParameterValue must be an http get parameter called _format
     * @return content type to set
     */
    private String determineFormatParameterContentType(String httpParameterValue) {
        String contentType = null;
        if (isHapiVersionDstu2()) {
            contentType = (httpParameterValue.contains("json")) ? FHIR_JSON_MIMETYPE_DSTU2 : FHIR_XML_MIMETYPE_DSTU2;
        } else if (isHapIVersionNewerThanDstu2()) {
            contentType = (httpParameterValue.contains("json")) ? FHIR_JSON_MIMETYPE_STU3 : FHIR_XML_MIMETYPE_STU3;
        }
        return contentType;
    }

    /**
     * This method focusses on setting the correct content-type given the fhir version if present
     * It derives the content type from the response body and the hapi fhir version being used.
     * It will handle non fhir responses.
     * For fhir responses this content type may be modified later if a url _format paremeter is supplied. see modifyResponseContentTypeApplyingFormatParameter
     * @param req HttpRequest Object
     * @param responseStr String containing response body - used to determine if response is json or xml
     * @param resp HttpResponse Object
     * @throws UnsupportedEncodingException
     */
    private void determineReturnContentType(HttpRequest req, String responseStr, HttpResponse resp) throws UnsupportedEncodingException {

        String accept = req.getField(ACCEPT_HEADER);
        // TODO this is a bit of a hack
        if (responseStr.trim().startsWith("<")) {
            if (isXmlFhir(accept)) {
                if (isHapiVersionDstu2()) {
                    resp.setContentType(FHIR_XML_MIMETYPE_DSTU2);
                } else if (isHapIVersionNewerThanDstu2()) {
                    resp.setContentType(FHIR_XML_MIMETYPE_STU3);
                }
            } else if (isJsonFhir(accept)) {
                if (isHapiVersionDstu2()) {
                    resp.setContentType(FHIR_JSON_MIMETYPE_DSTU2);
                } else if (isHapIVersionNewerThanDstu2()) {
                    resp.setContentType(FHIR_JSON_MIMETYPE_STU3);
                }
            } else {
                // default to text/xml
                resp.setContentType(XML_MIMETYPE);
            }
        } else if (responseStr.trim().startsWith("{")) {
            // TODO is this block ever entered? Suspect all *fhir* responses are xml at this point.
            if (isJsonFhir(accept)) {
                if (isHapiVersionDstu2()) {
                    resp.setContentType(FHIR_JSON_MIMETYPE_DSTU2);
                } else if (isHapIVersionNewerThanDstu2()) {
                    resp.setContentType(FHIR_JSON_MIMETYPE_STU3);
                }
            } else if (isXmlFhir(accept)) {
                if (isHapiVersionDstu2()) {
                    resp.setContentType(FHIR_XML_MIMETYPE_DSTU2);
                } else if (isHapIVersionNewerThanDstu2()) {
                    resp.setContentType(FHIR_XML_MIMETYPE_STU3);
                }
            } else {
                resp.setContentType(JSON_MIMETYPE);
            }
        }
    }

    /**
     * centralised determination of whether a http header refers to xml fhir
     * content this is fuzzy to handle both DSTU2 and STU 3 order it may favour
     * xml over json as a return type
     *
     * @param httpHeader string may be accept or content-type
     * @return boolean
     */
    private boolean isXmlFhir(String httpHeader) {
        return httpHeader != null && httpHeader.contains("fhir") && httpHeader.contains("xml");
    }

    /**
     * centralised determination of whether a http header refers to json fhir
     * content this is fuzzy to handle both DSTU2 and STU 3 order it may favour
     * json over xml as a return type
     *
     * @param httpHeader string may be accept or content-type
     * @return boolean
     */
    public static boolean isJsonFhir(String httpHeader) {
        return httpHeader != null && httpHeader.contains("fhir") && httpHeader.contains("json");
    }

    private boolean isZip(String httpHeader) {
        return httpHeader != null && httpHeader.contains("zip");
    }

    private boolean isHapiVersionDstu2() {
        return fhirVersion == DSTU2;
    }

    private boolean isHapIVersionNewerThanDstu2() {
        return fhirVersion.isNewerThan(DSTU2);
    }

}
