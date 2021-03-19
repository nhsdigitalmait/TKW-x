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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractAutotestParser.getAutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParserBaseVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassFailCheck;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.FUNCTION_PREFIX;

/**
 * Populates various collections from parsing a script used by Script.parse to
 * populate a script object
 *
 * @author SIFA2
 */
public class AutotestGrammarCompilerVisitor extends AutotestParserBaseVisitor {

    private final Script script;
    private final Properties bootProperties;

    private final HashMap<String, DataSource> datasources = new HashMap<>();
    private final HashMap<String, Message> messages = new HashMap<>();
    private final HashMap<String, PassFailCheck> passfailchecks = new HashMap<>();
    private final HashMap<String, ResponseExtractor> extractors = new HashMap<>();
    private final HashMap<String, Template> templates = new HashMap<>();
    private final LinkedHashMap<String, Test> tests = new LinkedHashMap<>();
    private final HashMap<String, NamedPropertySet> propertySets = new HashMap<>();
    private final HashMap<String, HashMap<String, Object>> httpHeaderSets = new HashMap<>();
    private final HashMap<String, Object> substitutionTags = new HashMap<>();

    private final ArrayList<Schedule> schedules = new ArrayList<>();
    private final HashMap<String, String> testRootTest = new HashMap<>();

    /**
     * public constructor
     *
     * @param script object to be populated
     * @param bootProperties
     * @throws java.lang.Exception
     */
    public AutotestGrammarCompilerVisitor(Script script, Properties bootProperties) throws Exception {
        this.script = script;
        this.bootProperties = bootProperties;
        propertySets.put(NamedPropertySet.BASELINE, new NamedPropertySet(bootProperties));
    }

    @Override
    public Object visitScript(ScriptContext ctx) {
        script.setName(ctx.scriptName().getText());
        return super.visitScript(ctx);
    }

    @Override
    public Object visitValidator(ValidatorContext ctx) {
        script.setValidatorConfig(ctx.PATH().getText());
        return super.visitValidator(ctx);
    }

    @Override
    public Object visitSimulator(SimulatorContext ctx) {
        script.setSimulatorRules(ctx.PATH().getText());
        return super.visitSimulator(ctx);
    }

    @Override
    public Object visitStop_when_complete(Stop_when_completeContext ctx) {
        script.setStopWhenComplete();
        return super.visitStop_when_complete(ctx);
    }

    @Override
    public Object visitSchedule(ScheduleContext ctx) {
        getSchedules().add(new Schedule(ctx));
        String rootTestName = null;
        for (TestNameContext testNameCtx : ctx.testName()) {
            if (rootTestName == null) {
                rootTestName = testNameCtx.getText();
            }
            // builds a hashmap relating a test to the root test in the schedule
            // This is required so that we can derive the appropriate Extractor defined in the PFC for a given schedule
            if (testRootTest.get(testNameCtx.getText()) == null) {
                testRootTest.put(testNameCtx.getText(), rootTestName);
            } else {
                // This is only a concern if the PassFailCheck associated with the Test has an Extractor defined.
                //Logger.getInstance().log(WARNING, AutotestGrammarCompilerVisitor.class.getName(),
                //        "Warning attempt to override root for a repeated test " + testNameCtx.getText());
            }
        }
        return super.visitSchedule(ctx);
    }

    @Override
    public Object visitTest(TestContext ctx) {
        Test t = new Test(ctx);
        t.setScript(script);
        getTests().put(t.getName(), t);
        return super.visitTest(ctx);
    }

    @Override
    public Object visitMessage(MessageContext ctx) {
        Message m = new Message(ctx);
        getMessages().put(m.getName(), m);
        return super.visitMessage(ctx);
    }

