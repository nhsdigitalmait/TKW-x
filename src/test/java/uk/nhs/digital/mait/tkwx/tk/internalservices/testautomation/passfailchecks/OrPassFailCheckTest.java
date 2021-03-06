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

/**
 *
 * @author sifa2
 */
public class OrPassFailCheckTest {

    private static AutotestParser.PassfailContext passfailCtx;

    public OrPassFailCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        AutotestParser.PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (AutotestParser.PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("orcheck")) {
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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of processSubTests method, of class OrPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessSubTests() throws Exception {
        System.out.println("processSubTests");
        Script s = null;

        byte[] copiedInResponse = (END_REQUEST_MARKER + "\r\nHTTP/1.1 200").getBytes();
        byte[] copiedInRequest = null;
        OrPassFailCheck instance = new OrPassFailCheck();
        instance.init(passfailCtx);

        instance.subTests = new PassFailCheck[]{new HttpOK(), new HttpOK()};
        TestResult expResult = TestResult.PASS;
        TestResult result = instance.processSubTests(s, copiedInResponse, copiedInRequest);
        assertEquals(expResult, result);

        instance.subTests = new PassFailCheck[]{new HttpOK(), new Http500()};
        result = instance.processSubTests(s, copiedInResponse, copiedInRequest);
        assertEquals(expResult, result);

        instance.subTests = new PassFailCheck[]{new Http500(), new HttpOK()};
        result = instance.processSubTests(s, copiedInResponse, copiedInRequest);
        assertEquals(expResult, result);

        instance.subTests = new PassFailCheck[]{new Http500(), new Http500()};
        expResult = TestResult.FAIL;
        result = instance.processSubTests(s, copiedInResponse, copiedInRequest);
        assertEquals(expResult, result);
    }

}
