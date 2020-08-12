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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.stream.StreamSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.SchemaValidationReporter;
import org.xml.sax.InputSource;

/**
 *
 * @author sifa2
 */
public class SaxValidatorTest {

    private InputSource message;
    private StreamSource schema;
    private SaxValidator instance;

    public SaxValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws FileNotFoundException {
        message = new InputSource(new FileInputStream(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
        schema = new StreamSource(new FileInputStream(System.getenv("TKWROOT") + "/contrib/DMS_Schema/Ambulance/Schemas/distributionEnvelope-v2-0.xsd"));
        instance = new SaxValidator(message, schema);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getErrorString method, of class SaxValidator.
     */
    @Test
    public void testGetErrorString() {
        System.out.println("getErrorString");
        String expResult = null;
        String result = instance.getErrorString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrorDetected method, of class SaxValidator.
     */
    @Test
    public void testGetErrorDetected() {
        System.out.println("getErrorDetected");
        boolean expResult = false;
        boolean result = instance.getErrorDetected();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class SaxValidator.
     */
    @Test
    public void testValidate_ValidatorServiceErrorHandler() throws Exception {
        System.out.println("validate");
        ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(new SchemaValidationReporterImpl());
        instance.validate(v);
    }

    /**
     * Test of validate method, of class SaxValidator.
     */
    @Test
    public void testValidate_Writer() throws Exception {
        System.out.println("validate");
        Writer out = new StringWriter();
        instance.validate(out);
    }

    /**
     * Test of validate method, of class SaxValidator.
     */
    @Test
    public void testValidate_0args() throws Exception {
        System.out.println("validate");
        boolean expResult = true;
        boolean result = instance.validate();
        assertEquals(expResult, result);
    }

    /**
     * Test of setErrorDetected method, of class SaxValidator.
     */
    @Test
    public void testSetErrorDetected() {
        System.out.println("setErrorDetected");
        instance.setErrorDetected();
    }

    /**
     * Test of setErrorString method, of class SaxValidator.
     */
    @Test
    public void testSetErrorString() {
        System.out.println("setErrorString");
        String s = "";
        instance.setErrorString(s);
    }

    public class SchemaValidationReporterImpl extends SchemaValidationReporter {

        @Override
        public void addValidationExceptionDetail(String s) {
            System.out.println("Adding: [" + s + "]");
        }

    }
}
