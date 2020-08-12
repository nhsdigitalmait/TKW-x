/*
 Copyright 2015  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesLexer;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParserBaseVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.InputContext;
import static uk.nhs.digital.mait.tkwx.util.Utils.getTextFromAntlrRule;

/**
 * parses a given file and allows us to access parser context objects
 * NB Don't delete the commented out lines they are there so they can be uncommented!
 * @author simonfarrow
 */
public class TestVisitor extends SimulatorRulesParserBaseVisitor {

    private final HashMap<String, SimulatorRulesParser.ExpressionContext> hm_ExpressionCtx = new HashMap<>();
    private final HashMap<String, SimulatorRulesParser.SubstitutionContext> hm_SubstitutionCtx = new HashMap<>();
    private final HashMap<String, SimulatorRulesParser.ResponseContext> hm_ResponseCtx = new HashMap<>();
    private final HashMap<String, SimulatorRulesParser.If_statementContext> hm_IfStatementCtx = new HashMap<>();
    private final HashMap<String, SimulatorRulesParser.If_expressionContext> hm_IfExpressionCtx = new HashMap<>();
    private final HashMap<String, SimulatorRulesParser.Simrule_blockContext> hm_SimRuleBlockRuleCtx = new HashMap<>();

    // ordered list of if statement (ie rule) names, TODO this is wrong
    private final ArrayList<String> ifStatementNames = new ArrayList<>();
    
    /**
     * public constructor defaults to src/test/resources/test_tks_rule_config.txt
     *
     * @throws java.io.IOException
     */
    public TestVisitor() throws IOException {
        init("src/test/resources/test_tks_rule_config.txt");
    }

    /**
     * public constructor taking a file name
     *
     * @param fileName
     */
    private void init(String fileName) throws IOException {
        SimulatorRulesLexer lexer
                = new SimulatorRulesLexer(new ANTLRFileStream(fileName));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        SimulatorRulesParser parser = new SimulatorRulesParser(tokens);

        InputContext start = parser.input();

        visit(start);
    }

//    @Override
//    public Object visitInput(SimulatorRulesParser.InputContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitBlock(SimulatorRulesParser.BlockContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_arg(SimulatorRulesParser.Xpath_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitResponses_block(SimulatorRulesParser.Responses_blockContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitutions_block(SimulatorRulesParser.Substitutions_blockContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitExpressions_block(SimulatorRulesParser.Expressions_blockContext ctx) {
//        return visitChildren(ctx);
//    }
//
    @Override
    public Object visitSimrules(SimulatorRulesParser.SimrulesContext ctx) {
        for ( SimulatorRulesParser.Simrule_blockContext blockCtx : ctx.simrule_block() ) {
            hm_SimRuleBlockRuleCtx.put(blockCtx.match_rule().getText(),blockCtx);
        }
        return visitChildren(ctx);
    }
//
//    @Override
//    public Object visitResponses(SimulatorRulesParser.ResponsesContext ctx) {
//        return visitChildren(ctx);
//    }
    @Override
    public Object visitResponse(SimulatorRulesParser.ResponseContext ctx) {
        if (ctx.response_name() != null) {
            hm_ResponseCtx.put(ctx.response_name().getText(), ctx);
        }
        return visitChildren(ctx);
    }

//    @Override
//    public Object visitResponse_name(SimulatorRulesParser.Response_nameContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitResponse_url(SimulatorRulesParser.Response_urlContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitReason_code(SimulatorRulesParser.Reason_codeContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitReason_phrase(SimulatorRulesParser.Reason_phraseContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitResponse_action(SimulatorRulesParser.Response_actionContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitutions(SimulatorRulesParser.SubstitutionsContext ctx) {
//        return visitChildren(ctx);
//    }
    @Override
    public Object visitSubstitution(SimulatorRulesParser.SubstitutionContext ctx) {
        if (ctx.substitution_tag() != null) {
            hm_SubstitutionCtx.put(ctx.substitution_tag().getText(), ctx);
        }
        return visitChildren(ctx);
    }

//    @Override
//    public Object visitSubstitution_tag(SimulatorRulesParser.Substitution_tagContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitution_no_arg(SimulatorRulesParser.Substitution_no_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitution_xpath(SimulatorRulesParser.Substitution_xpathContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitution_literal_arg(SimulatorRulesParser.Substitution_literal_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitution_literal(SimulatorRulesParser.Substitution_literalContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitution_property(SimulatorRulesParser.Substitution_propertyContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubstitution_class(SimulatorRulesParser.Substitution_classContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitExpressions(SimulatorRulesParser.ExpressionsContext ctx) {
//        return visitChildren(ctx);
//    }
    @Override
    public Object visitExpression(SimulatorRulesParser.ExpressionContext ctx) {
        if (ctx.expression_name() != null) {
            hm_ExpressionCtx.put(ctx.expression_name().getText(), ctx);
        }
        return visitChildren(ctx);
    }

//    @Override
//    public Object visitExpression_name(SimulatorRulesParser.Expression_nameContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitExpression_no_arg(SimulatorRulesParser.Expression_no_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitExpression_one_arg(SimulatorRulesParser.Expression_one_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitMatch_source(SimulatorRulesParser.Match_sourceContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitHttp_header_name(SimulatorRulesParser.Http_header_nameContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXslt_file(SimulatorRulesParser.Xslt_fileContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitExpression_two_arg(SimulatorRulesParser.Expression_two_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSimrule_block(SimulatorRulesParser.Simrule_blockContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitMatch_rule(SimulatorRulesParser.Match_ruleContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitRule_lines(SimulatorRulesParser.Rule_linesContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitRule_line(SimulatorRulesParser.Rule_lineContext ctx) {
//        return visitChildren(ctx);
//    }
//
    @Override
    public Object visitIf_statement(SimulatorRulesParser.If_statementContext ctx) {
        SimulatorRulesParser.If_expressionContext ifExpCtx = ctx.if_expression();
        String name = getTextFromAntlrRule(ifExpCtx);
        // TODO This is not the if statement name if statements don't have names
        // this is the expression name
        hm_IfStatementCtx.put(name, ctx);
        ifStatementNames.add(name); // ordered list
        return visitChildren(ctx);
    }

