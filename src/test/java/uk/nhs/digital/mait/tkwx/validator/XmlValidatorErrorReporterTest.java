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
package uk.nhs.digital.mait.tkwx.validator;

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
public class XmlValidatorErrorReporterTest {
    
    public XmlValidatorErrorReporterTest() {
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
     * Test of setErrorDetected method, of class XmlValidatorErrorReporter.
     */
    @Test
    public void testSetErrorDetected() {
        System.out.println("setErrorDetected");
        XmlValidatorErrorReporter instance = new XmlValidatorErrorReporterImpl();
        instance.setErrorDetected();
    }

    /**
     * Test of setErrorString method, of class XmlValidatorErrorReporter.
     */
    @Test
    public void testSetErrorString() {
        System.out.println("setErrorString");
        String s = "";
        XmlValidatorErrorReporter instance = new XmlValidatorErrorReporterImpl();
        instance.setErrorString(s);
    }

    /**
     * Test of getErrorString method, of class XmlValidatorErrorReporter.
     */
    @Test
    public void testGetErrorString() {
        System.out.println("getErrorString");
        XmlValidatorErrorReporter instance = new XmlValidatorErrorReporterImpl();
        String expResult = "";
        String result = instance.getErrorString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrorDetected method, of class XmlValidatorErrorReporter.
     */
    @Test
    public void testGetErrorDetected() {
        System.out.println("getErrorDetected");
        XmlValidatorErrorReporter instance = new XmlValidatorErrorReporterImpl();
        boolean expResult = false;
        boolean result = instance.getErrorDetected();
        assertEquals(expResult, result);
    }

    public class XmlValidatorErrorReporterImpl implements XmlValidatorErrorReporter {

        public void setErrorDetected() {
        }

        public void setErrorString(String s) {
        }

        public String getErrorString() {
            return "";
        }

        public boolean getErrorDetected() {
            return false;
        }
    }
}
