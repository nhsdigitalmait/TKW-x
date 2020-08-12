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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_REQUEST_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScriptParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailsContext;

/**
 *
 * @author sifa2
 */
public class AbstractLogicalOperatorPassFailCheckTest {
    private static PassfailContext passfailCtx;
    private static PassfailContext failpassfailCtx;

    private AndPassFailCheck instance;

    public AbstractLogicalOperatorPassFailCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("andcheck") ) {
                passfailCtx = lpassfailCtx;
            }
            if (lpassfailCtx.passFailCheckName().getText().equals("failandcheck") ) {
                failpassfailCtx = lpassfailCtx;
            }
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new AndPassFailCheck();
        instance.subTests = new PassFailCheck[]{new HttpOK(), new HttpOK()};
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class AbstractLogicalOperatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of getDescription method, of class
     * AbstractLogicalOperatorPassFailCheck.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        String expResult = "";
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }


    /**
     * Test of link method, of class AbstractLogicalOperatorPassFailCheck.
     */
    @Test
    public void testLink() {
        try {
            System.out.println("link");
            Properties props = new Properties();
            props.load(new FileInputStream(System.getenv("TKWROOT") + "/config/ITK_Autotest/tkw-x.properties"));
            ScriptParser p = new ScriptParser(props);
            instance.passfailCtx = failpassfailCtx;
            instance.link(p);
            InputStream inResponse = new ByteArrayInputStream((END_REQUEST_MARKER + "\r\nHTTP/1.1 200").getBytes());
            InputStream inRequest = null;
            TestResult expResult = TestResult.FAIL;
            TestResult result = instance.passed(null, inResponse, inRequest);
            assertEquals(expResult, result);

            instance.passfailCtx = passfailCtx;
            instance.link(p);
            inResponse = new ByteArrayInputStream((END_REQUEST_MARKER + "\r\nHTTP/1.1 200").getBytes());
            expResult = TestResult.PASS;
            result = instance.passed(null, inResponse, inRequest);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Threw exception " + ex.getMessage());
        }
    }

    /**
     * Test of passed method, of class AbstractLogicalOperatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
        Script s = null;
        InputStream inResponse = new ByteArrayInputStream((END_REQUEST_MARKER + "\r\nHTTP/1.1 200").getBytes());
        InputStream inRequest = null;
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.passed(s, inResponse, inRequest);
        assertEquals(expResult, result);
    }

    /**
     * Test of copyStreamsRunTest method, of class
     * AbstractLogicalOperatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testCopyStreamsRunTest() throws Exception {
        System.out.println("copyStreamsRunTest");
        PassFailCheck pfc = new HttpOK();
        Script s = null;
        byte[] copiedInResponse = (END_REQUEST_MARKER + "\r\nHTTP/1.1 200").getBytes();
        byte[] copiedInRequest = null;
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.copyStreamsRunTest(pfc, s, copiedInResponse, copiedInRequest);
        assertEquals(expResult, result);
    }

    /**
     * Test of processSubTests method, of class
     * AbstractLogicalOperatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessSubTests() throws Exception {
        System.out.println("processSubTests");
        Script s = null;
        byte[] inResponse = (END_REQUEST_MARKER + "\r\nHTTP/1.1 200").getBytes();
        byte[] inRequest = null;
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.processSubTests(s, inResponse, inRequest);
        assertEquals(expResult, result);
    }

    public class AbstractLogicalOperatorPassFailCheckImpl extends AbstractLogicalOperatorPassFailCheck {

        @Override
        public TestResult processSubTests(Script s, byte[] inResponse, byte[] inRequest) throws Exception {
            return null;
        }
    }
}
