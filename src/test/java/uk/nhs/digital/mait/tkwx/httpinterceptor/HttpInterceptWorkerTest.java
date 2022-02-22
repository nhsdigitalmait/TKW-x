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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import static uk.nhs.digital.mait.jwttools.AuthorisationGenerator.getJWT;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor.HttpInterceptHandler;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpInterceptorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.tkwx.ProcessStreamDumper;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor.HttpInterceptHandlerTest.TEMP_PROPERTIES_FILE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.FORWARDINGPORTPROPERTY;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import static uk.nhs.digital.mait.tkwx.util.Utils.folderExists;
import static uk.nhs.digital.mait.tkwx.util.Utils.readPropertiesFile;

/**
 * Setup creates a request which is submitted to a worker instance which then
 * forwards the request to the GP_CONNECT endpoint.
 *
 * @author SIFA2
 */
@Category(RestartJVMTest.class)
public class HttpInterceptWorkerTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private static Process process;

    private HttpInterceptWorker xmlInstanceSimulateRules;
    private HttpInterceptWorker xmlInstanceForwarding;
    private HttpInterceptWorker jsonInstanceSimulateRules;
    private ByteArrayOutputStream ostream;

    private static final String TESTSAVED_MESSAGES_FOLDER = "src/test/resources/saved_messages_folder";
    private static final String ENDPOINT_FOLDER = TESTSAVED_MESSAGES_FOLDER + "/127.0.0.1/";
    private HttpRequest xmlRequestSimulateRules;
    private HttpRequest xmlRequestForwarding;
    private HttpRequest jsonRequestSimulateRules;

    private static String simulatorListenPortNo;
    private static final String INTERCEPTOR_LOG = "src/test/resources/interceptor.log";

    private static final String GET_STRUCTURED_RECORD_URL = "/gpconnect-demonstrator/v1/fhir/Patient/$gpc.getstructuredrecord";
    private static final String GET_STRUCTURED_RECORD_INT_ID = "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getstructuredrecord-1";

    public HttpInterceptWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    /*
                0 (5001)   -------> 0 (4854)
                |         |         |
                |         |         |
            ----------    |     ----------
            |        |    |     |        |
            |        |-----     |        |
            |        |          |        |
            ----------          ---------- 
        domain  GP_CONNECT          SKELETON
        props   tkw-x-forwarding    tkw-x
        role    forwarder           closed rules
        
        */
    
        startForwardTarget("GP_CONNECT");
        
        // start the forwarder under test make this listen on an unused port (5001) to avoid loops
        startInterceptor("GP_CONNECT", 5001);
    }

    private static void startForwardTarget(String domain) throws IOException {
        // start a domain host responder this is used for testing forwarded interactions that don't match the ruleset in the httpinterceptor
        // we don't care if the endpoint returns an error since we are not interested in the response content
        // This ruleset should be closed ie all conditions are handled by a rule so there is no forwarding
        
        // start the closed endpoint listener (no unwanted forwarding)
        String simulatorPropsPath = System.getenv("TKWROOT") + "/config/"+domain+"/tkw-x.properties";
        Properties simulatorProperties = new Properties();
        readPropertiesFile(simulatorPropsPath, simulatorProperties);
        simulatorListenPortNo = simulatorProperties.getProperty("tks.HttpTransport.listenport");

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", System.getenv("TKWROOT") + "/TKW-x.jar", "-httpinterceptor", simulatorPropsPath);
        process = pb.start();
        ProcessStreamDumper.dumpProcessStreams(process);
    }

    /**
     * Start the interceptor into which we are injecting requests that either
     * get serviced by simulator rules or else forwarded
     *
     * @param domain
     * @param listenPort
     * @throws IOException
     * @throws Exception
     */
    private static void startInterceptor(String domain, int listenPort) throws IOException, Exception {
        String interceptorPropsPath = System.getenv("TKWROOT") + "/config/" + domain + "/tkw-x-forwarding.properties";
        Properties interceptorProperties = new Properties();
        readPropertiesFile(interceptorPropsPath, interceptorProperties);
        // set the interceptor to forward to whichever port the responder is listening on
        interceptorProperties.setProperty(FORWARDINGPORTPROPERTY, simulatorListenPortNo);
        // set the interceptor listenport
        interceptorProperties.setProperty("tks.HttpTransport.listenport", "" + listenPort);

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

        String xmlMessage = Utils.readFile2String("src/test/resources/get_structured_record.xml");

        String xmlMessage0 = xmlMessage.replaceAll("__NNN__", "9690937316"); // patient 22 is only in the forwarder config
        xmlRequestSimulateRules = setupRequest(xmlMessage0);
        xmlInstanceSimulateRules = new HttpInterceptWorker(xmlRequestSimulateRules, handler);
        xmlInstanceSimulateRules.logfile = new LoggingFileOutputStream(new File(INTERCEPTOR_LOG));

        String xmlMessage1 = xmlMessage.replaceAll("__NNN__", "9690937286"); // patient 2 1.5 meds is only in the forwarded config
        //String xmlMessage1 = xmlMessage.replaceAll("__NNN__", "9658218873"); // patient 2 1.2 meds is only in the forwarded config

        xmlRequestForwarding = setupRequest(xmlMessage1);
        xmlInstanceForwarding = new HttpInterceptWorker(xmlRequestForwarding, handler);
        xmlInstanceForwarding.logfile = new LoggingFileOutputStream(new File(INTERCEPTOR_LOG));

        String jsonMessage = FHIRJsonXmlAdapter.fhirConvertXml2Json(xmlMessage0);
        jsonRequestSimulateRules = setupRequest(jsonMessage);
        jsonRequestSimulateRules.setHeader("Accept", FHIR_JSON_MIMETYPE_STU3);
        jsonRequestSimulateRules.setHeader(CONTENT_TYPE_HEADER, FHIR_JSON_MIMETYPE_STU3);
        jsonInstanceSimulateRules = new HttpInterceptWorker(jsonRequestSimulateRules, handler);
        jsonInstanceSimulateRules.logfile = new LoggingFileOutputStream(new File(INTERCEPTOR_LOG));

        ostream = new ByteArrayOutputStream();
    }

    /**
     * Constructs an HttpRequest object
     *
     * @param message String containing payload (xml or json)
     * @return HttpRequest populated request object
     * @throws Exception
     * @throws FileNotFoundException
     * @throws UnknownHostException
     */
    private HttpRequest setupRequest(String message) throws Exception, FileNotFoundException, UnknownHostException {
        byte[] buf = message.getBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);

        HttpRequest request = new HttpRequest("id");
        request.setHeader(SSP_INTERACTION_ID_HEADER, GET_STRUCTURED_RECORD_INT_ID);
        request.setHeader("ssp-from", "123456789012");
        request.setHeader("ssp-to", "918999198738"); // 1.5 O/T asid
        request.setHeader("ssp-traceid", "trid");
        // note that this template does not contain __NNN__
        String authorization = getJWT("src/test/resources/gp_connect_jwt_template.fhir3.txt", "practitionerID", "9999999999", "secret", "true", "false");
        request.setHeader("Authorization", "Bearer " + authorization);
        request.setHeader("Accept", FHIR_XML_MIMETYPE_STU3);

        request.setHeader("host", "localhost:" + simulatorListenPortNo);
        request.setRequestType("POST");
        request.setRequestContext(GET_STRUCTURED_RECORD_URL);
        request.setRemoteAddress(InetAddress.getByName("127.0.0.1"));
        // NB This protocol setting is *essential*
        request.setProtocol("HTTP/1.1");
        request.setInputStream(bis);
        if (buf.length > 0) {
            request.setHeader(CONTENT_LENGTH, "" + buf.length);
            request.setHeader(CONTENT_TYPE_HEADER, FHIR_XML_MIMETYPE_STU3);
        }
        return request;
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
     * Test of getForwardingAddress method, of class HttpInterceptWorker.
     */
    @Test
    public void testGetForwardingAddress() {
        System.out.println("getForwardingAddress");
        String expResult = "127.0.0.1";
        String result = xmlInstanceSimulateRules.getForwardingAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of getForwardingPort method, of class HttpInterceptWorker.
     */
    @Test
    public void testGetForwardingPort() {
        System.out.println("getForwardingPort");
        int expResult = Integer.parseInt(simulatorListenPortNo);
        int result = xmlInstanceSimulateRules.getForwardingPort();
        assertEquals(expResult, result);
    }

    /**
     * Test of forward method, of class HttpInterceptWorker. No matching
     * simulator rule response so this request is forwarded on
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testForward() throws Exception {
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        xmlInstanceForwarding.process(xmlRequestForwarding, resp);
        //String log = waitForFile();
        // need to test the response log
        // resp.getHttpHeader() is null for responses from forwarded end points
        assertEquals(null, resp.getHttpHeader());
    }

    /**
     * Test of processXmlAcceptXml method, of class HttpInterceptWorker.
     * Matching simulator rule response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessXmlAcceptXml() throws Exception {
        System.out.println("processXmlAcceptXml");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        xmlInstanceSimulateRules.process(xmlRequestSimulateRules, resp);
        String log = waitForFile();
        checkReturnContentType(resp, FHIR_XML_MIMETYPE_STU3);
    }

    /**
     * Test of processXmlAcceptXmlFormatJson method, of class
     * HttpInterceptWorker. Matching simulator rule response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessXmlAcceptXmlFormatJson() throws Exception {
        System.out.println("processXmlAcceptXmlFormatJson");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        xmlRequestSimulateRules.setRequestContext(GET_STRUCTURED_RECORD_URL + "?_format=json");
        xmlInstanceSimulateRules.process(xmlRequestSimulateRules, resp);
        String log = waitForFile();

        checkReturnContentType(resp, FHIR_JSON_MIMETYPE_STU3);
    }

    /**
     * Test of processXmlAcceptJson method, of class HttpInterceptWorker.
     * Matching simulator rule response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessXmlAcceptJson() throws Exception {
        System.out.println("processXmlAcceptJson");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);

        xmlRequestSimulateRules.setHeader("Accept", FHIR_JSON_MIMETYPE_STU3);
        xmlInstanceSimulateRules.process(xmlRequestSimulateRules, resp);
        String log = waitForFile();

        checkReturnContentType(resp, FHIR_JSON_MIMETYPE_STU3);
    }

    /**
     * Test of processJsonAcceptJson method, of class HttpInterceptWorker.
     * Matching simulator rule response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessJsonAcceptJson() throws Exception {
        System.out.println("processJsonAcceptJson");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        jsonRequestSimulateRules.setHeader("Accept", FHIR_JSON_MIMETYPE_STU3);
        jsonInstanceSimulateRules.process(jsonRequestSimulateRules, resp);
        String log = waitForFile();

        checkReturnContentType(resp, FHIR_JSON_MIMETYPE_STU3);
    }

    /**
     * Test of processJsonAcceptJsonFormatXml method, of class
     * HttpInterceptWorker. Matching simulator rule response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessJsonAcceptJsonFormatXml() throws Exception {
        System.out.println("processJsonAcceptJsonFormatXml");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        jsonRequestSimulateRules.setHeader("Accept", FHIR_JSON_MIMETYPE_STU3);
        jsonRequestSimulateRules.setRequestContext(GET_STRUCTURED_RECORD_URL + "?_format=xml");
        jsonInstanceSimulateRules.process(jsonRequestSimulateRules, resp);
        String log = waitForFile();

        checkReturnContentType(resp, FHIR_XML_MIMETYPE_STU3);
    }

    /**
     * Test of processJsonAcceptXml method, of class HttpInterceptWorker.
     * Matching simulator rule response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessJsonAcceptXml() throws Exception {
        System.out.println("process_json_accept_xml");
        assertTrue(!folderExists(ENDPOINT_FOLDER));
        HttpResponse resp = new HttpResponse(ostream);
        jsonRequestSimulateRules.setHeader("Accept", FHIR_XML_MIMETYPE_STU3);
        jsonInstanceSimulateRules.process(jsonRequestSimulateRules, resp);
        String log = waitForFile();

        checkReturnContentType(resp, FHIR_XML_MIMETYPE_STU3);
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

    /**
     * wait for worker thread to complete. This takes quite a long time check
     * non zero file
     *
     * @return content of file
     * @throws InterruptedException
     */
    private String waitForFile() throws InterruptedException, Exception {
        final int TIMEOUT = 15;
        int timeoutCount = 0;
        File endPointFolder = new File(ENDPOINT_FOLDER);
        
        // wait for worker thread to complete. This takes quite a long time
        while (++timeoutCount < TIMEOUT && ! endPointFolder.exists()) {
              Thread.sleep(TIMEOUT*1000);
        }

        if (timeoutCount >= TIMEOUT) {
            fail("Timed out after " + TIMEOUT + "s");
        }

        assertTrue(endPointFolder.exists());
        int expResult = 1;
        assertEquals(expResult, endPointFolder.listFiles().length);

        // check non zero file length
        assertTrue(endPointFolder.listFiles()[0].length() > 0);
        String log = Utils.readFile2String(endPointFolder.listFiles()[0].getAbsolutePath());
        System.out.println(log);
        return log;
    }

    private void checkReturnContentType(HttpResponse resp, String expResult) {
        uk.nhs.digital.mait.tkwx.http.HttpHeaderManager hm = new uk.nhs.digital.mait.tkwx.http.HttpHeaderManager();
        hm.parseHttpHeaders(resp.getHttpHeader());
        assertEquals(expResult, hm.getHttpHeaderValue(CONTENT_TYPE_HEADER));
    }
}
