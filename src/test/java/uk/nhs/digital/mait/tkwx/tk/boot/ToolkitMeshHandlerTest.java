/*
 Copyright 2012-13  Simon Farrow <simon.farrow1@nhs.net>

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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.nio.file.Path;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.nhs.digital.mait.tkwx.mesh.MeshDataException;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;

/**
 *
 * @author sifa2
 */
public class ToolkitMeshHandlerTest {
    
    public ToolkitMeshHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setToolkit method, of class ToolkitMeshHandler.
     */
    @Test
    public void testSetToolkit() throws Exception {
        System.out.println("setToolkit");
        MeshTransport t = null;
        ToolkitMeshHandler instance = new ToolkitMeshHandlerImpl();
        instance.setToolkit(t);
    }

    /**
     * Test of handle method, of class ToolkitMeshHandler.
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String meshDirectoryType = "";
        Path req = null;
        ToolkitMeshHandler instance = new ToolkitMeshHandlerImpl();
        instance.handle(meshDirectoryType, req);
    }

    public class ToolkitMeshHandlerImpl extends ToolkitMeshHandler {

        public void handle(String meshDirectoryType, Path req) throws MeshDataException {
        }
    }

    /**
     * Test of setSavedMessagesDirectory method, of class ToolkitMeshHandler.
     */
    @Test
    public void testSetSavedMessagesDirectory() {
        System.out.println("setSavedMessagesDirectory");
        String s = "";
        ToolkitMeshHandler instance = new ToolkitMeshHandlerImpl();
        instance.setSavedMessagesDirectory(s);
    }

    /**
     * Test of setMESHMailboxId method, of class ToolkitMeshHandler.
     */
    @Test
    public void testSetMESHMailboxId() {
        System.out.println("setMESHMailboxId");
        String s = "";
        ToolkitMeshHandler instance = new ToolkitMeshHandlerImpl();
        instance.setMESHMailboxId(s);
    }
}
