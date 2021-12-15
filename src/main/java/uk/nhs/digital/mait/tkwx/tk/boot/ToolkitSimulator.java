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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.io.BufferedReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import static java.util.logging.Level.SEVERE;
import org.apache.commons.text.StringSubstitutor;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.commonutils.util.ConfigurationStringTokeniser;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.util.Utils;
// Rev 176  Update to support json Hapi Fhir profiles
// Rev 175  Recommit as ANTLR artefacts didnt recreate on previous commit
// Rev 174  Updated CDSS functionality and extended HAPI FHIR to be able to validate multiple configurations
// Rev 173  Updated CDSS functionality to include offline hapifhir validation
// Rev 172  Added some tests for new features of rev 171
// Rev 171  Addition of HTTP header substitution points + tweaks to fhir profile
// Rev 170  Tweaks to CheckConfiguredSspTo to allow use of internal default property values. 
// Rev 169  Fixed another test. Renamed a test log file to avoid deletion. Added full url decoding to fhir for FhirDateCompareExpression
// Rev 168  Enhancement to tests, modified hapifhir parser to add versions to references
// Rev 167  Removed dependency on TKWPropertyManager, fixes for use in Docker with gpconnect
// Rev 166  Testing to support 165
// Rev 165  Implemented the generation of a MetaData log.
// Rev 164  Removed dependency on external uuid generator. Extra utils tests.
// Rev 163  Positioning for use of modules. Rationalisation of pom. Moved all resources into eponymous directory. Now passes all but one tests under Maven.
// Rev 162  Removed svn tags from all but ToolkitSimulator. Removed nugatory import from the the same package.
// Rev 161  Added Rev and Id keywords properties to this file so that the revision strings are updated automatically.
// Rev 160  Added class expressions to rules syntax allowing java extensions to expressions using text rather tham xml
// Rev 159  More tweaks to the Antlr piece
// Rev 158  Uses Antlr plugin, Perform fhir parsing on xml as well as json
// Rev 157  Tweaks to the tests. More tests tidy up on termination. 
// Rev 156  Adjustments to test artefacts. Changes to pom to ensure jvm isolation which is not the default.
// Rev 155  Post mavenisation updates to missed files. Minor changes to pom
// Rev 154  Mavenisation
// Rev 153  Mavenisation
// Rev 152  Mavenisation
// Rev 151  Adjustment to some Junit Tests. HttpForwarder no longer support proxy aware clients.
// Rev 150  Fix to stop a casting exception for SpineValidatorService
// Rev 149 Updates to allow inline validation in the simulator responses
// Rev 148 Disable proxy aware handling for HttpInterceptor. Not used and probably not safe anyway.
// Rev 147 Fixed a null pointer exception in Substitution that only appears when running in Java 11 JRE
// Rev 146 Reapplied the changes from 143
// Rev 145 To Java 11,. package name change to uk.nhs.digital.mait.tkwx
// Rev 144 To Java 11,. package name change to uk.nhs.digital.mait.tkwx
// Rev 143 This fixes HAPI FHIR validation report filtering when there are multiple documents being validated. 
// Rev 142 Removed calls to execute ( String String )
// Rev 141 Removed calls to execute ( String Object )
// Rev 140 Removed Synchronous ITK Soap Handlers, Soap Header, SignerService
// Rev 139 Removed experimental, Delivery Resolver, Queue, Processor, distributor
// Rev 138 Fixed group count defect TKW-x #1 in HttpInterceptor, Removed XMLEncryption
// Rev 137 Removed ITK Soap Specific tests from Autotest
// Rev 136 renamed root folder
// Rev 135 Removed further PH code, removed ITK Async Code
// Rev 134 Post migration to TKW-x, removed ers, mllp, P&H

/**
 * Main class.
 *
 * @author Damian Murphy murff@warlock.org
 *
 * Note: From v2.0.13 this adds a dependency on DistributionEnvelopeTools.
 *
 */
public class ToolkitSimulator {

    public static String versionString;
    private static final String USAGE = "java -jar TKW-x.jar [-version] -transmit|-validate|-simulator|-httpinterceptor|-meshinterceptor|-spinevalidate|-autotest propertiesfile [tstfile]";

