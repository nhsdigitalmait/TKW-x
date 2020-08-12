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
package uk.nhs.digital.mait.tkwx.tk.boot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class TKWFileTypeTest {
    
    public TKWFileTypeTest() {
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
     * Test of values method, of class TKWFileType.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        int expResult = 14;
        TKWFileType[] result = TKWFileType.values();
        assertEquals(expResult, result.length);
    }

    /**
     * Test of valueOf method, of class TKWFileType.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        String name = "FHIR_MESSAGING_JSON";
        TKWFileType expResult = TKWFileType.FHIR_MESSAGING_JSON;
        TKWFileType result = TKWFileType.valueOf(name);
        assertEquals(expResult, result);
    }
    
}
