/*
 Copyright 2012  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorFactory;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationSet;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import javax.xml.xpath.XPathExpression;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static java.util.logging.Level.WARNING;
import javax.xml.xpath.XPathExpressionException;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.itklogverifier.LogVerifier;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Schedule.deriveInteractionID;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.RequestBodyExtractor;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.RulesetMetadata;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import static uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor.BODY_EXTRACTOR_LABEL;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.HttpRequestBodyExtractorAdapter;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;

/**
 * Service to implement the TKW "validate" mode. Driven by configurations in the
 * TKW properties file, and validator configurations.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class ValidatorService extends AbstractValidatorService{

    private XPathExpression soapActionExtractor = null;
    private XPathExpression distributionEnvelopeServiceExtractor = null;
    private XPathExpression fhirServiceExtractor = null;
    private FileWriter supportingDataFileWriter = null;
    private File supportingDataFile = null;
    private boolean supportingDataWritten = false;
    private boolean singleMessageValidationMode = false;
    private String summaryReportNameElement = null;
    private static final String ITKSERVICEXPATH = "//itk:DistributionEnvelope/itk:header/@service";

    // string constants not Validation types
    private static final String VALIDATE_AS = "VALIDATE-AS:";
    private static final String SIMULATOR_LOG_REQUEST_STR = "SIMULATOR_LOG_REQUEST";
    private static final String TRANSMITTER_LOG_RESPONSE_STR = "TRANSMITTER_LOG_RESPONSE";
    // looks for a log file validate as with an added interaction id hint
    private static final String SERVICE_REG_EXP = "^(" + TRANSMITTER_LOG_RESPONSE_STR + "|" + SIMULATOR_LOG_REQUEST_STR + ")\\s*/\\s*(.*)\\s*$";
    enum ValidationType {
        BODY,
        VALIDATE_AS,
        TRANSMITTER_LOG_RESPONSE,
        SIMULATOR_LOG_REQUEST
    }

    /**
     * Creates a new instance of ValidatorService
     */
    public ValidatorService() {
        super();
    }

    /**
     *
     * @param t ToolkitSimulator
     * @param p boot properties
     * @param s service name
     * @throws Exception
     */
    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        super.boot(t, p, s);

        soapActionExtractor = getXpathExtractor(SOAP_HEADER_WSA_ACTION);
        distributionEnvelopeServiceExtractor = getXpathExtractor(ITKSERVICEXPATH);
        fhirServiceExtractor = getXpathExtractor(FHIR_SERVICE_LOCATION);

        ValidatorFactory.getInstance().clear();
        ValidatorFactory.getInstance().init(bootProperties);

        for (String property : new String[]{"tks.phxmlconverter.clustermap", "tks.debug.redirecttransformerrors", DEBUG_DISPLAY_DIGEST_PROPERTY}) {
            if (bootProperties.containsKey(property)) {
                System.setProperty(property, bootProperties.getProperty(property));
            }
        }
        System.out.println(serviceName + " started, class: " + this.getClass().getCanonicalName());
    }

    /**
     * "Supporting data" is created by validations that work on some derivative
     * of the message under test, where the validator output is written with
     * respect to the derivative and not the original message. An example is CDA
     * "template schemas" where the message for validation is in the "wire"
     * form, and is converted to a "template" form for additional schema
     * validation. The templated form is "supporting data" and is saved to a
     * file using this method.
     *
     * @param s Supporting data for writing.
     */
    @Override
    public void writeSupportingData(String s) {
        try {
            if (supportingDataFileWriter == null) {
                return;
            }
            supportingDataFileWriter.write(s);
            supportingDataFileWriter.flush();
            supportingDataWritten = true;
        } catch (IOException e) {
            Logger.getInstance().log(WARNING, ValidatorService.class.getName(), "Exception writing supporting data: " + e.getMessage());
        }
    }

    private void initSupportingData(String filename, String runDate) {
        try {
            String fn = "supportingData_" + filename + "-" + runDate + ".out";
            supportingDataFile = new File(reportDirectory, fn);
            supportingDataFileWriter = new FileWriter(supportingDataFile);
        } catch (IOException e) {
            Logger.getInstance().log(WARNING, ValidatorService.class.getName(), "Exception creating supporting data: " + e.getMessage());
            supportingDataFileWriter = null;
        }
        supportingDataWritten = false;
    }

    private void closeSupportingData() {
        if (supportingDataFileWriter == null) {
            return;
        }
        try {
            supportingDataFileWriter.flush();
            supportingDataFileWriter.close();
            if (!supportingDataWritten) {
                supportingDataFile.delete();
            }
            supportingDataFile = null;
        } catch (IOException e) {
        }
    }

    /**
     * @param param Object
     * @return ServiceResponse
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        // NB passing String[] to instanceof Object[] returns true so the order is important here
        if (param != null && param instanceof String[]) {
            String[] sa = (String[]) param;
            if (sa.length == 2) {
                // content , service
                return validateMessage(sa[0], sa[1]);
            } else {
                throw new Exception("String[] signature is not valid array length is not 2");
            }
        } else if (param != null && param instanceof Object[]) {
            Object[] oa = (Object[]) param;
            if (oa.length == 2) {
                if (oa[0] instanceof String && oa[1] instanceof HttpRequest) {
                    return validateHttpRequest((String) oa[0], (HttpRequest) oa[1]);
                } else {
                    throw new IllegalArgumentException("Object[] signature is not valid oa[0] is not a String or oa[1] is not HttpRequest");
                }
            } else {
                throw new IllegalArgumentException("Object[] signature is not valid array length is not 2");
            }
        } else {
            if (param == null || param instanceof HashMap) {
                return validateMessageForValidationFolder(param == null ? null : (HashMap<String, String>) param);
            } else {
                throw new IllegalArgumentException("Object signature is not valid. Not null or a HashMap ");
            }
        }
    }

    /**
     * Validate the "messages for validation" directory contents as configured
     * in the TKW properties file.
     *
     * @param links HashMap&lt;String,String&gt; (links)
     * @return Empty ServiceResponse.
     * @throws Exception
     */
    private ServiceResponse validateMessageForValidationFolder(HashMap<String, String> links) throws Exception {
        resetSummaryCounts();
        allPassed = true;
        singleMessageValidationMode = false;

        String files[] = sourceDirectory.list();
        if (files.length == 0) {
            // ITK 2.2 and later it is not necessarily an error to have no responses to validate
            // System.err.println(getClass().getName() + ":Nothing to do: source directory is empty");
            return new ServiceResponse(0, null);
        }
        ArrayList<ValidationReport> reports = new ArrayList<>();
        String runDate = LOGFILEDATE.format(new Date());
        ValidatorFactory vf = ValidatorFactory.getInstance();
        List<RulesetMetadata> metadata = vf.getMetadata();
        int fileCount = 0;
        for (String f : files) {
            if (f.startsWith("MULTIPLE")) {
                continue;
            }
            HashMap<String, Object> extraMessageInfo = null;
            fileCount++;
            String service = null;
            String vm = null;
            boolean stripSOAPHeader = true;
            initSupportingData(f, runDate);
            try {
                File valFile = new File(sourceDirectory, f);
                if (valFile.length() == 0) {
                    continue;
                }
                FileReader fr = new FileReader(valFile);
                BufferedReader br = new BufferedReader(fr);
                StringBuilder sb = new StringBuilder();
                boolean firstLine = true;
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        if (line.startsWith(VALIDATE_AS)) {
                            service = line.substring(VALIDATE_AS.length());
                            if ((service == null) || (service.trim().length() == 0)) {
                                throw new Exception("Malformed VALIDATE-AS directive, should be VALIDATE-AS: servicename");
                            }
                            service = service.trim();
                            String logStr = null;
                            String body = null;
                            String logService = null;

                            // check if the log has an added hint of an interaction id
                            // form is "VALIDATE-AS:" ("TRANSMITTER_LOG_RESPONSE"|"SIMULATOR_LOG_REQUEST") [ / <interactionid> ]
                            if (service.matches(SERVICE_REG_EXP)) {
                                // this is the intaraction id added as a hint
                                logService = service.replaceFirst(SERVICE_REG_EXP, "$2");
                                service = service.replaceFirst(SERVICE_REG_EXP, "$1");
                            }
                            switch (service) {
                                case TRANSMITTER_LOG_RESPONSE_STR:
                                case SIMULATOR_LOG_REQUEST_STR:
                                    logStr = new String(Files.readAllBytes(Paths.get(valFile.getAbsolutePath())));
                                    logStr = logStr.replaceFirst("(?s)^" + VALIDATE_AS + " *" + service + " *\r\n", "");
                                    AbstractBodyExtractor be = null;
                                    if (service.equals(TRANSMITTER_LOG_RESPONSE_STR)) {
                                        be = new SynchronousResponseBodyExtractor();
                                    } else {  // SIMULATOR_LOG_REQUEST
                                        be = new RequestBodyExtractor();
                                    }
                                    extraMessageInfo = new HashMap<>();
                                    extraMessageInfo.put(BODY_EXTRACTOR_LABEL, be);
                                    // true means retrieve as xml even if its json
                                    body = be.getBody(new ByteArrayInputStream(logStr.getBytes()), true);
                                    if (logService != null) {
                                        service = logService;
                                    } else {
                                        // try deriving from the HttpHeaders
                                        HttpHeaderManager requestHeaders = be.getHttpRequestHeaders();
                                        service = requestHeaders.getHttpHeaderValue(SOAP_ACTION_HEADER);
                                        if (service == null) {
                                            service = requestHeaders.getHttpHeaderValue(SSP_INTERACTION_ID_HEADER);
                                        }
                                        // try deriving from interaction map in the properties file using http method and context path
                                        if (service == null) {
                                            String method = be.getHttpRequestMethod();
                                            String cp = be.getHttpRequestContextPath();
                                            service = deriveInteractionID(method, cp);
                                        }
                                    }
                                    if (!Utils.isNullOrEmpty(service)) {
                                        sb.append(body);
                                        stripSOAPHeader = false;
                                    } else {
                                        throw new Exception("Can't determine service name from log file " + valFile.getAbsolutePath());
                                    }
                                    break;
                                default:
                                    stripSOAPHeader = false;
                            }
                            if (body != null) {
                                break;
                            }
                            continue;
                        }
                    }
                    sb.append(line);
                    sb.append("\n");
                }
                vm = sb.toString();
                ValidationSet defaultVs = vf.getValidationSet("DEFAULT");

                if (service == null) {
                    if ((service = getSoapAction(vm)) != null) {
                        // got the service from the soap header so there is one
                        stripSOAPHeader = true;
                    } else if ((service = getService(vm)) != null) {
                        // got the service from the DE so there is no soap header
                        stripSOAPHeader = false;
                    } else {
                        if (defaultVs == null) {
                            throw new Exception("Failed to resolve service name via SOAP Action,DistributionEnvelope service or FHIR Service- try adding VALIDATE-AS: with the service name as first line in the file or add a VALIDATE DEFAULT to the ruleset");
                        }
                    }
                }
                ValidationSet vs = vf.getValidationSet(service);
                if (vs == null) {
                    vs = defaultVs;
                }
                if (vs == null) {
                    ArrayList<ValidationReport> reps = new ArrayList<>();
                    ValidationReport v = new ValidationReport("Validation failed: Unrecognised service: " + service);
                    v.setFilename(f);
                    reps.add(v);
                    reports.addAll(reps);
                } else {
                    ArrayList<ValidationReport> reps = vs.doValidations(vm, extraMessageInfo, stripSOAPHeader, this);
                    for (ValidationReport v : reps) {
                        v.setFilename(f);
                    }
                    reports.addAll(reps);
                }
                closeSupportingData();
            } catch (Exception e) {
                ValidationReport vr = new ValidationReport("ERROR: Exception thrown validating file " + f + " : " + e.getMessage());
                vr.setFilename(f);
                vr.setTest(e.getMessage());
                reports.add(vr);
            }
        }

        String fn = createSummaryReport(reports, runDate, fileCount, links, metadata);
        return new ServiceResponse(isAllPassed() ? ServiceResponse.ALLPASSED : ServiceResponse.NOTALLPASSED, fn);
    }

    /**
     * Write the report to a file and return the filename
     *
     * @param vr ArrayList of validation reports
     * @param dateString
     * @param numfiles
     * @param links
     * @param metadata
     * @return String containing report filename
     * @throws Exception
     */
    private String createSummaryReport(ArrayList<ValidationReport> vr, String dateString, int numfiles, HashMap<String, String> links, List<RulesetMetadata> metadata)
            throws Exception {
        StringBuilder rname = new StringBuilder();
        if (singleMessageValidationMode) {
            rname.append(summaryReportNameElement).append("_");
        }
        rname.append(dateString);

        ValidationResults validationResults = new ValidationResults();

        StringBuilder sb = createPreamble(vr, dateString, numfiles, metadata, validationResults);

        int row = 0;
        String lastfilename = "";
        for (ValidationReport v : vr) {
            if (!v.getFilename().contentEquals(lastfilename)) {
                String  linkFileName = null;
                sb.append("<tr bgcolor=\"#AAAAFF\"><td colspan=\"3\"><b>Validation file: ");
                if (links != null && links.containsKey(v.getFilename())) {
                    appendLink(links, v, sb);
                    if (evidenceMetaData != null) {
                        evidenceMetaData.addMetaData("validation-request-log","Validation Report request log",null,reportDirectory+File.separator+links.get(v.getFilename()));
                    }
                } else {
                    sb.append(v.getFilename());
                }
                sb.append("</b></td></tr>");
            }
            lastfilename = v.getFilename();
            appendPassFail(row, v, sb);
            appendContext(v, sb);
            sb.append("</td></tr>");
            ++row;
        }

        appendPostamble(sb);

        String filename = VALIDATION_REPORT_PREFIX + "_" + service2Filename(rname.toString()) + ".html";
        writeFile(reportDirectory, filename, sb.toString());

        File f = new File(reportDirectory, filename);
        // requires an explicit Y to inhibit
        if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
            LogVerifier l = LogVerifier.getInstance();
            l.makeSignature(f.getCanonicalPath());
        }
        return filename;
    }

    /**
     * append internal html links to source files in the report
     *
     * @param links HashRef between filename and html link
     * @param v the validation report object
     * @param sb StringBuilder
     */
    private void appendLink(HashMap<String, String> links, ValidationReport v, StringBuilder sb) {
        appendALink(links.get(v.getFilename()), links.get(v.getFilename()), sb);
    }

    /**
     * append the context and iteration of the validation
     *
     * @param v validation report
     * @param sb StringBuilder to append to
     */
    private void appendContext(ValidationReport v, StringBuilder sb) {
        if ((v.getContext() != null) && (v.getIteration() != -1)) {
            sb.append(" in ");
            sb.append(v.getContext());
            sb.append(" instance ");
            sb.append(v.getIteration());
        }
    }

    /**
     * try to get the soap action from the Soap header
     *
     * @param vm String containing xml validation message
     * @return String containing soap action
     * @throws Exception
     */
    private String getSoapAction(String vm)
            throws Exception {
        String s = null;
        InputSource is = new InputSource(new StringReader(vm));
        try {
            s = soapActionExtractor.evaluate(is);
        } catch (XPathExpressionException e) {
            Logger.getInstance().log(WARNING, ValidatorService.class.getName(), "soapActionExtractor evaluation failed " + e.getMessage());
            throw e;
        }
        if (Utils.isNullOrEmpty(s)) {
            return null;
        }
        return s;
    }

    // TODO this is only a short term mod we need a
    // better way of handling this 
    // redmine 2113 only called in single validation mode ie ers or httpinterceptor
    /**
     * determines whether to strip the header which we do for Spine and ITK but
     * not otherwise FGM uses FHIR messaging which is xml based but does not
     * carry an ITK DE or SOAP header
     *
     *
     * @param service string holding service name
     * @return boolean whether the xml is wrapped in an ITK or soap header or
     * not
     */
    private boolean serviceHasHeader(String service) {
        boolean result = false;
        // The only instance in which this method should return true 
        // (and by implication attempting to "stripHeaders" in the validations) 
        // is when a Spine message is detected. 
        // It is assumed that all Spine messages will have a service of 
        // "urn:nhs:names:services:" - with exception of messages where fhir is specified
        if (service.startsWith("urn:nhs:names:services:")) {
            if (!service.contains("fhir")) {
            result = true;
            }
        }
        return result;
    }

    /**
     * gets the service name from the de or fhir element
     *
     * @param vm String containing validation message
     * @return String containing extracted service name or null
     * @throws Exception
     */
    private String getService(String vm)
            throws Exception {
        String s = null;
        InputSource is = new InputSource(new StringReader(vm));
        try {
            s = distributionEnvelopeServiceExtractor.evaluate(is);
        } catch (XPathExpressionException e) {
            Logger.getInstance().log(WARNING, ValidatorService.class.getName(), "distributionEnvelopeServiceExtractor evaluation failed " + e.getMessage());
            throw e;
        }

        if (Utils.isNullOrEmpty(s)) {
            s = fhirServiceExtractor.evaluate(new InputSource(new StringReader(vm)));
            if (Utils.isNullOrEmpty(s)) {
                return null;
            }
        }
        return s;
    }

    /**
     * Validator Service called only from HttpInterceptor used to validate one
     * XML message at a time. Requires the service name and HttpRequest
     * validated against
     *
     * @param service
     * @param httprequest the HttpRequest object
     * @return ServiceResponse
     * @throws Exception
     */
    private ServiceResponse validateHttpRequest(String service, HttpRequest httpRequest) throws Exception {
        resetSummaryCounts();
        allPassed = true;
        singleMessageValidationMode = true;
        summaryReportNameElement = service2Filename(service);
        ArrayList<ValidationReport> reports = new ArrayList<>();
        String runDate = LOGFILEDATE.format(new Date());
        ValidatorFactory vf = ValidatorFactory.getInstance();
        List<RulesetMetadata> metadata = vf.getMetadata();
        String vm = new String(httpRequest.getBody());
        // get EvidenceMetaData from HttpRequest for logging metadata
        evidenceMetaData = httpRequest.getLoggingFileOutputStream().getEvidenceMetaDataHandler();
        // added scf -wasnt writing supporting data in interceptor mode
        initSupportingData(service, runDate);
        try {
            metadata = setValidationSet(service, vm, vf, metadata);

            ValidationSet vs = vf.getValidationSet(service);
            if (vs == null) {
                ArrayList<ValidationReport> reps = new ArrayList<>();
                ValidationReport v = new ValidationReport("Validation failed: Unrecognised service: " + service);
                v.setFilename(service);
                reps.add(v);
                reports.addAll(reps);
            } else {
                boolean stripHeader = serviceHasHeader(service);
                HashMap<String, Object> messageInfo = new HashMap<>();
                // This adapter wraps an HttpRequest so that it can be used as a BodyExtractor within validations
                messageInfo.put(BODY_EXTRACTOR_LABEL, new HttpRequestBodyExtractorAdapter(httpRequest));
                ArrayList<ValidationReport> reps = vs.doValidations(vm, messageInfo, stripHeader, this);
                for (ValidationReport v : reps) {
                    v.setFilename(service);
                }
                reports.addAll(reps);
            }
            closeSupportingData();
        } catch (Exception e) {
            ValidationReport vr = new ValidationReport("ERROR: Exception thrown validating file " + service + " : " + e.getMessage());
            vr.setFilename(service);
            vr.setTest(e.getMessage());
            reports.add(vr);
        }

        // create a link for the report
        HashMap<String, String> links = new HashMap();
        links.put(service, service2Filename(service) + "_" + runDate + ".log");
        String fn = createSummaryReport(reports, runDate, 1, links, metadata);

        return new ServiceResponse(isAllPassed() ? ServiceResponse.ALLPASSED : ServiceResponse.NOTALLPASSED, fn);
    }

    /**
     * Validator Service called only from eRS used to validate one XML message
     * at a time. Requires the XML message and service to be validated against
     *
     * @param vm single XML form message to be validated
     * @param service service to be validated against
     * @return ServiceResponse containing the validation report
     * @throws Exception
     */
    private ServiceResponse validateMessage(String vm, String service)
            throws Exception {
        resetSummaryCounts();
        allPassed = true;
        singleMessageValidationMode = true;
        summaryReportNameElement = service2Filename(service);
        ArrayList<ValidationReport> reports = new ArrayList<>();
        String runDate = LOGFILEDATE.format(new Date());
        ValidatorFactory vf = ValidatorFactory.getInstance();
        List<RulesetMetadata> metadata = vf.getMetadata();
        initSupportingData(service, runDate);

        try {
            metadata = setValidationSet(service, vm, vf, metadata);

            ValidationSet vs = vf.getValidationSet(service);
            if (vs == null) {
                ArrayList<ValidationReport> reps = new ArrayList<>();
                ValidationReport v = new ValidationReport("Validation failed: Unrecognised service: " + service);
                v.setFilename(service);
                reps.add(v);
                reports.addAll(reps);
            } else {
                boolean stripHeader = serviceHasHeader(service);
                ArrayList<ValidationReport> reps = vs.doValidations(vm, null, stripHeader, this);
                for (ValidationReport v : reps) {
                    v.setFilename(service);
                }
                reports.addAll(reps);
            }
            closeSupportingData();
        } catch (Exception e) {
            ValidationReport vr = new ValidationReport("ERROR: Exception thrown validating file " + service + " : " + e.getMessage());
            vr.setFilename(service);
            vr.setTest(e.getMessage());
            reports.add(vr);
        }

        // create a link for the report
        HashMap<String, String> links = new HashMap();
        links.put(service, service2Filename(service) + "_" + runDate + ".log");
        String fn = createSummaryReport(reports, runDate, 1, links, metadata);

        return new ServiceResponse(isAllPassed() ? ServiceResponse.ALLPASSED : ServiceResponse.NOTALLPASSED, fn);
    }

}
