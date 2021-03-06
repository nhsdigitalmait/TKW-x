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
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_REQUEST_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult.FAIL;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult.PASS;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 *
 * @author simonfarrow
 */
public class NullResponseTest {

    private static AutotestParser.PassfailContext passfailCheckCtx;
    private NullResponse instance;

    public NullResponseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        AutotestParser.PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (AutotestParser.PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("nullresp")) {
                passfailCheckCtx = lpassfailCtx;
                break;
            }
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new NullResponse();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class NullResponse.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(passfailCheckCtx);
    }

    /**
     * Test of passed method, of class NullResponse.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
        Script s = null;
        instance.init(passfailCheckCtx);
        InputStream in = new ByteArrayInputStream((END_REQUEST_MARKER+"\r\nFailed").getBytes());
        InputStream inRequest = null;
        TestResult expResult = PASS;
        TestResult result = instance.passed(s, in, inRequest);
        assertEquals(expResult, result);

        in = new ByteArrayInputStream((END_REQUEST_MARKER+"\r\nHTTP/1.1 202\r\nContent-Length: 1\r\n\r\nx").getBytes());
        expResult = FAIL;
        result = instance.passed(s, in, inRequest);
        assertEquals(expResult, result);

    }

}
