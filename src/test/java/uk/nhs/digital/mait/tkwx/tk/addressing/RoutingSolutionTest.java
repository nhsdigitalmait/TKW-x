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
package uk.nhs.digital.mait.tkwx.tk.addressing;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sifa2
 */
public class RoutingSolutionTest {

    private RoutingSolution instance;

    public RoutingSolutionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new RoutingSolution();
        instance.addService("service", "addr", "url", true);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isStarService method, of class RoutingSolution.
     */
    @Test
    public void testIsStarService() {
        System.out.println("isStarService");
        boolean expResult = false;
        boolean result = instance.isStarService();
        assertEquals(expResult, result);
    }

    /**
     * Test of isAuthoritative method, of class RoutingSolution.
     */
    @Test
    public void testIsAuthoritative() throws Exception {
        System.out.println("isAuthoritative");
        boolean expResult = true;
        boolean result = instance.isAuthoritative();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSolutions method, of class RoutingSolution.
     */
    @Test
    public void testGetSolutions() throws Exception {
        System.out.println("getSolutions");
        HashMap<String, PhysicalAddress> expResult = new HashMap<>();
        expResult.put("service", new PhysicalAddress("addr", "url", true));

        HashMap<String, PhysicalAddress> result = instance.getSolutions();
        assertEquals(expResult, result);
    }

    /**
     * Test of addService method, of class RoutingSolution.
     */
    @Test
    public void testAddService() throws Exception {
        System.out.println("addService");
        String s = "service1";
        String a = "addr";
        String t = "url";
        boolean auth = false;
        instance.addService(s, a, t, auth);
    }

    /**
     * Test of getAddress method, of class RoutingSolution.
     */
    @Test
    public void testGetAddress_0args() throws Exception {
        System.out.println("getAddress");
        PhysicalAddress expResult = new PhysicalAddress("addr", "url", true);
        // requires a wildcard service to work
        instance.addService("*", "addr", "url", true);
        PhysicalAddress result = instance.getAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddress method, of class RoutingSolution.
     */
    @Test
    public void testGetAddress_String() throws Exception {
        System.out.println("getAddress");
        String s = "service";
        PhysicalAddress expResult = new PhysicalAddress("addr", "url", true);
        PhysicalAddress result = instance.getAddress(s);
        assertEquals(expResult, result);
    }
}
