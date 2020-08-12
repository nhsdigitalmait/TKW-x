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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Payload;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;

/**
 *
 * @author sifa2
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
    public void setUp() throws Exception {
        System.setProperty(ORG_AUDIT_ID_PROPERTY, "auditidentity");
        System.setProperty(ORG_SENDER_PROPERTY, "senderaddress");

        String path = System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml";
        DistributionEnvelope de = DistributionEnvelopeHelper.getInstance().getDistributionEnvelope(new String(Files.readAllBytes(Paths.get(path))));
        Payload payload = new Payload("mime_type");
        de.addPayload(payload);
        instance = new ApplicationAcknowledgment(de);
    }

    @After
    public void tearDown() {
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
    }
}
