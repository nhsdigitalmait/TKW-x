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
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScheduleElement.ScheduleElementType.LOOP;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestTest.deleteFolder;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class ScheduleElementTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private static AutotestParser.ScheduleContext scheduleCtx;
    private static AutotestParser.TestsContext testsCtx;

    private ScheduleElement single;
    private ScheduleElement loop;

    public final static String TEST_PREFIX = "DE_EMPPID_";
    private Properties props;

    public ScheduleElementTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        scheduleCtx = visitor.getScheduleContext();
        testsCtx = visitor.getTestsContext();
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
    public void setUp() throws IOException {
        props = new Properties();
        // dont forget to load the internal props
        props.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        // speed things up its going to fail to connect anyway
        props.setProperty("tks.autotest.synchronous.log.retries","1");
        props.setProperty("tks.autotest.synchronous.log.delay","500");

        single = new ScheduleElement(TEST_PREFIX + "test1");
//        List<TestNameContext> testNameCtxList = new ArrayList<>();
//        for (TestContext testCtx : testsCtx.test()) {
//            testNameCtxList.add(testCtx.testName());
//        }
        loop = new ScheduleElement(LOOP, scheduleCtx);
    }

    @After
    public void tearDown() {
        deleteFolder("reportfolder");
    }

    /**
     * Test of isMultiple method, of class ScheduleElement.
     */
    @Test
    public void testIsMultiple() {
        System.out.println("isMultiple");
        boolean expResult = false;
        boolean result = single.isMultiple();
        assertEquals(expResult, result);

        expResult = true;
        result = loop.isMultiple();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTests method, of class ScheduleElement.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetTests() throws Exception {
        System.out.println("getTests");
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        ScriptParser p = new ScriptParser(props);
        p.parse("src/test/resources/test.tst");
        loop.getTests(p);
    }

    /**
     * Test of execute method, of class ScheduleElement.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");

        // ensure correct configurator is used
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);

        ToolkitSimulator t = new ToolkitSimulator(TKWROOT + "/config/ITK_Autotest/tkw-x.properties");
        t.startService("HttpTransport");
        Mode mode = new TransmitterMode();
        mode.init(t);
        Schedule schedule = new Schedule(scheduleCtx);
        Script script = new Script();
        script.setName("testscript");
        File logroot = new File("reportfolder");
        script.setName("scriptname");

        script.setProperties(props);

        ScriptParser p = new ScriptParser(props);
        p.parse("src/test/resources/test.tst");
        schedule.link(p);
        script.execute(t);

        String runName = "run1";
        boolean expResult = false;
        schedule.execute(script, "runid");

        single.getTests(p);
        boolean result = single.execute(schedule, runName, logroot);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTest method, of class ScheduleElement.
     * @throws java.io.IOException
     */
    @Test
    public void testGetTest() throws IOException, Exception {
        System.out.println("getTest");

        ScriptParser p = new ScriptParser(props);
        p.parse("src/test/resources/test.tst");
        
        single.getTests(p);

        String expResult = "DE_EMPPID_test1";
        
        // don't want to get default Test which is JUnit
        uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test result = single.getTest();
        assertEquals(expResult, result.getName());
    }

}
