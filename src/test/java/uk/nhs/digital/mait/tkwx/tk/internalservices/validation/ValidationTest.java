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
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.SpineValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SEND_CDA_V2_SERVICE;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class ValidationTest {

    // TODO there are more types than this eg hl7_
    String[] iTKTypes = {"xpathexists", "xpathnotexists",
        "xpathequals", "xpathnotequals",
        "xpathcontains", "xpathnotcontains",
        "xpathmatches", "xpathnotmatches",
        "xpathin",
        "xslt",
        "signature",
        "cdarenderer",
        "cdatemplatelist",
        "cdaconformancexslt",
        "cdaconformanceschema",};

    String[] spineTypes = {"xpathequalsignorecase", "xpathnotequalsignorecase",
        "xpathcontainsignorecase", "xpathnotcontainsignorecase",
        "schema"
    };

    private final HashMap<String, Validation> iTKValidations = new HashMap<>();
    private final HashMap<String, Validation> spineValidations = new HashMap<>();

    private Validation instance;
    private Validation iFinstance;

    static private Properties properties;

    public ValidationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        properties = new Properties();
        properties.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        properties.load(new FileReader(System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties"));
        // add spine validators for ignore case tests
        properties.put("tks.validator.check.xpathequalsignorecase", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        properties.put("tks.validator.check.xpathnotequalsignorecase", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        properties.put("tks.validator.check.xpathcontainsignorecase", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        properties.put("tks.validator.check.xpathnotcontainsignorecase", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
//      p.load(new FileReader(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"));
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private void addITK(Validation v) {
        iTKValidations.put(v.getType(), v);
    }

    private void addSpine(Validation v) {
        spineValidations.put(v.getType(), v);
    }

    @Before
    public void setUp() throws Exception {

        // just pick one for general testing
        instance = new Validation("CHECK");
        instance.setType("xpathexists");
        instance.setResource("//itk:DistributionEnvelope");  // xpathexists parameter
        instance.initialise();

        iFinstance = new Validation("IF");
        iFinstance.setType("xpathexists");
        iFinstance.setResource("/fhir:Bundle");
        iFinstance.initialise();
        iFinstance.initCheck(properties);

        // specific tests
        Validation validation = null;

        for (String type : iTKTypes) {
            validation = new Validation("CHECK");
            validation.setType(type);

            switch (type) {
                case "xpathexists":
                    // xpath one parm
                    validation.setResource("//itk:DistributionEnvelope");  // xpathexists parameter
                    break;
                case "xpathnotexists":
                    validation.setResource("//itk:xxxDistributionEnvelope");  // xpathnotexists parameter
                    break;
                // two parms
                case "xpathequals":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathequals parameter
                    validation.setData(SEND_CDA_V2_SERVICE);
                    break;
                case "xpathnotequals":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathnotequals parameter
                    validation.setData("urn:nhs-itk:services:201005:SendCDADocument-v3-0");
                    break;
                case "xpathmatches":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathmatches parameter
                    validation.setData("^urn:.*v2-0$");
                    break;
                case "xpathnotmatches":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathnotmatches parameter
                    validation.setData("^urn:.*v3-0$");
                    break;
                case "xpathcontains":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathcontains parameter
                    validation.setData("services:201005:SendCDADocument");
                    break;
                case "xpathnotcontains":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathnotcontains parameter
                    validation.setData("services:201005:xxxSendCDADocument");
                    break;
                case "xpathin":
                    validation.setResource("//itk:DistributionEnvelope/itk:header/@service");  // xpathin parameter
                    validation.setData("\""+SEND_CDA_V2_SERVICE+"\" \"urn:nhs-itk:services:201005:SendCDADocument-v3-0\"");
                    break;
                case "xslt":
                    validation.setResource("src/test/resources/testtransform.xsl");  // xslt parameter
                    validation.setData("ERROR");
                    break;
                case "signature":
                case "cdarenderer":
                case "cdatemplatelist":
                    break;
                case "cdaconformancexslt":
                    validation.setResource(System.getenv("TKWROOT") + "/contrib/Common/cda/CfH_CDA_Document_Schematron.xsl");
                    validation.setData("Error");
                    break;
                case "conformanceschema":
                    // conformanceschema  TODO does not seem to be used
                    validation.setResource(System.getenv("TKWROOT") + "/contrib/DMS_Schema/Ambulance/Schemas/POCD_MT030001UK01.xsd");
                    break;
                case "cdaconformanceschema":
                    validation.setResource(System.getenv("TKWROOT") + "/contrib/DMS_Schema/Ambulance/Schemas/POCD_MT030001UK01.xsd");
                    break;
            } // switch

            validation.initialise();
            addITK(validation);

        } // for

        for (String type : spineTypes) {

            // xpath equals ic
            validation = new Validation("CHECK");
            validation.setType(type);
            validation.setResource("//hl7:interactionId/@extension");  // xpathequals parameter

            // TODO the case of the messages is wrong here they show what is being looked for not what is present
            switch (type) {
                case "xpathequalsignorecase":
                    validation.setData("copc_IN000001GB01");
                    break;
                case "xpathnotequalsignorecase":
                    validation.setData("xxcopc_IN000001GB01");
                    break;
                case "xpathcontainsignorecase":
                    // xpath contains ic
                    validation.setData("copc_IN");
                    break;
                case "xpathnotcontainsignorecase":
                    validation.setData("copc_INX");
                    break;
                case "schema":
                    // schema
                    validation.setResource(System.getenv("TKWROOT") + "/config/SPINE_schema/omvt/TightenedSchemas/COPC_IN000001GB01.xsd");
                    break;
            }

            validation.initialise();
            addSpine(validation);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setAnnotation method, of class Validation.
     */
    @Test
    public void testSetAnnotation() {
        System.out.println("setAnnotation");
        String a = "annotation";
        instance.setAnnotation(a);
    }

    /**
     * Test of getSequenceType method, of class Validation.
     */
    @Test
    public void testGetSequenceType() {
        System.out.println("getSequenceType");
        SequenceType expResult = SequenceType.CHECK;
        SequenceType result = instance.getSequenceType();
        assertEquals(expResult, result);
    }

    /**
     * Test of appendValidation method, of class Validation.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAppendValidation() throws Exception {
        System.out.println("appendValidation");
        // cant append to a check validation
        Validation v = new Validation("THEN");
        iFinstance.appendValidation(v);
    }

    /**
     * Test of setParent method, of class Validation.
     */
    @Test
    public void testSetParent() {
        System.out.println("setParent");
        Validation v = new Validation("CHECK");
        instance.setParent(v);
    }

    /**
     * Test of setType method, of class Validation.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "IF";
        iFinstance.setType(t);
    }

    /**
     * Test of getType method, of class Validation.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        String expResult = "THEN";
        instance.setType(expResult);
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResource method, of class Validation.
     */
    @Test
    public void testGetResource() {
        System.out.println("getResource");
        String expResult = "resource";
        instance.setResource(expResult);
        String result = instance.getResource();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResource method, of class Validation.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "resource";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class Validation.
     */
    @Test
    public void testSetData() {
        System.out.println("setData");
        String d = "data";
        instance.setData(d);
    }

    /**
     * Test of setService method, of class Validation.
     */
    @Test
    public void testSetService() {
        System.out.println("setService");
        String s = "service";
        instance.setService(s);
    }

    /**
     * Test of getServiceName method, of class Validation.
     */
    @Test
    public void testGetServiceName() {
        System.out.println("getServiceName");
        String expResult = "service";
        instance.setService(expResult);
        String result = instance.getServiceName();
        assertEquals(expResult, result);
    }

    /**
     * Test of initialise method, of class Validation.
     */
    @Test
    public void testInitialise_0args() {
        System.out.println("initialise_0args");
        Validation result = instance.initialise();
        assertNotNull(result);
    }

    /**
     * Test of initialise method, of class Validation.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise_Properties() throws Exception {
        System.out.println("initialise_Properties");
        Validation expResult = null;
        Validation result = instance.initialise(properties);
        assertEquals(expResult, result);
    }

    /**
     * Test of initCheck method, of class Validation.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitCheck() throws Exception {
        System.out.println("initCheck");
        instance.initCheck(properties);
    }

    /**
     * Test of validate method, of class Validation. Spine Version
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage_SpineValidatorService() throws Exception {
        System.out.println("validate");
        SpineMessage spineMessage = new SpineMessage("src/test/resources", "ITK_Trunk.message");

        SpineValidatorService vs = new SpineValidatorService();
        String propertiesFile = System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        String s = ""; //serviceName
        vs.boot(t, properties, s);

        instance.setType("attachment1_xpathexists");
        instance.initCheck(properties);

        int expResult = 1;
        ValidationReport[] result = instance.validate(spineMessage, vs);
        assertEquals(expResult, result.length);

        System.out.println(result[0].getTestDetails());

        boolean bExpResult = true;
        assertEquals(bExpResult, result[0].getPassed());

        for (String type : spineTypes) {
            Validation validation = spineValidations.get(type);
            if (validation == null) {
                fail("Missing validation " + type);
            } else {
                validation.initCheck(properties);

                result = validation.validate(spineMessage, vs);

                assertEquals(expResult, result.length);
                boolean passed = result[0].getPassed();
                if (passed) {
                    System.out.println(result[0].getTestDetails());
                } else {
                    System.out.println(result[0].getDetail());
                }

                assertEquals(bExpResult, passed);
            }
        }
    }

    /**
     * Test of validate method, of class Validation. This is for ITK messages
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
        String itkMessage = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));

        boolean stripHeader = false;
        ValidatorService vs = new ValidatorService();
        int expResult = 1;
        boolean bExpResult = true;

        for (String type : iTKTypes) {
            System.out.println("type=" + type);
            Validation validation = iTKValidations.get(type);
            if (validation == null) {
                fail("Missing validation " + type);
            } else {
                validation.initCheck(properties);

                ValidationReport[] result = validation.validate(itkMessage, null, stripHeader, vs);

                assertEquals(expResult, result.length);
                boolean passed = result[0].getPassed();
                if (passed) {
                    System.out.println(result[0].getTestDetails());
                } else {
                    System.out.println(result[0].getDetail());
                }

                bExpResult = !validation.getType().equals("signature") && !validation.getType().equals("xslt");
                assertEquals(bExpResult, passed);
            }
        }
    }

    /**
     * Test of setVariableProvider method, of class Validation.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        instance.setVariableProvider(instance);
    }

    /**
     * Test of getVariable method, of class Validation.
     */
    @Test
    public void testGetVariable() {
        System.out.println("getVariable");
        String s = "a";
        Object expResult = "b";
        instance.setVariableProvider(instance);
        instance.setVariable("a", "b");
        Object result = instance.getVariable(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariable method, of class Validation.
     */
    @Test
    public void testSetVariable() {
        System.out.println("setVariable");
        String s = "v";
        Object o = "value";
        instance.setVariableProvider(instance);
        instance.setVariable(s, o);
    }

    /**
     * Test of if Validation of class Validation.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCheck() throws Exception {
        System.out.println("check");
        ValidatorService vs = new ValidatorService();
        Validation checkValidation = new Validation("CHECK");
        checkValidation.setType("xpathexists");
        checkValidation.setResource("/fhir:Bundle");
        checkValidation.initialise();
        checkValidation.initCheck(properties);
        int expResult = 1;
        ValidationReport[] result = checkValidation.validate("<Bundle xmlns=\"http://hl7.org/fhir\">xx</Bundle>", null, false, vs);
        assertEquals(expResult, result.length);
        boolean bExpResult = true;
        assertEquals(bExpResult, result[0].getPassed());
    }

    /**
     * Test of if Validation of class Validation.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIf() throws Exception {
        System.out.println("if");

        // NB initialise is only an accessor not a mutator so no point in calling it here
        ValidatorService vs = new ValidatorService();

        Validation checkValidationThen = new Validation("CHECK");
        checkValidationThen.setType("xpathequals");
        checkValidationThen.setResource("/fhir:Bundle");
        checkValidationThen.setData("xx");
        checkValidationThen.setAnnotation("Then clause check for xx");
        checkValidationThen.initCheck(properties);

        Validation checkValidationElse = new Validation("CHECK");
        checkValidationElse.setType("xpathequals");
        checkValidationElse.setResource("/fhir:Bundle");
        checkValidationElse.setData("xx");
        checkValidationElse.setAnnotation("Else clause check for xx");
        checkValidationElse.initCheck(properties);

        // test criterion for IF then leg statement
        Validation iFValidation = new Validation("IF");
        iFValidation.setType("xpathexists");
        iFValidation.setResource("/fhir:Bundle");
        iFValidation.initCheck(properties);

        iFValidation.setType("THEN");
        iFValidation.appendValidation(checkValidationThen);
        iFValidation.setType("ELSE");
        iFValidation.appendValidation(checkValidationElse);
        iFValidation.setType("ENDIF");

        String payload = "<Bundle xmlns=\"http://hl7.org/fhir\">xx</Bundle>";

        int expResult = 1;
        boolean bExpResult = true;
        String expStrResult = "Then clause check for xx";

        ValidationReport[] result = iFValidation.validate(payload, null, false, vs);

        assertEquals(expResult, result.length);
        assertEquals(bExpResult, result[0].getPassed());
        System.out.println(result[0].getAnnotation());
        assertEquals(expStrResult, result[0].getAnnotation());

        bExpResult = false;
        // check the leg failure condition by changing the paylaod so there noo match for if test
        result = iFValidation.validate(payload.replaceFirst("xx", "yy"), null, false, vs);

        assertEquals(expResult, result.length);
        assertEquals(bExpResult, result[0].getPassed());
        System.out.println(result[0].getAnnotation());
        assertEquals(expStrResult, result[0].getAnnotation());

        bExpResult = true;
        // checking else leg
        iFValidation = new Validation("IF");
        iFValidation.setType("xpathexists");
        iFValidation.setResource("/fhir:XBundle"); // absent element
        iFValidation.initCheck(properties);

        iFValidation.setType("THEN");
        iFValidation.appendValidation(checkValidationThen);
        iFValidation.setType("ELSE");
        iFValidation.appendValidation(checkValidationElse);
        iFValidation.setType("ENDIF");

        expStrResult = "Else clause check for xx";
        result = iFValidation.validate(payload, null, false, vs);
        assertEquals(expResult, result.length);
        assertEquals(bExpResult, result[0].getPassed());
        System.out.println(result[0].getAnnotation());
        assertEquals(expStrResult, result[0].getAnnotation());

        iFValidation.walk(iFValidation);
    }

    @Test
    public void testNestedIf() throws Exception {
        // NB initialise is only an accessor not a mutator so no point in calling it here
        ValidatorService vs = new ValidatorService();

        Validation checkValidationLevel2 = new Validation("CHECK");
        checkValidationLevel2.setType("xpathequals");
        checkValidationLevel2.setResource("/fhir:Bundle");
        checkValidationLevel2.setData("xx");
        checkValidationLevel2.setAnnotation("Level 2 clause check for xx");
        checkValidationLevel2.initCheck(properties);

        // test criterion for IF then leg statement
        Validation iFValidationLevel2 = new Validation("IF");
        iFValidationLevel2.setType("xpathexists");
        iFValidationLevel2.setResource("/fhir:Bundle");
        iFValidationLevel2.initCheck(properties);
     
        iFValidationLevel2.setType("THEN");
        iFValidationLevel2.appendValidation(checkValidationLevel2);
        iFValidationLevel2.setType("ENDIF");
       
        iFValidationLevel2.walk();
        
        Validation iFValidation = new Validation("IF");
        iFValidation.setType("xpathexists");
        iFValidation.setResource("/fhir:Bundle");
        iFValidation.initCheck(properties);

        iFValidation.setType("THEN");
        iFValidation.appendValidation(iFValidationLevel2);
        iFValidation.setType("ENDIF");
        
        iFValidation.walk();
    }

    /**
     * Test of validate method, of class Validation.
     * see testCheck
     */
    @Test
    public void testValidate_4args() throws Exception {
        System.out.println("validate");
    }

    /**
     * Test of walk method, of class Validation.
     */
    @Test
    public void testWalk_0args() {
        System.out.println("walk");
        instance.walk();
    }

    /**
     * Test of walk method, of class Validation.
     */
    @Test
    public void testWalk_Validation() throws Exception {
        System.out.println("walk");
        Validation validation = new Validation("CHECK");
        validation.setType("xpathequals");
        validation.setResource("/fhir:Bundle");
        validation.setData("xx");
        validation.setAnnotation("Level 2 clause check for xx");
        validation.initCheck(properties);
        instance.walk(validation);
    }
}
