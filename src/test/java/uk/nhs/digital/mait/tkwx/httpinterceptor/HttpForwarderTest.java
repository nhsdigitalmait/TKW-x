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
 limitations under the License.kill
 */
package uk.nhs.digital.mait.tkwx.httpinterceptor;

import uk.nhs.digital.mait.jwttools.AuthorisationGenerator;
import static com.helger.commons.http.CHttpHeader.CONTENT_LENGTH;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor.HttpInterceptHandler;
import uk.nhs.digital.mait.tkwx.tk.GeneralConstants;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpInterceptorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.tkwx.ProcessStreamDumper;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SSP_INTERACTION_ID_HEADER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;
import static uk.nhs.digital.mait.tkwx.util.Utils.readPropertiesFile;

/**
 *
 * @author simonfarrow
 */
public class HttpForwarderTest {

    private static Process process;

    private HttpForwarder instance;
    private OutputStream ostream;
    private static String interceptorPortNo;

    private static final String INTERCEPTOR_LOG = "src/test/resources/interceptor.log";
    private static final String NNN = "9658218873";

    public HttpForwarderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // start a GP Connect host responder on 4848
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", System.getenv("TKWROOT") + "/TKW-x.jar", "-simulator", System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        process = pb.start();
        ProcessStreamDumper.dumpProcessStreams(process);
        
        // setup the interceptor
        String propsPath = System.getenv("TKWROOT") + "/config/HTTP_Interceptor/tkw-x.properties";
        Properties properties = new Properties();
        readPropertiesFile(propsPath, properties);
        interceptorPortNo = properties.getProperty("tks.HttpTransport.listenport");

        ToolkitSimulator tks = new ToolkitSimulator(propsPath);
        new HttpInterceptorMode().init(tks);
    }

    @AfterClass
    public static void tearDownClass() {
        process.destroy();
    }

    @Before
    public void setUp() throws Exception {
        HttpInterceptHandler httpInterceptHandler = new HttpInterceptHandler();
        HttpRequest req = new HttpRequest("id");
        req.setHeader("host", "localhost:"+interceptorPortNo);
        HttpInterceptWorker httpInterceptWorker = new HttpInterceptWorker(req, httpInterceptHandler);
        httpInterceptWorker.logfile = new LoggingFileOutputStream(new File(INTERCEPTOR_LOG));
        instance = new HttpForwarder(httpInterceptWorker);
        ostream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws IOException {
        ostream.close();
        new File(INTERCEPTOR_LOG).delete();
    }

    /**
     * Test of forward method, of class HttpForwarder.
     * expects a correspondence simulator to be running at 4848
     * @throws java.lang.Exception
     */
    @Test
    public void testForward() throws Exception {
        System.out.println("forward");
        String message = Utils.readFile2String("src/test/resources/get_structured_record.xml");
        message = message.replaceAll("__NNN__",NNN);
        message = message.replaceAll("__SECTION__","SUM");
        
        byte[] buf = message.getBytes();

        HttpResponse resp = new HttpResponse(ostream);
        HttpRequest req = new HttpRequest("id");
        req.setHeader(SSP_INTERACTION_ID_HEADER, "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getstructuredrecord-1");
        req.setHeader("ssp-from", "123456789012");
        req.setHeader("ssp-to", "123456789013");
        req.setHeader("ssp-traceid", "traceid");
        req.setHeader("host", "localhost");
        AuthorisationGenerator authorisationGenerator
           = new AuthorisationGenerator("src/test/resources/gp_connect_jwt_template.fhir3.txt");
        req.setHeader("Authorization", "Bearer "+ authorisationGenerator.getAuthorisationString("pid", NNN, "secret",true,false));
  
        req.setRequestType("POST");
        req.setRequestContext("/gpconnect-demonstrator/v1/fhir/Patient/$gpc.getstructuredrecord");
        req.setProtocol("HTTP/1.1");
        req.setHeader("Content-type","application/fhir+xml");
        // TODO why does this line not work?
        //req.setContentLength(buf.length);
        req.setHeader(CONTENT_LENGTH,""+buf.length);
        instance.forward(buf, resp, req);
        instance.join();
        String response = new String(resp.getHttpBuffer());
        System.out.println(response);
        assertTrue(response.contains("OperationOutcome"));
        
        // check the interceptor log file has been created
        assertTrue(fileExists(INTERCEPTOR_LOG));
    }

    /**
     * Test of run method, of class HttpForwarder.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // invoked by forward with correct parameters set
    }

}
