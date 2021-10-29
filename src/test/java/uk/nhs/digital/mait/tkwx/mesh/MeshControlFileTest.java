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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
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

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class MeshControlFileTest {

    private MeshControlFile instance;

    public MeshControlFileTest() {
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
        instance = new MeshControlFile(new MeshMessage(MAILBOXID));
        instance.parseMeshControlFile(Paths.get(TEST_MESH_CTL));
    }

    @After
    public void tearDown() throws IOException {
        commonTeardown();
    }

    /**
     * Test of createMeshControlFile method, of class MeshControlFile.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateMeshControlFile() throws Exception {
        System.out.println("createMeshControlFile");

        String to = "__TO_NEW_DTS__";
        String li = "newlocalid";
        instance.createMeshControlFile(to, li);

        String expResult = to;
        assertEquals(expResult, instance.getToDTS());

        expResult = li;
        assertEquals(expResult, instance.getLocalId());
    }

    /**
     * Test of getWorkflowIdFromXref method, of class MeshControlFile.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetWorkflowIdFromXref() throws Exception {
        System.out.println("getWorkflowIdFromXref");
        String requestingWorkflowId = "TOC_DISCH_DMS";
        String expResult = "TOC_DISCH_DMS_ACK";
        String result = instance.getWorkflowIdFromXref(requestingWorkflowId);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseMeshControlFile method, of class MeshControlFile.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseMeshControlFile() throws Exception {
        System.out.println("parseMeshControlFile");
        Path file = Paths.get(TEST_MESH_CTL);
        instance.parseMeshControlFile(file);
    }

    /**
     * Test of logControlFile method, of class MeshControlFile.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLogControlFile() throws Exception {
        System.out.println("logControlFile");
        instance.logControlFile("type");
    }

    /**
     * Test of getControlStatus method, of class MeshControlFile.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetControlStatus() throws Exception {
        System.out.println("getControlStatus");
        MeshControlStatus result = instance.getControlStatus();
        assertNotNull(result);
    }

    /**
     * Test of setControlStatus method, of class MeshControlFile.
     */
    @Test
    public void testSetControlStatus() throws Exception {
        System.out.println("setControlStatus");
        MeshControlStatus controlStatus = new MeshControlStatus(instance);
        instance.setControlStatus(controlStatus);
    }

    /**
     * Test of getVersion method, of class MeshControlFile.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        String expResult = "1.0";
        String result = instance.getVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressType method, of class MeshControlFile.
     */
    @Test
    public void testGetAddressType() {
        System.out.println("getAddressType");
        String expResult = "DTS";
        String result = instance.getAddressType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMessageType method, of class MeshControlFile.
     */
    @Test
    public void testGetMessageType() {
        System.out.println("getMessageType");
        String expResult = "Data";
        String result = instance.getMessageType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWorkflowId method, of class MeshControlFile.
     */
    @Test
    public void testGetWorkflowId() {
        System.out.println("getWorkflowId");
        String expResult = "TOC_DISCH_DMS";
        String result = instance.getWorkflowId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCtlFileContents method, of class MeshControlFile.
     */
    @Test
    public void testGetCtlFileContents() {
        System.out.println("getCtlFileContents");
        String expResult = "<DTSControl";
        String result = instance.getCtlFileContents();
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getFromDTS method, of class MeshControlFile.
     */
    @Test
    public void testGetFromDTS() {
        System.out.println("getFromDTS");
        String expResult = TEST_MESH_FROM_DTS;
        String result = instance.getFromDTS();
        assertEquals(expResult, result);
    }

    /**
     * Test of getToDTS method, of class MeshControlFile.
     */
    @Test
    public void testGetToDTS() {
        System.out.println("getToDTS");
        String expResult = TEST_MESH_TO_DTS;
        String result = instance.getToDTS();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLocalId method, of class MeshControlFile.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetLocalId() throws Exception {
        System.out.println("getLocalId");
        instance.createMeshControlFile("newto", "newli");
        String expResult = "newli";
        String result = instance.getLocalId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDtsId method, of class MeshControlFile.
     */
    @Test
    public void testGetDtsId() {
        System.out.println("getDtsId");
        String expResult = "";
        String result = instance.getDtsId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getControlFilePath method, of class MeshControlFile.
     */
    @Test
    public void testGetControlFilePath() {
        System.out.println("getControlFilePath");
        Path result = instance.getControlFilePath();
        assertTrue(!result.toString().isEmpty());
        assertTrue(result.endsWith(TEST_MESH_CTL));
    }

    /**
     * Test of getTransmissionTime method, of class MeshControlFile.
     */
    @Test
    public void testGetTransmissionTime() {
        System.out.println("getTransmissionTime");
        String expResult = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+Z$";
        Instant result = instance.getTransmissionTime();
        assertTrue(result.toString().matches(expResult));
    }

    /**
     * Test of setWorkflowId method, of class MeshControlFile.
     */
    @Test
    public void testSetWorkflowId() {
        System.out.println("setWorkflowId");
        String expResult = "newid";
        instance.setWorkflowId(expResult);
        String result = instance.getWorkflowId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFromDTS method, of class MeshControlFile.
     */
    @Test
    public void testSetFromDTS() {
        System.out.println("setFromDTS");
        String expResult = "newFromDTS";
        instance.setFromDTS(expResult);
        String result = instance.getFromDTS();
        assertEquals(expResult, result);
    }

    /**
     * Test of setControlFilePath method, of class MeshControlFile.
     */
    @Test
    public void testSetControlFilePath() {
        System.out.println("setControlFilePath");
        String expResult = TEST_MESH_CTL;
        Path path = Paths.get(expResult);
        instance.setControlFilePath(path);
        String result = instance.getControlFilePath().toString();
        assertTrue(result.endsWith(TEST_MESH_CTL));
    }

}
