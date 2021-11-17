/*
 Copyright 2015  Simon Farrow <simon.farrow1@hscic.gov.uk>

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.RulesetMetadata;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorFactory;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceInterface;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * common base class handles validation maps used for assigning specific
 * validation configs based on the soap action
 *
 * @author SIFA2
 */
public abstract class AbstractValidatorService implements ToolkitService, Reconfigurable {

    protected static final SimpleDateFormat LOGFILEDATE = new SimpleDateFormat(FILEDATEMASK);
    protected static SimpleDateFormat READABLEFORMAT = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
    protected static final String VALIDATION_REPORT_PREFIX = "validation_report";
    private static final String TKSVALIDATORVALIDATIONMAP = "tks.validator.validationmap";

    private static Properties validationMap = null;

    protected Properties bootProperties = null;
    protected String serviceName = null;
    protected ToolkitSimulator toolkitSimulator = null;
    protected File sourceDirectory = null;
    protected File reportDirectory = null;
    private String validatorConfig = null;
    protected boolean allPassed = true;
    private String htmlHeader = null;
    StringBuilder sbSummaryByTestTable;
    protected EvidenceInterface evidenceInterface = null;
    protected EvidenceMetaDataHandler evidenceMetaData = null;

    // sorted hashmap
    private final TreeMap<String, Integer> hmCategorySummary = new TreeMap<>();

    private static final String PASS_COLOUR = "#008000"; // green
    private static final String FAIL_COLOUR = "#900000"; // red

    /**
     * lazy evaluation of static
     *
     * @return Properties object containing mappings from service to validation
     * config name eg pds etc
     */
    private Properties getValidationMap() throws IOException {
        if (validationMap == null) {
            String validationMapPath = bootProperties.getProperty(TKSVALIDATORVALIDATIONMAP);
            if (validationMapPath != null && !validationMapPath.isEmpty()) {
                validationMap = new Properties();
                validationMap.load(new FileReader(validationMapPath));
            }
        }
        return validationMap;
    }

