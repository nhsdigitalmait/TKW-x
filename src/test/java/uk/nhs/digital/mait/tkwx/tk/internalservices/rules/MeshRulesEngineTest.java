/*
 Copyright 2017  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Node;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest.makeMeshRequest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_OUT_FOLDER;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class MeshRulesEngineTest {

    private MeshRulesEngine instance;
    private final static String MESH_RULES_FILE = System.getenv("TKWROOT")+"/config/FHIR_MESH/simulator_config/test_tks_rule_config-x.txt";
    
    public MeshRulesEngineTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.setUpClass();
    }
    
    @AfterClass
    public static void tearDownClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.tearDownClass();
    }
    
    @Before
    public void setUp() throws IOException {
        // This loads src/test/resources/ITK_Infrastructure_Acknowledgement_Bundle_Success.xml
        commonSetup();
        instance = new MeshRulesEngine();
    }
    
    @After
    public void tearDown() throws IOException {
        commonTeardown();
    }

    /**
     * Test of load method, of class MeshRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        instance.load(MESH_RULES_FILE);
    }

    /**
     * Test of execute method, of class MeshRulesEngine.
     * @throws java.lang.Exception
     */
    @Test (expected=UnsupportedOperationException.class)
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String soapAction = "";
        String input = "";
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(soapAction, input);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class MeshRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Node() throws Exception {
        System.out.println("execute");
        ServiceResponse s = new ServiceResponse();
        // hard coded 0 response
        String type = "";
        Node input = null;
        int expResult = 0;
        ServiceResponse result = instance.execute(type, input);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of instantiateResponse method, of class MeshRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse s = new ServiceResponse();
        // hard coded 0 response
        String body = "";
        int expResult = 0;
        ServiceResponse result = instance.instantiateResponse(s, body);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class MeshRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_5args() throws Exception {
        System.out.println("execute");
        instance.load(MESH_RULES_FILE);

        /*
        ITK002M  ITK Bus Ack
        ITK008M  ITK Infrastructure Ack
        
        ITK003D  Outpatient letter
        ITK004D  Emergency Care discharge
        ITK005D  Mental Health Discharge
        ITK006D  ITK Discharge
        */
        
        MeshRequest req = makeMeshRequest();
        String eventCode = "ITK008M";

        // equivalent to an interactiopn id
        req.setHeader("eventCode",eventCode);
        
        // fires ruleset:ITK008M rule: incoming_busack_inf, response inf_ack_only_20013
        // class MESHFHIRITKRoutingActorInfAckOnly response code 20013
        ServiceResponse result = instance.execute(req);
        
        String expResult = "200 OK";
        assertEquals(expResult, result.getCodePhrase());
        
        assertEquals(0, result.getCode());
        
        File outFolder = new File(TEST_MESH_OUT_FOLDER);
        // check for a pair of dat ctl files for the response in the out folder
        assertEquals(2,outFolder.listFiles().length);
        
        // check the generated response code
        for (File file : outFolder.listFiles()) {
            System.out.println(file.getName());
            if (file.getName().endsWith(".dat")){
                String content = readFile2String(file.getCanonicalPath());
                String ec = xpathExtractor("//fhir:coding[fhir:system/@value=\"https://fhir.nhs.uk/STU3/CodeSystem/ITK-ResponseCodes-1\"]/fhir:code/@value",content);
                expResult = "20013";
                assertEquals(expResult, ec);
            }
        }
    }

    /**
     * Test of isRestful method, of class MeshRulesEngine.
     */
    @Test
    public void testIsRestful() {
        System.out.println("isRestful");
        boolean expResult = false;
        boolean result = instance.isRestful();
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class MeshRulesEngine.
     * see testExecute_5args
     */
    @Test
    public void testExecute_Request() throws Exception {
        System.out.println("execute");
    }
    
}
