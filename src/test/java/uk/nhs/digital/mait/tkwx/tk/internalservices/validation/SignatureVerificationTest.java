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

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;

/**
 *
 * @author sifa2
 */
public class SignatureVerificationTest {

    private SignatureVerification instance;
    
    public SignatureVerificationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new SignatureVerification();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class SignatureVerification.
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of getSupportingData method, of class SignatureVerification.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeExternalOutput method, of class SignatureVerification.
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of validate method, of class SignatureVerification.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = new SpineMessage("src/test/resources","ITK_Trunk.message");
        boolean expResult = false;
        instance.setType("attachment1_xx");
        ValidationReport[] result = instance.validate(o);
        // this one fails with no security token. Another one in a different class passed..
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of setType method, of class SignatureVerification.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class SignatureVerification.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class SignatureVerification.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of validate method, of class SignatureVerification.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        // TODO need to add a signed message example
        String s = "<a/>";
        boolean stripHeader = false;
        boolean expResult = false;
        ValidatorOutput result = instance.validate(s, null, stripHeader);
        // fails with no security token found
        assertEquals(expResult, result.getReport()[0].getPassed());
    }

    /**
     * Test of doHtmlEscapes method, of class SignatureVerification.
     */
    @Test
    public void testDoHtmlEscapes() {
        System.out.println("doHtmlEscapes");
        StringBuilder sb = new StringBuilder("A < B");
        SignatureVerification.doHtmlEscapes(sb);
        String expResult = "A &lt; B";
        assertEquals(expResult,sb.toString());
    }

    /**
     * Test of doEscape method, of class SignatureVerification.
     */
    @Test
    public void testDoEscape() {
        System.out.println("doEscape");
        StringBuilder sb = new StringBuilder("A & B");
        String s = "&";
        String w = "&amp;";
        SignatureVerification.doEscape(sb, s, w);
        String expResult = "A &amp; B";
        assertEquals(expResult,sb.toString());
    }

    /**
     * Test of setVariableProvider method, of class SignatureVerification.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    /**
     * Test of validate method, of class SignatureVerification.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }
    
}
