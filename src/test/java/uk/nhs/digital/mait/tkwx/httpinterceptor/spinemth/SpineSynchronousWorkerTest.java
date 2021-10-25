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
package uk.nhs.digital.mait.tkwx.httpinterceptor.spinemth;

import uk.nhs.digital.mait.tkwx.tk.handlers.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.tkwx.tk.handlers.SpineAsynchronousWorkerTest.CLIENT_ASID;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 *
 * @author sifa2
 */
public class SpineSynchronousWorkerTest extends AbstractHandler {

    private HttpRequest req;
    private HttpResponse resp;
    private SpineSynchronousWorker instance;

    private static File simulatorSavedMessages;
    private static final String TEST_LOG_FILE = "src/test/resources/testlog.txt";

    public SpineSynchronousWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, IOException {
        AbstractHandler.setUpClass(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_QUQI_IN010000UK14.xml");
        Properties props = new Properties();
        props.load(new FileReader(System.getenv("TKWROOT") + "/config/SPINE_MTH/tkw-x.properties"));
        simulatorSavedMessages = new File(props.getProperty(SAVEDMESSAGES_PROPERTY) + "/" + CLIENT_ASID);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        deleteFolderAndContents(simulatorSavedMessages);
        AbstractHandler.tearDownClass();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/SPINE_MTH/tkw-x.properties");
        SimulatorMode m = new SimulatorMode();
        m.init(t);
        req = new HttpRequest("id");
        req.setHeader(SOAP_ACTION_HEADER, "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
        req.setHeader(CONTENT_LENGTH_HEADER, "" + content.length());
        req.setRequestContext("/syncservice-pds/pds");
        req.setRequestType("POST");
        req.setInputStream(istream);
   
        resp = new HttpResponse(ostream);
        SpineSynchronousSoapRequestHandler ssrh = new SpineSynchronousSoapRequestHandler();
        ssrh.setSavedMessagesDirectory(TEMPFOLDER);
        ssrh.setToolkit(new HttpTransport());
        instance = new SpineSynchronousWorker(ssrh);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        new File(TEST_LOG_FILE).delete();
    }

    /**
     * Test of handle method, of class SpineSynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        ServiceResponse sr = instance.handle(req);
        String result = sr.getResponse();
        // result is an xml response
        String expResult = "<";
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of makeSoapFault method, of class SpineSynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeSoapFault() throws Exception {
        System.out.println("makeSoapFault");
        String faultCode = "";
        String faultString = "";
        String codeContext = "";
        String errorCode = "";
        String severity = "";
        String location = "";
        String description = "";
        String expResult = "Fault";
        String result = instance.makeSoapFault(faultCode, faultString, codeContext, errorCode, severity, location, description);
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of doChecks method, of class SpineSynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
        boolean expResult = true;
        boolean result = instance.doChecks(req);
        assertEquals(expResult, result);
    }

    /**
     * Test of makeWrapper method, of class SpineSynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeWrapper() throws Exception {
        System.out.println("makeWrapper");
        String action = "";
        String expResult = "SOAP-ENV";
        StringBuilder result = instance.makeWrapper(action);
        assertTrue(result.toString().contains(expResult));
    }

    /**
     * Test of synchronousResponse method, of class SpineSynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSynchronousResponse() throws Exception {
        System.out.println("synchronousResponse");
        int httpError = 200;
        String statusText = "OK";
        String soapBody = "message";
        String soapAction = "action";
        ServiceResponse sr = new ServiceResponse();
        sr.setHttpHeaders(new HttpHeaderManager());
        instance.serviceResponse = sr;
        instance.synchronousResponse(httpError, statusText, soapBody, soapAction);


        assertEquals(sr.getCode(), httpError);
        assertEquals(sr.getCodePhrase(), statusText);

        assertTrue(sr.getResponse().contains(soapBody));
        assertEquals(sr.getHttpHeaders().getHttpHeaderValue("SOAPAction"),soapAction);
    }

    /**
     * Test of readMessage method, of class SpineSynchronousWorker
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadMessage() throws Exception {
        System.out.println("readMessage");

        instance.readMessage(req);
        assertNotNull(instance.sm);
        
    }

    /**
     *
     * @param count
     */
    private void deleteLatestFiles(int count) {
        File[] files = simulatorSavedMessages.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        for (int i = 0; i < count; i++) {
            files[i].delete();
        }
    }


}
