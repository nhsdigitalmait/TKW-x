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
import static uk.nhs.digital.mait.commonutils.util.CfHNamespaceContext.HL7NAMESPACE;
import static uk.nhs.digital.mait.commonutils.util.CfHNamespaceContext.ITK;
import org.xml.sax.InputSource;

/**
 *
 * @author sifa2
 */
public class SecondAsynchronousResponseAcknowledgedBy3PassFailCheckTest {

    private static AutotestParser.PassfailContext passfailCtx;

    public SecondAsynchronousResponseAcknowledgedBy3PassFailCheckTest() {
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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class
     * SecondAsynchronousResponseAcknowledgedBy3PassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        SecondAsynchronousResponseAcknowledgedBy3PassFailCheck instance = new SecondAsynchronousResponseAcknowledgedBy3PassFailCheck();
        instance.init(passfailCtx);
    }

    /**
     * Test of doChecks method, of class
     * SecondAsynchronousResponseAcknowledgedBy3PassFailCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
        Script s = null;
        InputSource request = new InputSource(new ByteArrayInputStream(setTrackingID("ID").getBytes()));
        InputSource response = new InputSource(new ByteArrayInputStream(setAckBy3("ID").getBytes()));
        SecondAsynchronousResponseAcknowledgedBy3PassFailCheck instance = new SecondAsynchronousResponseAcknowledgedBy3PassFailCheck();
        boolean expResult = true;
        instance.init(passfailCtx);
        boolean result = instance.doChecks(s, request, response);
        assertEquals(expResult, result);
    }

    /**
     * Construct a minimal xml tree containing a DE tracking ID
     *
     * @param id
     * @return
     */
    public static String setTrackingID(String id) {
        //itk:DistributionEnvelope/itk:header/@trackingid"
        return "<itk:DistributionEnvelope xmlns:itk=\"" + ITK + "\">"
                + "<itk:header trackingid=\"" + id + "\"/>"
                + "</itk:DistributionEnvelope>";

    }

    /**
     * Construct a minimal xml tree containing a HL7 acknowledgedBy3 element
     * containing an id
     *
     * @param id
     * @return
     */
    private String setAckBy3(String id) {
        //itk:DistributionEnvelope/itk:payloads/itk:payload[1]/hl7:BusinessResponseMessage/hl7:acknowledgedBy3/hl7:conveyingTransmission/hl7:id/@root"
        return "<itk:DistributionEnvelope xmlns:itk=\"" + ITK + "\"><itk:payloads>"
                + "<itk:payload>"
                + "<hl7:BusinessResponseMessage xmlns:hl7=\"" + HL7NAMESPACE + "\">"
                + "<hl7:acknowledgedBy3>"
                + "<hl7:conveyingTransmission>"
                + "<hl7:id root=\"" + id + "\"/>"
                + "</hl7:conveyingTransmission>"
                + "</hl7:acknowledgedBy3>"
                + "</hl7:BusinessResponseMessage>"
                + "</itk:payload>"
                + "</itk:payloads></itk:DistributionEnvelope>";

    }

}
