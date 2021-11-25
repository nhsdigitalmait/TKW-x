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

import java.io.File;
import java.io.FileReader;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.SAVEDMESSAGES_PROPERTY;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.spinemth.SpineAsynchronousWorkerTest.CLIENT_ASID;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class SpineSynchronousSoapRequestHandlerTest extends AbstractHandler {

    private static Document d;
    private static String m;

    private HttpRequest req;
    private HttpResponse resp;
    private SpineSynchronousSoapRequestHandler instance;

    private static File simulatorSavedMessages;

    public SpineSynchronousSoapRequestHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
        AbstractHandler.setUpClass(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_QUQI_IN010000UK14.xml");
        m = content.replaceFirst("(?s)^.*?(<soap:Envelope)", "$1").replaceFirst("(?s)(</soap:Envelope>).*$", "$1");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        d = builder.parse(new InputSource(new StringReader(m)));

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
        SimulatorMode mode = new SimulatorMode();
        mode.init(t);
        instance = new SpineSynchronousSoapRequestHandler();
        instance.setSavedMessagesDirectory(TEMPFOLDER);
        instance.setToolkit(new HttpTransport());

        req = new HttpRequest("id");
        req.setHeader(SOAP_ACTION_HEADER, "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
        req.setHeader(CONTENT_LENGTH_HEADER, "" + content.length());
        req.setRequestContext("/syncservice-pds/pds");
        req.setRequestType("POST");
        req.setInputStream(istream);

        resp = new HttpResponse(ostream);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getAsyncTTL method, of class SpineSynchronousSoapRequestHandler.
     */
    @Test
    public void testGetAsyncTTL() {
        System.out.println("getAsyncTTL");
        int expResult = 10;
        int result = instance.getAsyncTTL();
        assertEquals(expResult, result);
    }

    /**
     * Test of extractHeader method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractHeader() throws Exception {
        System.out.println("extractHeader");
        String expResult = "soap:Header";
        Node result = instance.extractHeader(d);
        assertEquals(expResult, result.getNodeName());
    }

    /**
     * Test of extractBody method, of class SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractBody() throws Exception {
        System.out.println("extractBody");
        String expResult = "soap:Body";
        Node result = instance.extractBody(d);
        assertEquals(expResult, result.getNodeName());
    }

    /**
     * Test of extractSoapRequest method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractSoapRequest() throws Exception {
        System.out.println("extractSoapRequest");
        String expResult = "#document";
        Node result = instance.extractSoapRequest(d);
        assertEquals(expResult, result.getNodeName());
    }

    /**
     * Test of extractMessageId method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractMessageId() throws Exception {
        System.out.println("extractMessageId");
        String expResult = "uuid:971084C8-5096-41B2-8B7F-7E5A3317B2B8";
        String result = instance.extractMessageId(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractReplyTo method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractReplyTo() throws Exception {
        System.out.println("extractReplyTo");
        String expResult = "10.210.162.81";
        String result = instance.extractReplyTo(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractFaultTo method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractFaultTo() throws Exception {
        System.out.println("extractFaultTo");
        String expResult = null;
        String result = instance.extractFaultTo(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractToAddress method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractToAddress() throws Exception {
        System.out.println("extractToAddress");
        String expResult = "10.210.162.81";
        String result = instance.extractToAddress(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractFromAddress method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractFromAddress() throws Exception {
        System.out.println("extractFromAddress");
        String expResult = "https://pds-sync.nis1.national.ncrs.nhs.uk/syncservice-pds/pds";
        String result = instance.extractFromAddress(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractRcvAsid method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractRcvAsid() throws Exception {
        System.out.println("extractRcvAsid");
        String expResult = "SIAB-Client-200";
        String result = instance.extractRcvAsid(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractSndAsid method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractSndAsid() throws Exception {
        System.out.println("extractSndAsid");
        String expResult = "SIAB-001";
        String result = instance.extractSndAsid(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractNodeXpath method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractNodeXpath() throws Exception {
        System.out.println("extractNodeXpath");
        XPathExpression x = getXpathExtractor("/a");
        InputSource m = new InputSource(new StringReader("<a><b>xxxx</b></a>"));
        String expResult = "xxxx";
        Node result = instance.extractNodeXpath(x, m);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractStringXpath method, of class
     * SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractStringXpath() throws Exception {
        System.out.println("extractStringXpath");
        XPathExpression x = getXpathExtractor("/a/b/text()");
        String expResult = "xxxx";
        InputSource m = new InputSource(new StringReader("<a><b>" + expResult + "</b></a>"));
        String result = instance.extractStringXpath(x, m);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSoapFault method, of class SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSoapFault() throws Exception {
        System.out.println("getSoapFault");
        String expResult = "soap:Envelope";
        instance.setToolkit(new HttpTransport());
        String result = instance.getSoapFault();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getSynchronousWrapper method, of class
     * SpineSynchronousSoapRequestHandler.
     */
    @Test
    public void testGetSynchronousWrapper() {
        System.out.println("getSynchronousWrapper");
        String expResult = "SOAP-ENV:Envelope";
        String result = instance.getSynchronousWrapper();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getSavedMessagesDirectory method, of class
     * SpineSynchronousSoapRequestHandler.
     */
    @Test
    public void testGetSavedMessagesDirectory() {
        System.out.println("getSavedMessagesDirectory");
        String expResult = "xx";
        instance.setSavedMessagesDirectory(expResult);
        String result = instance.getSavedMessagesDirectory();
        assertEquals(expResult, result);
    }

    /**
     * Test of setToolkit method, of class SpineSynchronousSoapRequestHandler.
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
     * Test of handle method, of class SpineSynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInvoke() throws Exception {
        System.out.println("invoke");
        String path = "ignored";
        String params = "ignored";
        ServiceResponse sr = instance.invoke(req);
        String result = sr.getResponse();
        // result is an xml response
        String expResult = "<";
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of handle method, of class SpineSynchronousSoapRequestHandler.
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String pathIgnored = "";
        String paramsIgnored = "";
        instance.handle(pathIgnored, paramsIgnored, req, resp);
    }
}
