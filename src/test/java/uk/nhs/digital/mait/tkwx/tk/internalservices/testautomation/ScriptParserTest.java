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
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SEND_CDA_V2_SERVICE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScheduleElementTest.TEST_PREFIX;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.Http500;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassFailCheck;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class ScriptParserTest {

    private ScriptParser instance;
    private AutotestParser.PassfailsContext passfailsCtx;

    public ScriptParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        new File("src/test/resources/test.tdv").delete();
    }

    @Before
    public void setUp() throws IOException, Exception {
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        instance = new ScriptParser(props);
        instance.parse("src/test/resources/test.tst");
        TestVisitor visitor = new TestVisitor();
        passfailsCtx = visitor.getPassfailsContext();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBootProperties method, of class ScriptParser.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        Properties result = instance.getBootProperties();
        assertTrue(result.size() > 0);
    }

    /**
     * Test of parse method, of class ScriptParser.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        String file = "src/test/resources/test.tst";
        Script result = instance.parse(file);
        assertTrue(result != null);
    }

    /**
     * Test of getDataSource method, of class ScriptParser.
     */
    @Test
    public void testGetDataSource() throws Exception {
        System.out.println("getDataSource");
        String name = "patients";
        DataSource result = instance.getDataSource(name);
        assertTrue(result != null);
    }

    /**
     * Test of getTest method, of class ScriptParser.
     */
    @Test
    public void testGetTest() throws Exception {
        System.out.println("getTest");
        String name = TEST_PREFIX + "test1";
        uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test result = instance.getTest(name);
        assertTrue(result != null);
    }

    /**
     * Test of getPassFailCheck method, of class ScriptParser.
     */
    @Test
    public void testGetPassFailCheck() throws Exception {
        System.out.println("getPassFailCheck");
        String name = "actionok";
        PassFailCheck result = instance.getPassFailCheck(name);
        assertTrue(result != null);
    }

    /**
     * Test of getMessage method, of class ScriptParser.
     */
    @Test
    public void testGetMessage() throws Exception {
        System.out.println("getMessage");
        String name = "DE_EMPPID";
        Message result = instance.getMessage(name);
        assertTrue(result != null);
    }

    /**
     * Test of getExtractor method, of class ScriptParser.
     */
    @Test
    public void testGetExtractor() throws Exception {
        System.out.println("getExtractor");
        String name = "extractor_name";
        String expResult = "uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.SingleRecordXpathResponseExtractor";
        String result = instance.getExtractor(name).getClass().getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTemplate method, of class ScriptParser.
     */
    @Test
    public void testGetTemplate() throws Exception {
        System.out.println("getTemplate");
        String name = "DE_EMPPID_"+SEND_CDA_V2_SERVICE+"_template";
        Template result = instance.getTemplate(name);
        assertTrue(result != null);
    }

    /**
     * Test of getPropertySet method, of class ScriptParser.
     */
    @Test
    public void testGetPropertySet() throws Exception {
        System.out.println("getPropertySet");
        String name = "webservices";
        NamedPropertySet result = instance.getPropertySet(name);
        assertTrue(result != null);
    }

    /**
     * Test of makePassFail method, of class ScriptParser.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakePassFail() throws Exception {
        System.out.println("makePassFail");
        for (PassfailContext passfailCtx : passfailsCtx.passfail()) {
            if (passfailCtx.passFailCheckName().getText().equals("fail")) {
                Class<Http500> expResult = Http500.class;
                PassFailCheck result = instance.makePassFail(passfailCtx.passFailCheck());
                assertEquals(expResult, result.getClass());
                break;
            }
        }
    }

    /**
     * Test of getHttpHeaderSet method, of class ScriptParser.
     */
    @Test
    public void testGetHttpHeaderSet() throws Exception {
        System.out.println("getHttpHeaderSet");
        String name = "headerset1";
        HashMap<String, Object> result = instance.getHttpHeaderSet(name);
        assertNotNull(result);
    }

    /**
     * Test of getSubstitutionTags method, of class ScriptParser.
     */
    @Test
    public void testGetSubstitutionTags() throws Exception {
        System.out.println("getSubstitutionTags");
        HashMap<String, Object> result = instance.getSubstitutionTags();
        assertNotNull(result);
    }
}
