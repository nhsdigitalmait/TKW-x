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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import static org.mockito.Mockito.*;

/**
 *
 * @author sifa2
 */
public class HTTPGetCRLHandlerTest extends AbstractHandler {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private HttpRequest req;
    private HttpResponse resp;
    private HTTPGetCRLHandler instance;

    public HTTPGetCRLHandlerTest() {
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
        // add any old file which is then returned as the payload
        System.setProperty("tks.crl", "src/test/resources/extract.cfg");
        System.setProperty("tks.tls.crl", "src/test/resources/extract.cfg");
        System.setProperty("tks.signing.crl", "src/test/resources/extract.cfg");
        instance = new HTTPGetCRLHandler();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getTlsCrl method, of class HTTPGetCRLHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetTlsCrl() throws Exception {
        System.out.println("getTlsCrl");
        instance.setToolkit(new HttpTransport());
        byte[] result = instance.getTlsCrl();
        assertNotNull(result);
    }

    /**
     * Test of getSigningCrl method, of class HTTPGetCRLHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSigningCrl() throws Exception {
        System.out.println("getSigningCrl");
        instance.setToolkit(new HttpTransport());
        byte[] result = instance.getSigningCrl();
        assertNotNull(result);
    }

    /**
     * Test of getCommonCrl method, of class HTTPGetCRLHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCommonCrl() throws Exception {
        System.out.println("getCommonCrl");
        instance.setToolkit(new HttpTransport());
        byte[] result = instance.getCommonCrl();
        assertNotNull(result);
    }

    /**
     * Test of setToolkit method, of class HTTPGetCRLHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetToolkit() throws Exception {
        System.out.println("setToolkit");
        HttpTransport t = new HttpTransport();
        instance.setToolkit(t);
    }

    /**
     * Test of handle method, of class HTTPGetCRLHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String path = "getcrl";
        String params = "ignored";
        instance.setToolkit(new HttpTransport());

        instance.handle(path, params, req, resp);
        resp.flush();
        String result = resp.getHttpHeader();
        assertTrue(result.contains("200"));

        String expResult = "__ID__";
        result = resp.getOutputStream().toString();
        assertTrue(result.startsWith(expResult));
    }

}
