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
package uk.nhs.digital.mait.tkwx.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;

/**
 *
 * @author sifa2
 */
public class HttpServerTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private HttpServer instance;
    private InputStream istream;
    private final static String TESTFILE = "src/test/resources/test.txt";
    private final static String CONTENT = "<test>xxxx</test>";
    private final static String SAVEDMESSAGESFOLDER = "src/test/resources/testfolder";

    public HttpServerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        new File(SAVEDMESSAGESFOLDER).mkdirs();
        System.setProperty(SAVEDMESSAGES_PROPERTY, SAVEDMESSAGESFOLDER);
        System.setProperty(SYNCWRAPPER_PROPERTY, TESTFILE);
        System.setProperty(FAULTPAYLOAD_PROPERTY, TESTFILE);

        // hack to avoid cast error
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";

        ToolkitSimulator tks = new ToolkitSimulator(propertiesFile);
        Mode mode = new SimulatorMode();
        mode.init(tks);
        ServiceManager.getInstance(tks);
    }

    @AfterClass
    public static void tearDownClass() {
        new File(SAVEDMESSAGESFOLDER).delete();
    }

    @Before
    public void setUp() {
        instance = new HttpServer();
        istream = new ByteArrayInputStream(CONTENT.getBytes());
    }

    @After
    public void tearDown() throws IOException {
        istream.close();
    }

    /**
     * Test of start method, of class HttpServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testStart() throws Exception {
        System.out.println("start");
        instance.start();
    }

    /**
     * Test of stop method, of class HttpServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testStop() throws Exception {
        System.out.println("stop");
        instance.stop();
    }

    /**
     * Test of addListener method, of class HttpServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddListener() throws Exception {
        System.out.println("addListener");
        Listener s = new SocketListener();
        instance.addListener(s);
    }

    /**
     * Test of addContext method, of class HttpServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddContext() throws Exception {
        System.out.println("addContext");
        HttpContext c = new HttpContext();
        instance.addContext(c);
    }

    /**
     * Test of addRequest method, of class HttpServer.
     * @throws java.lang.Exception
     */
    @Test
    public void testAddRequest() throws Exception {
        System.out.println("addRequest");
        final String MESSAGE = "message";
        final String CP = "/";
        
        HttpRequest r = new HttpRequest("");
        r.setRequestContext(CP);
        r.setInputStream(new ByteArrayInputStream(MESSAGE.getBytes()));
        r.setContentLength(MESSAGE.length());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        r.setResponse(new HttpResponse(bos));
        
        HttpContext c = new HttpContext();
        c.setContextPath(CP);
        c.addHandler(new HandlerImpl());
        
        instance.addContext(c);
        instance.addRequest(r);
        System.out.println(bos.toString());
    }

    private static class HandlerImpl implements Handler {

        public HandlerImpl() {
        }

        @Override
        public void handle(String path, String params, HttpRequest req, HttpResponse resp) throws HttpException {
            System.out.println(String.format("handle called path %s params %s\n",path, params));
        }
    }
}
