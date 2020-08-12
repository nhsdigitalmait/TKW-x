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

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import javax.xml.transform.stream.StreamSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.tkwx.validator.ValidatorServiceErrorHandler;

/**
 *
 * @author sifa2
 */
public class SchemaValidatorTest {

    private SchemaValidator instance;

    public SchemaValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new SchemaValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of validate method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = new SpineMessage("src/test/resources", "ITK_Trunk.message");
        boolean expResult = true;
        instance.setType("attachment1_xxx");
        instance.setResource(System.getenv("TKWROOT") + "/contrib/DMS_Schema/ITK_Core/Schemas/distributionEnvelope-v2-0.xsd");
        ValidationReport[] result = instance.validate(o);
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of getSupportingData method, of class SchemaValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeExternalOutput method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setResource method, of class SchemaValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String s = "";
        instance.setResource(s);
    }

    /**
     * Test of setData method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of addValidationExceptionDetail method, of class SchemaValidator.
     */
    @Test
    public void testAddValidationExceptionDetail() throws IOException, Exception {
        System.out.println("addValidationExceptionDetail");
        String o = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));
        boolean stripHeader = false;

        // starting xpath
        instance.setData("");

        // schema file
        instance.setResource(System.getenv("TKWROOT") + "/contrib/DMS_Schema/GenericCDA/Schemas/POCD_MT000002UK01.xsd");

        ValidatorOutput result = instance.validate(o, null, stripHeader);
        String s = "exception detail";
        instance.addValidationExceptionDetail(s);
    }

    /**
     * Test of setType method, of class SchemaValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of initialise method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        // no op
        instance.initialise();
    }

    /**
     * Test of validate method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));
        boolean stripHeader = false;

        // starting xpath
        instance.setData("/soap:Envelope/soap:Body/itk:DistributionEnvelope/itk:payloads/itk:payload[1]/*[1]");

        // schema file
        instance.setResource(System.getenv("TKWROOT") + "/contrib/DMS_Schema/GenericCDA/Schemas/POCD_MT000002UK01.xsd");

        boolean expResult = true;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result.getReport()[0].getPassed());
    }

    /**
     * Test of doDomValidation method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoDomValidation() throws Exception {
        System.out.println("doDomValidation");
        StreamSource toValidate = new StreamSource(new FileReader(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
        StreamSource schema = new StreamSource(new FileReader(System.getenv("TKWROOT") + "/contrib/DMS_Schema/ITK_Core/Schemas/distributionEnvelope-v2-0.xsd"));
        ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(instance);
        boolean stripHeader = false;
        // starting xpath
        instance.setData("/soap:Envelope/soap:Body/itk:DistributionEnvelope");
        instance.doDomValidation(toValidate, schema, v, stripHeader);
        // this generates report files in the folder specified in properties
    }

    /**
     * Test of getValidationRoot method, of class SchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValidationRoot() throws Exception {
        System.out.println("getValidationRoot");
        // starting xpath
        //instance.setData("");

        // schema file
        instance.setResource(System.getenv("TKWROOT") + "/contrib/DMS_Schema/Ambulance/Schemas/POCD_MT030001UK01.xsd");
        StreamSource s = new StreamSource(new FileReader(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
        boolean stripHeader = false;
        String expResult = "soap:Envelope";
        Node result = instance.getValidationRoot(s, stripHeader);
        assertEquals(expResult, result.getNodeName());
    }

    /**
     * Test of setVariableProvider method, of class SchemaValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = new VariableProvider() {
            @Override
            public Object getVariable(String s) {
                return null;
            }

            @Override
            public void setVariable(String name, Object value) {
            }
        };
        instance.setVariableProvider(vp);
    }

    /**
     * Test of validate method, of class SchemaValidator.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }

}
