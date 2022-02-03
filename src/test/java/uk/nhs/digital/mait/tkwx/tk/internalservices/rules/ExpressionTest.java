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
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import javax.xml.transform.stream.StreamSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource.CONTENT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;

/**
 *
 * @author sifa2
 */
public class ExpressionTest {

    private static TestVisitor visitor;

    private Expression instance;
    private HttpRequest req;

    private static final String SESSION_ID = "s1";
    private static final String VARIABLE_NAME = "$v1";
    private static final String VARIABLE_VALUE = "l1";
    
    private static final String TEST_JSON = "{ \"resourceType\" : \"Bundle\" }";
    private static String TEST_JWT;
    
    @org.junit.Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();


    public ExpressionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        visitor = new TestVisitor();
        
        // populate JWT
        TEST_JWT = Base64.getUrlEncoder().encodeToString("{\"a\" : \"b\" }".getBytes())+"."+ 
                Base64.getUrlEncoder().encodeToString("{\"c\" : \"d\" }".getBytes())+".";
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        SessionStateManager.getInstance().resetSession(SESSION_ID);
        // required for schema checks to work
        System.setProperty("tks.rules.simulatorschemacheck", "Y");
//      instance = new Expression("exp_xpathequals", "xpathequals", "/soap:Envelope/soap:Body/itk:DistributionEnvelope/itk:header/@service", SEND_CDA_V2_SERVICE);
        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathequals"));
        req = new HttpRequest("");
    }

    private void popReq(String s) throws Exception {
        req.setInputStream(new ByteArrayInputStream(s.getBytes()));
        req.setContentLength(s.getBytes().length);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of evaluate method, of class Expression.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testEvaluate() throws Exception {
        System.out.println("evaluate");
        popReq(new String(Files.readAllBytes(Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"))));
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathnotequals"));
        expResult = false;
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testAlways() throws Exception {
        System.out.println("always");
        boolean expResult = true;
        instance = new Expression(visitor.getExpressionCtx().get("exp_always"));
        popReq("");
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_never"));
        expResult = false;
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testXpathExists() throws Exception {
        System.out.println("xpathexists");
        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathexists"));

        // populated element
        popReq("<fhir:Bundle xmlns:fhir=\"http://hl7.org/fhir\">xxx</fhir:Bundle>");
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        // empty element
        HttpRequest empty = new HttpRequest("");
        String o = "<fhir:Bundle xmlns:fhir=\"http://hl7.org/fhir\"/>";
        empty.setInputStream(new ByteArrayInputStream(o.getBytes()));
        empty.setContentLength(o.getBytes().length);
        result = instance.evaluate(empty);
        assertEquals(expResult, result);

        expResult = false;
        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathnotexists"));

        result = instance.evaluate(req);
        assertEquals(expResult, result);

        result = instance.evaluate(empty);
        assertEquals(expResult, result);
    }

    @Test
    public void testXpathIn() throws Exception {
        System.out.println("xpathin");
        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathin"));

        popReq("<fhir:Bundle xmlns:fhir=\"http://hl7.org/fhir\">a b</fhir:Bundle>");
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathnotin"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);

        SimulatorRulesParser.ExpressionContext ctx = visitor.getExpressionCtx().get("exp_xpathin_embedded_quote");
        String strResult = ctx.expression_two_arg().xpath_arg(1).getText();
        String expStrResult = "\"w x\"";
        assertEquals(expStrResult, strResult);

    }

    @Test
    public void testXpathMatches() throws Exception {
        System.out.println("xpathmatches");
        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathmatches"));

        popReq("<fhir:Bundle xmlns:fhir=\"http://hl7.org/fhir\">a</fhir:Bundle>");
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathnotmatches"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testXpathCompare() throws Exception {
        System.out.println("xpathcompare");
        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathcompare"));

        popReq("<fhir:Bundle xmlns:fhir=\"http://hl7.org/fhir\">a<fhir:Element>b</fhir:Element></fhir:Bundle>");
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_xpathnotcompare"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testXslt() throws Exception {
        System.out.println("xslt");
        instance = new Expression(visitor.getExpressionCtx().get("exp_xslt"));

        popReq("<fhir:Bundle xmlns:fhir=\"http://hl7.org/fhir\">a</fhir:Bundle>");

        // the transform ensures the result includes ERROR which *should* cause a failure
        // see manual for details of why it should be this way round ie fail on  a match
        // but the reality is that its the wrong way round cf validation. There are multiple uses
        // of this so we have to live with it.
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testSchema() throws Exception {
        System.out.println("schema");
        popReq(new String(Files.readAllBytes(Paths.get(
                System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml"))));

        instance = new Expression(visitor.getExpressionCtx().get("exp_schema"));

        boolean expResult = true;

        // invokes saxValidation with DE at the root
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        req = new HttpRequest("");
        popReq(new String(Files.readAllBytes(Paths.get(
                System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"))));
        // invokes doDomValidation with an xpath to the DE
        instance = new Expression(visitor.getExpressionCtx().get("exp_schema_xpath"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    /**
     * Test of doDomValidation method, of class Expression.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoDomValidation() throws Exception {
        System.out.println("doDomValidation");
        StreamSource toValidate
                = new StreamSource(new FileReader(
                        System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
        StreamSource schema
                = new StreamSource(new FileReader(
                        System.getenv("TKWROOT") + "/contrib/DMS_Schema/ITK_Core/Schemas/distributionEnvelope-v2-0.xsd"));
        boolean stripHeader = false;
        boolean expResult = true;
        boolean result = instance.doDomValidation(toValidate, schema, stripHeader);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValidationRoot method, of class Expression.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValidationRoot() throws Exception {
        System.out.println("getValidationRoot");
        StreamSource s = new StreamSource(new FileReader(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
        boolean stripHeader = false;
        String expResult = "service";
        Node result = instance.getValidationRoot(s, stripHeader);
        assertEquals(expResult, result.getLocalName());
    }

    /**
     * Test of getName method, of class Expression.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "exp_xpathequals";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMatchSource method, of class Expression.
     */
    @Test
    public void testGetMatchSource() {
        System.out.println("getMatchSource");
        MatchSource expResult = CONTENT;
        MatchSource result = instance.getMatchSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMatchSource2 method, of class Expression.
     */
    @Test
    public void testGetMatchSource2() {
        System.out.println("getMatchSource2");
        MatchSource expResult = null;
        MatchSource result = instance.getMatchSource2();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMatchContent method, of class Expression.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetMatchContent() throws Exception {
        System.out.println("getMatchContent");
        String name = VARIABLE_NAME;
        String expResult = VARIABLE_VALUE;
        SessionStateManager ssm = SessionStateManager.getInstance();
        ssm.setCurrentSessionID(SESSION_ID);
        ssm.setValue(SESSION_ID, name, expResult);
        MatchSource matchSource = MatchSource.VARIABLE;
        String result = Expression.getMatchContent(req, matchSource, name);
        assertEquals(expResult, result);
    }

    @Test
    public void testVariableContains() throws Exception {
        System.out.println("variableContains");
        instance = new Expression(visitor.getExpressionCtx().get("variable_contains"));

        boolean expResult = false;

        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);
        SessionStateManager ssm = SessionStateManager.getInstance();
        ssm.setCurrentSessionID(SESSION_ID);
        ssm.setValue(SESSION_ID, VARIABLE_NAME, VARIABLE_VALUE);
        expResult = true;
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testContainsVariable() throws Exception {
        System.out.println("containsVariable");
        instance = new Expression(visitor.getExpressionCtx().get("contains_variable"));

        boolean expResult = false;
        req.setRequestContext(VARIABLE_VALUE);

        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        SessionStateManager ssm = SessionStateManager.getInstance();
        ssm.setCurrentSessionID(SESSION_ID);
        ssm.setValue(SESSION_ID, VARIABLE_NAME, VARIABLE_VALUE);
        expResult = true;
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testJsonpathExists() throws Exception {
        System.out.println("jsonpathexists");
        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathexists"));
        
        // populated element
        popReq(TEST_JSON);
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathnotexists"));

        result = instance.evaluate(req);
        assertEquals(expResult, result);
        
        // now for JWT header and payload
        req.setHeader("Authorization", "Bearer " + TEST_JWT);
        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpath_jwt_header_exists"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpath_jwt_payload_exists"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
        
        // two tests for not exists
        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpath_jwt_header_not_exists"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpath_jwt_payload_not_exists"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testJsonpathIn() throws Exception {
        System.out.println("jsonpathin");
        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathin"));

        popReq(TEST_JSON);
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathnotin"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);

        SimulatorRulesParser.ExpressionContext ctx = visitor.getExpressionCtx().get("exp_jsonpathin_embedded_quote");
        String strResult = ctx.expression_two_arg().xpath_arg(1).getText();
        String expStrResult = "\"w x\"";
        assertEquals(expStrResult, strResult);

    }

    @Test
    public void testJsonpathMatches() throws Exception {
        System.out.println("jsonpathmatches");
        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathmatches"));

        popReq(TEST_JSON);
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathnotmatches"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

    @Test
    public void testJsonpathCompare() throws Exception {
        System.out.println("jsonpathcompare");
        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathcompare"));

        popReq(TEST_JSON);
        boolean expResult = true;
        boolean result = instance.evaluate(req);
        assertEquals(expResult, result);

        instance = new Expression(visitor.getExpressionCtx().get("exp_jsonpathnotcompare"));
        result = instance.evaluate(req);
        assertEquals(expResult, result);
    }

}
