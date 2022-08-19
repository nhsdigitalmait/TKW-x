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
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_REQUEST_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import org.xml.sax.InputSource;

/**
 *
 * @author sifa2
 */
public class SynchronousXPathAssertionPassFailCheckTest {
    private static AutotestParser.PassfailContext passfailCtx;
    private SynchronousXPathAssertionPassFailCheck instance;

    public SynchronousXPathAssertionPassFailCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        AutotestParser.PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (AutotestParser.PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("matchescheck")) {
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
        instance = new SynchronousXPathAssertionPassFailCheck();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class SynchronousXPathAssertionPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of doChecks method, of class SynchronousXPathAssertionPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
        Script s = null;
        InputSource is = new InputSource(new ByteArrayInputStream("<el>xxx</el>".getBytes()));
        instance.init(passfailCtx);
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.doChecks(s, is);
        assertEquals(expResult, result);
    }

    /**
     * Test of passed method, of class SynchronousXPathAssertionPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
        Script s = null;
        InputStream in = new ByteArrayInputStream((END_REQUEST_MARKER + "\r\n\r\nHTTP/1.1 200\r\n\r\n<el>xxx</el>").getBytes());
        InputStream inSync = null;
        instance.init(passfailCtx);
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.passed(s, in, inSync);
        assertEquals(expResult, result);
    }

}