    /**
     * enables restart of validation factory when inline validators detect
     * specific message types
     *
     * @param service the service name extracted from the request
     * @param validationMessage
     * @param validationFactory
     * @param metadata
     * @return metadata which may have been modified if the factory has been
     * restarted
     * @throws IOException
     * @throws Exception
     */
    protected List<RulesetMetadata> setValidationSet(String service, String validationMessage, ValidatorFactory validationFactory, List<RulesetMetadata> metadata) throws IOException, Exception {
        Properties lvalidationMap = getValidationMap();

        // if there's no map just use the default validation config
        if (lvalidationMap != null) {
            // get the validation set from the map set name defaults to ""
            String domain = lvalidationMap.getProperty(service, "");
            // set the config file to use
            Properties clonedBootProperties = new Properties(bootProperties);
            validatorConfig = bootProperties.getProperty(VALIDATOR_CONFIG_PROPERTY + (!domain.isEmpty() ? "." + domain : ""),
                    bootProperties.getProperty(VALIDATOR_CONFIG_PROPERTY));
            clonedBootProperties.setProperty(VALIDATOR_CONFIG_PROPERTY, validatorConfig);

            // restart the validator with new config
            validationFactory.init(clonedBootProperties);
            metadata = validationFactory.getMetadata();
        } else {
            // if there are no validationmap set return to using the existing metadata
            metadata = validationFactory.getMetadata();
        }
        return metadata;
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    /**
     *
     * @param toolkitSimulator
     * @param bootProperties
     * @param serviceName
     * @throws Exception
     */
    @Override
    public void boot(ToolkitSimulator toolkitSimulator, Properties bootProperties, String serviceName)
            throws Exception {
        this.bootProperties = bootProperties;
        this.serviceName = serviceName;
        this.toolkitSimulator = toolkitSimulator;

        String prop = null;
        prop = bootProperties.getProperty(VALIDATOR_SOURCE_PROPERTY);
        // A directory is not required for validation as part of the simulator response
        if (!Utils.isNullOrEmpty(prop)) {
            Utils.createFolderIfMissing(prop);
            sourceDirectory = new File(prop);
            if (!sourceDirectory.canRead()) {
                throw new Exception("Validator source directory "
                        + prop
                        + " not readable");
            }
        }

        prop = bootProperties.getProperty(VALIDATOR_REPORT_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("Validator report directory property "
                    + VALIDATOR_REPORT_PROPERTY
                    + " not set");
        }
        Utils.createFolderIfMissing(prop);
        reportDirectory = new File(prop);
        if (!reportDirectory.canWrite()) {
            throw new Exception("Validator report directory "
                    + prop
                    + " not writable");
        }

        validatorConfig = bootProperties.getProperty(VALIDATOR_CONFIG_PROPERTY);
        if (Utils.isNullOrEmpty(validatorConfig)) {
            throw new Exception("Validator configuration file property "
                    + VALIDATOR_CONFIG_PROPERTY
                    + " not set");
        }
    }

    /**
     * Default null behaviour.
     *
     * @param s
     */
    public void writeSupportingData(String s) {
    }

    @Override
    public void reconfigure(Properties p) throws Exception {
        boot(toolkitSimulator, p, serviceName);
    }

    @Override
    public String reconfigure(String what, String value) throws Exception {

        if (what.equals(VALIDATOR_SOURCE_PROPERTY)) {
            Utils.createFolderIfMissing(value);
            sourceDirectory = new File(value);
            if (!sourceDirectory.canRead()) {
                throw new Exception("Validator source directory "
                        + value +
                        " not readable");
            }

            ValidatorFactory.getInstance().clear();
            ValidatorFactory.getInstance().init(bootProperties);
        }
        if (what.equals(VALIDATOR_REPORT_PROPERTY)) {
            Utils.createFolderIfMissing(value);
            reportDirectory = new File(value);
            if (!reportDirectory.canWrite()) {
                throw new Exception("Validator report directory "
                        + value +
                        " not writable");
            }

            ValidatorFactory.getInstance().clear();
            ValidatorFactory.getInstance().init(bootProperties);
        }

        if (what.equals(VALIDATOR_CONFIG_PROPERTY)) {
            validatorConfig = value;
            if (Utils.isNullOrEmpty(validatorConfig)) {
                throw new Exception("Validator configuration file property "
                        + VALIDATOR_CONFIG_PROPERTY
                        + " not set");
            }

            ValidatorFactory.getInstance().clear();
            ValidatorFactory.getInstance().init(bootProperties);
        }

        return null;
    }

    /**
     * create the preamble to the report (top)
     *
     * @param vr
     * @param dateString
     * @param numfiles
     * @param metadata
     * @param validationResults
     * @return populated StringBuilder Object
     */
    protected StringBuilder createPreamble(ArrayList<ValidationReport> vr, String dateString, int numfiles, List<RulesetMetadata> metadata, ValidationResults validationResults) {
        for (ValidationReport v : vr) {
            if (v.getPassed()) {
                validationResults.pass();
            } else {
                validationResults.fail();
            }
        }
        try {
            Date date = LOGFILEDATE.parse(dateString);
            dateString = READABLEFORMAT.format(date);
        } catch (ParseException ex) {
            //no action
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Validation summary ");
        sb.append(dateString);
        sb.append("</title></head><body>");
        sb.append("<h1>Validation summary</h1>");
        sb.append("<h3>Run at: ");
        sb.append(dateString);
        sb.append(" using configuration \"");
        sb.append(toolkitSimulator.getConfigurationName());
        sb.append("\" validator configuration \"");
        sb.append(validatorConfig);
        sb.append("\"</h3>");
        for (RulesetMetadata r : metadata) {
            appendIfNonNullValue("Validation Ruleset Name", r.getName(), sb);
            appendIfNonNullValue("Validation Ruleset Version", r.getVersion(), sb);
            appendIfNonNullValue("Validation Ruleset Timestamp", r.getTimestamp(), sb);
            appendIfNonNullValue("Validation Ruleset Author", r.getAuthor(), sb);
            sb.append("<br/>");
        }
        sb.append("<h3>Submitter: ");
        sb.append(toolkitSimulator.getOrganisationName());
        sb.append("</h3>");
        sb.append("<p/>");
        sb.append("<h2>Summary</h2><table><tr><td>Files validated: </td><td align=\"right\">");
        sb.append(numfiles);
        sb.append("</td></tr><tr><td>Checks reported: </td><td align=\"right\">");
        sb.append(vr.size());
        sb.append("</td></tr><tr><td>Passes: </td><td align=\"right\">");
        sb.append(validationResults.getPasses());
        sb.append("</td></tr><tr><td>Check issues: </td><td align=\"right\">");
        sb.append(validationResults.getFails());
        sb.append("</td></tr></table><br/><h2>Results by file</h2>");
        sb.append("<table>");

        sb.append("<tr bgcolor=\"#000000\"><td style=\"color:#FFFFFF\"><b>File</b></td><td style=\"color:#FFFFFF\"><b>Result</b></td><td style=\"color:#FFFFFF\"><b>Comment</b></td></tr>");

        if (validationResults.getFails() > 0) {
            allPassed = false;
        }

        return sb;
    }

    /**
     * appends name and value as a body element if value is not null
     *
     * @param name
     * @param value
     * @param sb
     */
    private void appendIfNonNullValue(String name, String value, StringBuilder sb) {
        if (value != null) {
            sb.append("<br/>").append(name).append(": ");
            sb.append(value);
        }
    }

    /**
     * append the postamble to the report (tail)
     *
     * @param sb StringBuilder containing html report content
     */
    protected void appendPostamble(StringBuilder sb) {
        sb.append("</table>");
        sb.append("<hr/>Prepared by: ");
        sb.append(toolkitSimulator.getVersion());
        sb.append("</body></html>");

        createSummaryByTest();

        // insert summary at top of the report
        if (sb.indexOf("<h2>Results by file</h2>") != -1) {
            sb.insert(sb.indexOf("<h2>Results by file</h2>"), sbSummaryByTestTable);
        } else {
            Logger.getInstance().log(WARNING, AbstractValidatorService.class.getName(), "Can't find insert point in validation report");
        }
    }

    /**
     * create summary by test table
     */
    private void createSummaryByTest() {
        sbSummaryByTestTable = new StringBuilder("<h2>Check Issues by Test</h2><table>");
        sbSummaryByTestTable.append("<tr bgcolor=\"#000000\"><td style=\"color:#FFFFFF\"><b>Test</b></td><td style=\"color:#FFFFFF\"><b>Count</b></td></tr>");

        int total = 0;
        int row = 0;
        for (String category : hmCategorySummary.keySet()) {
            if ((row++ % 2) == 0) {
                sbSummaryByTestTable.append("<tr bgcolor=\"#FFFFFF\"><td style=\"color:" + FAIL_COLOUR + "\">");
            } else {
                sbSummaryByTestTable.append("<tr bgcolor=\"#EAEAEA\"><td style=\"color:" + FAIL_COLOUR + "\">");
            }

            sbSummaryByTestTable.append(category).append("</td><td align=\"right\" style=\"color:" + FAIL_COLOUR + "\">").
                    append(hmCategorySummary.get(category)).append("</td></tr>");
            total += hmCategorySummary.get(category);
        }
        sbSummaryByTestTable.append("<tr><td>Total</td><td align=\"right\">").append(total).append("</td></tr>");
        sbSummaryByTestTable.append("</table>");
    }

    /**
     * Append the result of the test
     *
     * @param filename
     * @param fontColour
     * @param result "PASS" or FAIL""
     * @param sb StringBuilder to append to
     */
    private void appendResult(String filename, String fontColour, String result, StringBuilder sb) {
        sb.append("<td style=\"color:").append(fontColour).append("\">");
        sb.append(filename);
        sb.append("</td><td style=\"color:").append(fontColour).append("\">");
        sb.append(result).append("</td><td style=\"color:").append(fontColour).append("\">");
    }

    /**
     * append any annotation and test details
     *
     * @param v validation report
     * @param sb StringBuilder
     */
    private void appendAnnotationAndDetails(ValidationReport v, StringBuilder sb) {
        if (v.hasAnnotation()) {
            sb.append(v.getAnnotation());
            sb.append("<br/>");
        }
        sb.append(v.getDetail());
        if ((v.getTestDetails() != null) && (v.getTestDetails().trim().length() != 0)) {
            sb.append(" -- ");
            sb.append(v.getTestDetails());

            if (v.getDetail().equals("Failed")) {
                String annotation = v.hasAnnotation() ? v.getAnnotation() : "No Annotation";
                if (hmCategorySummary.containsKey(annotation)) {
                    hmCategorySummary.put(annotation, hmCategorySummary.get(annotation) + 1);
                } else {
                    hmCategorySummary.put(annotation, 1);
                }
            }
        }
    }

    /**
     * append results of ValidationReport
     *
     * @param row row number
     * @param v validation report
     * @param sb StringBuilder
     */
    protected void appendPassFail(int row, ValidationReport v, StringBuilder sb) throws Exception {
        // alternating row colour
        if ((row % 2) == 0) {
            sb.append("<tr bgcolor=\"#FFFFFF\">");
        } else {
            sb.append("<tr bgcolor=\"#EAEAEA\">");
        }
        v.writeExternalValidation(reportDirectory.getAbsolutePath());
        if (v.getPassed()) {
            appendResult(v.getFilename(), PASS_COLOUR, "PASS", sb);
        } else {
            appendResult(v.getFilename(), FAIL_COLOUR, "FAIL", sb);
        }
        appendAnnotationAndDetails(v, sb);
    }

    /**
     * write string s to a file in the designated location
     *
     * @param outputDirectory
     * @param filename
     * @param s
     * @throws IOException
     */
    protected void writeFile(File outputDirectory, String filename, String s) throws IOException {
        // Write the contents of the string to the designated directory and filename
        File f = new File(outputDirectory, filename);
        Utils.writeString2File(f, s);
        // write meta data to evidenceMetaDataHandler
        if (evidenceMetaData != null) {
            evidenceMetaData.addMetaData("validation-report","TKW Validation Report", null, f.getAbsolutePath());
        }
    }

    /**
     * append html a element to StringBuilder
     *
     * @param url the url to go in the href
     * @param text the text for the href
     * @param sb StringBuilder
     */
    protected void appendALink(String url, String text, StringBuilder sb) {
        sb.append("<a href=\"");
        sb.append(url);
        sb.append("\">");
        sb.append(text);
        sb.append("</a>");
    }

    /**
     * munge a url so that it becomes a viable filesystem name
     *
     * @param service
     * @return service name mapped to a safe filename
     */
    protected String service2Filename(String service) throws UnsupportedEncodingException {
        return URLDecoder.decode(service, "UTF-8").replace(':', '_').replace('/', '_');
    }

    /**
     * @return the allPassed
     */
    public boolean isAllPassed() {
        return allPassed;
    }

    /**
     * encapsulates passes and fails
     */
    public class ValidationResults {

        private int passes;
        private int fails;

        /**
         * public constructor
         */
        public ValidationResults() {
            passes = 0;
            fails = 0;
        }

        /**
         * add a pass
         */
        public void pass() {
            passes++;
        }

        /**
         * add a fail
         */
        public void fail() {
            fails++;
        }

        /**
         * @return the passes
         */
        public int getPasses() {
            return passes;
        }

        /**
         * @return the fails
         */
        public int getFails() {
            return fails;
        }
    }

    /**
     * method to ensure one shots don't accumulate summary counts
     */
    public void resetSummaryCounts() {
        hmCategorySummary.clear();
    }

    public void registerForReport(EvidenceInterface ei) {
        evidenceInterface = ei;
    }
    // allow the use of evidenceMetaData to services which do not use httprequest
    public void setEvidenceMetaData(EvidenceMetaDataHandler emd) {
        evidenceMetaData = emd;
    }

}
