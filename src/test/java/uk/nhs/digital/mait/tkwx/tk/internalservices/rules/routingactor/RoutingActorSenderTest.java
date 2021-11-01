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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

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
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class RoutingActorSenderTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private RoutingActorSender instance;

    public RoutingActorSenderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty(ORG_AUDIT_ID_PROPERTY, "auditidentity");
        System.setProperty(ORG_SENDER_PROPERTY, "senderaddress");
        System.setProperty(TXTTL_PROPERTY, "1000");
        System.setProperty("tks.routingactor.username", "routingactotuser");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        // requires the Sender service to be running
        ToolkitSimulator tks = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        Mode mode = new TransmitterMode();
        mode.init(tks);

        // Force fhir messaging rather than defaulting to adding a soap wrapper
        // THis message is not an inf ack or a bus ack but it is a fhir messaging message
        String path = System.getenv("TKWROOT")+"/config/FHIR_REST/Request_FGM_Record.xml";
        instance = new RoutingActorSender(Utils.readFile2String(path), Utils.readFile2String(path), "http://localhost", "http://localhost", 0, 0);

        // atypically this thread is explicitly started by client code when required
        instance.start();
        instance.join();

        // currently this write logs to config/GP_CONNECT/transmitter_sent_messages
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInfrastructureTransmissionResult method, of class
     * RoutingActorSender.
     */
    @Test
    public void testGetInfrastructureTransmissionResult() {
        System.out.println("getInfrastructureTransmissionResult");
        String expResult = "Toolkit simulator sender service";
        String result = instance.getInfrastructureTransmissionResult();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAppTransmissionResult method, of class RoutingActorSender.
     */
    @Test
    public void testGetAppTransmissionResult() {
        System.out.println("getAppTransmissionResult");
        String expResult = "Toolkit simulator sender service";
        String result = instance.getAppTransmissionResult();
        assertEquals(expResult, result);
    }

    /**
     * Test of run method, of class RoutingActorSender.
     */
    @Test
    public void testRun() throws InterruptedException {
        System.out.println("run");
        // atypically this thread is explicitly started by client code when required
        // which is what invokes run
    }

    /**
     * Test of setInfackservice method, of class RoutingActorSender.
     */
    @Test
    public void testSetInfackservice() {
        System.out.println("setInfackservice");
        String infackservice = "infackservice";
        instance.setInfackservice(infackservice);
    }

    /**
     * Test of setBizackservice method, of class RoutingActorSender.
     */
    @Test
    public void testSetBizackservice() {
        System.out.println("setBizackservice");
        String bizackservice = "bizackservice";
        instance.setBizackservice(bizackservice);
    }

}
