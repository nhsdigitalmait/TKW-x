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
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorFactory;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationSet;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.RulesetMetadata;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Service to call validations for Spine messages. Used by the TKW
 * "spinevalidator" mode.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SpineValidatorService extends AbstractValidatorService{

    /**
     * Creates a new instance of ValidatorService
     */

    public SpineValidatorService() {
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

        ValidatorFactory.getInstance().init(bootProperties);
        System.out.println(serviceName + " started, class: " + this.getClass().getCanonicalName());
    }

    /**
     * default no supplied outputDirectory defaults to reportDirectory
     * overloaded method call
     *
     * @param vr ArrayList of ValidationReports
     * @param dateString report date
     * @param numfiles
     * @param metadata List of meta data rulesets
     * @throws Exception
     */
    private void writeSummaryReport(ArrayList<ValidationReport> vr, String dateString, int numfiles, List<RulesetMetadata> metadata, String service) throws Exception {
        writeSummaryReport(reportDirectory, vr, dateString, numfiles, metadata, service);
    }

    /**
     * Generates the validation report and writes it to the designated file
     *
     * @param outputDirectory
     * @param vr ArrayList of ValidationReports
     * @param dateString report date
     * @param numfiles
     * @param metadata List of meta data rulesets
     * @param service
     * @throws Exception
     */
    private void writeSummaryReport(File outputDirectory, ArrayList<ValidationReport> vr, String dateString, int numfiles, List<RulesetMetadata> metadata, String service)
            throws Exception {

        ValidationResults validationResults = new ValidationResults();

        StringBuilder sb = createPreamble(vr, dateString, numfiles, metadata, validationResults);

        int row = 0;
        String lastfilename = "";
        for (ValidationReport v : vr) {
            if (!v.getFilename().contentEquals(lastfilename)) {
                sb.append("<tr bgcolor=\"#AAAAFF\"><td colspan=\"3\"><b>Validation file: ");
                if (service == null) {
                    sb.append(v.getFilename());
                } else {
                    // this pattern of invocation is unique to HttpInterceptor
                    // if there's a supplied service theres a convention for the associated log file
                    // so we add the link to the report, at this point the file does not exist
                    // TODO this is inverted we should create the file then run the report on it not pass the
                    // string containing the message then create the file after the validation report
                    appendALink(service2Filename(service) + "_" + dateString + ".log", v.getFilename(), sb);
                    //write metatdata
                   if (evidenceMetaData != null) {
                        evidenceMetaData.addMetaData("validation-request-log","Validation Report request log",null,reportDirectory+File.separator+service2Filename(service) + "_" + dateString + ".log");
                    }                }
                sb.append("</b></td></tr>");
            }
            lastfilename = v.getFilename();
            appendPassFail(row, v, sb);
            sb.append("</td></tr>");
            ++row;
        }

        appendPostamble(sb);


        String filename = getFileName(service, dateString);
        writeFile(outputDirectory, filename, sb.toString());
    }

    /**
     * Validator Service called only from HttpInterceptor used to validate one
     * XML message at a time. Requires the service name and HttpRequest
     * validated against
     *
     * @param service
     * @param o the HttpRequest object
     * @return ServiceResponse
     * @throws Exception
     */
    private ServiceResponse validateHttpRequest(String service, HttpRequest httpRequest) throws Exception {
        String s = new String(httpRequest.getBody());
        // get EvidenceMetaData from HttpRequest for logging
        evidenceMetaData = httpRequest.getLoggingFileOutputStream().getEvidenceMetaDataHandler();
        return validateMessage(s, service);
    }

    /**
     * validate the message in the content String parameter NB The parameter
     * order has been swapped to be consistent with ValidatorService
     *
     * @param content String containing message to be validated
     * @param service String may be null in which case defaults to
     * reportDirectory
     * @return Empty ServiceResponse
     * @throws Exception
     */
    private ServiceResponse validateMessage(String content, String service)
            throws Exception {
        resetSummaryCounts();
        allPassed = true;
        ArrayList<ValidationReport> reports = new ArrayList<>();
        String runDate = LOGFILEDATE.format(new Date());
        ValidatorFactory vf = ValidatorFactory.getInstance();
        List<RulesetMetadata> metadata = null;

        SpineMessage sm = new SpineMessage(content);

        // single output file is named for service
        if (service == null) {
            validate(sm, sm.getService(), vf, reports);
            metadata = vf.getMetadata();
            writeSummaryReport(reports, runDate, 1, metadata, service);
        } else {
            // HttpInterceptor Call we may need to change the validation config path
            metadata = setValidationSet(service, content, vf, metadata);
            validate(sm, sm.getService(), vf, reports);
            writeSummaryReport(reportDirectory, reports, runDate, 1, metadata, service);
        }

        return new ServiceResponse(isAllPassed() ? ServiceResponse.ALLPASSED : ServiceResponse.NOTALLPASSED,
                getFileName(service, runDate));
    }

    /**
     * @param param Should be null.
     * @return Empty ServiceResponse on success.
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        // NB passing String[] to instanceof Object[] returne true so the order is important here
        if (param != null && param instanceof String[]) {
            String[] sa = (String[]) param;
            if (sa.length == 2) {
                // content, service
                return validateMessage(sa[0], sa[1]);
            } else {
                throw new IllegalArgumentException("String[] signature is not valid array length is not 2");
            }
        } else if (param != null && param instanceof Object[]) {
            Object[] oa = (Object[]) param;
            if (oa.length == 2) {
                if (oa[0] instanceof String && oa[1] instanceof HttpRequest) {
                    return validateHttpRequest((String) oa[0], (HttpRequest) oa[1]);
                } else {
                    throw new IllegalArgumentException("Object[] signature is not valid oa[0] is not a String or oa[1] is not an HttpRequest");
                }
            } else {
                throw new IllegalArgumentException("Object[] signature is not valid array length is not 2");
            }
        } else {
            return validateFiles();
        }
    }

    /**
     * Do validations under control of TKW properties file (validator
     * configuration location, and directories for messages-to-validate and
     * results), and the validator configurations themselves.
     *
     * @return
     * @throws Exception
     */
    private ServiceResponse validateFiles() throws Exception {
        resetSummaryCounts();
        allPassed = true;
        int validated = 0;
        String files[] = sourceDirectory.list();
        if (files.length == 0) {
            Logger.getInstance().log(WARNING, SpineValidatorService.class.getName(), "Nothing to do: source directory is empty");
            return new ServiceResponse(0, null);
        }
        ArrayList<ValidationReport> reports = new ArrayList<>();
        String runDate = LOGFILEDATE.format(new Date());
        ValidatorFactory vf = ValidatorFactory.getInstance();
        List<RulesetMetadata> metadata = vf.getMetadata();
        for (String filename : files) {
            SpineMessage sm = new SpineMessage(sourceDirectory.getPath(), filename);
            validate(sm, filename, vf, reports);
        }

        writeSummaryReport(reports, runDate, files.length, metadata, null);

        return new ServiceResponse(validated, null);
    }

    /**
     * actually do the validation called by both execute methods with different
     * parameters
     *
     * @param sm SpineMessage
     * @param filename
     * @param vf validator factory
     * @param reports
     */
    private void validate(SpineMessage sm, String filename, ValidatorFactory vf, ArrayList<ValidationReport> reports) {
        String service;
        try {
            service = sm.getService();
            ValidationSet vs = vf.getValidationSet(service);
            if (vs == null) {
                ArrayList<ValidationReport> reps = new ArrayList<>();
                ValidationReport v = new ValidationReport("Validation failed: No validation rules found for: " + service);
                v.setFilename(filename);
                reps.add(v);
                reports.addAll(reps);
            } else {
                ArrayList<ValidationReport> reps = vs.doSpineValidations(sm, this);
                for (ValidationReport v : reps) {
                    v.setFilename(filename);
                }
                reports.addAll(reps);
            }
        } catch (Exception e) {
            ValidationReport vr = new ValidationReport("ERROR: Exception thrown validating file " + filename + " : " + e.getMessage());
            vr.setFilename(filename);
            // e.printStackTrace();
            vr.setTest(e.toString());
            reports.add(vr);
        }
    }

    private String getFileName(String service, String date) throws UnsupportedEncodingException {
        return VALIDATION_REPORT_PREFIX + "_" + (service != null ? service2Filename(service) + "_" : "") + date + ".html";
    }

}
