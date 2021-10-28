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

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class RoutingActorTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private RoutingActorImpl instance;

    public RoutingActorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new RoutingActorImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class RoutingActor.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init();
    }


    /**
     * Test of loadTemplate method, of class RoutingActor.
     */
    @Test
    public void testLoadTemplate_String() throws Exception {
        System.out.println("loadTemplate");
        System.setProperty("testprop", System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml");
        String pname = "testprop";
        String expResult = "<?xml";
        String result = instance.loadTemplate(pname);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getDelay method, of class RoutingActor.
     */
    @Test
    public void testGetDelay() {
        System.out.println("getDelay");
        int min = 0;
        int max = 0;
        int expResult = 0;
        //int result = instance.getDelay(min, max);
        //assertEquals(expResult, result);
    }

    /**
     * Test of getAckDelay method, of class RoutingActor.
     */
    @Test
    public void testGetAckDelay() {
        System.out.println("getAckDelay");
        int expResult = 0;
        //int result = instance.getAckDelay();
        //assertEquals(expResult, result);
    }

    /**
     * Test of getResponseDelay method, of class RoutingActor.
     */
    @Test
    public void testGetResponseDelay() {
        System.out.println("getResponseDelay");
        int expResult = 0;
        //int result = instance.getResponseDelay();
        //assertEquals(expResult, result);
    }

    /**
     * Test of makeResponse method, of class RoutingActor.
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String,Substitution> substitutions = new HashMap<>();
        String o = "";
        String expResult = "";
        String result = instance.makeResponse(substitutions, o);
        assertEquals(expResult, result);
    }

    /**
     * Test of forceClose method, of class RoutingActor.
     */
    @Test
    public void testForceClose() {
        System.out.println("forceClose");
        Boolean expResult = false;
        Boolean result = instance.forceClose();
        assertEquals(expResult, result);
    }

    /**
     * Test of performSimulatorConfigSubstitutions method, of class RoutingActor.
     * @throws java.lang.Exception
     */
    @Test
    public void testPerformSimulatorConfigSubstitutions() throws Exception {
        System.out.println("performSimulatorConfigSubstitutions");
        String template = "a __TAG__ b"; // templated response
        HashMap<String,Substitution> substitutions = new HashMap<>();
        String o = ""; // message from which to derive data
        String expResult = "a __TAG__ b";
        String result = instance.performSimulatorConfigSubstitutions(template, substitutions, o);
        assertEquals(expResult, result);
    }

    public class RoutingActorImpl extends RoutingActor {

        @Override
        public String makeResponse(HashMap<String,Substitution> substitutions, Object o) throws Exception {
            System.out.println("Routing actor impl makeResponse called with "+substitutions.size()+" substitutions to object of "+o.getClass());
            return "";
        }
    }

    /**
     * Test of loadTemplate method, of class RoutingActor.
     * see testLoadTemplate_String
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadTemplate() throws Exception {
        System.out.println("loadTemplate");
    }

    /**
     * Test of getBody method, of class RoutingActor.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        Object obj = "body";
        String expResult = "body";
        String result = instance.getBody(obj);
        assertEquals(expResult, result);
    }
}
