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

import java.io.ByteArrayInputStream;
import java.io.StringReader;
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

/**
 *
 * @author sifa2
 */
public class XsltValidatorTest {

    private XsltValidator instance;

    public XsltValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new XsltValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of writeExternalOutput method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "src/test/resources";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of validate method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = new SpineMessage("src/test/resources", "ITK_Trunk.message");
        instance.setType("attachment1_xxx");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.setData("ERROR");
        instance.initialise();

        boolean expResult = false;
        ValidationReport[] result = instance.validate(o);
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of setResource method, of class XsltValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String t = "src/test/resources/testtransform.xsl";
        instance.setResource(t);
    }

    /**
     * Test of setData method, of class XsltValidator.
     */
    @Test
    public void testSetData() {
        System.out.println("setData");
        String d = "ERROR";
        instance.setData(d);
    }

    /**
     * Test of getValidationRoot method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValidationRoot() throws Exception {
        System.out.println("getValidationRoot");
        // NB Using ByteArrayInputStream caused an invalid URL exception.
        StreamSource s = new StreamSource(new StringReader("<a/>"));
        boolean stripHeader = false;
        String expResult = "a";
        Node result = instance.getValidationRoot(s, stripHeader);
        assertEquals(expResult, result.getLocalName());
    }

    /**
     * Test of getSupportingData method, of class XsltValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of initialise method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setData("ERROR");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.initialise();
    }

    /**
     * Test of setType method, of class XsltValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "xxxxxxxxxx2_y";
        instance.setType(t);
    }

    /**
     * Test of validate method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = "<a/>";
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.setData("ERROR");
        instance.initialise();
        boolean stripHeader = false;
        boolean expResult = false;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result.getReport()[0].getPassed());
    }

    /**
     * Test of setVariableProvider method, of class XsltValidator.
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
     * Test of validate method, of class XsltValidator.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }

}
