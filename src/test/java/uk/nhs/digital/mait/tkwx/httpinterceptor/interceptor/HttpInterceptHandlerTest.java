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
package uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor;

import static com.helger.commons.http.CHttpHeader.CONTENT_LENGTH;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.ProcessStreamDumper;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.FORWARDINGPORTPROPERTY;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpInterceptorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author SIFA2
 */
public class HttpInterceptHandlerTest {

    private HttpInterceptHandler instance;
    private OutputStream ostream;

    private static Process process;

    // TODO derive these from the prpoerties file
    private static final String TESTSAVED_MESSAGES_FOLDER = System.getenv("TKWROOT") + "/config/HTTP_Interceptor/simulator_saved_messages";
    private static final String VALIDATOR_REPORTS_FOLDER = System.getenv("TKWROOT") + "/config/HTTP_Interceptor/validator_reports";
    private static final String ENDPOINT_MESSAGES_FOLDER = TESTSAVED_MESSAGES_FOLDER + "/SIAB-Client-200/";
    private static final String ENDPOINT_REPORTS_FOLDER = VALIDATOR_REPORTS_FOLDER + "/SIAB-Client-200/";

    private static final String TEMP_PROPERTIES_FILE = "src/test/resources/temp.properties";

    public HttpInterceptHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        // start an async Spine host responder
        ProcessBuilder pb = new ProcessBuilder();
        String responderPropsFile = System.getenv("TKWROOT") + "/config/SPINE_MTH/tkw-x.properties";
        pb.command("java", "-jar", System.getenv("TKWROOT") + "/TKW-x.jar", "-simulator", responderPropsFile);
        process = pb.start();
        ProcessStreamDumper.dumpProcessStreams(process);

        Properties responderProps = new Properties();
        responderProps.load(new FileReader(responderPropsFile));

        Properties interceptorProps = new Properties();
        interceptorProps.load(new FileReader(System.getenv("TKWROOT") + "/config/HTTP_Interceptor/tkw-x.properties"));
        // set the interceptor to forward to whichever port the responder is listening on
        interceptorProps.setProperty(FORWARDINGPORTPROPERTY, responderProps.getProperty("tks.HttpTransport.listenport"));
        try (FileWriter fw = new FileWriter(TEMP_PROPERTIES_FILE)) {
            for (Object key : interceptorProps.keySet()) {
                fw.write(key.toString() + " " + interceptorProps.getProperty((String) key) + "\r\n");
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
    public void setUp() {
        instance = new HttpInterceptHandler();
        ostream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws IOException {
        ostream.close();
        for (File file : new File(ENDPOINT_MESSAGES_FOLDER).listFiles()) {
            file.delete();
        }
    }

    /**
     * Test of getSavedMessagesDirectory method, of class HttpInterceptHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSavedMessagesDirectory() throws Exception {
        System.out.println("getSavedMessagesDirectory");
        instance.setToolkit(new HttpTransport());
        String expResult = System.getenv("TKWROOT") + "/config/HTTP_Interceptor/simulator_saved_messages";
        String result = instance.getSavedMessagesDirectory();
        assertTrue(expResult.equalsIgnoreCase(result));
    }

    /**
     * Test of setToolkit method, of class HttpInterceptHandler.
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
     * Test of handle method, of class HttpInterceptHandler. Coincidentally this
     * indirectly tests Spine Validation
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");

        // The http interceptor has a variety of deployment models
        // This one is as Spine Validator Proxy
        // No simulator rules just log the request, validate it and pass the request on to the real host
        String pdsrq = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml")));

        // remove the http preamble we are adding our own
        pdsrq = pdsrq.replaceFirst("(?s)^.*<\\?", "<?");

        String interactionId = "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01";
        HttpRequest req = new HttpRequest("id");
        req.setHeader(SOAP_ACTION_HEADER, interactionId);
        req.setHeader("Host", "127.0.0.1");
        req.setRequestType("POST");
        String path = "/syncservice-pds/pds";
        req.setRequestContext(path);
        req.setProtocol("HTTP/1.1");
        req.setHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);
        // This doesn't work for some reason
        //req.setContentLength(pdsrq.length());
        req.setHeader(CONTENT_LENGTH, "" + pdsrq.getBytes().length);
        req.setInputStream(new ByteArrayInputStream(pdsrq.getBytes()));
        HttpResponse resp = new HttpResponse(ostream);
        instance.setToolkit(new HttpTransport());

        File endpointMessagesFolder = new File(ENDPOINT_MESSAGES_FOLDER);
        File endpointReportsFolder = new File(ENDPOINT_REPORTS_FOLDER);
        int messagesCount = endpointMessagesFolder.listFiles().length;
        int reportsCount = endpointReportsFolder.listFiles().length;
        // proxy will attempt to connect to the real endpoint given in host. 
        String params = "";
        instance.handle(path, params, req, resp);

        int expResult = 1;
        assertEquals(expResult, endpointMessagesFolder.listFiles().length - messagesCount);
        
        // wait for worker thread to complete. This takes quite a long time 5s is not enough
        int timeoutCount = 0;
        while ( Thread.activeCount() > 3  && ++timeoutCount < 12) {
            Thread.sleep(1000);
        }

        File[] files = endpointMessagesFolder.listFiles();
        Arrays.sort(files, (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));
        File latestMessageFile = files[files.length - 1];

        // check the latest saved messages file is now populated with the log
        assertTrue(latestMessageFile.length() > 0);
        latestMessageFile.delete();

        // look for a log file and associated validation report
        //  in the config/HTTP_Interceptor/validator_reports folder sub folder SIAB-Client-200
        // for validator_reports_<timestamp>.html and  urn_nhs_names_services_pdsquery_QUPA_IN000005UK01_<timestamp>.log
        expResult = 2;
        assertEquals(expResult, endpointReportsFolder.listFiles().length - reportsCount);

        files = endpointReportsFolder.listFiles();
        Arrays.sort(files, (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));
        String suffix = interactionId.replaceAll("[:/]", "_") + "_[0-9]{17}\\.";
        for (int i = 1; i <= 2; i++) {
            File reportFile = files[files.length - i];
            if (reportFile.getName().endsWith(".log")) {
                assertTrue(reportFile.getName().matches("^" + suffix + "log$"));
            } else {
                // validation report file name now more closely aligned with log file name
                assertTrue(reportFile.getName().matches("^validation_report_" + suffix+"html$"));
            }
            reportFile.delete();
        }

        // TODO the other modes are
        // soap non itk with simulator rules (forward if no rules if no rules fire)
        // not soap eg fhir rest with simulator rules (forward if no rules fire)
    }

}
