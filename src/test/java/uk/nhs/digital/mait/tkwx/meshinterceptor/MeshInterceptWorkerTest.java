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
package uk.nhs.digital.mait.tkwx.meshinterceptor;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.mesh.MeshDataTest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.MESH_CTL_FILE;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.MESH_MESSAGE_FILE;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_FROM_DTS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_OUT_FOLDER;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_TO_DTS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.mesh.OpenMeshMessageRegister;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.REQUEST_ID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.TRANSACTIONAL_LOGS_IN_FOLDER;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.TRANSACTIONAL_LOGS_TO_FOLDER;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.MeshTransport;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest.makeMeshRequest;

/**
 *
 * @author simonfarrow
 */
public class MeshInterceptWorkerTest {

    private MeshInterceptWorker instance;
    private MeshRequest request;
    private static final OpenMeshMessageRegister ommr = OpenMeshMessageRegister.getInstance();

    public MeshInterceptWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, Exception {
        MeshDataTest.setUpClass();
        System.setProperty("tks.MeshTransport.reporttimeoutperiod", "43200"); // half a day
        System.setProperty(SAVEDMESSAGES_PROPERTY, SAVED_MESSAGES_FOLDER);
    }

    @AfterClass
    public static void tearDownClass() {
        for (File f : new File[]{new File(SAVED_MESSAGES_FOLDER + "/" + TEST_MESH_FROM_DTS), new File(SAVED_MESSAGES_FOLDER + "/" + TEST_MESH_TO_DTS)}) {
            f.delete();
        }
        MeshDataTest.tearDownClass();
    }

    @Before
    public void setUp() throws Exception {
        commonSetup();

        // ensure the singleton is in the correct initial state before a test starts
        // avoids  testProcessForwarder fail when run as part of suite
        MeshMessage mm = ommr.getOpenMeshMessage(REQUEST_ID);
        if (mm !=null){
            ommr.deRegisterMeshRequest(REQUEST_ID);
        }
        
        MeshInterceptHandler mi = new MeshInterceptHandler();
        mi.setToolkit(new MeshTransport());
        instance = new MeshInterceptWorker(mi);
        request = makeMeshRequest();
    }

    @After
    public void tearDown() throws IOException {
        for (String folder : new String[]{TRANSACTIONAL_LOGS_IN_FOLDER.getParent(), TRANSACTIONAL_LOGS_TO_FOLDER.getParent()}) {
            deleteFolderAndContents(folder);
        }
        commonTeardown();
    }

    /**
     * Test of processIncomingRequest method, of class MeshInterceptWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessIncomingRequest() throws Exception {
        System.out.println("processIncomingRequest");
        instance.processIncomingRequest(request);

        // writes a transactional log to the to_dts in log folder
        assertEquals(1, TRANSACTIONAL_LOGS_IN_FOLDER.listFiles().length);
        assertEquals(true, TRANSACTIONAL_LOGS_IN_FOLDER.listFiles()[0].getName().matches("^" + REQUEST_ID + "_[0-9]{17}\\.log$"));
    }

    /**
     * Test of processForwarder method, of class MeshInterceptWorker.
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessForwarder() throws Exception {
        System.out.println("processForwarder");
        instance.processForwarder(request);

        // writes a transactional log to the to dts to log folder
        if (TRANSACTIONAL_LOGS_TO_FOLDER.exists()) {
            assertEquals(1, TRANSACTIONAL_LOGS_TO_FOLDER.listFiles().length);
        } else {
            fail("Transactional logs to folder " + TRANSACTIONAL_LOGS_TO_FOLDER.getCanonicalPath() + " does not exist");
        }

        assertEquals(true, TRANSACTIONAL_LOGS_TO_FOLDER.listFiles()[0].getName().matches("^" + REQUEST_ID + "_[0-9]{17}\\.log$"));

        // copies the mesh data and control files to the Mesh out folder
        File meshOutFolder = new File(TEST_MESH_OUT_FOLDER);
        // pair of dat and ctl
        assertEquals(2, meshOutFolder.listFiles().length);
        for (String fn : meshOutFolder.list()) {
            if (fn.endsWith(".dat")) {
                assertEquals(MESH_MESSAGE_FILE, fn);
            } else {
                assertEquals(MESH_CTL_FILE, fn);
            }
        }
    }

    /**
     * Test of readDataFile method, of class MeshInterceptWorker. This is a
     * protected method that is called by both processForwarder and
     * processIncomingRequest Its not practical to test this method in an
     * isolated fashion. Test left in so that automated test builds don't
     * continually keep recreating it
     */
    @Test
    public void testReadDataFile() {
        System.out.println("readDataFile");
    }
}
