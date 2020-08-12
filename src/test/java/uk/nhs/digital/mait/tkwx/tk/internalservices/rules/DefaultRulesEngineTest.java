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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import org.xml.sax.InputSource;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;

/**
 *
 * @author sifa2
 */
public class DefaultRulesEngineTest {

    private DefaultRulesEngine instance;
    private static final String SAMPLE_MESSAGE_PATH = System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml";
    private static final String SOAP_ACTION = SEND_CDA_V2_SERVICE;

    public DefaultRulesEngineTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new DefaultRulesEngine();
        String filename = System.getenv("TKWROOT") + "/config/GP_CONNECT/simulator_config/test_tks_rule_config.txt";
        instance.load(filename);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class DefaultRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        // already done
    }

    /**
     * Test of execute method, of class DefaultRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String type = SOAP_ACTION; // soapaction
        String inputFile = SAMPLE_MESSAGE_PATH;
        String input = new String(Files.readAllBytes(Paths.get(inputFile)));
        int expResult = -1; // in TKW-x world this engine is not usaed and there wont be any rules appropraite
        ServiceResponse result = instance.execute(type, input);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class DefaultRulesEngine. This will throw a no
     * rules expception in TKW-x
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testExecute_String_Node() throws Exception {
        System.out.println("execute");
        String type = SOAP_ACTION;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        String s = new String(Files.readAllBytes(Paths.get(SAMPLE_MESSAGE_PATH)));
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(s.toCharArray())));

        Node input = doc.getDocumentElement();
        int expResult = 500;
        ServiceResponse result = instance.execute(type, input);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of instantiateResponse method, of class DefaultRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse() throws Exception {
        System.out.println("instantiateResponse");
        // does nothing
        ServiceResponse s = null;
        String body = "";
        int expResult = 0;
        ServiceResponse result = instance.instantiateResponse(s, body);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class DefaultRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_5args() throws Exception {
        System.out.println("execute");
        String type = SOAP_ACTION;
        String contextPath = "/";
        String input = new String(Files.readAllBytes(Paths.get(SAMPLE_MESSAGE_PATH)));
        int expResult = -1; // in TKW-x world this engine is not used and there wont be any rules appropraite
        HttpRequest httpRequest = new HttpRequest("");
        httpRequest.setRequestContext(contextPath);
        httpRequest.setInputStream(new ByteArrayInputStream(input.getBytes()));
        httpRequest.setContentLength(input.length());
        httpRequest.setHeader(SOAP_ACTION_HEADER, type);
        ServiceResponse result = instance.execute(httpRequest);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of isRestful method, of class DefaultRulesEngine.
     */
    @Test
    public void testIsRestful() {
        System.out.println("isRestful");
        boolean expResult = false;
        boolean result = instance.isRestful();
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiateResponse method, of class DefaultRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_HttpRequest() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse s = null;
        HttpRequest req = null;
        ServiceResponse result = instance.instantiateResponse(s, req);
        assertNotNull(result);
    }

    /**
     * Test of instantiateResponse method, of class DefaultRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_String() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse s = null;
        String body = "";
        ServiceResponse result = instance.instantiateResponse(s, body);
        assertNotNull(result);
    }

    /**
     * Test of execute method, of class DefaultRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Request() throws Exception {
        System.out.println("execute");
        Request req = null;
        ServiceResponse result = instance.execute(req);
        assertNotNull(result);
    }

    /**
     * Test of processResponses method, of class DefaultRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessResponses() throws Exception {
        System.out.println("processResponses");
        List<Response> responses = new ArrayList<>();
        Object o = null;
        ServiceResponse result = instance.processResponses(responses, o);
        assertNull(result);
    }

    /**
     * Test of setSessionID method, of class DefaultRulesEngine.
     */
    @Test
    public void testSetSessionID() {
        System.out.println("setSessionID");
        Request req = new HttpRequest("");
        instance.setSessionID(req);
    }

}
