/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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

import uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners.VerboseErrorListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParserBaseVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesLexer;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.ExpressionContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.HttpheaderContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.HttpheaderresponseContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.If_statementContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.ResponseContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Simrule_blockContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.SubstitutionContext;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Main scanner for simulator rules files Populates and exposes collections of
 * Response, Expression, Substitution and Rule
 *
 * @author simonfarrow
 */
public class SimulatorRulesGrammarCompilerVisiter extends SimulatorRulesParserBaseVisitor {

    // these four are exposed via public accessors
    private final HashMap<String, Response> responses = new HashMap<>();
    private final HashMap<String, Expression> expressions = new HashMap<>();
    private final HashMap<String, Substitution> substitutions = new HashMap<>();
    private final HashMap<String, RuleSet> ruleSets = new HashMap<>();

    // internal attributes
    private BaseErrorListener[] customErrorListeners = null;

    // locally track which ruleset we are currently populating
    private RuleSet ruleset;

    // track which top level block we are parsing. Required for handling include files.
    private RuleCategory ruleCategory = null;

    // these are the four top level blocks in a simulator rules file
    private enum RuleCategory {
        RESPONSE,
        SUBSTITUTION,
        EXPRESSION,
        RULE,
    }

    /**
     * this needs to be elsewhere in the future as per the equivalent Autotest
     * method
     *
     * @param fileName rules file name path to parse
     * @throws IOException
     */
    public void parse(String fileName) throws IOException {
        SimulatorRulesParser parser = getParser(fileName);

        ParseTree pt = parser.input();

        visit(pt);
    }

    private SimulatorRulesParser getParser(String fileName) throws IOException {
        SimulatorRulesParser parser = getRulesParser(fileName);
        parser.removeErrorListeners();
        parser.addErrorListener(new VerboseErrorListener(fileName));
        if (customErrorListeners != null) {
            for (BaseErrorListener el : customErrorListeners) {
                parser.addErrorListener(el);
            }
        }

        // Pass the tokens to the parser
        return parser;
    }

    private SimulatorRulesParser getRulesParser(String fileName) throws IOException {
        SimulatorRulesLexer lexer
                = new SimulatorRulesLexer(new ANTLRFileStream(fileName));
        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new SimulatorRulesParser(tokens);
    }

