/*
  Copyright 2012  Damian Murphy murff@warlock.org

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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.HttpRoutingHelper.MAXACKDELAY;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.HttpRoutingHelper.MAXRESPDELAY;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.HttpRoutingHelper.MINACKDELAY;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.HttpRoutingHelper.MINRESPDELAY;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class HttpRoutingHelperTest {

    private HttpRoutingHelper instance;
    
    public HttpRoutingHelperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    System.setProperty(MINACKDELAY, "10");
    System.setProperty(MAXACKDELAY, "11");
    System.setProperty(MINRESPDELAY, "100");
    System.setProperty(MAXRESPDELAY, "101");

    //public static final String INFRACKDELIVERYURL = "tks.routingactor.infrastructure.forcedeliveryurl";

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        instance = new HttpRoutingHelper();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDelay method, of class HttpRoutingHelper.
     */
    @Test
    public void testGetDelay() {
        System.out.println("getDelay");
        int min = 10;
        int max = 100;
        int result = instance.getDelay(min, max);
        assertTrue(min <= result && result <= max);
    }

    /**
     * Test of getAckDelay method, of class HttpRoutingHelper.
     */
    @Test
    public void testGetAckDelay() {
        System.out.println("getAckDelay");
        int expResult = 10;
        int result = instance.getAckDelay();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResponseDelay method, of class HttpRoutingHelper.
     */
    @Test
    public void testGetResponseDelay() {
        System.out.println("getResponseDelay");
        int expResult = 100;
        int result = instance.getResponseDelay();
        assertEquals(expResult, result);
    }
    
}
