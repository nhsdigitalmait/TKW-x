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
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author simonfarrow
 */
public class HapiAssetCacheStu3Test {

    private HapiAssetCacheStu3 instance;
    private final static String ROOT = System.getenv("TKWROOT")+"/config/GP_CONNECT/validator_config/fhir_assets/STU3-FHIR-Assets/";
    private static final String PROFILE_VERSION = "100";
    private static final String SOFTWARE_VERSION = "99";
    
    public HapiAssetCacheStu3Test() {
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
        String path = ROOT + "CapabilityStatements/ODSAPI-CapabilityStatement-1.xml";
        instance.addFile(path);
        String uri = "https://fhir.nhs.uk/STU3/CapabilityStatement/ODSAPI-CapabilityStatement-1";
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
        String path = ROOT +"StructureDefinitions/birthPlace.xml";
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
        String path = ROOT +"ConceptMaps/ConceptMap-CareConnect-AdministrativeGender-1.xml";
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
        String path = ROOT +"ValueSets/ValueSet-CareConnect-AdministrativeGender-1.xml";
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
        String path = ROOT +"CodeSystems/CodeSystem-CareConnect-ConditionCategory-1.xml";
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
        String path = ROOT +"CapabilityStatements/ODSAPI-CapabilityStatement-1.xml";
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
        String path = ROOT +"ConceptMaps/ConceptMap-CareConnect-AdministrativeGender-1.xml";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, ConceptMap> result = instance.getConceptMapCache();
        assertEquals(expResult, result.size());
    }
    /**
     * Test of getOperationDefinitionMapCache method, of class HapiAssetCache.
     */
    @Test
    public void testGetOperationDefinitionCache() throws Exception {
        System.out.println("getOperationDefinitionCache");
        String path = ROOT +"Operations/eRS-ClinicalReferralInformation-Operation-1.xml";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, OperationDefinition> result = instance.getOperationDefinitionCache();
        assertEquals(expResult, result.size());
    }
    /**
     * Test of getNamingSystemCache method, of class HapiAssetCache.
     */
    @Test
    public void testGetNamingSystemCache() throws Exception{
        System.out.println("getNamingSystemCache");
        String path = ROOT +"NamingSystem/NamingSystem-NHSNumber.json";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, NamingSystem> result = instance.getNamingSystemCache();
        assertEquals(expResult, result.size());
    }
    
    /**
     * Test of getSearchParameterCache method, of class HapiAssetCache.
     */
    @Test
    public void testGetSearchParameterCache() throws Exception {
        System.out.println("getSearchParameterCache");
        String path = ROOT +"SearchParameters/SearchParameter-ODSAPI-OrganizationRole-PrimaryRole.xml";
        instance.addFile(path);
        int expResult = 1;
        HashMap<String, SearchParameter> result = instance.getSearchParameterCache();
        assertEquals(expResult, result.size());
    }
    /**
     * Test of getValueSet method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValueSet() throws Exception {
        System.out.println("getValueSet");
        String path = ROOT +"ValueSets/ValueSet-CareConnect-AdministrativeGender-1.xml";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/STU3/ValueSet/CareConnect-AdministrativeGender-1";
        String expResult = "Administrative Gender";
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
        String path = ROOT +"CodeSystems/CodeSystem-CareConnect-ConditionCategory-1.xml";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-ConditionCategory-1";
        String expResult = "Care Connect Condition Category";
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
        String path = ROOT +"CapabilityStatements/ODSAPI-CapabilityStatement-1.xml";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/STU3/CapabilityStatement/ODSAPI-CapabilityStatement-1";
        String expResult = "ODSAPI-CapabilityStatement-1";
        CapabilityStatement result = instance.getCapabilityStatement(system);
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of getConceptMap method, of class HapiAssetCache.
     */
    @Test
    public void testGetConceptMap() throws Exception {
        System.out.println("getConceptMap");
        String path = ROOT +"ConceptMaps/ConceptMap-CareConnect-AdministrativeGender-1.xml";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/STU3/ConceptMap/CareConnect-AdministrativeGender-1";
        ConceptMap result = instance.getConceptMap(system);
        assertNotNull(result);
    }


    /**
     * Test of getNamingSystem method, of class HapiAssetCache.
     */
    @Test
    public void testGetNamingSystem() throws Exception {
        System.out.println("getNamingSystem");
        String path = ROOT +"NamingSystem/NamingSystem-NHSNumber.json";
        instance.addFile(path);
        String system = "NHS Number";
        NamingSystem result = instance.getNamingSystem(system);
        assertNotNull(result);
    }

    /**
     * Test of getSearchParameter method, of class HapiAssetCache.
     */
    @Test
    public void testGetSearchParameter() throws Exception {
        System.out.println("getSearchParameter");
        String path = ROOT +"SearchParameters/SearchParameter-ODSAPI-OrganizationRole-PrimaryRole.xml";
        instance.addFile(path);
        String system = "https://fhir.nhs.uk/STU3/SearchParameter/ODSAPI-OrganizationRole-PrimaryRole-1";
        SearchParameter result = instance.getSearchParameter(system);
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
        String path = ROOT +"CapabilityStatements/ODSAPI-CapabilityStatement-1.xml";
        instance.addFile(path);
    }

    /**
     * Test of addAll method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddAll() throws Exception {
        System.out.println("addAll");
        String d = ROOT +"CodeSystems";
        instance.addAll(d);
    }

    /**
     * Test of addResource method, of class HapiAssetCache.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddResource() throws Exception {
        System.out.println("addResource");
        String path = ROOT +"CapabilityStatements/ODSAPI-CapabilityStatement-1.xml";
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