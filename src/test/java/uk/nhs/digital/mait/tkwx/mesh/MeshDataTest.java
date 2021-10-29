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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;

/**
 *
 * @author riro
 */
@Category(RestartJVMTest.class)
public class MeshDataTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    public static final String MESH_MESSAGE_FILE = "mesh.dat";
    public static final String MESH_CTL_FILE = "mesh.ctl";
    public static final String TEST_MESH_CTL = new File(SAVED_MESSAGES_FOLDER + "/" + MESH_CTL_FILE).getAbsolutePath();
    public static final String TEST_MESH_DAT = new File(SAVED_MESSAGES_FOLDER + "/" + MESH_MESSAGE_FILE).getAbsolutePath();
    public static final String TEST_MESH_TEMP_FOLDER = SAVED_MESSAGES_FOLDER + "/meshtempfolder";
    public static final String TEST_MESH_OUT_FOLDER = SAVED_MESSAGES_FOLDER + "/meshoutfolder";
    public static final String TEST_MESH_FROM_DTS = "X26OT020";
    public static final String TEST_MESH_TO_DTS = "__TO_DTS__";
    private static final String XREF_FILE = System.getenv("TKWROOT") + "/config/FHIR_MESH/simulator_config/MeshWorkflowIdXRef.txt";
    public static String MESH_MESSAGE;
    private MeshData instance;
    private MeshMessage mm;

    public MeshDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        MESH_MESSAGE = new String(Files.readAllBytes(Paths.get(SAVED_MESSAGES_FOLDER + "/ITK_Infrastructure_Acknowledgement_Bundle_Success.xml")));
