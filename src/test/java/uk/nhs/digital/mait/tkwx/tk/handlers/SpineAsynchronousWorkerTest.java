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

import java.io.ByteArrayInputStream;
import java.io.File;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestName;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 *
 * @author sifa2
 */
public class SpineAsynchronousWorkerTest extends AbstractHandler {

    @Rule
    public TestName testName = new TestName();

    private HttpRequest req;
    private HttpResponse resp;
    private SpineAsynchronousWorker instance;

    private static final String TEST_LOG_FILE = "src/test/resources/testlog.txt";
    // QUPA_IN000006UK02 mim 6? advanced trace query (async) response is QUPA_IN000011
    // lookup fails for handle when "from asid" is SIAB-001
    private static final String INTERACTION = "QUPA_IN000006UK02";
    private static final String ACTION = "urn:nhs:names:services:pdsquery/" + INTERACTION;

    public static final String HOST_ASID = "SIAB-001";
    public static final String CLIENT_ASID = "SIAB-Client-200";

    private String modifiedContent;

    private static File simulatorSavedMessages;

    public SpineAsynchronousWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        // This has test message 
        // from asid SIAB-Client-200
        // to asid SIAB-001

        // extracts from the lookup table simulator_config/siab-test-sds-ref.xml thse are the only viable entries for testing mim 6 adv trace 
        // asid            service                         interaction       party key   endpoint
        // SIAB-001        urn:mhs:names:services:pdsquery QUPA_IN000006UK02 RHM-0000953 http://127.0.0.1:4001/       adv tr request     client looks up server
        // SIAB-Client-200 urn:mhs:names:services:pdsquery QUPA_IN000011UK02 YEA-0000805 http://127.0.0.1:4444/       adv trace response server looks up client
        AbstractHandler.setUpClass(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000006UK02_QUPA_IN000011UK02.xml");

        simulatorSavedMessages = new File(TEMPFOLDER + "/" + CLIENT_ASID);
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
        modifiedContent = content;
        // HACK to get all tests cases to have successful lookups
        // handle fails on the lookup for the response so don't modify the asids for the handle test
        if (!testName.getMethodName().equals("testHandle")) {
            modifiedContent = content.replaceAll("(?s)" + CLIENT_ASID, HOST_ASID);
            istream = new ByteArrayInputStream(modifiedContent.getBytes());
        }

        // This one does not seem to send when handle is invoked
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/SPINE_MTH/tkw-x.properties");
        SimulatorMode m = new SimulatorMode();
        m.init(t);
        req = new HttpRequest("id");
        req.setHeader(SOAP_ACTION_HEADER, ACTION);
        req.setHeader(CONTENT_LENGTH_HEADER, "" + modifiedContent.length());
        req.setRequestContext("/reliablemessaging/reliablerequest");
        req.setRequestType("POST");
        req.setInputStream(istream);

        resp = new HttpResponse(ostream);
        SpineAsynchronousSoapRequestHandler sasrh = new SpineAsynchronousSoapRequestHandler();
        sasrh.setToolkit(new HttpTransport());
        sasrh.setSavedMessagesDirectory(TEMPFOLDER);
        instance = new SpineAsynchronousWorker(sasrh);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        new File(TEST_LOG_FILE).delete();
    }

