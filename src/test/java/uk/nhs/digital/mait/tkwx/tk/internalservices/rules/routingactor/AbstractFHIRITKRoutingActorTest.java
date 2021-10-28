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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class AbstractFHIRITKRoutingActorTest {

    private AbstractFHIRITKRoutingActorImpl instance;
    
    public AbstractFHIRITKRoutingActorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        instance = new AbstractFHIRITKRoutingActorImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class AbstractFHIRITKRoutingActor.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        System.setProperty("tks.routingactor.fhir.appacktemplate",System.getenv("TKWROOT")+"/config/FHIR_REST/simulator_config/fhir_routing_actor_templates/ITK-InfAck-Success-Example-1.xml");
        System.setProperty("tks.routingactor.fhir.busacktemplate",System.getenv("TKWROOT")+"/config/FHIR_REST/simulator_config/fhir_routing_actor_templates/ITK-InfAck-Success-Example-1.xml");
        instance.init();
    }

    /**
     * Test of setErrorCode method, of class AbstractFHIRITKRoutingActor.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetErrorCode() throws Exception {
        System.out.println("setErrorCode");
        String errorLabel = "";
        instance.setErrorCode(errorLabel);
    }

    public class AbstractFHIRITKRoutingActorImpl extends AbstractFHIRITKRoutingActor {

        public AbstractFHIRITKRoutingActorImpl() throws Exception {
            super();
        }

        @Override
        public String makeResponse(HashMap<String,Substitution> substitutions, Object o) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
