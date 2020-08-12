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
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 *
 * @author simonfarrow
 */
public class MeshRequestTest {

    private MeshRequest instance;
    private static final String LOGGING_FILE = "src/test/resources/lfos.txt";

    public MeshRequestTest() {
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
    public void setUp() throws IOException {
        commonSetup();
        instance = new MeshRequest();
    }

    @After
    public void tearDown() throws IOException {
        File f = new File(LOGGING_FILE);
        if ( f.exists()){
            f.delete();
        }
        commonTeardown();
    }

    /**
     * Test of getRequestMeshMessage method, of class MeshRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetRequestMeshMessage() throws Exception {
        System.out.println("getRequestMeshMessage");
        MeshMessage expResult = new MeshMessage(MAILBOXID);
        instance.setRequestMessage(expResult);
        MeshMessage result = instance.getRequestMeshMessage();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRequestMessage method, of class MeshRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetRequestMessage() throws Exception {
        System.out.println("setRequestMessage");
        MeshMessage mm = new MeshMessage(MAILBOXID);
        instance.setRequestMessage(mm);
    }

    /**
     * Test of addResponseMeshMessage method, of class MeshRequest.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddResponseMeshMessage() throws Exception {
        System.out.println("addResponseMeshMessage");
        MeshMessage mm = new MeshMessage(MAILBOXID);
        instance.addResponseMeshMessage(mm);
    }

    /**
     * Test of getLogFile method, of class MeshRequest.
     */
    @Test
    public void testGetLoggingStream() {
        System.out.println("getLoggingStream");
        LoggingFileOutputStream expResult = null;
        LoggingFileOutputStream result = instance.getLoggingStream();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLogFile method, of class MeshRequest.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testSetLoggingStream() throws IOException, Exception {
        System.out.println("setLoggingStream");
        instance.setRequestMessage(new MeshMessage(MAILBOXID));
        try (LoggingFileOutputStream lfos = new LoggingFileOutputStream(SAVED_MESSAGES_FOLDER+"/mesh.log")) {
            instance.setLoggingStream(lfos);
        }
        new File(SAVED_MESSAGES_FOLDER+"/mesh.log").delete();
    }

    /**
     * Test of setRequestContext method, of class MeshRequest.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetRequestContext() throws Exception {
        System.out.println("setRequestContext");
        String c = "";
        instance.setRequestContext(c);
    }

    /**
     * Test of setRequestType method, of class MeshRequest.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetRequestType() throws Exception {
        System.out.println("setRequestType");
        String r = "";
        instance.setRequestType(r);
    }

    /**
     * Test of setInputStream method, of class MeshRequest.
     */
    @Test
    public void testSetInputStream() {
        System.out.println("setInputStream");
        InputStream h = null;
        instance.setInputStream(h);
    }

    /**
     * Test of setContentLength method, of class MeshRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetContentLength() throws Exception {
        System.out.println("setContentLength");
        int c = 0;
        instance.setContentLength(c);
    }

    /**
     * Test of getField method, of class MeshRequest.
     */
    @Test
    public void testGetField() {
        // this is hard coded to return null regardless
        System.out.println("getField");
        String h = "fieldname";
        String expResult = null;
        String result = instance.getField(h);
        assertEquals(expResult, result);
    }

    /**
     * Test of getPriorityCode method, of class MeshRequest.
     */
    @Test
    public void testGetPriorityCode() {
        System.out.println("getPriorityCode");
        String expResult = "pc";
        instance.setPriorityCode(expResult);
        String result = instance.getPriorityCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPriorityCode method, of class MeshRequest.
     */
    @Test
    public void testSetPriorityCode() {
        System.out.println("setPriorityCode");
        String priorityCode = "pc";
        instance.setPriorityCode(priorityCode);
    }

    /**
     * Test of getPriorityDisplay method, of class MeshRequest.
     */
    @Test
    public void testGetPriorityDisplay() {
        System.out.println("getPriorityDisplay");
        String expResult = "pd";
        instance.setPriorityDisplay(expResult);
        String result = instance.getPriorityDisplay();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPriorityDisplay method, of class MeshRequest.
     */
    @Test
    public void testSetPriorityDisplay() {
        System.out.println("setPriorityDisplay");
        String priorityDisplay = "pd";
        instance.setPriorityDisplay(priorityDisplay);
    }

    /**
     * Test of setHeader method, of class MeshRequest.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetHeader() throws Exception {
        System.out.println("setHeader");
        String h = "";
        String v = "";
        instance.setHeader(h, v);
    }

    /**
     * Test of updateHeader method, of class MeshRequest.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateHeader() throws Exception {
        System.out.println("updateHeader");
        String h = "";
        String v = "";
        instance.updateHeader(h, v);
    }
}
