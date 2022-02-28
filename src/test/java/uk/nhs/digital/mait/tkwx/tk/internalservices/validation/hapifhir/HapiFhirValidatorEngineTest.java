/*
 Copyright 2018 Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.VersionUtil;
import ca.uhn.fhir.validation.ValidationResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class HapiFhirValidatorEngineTest {
    
    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private HapiFhirValidatorEngine instance;

    private static final String PROFILE_VERSION = "100";

    public HapiFhirValidatorEngineTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        System.setProperty("tks.validator.hapifhirvalidator.profileversion", PROFILE_VERSION);
        instance = new HapiFhirValidatorEngine(null);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class HapiFhirValidatorEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetInstance() throws Exception {
        System.out.println("getInstance");
        assertNotNull(instance);
    }

    /**
     * Test of validate method, of class HapiFhirValidatorEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate() throws Exception {
        System.out.println("validate");
        // pick a fhir v3 source file
        String o = Utils.readFile2String("src/test/resources/slots.json");
        boolean expResult = false;
        ValidationResult result = instance.validate(o);
        // reports unknown element fullUrl
        System.out.println(result.toString());
        assertEquals(expResult, result.isSuccessful());
    }

    /**
     * Test of getMinimumReportLevel method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetMinimumReportLevel() {
        System.out.println("getMinimumReportLevel");
        int expResult = 0;
        int result = instance.getMinimumReportLevel();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSoftwareVersion method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetSoftwareVersion() {
        System.out.println("getSoftwareVersion");
        String expResult = VersionUtil.getVersion();
        String result = instance.getSoftwareVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of getProfileVersion method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetProfileVersion() {
        System.out.println("getProfileVersion");
        String expResult = PROFILE_VERSION;
        String result = instance.getProfileVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContext method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetContext() {
        System.out.println("getContext");
        FhirContext expResult = FhirContext.forDstu3();
        FhirContext result = instance.getContext();
        assertEquals(expResult.getVersion().getVersion(), result.getVersion().getVersion());
    }


    /**
     * Test of rebuild method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testRebuild_0args() {
        System.out.println("rebuild");
        instance.rebuild();
    }

    /**
     * Test of rebuild method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testRebuild_String() {
        System.out.println("rebuild");
        instance.rebuild("test");
    }

    /**
     * Test of getRebuildBusyOOMessage method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetRebuildBusyOOMessage() {
        System.out.println("getRebuildBusyOOMessage");
        boolean expResult = true;
        String result = instance.getRebuildBusyOOMessage();
        assertEquals(expResult, result.startsWith("<OperationOutcome"));
    }

    /**
     * Test of getRebuildSuccessOOMessage method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetRebuildSuccessOOMessage() {
        System.out.println("getRebuildSuccessOOMessage");
        boolean expResult = true;
        String result = instance.getRebuildSuccessOOMessage();
        assertEquals(expResult, result.startsWith("<OperationOutcome"));
    }

    /**
     * Test of validateWithStringOOResult method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testValidateWithStringOOResult() throws Exception {
        System.out.println("validateWithStringOOResult");
        String o = "<Bundle xmlns=\"http://hl7.org/fhir\"/>";
        boolean expResult = true;
        String result = instance.validateWithStringOOResult(o);
        assertEquals(expResult, result.startsWith("<OperationOutcome"));
    }

    /**
     * Test of isPrepopulatedValidationSupport method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testIsPrepopulatedValidationSupport() {
        System.out.println("isPrepopulatedValidationSupport");
        boolean expResult = true;
        boolean result = instance.isPrepopulatedValidationSupport();
        assertEquals(expResult, result);
    }

    /**
     * Test of isInMemoryTerminologyServerValidationSupport method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testIsInMemoryTerminologyServerValidationSupport() {
        System.out.println("isInMemoryTerminologyServerValidationSupport");
        boolean expResult = true;
        boolean result = instance.isInMemoryTerminologyServerValidationSupport();
        assertEquals(expResult, result);
    }

    /**
     * Test of isCommonCodeSystemTerminologyServiceValidationSupport method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testIsCommonCodeSystemTerminologyServiceValidationSupport() {
        System.out.println("isCommonCodeSystemTerminologyServiceValidationSupport");
        boolean expResult = true;
        boolean result = instance.isCommonCodeSystemTerminologyServiceValidationSupport();
        assertEquals(expResult, result);
    }

    /**
     * Test of isSnapshotGeneratingValidationSupport method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testIsSnapshotGeneratingValidationSupport() {
        System.out.println("isSnapshotGeneratingValidationSupport");
        boolean expResult = true;
        boolean result = instance.isSnapshotGeneratingValidationSupport();
        assertEquals(expResult, result);
    }

    /**
     * Test of isCachingValidationSupport method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testIsCachingValidationSupport() {
        System.out.println("isCachingValidationSupport");
        boolean expResult = true;
        boolean result = instance.isCachingValidationSupport();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRemoteTerminologyServiceUrl method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetRemoteTerminologyServiceUrl() {
        System.out.println("getRemoteTerminologyServiceUrl");
        String expResult = null;
        String result = instance.getRemoteTerminologyServiceUrl();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFhirVersion method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testGetFhirVersion() {
        System.out.println("getFhirVersion");
        String expResult = "DSTU3";
        String result = instance.getFhirVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of updateFhirTerminologyServerAccessToken method, of class HapiFhirValidatorEngine.
     */
    @Test
    public void testUpdateFhirTerminologyServerAccessToken() {
        System.out.println("updateFhirTerminologyServerAccessToken");
        instance.updateFhirTerminologyServerAccessToken();
    }

}
