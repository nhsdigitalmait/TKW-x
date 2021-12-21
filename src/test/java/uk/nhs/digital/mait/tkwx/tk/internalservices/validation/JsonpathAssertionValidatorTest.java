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
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidatorTest.MyVP;

/**
 *
 * @author simonfarrow
 */
public class JsonpathAssertionValidatorTest {

    private JsonpathAssertionValidator instance;

    private static String json = "{\n"
            + "  \"resourceType\": \"Bundle\",\n"
            + "  \"meta\": {\n"
            + "    \"profile\": [ \"http://fhir.nhs.net/StructureDefinition/spine-message-bundle-1-0\" ]\n"
            + "  },\n"
            + "  \"entry\": [ {\n"
            + "    \"resource\": {\n"
            + "      \"resourceType\": \"MessageHeader\",\n"
            + "      \"meta\": {\n"
            + "        \"profile\": [ \"http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0\" ]\n"
            + "      }\n"
            + "    }\n"
            + "  } ]\n"
            + "}";

    private final static String PROFILE_PATH = "$.entry[0].resource.meta.profile[0]";

    public JsonpathAssertionValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new JsonpathAssertionValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of writeExternalOutput method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setResource method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String jsonpath = PROFILE_PATH;
        instance.setResource(jsonpath);
    }

    /**
     * Test of setType method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "jsonpathexists";
        instance.setType(t);
    }

    /**
     * Test of setData method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testSetData() {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of getSupportingData method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of initialise method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setType("jsonpathexists");
        instance.setResource(PROFILE_PATH);
        instance.initialise();
    }

    /**
     * Test of validate method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
        String o = json;
        instance.setType("jsonpathexists");
        instance.setResource("resourceType");
        instance.initialise();
        boolean stripHeader = false;
        HashMap<String, Object> extraMessageInfo = new HashMap<>();
        ValidatorOutput result = instance.validate(o, extraMessageInfo, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(reports[0].getPassed());
    }

    /**
     * Test of setVariableProvider method, of class JsonpathAssertionValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    @Test
    public void testValidate_String_boolean() throws Exception {
        /*
        jsonpathequals 
        jsonpathnotequals 
        
        jsonpathcontains 
        jsonpathnotcontains
        
        jsonpathexists 
        jsonpathnotexists 
        
        jsonpathmatches 
        jsonpathnotmatches 
        
        jsonpathin 
        
        jsonpathcompare 
        jsonpathnotcompare
         */
        System.out.println("validate");
        boolean stripHeader = false;

        for (boolean b : new Boolean[]{true, false}) {
            testJsonpathEquals(stripHeader, b);
            testJsonpathContains(stripHeader, b);
            testJsonpathExists(stripHeader, b);
            testJsonpathMatches(stripHeader, b);
            testJsonpathCompare(stripHeader, b);
        }
        testJsonpathIn(stripHeader);
    }

    private void testJsonpathEquals(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "jsonpathequals" : "jsonpathnotequals");
        instance.setType(positive ? "jsonpathequals" : "jsonpathnotequals");

        instance.setResource(PROFILE_PATH);
        instance.setData("http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0");
        instance.initialise();
        ValidatorOutput result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testJsonpathContains(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "jsonpathcontains" : "jsonpathnotcontains");
        instance.setType(positive ? "jsonpathcontains" : "jsonpathnotcontains");

        instance.setResource(PROFILE_PATH);
        instance.setData("StructureDefinition");
        instance.initialise();
        ValidatorOutput result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testJsonpathMatches(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "jsonpathmatches" : "jsonpathnotmatches");
        instance.setType(positive ? "jsonpathmatches" : "jsonpathnotmatches");

        instance.setResource(PROFILE_PATH);
        instance.setData("^.*StructureDefinition.*$");
        instance.initialise();
        ValidatorOutput result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testJsonpathCompare(boolean stripHeader, boolean positive) throws Exception {
        System.out.println(positive ? "jsonpathcompare" : "jsonpathnotcompare");
        instance.setType(positive ? "jsonpathcompare" : "jsonpathnotcompare");

        instance.setResource(PROFILE_PATH);
        instance.setData(PROFILE_PATH);
        instance.initialise();
        ValidatorOutput result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

    private void testJsonpathIn(boolean stripHeader) throws Exception {
        System.out.println("jsonpathin");
        instance.setType("jsonpathin");

        instance.setResource(PROFILE_PATH);
        instance.setData("a http://fhir.nhs.net/StructureDefinition/spine-request-messageheader-1-0");
        instance.initialise();
        ValidatorOutput result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        assertTrue(reports[0].getPassed());

        instance.setData("a b");
        instance.initialise();
        result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        assertFalse(reports[0].getPassed());
    }

    private void testJsonpathExists(boolean stripHeader, boolean positive) throws Exception {
        instance.setType(positive ? "jsonpathexists" : "jsonpathnotexists");

        instance.setResource(PROFILE_PATH);
        instance.initialise();
        ValidatorOutput result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        ValidationReport[] reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());

        instance.setResource("entry[0].resource.meta.profile[0]");
        instance.initialise();
        result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());


        // not there at all
        instance.setResource("DoesNotExist");
        instance.initialise();
        result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? !reports[0].getPassed() : reports[0].getPassed());


        // use of a variable
        VariableProvider vp = new MyVP("$anyoldshite", PROFILE_PATH);
        instance.setVariableProvider(vp);
        instance.setResource("$anyoldshite");
        instance.initialise();
        result = instance.validate(json, null, stripHeader);
        assertNotNull(result);
        assertEquals(result.getReport().length, 1);
        reports = result.getReport();
        System.out.println(reports[0].getTestDetails());
        assertTrue(positive ? reports[0].getPassed() : !reports[0].getPassed());
    }

}
