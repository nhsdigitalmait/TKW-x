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
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;

/**
 *
 * @author sifa2
 */
public class RuleSetTest {

    private static TestVisitor visitor;

    private RuleSet instance;
    private ArrayList<Response> t = new ArrayList<>();
    private ArrayList<Response> f = new ArrayList<>();
    private Rule rule;

    public RuleSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        visitor = new TestVisitor();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new RuleSet("requesttype");
//          Expression ife = new Expression("exp_contains", "contains", "/query", "");
        HashMap<String, SimulatorRulesParser.If_expressionContext> hmIfExpressionCtx = visitor.getIfExpressionCtx();
        SimulatorRulesParser.If_expressionContext ife = hmIfExpressionCtx.get("exp_cp_contains");

        Expression e = new Expression(visitor.getExpressionCtx().get("exp_cp_contains"));
        HashMap<String,Expression> expressions = new HashMap<>();
        expressions.put("exp_cp_contains",e);

        t.add(new Response("namet", "NONE"));
        f.add(new Response("namef", "NONE"));
        rule = new Rule(ife, expressions, t, f);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addRule method, of class RuleSet.
     */
    @Test
    public void testAddRule() {
        System.out.println("addRule");
        Rule r = rule;
        instance.addRule(r);
    }

    /**
     * Test of execute method, of class RuleSet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        HttpRequest req = new HttpRequest("");
        String o = "/query";
        req.setRequestContext(o);
        Response expResult = t.get(0);
        instance.addRule(rule);
        List<Response> result = instance.execute(req);
        assertEquals(expResult, result.get(0));

        o = "/xxx";
        req.setRequestContext(o);
        expResult = f.get(0);
        result = instance.execute(req);
        assertEquals(expResult, result.get(0));
    }

    /**
     * Test of execute method, of class RuleSet.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_HttpRequest() throws Exception {
        System.out.println("execute");
        HttpRequest req = new HttpRequest("id");
        Response expResult = f.get(0);
        instance.addRule(rule);
        List<Response> result = instance.execute(req);
        assertEquals(expResult, result.get(0));
    }

}
