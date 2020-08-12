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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class CachingIValidationSupportTest {

    private static HapiAssetCache p;
    private static final FhirContext THE_CONTEXT = FhirContext.forDstu3();
    private final static String ROOT = System.getenv("TKWROOT")+"/config/GP_CONNECT/validator_config/fhir_assets/STU3-FHIR-Assets/";

    private CachingIValidationSupport instance;
    
    public CachingIValidationSupportTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        p = new HapiAssetCache(THE_CONTEXT, new ArrayList<File>());
        for ( String s : new String[]{"CapabilityStatements","CodeSystems","ConceptMaps",/*"MessageDefinitions","Operations",
            "SearchParameters",*/"StructureDefinitions","ValueSets"}) {
            System.out.println(s);
            p.addAll(ROOT+s);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        instance = new CachingIValidationSupport();
        instance.setProfileCache(p);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setProfileCache method, of class CachingIValidationSupport.
     */
    @Test
    public void testSetProfileCache() {
        System.out.println("setProfileCache");
        instance.setProfileCache(p);
    }

    /**
     * Test of fetchResource method, of class CachingIValidationSupport.
     */
    @Test
    public void testFetchResource() {
        System.out.println("fetchResource");
        String expResult = "CareConnect-EMS-Organization-1";
        StructureDefinition result = (StructureDefinition) instance.fetchResource(THE_CONTEXT, StructureDefinition.class, 
                "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-EMS-Organization-1");
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of validateCode method, of class CachingIValidationSupport.
     * This is another hapifhir callback
     */
    @Test
    public void testValidateCode() {
        System.out.println("validateCode");
        String theCodeSystem = "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-LanguageAbilityMode-1";
        String theCode = "RWR";
        String theDisplay = "Recived written";
        IValidationSupport.CodeValidationResult result = instance.validateCode(THE_CONTEXT, theCodeSystem, theCode, theDisplay);
        assertNotNull(result);

        theCode = "XXX";
        theDisplay = "XXX";
        result = instance.validateCode(THE_CONTEXT, theCodeSystem, theCode, theDisplay);
        assertNotNull(result);
    }

    /**
     * Test of fetchCodeSystem method, of class CachingIValidationSupport.
     */
    @Test
    public void testFetchCodeSystem() {
        System.out.println("fetchCodeSystem");
        String theSystem = "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-LanguageAbilityMode-1";
        String expResult = "Care Connect Language Ability Mode";
        CodeSystem result = instance.fetchCodeSystem(THE_CONTEXT, theSystem);
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of expandValueSet method, of class CachingIValidationSupport.
     * This is boilerplate call back code for hapifhir
     * very difficult to mock up test scanario for this
     */
    @Test
    public void testExpandValueSet() {
        System.out.println("expandValueSet");
        ValueSet.ConceptSetComponent csc = null;
        ValueSet.ValueSetExpansionComponent expResult = null;
//      ValueSet.ValueSetExpansionComponent result = instance.expandValueSet(theContext, csc);
//      assertEquals(expResult, result);
    }

    /**
     * Test of fetchAllStructureDefinitions method, of class CachingIValidationSupport.
     */
    @Test
    public void testFetchAllStructureDefinitions() {
        System.out.println("fetchAllStructureDefinitions");
        List<StructureDefinition> result = instance.fetchAllStructureDefinitions(THE_CONTEXT);
        assertTrue(result.size() > 0);
    }

    /**
     * Test of fetchStructureDefinition method, of class CachingIValidationSupport.
     */
    @Test
    public void testFetchStructureDefinition() {
        System.out.println("fetchStructureDefinition");
        String string = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-EMS-Organization-1";
        String expResult = "CareConnect-EMS-Organization-1";
        StructureDefinition result = instance.fetchStructureDefinition(THE_CONTEXT, string);
        assertEquals(expResult, result.getName());
    }

    /**
     * Test of isCodeSystemSupported method, of class CachingIValidationSupport.
     */
    @Test
    public void testIsCodeSystemSupported() {
        System.out.println("isCodeSystemSupported");
        String string = "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-LanguageAbilityMode-1";
        boolean expResult = true;
        boolean result = instance.isCodeSystemSupported(THE_CONTEXT, string);
        assertEquals(expResult, result);

        string = "https://fhir.nhs.uk/STU3/CodeSystem/CareConnect-LanguageAbilityMode-1XXX";
        expResult = false;
        result = instance.isCodeSystemSupported(THE_CONTEXT, string);
        assertEquals(expResult, result);
    }

    /**
     * Test of fetchAllConformanceResources method, of class CachingIValidationSupport.
     */
    @Test
    public void testFetchAllConformanceResources() {
        System.out.println("fetchAllConformanceResources");
        List<IBaseResource> result = instance.fetchAllConformanceResources(THE_CONTEXT);
        assertTrue(result.size() > 0);
    }
    
}
