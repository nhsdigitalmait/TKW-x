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
public class PhysicalAddressTest {

    private PhysicalAddress instance;

    public PhysicalAddressTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new PhysicalAddress("http://localhost:4000", "url", true);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setStarService method, of class PhysicalAddress.
     */
    @Test
    public void testSetStarService() {
        System.out.println("setStarService");
        instance.setStarService();
    }

    /**
     * Test of isAuthoritative method, of class PhysicalAddress.
     */
    @Test
    public void testIsAuthoritative() {
        System.out.println("isAuthoritative");
        boolean expResult = true;
        boolean result = instance.isAuthoritative();
        assertEquals(expResult, result);
    }

    /**
     * Test of isStarService method, of class PhysicalAddress.
     */
    @Test
    public void testIsStarService() {
        System.out.println("isStarService");
        boolean expResult = false;
        boolean result = instance.isStarService();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddress method, of class PhysicalAddress.
     */
    @Test
    public void testGetAddress() {
        System.out.println("getAddress");
        String expResult = "http://localhost:4000";
        String result = instance.getAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class PhysicalAddress.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        int expResult = 2;
        int result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class PhysicalAddress.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = 1409;
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class PhysicalAddress.
     */
    @Test
    public void testEquals() throws Exception {
        System.out.println("equals");
        Object o = new PhysicalAddress("http://localhost:4000", "url", true);
        boolean expResult = true;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

}
