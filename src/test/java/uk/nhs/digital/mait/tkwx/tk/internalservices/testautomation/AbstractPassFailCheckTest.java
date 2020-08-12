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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class AbstractPassFailCheckTest {

    private static PassfailContext passfailCtx;

    private AbstractPassFailCheckImpl instance;

    public AbstractPassFailCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        passfailCtx = visitor.getPassfailContext();
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
        instance = new AbstractPassFailCheckImpl();
        instance.init(passfailCtx);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class AbstractPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of link method, of class AbstractPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLink() throws Exception {
        System.out.println("link");
        ScriptParser p = null;
        instance.link(p);
    }

    /**
     * Test of getName method, of class AbstractPassFailCheck.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "actionok";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of passed method, of class AbstractPassFailCheck.
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
        Script s = null;
        InputStream in = null;
        InputStream inRequest = null;
        TestResult expResult = null;
        TestResult result = instance.passed(s, in, inRequest);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescription method, of class AbstractPassFailCheck.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        String expResult = "stuff";
        instance.setDescription(expResult);
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of doExtract method, of class AbstractPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoExtract() throws Exception {
        System.out.println("doExtract");
        String s = "";
        instance.doExtract(s);
    }

    /**
     * Test of colourString method, of class AbstractPassFailCheck.
     */
    @Test
    public void testColourString() {
        System.out.println("colourString");
        String content = "stuff";
        String clr = "green";
        String expResult = "<font color=\"green\">stuff</font>";
        String result = instance.colourString(content, clr);
        assertEquals(expResult, result);
    }

    /**
     * Test of setDescription method, of class AbstractPassFailCheck.
     */
    @Test
    public void testSetDescription() {
        System.out.println("setDescription");
        String description = "";
        instance.setDescription(description);
    }

    /**
     * Test of appendDescription method, of class AbstractPassFailCheck.
     */
    @Test
    public void testAppendDescription() {
        System.out.println("appendDescription");
        String s = "b";
        instance.setDescription("a");
        instance.appendDescription(s);
        String expResult = "ab";
        String result = instance.getDescription();
        assertEquals(expResult, result);

    }

    public class AbstractPassFailCheckImpl extends AbstractPassFailCheck {

        @Override
        public TestResult passed(Script s, InputStream in, InputStream inRequest) throws Exception {
            return null;
        }
    }

    /**
     * Test of init method, of class AbstractPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit_AutotestParserPassfailContext() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of init method, of class AbstractPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit_AutotestParserPassFailCheckContext() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of isNullOrEmpty method, of class AbstractPassFailCheck.
     */
    @Test
    public void testIsNullOrEmpty() {
        System.out.println("isNullOrEmpty");
        String st = "";
        boolean expResult = true;
        boolean result = Utils.isNullOrEmpty(st);
        assertEquals(expResult, result);
    }

    /**
     * Test of getExtractorName method, of class AbstractPassFailCheck.
     */
    @Test
    public void testGetExtractorName() {
        System.out.println("getExtractorName");
        String expResult = null;
        String result = instance.getExtractorName();
        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class AbstractPassFailCheck.
     * @throws java.lang.Exception
     * This is tested in SingleRecordXpathResponseExtractorTest 

     */
    @Test
    public void testExtract() throws Exception {
        System.out.println("extract");
    }

    /**
     * Test of setDatasourceDetails method, of class AbstractPassFailCheck.
     */
    @Test
    public void testSetDatasourceDetails() {
        System.out.println("setDatasourceDetails");
        DataSource datasource = null;
        String recordid = "";
        instance.setDatasourceDetails(datasource, recordid);
    }

}
