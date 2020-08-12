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
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SIMPLE_ITK_OK_RESPONSE;

/**
 *
 * @author sifa2
 */
public class CDARoutingActorTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private CDARoutingActor instance;

    public CDARoutingActorTest() {
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
        System.setProperty(TXTTL_PROPERTY, "1000");

        instance = new CDARoutingActor();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class CDARoutingActor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init();
    }

    /**
     * Test of makeResponse method, of class CDARoutingActor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String,Substitution> substitutions = new HashMap<>();
        String path = System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml";
        String o = new String(Files.readAllBytes(Paths.get(path)));
        String expResult = SIMPLE_ITK_OK_RESPONSE;
        String result = instance.makeResponse(substitutions, o);
        assertEquals(expResult, result);
    }

    /**
     * Test of forceClose method, of class CDARoutingActor.
     */
    @Test
    public void testForceClose() {
        System.out.println("forceClose");
        Boolean expResult = false;
        Boolean result = instance.forceClose();
        assertEquals(expResult, result);
    }

}
