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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class OpenMeshMessageRegisterTest {

    private OpenMeshMessageRegister instance;
    private MeshMessage mm;

    public OpenMeshMessageRegisterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, Exception {
        MeshDataTest.setUpClass();
        System.setProperty("tks.MeshTransport.reporttimeoutperiod","10");
    }

    @AfterClass
    public static void tearDownClass() {
        MeshDataTest.tearDownClass();
    }

    @Before
    public void setUp() throws IOException, Exception {
        commonSetup();
        instance = OpenMeshMessageRegister.getInstance();
        mm = new MeshMessage(MAILBOXID);
    }

    @After
    public void tearDown() throws IOException {
        commonTeardown();
    }

    /**
     * Test of run method, of class OpenMeshMessageRegister.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // called by private constructor.start() every time
        // instance.run();
    }

    /**
     * Test of registerMeshRequest method, of class OpenMeshMessageRegister.
     * @throws java.lang.Exception
     */
    @Test
    public void testRegisterMeshRequest() throws Exception {
        System.out.println("registerMeshRequest");
        String id = "id1";
        instance.registerMeshRequest(mm, id);
    }

    /**
     * Test of deRegisterMeshRequest method, of class OpenMeshMessageRegister.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDeRegisterMeshRequest() throws Exception {
        System.out.println("deRegisterMeshRequest");
        String id = "id2";
        instance.registerMeshRequest(mm, id);
        MeshMessage result = instance.getOpenMeshMessage(id);
        MeshMessage expResult = mm;
        assertEquals(expResult, result);

        instance.deRegisterMeshRequest(id);
        
        // check its empty now
        result = instance.getOpenMeshMessage(id);
        expResult = null;
        assertEquals(expResult, result);
    }

    /**
     * Test of getOpenMeshMessage method, of class OpenMeshMessageRegister.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetOpenMeshMessage() throws Exception {
        System.out.println("getOpenMeshMessage");
        String id = "id3";
        instance.registerMeshRequest(mm, id);
        MeshMessage expResult = mm;
        MeshMessage result = instance.getOpenMeshMessage(id);
        assertEquals(expResult, result);

        // check absence of non existent message 
        expResult = null;
        id = "id";
        result = instance.getOpenMeshMessage(id);
        assertEquals(expResult, result);

    }

    /**
     * Test of stopTimer method, of class OpenMeshMessageRegister.
     */
    @Test
    public void testStopTimer() {
        System.out.println("stopTimer");
        instance.stopTimer();
    }

    /**
     * Test of getInstance method, of class OpenMeshMessageRegister.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        OpenMeshMessageRegister result = OpenMeshMessageRegister.getInstance();
        assertNotNull(result);
    }

}
