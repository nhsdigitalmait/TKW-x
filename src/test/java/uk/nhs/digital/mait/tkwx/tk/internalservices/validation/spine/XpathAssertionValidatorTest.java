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
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SOAP_HEADER_WSASPINE_ACTION;

/**
 *
 * @author sifa2
 */
public class XpathAssertionValidatorTest {

    private XpathAssertionValidator instance;

    public XpathAssertionValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new XpathAssertionValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setResource method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String t = "";
        instance.setResource(t);
    }

    /**
     * Test of setType method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of writeExternalOutput method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setData method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetData() {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of getSupportingData method, of class XpathAssertionValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of initialise method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setResource("/");
        instance.setType("xpathequals");
        instance.initialise();
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test(expected=Exception.class)
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = "";
        boolean stripHeader = false;
        instance.setResource("/");
        instance.initialise();
        ValidatorOutput expResult = null;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathEquals() throws Exception {
        System.out.println("validateXpathEquals");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathequals");
        instance.setData("urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotequals");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathContains() throws Exception {
        System.out.println("validateXpathContains");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathcontains");
        instance.setData("QUPA_IN000005UK01");
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotcontains");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathExists() throws Exception {
        System.out.println("validateXpathExists");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathexists");
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotexists");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());
    }
    
    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathMatches() throws Exception {
        System.out.println("validateXpathMatches");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathmatches");
        instance.setData("^urn:nhs:names:services:pdsquery/QUPA_IN000005UK01$");
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotmatches");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathCompare() throws Exception {
        System.out.println("validateXpathCompare");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathcompare");
        instance.setData(SOAP_HEADER_WSASPINE_ACTION);
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotcompare");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathEqualsIgnoreCase() throws Exception {
        System.out.println("validateXpathEqualsIgnoreCase");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathequalsignorecase");
        instance.setData("URN:nhs:names:services:pdsquery/QUPA_IN000005UK01");
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotequalsignorecase");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessageXpathContainsIgnoreCase() throws Exception {
        System.out.println("validateXpathContainsIgnoreCase");
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/","QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        // NB wsaspine not wsa
        instance.setResource(SOAP_HEADER_WSASPINE_ACTION);
        instance.setType("xpathcontainsignorecase");
        instance.setData("qupa_IN000005UK01");
        instance.initialise();
        boolean expResult = true;
        ValidationReport[] result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());

        instance.setType("xpathnotcontainsignorecase");
        instance.initialise();
        expResult = false;
        result = instance.validate(sm);
        System.out.println(result[0].getTestDetails());
        assertEquals(expResult, result[0].getPassed());
    }


    /**
     * Test of setVariableProvider method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * see testValidate_SpineMessageXpathMatches etc
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
    }

}
