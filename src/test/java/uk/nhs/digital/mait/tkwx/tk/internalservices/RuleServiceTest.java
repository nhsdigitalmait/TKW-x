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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import uk.nhs.digital.mait.jwttools.AuthorisationGenerator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Response;

/**
 * This test is now against a restful rules engine in TKW-x
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class RuleServiceTest {

    private RuleService instance;
    private static final String TEST_MESSAGE = "src/test/resources/getcarerecord.xml";
    private static final String NNN = "9476719915";
    
    public RuleServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new RuleService();
        boot();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBootProperties method, of class RuleService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        String expResult = "uk.nhs.digital.mait.tkwx.tk.internalservices.SpineSenderService";
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result.get("tks.classname.SpineSender"));
    }

    /**
     * Test of boot method, of class RuleService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
    }

    /**
     * Test of execute method, of class RuleService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");

        HttpRequest httpRequest = new HttpRequest("id");
        String content = new String(Files.readAllBytes(Paths.get(TEST_MESSAGE)));
        httpRequest.setInputStream(new ByteArrayInputStream(content.getBytes()));
        httpRequest.setContentLength(content.length());
        httpRequest.setRequestType("POST");
        AuthorisationGenerator authorisationGenerator
           = new AuthorisationGenerator("src/test/resources/gp_connect_jwt_template.fhir3.txt");
        httpRequest.setHeader("Authorization", "Bearer "+ authorisationGenerator.getAuthorisationString("pid", NNN, "secret"));

        Object param = httpRequest;
        int expResult = 1;
        // This is currently giving a operation outcome 400 because there is some issue with the structure

        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class RuleService. NB For TKW-x this is now a
     * restful rules engine which means type is httpMethod the second param is
     * the context path not a message for default and mesh the first param is
     * service and the second param is message
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String type = "GET";
        String contextPath = "/";
        int expResult = 1;
        // this is now failing with autthorization header missing which is fair enough
        ServiceResponse result = instance.execute(new String[]{type, contextPath});
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class RuleService.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");
        String type = "urn:nhs-itk:services:201005:SendCDADocument-v1-0";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        InputSource is = new InputSource(new InputStreamReader(new FileInputStream(
                new File(TEST_MESSAGE))));
        Document doc = dbf.newDocumentBuilder().parse(is);
        // this is a Node object
        Object param = doc.getDocumentElement();
        int expResult = 500;
        ServiceResponse result = instance.execute(new Object[]{type, param});
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of reconfigure method, of class RuleService.
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_Properties() throws Exception {
        System.out.println("reconfigure");
        Properties p = instance.getBootProperties();
        instance.reconfigure(p);
    }

    /**
     * Test of reconfigure method, of class RuleService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = "tks.rules.configuration.file";
        String value = System.getenv("TKWROOT") + "/config/GP_CONNECT/simulator_config/test_tks_rule_config.txt";
        String expResult = null;
        String result = instance.reconfigure(what, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiateResponse method, of class RuleService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse s = new ServiceResponse();
        ArrayList<Response> al = new ArrayList<>();
        Response response = new Response("Response",System.getenv("TKWROOT")+"/config/GP_CONNECT/simulator_config/gp_connect_patients/"+NNN+"_SUM.xml");
        al.add(response);
        s.setScalar(al);
        String body = "";
        int expResult = 0;
        ServiceResponse result = instance.instantiateResponse(s, body);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of isRestful method, of class RuleService.
     */
    @Test
    public void testIsRestful() {
        System.out.println("isRestful");
        boolean expResult = true;
        boolean result = instance.isRestful();
        assertEquals(expResult, result);
    }

    private void boot() throws IOException, Exception {
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        Properties p = new Properties();
        // dont forget to load the internal props
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(propertiesFile));
        String s = "";
        instance.boot(t, p, s);
    }

    /**
     * Test of execute method, of class RuleService.
     * see testExecute_Object
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

    /**
     * Test of instantiateResponse method, of class RuleService.
     * @throws java.lang.Exception
     * see greater granularity testInstantiateResponse tests
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_String() throws Exception {
        System.out.println("instantiateResponse");
    }

    /**
     * Test of instantiateResponse method, of class RuleService.
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_HttpRequest() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse simulatorServiceResponse = new ServiceResponse();
        ArrayList<Response> responses = new ArrayList<>();
        Response response = new Response("name","NONE");
        responses.add(response);
        simulatorServiceResponse.setScalar(responses);
        HttpRequest req = new HttpRequest("");
        int expResult = 0;
        ServiceResponse result = instance.instantiateResponse(simulatorServiceResponse, req);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of setBusy method, of class RuleService.
     */
    @Test
    public void testSetBusy() {
        System.out.println("setBusy");
        boolean busy = false;
        String message = "message";
        instance.setBusy(busy, message);
    }

}
