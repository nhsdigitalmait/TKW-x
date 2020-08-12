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
public class ReportItemTest {
    private ReportItem instance;
    
    public ReportItemTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ReportItem("","","");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setLogFile method, of class ReportItem.
     */
    @Test
    public void testSetLogFile() {
        System.out.println("setLogFile");
        String f = "";
        instance.setLogFile(f);
    }

    /**
     * Test of addDetail method, of class ReportItem.
     */
    @Test
    public void testAddDetail() {
        System.out.println("addDetail");
        String s = "";
        instance.addDetail(s);
    }

    /**
     * Test of getLogFile method, of class ReportItem.
     */
    @Test
    public void testGetLogFile() {
        System.out.println("getLogFile");
        String expResult = "logfile";
        instance.setLogFile(expResult);
        String result = instance.getLogFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTime method, of class ReportItem.
     */
    @Test
    public void testGetTime() {
        System.out.println("getTime");
        String expResult = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]+$";
        String result = instance.getTime();
        assertTrue(result.matches(expResult));
    }

    /**
     * Test of getSchedule method, of class ReportItem.
     */
    @Test
    public void testGetSchedule() {
        System.out.println("getSchedule");
        String expResult = "";
        String result = instance.getSchedule();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTest method, of class ReportItem.
     */
    @Test
    public void testGetTest() {
        System.out.println("getTest");
        String expResult = "";
        String result = instance.getTest();
        assertEquals(expResult, result);
    }

    /**
     * Test of getComment method, of class ReportItem.
     */
    @Test
    public void testGetComment() {
        System.out.println("getComment");
        String expResult = "";
        String result = instance.getComment();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDetail method, of class ReportItem.
     */
    @Test
    public void testGetDetail() {
        System.out.println("getDetail");
        String expResult = "detail";
        instance.addDetail(expResult);
        String result = instance.getDetail();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPassed method, of class ReportItem.
     */
    @Test
    public void testGetPassed() {
        System.out.println("getPassed");
        TestResult expResult = null;
        TestResult result = instance.getPassed();
        assertEquals(expResult, result);
    }

    /**
     * Test of passedPresent method, of class ReportItem.
     */
    @Test
    public void testPassedPresent() {
        System.out.println("passedPresent");
        boolean expResult = false;
        boolean result = instance.passedPresent();
        assertEquals(expResult, result);
    }

    /**
     * Test of passed method, of class ReportItem.
     */
    @Test
    public void testPassed() {
        System.out.println("passed");
        boolean expResult = false;
        boolean result = instance.passed();
        assertEquals(expResult, result);
    }
    
}
