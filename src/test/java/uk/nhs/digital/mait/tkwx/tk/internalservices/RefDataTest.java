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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

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
public class RefDataTest {

    private RefData instance;
    
    public RefDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new RefData();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getArbitaryId method, of class RefData.
     */
    @Test
    public void testGetArbitaryId() {
        System.out.println("getArbitaryId");
        int expResult = 99;
        instance.setArbitaryId(expResult);
        int result = instance.getArbitaryId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setArbitaryId method, of class RefData.
     */
    @Test
    public void testSetArbitaryId() {
        System.out.println("setArbitaryId");
        int arbitaryId = 0;
        instance.setArbitaryId(arbitaryId);
    }

    /**
     * Test of isB64 method, of class RefData.
     */
    @Test
    public void testIsB64() {
        System.out.println("isB64");
        boolean expResult = false;
        boolean result = instance.isB64();
        assertEquals(expResult, result);
    }

    /**
     * Test of setB64 method, of class RefData.
     */
    @Test
    public void testSetB64() {
        System.out.println("setB64");
        boolean b64 = false;
        instance.setB64(b64);
    }

    /**
     * Test of isCompress method, of class RefData.
     */
    @Test
    public void testIsCompress() {
        System.out.println("isCompress");
        boolean expResult = false;
        boolean result = instance.isCompress();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCompress method, of class RefData.
     */
    @Test
    public void testSetCompress() {
        System.out.println("setCompress");
        boolean compress = false;
        instance.setCompress(compress);
    }

    /**
     * Test of isSign method, of class RefData.
     */
    @Test
    public void testIsSign() {
        System.out.println("isSign");
        boolean expResult = false;
        boolean result = instance.isSign();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSign method, of class RefData.
     */
    @Test
    public void testSetSign() {
        System.out.println("setSign");
        boolean sign = false;
        instance.setSign(sign);
    }

    /**
     * Test of getAlias method, of class RefData.
     */
    @Test
    public void testGetAlias() {
        System.out.println("getAlias");
        String expResult = "testalias";
        instance.setAlias(expResult);
        String result = instance.getAlias();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAlias method, of class RefData.
     */
    @Test
    public void testSetAlias() {
        System.out.println("setAlias");
        String alias = "";
        instance.setAlias(alias);
    }
    
}
