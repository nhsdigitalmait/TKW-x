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

import java.io.IOException;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import static org.mockito.Mockito.*;


/**
 *
 * @author sifa2
 */
public class DiagnosticHandlerTest extends AbstractHandler {

    private HttpRequest req;
    private HttpResponse resp;

    public DiagnosticHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        AbstractHandler.setUpClass("");
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        AbstractHandler.tearDownClass();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        req = mock(HttpRequest.class);
        resp = new HttpResponse(ostream);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of handle method, of class DiagnosticHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String path = "";
        String params = "";
        DiagnosticHandler instance = new DiagnosticHandler();
        instance.setToolkit(new HttpTransport());
        instance.handle(path, params, req, resp);

        String result = new String (resp.getHttpBuffer());
        // result is a text diagnostic response
        String expResult = "Input SOAPaction";
        assertTrue(result.startsWith(expResult));
    }

}
