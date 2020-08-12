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
package uk.nhs.digital.mait.tkwx.spinetools;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSSpineEndpointResolver;
import uk.nhs.digital.mait.spinetools.spine.connection.SdsTransmissionDetails;
import uk.nhs.digital.mait.spinetools.spine.messaging.Sendable;
import uk.nhs.digital.mait.spinetools.spine.messaging.SpineHL7Message;
import uk.nhs.digital.mait.spinetools.spine.messaging.SpineSOAPRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.ITK_TRUNK_INTERACTION;

/**
 *
 * @author SIFA2
 */
public class LoggingSessionCaptorTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    public LoggingSessionCaptorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("uk.nhs.digital.mait.spinetools.spine.sds.cachedir", System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/transmitter_source/cache");
        System.setProperty("uk.nhs.digital.mait.spinetools.spine.sds.myasid", "SIAB-001");
        System.setProperty("uk.nhs.digital.mait.spinetools.spine.sds.mypartykey", "YEA-801248");
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
     * Test of capture method, of class LoggingSessionCaptor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCapture() throws Exception {
        System.out.println("capture");

        SDSSpineEndpointResolver resolver = new SDSSpineEndpointResolver();
        String odsCode = "YEA";
        ArrayList<SdsTransmissionDetails> details = resolver.getTransmissionDetails(ITK_TRUNK_INTERACTION, odsCode, null, null);
        SdsTransmissionDetails std = details.get(0);

        SpineHL7Message m = new SpineHL7Message("id", "");
        Sendable s = new SpineSOAPRequest(std, m);
        s.setOnTheWireRequest("request".getBytes());
        s.setOnTheWireResponse("response".getBytes());
        LoggingSessionCaptor instance = new LoggingSessionCaptor();
        instance.capture(s);
    }

}
