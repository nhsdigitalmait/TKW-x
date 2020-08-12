/*
  Copyright 2012  Damian Murphy murff@warlock.org

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
 * @author simonfarrow
 */
public class TextAssertionValidatorTest {

    private static String xml = "<Bundle xmlns=\"http://hl7.org/fhir\">"
            + "<meta>"
            + "<profile value=\"http://fhir.nhs.net/StructureDefinition/spine-message-bundle-1-0\"/>"
            + "</meta>"
            + "<entry>"
            + "<resource>"
            + "<MessageHeader>"
            + "<meta>"
            + "<profile value=\"http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0\"/>"
            + "</meta>"
            + "</MessageHeader>"
            + "<EmptyElement/>"
            + "<PopulatedElement>xxxxx</PopulatedElement>"
            + "</resource>"
            + "</entry>"
            + "</Bundle>";

    private TextAssertionValidator instance;

    public TextAssertionValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new TextAssertionValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of writeExternalOutput method, of class TextAssertionValidator.
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
     * Test of setResource method, of class TextAssertionValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String d = "aa";
        instance.setResource(d);
    }

    /**
     * Test of validate method, of class TextAssertionValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = null;
        ValidationReport[] expResult = null;
//      ValidationReport[] result = instance.validate(o);
//      assertArrayEquals(expResult, result);
    }

    /**
     * Test of setType method, of class TextAssertionValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "equals";
        instance.setType(t);
    }

    /**
     * Test of setData method, of class TextAssertionValidator.
     */
    @Test
    public void testSetData() {
        System.out.println("setData");
    }

    /**
     * Test of getSupportingData method, of class TextAssertionValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of initialise method, of class TextAssertionValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setType("contains content");
        instance.setResource("aa");
        instance.initialise();
    }

    /**
     * Test of validate method, of class TextAssertionValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        boolean stripHeader = false;

        for (boolean b : new Boolean[]{true, false}) {
            testEquals(stripHeader, b);
            testContains(stripHeader, b);
            testMatches(stripHeader, b);
        }

    }

    /**
     * Test of setVariableProvider method, of class TextAssertionValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = new XpathAssertionValidatorTest.MyVP("a", "b");
        instance.setVariableProvider(vp);
    }

    private void testEquals(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "equals" : "notequals");
        instance.setType(positive ? "equals" : "notequals");

        instance.setResource(xml);
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testContains(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "contains" : "notcontains");
        instance.setType(positive ? "contains" : "notcontains");

        instance.setResource("Bundle");
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testMatches(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "matches" : "notmatches");
        instance.setType(positive ? "matches" : "notmatches");

        instance.setResource("^.*Bundle.*$");
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    static class MyVP implements VariableProvider {

        private final HashMap<String, Object> hm = new HashMap<>();

        MyVP(String s, Object o) {
            hm.put(s, o);
        }

        @Override
        public Object getVariable(String s) {
            return hm.get(s);
        }

        @Override
        public void setVariable(String s, Object o) {
            hm.put(s, o);
        }
    }

    /**
     * Test of validate method, of class TextAssertionValidator.
     * see testEquals, Matches, Contains
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }
}
