/*
  Copyright 2017  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.ackextension;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;

/**
 *
 * @author simonfarrow
 */
public class SendDistInfrastructureNackOnlyWithServiceTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    public SendDistInfrastructureNackOnlyWithServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
     * Test of makeResponse method, of class
     * SendDistInfrastructureNackOnlyWithService.
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String,Substitution> substitutions = new HashMap<>();
        System.setProperty(ORG_AUDIT_ID_PROPERTY, "auditid");
        System.setProperty(ORG_SENDER_PROPERTY, "senderaddress");
        System.setProperty(TXTTL_PROPERTY, "1000");

        String o = new String(Files.readAllBytes(Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));
        SendDistInfrastructureNackOnlyWithService instance = new SendDistInfrastructureNackOnlyWithService();
        String expResult = "";
        String result = instance.makeResponse(substitutions, o);
        assertEquals(expResult, result);
    }

}
