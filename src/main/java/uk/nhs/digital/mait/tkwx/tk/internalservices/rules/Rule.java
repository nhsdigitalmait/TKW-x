/*
  Copyright 2012  Damian Murphy murff@warlock.org

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

import java.util.HashMap;
import java.util.List;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.If_expressionContext;
import static uk.nhs.digital.mait.tkwx.util.Utils.getTextFromAntlrRule;
import uk.nhs.digital.mait.commonutils.util.Logger;


/**
 * Class to encapsulate an Expression, and Response members for "true" and
 * "false" results for evaluating the Expression.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Rule {

    private If_expressionContext ifExpressionCtx = null;
    private List<Response> trueResponses = null;
    private List<Response> falseResponses = null;
    private final HashMap<String, Expression> expressions;

    /**
     * Constructor
     *
     * @param exp ifExpressionContext object
     * @param expressions Hash Map of Expression objects
     * @param t True Response object
     * @param f False Response object
     * @throws Exception
     */
    Rule(If_expressionContext ifExpressionCtx, HashMap<String, Expression> expressions, List<Response> t, List<Response> f)
            throws Exception {
        this.ifExpressionCtx = ifExpressionCtx;
        this.expressions = expressions;
        trueResponses = t;
        falseResponses = f;
    }

    /**
     * evaluate the expression given the request
     *
     * @param req Request object
     * @return a List of Response objects
     * @throws Exception
     */
    List<Response> evaluate(Request req)
            throws Exception {
        return (evaluateIfExpression(ifExpressionCtx, req)) ? trueResponses : falseResponses;
    }

    /**
     * @return name of the simulator rule this is the full rule "name" including
     * all logical conjunctions
     */
    public String getName() {
        // This gets us the full matched rule including discarded whitespace
        return getTextFromAntlrRule(ifExpressionCtx);
    }

    /**
     * recursively evaluate the if expression context. This is a parsed tree of
     * expressions and logical conjunctions
     *
     * @param if_expression context
     * @param req the request
     * @return the boolean evaluation result
     * @throws Exception
     */
    private boolean evaluateIfExpression(If_expressionContext if_expression, Request req) throws Exception {
        // The operator precedence is indictaed by the order of expression in the syntax file
        // but it is as below in reality.
        if (if_expression.LPAREN() != null) { // bracketed expression
            return evaluateIfExpression(if_expression.if_expression(0), req);
        } else if (if_expression.NOT() != null) { // NOT
            return !evaluateIfExpression(if_expression.if_expression(0), req);
        } else if (if_expression.AND() != null) { // AND
            return evaluateIfExpression(if_expression.if_expression(0), req) && evaluateIfExpression(if_expression.if_expression(1), req);
        } else if (if_expression.OR() != null) { // OR
            return evaluateIfExpression(if_expression.if_expression(0), req) || evaluateIfExpression(if_expression.if_expression(1), req);
        } else { // actual expression. This is the recursive completer
            String expressionName = if_expression.expression_name().getText();
            if (!expressions.containsKey(expressionName)) {
               Logger.getInstance().error(SimulatorRulesGrammarCompilerVisiter.class.getName(), 
                       "Rules error, If statement references undefined expression: " + expressionName);
            }
            return expressions.get(expressionName).evaluate(req);
        }
    }
}