    /**
     * Test of handle method, of class SpineAsynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        // writes a 202 sync response into resp
        instance.handle(req, resp);
        instance.logfile.close();

        String respResult = resp.getHttpHeader();
        assertTrue(respResult.contains("202 Accepted"));

        assertEquals(2, simulatorSavedMessages.list().length);
    }

    /**
     * Test of doChecks method, of class SpineAsynchronousWorker. calls
     * readMessage and does a signer verify if appropriate
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
        boolean expResult = true;
        boolean result = instance.doChecks(req, resp);
        instance.logfile.close();
        assertEquals(expResult, result);
    }

    /**
     * Test of doProcess method, of class SpineAsynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoProcess() throws Exception {
        System.out.println("doProcess");
        // not convinced this is a valid method to test outside of invocation by handle
        //instance.doProcess(req, resp);
    }

    /**
     * Test of readMessage method, of class SpineAsynchronousWorker. reads the
     * request and populates various attributes, writes a logfile containing the
     * request to the saved messages directory
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadMessage() throws Exception {
        System.out.println("readMessage");

        instance.readMessage(req);
        instance.logfile.close();

        String expResult = INTERACTION;
        assertEquals(expResult, instance.sm.getService());
        // these are swapped in the test message so this actually the sending asid in this case
        expResult = HOST_ASID;
        assertEquals(expResult, instance.sndAsid);
        // one or two files a log (and a sig)
        String ssl = System.getProperty(DONTSIGNLOGS_PROPERTY, "N").toUpperCase();
        // NB now in a named subfolder
        File saved_messages_folder = new File(TEMPFOLDER + "/" + HOST_ASID);
        assertEquals(ssl.equals("Y") ? 1 : 2, saved_messages_folder.listFiles().length);
    }

    /**
     * Test of synchronousAck method, of class SpineAsynchronousWorker.
     * populates the synchronous response object
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSynchronousAck() throws Exception {
        System.out.println("synchronousAck");
        ServiceResponse r = new ServiceResponse();
        instance.synchronousAck(req, resp, r);

        String expResult = "202 Accepted";
        assertTrue(resp.getHttpHeader().contains(expResult));
        expResult = "<eb:Acknowledgment";
        assertTrue(new String(resp.getHttpBuffer()).contains(expResult));
    }

    /**
     * Test of synchronousEbXMLError method, of class SpineAsynchronousWorker.
     * ebxml fail sync response puts this into resp
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSynchronousEbXMLError() throws Exception {
        System.out.println("synchronousEbXMLError");
        ServiceResponse r = new ServiceResponse();
        r.setResponse("<a>response body<a>");
        instance.logfile = new LoggingFileOutputStream(TEST_LOG_FILE);
        instance.synchronousEbXMLError(req, resp, r);
        instance.logfile.close();

        String expResult = "200 OK";
        assertTrue(resp.getHttpHeader().contains(expResult));
        expResult = "<a>response body<a>";
        assertTrue(new String(resp.getHttpBuffer()).contains(expResult));
    }

    /**
     * Test of makeAsyncMessage method, of class SpineAsynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeAsyncMessage() throws Exception {
        System.out.println("makeAsyncMessage");
        ServiceResponse ruleresponse = new ServiceResponse();
        ruleresponse.setResponse("response");
        ruleresponse.setAction(ACTION);
        instance.logfile = new LoggingFileOutputStream(TEST_LOG_FILE);
        instance.readMessage(req);

        String expResult = "__PAYLOAD_BODY__";
        String result = instance.makeAsyncMessage(ruleresponse);
        assertTrue(result.contains(expResult));
        instance.logfile.close();
    }

    /**
     * Test of makeSafeReturnUrl method, of class SpineAsynchronousWorker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeSafeReturnUrl() throws Exception {
        System.out.println("makeSafeReturnUrl");
        String u = "http://localhost?a&b";
        String expResult = "http://localhost?a&amp;b";
        String result = instance.makeSafeReturnUrl(u);
        assertEquals(expResult, result);
    }

    /**
     * Test of asynchronousResponse method, of class SpineAsynchronousWorker.
     * Builds the async response and actually does the sending
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAsynchronousResponse() throws Exception {
        System.out.println("asynchronousResponse");
        ServiceResponse ruleresponse = new ServiceResponse();
        ruleresponse.setResponse("<a>response</a>");
        ruleresponse.setAction(ACTION);
        instance.logfile = new LoggingFileOutputStream(TEST_LOG_FILE);
        instance.readMessage(req);

        instance.asynchronousResponse(ruleresponse);
        instance.logfile.close();
    }
}
