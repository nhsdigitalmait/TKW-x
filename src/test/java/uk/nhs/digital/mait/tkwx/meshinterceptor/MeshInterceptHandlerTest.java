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
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.mesh.MeshControlStatusTest.TEST_MESH_CTL_WITH_STATUS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshControlStatusTest.createControlFileWithStatus;
import static uk.nhs.digital.mait.tkwx.mesh.MeshControlStatusTest.deleteControlFileWithStatus;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_CTL;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_FROM_DTS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_TO_DTS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;
import uk.nhs.digital.mait.tkwx.mesh.OpenMeshMessageRegister;
import uk.nhs.digital.mait.tkwx.tk.boot.MeshTransport;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 *
 * @author simonfarrow
 */
public class MeshInterceptHandlerTest {

    public final static String SAVED_MESSAGES_FOLDER = "src/test/resources";
    public final static String MAILBOXID = "mailboxid";
//  public final static String MAILBOXID = "NOWOT00X";
    public final static File TRANSACTIONAL_LOGS_IN_FOLDER = new File(SAVED_MESSAGES_FOLDER + "/" + TEST_MESH_FROM_DTS + "/transactional_logs");
    public final static File TRANSACTIONAL_LOGS_TO_FOLDER = new File(SAVED_MESSAGES_FOLDER + "/" + TEST_MESH_TO_DTS + "/transactional_logs");

    // from the inf ack template file
    public static final String REQUEST_ID = "__REQUEST_UUID__";

    private MeshInterceptHandler instance;

    public MeshInterceptHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, Exception {
        MESHFHIRITKRoutingActorBusAckOnlyTest.setUpClass();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.tearDownClass();
    }

    @Before
    public void setUp() throws Exception {
        commonSetup();

        // ensure a validation service is available
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/FHIR_MESH/tkw-x.properties");
        SimulatorMode m = new SimulatorMode();
        m.init(t);
        Configurator configurator = Configurator.getConfigurator();
        configurator.setConfiguration("tks.MeshTransport." + MAILBOXID + ".out", "doesnt matter");

        instance = new MeshInterceptHandler();

        instance.setSavedMessagesDirectory(SAVED_MESSAGES_FOLDER);
        instance.setMESHMailboxId(MAILBOXID);
    }

    @After
    public void tearDown() throws IOException {
        if (TRANSACTIONAL_LOGS_IN_FOLDER.exists()) {
            deleteFolderAndContents(TRANSACTIONAL_LOGS_IN_FOLDER);
            new File(SAVED_MESSAGES_FOLDER + "/" + TEST_MESH_FROM_DTS).delete();
        }
        if (TRANSACTIONAL_LOGS_TO_FOLDER.exists()) {
            deleteFolderAndContents(TRANSACTIONAL_LOGS_TO_FOLDER);
            new File(SAVED_MESSAGES_FOLDER + "/" + TEST_MESH_TO_DTS).delete();
        }
        commonTeardown();
    }