//      MESH_MESSAGE = new String(Files.readAllBytes(Paths.get(System.getenv("TKWROOT") + "/config/FHIR_MESH/simulator_config/ITK_Infrastructure_Acknowledgement_Bundle_Success.xml")));
//      MESH_MESSAGE = new String(Files.readAllBytes(Paths.get(System.getenv("TKWROOT") + "/config/FHIR_MESH/simulator_config/ITK_Business_Acknowledgement_Bundle_Success.xml")));
        System.setProperty(ORG_CONFIGURATOR, ORG_DEFAULT_SYSTEM_PROPERTIES_CONFIGURATOR);
        System.setProperty("tks.MeshTransport.workflowidxreffile", XREF_FILE);
        System.setProperty("tks.MeshTransport.tkwintermediarytempdir", TEST_MESH_TEMP_FOLDER);

        System.setProperty("tks.MeshTransport." + MAILBOXID + ".out", TEST_MESH_OUT_FOLDER);
        System.setProperty("tks.MeshTransport.siteid", "siteid");
    }

    @AfterClass
    /**
     * deletes control and data file
     */
    public static void tearDownClass() {
        new File(TEST_MESH_CTL).delete();
        new File(TEST_MESH_DAT).delete();
    }

    /**
     * clears out temp and out folders
     *
     * @throws java.io.IOException
     */
    public static void commonTeardown() throws IOException {
        for (String folderName : new String[]{TEST_MESH_TEMP_FOLDER, TEST_MESH_OUT_FOLDER}) {
            deleteFolderAndContents(folderName);
        }
    }

    /**
     * recursively delete files and folders required because simulator
     * validation files can now be placed in sub folders named for end points
     *
     * @param folder File
     * @throws IOException
     */
    public static void deleteFolderAndContents(File folder) throws IOException {
        if (folder.isDirectory() && folder.listFiles() != null) {
            for (File f : folder.listFiles()) {
                if (f.isFile()) {
                    if (!f.delete()) {
                        System.err.println("Error deleting file " + f.getCanonicalPath());
                    }
                } else {
                    deleteFolderAndContents(f);
                }
            }
            if (folder.exists()) {
                if (!folder.delete()) {
                    System.err.println("Error deleting folder " + folder.getCanonicalPath());
                }
            }
        }
    }

    /**
     * overload
     *
     * @param folderName String
     * @throws IOException
     */
    public static void deleteFolderAndContents(String folderName) throws IOException {
        deleteFolderAndContents(new File(folderName));
    }

    @Before
    /**
     * sets up temp and out folders and creates MeshMessage and MeshData objects
     */
    public void setUp() throws IOException, Exception {

        commonSetup();

        // This setup is for creating a Data file not reading / parsing an existing one
        mm = new MeshMessage(MAILBOXID);
        instance = new MeshData(mm);
        instance.createMeshData(MESH_MESSAGE, Paths.get(TEST_MESH_DAT));
    }

    /**
     * Creates mesh temp and mesh out folders Writes a mesh control file and
     * test message to the SAVED_MESSAGES_FOLDER
     *
     * @throws IOException
     */
    public static void commonSetup() throws IOException {
        new File(TEST_MESH_TEMP_FOLDER).mkdirs();
        new File(TEST_MESH_OUT_FOLDER).mkdirs();
        try (FileWriter fw = new FileWriter(TEST_MESH_CTL)) {
            fw.write("<DTSControl>\n"
                    + "	<Version>1.0</Version>\n"
                    + "	<AddressType>DTS</AddressType>\n"
                    + "	<MessageType>Data</MessageType>\n"
                    + "	<WorkflowId>TOC_DISCH_DMS</WorkflowId>\n"
                    + "	<To_DTS>" + TEST_MESH_TO_DTS + "</To_DTS>\n"
                    + "	<From_DTS>" + TEST_MESH_FROM_DTS + "</From_DTS>\n"
                    + "	<Compress>N</Compress>\n"
                    + "	<LocalId>localid</LocalId>\n"
                    + "</DTSControl>");
        }

        try (FileWriter fw = new FileWriter(TEST_MESH_DAT)) {
            fw.write(MESH_MESSAGE);
        }
    }

    @After
    public void tearDown() throws IOException {
        commonTeardown();
    }

    /**
     * Test of getContent method, of class MeshData.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetContent() throws Exception {
        System.out.println("getContent");

        byte[] expResult = MESH_MESSAGE.getBytes();
        byte[] result = instance.getContent();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of logDataFile method, of class MeshData.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLogDataFile() throws Exception {
        System.out.println("logDataFile");
        instance.logDataFile("type");
    }

    /**
     * Test of getContentLength method, of class MeshData.
     */
    @Test
    public void testGetContentLength() {
        System.out.println("getContentLength");
        int expResult = MESH_MESSAGE.length();
        int result = instance.getContentLength();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDataFilePath method, of class MeshData.
     */
    @Test
    public void testGetDataFilePath() {
        System.out.println("getDataFilePath");
        Path expResult = Paths.get(TEST_MESH_DAT);
        Path result = instance.getDataFilePath();
        assertEquals(expResult, result);
    }

    /**
     * Test of createMeshData method, of class MeshData.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateMeshData_String_Path() throws Exception {
        System.out.println("createMeshData_String_Path");
        String expResult = MESH_MESSAGE;
        Path path = Paths.get(TEST_MESH_DAT);
        instance.createMeshData(expResult, path);
        byte[] result = instance.getContent();
        assertArrayEquals(expResult.getBytes(), result);
    }

    /**
     * Test of createMeshData method, of class MeshData.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateMeshData_String() throws Exception {
        System.out.println("createMeshData_String");
        String expResult = MESH_MESSAGE;
        instance.createMeshData(expResult);
        byte[] result = instance.getContent();
        assertArrayEquals(expResult.getBytes(), result);
    }

    /**
     * Test of parseMeshData method, of class MeshData. throws an exception if
     * it cant find the file or its not well formed xml
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseMeshData() throws Exception {
        System.out.println("parseMeshData");

        // NB this must be called to populate the control file before deriving the dat file location
        mm.parseControl(Paths.get(TEST_MESH_CTL));

        // NB Path only 
        Path path = Paths.get(SAVED_MESSAGES_FOLDER);
        instance.parseMeshData(path);
    }

    /**
     * Test of setDataFilePath method, of class MeshData.
     */
    @Test
    public void testSetDataFilePath() {
        System.out.println("setDataFilePath");
        Path expResult = Paths.get(TEST_MESH_DAT);
        instance.setDataFilePath(expResult);

        Path result = instance.getDataFilePath();
        assertEquals(expResult, result);
    }

}