    @Override
    public Object visitIf_expression(SimulatorRulesParser.If_expressionContext ctx) {
        String name = getTextFromAntlrRule(ctx);
        if (!name.isEmpty()) {
            hm_IfExpressionCtx.put(name, ctx);
        }
        return visitChildren(ctx);
    }
    
//
//    @Override
//    public Object visitTrue_response(SimulatorRulesParser.True_responseContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitFalse_response(SimulatorRulesParser.False_responseContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitInclude_statement(SimulatorRulesParser.Include_statementContext ctx) {
//        return visitChildren(ctx);
//    }
    /**
     * @return the ExpressionCtx
     */
    public HashMap<String, SimulatorRulesParser.ExpressionContext> getExpressionCtx() {
        return hm_ExpressionCtx;
    }

    /**
     * @return the SubstitionCtx
     */
    public HashMap<String, SimulatorRulesParser.SubstitutionContext> getSubstitutionCtx() {
        return hm_SubstitutionCtx;
    }

    /**
     * @return the ResponseCtx
     */
    public HashMap<String, SimulatorRulesParser.ResponseContext> getResponseCtx() {
        return hm_ResponseCtx;
    }

    /**
     * @return the hm_IfExpressionCtx
     */
    public HashMap<String, SimulatorRulesParser.If_expressionContext> getIfExpressionCtx() {
        return hm_IfExpressionCtx;
    }

    /**
     * @return the hm_IfStatementCtx
     */
    public HashMap<String, SimulatorRulesParser.If_statementContext> getIfStatementCtx() {
        return hm_IfStatementCtx;
    }

    /**
     * @return the list of ifStatementNames
     */
    public List<String> getIfStatementNames() {
        return ifStatementNames;
    }

    /**
     * @return the hm_SimRuleBlockRuleCtx
     */
    public HashMap<String, SimulatorRulesParser.Simrule_blockContext> getSimruleBlockRuleCtx() {
        return hm_SimRuleBlockRuleCtx;
    }

}
