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
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;

/**
 *
 * @author SIFA2
 */
public class BusAckDistributionEnvelopeHandlerTest {
    
    private final static File logfile = new File("urn_nhs-itk_services_201005_SendCDADocument-v2-0_D7CB2376-F716-4145-B038-E492780D3BE3.message");
    
    public BusAckDistributionEnvelopeHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
         logfile.delete();
    }
    
    @After
    public void tearDown() {
         logfile.delete();
    }

    /**
     * Test of handle method, of class BusAckDistributionEnvelopeHandler.
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
       
        String destring = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT")+"/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml")));
        DistributionEnvelope de =  DistributionEnvelopeHelper.getInstance().getDistributionEnvelope(destring);

        BusAckDistributionEnvelopeHandler instance = new BusAckDistributionEnvelopeHandler();
        instance.handle(de);
        assertTrue(logfile.exists());
    }
    
}
