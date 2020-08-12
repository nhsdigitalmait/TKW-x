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
public class ValidationReportTest {

    private ValidationReport instance;
    
    public ValidationReportTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ValidationReport("vr");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setValidationCheck method, of class ValidationReport.
     */
    @Test
    public void testSetValidationCheck() {
        System.out.println("setValidationCheck");
        ValidationCheck v = null;
        instance.setValidationCheck(v);
    }

    /**
     * Test of writeExternalValidation method, of class ValidationReport.
     */
    @Test
    public void testWriteExternalValidation() throws Exception {
        System.out.println("writeExternalValidation");
        String reportDirectory = "";
        instance.writeExternalValidation(reportDirectory);
    }

    /**
     * Test of setPassed method, of class ValidationReport.
     */
    @Test
    public void testSetPassed() {
        System.out.println("setPassed");
        instance.setPassed();
    }

    /**
     * Test of getPassed method, of class ValidationReport.
     */
    @Test
    public void testGetPassed() {
        System.out.println("getPassed");
        boolean expResult = false;
        boolean result = instance.getPassed();
        assertEquals(expResult, result);
    }

    /**
     * Test of setServiceName method, of class ValidationReport.
     */
    @Test
    public void testSetServiceName() {
        System.out.println("setServiceName");
        String s = "";
        instance.setServiceName(s);
    }

    /**
     * Test of setTest method, of class ValidationReport.
     */
    @Test
    public void testSetTest() {
        System.out.println("setTest");
        String t = "";
        instance.setTest(t);
    }

    /**
     * Test of setFilename method, of class ValidationReport.
     */
    @Test
    public void testSetFilename() {
        System.out.println("setFilename");
        String f = "";
        instance.setFilename(f);
    }

    /**
     * Test of setAnnotation method, of class ValidationReport.
     */
    @Test
    public void testSetAnnotation() {
        System.out.println("setAnnotation");
        String a = "";
        instance.setAnnotation(a);
    }

    /**
     * Test of setIteration method, of class ValidationReport.
     */
    @Test
    public void testSetIteration() {
        System.out.println("setIteration");
        int i = 0;
        instance.setIteration(i);
    }

    /**
     * Test of setContext method, of class ValidationReport.
     */
    @Test
    public void testSetContext() {
        System.out.println("setContext");
        String c = "";
        instance.setContext(c);
    }

    /**
     * Test of getIteration method, of class ValidationReport.
     */
    @Test
    public void testGetIteration() {
        System.out.println("getIteration");
        int expResult = 0;
        instance.setIteration(expResult);
        int result = instance.getIteration();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContext method, of class ValidationReport.
     */
    @Test
    public void testGetContext() {
        System.out.println("getContext");
        String expResult = "context";
        instance.setContext(expResult);
        String result = instance.getContext();
        assertEquals(expResult, result);
    }

    /**
     * Test of getServiceName method, of class ValidationReport.
     */
    @Test
    public void testGetServiceName() {
        System.out.println("getServiceName");
        String expResult = "sn";
        instance.setServiceName(expResult);
        String result = instance.getServiceName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDetail method, of class ValidationReport.
     */
    @Test
    public void testGetDetail() {
        System.out.println("getDetail");
        String expResult = "vr";
        String result = instance.getDetail();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTestDetails method, of class ValidationReport.
     */
    @Test
    public void testGetTestDetails() {
        System.out.println("getTestDetails");
        String expResult = "details";
        instance.setTest(expResult);
        String result = instance.getTestDetails();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFilename method, of class ValidationReport.
     */
    @Test
    public void testGetFilename() {
        System.out.println("getFilename");
        String expResult = "fn";
        instance.setFilename(expResult);
        String result = instance.getFilename();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAnnotation method, of class ValidationReport.
     */
    @Test
    public void testGetAnnotation() {
        System.out.println("getAnnotation");
        String expResult = "annotation";
        instance.setAnnotation(expResult);
        String result = instance.getAnnotation();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasAnnotation method, of class ValidationReport.
     */
    @Test
    public void testHasAnnotation() {
        System.out.println("hasAnnotation");
        boolean expResult = false;
        boolean result = instance.hasAnnotation();
        assertEquals(expResult, result);
    }
    
}
