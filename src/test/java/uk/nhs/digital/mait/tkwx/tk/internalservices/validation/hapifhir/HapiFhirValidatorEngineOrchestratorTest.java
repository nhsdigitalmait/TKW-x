/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author riro
 */
public class HapiFhirValidatorEngineOrchestratorTest {
    
    public HapiFhirValidatorEngineOrchestratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class HapiFhirValidatorEngineOrchestrator.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        HapiFhirValidatorEngineOrchestrator result = HapiFhirValidatorEngineOrchestrator.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of getEngine method, of class HapiFhirValidatorEngineOrchestrator.
     */
    @Test
    public void testGetEngine() {
        System.out.println("getEngine");
        String hapiFhirValidatorInstanceName = null;
        HapiFhirValidatorEngineOrchestrator instance = HapiFhirValidatorEngineOrchestrator.getInstance();
        HapiFhirValidatorEngine result = instance.getEngine(hapiFhirValidatorInstanceName);
        assertNotNull(result);
    }
    
}
