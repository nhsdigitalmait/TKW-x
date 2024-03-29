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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import static org.mockito.Mockito.*;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class HTTPPostSimulatorRulesetWorkerTest extends AbstractHandler {

    private HttpRequest req;
    private HttpResponse resp;
    private HTTPPostSimulatorRulesetWorker instance;

    private static final String RULESET_FILE = "src/test/resources/ruleset.txt";
    private static final String RULESET = System.getenv("TKWROOT")+"/config/GP_CONNECT/simulator_config/test_tks_rule_config.txt";

    public HTTPPostSimulatorRulesetWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        try (FileWriter fw = new FileWriter(RULESET_FILE)) {
            fw.write(RULESET);
        }
        AbstractHandler.setUpClass(RULESET_FILE);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        AbstractHandler.tearDownClass();
        new File(RULESET_FILE).delete();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        req = mock(HttpRequest.class);
        when(req.getInputStream()).thenReturn(istream);
        when(req.getContentLength()).thenReturn(content.length());

        // ensure there is a rules engine available from the ServiceManager
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        SimulatorMode m = new SimulatorMode();
        m.init(t);

        resp = new HttpResponse(ostream);

        HTTPPostSimulatorRulesetHandler hpsrh = new HTTPPostSimulatorRulesetHandler();
        instance = new HTTPPostSimulatorRulesetWorker(hpsrh);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of handle method, of class HTTPPostSimulatorRulesetWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String path = "/rulesetchange";
        String params = "ignored";
        instance.handle(path, params, req, resp);
    }

}
