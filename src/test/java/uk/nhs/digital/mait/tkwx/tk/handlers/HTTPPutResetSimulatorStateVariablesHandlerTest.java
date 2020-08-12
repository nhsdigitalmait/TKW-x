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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.SessionStateManager;

/**
 *
 * @author simonfarrow
 */
public class HTTPPutResetSimulatorStateVariablesHandlerTest extends AbstractHandler {

    private HttpRequest req;
    private HttpResponse resp;
    private static final String SESSION_ID_FILE = "src/test/resources/sessionid.txt";
    private static final String SESSION_ID = "session123";

    public HTTPPutResetSimulatorStateVariablesHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        try (FileWriter fw = new FileWriter(SESSION_ID_FILE)) {
            fw.write(SESSION_ID);
        }
        AbstractHandler.setUpClass(SESSION_ID_FILE);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        AbstractHandler.tearDownClass();
        new File(SESSION_ID_FILE).delete();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        req = mock(HttpRequest.class);
        when(req.getInputStream()).thenReturn(istream);
        when(req.getContentLength()).thenReturn(content.length());
        resp = new HttpResponse(ostream);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of handle method, of class
     * HTTPPutResetSimulatorStateVariablesHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String path = "ignored";
        String params = "ignored";
        HTTPPutResetSimulatorStateVariablesHandler instance = new HTTPPutResetSimulatorStateVariablesHandler();
        SessionStateManager ssm = SessionStateManager.getInstance();
        ssm.setValue(SESSION_ID,"$v1","l1");
        instance.handle(path, params, req, resp);
        String result = ssm.getValue(SESSION_ID,"v1");
        String expResult = null;
        assertEquals(expResult, result);
    }

}
