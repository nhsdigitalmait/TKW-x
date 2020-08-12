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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

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
public class TestResultTest {
    
    public TestResultTest() {
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
     * Test of values method, of class TestResult.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        TestResult[] result = TestResult.values();
        assertTrue(result.length==3);
    }

    /**
     * Test of valueOf method, of class TestResult.
     */
    @Test
    public void testValueOf_String() {
        System.out.println("valueOf");
        String name = "FAIL";
        TestResult expResult = TestResult.FAIL;
        TestResult result = TestResult.valueOf(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of valueOf method, of class TestResult.
     */
    @Test
    public void testValueOf_boolean() {
        System.out.println("valueOf");
        boolean b = false;
        TestResult expResult = TestResult.FAIL;
        TestResult result = TestResult.valueOf(b);
        assertEquals(expResult, result);
    }
    
}
