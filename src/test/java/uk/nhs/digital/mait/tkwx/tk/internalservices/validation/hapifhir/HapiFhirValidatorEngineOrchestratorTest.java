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
