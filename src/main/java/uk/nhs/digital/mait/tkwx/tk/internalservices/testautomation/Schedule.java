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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ReconfigureTags;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScheduleElement.ScheduleElementType;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ScheduleContext;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.RESTfulRulesEngine;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;

/**
 * A Schedule is a collection of Tests (strictly, it is a set of ScheduleElement
 * instances which can be tests, or sequences of Tests executed in a loop). The
 * schedule provides the container for executing the tests and recording them
 * and their results as log files.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Schedule
        implements Linkable {
    
         private static String fhirServiceLocation;

     static {
        try {
            Configurator config = Configurator.getConfigurator();
            // default value from constant
            fhirServiceLocation = FHIR_SERVICE_LOCATION;
            // get any override from the configurator;
            String fsl = config.getConfiguration(FHIR_SERVICE_LOCATION_PROPERTY);
            if (!isNullOrEmpty(fsl)) {
                fhirServiceLocation = fsl;
            } }
         catch (Exception e) {
            Logger.getInstance().log(SEVERE, RESTfulRulesEngine.class.getName(), "Failure to retrieve config info " + e.toString());
        }   
     }


    private String name = null;
    private File logRoot = null;
    private String simulatorConfig = null;
    private String transmitterSource = null;
    private String simulatorMessages = null;
    private String transmitterSentMessages = null;
    private Script script = null;
    private NamedPropertySet baseProperties = null;
    private File validatorSource = null;
    private File validatorReports = null;

    private final ArrayList<ScheduleElement> scheduleElements = new ArrayList<>();
    private String transmitterMode = null; // which transport? ? or SpineTools
    public final static String SPINETOOLS_TRANSMITTER_MODE = "SpineTools";

    private static HashMap<String, String> interactionMap = null;

    /**
     * construct the interaction map from the properties file form is <br/>
     * tks.validator.interaction.map.&le;interactionid&gt; &le;http method&gt;
     * &le;reg exp on context path&gt;
     */
    private static void populateInteractionMap() {
        try {
            Configurator configurator = Configurator.getConfigurator();
            interactionMap = new HashMap<>();
            HashMap<String, String> localMap = (HashMap<String, String>) configurator.getConfigurationMap("^tks\\.validator\\.interaction\\.map\\..*$");
            if (localMap != null) {
                for (String key : localMap.keySet()) {
                    String value = localMap.get(key);
                    key = key.replaceFirst("^tks\\.validator\\.interaction\\.map\\.", "");
                    // unquote colons in quoted int id
                    key = key.replaceAll("\\\\:", ":");
                    interactionMap.put(key, value);
                }
            }
        } catch (Exception ex) {
            Logger.getInstance().log(WARNING, Schedule.class.getName(), "Failed to populate interaction map " + ex.getMessage());
        }
    }

    /**
     * Antlr parser constructor
     *
     * @param scheduleCtx
     */
    public Schedule(ScheduleContext scheduleCtx) {
        name = scheduleCtx.scheduleName().getText();
        if (scheduleCtx.LOOP() != null) {
            scheduleElements.add(new ScheduleElement(ScheduleElementType.LOOP, scheduleCtx));
        } else {
            for (AutotestParser.TestNameContext testName : scheduleCtx.testName()) {
                scheduleElements.add(new ScheduleElement(testName.getText()));
            }
        }
    }

    public String getName() {
        return name;
    }

    public Script getScript() {
        return script;
    }

    public void setSimulator(String s) {
        simulatorConfig = s;
    }

    @Override
    public void link(ScriptParser p)
            throws Exception {
        baseProperties = p.getPropertySet(NamedPropertySet.BASELINE);

        try {
            for (ScheduleElement se : scheduleElements) {
                se.getTests(p);
            }
        } catch (Exception e) {
            throw new Exception("Schedule " + name + " error: " + e.getMessage());
        }
    }

    /**
     * Runs the tests:
     * <ul>
     * <li>Make schedule log directory structure</li>
     * <li>If simulatorConfig is set, calculate the simulator save directory
     * names</li>
     * <li>Update with generated directories.</li>
     * <li>Start the rules engine and "Toolkit" if necessary for handling
     * asynchronous responses or to receive</li>
     * <li>Call execute on each of the ScheduleElement instances in tests</li>
     * </ul>
     *
     * @param s Containing script
     * @param r Run identifier (created by the calling script)
     * @throws Exception
     */
    public void execute(Script s, String r)
            throws Exception {
        script = s;
        ReportItem ri = null;
        ri = new ReportItem(name, null, "Starting");
        s.log(ri);
        String sof = s.getProperty(STOP_ON_FAIL_PROPERTY);
        boolean stoponfail = sof == null || isY(sof);
        if (stoponfail) {
            ri = new ReportItem(name, null, "This test run will stop if any part fails");
        } else {
            ri = new ReportItem(name, null, "This test run will continue if any part fails");
        }
        s.log(ri);

        // This assumes that all the logged messages for one schedule can go into the same set
        // of directories... which is *probably* true.
        //
        setupScheduleLogs();

        if (s.validatorConfig != null) {
            reconfigureValidator(s);
        }
        startServices();
        for (ScheduleElement se : scheduleElements) {
            boolean elementResult = se.execute(this, r, logRoot);
            if (!elementResult && stoponfail) {
                break;
            }
        }
        if (s.validatorConfig != null) {
            runValidations(s);
        }

    }

    public String getTransmitterDirectory() {
        return transmitterSource;
    }

    public String getSentMessagesDirectory() {
        return transmitterSentMessages;
    }

    public String getSimulatorDirectory() {
        return simulatorMessages;
    }

    /**
     * Make the directories, and call the appropriate reconfiguration methods on
     * the services, to have them used.
     *
     * @throws Exception
     */
    private void setupScheduleLogs()
            throws Exception {

        Properties p = script.getBootProperties();
        try {
            transmitterMode = p.getProperty(TRANSMITTERMODE_PROPERTY);
            ServiceManager smr = ServiceManager.getInstance();

            logRoot = new File(script.getRunDirectory(), name);
            logRoot.mkdirs();

            // transmitter source folder
            transmitterSource = createFolder("transmitter_source", p, TRANSMITDIR_PROPERTY);

            Reconfigurable transmitter = (Reconfigurable) smr.getService(transmitterMode + "Transmitter");
            transmitter.reconfigure(ReconfigureTags.SOURCE_DIRECTORY, transmitterSource);

            // transmitter sent messages
            transmitterSentMessages = createFolder("transmitter_sent_messages", p, TRANSMITLOG_PROPERTY);

            Reconfigurable sender = (Reconfigurable) smr.getService("Sender");
            sender.reconfigure(ReconfigureTags.DESTINATION_DIRECTORY, transmitterSentMessages);

            // simulator saved messages folder
            simulatorMessages = createFolder("simulator_saved_messages", p, SAVEDMESSAGES_PROPERTY);

            Reconfigurable toolkit = (Reconfigurable) smr.getService(transmitterMode + "Transport");
            if (toolkit != null) {
                toolkit.reconfigure(ReconfigureTags.SAVED_MESSAGES, simulatorMessages);
            } else {
                Logger.getInstance().log(SEVERE, Schedule.class.getName(),
                        "Failed to reconfigure service " + transmitterMode + " Transport with saved messages folder " + simulatorMessages);
            }
        } catch (Exception e) {
            ReportItem rdf = new ReportItem(name, null, "Directory setup failed");
            rdf.addDetail(e.toString());
            script.log(rdf);
            throw e;
        }
    }

    /**
     * create new folder for evidence , set properties and return absolute path
     * name
     *
     * @param name reconfigure tag name
     * @param p properties object to be updated
     * @param propertyName
     * @return fully qualified path name to folder
     */
    private String createFolder(String name, Properties p, String propertyName) {
        File f = new File(logRoot, name);
        f.mkdirs();
        String absolutePath = f.getAbsolutePath();
        p.setProperty(propertyName, absolutePath);
        baseProperties.updateBase(propertyName, absolutePath);
        return absolutePath;
    }

    private void startServices()
            throws Exception {
        if (simulatorConfig != null) {
            Properties p = script.getBootProperties();
            p.setProperty(RULEFILE_PROPERTY, simulatorConfig);
            baseProperties.updateBase(RULEFILE_PROPERTY, simulatorConfig);
            ServiceManager smr = ServiceManager.getInstance();
            Reconfigurable ruleService = (Reconfigurable) smr.getService("RulesEngine");
            ruleService.reconfigure(RULEFILE_PROPERTY, simulatorConfig);
        }

    }

    private void reconfigureValidator(Script s)
            throws Exception {
        // Make the directories, set the properties, then create and boot 
        // the validator.

        Properties p = script.getBootProperties();
        ServiceManager smr = ServiceManager.getInstance();
        Reconfigurable transmitter = (Reconfigurable) smr.getService("Validator");
        p.setProperty(VALIDATOR_CONFIG_PROPERTY, s.validatorConfig);
        baseProperties.updateBase(VALIDATOR_CONFIG_PROPERTY, s.validatorConfig);
        transmitter.reconfigure(VALIDATOR_CONFIG_PROPERTY, s.validatorConfig);

        validatorSource = new File(logRoot, "messages_for_validation");
        validatorSource.mkdirs();
        p.setProperty(VALIDATOR_SOURCE_PROPERTY, validatorSource.getAbsolutePath());
        baseProperties.updateBase(VALIDATOR_SOURCE_PROPERTY, validatorSource.getAbsolutePath());
        transmitter.reconfigure(VALIDATOR_SOURCE_PROPERTY, validatorSource.getAbsolutePath());

        validatorReports = new File(logRoot, "validator_reports");
        validatorReports.mkdirs();
        p.setProperty(VALIDATOR_REPORT_PROPERTY, validatorReports.getAbsolutePath());
        baseProperties.updateBase(VALIDATOR_REPORT_PROPERTY, validatorReports.getAbsolutePath());
        transmitter.reconfigure(VALIDATOR_REPORT_PROPERTY, validatorReports.getAbsolutePath());
    }

    /**
     * does what it says for the given script
     *
     * @param s the script object
     * @throws Exception
     */
    private void runValidations(Script s)
            throws Exception {
        HashMap<String, String> fileMap = gatherReceivedMessages();

        ServiceResponse sr = s.validator.execute(fileMap);
        TestResult allPassed = TestResult.FAIL;
        String comment;
        switch (sr.getCode()) {
            case ServiceResponse.ALLPASSED:
                allPassed = TestResult.PASS;
                comment = "Validation Successful";
                break;
            case ServiceResponse.NOTALLPASSED:
                allPassed = TestResult.FAIL;
                comment = "Validation Failed";
                break;
            case ServiceResponse.NOTRUN:
                allPassed = TestResult.FAIL;
                comment = "Validation not run";
                break;
            default:
                throw new Exception("Schedule Validation code not recorded");
        }

        // ITK 2.2 and after its not necesarily an error for there to be no messages returned
        if (sr.getCode() != ServiceResponse.NOTRUN) {
            ReportItem ri = new ReportItem(name, sr.getResponse(), allPassed, comment);

            ri.setLogFile(validatorReports + File.separator + sr.getResponse());
            s.log(ri);
        }
    }

    /**
     * @return hashmap of filenames and associated received messages
     * @throws Exception
     */
    private HashMap<String, String> gatherReceivedMessages()
            throws Exception {
        // Go through each of the schedules, get its log files and grab the
        // response parts. Copy to the validatorSource directory and keep a 
        // record in a HashMap of the validation file names and the original log
        // file names. Then go to the simulator logs (if the simulator is running)
        // and get what it was sent. Again copy to the validatorSource and record
        // in the HashMap. Return the HashMap.

        HashMap<String, String> fileMap = new HashMap<>();

        // copy transmitter and simulator logs into validator folder
        copyTransmitterReturns(transmitterSentMessages, fileMap);
        copySimulatorReturns(simulatorMessages, fileMap);

        return fileMap;
    }

    /**
     * constructs a relative path name
     *
     * @param absfile string containing the full path to the file
     * @param rd file object representing the file
     * @param parent boolean indicating whether to add parent drive path
     * @return String containing the resultant path
     */
    public static String getRelativeLinkPath(String absfile, File rd, boolean parent) {
        StringBuilder sb = new StringBuilder();
        if (parent) {
            sb.append("../");
        } else {
            sb.append("./");
        }
        // append the part after the path
        String absPath = rd.getAbsolutePath();
        // Resolves problems under Windows when absfile does not have a initial drive eg C: 
        // since rd.getAbsolutePath() *will* produce a leading drive letter 
        // this may be the case when run from cmd setting the default drive to eg E:
        // with a properties file that does not have a preceeding E:
        if ((absPath.matches("^[A-Za-z]:.*$") && absfile.matches("^[A-Za-z]:.*$"))
                || (!absPath.matches("^[A-Za-z]:.*$") && !absfile.matches("^[A-Za-z]:.*$"))) {
            sb.append(absfile.substring(absPath.length() + 1));
        } else {
            sb.append(absfile.substring(absPath.length() - 1));
        }

        // global substitution of backslash with forward slash
        // looks complicated but we are manipulating a StringBuilder not a String
        int l = -1;
        while ((l = sb.indexOf("\\")) != -1) {
            sb.replace(l, l + 1, "/");
        }
        return sb.toString();
    }

    /**
     * copy transmitter set messages to the validation folder
     *
     * @param tdir source log folder
     * @param links
     * @throws Exception
     */
    private void copyTransmitterReturns(String tdir, HashMap<String, String> links)
            throws Exception {
        // Go through the files in tdir, copying to a file in the validatorSource
        // logged information received from the SUT (i.e. everything after the 
        // delimiter between sent and received data in the log file)

        File logDir = new File(tdir);
        if (!logDir.isDirectory()) {
            throw new Exception("Error extracting validator data: " + tdir + " is not a directory");
        }
        File[] list = logDir.listFiles();
        for (File f : list) {
            // mod for ITK Trunk ignore folders and TKW logs 
            if (f.getName().endsWith(".signature") || f.getName().startsWith("TKW") || f.isDirectory()) {
                continue;
            }
            String ofile = f.getName().substring(0, f.getName().indexOf(".")) + "_response.out";

            links.put(ofile, getRelativeLinkPath(f.getAbsolutePath(), logRoot, true));

            File outputFile = new File(validatorSource, ofile);

            String body = null;
            SynchronousResponseBodyExtractor synchronousResponseBodyExtractor = new SynchronousResponseBodyExtractor();
            try {
                body = synchronousResponseBodyExtractor.getBody(new FileInputStream(f.getAbsolutePath()));
                // Typically this is just xml in which the interaction was embedded
                // however for restful responses this may (eg for ssp) or may not (eg nrls) be held in the header
                try (FileWriter ofw = new FileWriter(outputFile)) {
                    HttpHeaderManager httpRequestHeaders = synchronousResponseBodyExtractor.getHttpRequestHeaders();
                    HttpHeaderManager httpResponseHeaders = synchronousResponseBodyExtractor.getHttpResponseHeaders();
                    if (httpRequestHeaders != null) {
                        String interactionID = httpRequestHeaders.getHttpHeaderValue(SSP_INTERACTION_ID_HEADER);
                        if (!Utils.isNullOrEmpty(interactionID)) {
                            // write the whole log file since its a restful interaction if ssp is populated
                            // also theres no hint required because of the int id in the header 
                            String logStr = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                            ofw.write(("VALIDATE-AS: TRANSMITTER_LOG_RESPONSE\r\n" + logStr));
                        } else {
                            String responseContentType = httpResponseHeaders != null ? httpResponseHeaders.getHttpHeaderValue(CONTENT_TYPE_HEADER) : "";
                            if (!Utils.isNullOrEmpty(responseContentType) && responseContentType.toLowerCase().contains("fhir")) {
                                // see if we can get an interaction id from aMessageHeader
                                if (!isNullOrEmpty(fhirServiceLocation) && !isNullOrEmpty(body) ) {
                                    if (  body.trim().startsWith("<") ) {
                                        interactionID = XPathManager.xpathExtractor(fhirServiceLocation, body);
                                    } else if (body.trim().startsWith("{")) {
                                        String xmlBody = FHIRJsonXmlAdapter.fhirConvertJson2Xml(body);
                                        interactionID = XPathManager.xpathExtractor(fhirServiceLocation, xmlBody);
                                    }
                                }
                                
                                if (isNullOrEmpty(interactionID)) {
                                    // non ssp fhir ie NRLS at the time of writing we need to workout what the int id is
                                    // then add it as a hint in the validate-as 
                                    String method = synchronousResponseBodyExtractor.getHttpRequestMethod();
                                    String cp = synchronousResponseBodyExtractor.getHttpRequestContextPath();
                                    interactionID = derivePseudoInteractionID(method, cp);
                                }

                                if (!Utils.isNullOrEmpty(interactionID)) {
                                    String logStr = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                                    ofw.write(("VALIDATE-AS: TRANSMITTER_LOG_RESPONSE/" + interactionID + "\r\n" + logStr));
                                }
                            } else {
                                // no sspInteractionID/ no fhir found so just write the body
                                // this leg is reached for typical standard ITK interactions where the interactionid is available in the payload
                                // and the headers convey no usefully validatable information
                                ofw.write(body);
                            }
                        }
                    } // if hhtpRequestHeaders non null
                } // try write
                // If there was no response - e.g. HTTP 202 to an asynchronous
                // request, then stop the validator trying to look at it.
                //
                //  mod for ITK Trunk on zero length files
                if (outputFile.length() == 0) {
                    outputFile.delete();
                }
            } catch (Exception ex) {
                // trap invalid log file exception
                // no body detected
            }
        }
    }

    /**
     * copy simulator saved messages to the validation folder
     *
     * @param sdir source log folder
     * @param links
     * @throws Exception
     */
    private void copySimulatorReturns(String sdir, HashMap<String, String> links)
            throws Exception {
        // Go through the files in sdir, copying to a file in the validatorSource
        // logged information received from the SUT (i.e. everything before the 
        // delimiter between sent and received data in the log file)

        File logDir = new File(sdir);
        if (!logDir.isDirectory()) {
            throw new Exception("Error extracting validator data: " + sdir + " is not a directory");
        }
        File[] list = logDir.listFiles();
        for (File f : list) {
            // mod for IKT_Trunk also ignore subfolders and TKW Logs
            if (f.getName().endsWith(".signature") || f.getName().endsWith(".lck") || f.isDirectory() || f.getName().startsWith("TKW")) {
                continue;
            }

            File output = new File(validatorSource, f.getName());

            links.put(output.getName(), getRelativeLinkPath(f.getAbsolutePath(), logRoot, true));

            FileWriter ofw = null;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            boolean readingBody = false;
            boolean matched = false;
            while ((line = br.readLine()) != null) {
                // TODO this is nearly duplicated functionality from AsynchronousResponseBodyExtractor
                // Check for ITK style log content NB this breaks if you pre trim the line
                if ((line.matches("^<([^/].*:)?Envelope.*$") || line.matches("^<([^/].*:)?DistributionEnvelope.*$")) && !matched) {
                    ofw = new FileWriter(output);
                    readingBody = true;
                    matched = true;
                }
                if (readingBody) {
                    if (line.startsWith(LogMarkers.END_INBOUND_MARKER)) {
                        ofw.flush();
                        ofw.close();
                        break;
                    }
                    ofw.write(line);
                    ofw.write("\r\n");
                    ofw.flush();
                } else if (line.trim().length() == 0) {
                    readingBody = true;
                    ofw = new FileWriter(output);
                }
            }
        }
    }

    /**
     * chains non LOOP tests LOOP tests are chained by
     * ScheduleElement.getTests()
     *
     * @param p The script parser
     */
    public void chainTests(ScriptParser p) {
        Test prevTest = null;
        for (ScheduleElement scheduleElement : scheduleElements) {
            if (!scheduleElement.isMultiple()) {
                // ie not a loop containing multiple tests
                Test test = scheduleElement.getTest();
                // only chain if its a chained test eg not a send
                if (prevTest != null && test.getChainType() != null) {
                    test.setChain(prevTest);
                    try {
                        test.link(p);
                    } catch (Exception ex) {
                        Logger.getInstance().log(WARNING, Schedule.class.getName(), ex.getMessage());
                    }
                }
                prevTest = test;
            }
        }
    }

    /**
     * workout the pseudo interactionid for a non ssp restful service ie NRLS
     * tries to match a combination of http emthod and reg exp on the context
     * path mapping info is held in the properties file
     *
     * @param method http method
     * @param cp context path
     * @return derived interaction id or null for no lookup
     */
    public static String derivePseudoInteractionID(String method, String cp) {
        if (interactionMap == null) {
            populateInteractionMap();
        }
        for (String interactionid : interactionMap.keySet()) {
            String iMethod = interactionMap.get(interactionid).replaceFirst("^([^ ]+) +.*$", "$1");
            if (method.equals(iMethod)) {
                String iCpRe = interactionMap.get(interactionid).replaceFirst("^[^ ]+ +(.*)$", "$1");
                if (cp.matches(iCpRe)) {
                    return interactionid;
                }
            }
        }
        return null;
    }
}
