/*
 Copyright 2012-13  Simon Farrow <simon.farrow1@nhs.net>

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestTest.deleteFolder;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class ScheduleTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private static AutotestParser.ScheduleContext scheduleCtx;
    private final static String SCHEDULE_NAME = "DE_EMPPID";
    private static final String TDV_FILE = "src/test/resources/test.tdv";

    private Schedule instance;
    private Properties props;

    public ScheduleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", TDV_FILE);
        TestVisitor visitor = new TestVisitor();
        scheduleCtx = visitor.getScheduleContext();
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        new File(TDV_FILE).delete();
    }

    @Before
    public void setUp() throws IOException {
        props = new Properties();
        // dont forget to load the internal props
        props.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        props.load(new FileInputStream(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        // speed things up its going to fail to connect anyway
        props.setProperty("tks.autotest.synchronous.log.retries", "1");
        props.setProperty("tks.autotest.synchronous.log.delay", "500");

        instance = new Schedule(scheduleCtx);
    }

    @After
    public void tearDown() {
        // this is actually in the root folder of the dev tree but at least it disappears now
        deleteFolder(SCHEDULE_NAME);
    }

    /**
     * Test of getName method, of class Schedule.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = SCHEDULE_NAME;
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getScript method, of class Schedule.
     */
    @Test
    public void testGetScript() {
        System.out.println("getScript");
        Script expResult = null;
        Script result = instance.getScript();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSimulator method, of class Schedule.
     */
    @Test
    public void testSetSimulator() {
        System.out.println("setSimulator");
        String s = "";
        instance.setSimulator(s);
    }

    /**
     * Test of link method, of class Schedule.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLink() throws Exception {
        System.out.println("link");
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        ScriptParser p = new ScriptParser(props);
        p.parse("src/test/resources/test.tst");
        instance.link(p);
    }

    /**
     * Test of execute method, of class Schedule.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        runTKS();
    }

    /**
     * Test of getTransmitterDirectory method, of class Schedule.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetTransmitterDirectory() throws Exception {
        System.out.println("getTransmitterDirectory");
        runTKS();
        // method returns full path so need canaonical name
        String expResult = new File(SCHEDULE_NAME + "/transmitter_source").getCanonicalPath();
        String result = instance.getTransmitterDirectory().replace('\\', '/');
        assertEquals(expResult, result);
    }

    /**
     * Test of getSentMessagesDirectory method, of class Schedule.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSentMessagesDirectory() throws Exception {
        System.out.println("getSentMessagesDirectory");
        runTKS();
        // method returns full path so need canaonical name
        String expResult = new File(SCHEDULE_NAME + "/transmitter_sent_messages").getCanonicalPath();
        String result = instance.getSentMessagesDirectory().replace('\\', '/');
        assertEquals(expResult, result);
    }

    /**
     * Test of getSimulatorDirectory method, of class Schedule.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSimulatorDirectory() throws Exception {
        System.out.println("getSimulatorDirectory");
        runTKS();
        // method returns full path so need canaonical name
        String expResult = new File(SCHEDULE_NAME + "/simulator_saved_messages").getCanonicalPath();
        String result = instance.getSimulatorDirectory().replace('\\', '/');
        assertEquals(expResult, result);
    }

    protected void runTKS() throws Exception, IOException {
        // Ensure the correct configurator is constructed
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);

        ToolkitSimulator t = new ToolkitSimulator(TKWROOT + "/config/ITK_Autotest/tkw-x.properties");

        // essential call to set the toolkitsimulator pointer (only required for testing)
        ServiceManager.getInstance(t);
        Mode mode = new TransmitterMode();
        mode.init(t);

        Script script = new Script();
        script.setName("testscript");

        ScriptParser p = new ScriptParser(props);
        script.addSchedule(instance);
        p.parse("src/test/resources/test.tst");
        instance.link(p);
        script.reportWriter = new ReportWriter(script, new File("report.log"));
        script.setProperties(props);
        instance.execute(script, "runid");
    }

    /**
     * Test of getRelativeLinkPath method, of class Schedule.
     */
    @Test
    public void testGetRelativeLinkPath() {
        System.out.println("getRelativeLinkPath");
        String absfile = "/a/b/c/file.txt";
        File rd = new File("/a/b");
        boolean parent = false;
        String expResult = "./c/file.txt";
        String result = Schedule.getRelativeLinkPath(absfile, rd, parent);
        assertEquals(expResult, result);

        parent = true;
        expResult = "../c/file.txt";
        result = Schedule.getRelativeLinkPath(absfile, rd, parent);
        assertEquals(expResult, result);
    }

    /**
     * Test of chainTests method, of class Schedule.
     * @throws java.lang.Exception
     */
    @Test
    public void testChainTests() throws Exception {
        System.out.println("chainTests");
        ScriptParser p = new ScriptParser(props);
        instance.chainTests(p);
    }


    /**
     * Test of deriveInteractionID method, of class Schedule.
     * @throws java.lang.Exception
     */
    @Test
    public void testDeriveInteractionID() throws Exception {
        System.out.println("deriveInteractionID");
        // picks up interaction map props from ITK_Autotest, these relate to NRLS
        runTKS();

        String method = "POST";
        String cp = "/DocumentReference";
        String expResult = "urn:nhs:names:services:nrls:fhir:rest:create:documentreference";
        String result = Schedule.deriveInteractionID(method, cp);
        assertEquals(expResult, result);

        cp = "/xxx";
        expResult = null;
        result = Schedule.deriveInteractionID(method, cp);
        assertEquals(expResult, result);
    }

}