    @Override
    public Object visitTemplate(TemplateContext ctx) {
        try {
            Template tp = new Template(ctx);
            getTemplates().put(tp.getName(), tp);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, AutotestGrammarCompilerVisitor.class.getName(), "visitTemplate " + ex.getMessage());
        }
        return super.visitTemplate(ctx);
    }

    @Override
    public Object visitNamedPropertySet(NamedPropertySetContext ctx) {
        try {
            NamedPropertySet n = new NamedPropertySet(ctx);
            getPropertySets().put(n.getName(), n);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, AutotestGrammarCompilerVisitor.class.getName(), "visitNamedPropertyset " + ex.getMessage());
        }
        return super.visitNamedPropertySet(ctx);
    }

    @Override
    public Object visitDatasources(DatasourcesContext ctx) {
        for (DatasourceContext datasourceCtx : ctx.datasource()) {
            try {
                DataSource ds = makeDataSource(datasourceCtx);
                getDatasources().put(ds.getName(), ds);
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE, AutotestGrammarCompilerVisitor.class.getName(), "visitDatasources " + ex.getMessage());
            }
        }
        return super.visitDatasources(ctx);
    }

    @Override
    public Object visitExtractor(ExtractorContext ctx) {
        try {
            ResponseExtractor re = makeExtractor(ctx);
            getExtractors().put(re.getName(), re);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, AutotestGrammarCompilerVisitor.class.getName(), "visitExtractor " + ex.getMessage());
        }
        return super.visitExtractor(ctx);
    }

    @Override
    public Object visitPassfail(PassfailContext ctx) {
        try {
            PassFailCheck pf = makePassFail(ctx);
            getPassfailchecks().put(pf.getName(), pf);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, AutotestGrammarCompilerVisitor.class.getName(), "visitPassfail " + ex.getMessage());
        }
        return super.visitPassfail(ctx);
    }

    @Override
    public Object visitNamedHttpHeaderSet(NamedHttpHeaderSetContext ctx) {
        String headerSetName = ctx.httpHeaderSetName().getText();
        httpHeaderSets.put(headerSetName, new HashMap<>());
        ListIterator<HttpHeaderSetDirectiveContext> iter = ctx.httpHeaderSetDirective().listIterator();
        while (iter.hasNext()) {
            HttpHeaderSetDirectiveContext directiveCtx = iter.next();
            httpHeaderSets.get(headerSetName).put(directiveCtx.httpHeaderName().getText(), handlePsArg(directiveCtx.psArg()));
        }
        return super.visitNamedHttpHeaderSet(ctx);
    }

    /**
     * handles lines containing function:functionname arg
     *
     *
     * @param psArgCtx
     * @param headerSetName
     * @return either an object for a function or a string
     */
    private Object handlePsArg(PsArgContext psArgCtx) {
        if (psArgCtx.psValue() != null) {
            return psArgCtx.psValue().getText();
        } else {
            String functionStr = psArgCtx.psFunctionName().getText();
            if (functionStr.startsWith(FUNCTION_PREFIX)) {
                int lastdot = functionStr.lastIndexOf('.');
                int classstart = FUNCTION_PREFIX.length();
                String methodname = functionStr.substring(lastdot + 1);
                String classname = functionStr.substring(classstart, lastdot);
                try {
                    List<FunctionArgContext> functionArgsCtx = psArgCtx.functionArg();
                    Iterator<FunctionArgContext> argsIter = functionArgsCtx.iterator();
                    Object[] params = new Object[functionArgsCtx.size()];
                    Class[] parameterTypes = null;
                    if (functionArgsCtx.size() > 0) {
                        parameterTypes = new Class[functionArgsCtx.size()];
                    }
                    int i = 0;
                    while (argsIter.hasNext()) {
                        // we assume for now all parms are strings
                        parameterTypes[i] = String.class;
                        FunctionArgContext paramCtx = argsIter.next();
                        params[i++] = paramCtx.getText();
                    }
                    Class c = Class.forName(classname);
                    Method method = c.getMethod(methodname, parameterTypes);
                    // assumes we only ever use this method with one sig type
                    return new Object[]{method, params};
                } catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
                    Logger.getInstance().log(SEVERE, AutotestGrammarCompilerVisitor.class.getName(), "handleFunction " + ex.getMessage());
                }
            } else {
                return functionStr;
            }
        }
        return null;
    }

    @Override
    public Object visitInclude_statement(Include_statementContext ctx) {
        //System.out.println("including "+ ctx.PATH().getText());
        String fileName = ctx.PATH().getText();
        try {
            // Pass the tokens to the parser
            AutotestParser parser = getAutotestParser(fileName);

            ParseTree pt = parser.input();
            Object x = visit(pt);

        } catch (IOException | RecognitionException ex) {
            Logger.getInstance().log(SEVERE, ScriptParser.class.getName(), "IO Error " + ex.getMessage() + " reading test script file " + fileName);
        }
        return super.visitInclude_statement(ctx);
    }

    @Override
    public Object visitSubstitution_tag(Substitution_tagContext ctx) {
        if (ctx.psArg() != null) {
            substitutionTags.put(ctx.SUBSTITUTION_TAG().getText(), handlePsArg(ctx.psArg()));
        } else if (ctx.LITERAL() != null) {
            substitutionTags.put(ctx.SUBSTITUTION_TAG().getText(), ctx.QUOTED_STRING().getText());
        }
        return super.visitSubstitution_tag(ctx);
    }

    private DataSource makeDataSource(DatasourceContext datasourceCtx)
            throws Exception {
        String dsclass = "tks.autotest.datasource." + datasourceCtx.datasourceType().getText();
        dsclass = bootProperties.getProperty(dsclass);
        if (dsclass == null) {
            throw new Exception("DataSource " + datasourceCtx.datasourceType().getText() + " has no class defined");
        }
        DataSource ds = (DataSource) Class.forName(dsclass).newInstance();
        ds.init(datasourceCtx);
        return ds;
    }

    private ResponseExtractor makeExtractor(ExtractorContext extractorCtx)
            throws Exception {
        String reclass = "tks.autotest.extractor." + extractorCtx.extractorType().getText();
        reclass = bootProperties.getProperty(reclass);
        if (reclass == null) {
            throw new Exception("ResponseExtractor " + extractorCtx.extractorType().getText() + " has no class defined");
        }
        ResponseExtractor re = (ResponseExtractor) Class.forName(reclass).newInstance();
        re.init(extractorCtx);
        return re;
    }

    /**
     * walk through all the nodes
     *
     * @param parseTree
     * @param al accumulates the text in the tokens
     */
    private void walkParseTree(ParseTree parseTree, ArrayList<String> al) {
        if (parseTree.getChildCount() == 0) {
            if (al != null && !parseTree.getText().trim().isEmpty()) {
                al.add(parseTree.getText());
            }
        } else {
            for (int i = 0; i < parseTree.getChildCount(); i++) {
                walkParseTree(parseTree.getChild(i), al);
            }
        }
    }

    /**
     * Instantiates the correct check object from the syntax tree All are named
     * for the check apart from XPathCheck and NullCheck where the syntax
     * defines different tests with similar syntax TODO warning there is an
     * almost identical method in ScriptParser that needs amending in similar
     * style
     *
     * @param passfailCtx
     * @return initialised pass fail check object
     * @throws Exception
     */
    private PassFailCheck makePassFail(PassfailContext passfailCtx) throws Exception {

        PassFailCheckContext passfailCheckCtx = passfailCtx.passFailCheck();

        XPathCheckContext xPathCheckCtx = passfailCheckCtx.xPathCheck();
        HttpHeaderCheckContext httpHeaderCheckCtx = passfailCheckCtx.httpHeaderCheck();
        HttpStatusCheckContext httpStatusCheckCtx = passfailCheckCtx.httpStatusCheck();
        HttpHeaderCorrelationCheckContext httpHeaderCorrelationCheckCtx = passfailCheckCtx.httpHeaderCorrelationCheck();

        String checkType = null;
        if (xPathCheckCtx != null) {
            checkType = xPathCheckCtx.xpathType().getText();
        } else if (httpHeaderCheckCtx != null) {
            checkType = httpHeaderCheckCtx.HTTPHEADERCHECK().getText();
        } else if (httpStatusCheckCtx != null) {
            checkType = httpStatusCheckCtx.HTTPSTATUSCHECK().getText();
        } else if (httpHeaderCorrelationCheckCtx != null) {
            checkType = httpHeaderCorrelationCheckCtx.HTTPHEADERCORRELATIONCHECK().getText();
        } else {
            NullCheckContext nullCheckContext = passfailCheckCtx.nullCheck();
            if (nullCheckContext != null) {
                checkType = nullCheckContext.nullCheckType().getText();
            } else {
                checkType = passfailCheckCtx.getChild(0).getText();
            }
        }

        String pfclass = "tks.autotest.passfail." + checkType;
        pfclass = bootProperties.getProperty(pfclass);
        if (pfclass == null) {
            throw new Exception("PassFail check " + checkType + " has no class defined");
        }
        PassFailCheck pf = (PassFailCheck) Class.forName(pfclass).newInstance();
        pf.init(passfailCtx);
        return pf;
    }

    /**
     * @return the datasources
     */
    public HashMap<String, DataSource> getDatasources() {
        return datasources;
    }

    /**
     * @return the messages
     */
    public HashMap<String, Message> getMessages() {
        return messages;
    }

    /**
     * @return the passfailchecks
     */
    public HashMap<String, PassFailCheck> getPassfailchecks() {
        return passfailchecks;
    }

    /**
     * @return the extractors
     */
    public HashMap<String, ResponseExtractor> getExtractors() {
        return extractors;
    }

    /**
     * @return the templates
     */
    public HashMap<String, Template> getTemplates() {
        return templates;
    }

    /**
     * @return the tests
     */
    public HashMap<String, Test> getTests() {
        return tests;
    }

    /**
     * @return the propertySets
     */
    public HashMap<String, NamedPropertySet> getPropertySets() {
        return propertySets;
    }

    /**
     * @return the schedules
     */
    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    /**
     * @return the httpHeaderSets
     */
    public HashMap<String, HashMap<String, Object>> getHttpHeaderSets() {
        return httpHeaderSets;
    }

    /**
     * @return the SubstitutionTags
     */
    public HashMap<String, Object> getSubstitutionTags() {
        return substitutionTags;
    }


    /**
     * uses a map to enable us to back track to the first test in the schedule
     * so that we can derive the message and hence the datasource for the test
     *
     * @param testName
     * @return The name of the first or root test for the given test
     */
    public String getRootTest(String testName) {
        return testRootTest.get(testName);
    }
}
