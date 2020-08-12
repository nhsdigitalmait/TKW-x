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

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.configurator.ResettablePropertiesConfigurator;

/**
 *
 * @author simonfarrow
 */
public class FHIRITKRoutingActorAckOnlyTest {

    private FHIRITKRoutingActorAckOnly instance;

    public FHIRITKRoutingActorAckOnlyTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
        ResettablePropertiesConfigurator c = (uk.nhs.digital.mait.commonutils.util.configurator.ResettablePropertiesConfigurator) Configurator.getConfigurator();
        Properties p = new Properties();
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        p.load(new FileReader(propertiesFile));

        String root = System.getenv("TKWROOT") + "/config/FHIR_REST/simulator_config/fhir_routing_actor_templates/";
        p.setProperty("tks.routingactor.fhir.busacktemplate", root + "/ITK-BusinessAck-Success-Example-1.xml");
        p.setProperty("tks.routingactor.fhir.appacktemplate", root + "/ITK-InfAck-Success-Example-1.xml");
        p.setProperty("tks.routingactor.cda.forcedeliveryurl", "http://127.0.0.1:4848/fhir/acks");
        p.setProperty("tks.routingactor.infrastructure.forcedeliveryurl", "http://127.0.0.1:4848/fhir/acks");
      
        // default values if set error code not called
        p.setProperty("tks.routingactor.fhir.ia.errorcode", "77777");
        p.setProperty("tks.routingactor.fhir.ia.responsecode", "responsecode");
        p.setProperty("tks.routingactor.fhir.ia.errortext", "errortext");
        p.setProperty("tks.routingactor.fhir.ia.issueseverity", "issueseverity");
        p.setProperty("tks.routingactor.fhir.ia.issuetext", "issuetext");

        // per error code values
        p.setProperty("tks.routingactor.fhir.ia.errorcode.9999", "9999");
        p.setProperty("tks.routingactor.fhir.ia.responsecode.9999", "responsecode");
        p.setProperty("tks.routingactor.fhir.ia.errortext.9999", "errortext");
        p.setProperty("tks.routingactor.fhir.ia.issueseverity.9999", "issueseverity");
        p.setProperty("tks.routingactor.fhir.ia.issuetext.9999", "issuetext");

       
        p.setProperty("tks.routingactor.username", "username");

        p.setProperty(TXTTL_PROPERTY, "100");
        c.setProperties(p);
        instance = new FHIRITKRoutingActorAckOnly();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class FHIRMESHRoutingActorAckOnly.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init();
    }

    /**
     * Test of makeResponse method, of class FHIRITKRoutingActorAckOnly.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        instance.init();
        boot();
        HashMap<String,Substitution> substitutions = new HashMap<>();
        HttpRequest req  = new HttpRequest("id");
        String expResult = "";
        String result = instance.makeResponse(substitutions, req);
        assertEquals(expResult, result);
        // wait 10s for completion of ras sender object tasks
        Thread.sleep(10000);
    }

    /**
     * Test of forceClose method, of class FHIRITKRoutingActorAckOnly.
     */
    @Test
    public void testForceClose() {
        System.out.println("forceClose");
        Boolean expResult = false;
        Boolean result = instance.forceClose();
        assertEquals(expResult, result);
    }

    private void boot() throws IOException, Exception {
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        t.startService("Sender");
    }

}
