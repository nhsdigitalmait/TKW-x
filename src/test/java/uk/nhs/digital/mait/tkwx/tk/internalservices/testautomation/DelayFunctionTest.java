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
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestTest.deleteFolders;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestContext;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class DelayFunctionTest {

    private static TestContext functionTestCtx;
    private static AutotestParser.ScheduleContext scheduleCtx;

    private DelayFunction instance;

    public DelayFunctionTest() {
    }

    /**
     *
     */
    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(System.getenv("TKWROOT") + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        functionTestCtx = visitor.getFunctionTestContext();
        scheduleCtx = visitor.getScheduleContext();
    }

    @AfterClass
    public static void tearDownClass() {
        for (String filename : new String[]{"src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() {
        instance = new DelayFunction();
    }

    @After
    public void tearDown() {
        deleteFolders(new String[]{"logFolder", "name", "DE_EMPPID"});
    }

    /**
     * Test of init method, of class DelayFunction.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(functionTestCtx);
    }

    /**
     * Test of execute method, of class DelayFunction.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        String instanceName = "instance";

        Schedule schedule = new Schedule(scheduleCtx);

        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/ITK_Autotest/tkw-x.properties");
        TransmitterMode mode = new TransmitterMode();
        mode.init(t);
        t.startService("HttpTransport");

        Script script = new Script();
        script.setName("testscript");
        Properties props = new Properties();
        // dont forget to load the internal props
        props.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        props.load(new FileInputStream(System.getenv("TKWROOT") + "/config/ITK_Autotest/tkw-x.properties"));
        // speed things up its going to fail to connect anyway
        props.setProperty("tks.autotest.synchronous.log.retries","1");
        props.setProperty("tks.autotest.synchronous.log.delay","500");

        ScriptParser scriptParser = new ScriptParser(props);
        script.addSchedule(schedule);
        scriptParser.parse("src/test/resources/test.tst");
        schedule.link(scriptParser);

        script.reportWriter = new ReportWriter(script, new File("report.log"));
        script.setProperties(props);
        uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test test
                = new uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test(functionTestCtx);
        test.setScript(script);

        test.link(scriptParser);
        schedule.execute(script, "runid");

        boolean expResult = true;
        instance.init(functionTestCtx);
        boolean result = instance.execute(instanceName, schedule, test);
        assertEquals(expResult, result);
    }

}
