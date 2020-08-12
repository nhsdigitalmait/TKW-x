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
public class XpathAssertionValidatorTest {

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

    private XpathAssertionValidator instance;
    private static final String PROFILE_PATH = "//fhir:MessageHeader/fhir:meta/fhir:profile[1]/@value";

    public XpathAssertionValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // Put something here if you want to use an external file
//        try {
//            String TKWROOT = System.getenv("TKWROOT");
//            xml = new String(Files.readAllBytes(Paths.get(TKWROOT+"/config/SPINE_FGM/messages_for_validation","FGMQuery_AfterIdentityTransform.txt")));
//            xml = new String(Files.readAllBytes(Paths.get(TKWROOT+"/config/SPINE_FGM/messages_for_validation","FGMQuery_SingleLine.txt")));
//            xml = xml.replaceFirst("^VALIDATE-AS.*FGMQuery_1_0","");
//        } catch (IOException ex) {
//        }
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
     * Test of writeExternalOutput method, of class XpathAssertionValidator.
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setResource method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String t = PROFILE_PATH;
        instance.setResource(t);
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = null;
        ValidationReport[] expResult = null;
        // ValidationReport[] result = instance.validate(o);
        // assertArrayEquals(expResult, result);
    }

    /**
     * Test of setType method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "xpathexists";
        instance.setType(t);

        instance.setType("type12345699_xpathexists");
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
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setType("xpathexists");
        instance.setResource(PROFILE_PATH);
        instance.initialise();
    }

    /**
     * Test of validate method, of class XpathAssertionValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        /*
        xpathequals 
        xpathnotequals 
        
        xpathcontains 
        xpathnotcontains
        
        xpathexists 
        xpathnotexists 
        
        xpathmatches 
        xpathnotmatches 
        
        xpathin 
        
        xpathcompare 
        xpathnotcompare
         */
        System.out.println("validate");
        boolean stripHeader = false;

        for (boolean b : new Boolean[]{true, false}) {
            testXpathEquals(stripHeader, b);
            testXpathContains(stripHeader, b);
            testXpathExists(stripHeader, b);
            testXpathMatches(stripHeader, b);
            testXpathCompare(stripHeader, b);
        }
        testXpathIn(stripHeader);
    }

    private void testXpathEquals(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "xpathequals" : "xpathnotequals");
        instance.setType(positive ? "xpathequals" : "xpathnotequals");

        instance.setResource(PROFILE_PATH);
        instance.setData("http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0");
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testXpathContains(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "xpathcontains" : "xpathnotcontains");
        instance.setType(positive ? "xpathcontains" : "xpathnotcontains");

        instance.setResource(PROFILE_PATH);
        instance.setData("StructureDefinition");
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testXpathMatches(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "xpathmatches" : "xpathnotmatches");
        instance.setType(positive ? "xpathmatches" : "xpathnotmatches");

        instance.setResource(PROFILE_PATH);
        instance.setData("^.*StructureDefinition.*$");
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testXpathCompare(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "xpathcompare" : "xpathnotcompare");
        instance.setType(positive ? "xpathcompare" : "xpathnotcompare");

        instance.setResource(PROFILE_PATH);
        instance.setData(PROFILE_PATH);
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testXpathIn(boolean stripHeader) throws Exception {
        System.out.println("xpathin");
        instance.setType("xpathin");

        instance.setResource(PROFILE_PATH);
        instance.setData("a http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0");
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(reports[0].getPassed());

        instance.setData("a b");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        assertFalse(reports[0].getPassed());
    }

    private void testXpathExists(boolean stripHeader, boolean positive) throws Exception {
        instance.setType(positive ? "xpathexists" : "xpathnotexists");

        instance.setResource(PROFILE_PATH);
        instance.initialise();
        ValidatorOutput result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        instance.setResource("//fhir:MessageHeader/fhir:meta/fhir:profile[@value='http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0']/@value");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        instance.setResource("//fhir:PopulatedElement");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        // No text but there are child elements
        instance.setResource("//fhir:MessageHeader");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        // element of the form  <element/>
        instance.setResource("//fhir:EmptyElement");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        // not there at all
        instance.setResource("//fhir:DoesNotExist");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? !reports[0].getPassed() : reports[0].getPassed());

        // failing string from redmine
        String literal = "descendant::fhir:Bundle//fhir:profile[@value='http://fhir.nhs.net/StructureDefinition/spine-message-bundle-1-0']/../..//fhir:MessageHeader";
        instance.setResource(literal);
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        // use of a variable
        VariableProvider vp = new MyVP("$anyoldshite",literal);
        instance.setVariableProvider(vp);
        instance.setResource("$anyoldshite");
        instance.initialise();
        result = instance.validate(xml, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }
    

    /**
     * Test of setVariableProvider method, of class XpathAssertionValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = new MyVP("a","b");
        instance.setVariableProvider(vp);
    }
    

    /**
     * Test of validate method, of class XpathAssertionValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
        String o = xml;
        instance.setType("xpathexists");
        instance.setResource("//fhir:Bundle");
        instance.initialise();
        boolean stripHeader = false;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(reports[0].getPassed());
    }

    static class MyVP implements VariableProvider {
        private final HashMap<String,Object> hm = new HashMap<>();

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
}
