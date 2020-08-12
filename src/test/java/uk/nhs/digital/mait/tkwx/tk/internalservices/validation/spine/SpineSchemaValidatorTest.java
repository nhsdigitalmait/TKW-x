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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine;

import java.io.File;
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
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorOutput;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;
import uk.nhs.digital.mait.tkwx.validator.ValidatorServiceErrorHandler;

/**
 *
 * @author sifa2
 */
public class SpineSchemaValidatorTest {

    private SpineSchemaValidator instance;

    public SpineSchemaValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new SpineSchemaValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of writeExternalOutput method, of class SpineSchemaValidator.
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
     * Test of getSupportingData method, of class SpineSchemaValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class SpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        String path = System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/PDS/PRPA_IN000203UK03.xml";
        String msg = new String(Files.readAllBytes(Paths.get(path)));
        SpineMessage sm = new SpineMessage(msg);
        boolean expResult = true;
        // resource is a schema file
        String schemapath = System.getenv("TKWROOT") + "/config/SPINE_schema/omvt/Schemas/PRPA_IN000203UK03.xsd";
        instance.setResource(schemapath);
        ValidationReport[] result = instance.validate(sm);
        assertEquals(expResult, result[0].getPassed());

        expResult = false;
        schemapath = System.getenv("TKWROOT") + "/config/SPINE_schema/omvt/Schemas/PRPA_IN000203UK06.xsd";
        instance.setResource(schemapath);
        result = instance.validate(sm);
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of setResource method, of class SpineSchemaValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String s = "";
        instance.setResource(s);
    }

    /**
     * Test of setData method, of class SpineSchemaValidator.
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
     * Test of addValidationExceptionDetail method, of class
     * SpineSchemaValidator.
     */
    @Test
    public void testAddValidationExceptionDetail() throws IOException, Exception {
        System.out.println("addValidationExceptionDetail");
        // need to run validate spine message first
        String path = System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/PDS/PRPA_IN000203UK03.xml";
        String msg = new String(Files.readAllBytes(Paths.get(path)));
        SpineMessage sm = new SpineMessage(msg);
        // resource is a schema file
        String schemapath = System.getenv("TKWROOT") + "/config/SPINE_schema_20120723/omvt/Schemas/PRPA_IN000203UK03.xsd";
        instance.setResource(schemapath);
        ValidationReport[] result = instance.validate(sm);

        String s = "validation message";
        instance.addValidationExceptionDetail(s);
    }

    /**
     * Test of setType method, of class SpineSchemaValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of initialise method, of class SpineSchemaValidator.
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
     * Test of validate method, of class SpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = null;
        boolean stripHeader = false;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
    }

    /**
     * Test of doDomValidation method, of class SpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoDomValidation() throws Exception {
        System.out.println("doDomValidation");
        String path = System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/PDS/PRPA_IN000203UK03.xml";
        StreamSource toValidate = new StreamSource(new FileReader(path));
        String schemapath = System.getenv("TKWROOT") + "/config/SPINE_schema/omvt/Schemas/PRPA_IN000203UK03.xsd";
        StreamSource schema = new StreamSource(new File(schemapath));
        ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(instance);
        boolean stripHeader = false;
        instance.setData("/");
        instance.doDomValidation(toValidate, schema, v, stripHeader);
    }

    /**
     * Test of doDomValidationFail method, of class SpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testDoDomValidationFail() throws Exception {
        System.out.println("doDomValidation");
        // incorrect schema causes an exception to be thrown
        String path = System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/PDS/PRPA_IN000203UK03.xml";
        StreamSource toValidate = new StreamSource(new FileReader(path));
        String schemapath = System.getenv("TKWROOT") + "/config/SPINE_schema/omvt/Schemas/PRPA_IN000203UK06.xsd";
        StreamSource schema = new StreamSource(new File(schemapath));
        ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(instance);
        boolean stripHeader = false;
        instance.setData("/");
        instance.doDomValidation(toValidate, schema, v, stripHeader);
    }

    /**
     * Test of getValidationRoot method, of class SpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValidationRoot() throws Exception {
        System.out.println("getValidationRoot");
        String path = System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/PDS/PRPA_IN000203UK03.xml";
        // NB has to be a Reader
        StreamSource s = new StreamSource(new FileReader(path));
        boolean stripHeader = false;
        String expResult = "#document";
        // starting xpath
        instance.setData("/");
        Node result = instance.getValidationRoot(s, stripHeader);
        assertEquals(expResult, result.getNodeName());
    }

    /**
     * Test of setVariableProvider method, of class SpineSchemaValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    /**
     * Test of validate method, of class SpineSchemaValidator.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }

}
