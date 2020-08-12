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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.DataSource;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScriptParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 *
 * @author sifa2
 */
public class PassFailCheckTest {

    private static AutotestParser.PassfailContext passfailCtx;
    private PassFailCheck instance;

    public PassFailCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        AutotestParser.PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (AutotestParser.PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("andcheck")) {
                passfailCtx = lpassfailCtx;
                break;
            }
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new PassFailCheckImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class PassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        String[] line = null;
        instance.init(passfailCtx);
    }

    /**
     * Test of getName method, of class PassFailCheck.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescription method, of class PassFailCheck.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        String expResult = "description";
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of passed method, of class PassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
        Script s = null;
        InputStream in = null;
        InputStream inRequest = null;
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.passed(s, in, inRequest);
        assertEquals(expResult, result);
    }

    /**
     * Test of init method, of class PassFailCheck.
     */
    @Test
    public void testInit_AutotestParserPassfailContext() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of init method, of class PassFailCheck.
     */
    @Test
    public void testInit_AutotestParserPassFailCheckContext() throws Exception {
        System.out.println("init");
        AutotestParser.PassFailCheckContext passfailCheckCtx = null;
        instance.init(passfailCheckCtx);
    }

    /**
     * Test of setDatasourceDetails method, of class PassFailCheck.
     */
    @Test
    public void testSetDatasourceDetails() {
        System.out.println("setDatasourceDetails");
        DataSource arg0 = null;
        String arg1 = "";
        instance.setDatasourceDetails(arg0, arg1);
    }

    /**
     * Test of getExtractorName method, of class PassFailCheck.
     */
    @Test
    public void testGetExtractorName() {
        System.out.println("getExtractorName");
        String expResult = "extractorName";
        String result = instance.getExtractorName();
        assertEquals(expResult, result);
    }

    public class PassFailCheckImpl implements PassFailCheck {

        @Override
        public String getName() {
            return "name";
        }

        @Override
        public String getDescription() {
            return "description";
        }

        @Override
        public TestResult passed(Script s, InputStream in, InputStream inRequest) throws Exception {
            return TestResult.PASS;
        }

        @Override
        public void link(ScriptParser p) throws Exception {
        }

        @Override
        public void init(AutotestParser.PassfailContext passfailCtx) throws Exception {
        }

        @Override
        public void init(AutotestParser.PassFailCheckContext passfailCheckCtx) throws Exception {
        }

        @Override
        public void setDatasourceDetails(DataSource datasource, String recordid) {
        }

        @Override
        public String getExtractorName() {
            return "extractorName";
        }
    }

}
