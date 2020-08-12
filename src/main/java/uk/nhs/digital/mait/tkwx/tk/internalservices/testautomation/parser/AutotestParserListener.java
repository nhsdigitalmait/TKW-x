// Generated from AutotestParser.g4 by ANTLR 4.5.3

package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AutotestParser}.
 */
public interface AutotestParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AutotestParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(AutotestParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(AutotestParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(AutotestParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(AutotestParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(AutotestParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(AutotestParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#validator}.
	 * @param ctx the parse tree
	 */
	void enterValidator(AutotestParser.ValidatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#validator}.
	 * @param ctx the parse tree
	 */
	void exitValidator(AutotestParser.ValidatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#simulator}.
	 * @param ctx the parse tree
	 */
	void enterSimulator(AutotestParser.SimulatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#simulator}.
	 * @param ctx the parse tree
	 */
	void exitSimulator(AutotestParser.SimulatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#stop_when_complete}.
	 * @param ctx the parse tree
	 */
	void enterStop_when_complete(AutotestParser.Stop_when_completeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#stop_when_complete}.
	 * @param ctx the parse tree
	 */
	void exitStop_when_complete(AutotestParser.Stop_when_completeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#schedules}.
	 * @param ctx the parse tree
	 */
	void enterSchedules(AutotestParser.SchedulesContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#schedules}.
	 * @param ctx the parse tree
	 */
	void exitSchedules(AutotestParser.SchedulesContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#tests}.
	 * @param ctx the parse tree
	 */
	void enterTests(AutotestParser.TestsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#tests}.
	 * @param ctx the parse tree
	 */
	void exitTests(AutotestParser.TestsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messages}.
	 * @param ctx the parse tree
	 */
	void enterMessages(AutotestParser.MessagesContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messages}.
	 * @param ctx the parse tree
	 */
	void exitMessages(AutotestParser.MessagesContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#templates}.
	 * @param ctx the parse tree
	 */
	void enterTemplates(AutotestParser.TemplatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#templates}.
	 * @param ctx the parse tree
	 */
	void exitTemplates(AutotestParser.TemplatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#propertysets}.
	 * @param ctx the parse tree
	 */
	void enterPropertysets(AutotestParser.PropertysetsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#propertysets}.
	 * @param ctx the parse tree
	 */
	void exitPropertysets(AutotestParser.PropertysetsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#httpheaders}.
	 * @param ctx the parse tree
	 */
	void enterHttpheaders(AutotestParser.HttpheadersContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#httpheaders}.
	 * @param ctx the parse tree
	 */
	void exitHttpheaders(AutotestParser.HttpheadersContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#datasources}.
	 * @param ctx the parse tree
	 */
	void enterDatasources(AutotestParser.DatasourcesContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#datasources}.
	 * @param ctx the parse tree
	 */
	void exitDatasources(AutotestParser.DatasourcesContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#extractors}.
	 * @param ctx the parse tree
	 */
	void enterExtractors(AutotestParser.ExtractorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#extractors}.
	 * @param ctx the parse tree
	 */
	void exitExtractors(AutotestParser.ExtractorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#passfails}.
	 * @param ctx the parse tree
	 */
	void enterPassfails(AutotestParser.PassfailsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#passfails}.
	 * @param ctx the parse tree
	 */
	void exitPassfails(AutotestParser.PassfailsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#substitution_tags}.
	 * @param ctx the parse tree
	 */
	void enterSubstitution_tags(AutotestParser.Substitution_tagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#substitution_tags}.
	 * @param ctx the parse tree
	 */
	void exitSubstitution_tags(AutotestParser.Substitution_tagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#include_statement}.
	 * @param ctx the parse tree
	 */
	void enterInclude_statement(AutotestParser.Include_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#include_statement}.
	 * @param ctx the parse tree
	 */
	void exitInclude_statement(AutotestParser.Include_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#scriptName}.
	 * @param ctx the parse tree
	 */
	void enterScriptName(AutotestParser.ScriptNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#scriptName}.
	 * @param ctx the parse tree
	 */
	void exitScriptName(AutotestParser.ScriptNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#schedule}.
	 * @param ctx the parse tree
	 */
	void enterSchedule(AutotestParser.ScheduleContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#schedule}.
	 * @param ctx the parse tree
	 */
	void exitSchedule(AutotestParser.ScheduleContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#scheduleName}.
	 * @param ctx the parse tree
	 */
	void enterScheduleName(AutotestParser.ScheduleNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#scheduleName}.
	 * @param ctx the parse tree
	 */
	void exitScheduleName(AutotestParser.ScheduleNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#test}.
	 * @param ctx the parse tree
	 */
	void enterTest(AutotestParser.TestContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#test}.
	 * @param ctx the parse tree
	 */
	void exitTest(AutotestParser.TestContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testName}.
	 * @param ctx the parse tree
	 */
	void enterTestName(AutotestParser.TestNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testName}.
	 * @param ctx the parse tree
	 */
	void exitTestName(AutotestParser.TestNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testSynchronicity}.
	 * @param ctx the parse tree
	 */
	void enterTestSynchronicity(AutotestParser.TestSynchronicityContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testSynchronicity}.
	 * @param ctx the parse tree
	 */
	void exitTestSynchronicity(AutotestParser.TestSynchronicityContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#sendType}.
	 * @param ctx the parse tree
	 */
	void enterSendType(AutotestParser.SendTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#sendType}.
	 * @param ctx the parse tree
	 */
	void exitSendType(AutotestParser.SendTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testArg}.
	 * @param ctx the parse tree
	 */
	void enterTestArg(AutotestParser.TestArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testArg}.
	 * @param ctx the parse tree
	 */
	void exitTestArg(AutotestParser.TestArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testFunctionName}.
	 * @param ctx the parse tree
	 */
	void enterTestFunctionName(AutotestParser.TestFunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testFunctionName}.
	 * @param ctx the parse tree
	 */
	void exitTestFunctionName(AutotestParser.TestFunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArg(AutotestParser.FunctionArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArg(AutotestParser.FunctionArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testArgPair}.
	 * @param ctx the parse tree
	 */
	void enterTestArgPair(AutotestParser.TestArgPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testArgPair}.
	 * @param ctx the parse tree
	 */
	void exitTestArgPair(AutotestParser.TestArgPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testString}.
	 * @param ctx the parse tree
	 */
	void enterTestString(AutotestParser.TestStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testString}.
	 * @param ctx the parse tree
	 */
	void exitTestString(AutotestParser.TestStringContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testURL}.
	 * @param ctx the parse tree
	 */
	void enterTestURL(AutotestParser.TestURLContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testURL}.
	 * @param ctx the parse tree
	 */
	void exitTestURL(AutotestParser.TestURLContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testIntArg}.
	 * @param ctx the parse tree
	 */
	void enterTestIntArg(AutotestParser.TestIntArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testIntArg}.
	 * @param ctx the parse tree
	 */
	void exitTestIntArg(AutotestParser.TestIntArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testStringArg}.
	 * @param ctx the parse tree
	 */
	void enterTestStringArg(AutotestParser.TestStringArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testStringArg}.
	 * @param ctx the parse tree
	 */
	void exitTestStringArg(AutotestParser.TestStringArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testURLArg}.
	 * @param ctx the parse tree
	 */
	void enterTestURLArg(AutotestParser.TestURLArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testURLArg}.
	 * @param ctx the parse tree
	 */
	void exitTestURLArg(AutotestParser.TestURLArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testPropertySet}.
	 * @param ctx the parse tree
	 */
	void enterTestPropertySet(AutotestParser.TestPropertySetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testPropertySet}.
	 * @param ctx the parse tree
	 */
	void exitTestPropertySet(AutotestParser.TestPropertySetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#withPropertySet}.
	 * @param ctx the parse tree
	 */
	void enterWithPropertySet(AutotestParser.WithPropertySetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#withPropertySet}.
	 * @param ctx the parse tree
	 */
	void exitWithPropertySet(AutotestParser.WithPropertySetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#testHttpHeaderSet}.
	 * @param ctx the parse tree
	 */
	void enterTestHttpHeaderSet(AutotestParser.TestHttpHeaderSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#testHttpHeaderSet}.
	 * @param ctx the parse tree
	 */
	void exitTestHttpHeaderSet(AutotestParser.TestHttpHeaderSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#withHttpHeaderSet}.
	 * @param ctx the parse tree
	 */
	void enterWithHttpHeaderSet(AutotestParser.WithHttpHeaderSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#withHttpHeaderSet}.
	 * @param ctx the parse tree
	 */
	void exitWithHttpHeaderSet(AutotestParser.WithHttpHeaderSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#preTransform}.
	 * @param ctx the parse tree
	 */
	void enterPreTransform(AutotestParser.PreTransformContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#preTransform}.
	 * @param ctx the parse tree
	 */
	void exitPreTransform(AutotestParser.PreTransformContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#preTransformPoints}.
	 * @param ctx the parse tree
	 */
	void enterPreTransformPoints(AutotestParser.PreTransformPointsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#preTransformPoints}.
	 * @param ctx the parse tree
	 */
	void exitPreTransformPoints(AutotestParser.PreTransformPointsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#preSubstitute}.
	 * @param ctx the parse tree
	 */
	void enterPreSubstitute(AutotestParser.PreSubstituteContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#preSubstitute}.
	 * @param ctx the parse tree
	 */
	void exitPreSubstitute(AutotestParser.PreSubstituteContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#preSubstitutionPoints}.
	 * @param ctx the parse tree
	 */
	void enterPreSubstitutionPoints(AutotestParser.PreSubstitutionPointsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#preSubstitutionPoints}.
	 * @param ctx the parse tree
	 */
	void exitPreSubstitutionPoints(AutotestParser.PreSubstitutionPointsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#plusDelimPaths}.
	 * @param ctx the parse tree
	 */
	void enterPlusDelimPaths(AutotestParser.PlusDelimPathsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#plusDelimPaths}.
	 * @param ctx the parse tree
	 */
	void exitPlusDelimPaths(AutotestParser.PlusDelimPathsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#substPairs}.
	 * @param ctx the parse tree
	 */
	void enterSubstPairs(AutotestParser.SubstPairsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#substPairs}.
	 * @param ctx the parse tree
	 */
	void exitSubstPairs(AutotestParser.SubstPairsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#plusDelimTransformPoints}.
	 * @param ctx the parse tree
	 */
	void enterPlusDelimTransformPoints(AutotestParser.PlusDelimTransformPointsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#plusDelimTransformPoints}.
	 * @param ctx the parse tree
	 */
	void exitPlusDelimTransformPoints(AutotestParser.PlusDelimTransformPointsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#substPair}.
	 * @param ctx the parse tree
	 */
	void enterSubstPair(AutotestParser.SubstPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#substPair}.
	 * @param ctx the parse tree
	 */
	void exitSubstPair(AutotestParser.SubstPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#matchRegexp}.
	 * @param ctx the parse tree
	 */
	void enterMatchRegexp(AutotestParser.MatchRegexpContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#matchRegexp}.
	 * @param ctx the parse tree
	 */
	void exitMatchRegexp(AutotestParser.MatchRegexpContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#substituteRegexp}.
	 * @param ctx the parse tree
	 */
	void enterSubstituteRegexp(AutotestParser.SubstituteRegexpContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#substituteRegexp}.
	 * @param ctx the parse tree
	 */
	void exitSubstituteRegexp(AutotestParser.SubstituteRegexpContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#transformPoint}.
	 * @param ctx the parse tree
	 */
	void enterTransformPoint(AutotestParser.TransformPointContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#transformPoint}.
	 * @param ctx the parse tree
	 */
	void exitTransformPoint(AutotestParser.TransformPointContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#message}.
	 * @param ctx the parse tree
	 */
	void enterMessage(AutotestParser.MessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#message}.
	 * @param ctx the parse tree
	 */
	void exitMessage(AutotestParser.MessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messageName}.
	 * @param ctx the parse tree
	 */
	void enterMessageName(AutotestParser.MessageNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messageName}.
	 * @param ctx the parse tree
	 */
	void exitMessageName(AutotestParser.MessageNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messageArg}.
	 * @param ctx the parse tree
	 */
	void enterMessageArg(AutotestParser.MessageArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messageArg}.
	 * @param ctx the parse tree
	 */
	void exitMessageArg(AutotestParser.MessageArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messageArgSingle}.
	 * @param ctx the parse tree
	 */
	void enterMessageArgSingle(AutotestParser.MessageArgSingleContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messageArgSingle}.
	 * @param ctx the parse tree
	 */
	void exitMessageArgSingle(AutotestParser.MessageArgSingleContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#usingTemplate}.
	 * @param ctx the parse tree
	 */
	void enterUsingTemplate(AutotestParser.UsingTemplateContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#usingTemplate}.
	 * @param ctx the parse tree
	 */
	void exitUsingTemplate(AutotestParser.UsingTemplateContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#withDatasource}.
	 * @param ctx the parse tree
	 */
	void enterWithDatasource(AutotestParser.WithDatasourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#withDatasource}.
	 * @param ctx the parse tree
	 */
	void exitWithDatasource(AutotestParser.WithDatasourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messageArgPair}.
	 * @param ctx the parse tree
	 */
	void enterMessageArgPair(AutotestParser.MessageArgPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messageArgPair}.
	 * @param ctx the parse tree
	 */
	void exitMessageArgPair(AutotestParser.MessageArgPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messageStringArg}.
	 * @param ctx the parse tree
	 */
	void enterMessageStringArg(AutotestParser.MessageStringArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messageStringArg}.
	 * @param ctx the parse tree
	 */
	void exitMessageStringArg(AutotestParser.MessageStringArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#messageString}.
	 * @param ctx the parse tree
	 */
	void enterMessageString(AutotestParser.MessageStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#messageString}.
	 * @param ctx the parse tree
	 */
	void exitMessageString(AutotestParser.MessageStringContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#template}.
	 * @param ctx the parse tree
	 */
	void enterTemplate(AutotestParser.TemplateContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#template}.
	 * @param ctx the parse tree
	 */
	void exitTemplate(AutotestParser.TemplateContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#templateName}.
	 * @param ctx the parse tree
	 */
	void enterTemplateName(AutotestParser.TemplateNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#templateName}.
	 * @param ctx the parse tree
	 */
	void exitTemplateName(AutotestParser.TemplateNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#namedPropertySet}.
	 * @param ctx the parse tree
	 */
	void enterNamedPropertySet(AutotestParser.NamedPropertySetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#namedPropertySet}.
	 * @param ctx the parse tree
	 */
	void exitNamedPropertySet(AutotestParser.NamedPropertySetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#propertySetName}.
	 * @param ctx the parse tree
	 */
	void enterPropertySetName(AutotestParser.PropertySetNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#propertySetName}.
	 * @param ctx the parse tree
	 */
	void exitPropertySetName(AutotestParser.PropertySetNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#propertySetDirective}.
	 * @param ctx the parse tree
	 */
	void enterPropertySetDirective(AutotestParser.PropertySetDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#propertySetDirective}.
	 * @param ctx the parse tree
	 */
	void exitPropertySetDirective(AutotestParser.PropertySetDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void enterPropertyName(AutotestParser.PropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void exitPropertyName(AutotestParser.PropertyNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#psFunctionName}.
	 * @param ctx the parse tree
	 */
	void enterPsFunctionName(AutotestParser.PsFunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#psFunctionName}.
	 * @param ctx the parse tree
	 */
	void exitPsFunctionName(AutotestParser.PsFunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#psArg}.
	 * @param ctx the parse tree
	 */
	void enterPsArg(AutotestParser.PsArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#psArg}.
	 * @param ctx the parse tree
	 */
	void exitPsArg(AutotestParser.PsArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#psValue}.
	 * @param ctx the parse tree
	 */
	void enterPsValue(AutotestParser.PsValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#psValue}.
	 * @param ctx the parse tree
	 */
	void exitPsValue(AutotestParser.PsValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#namedHttpHeaderSet}.
	 * @param ctx the parse tree
	 */
	void enterNamedHttpHeaderSet(AutotestParser.NamedHttpHeaderSetContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#namedHttpHeaderSet}.
	 * @param ctx the parse tree
	 */
	void exitNamedHttpHeaderSet(AutotestParser.NamedHttpHeaderSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#httpHeaderSetName}.
	 * @param ctx the parse tree
	 */
	void enterHttpHeaderSetName(AutotestParser.HttpHeaderSetNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#httpHeaderSetName}.
	 * @param ctx the parse tree
	 */
	void exitHttpHeaderSetName(AutotestParser.HttpHeaderSetNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#httpHeaderSetDirective}.
	 * @param ctx the parse tree
	 */
	void enterHttpHeaderSetDirective(AutotestParser.HttpHeaderSetDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#httpHeaderSetDirective}.
	 * @param ctx the parse tree
	 */
	void exitHttpHeaderSetDirective(AutotestParser.HttpHeaderSetDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#httpHeaderName}.
	 * @param ctx the parse tree
	 */
	void enterHttpHeaderName(AutotestParser.HttpHeaderNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#httpHeaderName}.
	 * @param ctx the parse tree
	 */
	void exitHttpHeaderName(AutotestParser.HttpHeaderNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#datasource}.
	 * @param ctx the parse tree
	 */
	void enterDatasource(AutotestParser.DatasourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#datasource}.
	 * @param ctx the parse tree
	 */
	void exitDatasource(AutotestParser.DatasourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#datasourceName}.
	 * @param ctx the parse tree
	 */
	void enterDatasourceName(AutotestParser.DatasourceNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#datasourceName}.
	 * @param ctx the parse tree
	 */
	void exitDatasourceName(AutotestParser.DatasourceNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#datasourceType}.
	 * @param ctx the parse tree
	 */
	void enterDatasourceType(AutotestParser.DatasourceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#datasourceType}.
	 * @param ctx the parse tree
	 */
	void exitDatasourceType(AutotestParser.DatasourceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#extractor}.
	 * @param ctx the parse tree
	 */
	void enterExtractor(AutotestParser.ExtractorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#extractor}.
	 * @param ctx the parse tree
	 */
	void exitExtractor(AutotestParser.ExtractorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#extractorName}.
	 * @param ctx the parse tree
	 */
	void enterExtractorName(AutotestParser.ExtractorNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#extractorName}.
	 * @param ctx the parse tree
	 */
	void exitExtractorName(AutotestParser.ExtractorNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#extractorType}.
	 * @param ctx the parse tree
	 */
	void enterExtractorType(AutotestParser.ExtractorTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#extractorType}.
	 * @param ctx the parse tree
	 */
	void exitExtractorType(AutotestParser.ExtractorTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#passfail}.
	 * @param ctx the parse tree
	 */
	void enterPassfail(AutotestParser.PassfailContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#passfail}.
	 * @param ctx the parse tree
	 */
	void exitPassfail(AutotestParser.PassfailContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#passFailCheckName}.
	 * @param ctx the parse tree
	 */
	void enterPassFailCheckName(AutotestParser.PassFailCheckNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#passFailCheckName}.
	 * @param ctx the parse tree
	 */
	void exitPassFailCheckName(AutotestParser.PassFailCheckNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#passFailCheck}.
	 * @param ctx the parse tree
	 */
	void enterPassFailCheck(AutotestParser.PassFailCheckContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#passFailCheck}.
	 * @param ctx the parse tree
	 */
	void exitPassFailCheck(AutotestParser.PassFailCheckContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#bracketedPassfail}.
	 * @param ctx the parse tree
	 */
	void enterBracketedPassfail(AutotestParser.BracketedPassfailContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#bracketedPassfail}.
	 * @param ctx the parse tree
	 */
	void exitBracketedPassfail(AutotestParser.BracketedPassfailContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#httpStatusCheck}.
	 * @param ctx the parse tree
	 */
	void enterHttpStatusCheck(AutotestParser.HttpStatusCheckContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#httpStatusCheck}.
	 * @param ctx the parse tree
	 */
	void exitHttpStatusCheck(AutotestParser.HttpStatusCheckContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#xPathCheck}.
	 * @param ctx the parse tree
	 */
	void enterXPathCheck(AutotestParser.XPathCheckContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#xPathCheck}.
	 * @param ctx the parse tree
	 */
	void exitXPathCheck(AutotestParser.XPathCheckContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#xpathType}.
	 * @param ctx the parse tree
	 */
	void enterXpathType(AutotestParser.XpathTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#xpathType}.
	 * @param ctx the parse tree
	 */
	void exitXpathType(AutotestParser.XpathTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#xpathExpression}.
	 * @param ctx the parse tree
	 */
	void enterXpathExpression(AutotestParser.XpathExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#xpathExpression}.
	 * @param ctx the parse tree
	 */
	void exitXpathExpression(AutotestParser.XpathExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#xpathArg}.
	 * @param ctx the parse tree
	 */
	void enterXpathArg(AutotestParser.XpathArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#xpathArg}.
	 * @param ctx the parse tree
	 */
	void exitXpathArg(AutotestParser.XpathArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#xpathTypeNoArg}.
	 * @param ctx the parse tree
	 */
	void enterXpathTypeNoArg(AutotestParser.XpathTypeNoArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#xpathTypeNoArg}.
	 * @param ctx the parse tree
	 */
	void exitXpathTypeNoArg(AutotestParser.XpathTypeNoArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#xpathTypeArg}.
	 * @param ctx the parse tree
	 */
	void enterXpathTypeArg(AutotestParser.XpathTypeArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#xpathTypeArg}.
	 * @param ctx the parse tree
	 */
	void exitXpathTypeArg(AutotestParser.XpathTypeArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#httpHeaderCheck}.
	 * @param ctx the parse tree
	 */
	void enterHttpHeaderCheck(AutotestParser.HttpHeaderCheckContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#httpHeaderCheck}.
	 * @param ctx the parse tree
	 */
	void exitHttpHeaderCheck(AutotestParser.HttpHeaderCheckContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#nullCheck}.
	 * @param ctx the parse tree
	 */
	void enterNullCheck(AutotestParser.NullCheckContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#nullCheck}.
	 * @param ctx the parse tree
	 */
	void exitNullCheck(AutotestParser.NullCheckContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#nullCheckType}.
	 * @param ctx the parse tree
	 */
	void enterNullCheckType(AutotestParser.NullCheckTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#nullCheckType}.
	 * @param ctx the parse tree
	 */
	void exitNullCheckType(AutotestParser.NullCheckTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#matchString}.
	 * @param ctx the parse tree
	 */
	void enterMatchString(AutotestParser.MatchStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#matchString}.
	 * @param ctx the parse tree
	 */
	void exitMatchString(AutotestParser.MatchStringContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#usingExtractor}.
	 * @param ctx the parse tree
	 */
	void enterUsingExtractor(AutotestParser.UsingExtractorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#usingExtractor}.
	 * @param ctx the parse tree
	 */
	void exitUsingExtractor(AutotestParser.UsingExtractorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AutotestParser#substitution_tag}.
	 * @param ctx the parse tree
	 */
	void enterSubstitution_tag(AutotestParser.Substitution_tagContext ctx);
	/**
	 * Exit a parse tree produced by {@link AutotestParser#substitution_tag}.
	 * @param ctx the parse tree
	 */
	void exitSubstitution_tag(AutotestParser.Substitution_tagContext ctx);
}