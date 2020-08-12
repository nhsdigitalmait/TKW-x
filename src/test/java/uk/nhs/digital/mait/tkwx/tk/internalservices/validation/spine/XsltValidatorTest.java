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

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorOutput;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;

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
     * Test of validate method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/", "QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.initialise();
        instance.setType("");
        instance.setData("ERROR"); // failure search string
        boolean expResult = false;
        ValidationReport[] result = instance.validate(sm);
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of writeExternalOutput method, of class XsltValidator.
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
     * Test of setResource method, of class XsltValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String t = "";
        instance.setResource(t);
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
     * Test of setData method, of class XsltValidator.
     */
    @Test
    public void testSetData() {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of initialise method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.initialise();
    }

    /**
     * Test of setTransform method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetTransform() throws Exception {
        System.out.println("setTransform");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.setTransform();
    }

    /**
     * Test of setType method, of class XsltValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of validate method, of class XsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = "";
        boolean stripHeader = false;
        ValidatorOutput expResult = null;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariableProvider method, of class XsltValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
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