    // Visitor override methods these are called by the base visitor functionality
    @Override
    public Object visitInclude_statement(SimulatorRulesParser.Include_statementContext ctx) {
        try {
            SimulatorRulesParser parser = getParser(ctx.PATH().getText());

            ParseTree pt = null;
            switch (ruleCategory) {
                case RESPONSE:
                    pt = parser.responses();
                    break;
                case SUBSTITUTION:
                    pt = parser.substitutions();
                    break;
                case EXPRESSION:
                    pt = parser.expressions();
                    break;
                case RULE:
                    pt = parser.rule_lines();
                    break;
                default:
                    Logger.getInstance().log(SEVERE, SimulatorRulesGrammarCompilerVisiter.class.getName(), "Unexpected rule category value");
            }

            visit(pt);
        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, SimulatorRulesGrammarCompilerVisiter.class.getName(), ex.getMessage());
        }
        return super.visitInclude_statement(ctx);
    }

    @Override
    public Object visitResponse(ResponseContext ctx) {
        ruleCategory = RuleCategory.RESPONSE;
        try {
            if (ctx.response_name() != null) {
                // System.out.println(ctx.response_name().getText());
                Response response = new Response(ctx.response_name().getText(), ctx.response_url().getText());
                if (ctx.reason_code() != null) {
                    int reasonCode = Integer.parseInt(ctx.reason_code().getText());
                    response.setCode(reasonCode);
                } else if (ctx.reason_phrase() != null) {
                    response.setResponsePhrase(ctx.reason_phrase().QUOTED_STRING().getText());
                }

                if (ctx.response_action() != null) {
                    response.setAction(ctx.response_action().getText());
                }

                // handle http headers to be returned in responses
                if (ctx.httpheaderresponse() != null) {
                    HttpheaderresponseContext httpHeaderResponseCtx = ctx.httpheaderresponse();
                    for (HttpheaderContext httpHeaderCtx : httpHeaderResponseCtx.httpheader()) {
                        response.addHttpHeader(httpHeaderCtx.http_header_name().getText(), httpHeaderCtx.http_header_value().getText());
                    }
                }

                if (ctx.variable_assignment() != null) {
                    SimulatorRulesParser.Variable_assignmentContext vaCtx = ctx.variable_assignment();
                    response.setVariableAssignment(vaCtx.VARIABLE_NAME().getText(), vaCtx.QUOTED_STRING() != null ? vaCtx.QUOTED_STRING().getText() : null);
                }

                responses.put(ctx.response_name().getText(), response);
            }
        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, SimulatorRulesGrammarCompilerVisiter.class.getName(), "Error opening file " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, SimulatorRulesGrammarCompilerVisiter.class.getName(), "Visiting response " + ex.getMessage());
        }
        return super.visitResponse(ctx);
    }

    @Override
    public Object visitSubstitution(SubstitutionContext ctx) {
        ruleCategory = RuleCategory.SUBSTITUTION;
        if (ctx.substitution_tag() != null) {
            String tagName = ctx.substitution_tag().getText();
            substitutions.put(tagName, new Substitution(ctx));
        }
        return super.visitSubstitution(ctx);
    }

    @Override
    public Object visitExpression(ExpressionContext ctx) {
        ruleCategory = RuleCategory.EXPRESSION;
        if (ctx.expression_name() != null) {
            String expressionName = ctx.expression_name().getText();
            // System.out.println(ctx.expression_name().getText());
            expressions.put(expressionName, new Expression(ctx));
        }
        return super.visitExpression(ctx);
    }

    /**
     * matches a single rule line whether in a main file or an include
     *
     * @param ctx the if statement context
     * @return
     */
    @Override
    public Object visitIf_statement(If_statementContext ctx) {
        try {
            // System.out.println("Adding rule line [" + ctx.if_expression().getText()+"]");
            String responseName = null;
            ArrayList<Response> t = new ArrayList<>();
            ArrayList<Response> f = new ArrayList<>();

            // the first test will be false for NEXT
            for (SimulatorRulesParser.True_responseContext response : ctx.true_response()) {
                if (response.response_name() != null) {
                    responseName = response.response_name().getText();
                    Response r = responses.get(responseName);
                    if (r == null) {
                        throw new Exception("Rule \"true\" response " + responseName + " declared but not defined in line " + response.response_name().getStart().getLine());
                    }
                    t.add(r);
                }
            }

            // the first test may not be true the second will be false for NEXT
            for (SimulatorRulesParser.False_responseContext response : ctx.false_response()) {
                if (response.response_name() != null) {
                    responseName = response.response_name().getText();
                    Response r = responses.get(responseName);
                    if (r == null) {
                        throw new Exception("Rule \"false\" response " + responseName + " declared but not defined in line " + response.response_name().getStart().getLine());
                    }
                    f.add(r);
                }
            }

            Rule rule = new Rule(ctx.if_expression(), expressions, t, f);
            ruleset.addRule(rule);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, SimulatorRulesGrammarCompilerVisiter.class.getName(), ex.getMessage());
        }
        return super.visitIf_statement(ctx);
    }

    @Override
    public Object visitSimrule_block(Simrule_blockContext ctx) {
        ruleCategory = RuleCategory.RULE;
        try {
            ruleset = new RuleSet(ctx.match_rule().getText());
            ruleSets.put(ctx.match_rule().getText(), ruleset);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, SimulatorRulesGrammarCompilerVisiter.class.getName(), ex.getMessage());
        }
        return super.visitSimrule_block(ctx);
    }

    // accessors
    /**
     * @return the responses
     */
    public HashMap<String, Response> getResponses() {
        return responses;
    }

    /**
     * @return the expressions
     */
    public HashMap<String, Expression> getExpressions() {
        return expressions;
    }

    /**
     * @return the substitutions
     */
    public HashMap<String, Substitution> getSubstitutions() {
        return substitutions;
    }

    /**
     * @return the rules
     */
    public HashMap<String, RuleSet> getRules() {
        return ruleSets;
    }

    // mutators
    /**
     * allow addition of extra error listeners to parsing process
     *
     * @param customErrorListeners the customErrorListeners to set
     */
    public void setCustomErrorListeners(BaseErrorListener[] customErrorListeners) {
        this.customErrorListeners = customErrorListeners;
    }

    /**
     * allow addition of a single extra error listener to parsing process
     *
     * @param errorListener
     */
    public void setCustomErrorListener(BaseErrorListener errorListener) {
        setCustomErrorListeners(new BaseErrorListener[]{errorListener});
    }
}