    protected static final String SERVICES = "tks.servicenames";
    private static final String TKSNAME = "tks.configname";
    private static final String CLASSROOT = "tks.classname.";
    private static final String TKSORG = "tks.username";

    private static final String INTERNALPROPERTIES = "tkw.internal.properties";

    private Properties properties = null;
    private String configurationName = null;
    private String organisationName = null;

    public ToolkitSimulator(String propertiesfile)
            throws Exception {

        // Commented out since this breaks when OSGi bundles are used since each bundle has its own class loader
        //System.setProperty("javax.xml.xpath.XPathFactory", "net.sf.saxon.xpath.XPathFactoryImpl");
        // Set default JRE HTTP keep alive functionality to be false. This means that when TKW uses the java HTTP 
        // classes that the HTTP header will be populated with connection: close. 
        // This is important for the internal http call i.e. getUUID post the addition of HTTP streaming to TKW
        // TODO check this still works under OSGi
        System.setProperty("http.keepAlive", "false");
        Properties prop = new Properties();

        // load the Internal TKW properties file first so that the external file can overwrite it
        InputStream is = getClass().getResourceAsStream(INTERNALPROPERTIES);
        prop.load(is);

        try ( // load the External TKW properties - Port names for the request handlers MUST be appended
                 BufferedReader br = new BufferedReader(new FileReader(propertiesfile))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    continue;
                }
                if (line.trim().length() == 0) {
                    continue;
                }

                ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
                String key = st.nextToken();
                StringBuilder value = new StringBuilder(st.nextToken());
                String element;
                while (st.hasMoreTokens()) {
                    element = st.nextToken();
                    value.append(" ");
                    value.append(element);
                }
                // Add extra context names to the namelist
                if (key.equals("tks.HttpTransport.namelist")) {
                    value.insert(0, prop.getProperty("tks.HttpTransport.namelist") + " ");
                }
                // Copy any properties to system properties needed for the any of the server's SSL listeners.
                if (key.endsWith(".usesslcontext") || key.endsWith(".sslcontextpass") || key.endsWith(".sslalgorithm")) {
                    System.setProperty(key, value.toString());
                }
                prop.setProperty(key, value.toString());
            }
        }

        properties = new Properties();
        for (Entry entry: prop.entrySet()) {
            String key = (String) entry.getKey();
            String oldValue = (String) entry.getValue();
            String newValue = replaceTkwroot(replaceEnvVars(oldValue));
            properties.setProperty(key, newValue);
        }

        if (properties.getProperty(DONTSIGNLOGS_PROPERTY) != null) {
            System.setProperty(DONTSIGNLOGS_PROPERTY, properties.getProperty(DONTSIGNLOGS_PROPERTY));
        }
        String logFolder = properties.getProperty("tks.logdir");
        Utils.createFolderIfMissing(logFolder);
        Logger.getInstance().setAppName("TKS", logFolder);
        configurationName = properties.getProperty(TKSNAME, "Not given");
        organisationName = properties.getProperty(TKSORG, "Not given");
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
        Configurator c = Configurator.getConfigurator();
        c.setProperties(properties);
    }

    private static String replaceEnvVars(String s) {
        StringSubstitutor sub = new StringSubstitutor(System.getenv());
        return sub.replace(s);
    }

    private static String replaceTkwroot(String s) {
        if (!Utils.isNullOrEmpty(System.getenv("TKWROOT"))) {
            return s.replaceAll("TKW_ROOT", System.getenv("TKWROOT"));
        }
        return s;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public String getVersion() {
        return versionString;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    /**
     *
     * @throws Exception
     */
    public void boot()
            throws Exception {
        // 1. Read tks.services to get a list of the services to call
        // 2. For each, look up tks.servicename.class to find the class, and load it
        // 3. Once loaded, call "boot" and pass properties to it

        String sn = properties.getProperty(SERVICES);
        if (sn == null) {
            Logger.getInstance().log(SEVERE, ToolkitSimulator.class.getName(), "property " + SERVICES + " not defined");
            return;
        }
        String tksname = properties.getProperty(TKSNAME, "Not defined");
        System.out.println(Logger.getDate() + " booting " + tksname);
        Logger.getInstance().log(ToolkitSimulator.class.getName(), tksname);
        StringTokenizer st = new StringTokenizer(sn);
        while (st.hasMoreElements()) {
            String t = st.nextToken();
            startService(t);
        }
        System.out.println("ITK Testbench ready");
        Logger.getInstance().log("ITK Testbench ready");
    }

    /**
     *
     * @param sname service name
     * @throws Exception
     */
    public void startService(String sname)
            throws Exception {
        String serviceClass = properties.getProperty(CLASSROOT + sname);
        if (serviceClass == null) {
            throw new Exception("Class name not given for service " + sname + " : no property: " + CLASSROOT + sname);
        }
        try {
            ToolkitService t = ((ToolkitService) Class.forName(serviceClass).newInstance());
            t.boot(this, properties, sname);
            ServiceManager.getInstance().addService(sname, t);
        } catch (Exception e) {
            throw new Exception("Failed to initialise class " + serviceClass + " : " + e.toString());
        }
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable e) {
            Logger.getInstance().warn(ToolkitSimulator.class.getName(), e.toString());
        }
        Logger.getInstance().close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println(versionString + " starting on " + System.getProperty("os.name") + " version " + System.getProperty("os.version") + ", " + System.getProperty("os.arch"));
        Mode selectedMode = null;
        boolean showVersion = false;
        String pfile = null;
        String testscript = null;

        for (String arg : args) {
            switch (arg) {
                case "-version":
                    showVersion = true;
                    break;
                case "-transmit":
                    selectedMode = new TransmitterMode();
                    break;
                case "-validate":
                    selectedMode = new ValidatorMode();
                    break;
                case "-simulator":
                    selectedMode = new SimulatorMode();
                    break;
                case "-spinevalidate":
                    selectedMode = new SpineValidatorMode();
                    break;
                case "-httpinterceptor":
                    selectedMode = new HttpInterceptorMode();
                    break;
                case "-meshinterceptor":
                    selectedMode = new MeshInterceptorMode();
                    break;
                case "-autotest":
                    selectedMode = new AutoTestMode();
                    break;
                default:
                    if (pfile == null) {
                        pfile = arg;
                    } else {
                        // a final testscript parameter after the properties file is required for autotest mode but not otherwise
                        testscript = arg;
                    }
            } // switch
        } // for args

        if (pfile == null) {
            if (showVersion) {
                System.out.println(versionString);
            } else {
                System.err.println(USAGE);
            }
            System.exit(1);
        }

        if (showVersion) {
            System.out.println(versionString);
        }

        ToolkitSimulator t = null;
        try {
            t = new ToolkitSimulator(pfile);
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, ToolkitSimulator.class.getName(), "Failed to load properties file " + pfile + " : " + e.toString());
            System.exit(1);
        }

        try {
            selectedMode.init(t);
            ToolkitService tks = selectedMode.getService();
            selectedMode.executeService(testscript);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().log(SEVERE, ToolkitSimulator.class.getName(), "Toolkit boot error: " + (e.getMessage() != null ? e.getMessage() : e));
        }

        if (testscript != null) {
            // only required when in autotest mode
            // TODO workaround to ensure closes based on finalize get called
            // docs state that one cannot rely on finalize being called when an application exits
            // waiting 5s after garbage collection allows all the objects to close (hopefully)
            System.gc();
            System.out.println("Waiting 5000 ms after garbage collection");
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.print(".");
                    System.out.flush();
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            System.out.println();

            Thread[] threads = new Thread[Thread.activeCount()];
            if (threads.length > 1) {
                System.err.println("WARNING: extra threads still executing");
                Thread.enumerate(threads);
                for (Thread thread : threads) {
                    if (!thread.getName().equals("main")) {
                        System.err.printf("%s state %s\r\n", thread.getName(), thread.getState().toString());
                    }
                }
                System.exit(-1);
            }
        }
    }

    static {
        ClassLoader classLoader = ToolkitSimulator.class.getClassLoader();
        Properties versionProperties = new Properties();
        try {
            versionProperties.load(classLoader.getResourceAsStream("git.properties"));
        } catch (IOException ex) {
            System.err.println("Couldn't load git properties " + ex.getMessage());
            System.exit(1);
        }
        versionString = String.format("NHS Digital Interoperability Toolkit Testbench TKW-x-%s %s %s",
                versionProperties.getProperty("git.build.version"),
                versionProperties.getProperty("git.commit.id.abbrev"),
                versionProperties.getProperty("git.commit.time"));

    }
}
