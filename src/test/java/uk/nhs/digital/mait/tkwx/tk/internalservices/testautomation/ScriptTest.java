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
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class ScriptTest {

    private static AutotestParser.ScheduleContext scheduleCtx;

    private Script instance;

    public ScriptTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        scheduleCtx = visitor.getScheduleContext();
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
    public void setUp() {
        instance = new Script();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addSchedule method, of class Script.
     */
    @Test
    public void testAddSchedule() {
        System.out.println("addSchedule");
        Schedule s = new Schedule(scheduleCtx);
        instance.addSchedule(s);
    }

    /**
     * Test of setStopWhenComplete method, of class Script.
     */
    @Test
    public void testSetStopWhenComplete() {
        System.out.println("setStopWhenComplete");
        instance.setStopWhenComplete();
    }

    /**
     * Test of setName method, of class Script.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String n = "name";
        instance.setName(n);
    }

    /**
     * Test of setSimulatorRules method, of class Script.
     */
    @Test
    public void testSetSimulatorRules() {
        System.out.println("setSimulatorRules");
        String r = "";
        instance.setSimulatorRules(r);
    }

    /**
     * Test of setProperties method, of class Script.
     */
    @Test
    public void testSetProperties() {
        try {
            System.out.println("setProperties");
            Properties props = new Properties();
            props.load(new FileInputStream(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
            instance.setProperties(props);
        } catch (IOException ex) {
            fail("Exception thrown " + ex.getMessage());
        }
    }

    /**
     * Test of getBootProperties method, of class Script.
     */
    @Test
    public void testGetBootProperties() {
        try {
            System.out.println("getBootProperties");
            Properties expResult = new Properties();
            expResult.load(new FileInputStream(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
            instance.setProperties(expResult);
            Properties result = instance.getBootProperties();
            assertEquals(expResult, result);
        } catch (IOException ex) {
            fail("Exception thrown " + ex.getMessage());
        }
    }

    /**
     * Test of getProperty method, of class Script.
     */
    @Test
    public void testGetProperty() {
        System.out.println("getProperty");
        Properties p = new Properties();
        String expResult = "val";
        String s = "prop";
        p.setProperty(s, expResult);
        instance.setProperties(p);
        String result = instance.getProperty(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRunDirectory method, of class Script.
     */
    @Test
    public void testGetRunDirectory() {
        System.out.println("getRunDirectory");
        File expResult = null;
        File result = instance.getRunDirectory();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSimulator method, of class Script.
     */
    @Test
    public void testGetSimulator() {
        System.out.println("getSimulator");
        ToolkitSimulator expResult = null;
        ToolkitSimulator result = instance.getSimulator();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class Script.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "name";
        instance.setName(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValidatorConfig method, of class Script.
     */
    @Test
    public void testSetValidatorConfig() {
        System.out.println("setValidatorConfig");
        String v = "";
        instance.setValidatorConfig(v);
    }

    /**
     * Test of log method, of class Script.
     */
    @Test
    public void testLog() {
        System.out.println("log");
        ReportItem r = new ReportItem("schedule", "src/test/resources", TestResult.PASS, "comment");
        instance.reportWriter = new ReportWriter(new Script(), new File("report.log"));
        instance.log(r);
    }

    /**
     * Test of clear method, of class Script.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        instance.clear();
    }

    /**
     * Test of execute method, of class Script.
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        ToolkitSimulator t = new ToolkitSimulator(TKWROOT + "/config/ITK_Autotest/tkw-x.properties");
        Properties props = new Properties();
        props.load(new FileInputStream(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        instance.setProperties(props);
        instance.setName("testscript");
        instance.execute(t);
    }

    /**
     * Test of stopSimulator method, of class Script.
     */
    @Test
    public void testStopSimulator() throws Exception {
        System.out.println("stopSimulator");
        Properties props = new Properties();
        props.load(new FileInputStream(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        instance.setProperties(props);
        instance.setName("testscript");
        instance.stopSimulator();
    }

}
