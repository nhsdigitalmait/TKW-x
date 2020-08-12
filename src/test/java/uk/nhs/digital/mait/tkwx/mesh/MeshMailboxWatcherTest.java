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
package uk.nhs.digital.mait.tkwx.mesh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitMeshHandler;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;

/**
 *
 * @author sifa2
 */
public class MeshMailboxWatcherTest {

    private MeshMailboxWatcher instance;
    private boolean tripped = false;
    private static final String FILE_TO_WATCH = SAVED_MESSAGES_FOLDER+"/t1.ctl";

    public MeshMailboxWatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        instance = new MeshMailboxWatcher(Paths.get(SAVED_MESSAGES_FOLDER), "tks.MeshTransport."+MAILBOXID+".in", new HandlerImpl());
    }

    @After
    public void tearDown() {
        new File(FILE_TO_WATCH).delete();
    }

    public class HandlerImpl extends ToolkitMeshHandler {

        @Override
        public void handle(String meshDirectoryType, Path req) throws MeshDataException {
            System.out.format("Handle called mesh directory type: %s path: %s\n", meshDirectoryType, req);
            tripped = true;
        }
    }

    /**
     * Test of cast method, of class MeshMailboxWatcher.
     */
    @Test
    public void testCast() {
        System.out.println("cast");
        WatchEvent event = null;
        WatchEvent expResult = null;
        WatchEvent result = MeshMailboxWatcher.cast(event);
        assertEquals(expResult, result);
    }

    /**
     * Test of close method, of class MeshMailboxWatcher.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testClose() throws IOException, InterruptedException {
        System.out.println("close");
        instance.close();
    }

    /**
     * Test of run method, of class MeshMailboxWatcher.
     *
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testRun() throws InterruptedException, IOException {
        System.out.println("run");

        assertFalse(tripped);
        assertFalse(fileExists(FILE_TO_WATCH));
        
        // add a pseudo ctl file to the watched folder
        try (FileWriter fw = new FileWriter(FILE_TO_WATCH)) {
        }
        Thread.sleep(10);
        assertTrue(tripped);
    }

}
