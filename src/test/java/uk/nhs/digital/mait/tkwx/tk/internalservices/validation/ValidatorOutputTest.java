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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

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
public class ValidatorOutputTest {

    private ValidatorOutput instance;
    
    public ValidatorOutputTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        ValidationReport[] vrs = new ValidationReport[1];
        vrs[0] = new ValidationReport("report");
        instance = new ValidatorOutput("output", vrs);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getReport method, of class ValidatorOutput.
     */
    @Test
    public void testGetReport() {
        System.out.println("getReport");
        int expResult = 1;
        ValidationReport[] result = instance.getReport();
        assertEquals(expResult, result.length);
    }

    /**
     * Test of getOutput method, of class ValidatorOutput.
     */
    @Test
    public void testGetOutput() {
        System.out.println("getOutput");
        String expResult = "output";
        String result = instance.getOutput();
        assertEquals(expResult, result);
    }
    
}
