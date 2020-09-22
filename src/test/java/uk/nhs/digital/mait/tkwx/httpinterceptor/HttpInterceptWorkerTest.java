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
package uk.nhs.digital.mait.tkwx.httpinterceptor;

import static com.helger.commons.http.CHttpHeader.CONTENT_LENGTH;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor.HttpInterceptHandler;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpInterceptorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.tkwx.ProcessStreamDumper;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor.HttpInterceptHandlerTest.TEMP_PROPERTIES_FILE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.FORWARDINGPORTPROPERTY;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import static uk.nhs.digital.mait.tkwx.util.Utils.folderExists;
import static uk.nhs.digital.mait.tkwx.util.Utils.readPropertiesFile;

/**
 *
 * @author SIFA2
 */
public class HttpInterceptWorkerTest {

    private static Process process;

    private HttpInterceptWorker instance;
    private ByteArrayOutputStream ostream;

    private static final String TESTSAVED_MESSAGES_FOLDER = "src/test/resources/saved_messages_folder";
    private static final String ENDPOINT_FOLDER = TESTSAVED_MESSAGES_FOLDER + "/127.0.0.1/";
    private HttpRequest request;

    private static String simulatorListenPortNo;
    private static final String INTERCEPTOR_LOG = "src/test/resources/interceptor.log";

    public HttpInterceptWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // start an GP_CONNECT host responder
        String simulatorPropsPath = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        Properties simulatorProperties = new Properties();
        readPropertiesFile(simulatorPropsPath, simulatorProperties);
        simulatorListenPortNo = simulatorProperties.getProperty("tks.HttpTransport.listenport");

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", System.getenv("TKWROOT") + "/TKW-x.jar", "-simulator", simulatorPropsPath);
        process = pb.start();
        ProcessStreamDumper.dumpProcessStreams(process);

        String interceptorPropsPath = System.getenv("TKWROOT") + "/config/HTTP_Interceptor/tkw-x.properties";
        Properties interceptorProperties = new Properties();
        readPropertiesFile(interceptorPropsPath, interceptorProperties);
        // set the interceptor to forward to whichever port the responder is listening on
        interceptorProperties.setProperty(FORWARDINGPORTPROPERTY, simulatorListenPortNo);

        try ( FileWriter fw = new FileWriter(TEMP_PROPERTIES_FILE)) {
            for (Object key : interceptorProperties.keySet()) {
                fw.write(key.toString() + " " + interceptorProperties.getProperty((String) key) + "\r\n");
            }
        }

        ToolkitSimulator tks = new ToolkitSimulator(TEMP_PROPERTIES_FILE);
        new HttpInterceptorMode().init(tks);
    }

    @AfterClass
    public static void tearDownClass() {
        process.destroy();
        new File(TEMP_PROPERTIES_FILE).delete();
    }

    @Before
    public void setUp() throws Exception {
        HttpInterceptHandler handler = new HttpInterceptHandler();
        new File(TESTSAVED_MESSAGES_FOLDER).mkdirs();
        handler.setSavedMessagesDirectory(TESTSAVED_MESSAGES_FOLDER);

        String message = Utils.readFile2String("src/test/resources/get_structured_record.xml");
        byte[] buf = message.getBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);

        request = new HttpRequest("id");
        request.setHeader(SSP_INTERACTION_ID_HEADER, "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord");
        request.setHeader("ssp-from", "from");
        request.setHeader("ssp-to", "to");

        request.setHeader("host", "localhost:"+simulatorListenPortNo);
        request.setRequestType("POST");
        request.setRequestContext("/gpconnect-demonstrator/v0/fhir/Patient/$gpc.getcarerecord");
        request.setRemoteAddress(InetAddress.getByName("127.0.0.1"));
        // NB This protocol setting is *essential*
        request.setProtocol("HTTP/1.1");
        request.setInputStream(bis);
        request.setHeader(CONTENT_LENGTH, "" + buf.length);
        request.setHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);

        instance = new HttpInterceptWorker(request, handler);
        instance.logfile = new LoggingFileOutputStream(new File(INTERCEPTOR_LOG));
        ostream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws IOException {
        ostream.close();
        Path savedMessageFolder = Paths.get(TESTSAVED_MESSAGES_FOLDER);
        Files.walk(savedMessageFolder)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        File interceptorLogFile = new File(INTERCEPTOR_LOG);
        if (interceptorLogFile.exists()) {
            interceptorLogFile.delete();
        }
    }

    /**
     * Test of getSpineAddress method, of class HttpInterceptWorker.
     */
    @Test
    public void testGetForwardingAddress() {
        System.out.println("getForwardingAddress");
        String expResult = "127.0.0.1";
        String result = instance.getForwardingAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSpinePort method, of class HttpInterceptWorker.
     */
    @Test
    public void testGetForwardingPort() {
        System.out.println("getForwardingPort");
        int expResult = Integer.parseInt(simulatorListenPortNo);
        int result = instance.getForwardingPort();
        assertEquals(expResult, result);
    }

    /**
     * Test of process method, of class HttpInterceptWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        instance.process(request, resp);
        int timeoutCount = 0;
        // wait for worker thread to complete. This takes quite a long time
        while (Thread.activeCount() > 4 && ++timeoutCount < 12) {
            Thread.sleep(1000);
        }

        File endPointFolder = new File(ENDPOINT_FOLDER);
        assertTrue(endPointFolder.exists());
        int expResult = 1;
        assertEquals(expResult, endPointFolder.listFiles().length);
        assertTrue(endPointFolder.listFiles()[0].length() > 0);
    }

    /**
     * Test of isJsonFhir method, of class HttpInterceptWorker.
     */
    @Test
    public void testIsJsonFhir() {
        System.out.println("isJsonFhir");
        String httpHeader = "application/fhir+json";
        boolean expResult = true;
        boolean result = HttpInterceptWorker.isJsonFhir(httpHeader);
        assertEquals(expResult, result);
    }

}
