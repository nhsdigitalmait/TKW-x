/*
  Copyright 2012  Damian Murphy murff@warlock.org

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.mesh.MeshData;
import uk.nhs.digital.mait.tkwx.mesh.MeshDataTest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.MESH_MESSAGE;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_CTL;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_DAT;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_TEMP_FOLDER;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_OUT_FOLDER;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;

/**
 *
 * @author simonfarrow
 */
public class MESHFHIRITKRoutingActorBusAckOnlyTest {

    private static final String XREF_FILE = System.getenv("TKWROOT") + "/config/FHIR_MESH/simulator_config/MeshWorkflowIdXRef.txt";

    public MESHFHIRITKRoutingActorBusAckOnlyTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        MeshDataTest.setUpClass();
        System.setProperty(ORG_CONFIGURATOR, ORG_DEFAULT_SYSTEM_PROPERTIES_CONFIGURATOR);

        System.setProperty("tks.MeshTransport.workflowidxreffile", XREF_FILE);
        System.setProperty("tks.MeshTransport.tkwintermediarytempdir", TEST_MESH_TEMP_FOLDER);

        System.setProperty("tks.MeshTransport." + MAILBOXID + ".out", TEST_MESH_OUT_FOLDER);
        System.setProperty("tks.MeshTransport.siteid", "siteid");
        System.setProperty("tks.routingactor.fhir.appacktemplate", System.getenv("TKWROOT") + "/config/FHIR_MESH/simulator_config/ITK_Business_Acknowledgement_Bundle_Success.xml");
        System.setProperty("tks.routingactor.fhir.busacktemplate", System.getenv("TKWROOT") + "/config/FHIR_MESH/simulator_config/ITK_Infrastructure_Acknowledgement_Bundle_Success.xml");

        System.setProperty("tks.MeshTransport.reporttimeoutperiod", "43200"); // 12 hours

        // TODO  these codeless defaults seem to be required but the working code doesn't appear to set them
        System.setProperty("tks.routingactor.fhir.ia.errorcode", "100");
        System.setProperty("tks.routingactor.fhir.ia.errortext", "ert");
        System.setProperty("tks.routingactor.fhir.ia.responsecode", "100");
        System.setProperty("tks.routingactor.fhir.ia.issuetext", "it");
        System.setProperty("tks.routingactor.fhir.ia.issueseverity", "is");
        System.setProperty("tks.routingactor.fhir.ia.infackrequested", "irq");

        System.setProperty("tks.routingactor.fhir.ba.errorcode", "100");
        System.setProperty("tks.routingactor.fhir.ba.errortext", "ert");
        System.setProperty("tks.routingactor.fhir.ba.responsecode", "100");
        System.setProperty("tks.routingactor.fhir.ba.issuetext", "it");
        System.setProperty("tks.routingactor.fhir.ba.issueseverity", "is");

        // load up all the routing actor properties set in the FHIR_MESH props file
        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getenv("TKWROOT") + "/config/FHIR_MESH/tkw-x.properties"));
        for (Object prop : properties.keySet()) {
            String key = (String) prop;
            if (key.startsWith("tks.routingactor.fhir.")) {
                System.setProperty(key, properties.getProperty(key));
            } else {
                //System.out.println("Skipping " + key);
                if (System.getProperty(key) != null && !System.getProperty(key).equals(properties.getProperty(key))) {
                    //System.out.println(key + " clash: \t" + System.getProperty(key) + " would become: \r\n\t" + properties.getProperty(key));
                }
            }
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        MeshDataTest.tearDownClass();
    }

    @Before
    public void setUp() throws IOException {
        commonSetup();
    }

    @After
    public void tearDown() throws IOException {
        commonTeardown();
    }

    /**
     * Test of makeResponse method, of class MESHFHIRITKRoutingActorBusAckOnly.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String,Substitution> substitutions = new HashMap<>();
        MeshRequest mr = makeMeshRequest();
        MESHFHIRITKRoutingActorBusAckOnly instance = new MESHFHIRITKRoutingActorBusAckOnly();
        String expResult = "";
        String result = instance.makeResponse(substitutions, mr);
        assertEquals(expResult, result);

        // 1 pair of dat and ctl for bus
        File of = new File(TEST_MESH_OUT_FOLDER);
        assertEquals(2, of.listFiles().length);
    }

    /**
     * utility method
     *
     * @return valid constructed MeshRequest object
     * @throws Exception
     */
    public static MeshRequest makeMeshRequest() throws Exception {
        MeshMessage mm = new MeshMessage(MAILBOXID);
        MeshData md = new MeshData(mm);
        md.setDataFilePath(Paths.get(TEST_MESH_DAT));
        md.createMeshData(MESH_MESSAGE);
        mm.setData(md);
        mm.parseControl(Paths.get(TEST_MESH_CTL));
        MeshRequest mr = new MeshRequest();
        mr.setRequestMessage(mm);
        return mr;
    }

}
