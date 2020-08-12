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
import java.util.Arrays;
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
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.If_statementContext;

/**
 *
 * @author sifa2
 */
public class RuleTest {

    private static TestVisitor visitor;

    private Rule instance;
 
    private static Response TRUE_RESPONSE;
    private static Response FALSE_RESPONSE;
    private static final HashMap<String, Rule> RULES = new HashMap<>();
    private static final HashMap<String, Expression> EXPRESSIONS = new HashMap<>();
    private static final HashMap<String, Response> RESPONSES = new HashMap<>();
    private static final String TEST_RULE_NAME = "exp_cp_contains";

    static {
        // this sets up the two Responses to match namet and namef responses referenced in the test file
        // other response names would cause an exception
        try {
            TRUE_RESPONSE = new Response("namet", "NONE");
            FALSE_RESPONSE = new Response("namef", "NONE");
            RESPONSES.put("namet", TRUE_RESPONSE);
            RESPONSES.put("namef", FALSE_RESPONSE);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public RuleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, Exception {
        visitor = new TestVisitor();
        
        HashMap<String, SimulatorRulesParser.ExpressionContext> hmExpressionsCtx = visitor.getExpressionCtx();
        
        // create a hashmap of all the instantiated Expressions
        for (String expressionName : hmExpressionsCtx.keySet()) {
            EXPRESSIONS.put(expressionName, new Expression(hmExpressionsCtx.get(expressionName)));
        }

        // instantiate Rules honouring true and false Responses from the test simulator rules file
        // NB These are actual single line if statements they are not a whole block of rules with a match 
        //replace all the THEN EL:SE clauses for all the if expression conditionals with the specified responses
        HashMap<String, SimulatorRulesParser.If_statementContext> hmIfStatementCtx = visitor.getIfStatementCtx();
        for (String expressionText : hmIfStatementCtx.keySet()) {
            If_statementContext ruleCtx = hmIfStatementCtx.get(expressionText);
            Rule rule = new Rule(ruleCtx.if_expression(), EXPRESSIONS,
                    Arrays.asList(RESPONSES.get(ruleCtx.true_response().get(0).response_name().getText())),
                    Arrays.asList(RESPONSES.get(ruleCtx.false_response().get(0).response_name().getText())));
            RULES.put(expressionText, rule);
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
//      Expression ife = new Expression("exp_contains", "contains", "/query", "");
        instance = RULES.get(TEST_RULE_NAME);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class Rule.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = TEST_RULE_NAME;
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of evaluate method, of class Rule.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testEvaluate() throws Exception {
        System.out.println("evaluate");
        // these are all expected to return true response
        HttpRequest req = new HttpRequest("id");
        // using the arraylist of names ensures the rules are tested in the order they appear in the script
        // rather than the random key order of a hashmap
        for (String expressionText : visitor.getIfStatementNames()) {
            System.out.println(expressionText);
            List<Response> result = RULES.get(expressionText).evaluate(req);
            assertEquals(TRUE_RESPONSE, result.get(0));
        }
    }
}
