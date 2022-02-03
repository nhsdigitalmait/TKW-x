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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.ConceptMap;
import org.hl7.fhir.dstu3.model.NamingSystem;
import org.hl7.fhir.dstu3.model.OperationDefinition;
import org.hl7.fhir.dstu3.model.SearchParameter;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author simonfarrow
 */
public class HapiAssetCacheR4Test {
    
    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();


    private HapiAssetCacheStu3 instance;
    private final static String ROOT = System.getenv("TKWROOT")+"/config/GP_CONNECT/validator_config/fhir_assets/STU3-FHIR-Assets/";
    private static final String PROFILE_VERSION = "100";
    private static final String SOFTWARE_VERSION = "99";
    
    public HapiAssetCacheR4Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new HapiAssetCacheStu3(new ArrayList<File>());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getResource method, of class HapiAssetCache.
     * @throws java.io.IOException
     */
    @Test
    public void testGetResource() throws IOException, Exception {
        System.out.println("getResource");
        String path = "src/test/resources/R4/messaging.json";
        instance.addFile(path);
        String uri = "https://fhir.nhs.uk/CapabilityStatement/electronic-prescribing-server";
        IBaseResource result = instance.getResource(uri);
        assertNotNull(result);
    }

    /**
     * Test of getStructureDefinitions method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetStructureDefinitions() throws Exception {
        System.out.println("getStructureDefinitions");
        String path = "src/test/resources/R4/DM-CommunicationRequest.StructureDefinition.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, StructureDefinition> result = instance.getStructureDefinitions();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getResourceCache method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetResourceCache() throws Exception {
        System.out.println("getResourceCache");
        String path = "src/test/resources/R4/MedicationRequest-course-therapy-type-map.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, IBaseResource> result = instance.getResourceCache();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getValueSetCache method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValueSetCache() throws Exception {
        System.out.println("getValueSetCache");
        String path = "src/test/resources/R4/ValueSet-DM-MedicationCode.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, ValueSet> result = instance.getValueSetCache();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getCodeSystemCache method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCodeSystemCache() throws Exception {
        System.out.println("getCodeSystemCache");
        String path = "src/test/resources/R4/CodeSystem-medicationdispense-prescription-status.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, CodeSystem> result = instance.getCodeSystemCache();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getCapabilityStatementCache method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCapabilityStatementCache() throws Exception {
        System.out.println("getCapabilityStatementCache");
        String path = "src/test/resources/R4/messaging.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, CapabilityStatement> result = instance.getCapabilityStatementCache();
        assertEquals(expResult, result.size());
        
    }

    /**
     * Test of getConceptMapCache method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetConceptMapCache() throws Exception {
        System.out.println("getConceptMapCache");
        String path = "src/test/resources/R4/MedicationRequest-course-therapy-type-map.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, ConceptMap> result = instance.getConceptMapCache();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getValueSet method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValueSet() throws Exception {
        System.out.println("getValueSet");
        String path = "src/test/resources/R4/ValueSet-DM-MedicationCode.json";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/ValueSet/DM-MedicationRequest-Code";
        String expResult = "DMMedicationRequestCode";
        ValueSet result = instance.getValueSet(system);
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of getCodeSystem method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCodeSystem() throws Exception {
        System.out.println("getCodeSystem");
        String path = "src/test/resources/R4/CodeSystem-medicationdispense-prescription-status.json";
       instance.addFile(path);
        String system = "https://fhir.nhs.uk/CodeSystem/EPS-task-status-reason";
        String expResult = "Medication Dispense Prescription Status";
        CodeSystem result = instance.getCodeSystem(system);
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of getCapabilityStatement method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCapabilityStatement() throws Exception {
        System.out.println("getCapabilityStatement");
        String path = "src/test/resources/R4/messaging.json";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/CapabilityStatement/electronic-prescribing-server";
        String expResult = "Messaging support Capability Statement";
        CapabilityStatement result = instance.getCapabilityStatement(system);
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of getConceptMap method, of class HapiAssetCache.
     */
    @Test
    public void testGetConceptMap() throws Exception {
        System.out.println("getConceptMap");
        String path = "src/test/resources/R4/MedicationRequest-course-therapy-type-map.json";
       instance.addFile(path);
        String system = "https://fhir.nhs.uk/ConceptMap/MedicationRequest-course-therapy-type-map";
        ConceptMap result = instance.getConceptMap(system);
        assertNotNull(result);
    }


    /**
     * Test of getFhirContext method, of class HapiAssetCache.
     */
    @Test
    public void testGetFhirContext() {
        System.out.println("getFhirContext");
        FhirContext result = instance.getFhirContext();
        assertNotNull(result);
    }

    /**
     * Test of addFile method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddFile() throws Exception {
        System.out.println("addFile");
        String path = "src/test/resources/R4/messaging.json";
        instance.addFile(path);
    }

    /**
     * Test of addAll method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddAll() throws Exception {
        System.out.println("addAll");
        String d = "src/test/resources/R4";
        instance.addAll(d);
    }

    /**
     * Test of addResource method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddResource() throws Exception {
        System.out.println("addResource");
        String path = "src/test/resources/R4/messaging.json";
        String r = new String(Files.readAllBytes(Paths.get(path)));
        instance.addResource(r);
    }

    /**
     * Test of setProfileVersionFileName method, of class HapiAssetCache.
     */
    @Test
    public void testSetProfileVersionFileName() {
        System.out.println("setProfileVersionFileName");
        String name = "";
        instance.setProfileVersionFileName(name);
    }

    /**
     * Test of ignore method, of class HapiAssetCache.
     */
    @Test
    public void testIgnore() {
        System.out.println("ignore");
        String r = "";
        boolean expResult = false;
        boolean result = instance.ignore(r);
        assertEquals(expResult, result);
    }

    /**
     * Test of convertValidationResultToOOString method, of class HapiAssetCacheStu3.
     */
    @Test
    public void testConvertValidationResultToOOString() throws Exception {
        System.out.println("convertValidationResultToOOString");

        String expResult = "<OperationOutcome xmlns=\"http://hl7.org/fhir\">";
        
        // pick a fhir v3 source file

        System.setProperty("tks.validator.hapifhirvalidator.softwareversion", SOFTWARE_VERSION);
        System.setProperty("tks.validator.hapifhirvalidator.profileversion", PROFILE_VERSION);
        HapiFhirValidatorEngine hfve = new HapiFhirValidatorEngine(null);
        String o = Utils.readFile2String("src/test/resources/slots.json");
        ValidationResult vr = hfve.validate(o);

        String result = instance.convertValidationResultToOOString(vr, hfve);
        assertTrue(result.startsWith(expResult));


    }

    /**
     * Test of getRebuildBusyOOMessage method, of class HapiAssetCacheStu3.
     */
    @Test
    public void testGetRebuildBusyOOMessage() {
        System.out.println("getRebuildBusyOOMessage");
        String expResult = "Server is busy whilst Profile is being Rebuilt - try again later";
        String result = instance.getRebuildBusyOOMessage();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getRebuildSuccessOOMessage method, of class HapiAssetCacheStu3.
     */
    @Test
    public void testGetRebuildSuccessOOMessage() {
        System.out.println("getRebuildSuccessOOMessage");
        String expResult = "Server Profile Rebuild Successful";
        String result = instance.getRebuildSuccessOOMessage();
        assertTrue(result.contains(expResult));
    }

}
