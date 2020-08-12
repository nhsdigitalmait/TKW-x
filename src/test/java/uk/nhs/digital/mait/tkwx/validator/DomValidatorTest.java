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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.SchemaValidationReporter;
import uk.nhs.digital.mait.tkwx.util.Utils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author sifa2
 */
public class DomValidatorTest {

    private InputSource message;
    private StreamSource schema;
    private DomValidator instance;
    private static final String TEMP_FILE = "src/test/resources/temp";

    public DomValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        message = new InputSource(new FileInputStream(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
        schema = new StreamSource(new FileInputStream(System.getenv("TKWROOT") + "/contrib/DMS_Schema/Ambulance/Schemas/distributionEnvelope-v2-0.xsd"));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        Document doc = dbf.newDocumentBuilder().parse(message);
        Node n = doc.getFirstChild();
        instance = new DomValidator(n, schema);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getErrorString method, of class DomValidator.
     * @throws uk.nhs.digital.mait.tkwx.validator.XmlValidatorException
     */
    @Test
    public void testGetErrorString() throws XmlValidatorException {
        System.out.println("getErrorString");
        instance.validate();
        String expResult = "ERRORReason: cvc-elt.1.a: Cannot find the declaration of element 'soap:Envelope'.";
        String result = instance.getErrorString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrorDetected method, of class DomValidator.
     * @throws uk.nhs.digital.mait.tkwx.validator.XmlValidatorException
     */
    @Test
    public void testGetErrorDetected() throws XmlValidatorException {
        System.out.println("getErrorDetected");
        boolean expResult = true;
        instance.validate();
        boolean result = instance.getErrorDetected();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class DomValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_ValidatorServiceErrorHandler() throws Exception {
        System.out.println("validate");
        ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(new SchemaValidationReporterImpl());
        instance.validate(v);
    }

    /**
     * Test of validate method, of class DomValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_Writer() throws Exception {
        System.out.println("validate");
        try (Writer out = new FileWriter(TEMP_FILE)) {
            instance.validate(out);
        }
        String result = Utils.readFile2String(TEMP_FILE);
        new File(TEMP_FILE).delete();
    }

    /**
     * Test of validate method, of class DomValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_0args() throws Exception {
        System.out.println("validate");
        boolean expResult = true;
        boolean result = instance.validate();
        assertEquals(expResult, result);
    }

    /**
     * Test of setErrorDetected method, of class DomValidator.
     */
    @Test
    public void testSetErrorDetected() {
        System.out.println("setErrorDetected");
        instance.setErrorDetected();
    }

    /**
     * Test of setErrorString method, of class DomValidator.
     */
    @Test
    public void testSetErrorString() {
        System.out.println("setErrorString");
        String s = "";
        instance.setErrorString(s);
    }

    private class SchemaValidationReporterImpl extends SchemaValidationReporter {

        @Override
        public void addValidationExceptionDetail(String s) {
            System.out.println("Adding: [" + s + "]");
        }

    }
}
