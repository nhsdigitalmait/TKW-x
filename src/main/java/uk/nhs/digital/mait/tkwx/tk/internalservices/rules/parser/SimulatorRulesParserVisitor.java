// Generated from SimulatorRulesParser.g4 by ANTLR 4.5.3

package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SimulatorRulesParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SimulatorRulesParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(SimulatorRulesParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(SimulatorRulesParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#xpath_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_arg(SimulatorRulesParser.Xpath_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#responses_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponses_block(SimulatorRulesParser.Responses_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitutions_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitutions_block(SimulatorRulesParser.Substitutions_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expressions_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressions_block(SimulatorRulesParser.Expressions_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#simrules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimrules(SimulatorRulesParser.SimrulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#responses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponses(SimulatorRulesParser.ResponsesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponse(SimulatorRulesParser.ResponseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#response_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponse_name(SimulatorRulesParser.Response_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#response_url}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponse_url(SimulatorRulesParser.Response_urlContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#reason_code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReason_code(SimulatorRulesParser.Reason_codeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#reason_phrase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReason_phrase(SimulatorRulesParser.Reason_phraseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#response_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponse_action(SimulatorRulesParser.Response_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#httpheaderresponse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpheaderresponse(SimulatorRulesParser.HttpheaderresponseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#httpheader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpheader(SimulatorRulesParser.HttpheaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#http_header_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttp_header_value(SimulatorRulesParser.Http_header_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#variable_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_assignment(SimulatorRulesParser.Variable_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitutions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitutions(SimulatorRulesParser.SubstitutionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution(SimulatorRulesParser.SubstitutionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_tag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_tag(SimulatorRulesParser.Substitution_tagContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#property_file_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty_file_name(SimulatorRulesParser.Property_file_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#property_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty_name(SimulatorRulesParser.Property_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_no_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_no_arg(SimulatorRulesParser.Substitution_no_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_xpath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_xpath(SimulatorRulesParser.Substitution_xpathContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_regexp_cardinality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_regexp_cardinality(SimulatorRulesParser.Substitution_regexp_cardinalityContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#regexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegexp(SimulatorRulesParser.RegexpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_regexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_regexp(SimulatorRulesParser.Substitution_regexpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_literal(SimulatorRulesParser.Substitution_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_property(SimulatorRulesParser.Substitution_propertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#substitution_class}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_class(SimulatorRulesParser.Substitution_classContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressions(SimulatorRulesParser.ExpressionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(SimulatorRulesParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_name(SimulatorRulesParser.Expression_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression_no_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_no_arg(SimulatorRulesParser.Expression_no_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression_one_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_one_arg(SimulatorRulesParser.Expression_one_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#data_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_source(SimulatorRulesParser.Data_sourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#class_reg_exp_from}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_reg_exp_from(SimulatorRulesParser.Class_reg_exp_fromContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#class_reg_exp_replace}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_reg_exp_replace(SimulatorRulesParser.Class_reg_exp_replaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#class_extracted_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_extracted_value(SimulatorRulesParser.Class_extracted_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#class_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_args(SimulatorRulesParser.Class_argsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression_class}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_class(SimulatorRulesParser.Expression_classContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#xml_match_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXml_match_type(SimulatorRulesParser.Xml_match_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#match_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatch_type(SimulatorRulesParser.Match_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#text_match_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText_match_source(SimulatorRulesParser.Text_match_sourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#xml_match_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXml_match_source(SimulatorRulesParser.Xml_match_sourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#http_header_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttp_header_name(SimulatorRulesParser.Http_header_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#xslt_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXslt_file(SimulatorRulesParser.Xslt_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression_two_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_two_arg(SimulatorRulesParser.Expression_two_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#expression_xpath_compare}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_xpath_compare(SimulatorRulesParser.Expression_xpath_compareContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#simrule_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimrule_block(SimulatorRulesParser.Simrule_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#match_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatch_rule(SimulatorRulesParser.Match_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#rule_lines}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_lines(SimulatorRulesParser.Rule_linesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#rule_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_line(SimulatorRulesParser.Rule_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(SimulatorRulesParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#if_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_expression(SimulatorRulesParser.If_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#true_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrue_response(SimulatorRulesParser.True_responseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#false_response}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalse_response(SimulatorRulesParser.False_responseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimulatorRulesParser#include_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclude_statement(SimulatorRulesParser.Include_statementContext ctx);
}