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
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.ITK_TRUNK_INTERACTION;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author SIFA2
 */
public class SpineToolsSenderTest {

    private static Process process;

    private SpineToolsSender instance;

    public SpineToolsSenderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        // start an ITK Trunk Host responder on 4848
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", System.getenv("TKWROOT") + "/TKW-x.jar", "-simulator", System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Host/tkwHost-x.properties");
        process = pb.start();
        ProcessStreamDumper.dumpProcessStreams(process);
    }

    @AfterClass
    public static void tearDownClass() {
        process.destroy();
    }

    @Before
    public void setUp() {
        instance = new SpineToolsSender();
    }

    @After
    public void tearDown() {
        for (File f : new File("src/test/resources").listFiles()) {
            if (f.getName().endsWith(".log") || f.getName().endsWith("log.lck")) {
                f.delete();
            }
        }
    }

    /**
     * Test of init method, of class SpineToolsSender.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testInit() throws InterruptedException, IOException, Exception {
        System.out.println("init");
        ToolkitSimulator tks = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties");
        String wrapper = "<wrap>__PAYLOAD_BODY__</wrap>";
        String path = System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml";
        String payload = new String(Files.readAllBytes(Paths.get(path)));
        // last parm is the listen address
        SenderRequest what = new SenderRequest(payload, wrapper, "http://localhost:4848");
        // use ITK Trunk 
        what.setAction(ITK_TRUNK_INTERACTION);

        what.setODSCode("YEA");

        boolean useTls = false;
        String logdir = "src/test/resources";
        instance.init(tks, what, useTls, logdir);
        // it's a thread wait until it's done its work currently set to timeout at 30s
        instance.join();

        int logFileCount = 0;
        for (File f : new File(logdir).listFiles()) {
            if (f.getName().endsWith(".log")) {
                logFileCount++;
                System.out.println("logfile found \"" + f.getName() + "\"");
                String logFileContents = new String(Files.readAllBytes(Paths.get(f.getCanonicalPath())));
                System.out.println(logFileContents);
                if (!f.getName().matches("^TKW_[0-9]+\\.log$")) {
                    fail("logfile name not in expected form");
                }
            }
        }

        // we are expecting a single log file
        int expResult = 1;
        assertEquals(expResult, logFileCount);
    }

    /**
     * Test of run method, of class SpineToolsSender.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // run called by start in init
    }

}
