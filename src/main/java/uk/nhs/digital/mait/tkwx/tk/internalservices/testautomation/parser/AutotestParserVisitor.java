// Generated from AutotestParser.g4 by ANTLR 4.5.3

package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AutotestParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AutotestParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AutotestParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(AutotestParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(AutotestParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(AutotestParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#validator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValidator(AutotestParser.ValidatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#simulator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimulator(AutotestParser.SimulatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#stop_when_complete}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStop_when_complete(AutotestParser.Stop_when_completeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#schedules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchedules(AutotestParser.SchedulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#tests}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTests(AutotestParser.TestsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messages}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessages(AutotestParser.MessagesContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#templates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplates(AutotestParser.TemplatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#propertysets}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertysets(AutotestParser.PropertysetsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpheaders}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpheaders(AutotestParser.HttpheadersContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#datasources}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatasources(AutotestParser.DatasourcesContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#extractors}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtractors(AutotestParser.ExtractorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#passfails}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPassfails(AutotestParser.PassfailsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#substitution_tags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_tags(AutotestParser.Substitution_tagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#include_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclude_statement(AutotestParser.Include_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#scriptName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScriptName(AutotestParser.ScriptNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#schedule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchedule(AutotestParser.ScheduleContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#scheduleName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScheduleName(AutotestParser.ScheduleNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTest(AutotestParser.TestContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestName(AutotestParser.TestNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testSynchronicity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestSynchronicity(AutotestParser.TestSynchronicityContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#sendType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSendType(AutotestParser.SendTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestArg(AutotestParser.TestArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testFunctionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestFunctionName(AutotestParser.TestFunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#functionArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArg(AutotestParser.FunctionArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testArgPair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestArgPair(AutotestParser.TestArgPairContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testString}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestString(AutotestParser.TestStringContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testURL}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestURL(AutotestParser.TestURLContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testIntArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestIntArg(AutotestParser.TestIntArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testStringArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestStringArg(AutotestParser.TestStringArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testURLArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestURLArg(AutotestParser.TestURLArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testPropertySet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestPropertySet(AutotestParser.TestPropertySetContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#withPropertySet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithPropertySet(AutotestParser.WithPropertySetContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#testHttpHeaderSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestHttpHeaderSet(AutotestParser.TestHttpHeaderSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#withHttpHeaderSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithHttpHeaderSet(AutotestParser.WithHttpHeaderSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#preTransform}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreTransform(AutotestParser.PreTransformContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#preTransformPoints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreTransformPoints(AutotestParser.PreTransformPointsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#preSubstitute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreSubstitute(AutotestParser.PreSubstituteContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#preSubstitutionPoints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreSubstitutionPoints(AutotestParser.PreSubstitutionPointsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#plusDelimPaths}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlusDelimPaths(AutotestParser.PlusDelimPathsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#substPairs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstPairs(AutotestParser.SubstPairsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#plusDelimTransformPoints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlusDelimTransformPoints(AutotestParser.PlusDelimTransformPointsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#substPair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstPair(AutotestParser.SubstPairContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#matchRegexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchRegexp(AutotestParser.MatchRegexpContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#substituteRegexp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstituteRegexp(AutotestParser.SubstituteRegexpContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#transformPoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransformPoint(AutotestParser.TransformPointContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessage(AutotestParser.MessageContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messageName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageName(AutotestParser.MessageNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messageArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageArg(AutotestParser.MessageArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messageArgSingle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageArgSingle(AutotestParser.MessageArgSingleContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#usingTemplate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUsingTemplate(AutotestParser.UsingTemplateContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#withDatasource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithDatasource(AutotestParser.WithDatasourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messageArgPair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageArgPair(AutotestParser.MessageArgPairContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messageStringArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageStringArg(AutotestParser.MessageStringArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#messageString}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageString(AutotestParser.MessageStringContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#template}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate(AutotestParser.TemplateContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#templateName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateName(AutotestParser.TemplateNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#namedPropertySet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedPropertySet(AutotestParser.NamedPropertySetContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#propertySetName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertySetName(AutotestParser.PropertySetNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#propertySetDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertySetDirective(AutotestParser.PropertySetDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#propertyName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyName(AutotestParser.PropertyNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#psFunctionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPsFunctionName(AutotestParser.PsFunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#psArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPsArg(AutotestParser.PsArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#psValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPsValue(AutotestParser.PsValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#namedHttpHeaderSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedHttpHeaderSet(AutotestParser.NamedHttpHeaderSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpHeaderSetName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpHeaderSetName(AutotestParser.HttpHeaderSetNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpHeaderSetDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpHeaderSetDirective(AutotestParser.HttpHeaderSetDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpHeaderName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpHeaderName(AutotestParser.HttpHeaderNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#datasource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatasource(AutotestParser.DatasourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#datasourceName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatasourceName(AutotestParser.DatasourceNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#datasourceType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatasourceType(AutotestParser.DatasourceTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#extractor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtractor(AutotestParser.ExtractorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#extractorName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtractorName(AutotestParser.ExtractorNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#extractorType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtractorType(AutotestParser.ExtractorTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#passfail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPassfail(AutotestParser.PassfailContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#passFailCheckName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPassFailCheckName(AutotestParser.PassFailCheckNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#passFailCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPassFailCheck(AutotestParser.PassFailCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#bracketedPassfail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracketedPassfail(AutotestParser.BracketedPassfailContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpStatusCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpStatusCheck(AutotestParser.HttpStatusCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#xPathCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXPathCheck(AutotestParser.XPathCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#xpathType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathType(AutotestParser.XpathTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#xpathExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathExpression(AutotestParser.XpathExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#xpathArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathArg(AutotestParser.XpathArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#xpathTypeNoArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathTypeNoArg(AutotestParser.XpathTypeNoArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#xpathTypeArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathTypeArg(AutotestParser.XpathTypeArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpHeaderCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpHeaderCheck(AutotestParser.HttpHeaderCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#httpHeaderCorrelationCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHttpHeaderCorrelationCheck(AutotestParser.HttpHeaderCorrelationCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#nullCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullCheck(AutotestParser.NullCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#nullCheckType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullCheckType(AutotestParser.NullCheckTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#matchString}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchString(AutotestParser.MatchStringContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#usingExtractor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUsingExtractor(AutotestParser.UsingExtractorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AutotestParser#substitution_tag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubstitution_tag(AutotestParser.Substitution_tagContext ctx);
}