    /**
     * Test of setToolkit method, of class MeshInterceptHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetToolkit() throws Exception {
        System.out.println("setToolkit");
        MeshTransport t = new MeshTransport();
        instance.setToolkit(t);
    }

    /**
     * Test of handle method, of class MeshInterceptHandler. Currently there is
     * no validator service set so we get a warning when this is run
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandleIn() throws Exception {
        System.out.println("handleIn");

        // the files are actually in src/test/resources/ not any particular mailbox but the type is only tested for its suffix nothing else
        String mailboxType = "tks.MeshTransport." + MAILBOXID + ".in";
        Path ctlFile = Paths.get(TEST_MESH_CTL);
        instance.handle(mailboxType, ctlFile);

        // X26OT020 is the from dts
        // there should be a folder src/test/resources/X26OT020/transactional_logs containing 1 request log file
        assertTrue(TRANSACTIONAL_LOGS_IN_FOLDER.exists());
        assertEquals(1, TRANSACTIONAL_LOGS_IN_FOLDER.listFiles().length);
        assertEquals(true, TRANSACTIONAL_LOGS_IN_FOLDER.listFiles()[0].getName().matches("^" + REQUEST_ID + "_[0-9]{17}\\.log$"));

    }

    /**
     * Test of handle method, of class MeshInterceptHandler. Currently there is
     * no validator service set so we get a warning when this is run NB The
     * .forwarder type registers the MeshMessage with MeshMessageRegister but
     * the .in type as above does not
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandleForwarder() throws Exception {
        System.out.println("handleForwarder");
        // the files are actually in src/test/resources/ not any particular mailbox but the type is only tested for its suffix nothing else

        String mailboxType = "tks.MeshTransport." + MAILBOXID + ".forwarder";
        Path ctlFile = Paths.get(TEST_MESH_CTL);
        instance.handle(mailboxType, ctlFile);

        // __TO_DTS__ is to dts
        // there should be a folder src/test/resources/__TO_DTS__/transactional_logs containing 1 request log file
        assertTrue(TRANSACTIONAL_LOGS_TO_FOLDER.exists());
        assertEquals(1, TRANSACTIONAL_LOGS_TO_FOLDER.listFiles().length);
        // prefix is from the sample mesh message 
        assertEquals(true, TRANSACTIONAL_LOGS_TO_FOLDER.listFiles()[0].getName().matches("^" + REQUEST_ID + "_[0-9]{17}\\.log$"));

        OpenMeshMessageRegister ommr = OpenMeshMessageRegister.getInstance();
        MeshMessage message = ommr.getOpenMeshMessage(REQUEST_ID);
        // the forwarder should have registered the message with the register
        assertNotNull(message);
        assertEquals(REQUEST_ID, message.getOriginatingRequestId());
        // ensure singleton is cleared out every time the forwarder  is run otherwise stuff fails
        // this behaviour had been masked until we upped the timer delay from 10s to half a day
        // at which point it became clear that hangdleIn and handleForward behaved differently when run as part of the test suite
        // because the handleIn was called after the forwarder when the singleton had a registered message stored
        ommr.deRegisterMeshRequest(message.getOriginatingRequestId());
    }

    /**
     * Test of handle method, of class MeshInterceptHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandleSent() throws Exception {
        System.out.println("handleSent");

        createControlFileWithStatus();

        // the files are actually in src/test/resources/ not any particular mailbox but the type is only tested for its suffix nothing else
        String mailboxType = "tks.MeshTransport." + MAILBOXID + ".sent";
        Path ctlFile = Paths.get(TEST_MESH_CTL_WITH_STATUS);
        instance.handle(mailboxType, ctlFile);

        // __TO_DTS__ is the to dts
        // there should be a folder src/test/resources/__TO_DTS__/transactional_logs containing 1 log file with a sentbox transfer report
        assertTrue(TRANSACTIONAL_LOGS_TO_FOLDER.exists());
        assertEquals(1, TRANSACTIONAL_LOGS_TO_FOLDER.listFiles().length);
        assertEquals(true, TRANSACTIONAL_LOGS_TO_FOLDER.listFiles()[0].getName().matches("^[0-9]{17}\\.log$"));

        deleteControlFileWithStatus();
    }

    /**
     * Test of makeLogFile method, of class MeshInterceptHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeLogFile() throws Exception {
        System.out.println("makeLogFile");
        String logDirectory = SAVED_MESSAGES_FOLDER;
        String fromDTS = TEST_MESH_FROM_DTS;
        String requestUuid = "rquuid";
        String result = instance.makeLogFile(logDirectory, fromDTS, requestUuid).toString();
        assertTrue(result.startsWith(logDirectory));
        assertTrue(result.contains("/" + fromDTS + "/"));
        assertTrue(result.contains("/" + requestUuid));
        assertTrue(TRANSACTIONAL_LOGS_IN_FOLDER.exists());
    }

    /**
     * Test of getLogDirString method, of class MeshInterceptHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLogDirString() throws Exception {
        System.out.println("getLogDirString");
        String logDirectory = "dir";
        String fromDTS = "fromdts";
        String requestUuid = "rquuid";
        String result = instance.getLogDirString(logDirectory, fromDTS, requestUuid);
        assertTrue(result.startsWith(logDirectory));
        assertTrue(result.contains("/" + fromDTS + "/"));
        assertTrue(result.contains("/" + requestUuid));
    }

    /**
     * broken out into three sub tests (in, forwarder and sent) added to stop
     * auto generation issues Test of handle method, of class
     * MeshInterceptHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
    }

}
