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
import uk.nhs.digital.mait.tkwx.tk.internalservices.ConditionalCompilationControls;

/**
 *
 * @author sifa2
 */
public class HttpZeroContentLengthTest {

    private HttpZeroContentLength instance;

    public HttpZeroContentLengthTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new HttpZeroContentLength();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of passed method, of class HttpZeroContentLength. The passfail check
     * has been amended to check the size of the body not the value of
     * Content-Length which is only available for non chunked responses
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
        Script s = null;
        InputStream inRequest = null;

        TestResult expResult = TestResult.PASS;
        InputStream in = new ByteArrayInputStream((PREAMBLE + "Content-Length: 0\r\n\r\n").getBytes());
        TestResult result = instance.passed(s, in, inRequest);
        assertEquals(expResult, result);

        if (ConditionalCompilationControls.LOG_RAW_RESPONSE) {
            in = new ByteArrayInputStream((PREAMBLE + "Transfer-Encoding: chunked\r\n\r\n0\r\n\r\n").getBytes());
            result = instance.passed(s, in, inRequest);
            assertEquals(expResult, result);
        }

        expResult = TestResult.FAIL;
        in = new ByteArrayInputStream((PREAMBLE + "Content-Length: 5\r\n\r\nhello").getBytes());
        result = instance.passed(s, in, inRequest);
        assertEquals(expResult, result);

        if (ConditionalCompilationControls.LOG_RAW_RESPONSE) {
            in = new ByteArrayInputStream((PREAMBLE + "Transfer-Encoding: chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n").getBytes());
            result = instance.passed(s, in, inRequest);
            assertEquals(expResult, result);
        }
    }

    private static final String PREAMBLE = "POST / HTTP/1.1\r\n" + END_REQUEST_MARKER + "\r\nHTTP/1.1 200\r\n";

}
