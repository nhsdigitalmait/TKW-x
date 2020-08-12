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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.SecondAsynchronousResponseAcknowledgedBy3PassFailCheckTest.setTrackingID;
import org.xml.sax.InputSource;

/**
 *
 * @author sifa2
 */
public class SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheckTest {

    private static AutotestParser.PassfailContext passfailCtx;
    private SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck instance;

    public SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheckTest() {
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
        instance = new SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class
     * SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        String[] line = {"", ""};
        instance.init(passfailCtx);
    }

    /**
     * Test of doChecks method, of class
     * SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
        Script s = null;
        InputSource request = new InputSource(new ByteArrayInputStream(setTrackingID("ID1").getBytes()));
        InputSource response = new InputSource(new ByteArrayInputStream(setTrackingID("ID2").getBytes()));
        SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck instance = new SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck();
        boolean expResult = true;
        instance.init(passfailCtx);
        boolean result = instance.doChecks(s, request, response);
        assertEquals(expResult, result);
    }

}
