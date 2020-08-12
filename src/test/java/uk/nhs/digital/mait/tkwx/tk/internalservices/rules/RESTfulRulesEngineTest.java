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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;

/**
 *
 * @author sifa2
 */
public class RESTfulRulesEngineTest {

    private static TestVisitor visitor;

    private RESTfulRulesEngine instance;
    private ArrayList<Response> t = new ArrayList<>();
    private ArrayList<Response> f = new ArrayList<>();

    public RESTfulRulesEngineTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        visitor = new TestVisitor();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        instance = new RESTfulRulesEngine();
        instance.rules = new HashMap<>();
        RuleSet ruleset = new RuleSet("ruleset");

        // This test is now defined with the context path as match source
//      Expression ife = new Expression("exp_contains", "contains", "/query", "");
        HashMap<String, SimulatorRulesParser.If_expressionContext> hmIfExpressionCtx = visitor.getIfExpressionCtx();
        SimulatorRulesParser.If_expressionContext ife = hmIfExpressionCtx.get("exp_cp_contains");

        Expression e = new Expression(visitor.getExpressionCtx().get("exp_cp_contains"));
        HashMap<String, Expression> expressions = new HashMap<>();
        expressions.put("exp_cp_contains", e);

        t.add(new Response("namet", "NONE")); // true response
        f.add(new Response("namef", "NONE")); // false response

        Rule rule = new Rule(ife, expressions, t, f);
        ruleset.addRule(rule);
        instance.rules.put("GET", ruleset);

        instance.substitutions = new HashMap<>();

        instance.substitutions.put("__LITERAL_TAG__", new Substitution(visitor.getSubstitutionCtx().get("__LITERAL_TAG__")));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of instantiateResponse method, of class RESTfulRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse() throws Exception {
        System.out.println("instantiateResponse");
        // doesn't affect anything because noresponse is true because response URL == NONE
        ServiceResponse s = new ServiceResponse();
        s.setScalar(t);
        String body = "body __TAG__ body";
        int expResult = 0;
        ServiceResponse result = instance.instantiateResponse(s, body);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class RESTfulRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        // This is  used for restful ers 
        String type = "GET"; // method
        String input = "/query"; // context path
        Response expResult = t.get(0);
        ServiceResponse result = instance.execute(type, input);
        assertEquals(expResult, ((List<Response>) result.getScalar()).get(0));
    }

    /**
     * Test of execute method, of class RESTfulRulesEngine. This is a no op for
     * restful
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testExecute_String_Node() throws Exception {
        System.out.println("execute");
        String type = "GET";
        Node input = null;
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(type, input);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class RESTfulRulesEngine.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_5args() throws Exception {
        System.out.println("execute_5args");
        // NB req is mandatory for restful
        HttpRequest req = new HttpRequest("");
        String httpMethod = "GET";
        String contextPath = "/cp";
        String content = "xxx /query xxx";
        req.setRequestType(httpMethod);
        req.setRequestContext(contextPath);
        req.setInputStream(new ByteArrayInputStream(content.getBytes()));
        req.setContentLength(content.length());

        Response expResult = f.get(0);
        // req action method contextpath content
        ServiceResponse result = instance.execute(req);
        assertEquals(expResult, ((List<Response>) result.getScalar()).get(0));

        contextPath = "xxx /query xxx";
        req.setRequestContext(contextPath);
        expResult = t.get(0);
        result = instance.execute(req);
        assertEquals(expResult, ((List<Response>) result.getScalar()).get(0));
    }

    /**
     * Test of isRestful method, of class RESTfulRulesEngine.
     */
    @Test
    public void testIsRestful() {
        System.out.println("isRestful");
        boolean expResult = true;
        boolean result = instance.isRestful();
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiateResponse method, of class RESTfulRulesEngine.
     * see testInstantiateResponse
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_String() throws Exception {
        System.out.println("instantiateResponse");
    }

    /**
     * Test of instantiateResponse method, of class RESTfulRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_HttpRequest() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse s = new ServiceResponse();
        ArrayList<Response> responses = new ArrayList<>();
        Response r = new Response("name","NONE");
        responses.add(r);
        s.setScalar(responses);
        HttpRequest req = new HttpRequest("");
        int expResult = 0;
        ServiceResponse result = instance.instantiateResponse(s, req);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class RESTfulRulesEngine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Request() throws Exception {
        System.out.println("execute");
        Request req = new HttpRequest("");
        int expResult = -1;
        ServiceResponse result = instance.execute(req);
        assertEquals(expResult, result.getCode());
    }

}
