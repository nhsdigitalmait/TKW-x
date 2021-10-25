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
import java.io.IOException;
import java.util.Properties;
import uk.nhs.digital.mait.tkwx.AbstractHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.SAVEDMESSAGES_PROPERTY;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.tkwx.tk.handlers.SpineAsynchronousWorkerTest.CLIENT_ASID;

/**
 *
 * @author sifa2
 */
public class SpineAsynchronousSoapRequestHandlerTest extends AbstractHandler {

    private static String m;
    private SpineAsynchronousSoapRequestHandler instance;
    private HttpRequest req;
    private HttpResponse resp;

    private static File simulatorSavedMessages;

    public SpineAsynchronousSoapRequestHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, IOException {
        AbstractHandler.setUpClass(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000006UK02_QUPA_IN000011UK02.xml");
        m = content.replaceFirst("(?s)^.*?(<SOAP:Envelope)", "$1").replaceFirst("(?s)(</SOAP:Envelope>).*$", "$1");

        Properties props = new Properties();
        props.load(new FileReader(System.getenv("TKWROOT") + "/config/SPINE_MTH/tkw-x.properties"));
        simulatorSavedMessages = new File(props.getProperty(SAVEDMESSAGES_PROPERTY) + "/" + CLIENT_ASID);
        if (simulatorSavedMessages.exists()) {
            deleteFolderAndContents(simulatorSavedMessages);
        }
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

        instance = new SpineAsynchronousSoapRequestHandler();
        instance.setSavedMessagesDirectory(TEMPFOLDER);
        instance.setToolkit(new HttpTransport());

        req = new HttpRequest("id");
        req.setHeader(SOAP_ACTION_HEADER, "urn:nhs:names:services:pdsquery/QUPA_IN000006UK02");
        req.setHeader(CONTENT_LENGTH_HEADER, "" + content.length());
        req.setRequestContext("/reliablemessaging/reliablerequest");
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
     * Test of extractFromPartyID method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractFromPartyID() throws Exception {
        System.out.println("extractFromPartyID");
        String expResult = "RHM-801710";
        String result = instance.extractFromPartyID(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractToPartyID method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractToPartyID() throws Exception {
        System.out.println("extractToPartyID");
        String expResult = "SIAB-C-1";
        String result = instance.extractToPartyID(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractConversationID method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractConversationID() throws Exception {
        System.out.println("extractConversationID");
        String expResult = "993C8839-8FC0-11E2-A805-AFEB563F31BA";
        String result = instance.extractConversationID(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractCPAID method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractCPAID() throws Exception {
        System.out.println("extractCPAID");
        String expResult = "S2001924A2012004";
        String result = instance.extractCPAID(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractMessageId method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractMessageId() throws Exception {
        System.out.println("extractMessageId");
        String expResult = "993C8839-8FC0-11E2-A805-AFEB563F31BA";
        String result = instance.extractMessageId(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractRcvAsid method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractRcvAsid() throws Exception {
        System.out.println("extractRcvAsid");
        String expResult = "";
        String result = instance.extractRcvAsid(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractSndAsid method, of class
     * SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractSndAsid() throws Exception {
        System.out.println("extractSndAsid");
        String expResult = "";
        String result = instance.extractSndAsid(m);
        assertEquals(expResult, result);
    }

    /**
     * Test of setToolkit method, of class SpineAsynchronousSoapRequestHandler.
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
     * Test of handle method, of class SpineAsynchronousSoapRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInvoke() throws Exception {
        System.out.println("invoke");
        ServiceResponse sr = instance.invoke(req);
        assertEquals(sr.getCode(), 202);
        assertEquals(sr.getCodePhrase(), "Accepted");

    }

    /**
     * Test of getAckLoadException method, of class
     * SpineAsynchronousSoapRequestHandler.
     */
    @Test
    public void testGetAckLoadException() {
        System.out.println("getAckLoadException");
        Exception expResult = null;
        Exception result = instance.getAckLoadException();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSyncAckTemplate method, of class
     * SpineAsynchronousSoapRequestHandler.
     */
    @Test
    public void testGetSyncAckTemplate() {
        System.out.println("getSyncAckTemplate");
        String expResult = "SOAP:Envelope";
        String result = instance.getSyncAckTemplate();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getAsyncWrapper method, of class
     * SpineAsynchronousSoapRequestHandler.
     */
    @Test
    public void testGetAsyncWrapper() {
        System.out.println("getAsyncWrapper");
        String expResult = "RelatesTo";
        String result = instance.getAsyncWrapper();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getTimestampOffset method, of class
     * SpineAsynchronousSoapRequestHandler.
     */
    @Test
    public void testGetTimestampOffset() {
        System.out.println("getTimestampOffset");
        int expResult = 0;
        int result = instance.getTimestampOffset();
        assertEquals(expResult, result);
    }
}
