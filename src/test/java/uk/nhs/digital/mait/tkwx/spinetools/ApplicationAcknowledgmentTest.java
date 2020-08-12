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

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Address;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Payload;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author SIFA2
 */
public class ApplicationAcknowledgmentTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();
    private ApplicationAcknowledgment instance;

    public ApplicationAcknowledgmentTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException, Exception {
        System.setProperty(ORG_SENDER_PROPERTY, "senderaddress");
        System.setProperty(ORG_AUDIT_ID_PROPERTY, "auditidentity");

        DistributionEnvelope de = DistributionEnvelope.newInstance();
        de.setInteractionId("id");
        de.addSender("2.16.840.1.113883.2.1.3.2.4.18.22", "senderid");
        Payload payload = new Payload("text/xml");
        payload.setContent("Content");
        payload.setProfileId("profileid");
        de.addPayload(payload);

        instance = new ApplicationAcknowledgment(de);
        Utils.writeString2File("test.txt", "123456test\r\n123456test\r\n123456test\r\n");
    }

    @After
    public void tearDown() {
        new File("test.txt").delete();
    }

    /**
     * Test of makeResponse method, of class ApplicationAcknowledgment.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        instance.makeResponse();

        String expResult = "uuid_";
        String result = instance.getPayloadId(0);
        assertTrue(result.startsWith(expResult));

        expResult = ITK_BUSINESS_ACK_INTERACTION;
        result = instance.getInteractionId();
        assertEquals(expResult, result);

        expResult = BIZACKSERVICE;
        result = instance.getService();
        assertEquals(expResult, result);

        expResult = "^[A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12}$";
        result = instance.getTrackingId();
        assertTrue(result.matches(expResult));

        Address address = instance.getSender();

        result = address.getUri();
        expResult = "senderaddress";
        assertEquals(expResult, result);

        result = address.getOID();
        expResult = "2.16.840.1.113883.2.1.3.2.4.18.22";
        assertEquals(expResult, result);
    }

}
