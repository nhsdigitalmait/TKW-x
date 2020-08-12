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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.ProcessStreamDumper;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author sifa2
 */
public class SpineSenderTest {

    private static Process process;

    public SpineSenderTest() {
    }


    @BeforeClass
    public static void setUpClass() throws IOException, InterruptedException {
        // start an MTH host responder on 4430
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", System.getenv("TKWROOT") + "/TKW-x.jar", "-simulator", System.getenv("TKWROOT") + "/config/SPINE_MTH/tkw-x.properties");
        process = pb.start();
        ProcessStreamDumper.dumpProcessStreams(process);

        // give it time to start
        Thread.sleep(5000);
    }

    @AfterClass
    public static void tearDownClass() {
        process.destroy();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        for (File f : new File("src/test/resources").listFiles()) {
            if (f.getName().endsWith(".log") || f.getName().endsWith("log.signature")) {
                f.delete();
            }
        }
    }

    /**
     * Test of run method, of class SpineSender.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testConstructor() throws Exception {
        System.out.println("Constructor");
        ToolkitSimulator tks = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties");
        String wrapper = "<wrap>__PAYLOAD_BODY__</wrap>";
        SenderRequest senderRequest = new SenderRequest("<a/>", wrapper, "http://localhost:4430/syncservice-pds/pds");
        senderRequest.setAction("urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
        SpineSender instance = new SpineSender(tks, senderRequest, false, "src/test/resources");
        // it's a thread
        instance.join();
        int logFileCount = 0;
        for (File f : new File("src/test/resources").listFiles()) {
            if (f.getName().endsWith(".log")) {
                logFileCount++;
                System.out.println("logfile found \"" + f.getName() + "\"");
                String logFileContents = new String(Files.readAllBytes(Paths.get(f.getCanonicalPath())));
                System.out.println(logFileContents);
                if (!f.getName().matches("^localhost_sent_null_at_[0-9]+\\.log$")) {
                    fail("logfile name not in expected form");
                }
            }
        }

        // This will fail with a MTH simulator error reporting The service/interaction is not supported for the requested URI version mismatch
        // hardly surprising since the payload <wrap><a/></wrap> is nonsense.
        // This doesn't matter we are still getting an end to end interaction with the simulator
        // we are expecting a single log file
        int expResult = 1;
        assertEquals(expResult, logFileCount);
    }

    /**
     * Test of run method, of class SpineSender.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // constructor calls run indirectly via start
    }

}
