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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ValidatorService;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor.BODY_EXTRACTOR_LABEL;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.RequestBodyExtractor;

/**
 *
 * @author sifa2
 */
public class ValidatorFactoryTest {

    private ValidatorFactory instance;
    private Properties properties;

    public ValidatorFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = ValidatorFactory.getInstance();
        properties = new Properties();
        properties.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        properties.load(new FileReader(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"));

        // substitute TKW_ROOT this will have already been done in the executable
        properties = Utils.interpretEnvVars(properties);

        instance.init(properties);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class ValidatorFactory.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetInstance() throws Exception {
        System.out.println("getInstance");
        ValidatorFactory result = ValidatorFactory.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of getSubroutine method, of class ValidatorFactory.
     *
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testGetSubroutine() throws FileNotFoundException, IOException, Exception {
        System.out.println("getSubroutine");
        String r = "check_bundle";
        String expResult = r;
        ValidationSet result = instance.getSubroutine(r);
        assertEquals(expResult, result.getServiceName());
        ArrayList<Validation> validations = result.getValidations();
        for (Validation validation : validations) {
            System.out.println("Walking validation");
            validation.walk();
        }
    }

    /**
     * Test of clear method, of class ValidatorFactory.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        instance.clear();
    }

    /**
     * Test of init method, of class ValidatorFactory.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        // already called
    }

    /**
     * Test of getValidationSet method, of class ValidatorFactory.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValidationSet() throws Exception {
        System.out.println("getValidationSet");
        String s = "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord";
        String expResult = s;
        ValidationSet result = instance.getValidationSet(s);
        assertEquals(expResult, result.getServiceName());
    }

    /**
     * Test of getMetadata method, of class ValidatorFactory.
     */
    @Test
    public void testGetMetadata() {
        System.out.println("getMetadata");
        int expResult = 1;
        List<RulesetMetadata> result = instance.getMetadata();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getMetadata method, of class ValidatorFactory.
     * runs all the validations in testvalidation.conf
     * To extend just add validations to that file and amend the expected pass/fail count
     * @throws java.lang.Exception
     */
    @Test
    public void testTestfile() throws Exception {
        System.out.println("testfile");
        Properties localProperties = new Properties();
        localProperties.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        localProperties.load(new FileReader(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"));
        localProperties.setProperty(VALIDATOR_CONFIG_PROPERTY, "src/test/resources/testvalidation.conf");
        instance.init(localProperties);

//      byte[] fhirRestResponseMessage = Files.readAllBytes(
//              Paths.get("src/test/resources/get_care_record_ADM_3.msg_20170829093302.402.msg"));

        byte[] fhirRestRequestMessage = Files.readAllBytes(
                Paths.get("src/test/resources/POST_127.0.0.1_20170830164832957.msg"));
        String expStrResult = "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord";
        ValidationSet result = instance.getValidationSet(expStrResult);
        assertEquals(expStrResult, result.getServiceName());

        ValidatorService vs = new ValidatorService();

        RequestBodyExtractor be = new RequestBodyExtractor();
        String o = be.getBody(new ByteArrayInputStream(fhirRestRequestMessage));
        
        HashMap<String, Object> extraMessageInfo = new HashMap<>();
        extraMessageInfo.put(BODY_EXTRACTOR_LABEL, be);
        ArrayList<ValidationReport> reports = result.doValidations(o, extraMessageInfo, true, vs);
        int failCount = 0;
        int passCount = 0;
        for (ValidationReport report : reports) {
            if (!report.getPassed()) {
                failCount++;
            } else {
                passCount++;
            }
            System.out.println("Passed = " + report.getPassed() + " Details = " + report.getTestDetails());
            if (report.getAnnotation() != null) {
                System.out.println("Annotation = " + report.getAnnotation());
            }
        }
        
        int expResult = 32; // pass count
        assertEquals(expResult, passCount);
       

        expResult = 4; // failure count
        assertEquals(expResult, failCount);
    }

}
