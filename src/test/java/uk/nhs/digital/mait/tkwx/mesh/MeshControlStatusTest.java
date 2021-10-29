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
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_CTL;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_FROM_DTS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.TEST_MESH_TO_DTS;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class MeshControlStatusTest {

    static {
        try {
            // need full path for this test
            TEST_MESH_CTL_WITH_STATUS = new File(SAVED_MESSAGES_FOLDER + "/mesh_status.ctl").getCanonicalPath();
        } catch (IOException ex) {
            //Logger.getLogger(MeshControlStatusTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String TEST_MESH_CTL_WITH_STATUS;

    private MeshControlStatus instance;

    public MeshControlStatusTest() {
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

        MeshControlFile meshCtlFile = new MeshControlFile(new MeshMessage(MAILBOXID));
        meshCtlFile.parseMeshControlFile(Paths.get(TEST_MESH_CTL));

        instance = new MeshControlStatus(meshCtlFile);
        instance.createStatus("dt", "e", "s", "sc", "d");

        createControlFileWithStatus();
    }

    public static void createControlFileWithStatus() throws IOException {
        try (FileWriter fw = new FileWriter(TEST_MESH_CTL_WITH_STATUS)) {
            fw.write("<DTSControl>\n"
                    + "	<Version>1.0</Version>\n"
                    + "	<AddressType>DTS</AddressType>\n"
                    + "	<MessageType>Data</MessageType>\n"
                    + "	<WorkflowId>TOC_DISCH_DMS</WorkflowId>\n"
                    + "	<To_DTS>" + TEST_MESH_TO_DTS + "</To_DTS>\n"
                    + "	<From_DTS>" + TEST_MESH_FROM_DTS + "</From_DTS>\n"
                    + "	<LocalId>localid</LocalId>\n"
                    + "	<Compress>N</Compress>\n"
                    + "     <StatusRecord>\n"
                    + "        <DateTime>adt1</DateTime>\n"
                    + "        <Event>TRANSFER</Event>\n"
                    + "        <Status>as1</Status>\n"
                    + "        <StatusCode>_STATUSCODE__1</StatusCode>\n"
                    + "        <Description>ad1</Description>\n"
                    + "    </StatusRecord>\n"
                    + "</DTSControl>");
        }
    }

    @After
    public void tearDown() throws IOException {
        commonTeardown();
        deleteControlFileWithStatus();
    }

    public static void deleteControlFileWithStatus() {
        new File(TEST_MESH_CTL_WITH_STATUS).delete();
    }

    /**
     * Test of createStatus method, of class MeshControlStatus.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateStatus() throws Exception {
        System.out.println("createStatus");
        String dt = "dt";
        String e = "e";
        String s = "s";
        String sc = "sc";
        String d = "d";
        String expResult = "<StatusRecord>";
        String result = instance.createStatus(dt, e, s, sc, d);
        // nb leading spaces on the original file
        assertTrue(result.trim().startsWith(expResult));
    }

    /**
     * Test of updateTemplate method, of class MeshControlStatus.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateTemplate() throws Exception {
        System.out.println("updateTemplate");
        String expResult = "<DateTime>adt</DateTime>";
        instance.createStatus("adt", "ae", "as", "asc", "ad");
        String result = instance.updateTemplate();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of parseStatus method, of class MeshControlStatus.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseStatus() throws Exception {
        System.out.println("parseStatus");

        MeshControlFile meshCtlFile = new MeshControlFile(new MeshMessage(MAILBOXID));
        meshCtlFile.parseMeshControlFile(Paths.get(TEST_MESH_CTL_WITH_STATUS).toAbsolutePath());

        instance = new MeshControlStatus(meshCtlFile);
        instance.parseStatus();
        String expResult = "adt1";
        String result = instance.getDateTime();
        assertEquals(expResult, result);
    }

    /**
     * Test of logStatusReport method, of class MeshControlStatus.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLogStatusReport() throws Exception {
        System.out.println("logStatusReport");
        String expResult = "Date Time = dt";
        String result = instance.logStatusReport();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getDateTime method, of class MeshControlStatus.
     */
    @Test
    public void testGetDateTime() {
        System.out.println("getDateTime");
        String expResult = "dt";
        String result = instance.getDateTime();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEvent method, of class MeshControlStatus.
     */
    @Test
    public void testGetEvent() {
        System.out.println("getEvent");
        String expResult = "e";
        String result = instance.getEvent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStatus method, of class MeshControlStatus.
     */
    @Test
    public void testGetStatus() {
        System.out.println("getStatus");
        String expResult = "s";
        String result = instance.getStatus();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStatusCode method, of class MeshControlStatus.
     */
    @Test
    public void testGetStatusCode() {
        System.out.println("getStatusCode");
        String expResult = "sc";
        String result = instance.getStatusCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescription method, of class MeshControlStatus.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        String expResult = "d";
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

}
