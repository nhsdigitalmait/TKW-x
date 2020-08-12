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
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ScheduleContext;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class TestTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test instance;
    private uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test instance2;

    private static AutotestParser.TestsContext testsCtx;
    private static ScheduleContext scheduleCtx;
    private static final String SCHEDULE_NAME = "DE_EMPPID";
    private static final String TEST_SCRIPT = "src/test/resources/test.tst";
    private Schedule schedule;


    public TestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        testsCtx = visitor.getTestsContext();
        scheduleCtx = visitor.getScheduleContext();
        // ensure correct configurator is used
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        for (String filename : new String[]{"src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        instance = new uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test(testsCtx.test(0));
        instance2 = new uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test(testsCtx.test(1));
        ToolkitSimulator t = new ToolkitSimulator(TKWROOT + "/config/ITK_Autotest/tkw-x.properties");
        TransmitterMode mode = new TransmitterMode();

        // essential call to set the toolkitsimulator pointer (only required for testing)
        ServiceManager.getInstance(t);

        mode.init(t);
        t.startService("HttpTransport");

        Script script = new Script();
        script.setName("testscript");

        schedule = new Schedule(scheduleCtx);
        script.addSchedule(schedule);

        Properties props = new Properties();
        // dont forget to load the internal props
        props.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        props.load(new FileInputStream(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        // speed things up its going to fail to connect anyway
        props.setProperty("tks.autotest.synchronous.log.retries","1");
        props.setProperty("tks.autotest.synchronous.log.delay","500");

        ScriptParser scriptParser = new ScriptParser(props);
        scriptParser.parse(TEST_SCRIPT);
        schedule.link(scriptParser);

        script.reportWriter = new ReportWriter(script, new File("report.log"));
        script.setProperties(props);
        instance.setScript(script);

        instance.link(scriptParser);
        schedule.execute(script, "runid");
    }

    @After
    public void tearDown() {
        deleteFolder(SCHEDULE_NAME);
    }

    /**
     * overload
     *
     * @param folderName
     */
    public static void deleteFolder(String folderName) {
        deleteFolders(new String[]{folderName});
    }

    /**
     * recursively delete folder structures
     *
     * @param folderNames
     */
    public static void deleteFolders(String[] folderNames) {
        for (String folderName : folderNames) {
            File folder = new File(folderName);
            if (folder.exists()) {
                for (File file : folder.listFiles()) {
                    if (file.isDirectory()) {
                        try {
                            deleteFolder(file.getCanonicalPath());
                        } catch (IOException ex) {
                            fail("Exception deleting folder " + ex.getMessage());
                        }
                    } else if (!file.delete()) {
                        fail("Failed to delete file " + file.getName());
                    }
                }
                if (!folder.delete()) {
                    fail("Failed to delete folder " + folder.getName());
                }
            }
        }
    }

    /**
     * Test of setScript method, of class Test.
     */
    @Test
    public void testSetScript() {
        System.out.println("setScript");
        Script s = new Script();
        instance.setScript(s);
    }

    /**
     * Test of link method, of class Test.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLink() throws Exception {
        System.out.println("link");
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        
        ScriptParser p = new ScriptParser(props);
        p.parse(TEST_SCRIPT);
        instance.link(p);
    }

    /**
     * Test of getName method, of class Test.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = SCHEDULE_NAME+"_test1";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setChain method, of class Test.
     */
    @Test
    public void testSetChain() {
        System.out.println("setChain");
        instance2.setChain(instance);
    }

    /**
     * Test of execute method, of class Test.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");


        boolean expResult = false;
        boolean result = instance.execute("instancename", schedule);
        assertEquals(expResult, result);

        //result = instance2.execute("instancename", schedule);
        //assertEquals(expResult, result);
    }

    /**
     * Test of copy method, of class Test.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        String expResult = "DE_EMPPID_test1";
        uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test result = (uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test)instance.copy();
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of getMsgname method, of class Test.
     */
    @Test
    public void testGetMsgname() {
        System.out.println("getMsgname");
        String expResult = "DE_EMPPID";
        String result = instance.getMsgname();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSynchronousPassFailCheckName method, of class Test.
     */
    @Test
    public void testGetSynchronousPassFailCheckName() {
        System.out.println("getSynchronousPassFailCheckName");
        String expResult = "fail";
        String result = instance.getSynchronousPassFailCheckName();
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class Test.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Schedule() throws Exception {
        System.out.println("execute");
        String instanceName = "instancename";
        boolean expResult = false;
        boolean result = instance.execute(instanceName, schedule);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class Test.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_3args() throws Exception {
        System.out.println("execute");
        String instanceName = "instancename";
        int iteration = 0;
        boolean expResult = false;
        boolean result = instance.execute(instanceName, schedule, iteration);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChainType method, of class Test.
     */
    @Test
    public void testGetChainType() {
        System.out.println("getChainType");
        String expResult = null;
        String result = instance.getChainType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChainName method, of class Test.
     */
    @Test
    public void testGetChainName() {
        System.out.println("getChainName");
        String expResult = null;
        String result = instance.getChainName();
        assertEquals(expResult, result);
    }
}
