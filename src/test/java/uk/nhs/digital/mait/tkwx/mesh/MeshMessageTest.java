/*
 Copyright 2015  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.mesh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.MESH_CTL_FILE;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.MESH_MESSAGE;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.MESH_MESSAGE_FILE;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_CTL;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_DAT;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_TEMP_FOLDER;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_OUT_FOLDER;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;

/**
 *
 * @author simonfarrow
 */
public class MeshMessageTest {

    private MeshMessage instance;
    private static final String LOGGING_FILE = "src/test/resources/fos.txt";

    public MeshMessageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        MeshDataTest.setUpClass();
    }

    @AfterClass
    public static void tearDownClass() {
        MeshDataTest.tearDownClass();
    }

    @Before
    public void setUp() throws Exception {
        commonSetup();
        
        instance = new MeshMessage(MAILBOXID);

        // setup for writing which involves parsing the ctl file
        MeshData meshData = new MeshData(instance);
        meshData.createMeshData(MESH_MESSAGE, Paths.get(TEST_MESH_DAT));

        instance.setData(meshData);
        instance.parseControl(Paths.get(TEST_MESH_CTL));
    }

    @After
    public void tearDown() throws IOException {
        File f = new File(LOGGING_FILE);
        if (f.exists()) {
            f.delete();
        }
        commonTeardown();
    }

    /**
     * Test of parseControl method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseControl() throws Exception {
        System.out.println("parseControl");
        Path cfile = Paths.get(TEST_MESH_CTL);
        instance.parseControl(cfile);
    }

    /**
     * Test of parseData method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseData() throws Exception {
        System.out.println("parseData");
        setupForParse();
        instance.parseData();
    }

    /**
     * Test of createResponse method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateResponse() throws Exception {
        System.out.println("createResponse");
        String toDTS = "todts";
        String message = "<resp>yyyy</resp>";
        String requestingWorkflowId = "TOC_DISCH_DMS";
        String localId = "locid";
        File outfolder = new File(TEST_MESH_OUT_FOLDER);
        assertEquals(0,outfolder.listFiles().length);
        instance.createResponse(toDTS, message, requestingWorkflowId, localId);
        // there should be two files in the outbox named for a siteidsuffixed with a guid and .ctl or .dat
        assertEquals(2,outfolder.listFiles().length);
        for ( File f : outfolder.listFiles()){
            assertTrue(f.getName().startsWith("siteid"));
            assertTrue(f.getName().matches("^.*\\.(ctl|dat)$"));
        }
    }

    /**
     * Test of saveToTempDirectory method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSaveToTempDirectory() throws Exception {
        System.out.println("saveToTempDirectory");
        String content = MESH_MESSAGE;
        String extension = ".dat";
        String expResult = TEST_MESH_TEMP_FOLDER+"/"+MESH_MESSAGE_FILE;
        setupForParse();
        assertFalse(fileExists(expResult));
        Path result = instance.saveToTempDirectory(content, extension);
        assertEquals(expResult, result.toString());
        assertTrue(fileExists(expResult));
    }

    /**
     * Test of getMeshFileName method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetMeshFileName() throws Exception {
        System.out.println("getMeshFileName");
        setupForParse();

        String expResult = "mesh";
        String result = instance.getMeshFileName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getControlFile method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetControlFile() throws Exception {
        System.out.println("getControlFile");
        String expResult = "Data";
        setupForParse();
        MeshControlFile result = instance.getControlFile();
        assertEquals(expResult, result.getMessageType());
    }

    private void setupForParse() throws Exception {
        instance = new MeshMessage(MAILBOXID);
        // NB this must be called to populate the control file before derving the dat file location
        instance.parseControl(Paths.get(TEST_MESH_CTL));
    }

    /**
     * Test of getDataFile method, of class MeshMessage.
     */
    @Test
    public void testGetDataFile() {
        System.out.println("getDataFile");
        MeshData result = instance.getDataFile();
        assertArrayEquals(MESH_MESSAGE.getBytes(), result.getContent());
    }

    /**
     * Test of setData method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        MeshData data = new MeshData(new MeshMessage(MAILBOXID));
        data.createMeshData(MESH_MESSAGE, Paths.get(TEST_MESH_DAT));
        instance.setData(data);
    }

    /**
     * Test of setLogFileWriter method, of class MeshMessage.
     */
    @Test
    public void testSetLogFileWriter() {
        System.out.println("setLogFileWriter");
        LoggingFileOutputStream lfos = null;
        instance.setLoggingStream(lfos);
    }

    /**
     * Test of log method, of class MeshMessage.
     */
    @Test
    public void testLog() {
        System.out.println("log");
        String s = "logmsg";
        int numberOfNewLines = 0;
        boolean flush = false;
        instance.log(s, numberOfNewLines, flush);
    }

    /**
     * Test of setOriginatingRequestId method, of class MeshMessage.
     */
    @Test
    public void testSetOriginatingRequestId() {
        System.out.println("setOriginatingRequestId");
        String requestId = "";
        instance.setOriginatingRequestId(requestId);
    }

    /**
     * Test of getOriginatingRequestId method, of class MeshMessage.
     */
    @Test
    public void testGetOriginatingRequestId() {
        System.out.println("getOriginatingRequestId");
        String expResult = "orqid";
        instance.setOriginatingRequestId(expResult);
        String result = instance.getOriginatingRequestId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMeshMailboxId method, of class MeshMessage.
     */
    @Test
    public void testGetMeshMailboxId() {
        System.out.println("getMeshMailboxId");
        String expResult = MAILBOXID;
        String result = instance.getMeshMailboxId();
        assertEquals(expResult, result);
    }

    /**
     * Test of createRequest method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateRequest() throws Exception {
        System.out.println("createRequest");
        String id = "workflowid";
        File dat = new File(TEST_MESH_OUT_FOLDER+"/"+MESH_MESSAGE_FILE);
        File ctl = new File(TEST_MESH_OUT_FOLDER+"/"+MESH_CTL_FILE);
        
        assertFalse(dat.exists());
        assertFalse(ctl.exists());
        instance.createRequest(id);
        assertTrue(dat.exists());
        assertTrue(ctl.exists());
    }

    /**
     * Test of moveToOutboxDirectory method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMoveToOutboxDirectory() throws Exception {
        System.out.println("moveToOutboxDirectory");
        File b1 =  new File(TEST_MESH_TEMP_FOLDER+"/b1");
        try (FileWriter fw = new FileWriter(b1.getAbsolutePath())) {
            fw.write("xx");
        }
        assertTrue(b1.exists());
        
        Path p = Paths.get(b1.getAbsolutePath());
        instance.moveToOutboxDirectory(p);
        assertFalse(b1.exists());
        assertTrue(fileExists(TEST_MESH_OUT_FOLDER+"/b1"));
    }

    /**
     * Test of deleteFile method, of class MeshMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDeleteFile() throws Exception {
        System.out.println("deleteFile");
        try (FileWriter fw = new FileWriter(TEST_FILE_TO_DELETE)) {
            fw.write("xxx");
        }

        Path filePath = Paths.get(TEST_FILE_TO_DELETE);
        File ftodelete = new File(TEST_FILE_TO_DELETE);
        assertTrue(ftodelete.exists());
        instance.deleteFile(filePath);
        assertTrue(!ftodelete.exists());
    }
    private static final String TEST_FILE_TO_DELETE = SAVED_MESSAGES_FOLDER+"/ftodlete";

    /**
     * Test of getLogFileWriter method, of class MeshMessage.
     */
    @Test
    public void testGetLogFileWriter() {
        System.out.println("getLogFileWriter");
        LoggingFileOutputStream expResult = null;
        LoggingFileOutputStream result = instance.getLoggingStream();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLoggingStream method, of class MeshMessage.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testSetLoggingStream() throws FileNotFoundException {
        System.out.println("setLoggingStream");
        LoggingFileOutputStream lfos = new LoggingFileOutputStream(LOGGING_FILE);
        instance.setLoggingStream(lfos);
    }

    /**
     * Test of getLoggingStream method, of class MeshMessage.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testGetLoggingStream() throws FileNotFoundException {
        System.out.println("getLoggingStream");
        LoggingFileOutputStream expResult = new LoggingFileOutputStream(LOGGING_FILE);
        instance.setLoggingStream(expResult);
        LoggingFileOutputStream result = instance.getLoggingStream();
        assertEquals(expResult, result);
    }

    /**
     * Test of closeLog method, of class MeshMessage.
     */
    @Test
    public void testCloseLog() throws FileNotFoundException {
        System.out.println("closeLog");
        LoggingFileOutputStream lfos = new LoggingFileOutputStream(LOGGING_FILE);
        instance.setLoggingStream(lfos);
        instance.closeLog();
    }

}
