// Generated from ValidationParser.g4 by ANTLR 4.5.3

package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ValidationParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ValidationParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ValidationParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(ValidationParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#validation_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValidation_header(ValidationParser.Validation_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#validation_header_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValidation_header_type(ValidationParser.Validation_header_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#validate_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValidate_statement(ValidationParser.Validate_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#service_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitService_name(ValidationParser.Service_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#validate_directives}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValidate_directives(ValidationParser.Validate_directivesContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#validate_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValidate_directive(ValidationParser.Validate_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#check_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheck_directive(ValidationParser.Check_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#sub_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSub_name(ValidationParser.Sub_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#set_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet_directive(ValidationParser.Set_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#set_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet_type(ValidationParser.Set_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#if_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_directive(ValidationParser.If_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#then_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThen_clause(ValidationParser.Then_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#else_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElse_clause(ValidationParser.Else_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#endif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndif(ValidationParser.EndifContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#test_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTest_statement(ValidationParser.Test_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#unchecked_test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnchecked_test(ValidationParser.Unchecked_testContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#unchecked_test_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnchecked_test_name(ValidationParser.Unchecked_test_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#schema_test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchema_test(ValidationParser.Schema_testContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#schema_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchema_type(ValidationParser.Schema_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#schema_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchema_path(ValidationParser.Schema_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#schema_xpath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchema_xpath(ValidationParser.Schema_xpathContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#hapifhirvalidator_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHapifhirvalidator_id(ValidationParser.Hapifhirvalidator_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#no_arg_test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNo_arg_test(ValidationParser.No_arg_testContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xml_match_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXml_match_source(ValidationParser.Xml_match_sourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_one_arg_test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_one_arg_test(ValidationParser.Xpath_one_arg_testContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_one_arg_comparison_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_one_arg_comparison_type(ValidationParser.Xpath_one_arg_comparison_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_one_arg_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_one_arg_type(ValidationParser.Xpath_one_arg_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#text_match_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText_match_type(ValidationParser.Text_match_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#text_match_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText_match_source(ValidationParser.Text_match_sourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#http_header_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttp_header_name(ValidationParser.Http_header_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_arg(ValidationParser.Xpath_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_two_arg_comparison_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_two_arg_comparison_type(ValidationParser.Xpath_two_arg_comparison_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_two_arg_test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_two_arg_test(ValidationParser.Xpath_two_arg_testContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_two_arg_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_two_arg_type(ValidationParser.Xpath_two_arg_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_multi_arg_test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_multi_arg_test(ValidationParser.Xpath_multi_arg_testContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#xpath_multi_arg_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpath_multi_arg_type(ValidationParser.Xpath_multi_arg_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#annotation_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotation_directive(ValidationParser.Annotation_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#subset_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubset_statement(ValidationParser.Subset_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#subset_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubset_name(ValidationParser.Subset_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ValidationParser#include_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclude_statement(ValidationParser.Include_statementContext ctx);
}