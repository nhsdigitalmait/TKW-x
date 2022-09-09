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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.IOException;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractAutotestParser.getAutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParserBaseListener;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * extends the automatically generated class defining default behaviour for all
 * the listener methods.
 *
 * Used for cardinality checks and other sensibility checks
 *
 * @author sifa2
 */
public class AutotestListener extends AutotestParserBaseListener {

    private final String sourceFile;
    private final AutotestParser parser;

    private final HashMap<Namespace, TreeSet<String>> declarations;
    private final HashMap<Namespace, TreeSet<String>> references;
    private final HashSet<Namespace> blockDeclarations;

    private enum Namespace {
        TOP_LEVEL,
        SCHEDULE,
        TEST,
        MESSAGE,
        TEMPLATE,
        PROPERTYSET,
        HTTPHEADER,
        DATASOURCE,
        EXTRACTOR,
        PASSFAIL,
        SUBSTITUTION_TAG
    };

    /**
     * public constructor for initial construction
     *
     * @param sourceFile
     * @param parser
     */
    public AutotestListener(String sourceFile, AutotestParser parser) {
        this(sourceFile, parser, null);
    }

    /**
     * public Constructor for nested includes
     *
     * @param sourceFile
     * @param parser
     * @param al parent listener object
     */
    public AutotestListener(String sourceFile, AutotestParser parser, AutotestListener al) {
        //  this is the top level listener instantiation
        if (al == null) {
            declarations = new HashMap<>();
            references = new HashMap<>();
            for (Namespace namespace : Namespace.values()) {
                declarations.put(namespace, new java.util.TreeSet<>());
                if (!namespace.equals(Namespace.TOP_LEVEL)) {
                    references.put(namespace, new java.util.TreeSet<>());
                }
            }
        } else {
            // this is for include files so we reuse the existing maps
            declarations = al.declarations;
            references = al.references;
        }
        this.parser = parser;
        this.sourceFile = sourceFile;

        // check for redeclaration of any *block* within each local file
        blockDeclarations = new HashSet<>();

    }

    /**
     * is this symbol already defined in this namespace ?
     *
     * @param namespace
     * @param name
     * @return whether defined
     */
    private boolean alreadyDefined(Namespace namespace, String name) {
        // look for block declarations
        if (namespace == Namespace.TOP_LEVEL && name.startsWith("BEGIN ")) {
            try {
                Namespace n1 = Namespace.valueOf(name.replaceFirst("BEGIN ", "").replaceFirst("S$", "").toUpperCase());
                if (!blockDeclarations.contains(n1)) {
                    blockDeclarations.add(n1);
                    return false;
                } else {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                Logger.getInstance().log(Level.WARNING, AutotestListener.class.getName(), "Ignoring non namespace entry" + name);
            }
        } else if (declarations.get(namespace).contains(name)) {
            return true;
        } else {
            declarations.get(namespace).add(name);
            return false;
        }
        return true;
    }

    /**
     * return a guaranteed non null pointer to the terminal node required when a
     * terminal node may have more than one type eg IDENTIFIER or DOT SEPARATED
     * IDENTIFIER
     *
     * @param prc
     * @return the terminal node object
     */
    private TerminalNode getFirstTerminalNode(ParserRuleContext prc) {
        return prc.getToken(prc.start.getType(), 0);
    }

    /**
     * overload which derives the namespace from the context
     *
     * @param ctx
     * @param nameContext
     */
    private void checkCardinality(ParserRuleContext ctx, TerminalNode nameContext) {
        // derive the namespace from the context object type
        String namespace = ctx.getClass().getName().toLowerCase().replaceFirst("^.*\\$", "").replaceFirst("context$", "");

        // there's a couple of mismatches between context and block name that get addressed here
        if (namespace.equalsIgnoreCase("NAMEDPROPERTYSET")) {
            namespace = "propertyset";
        } else if (namespace.equalsIgnoreCase("NAMEDHTTPHEADERSET")) {
            namespace = "httpheader";
        }

        checkCardinality(Namespace.valueOf(namespace.toUpperCase()), ctx, nameContext);
    }

    /**
     *
     * @param namespace string label for the context level ie the namespace in
     * which the identifier must be unique and defined
     * @param ctx
     * @param nameContext terminal node containing the identifier
     */
    private void checkCardinality(Namespace namespace, ParserRuleContext ctx, TerminalNode nameContext) {
        if (alreadyDefined(namespace, nameContext.getText())) {
            parser.notifyErrorListeners(ctx.start,
                    "Syntax Error: source file " + sourceFile + " " + namespace + " element \"" + ctx.start.getText() + "\" already defined", null);
        }
    }

    @Override
    public void enterScript(AutotestParser.ScriptContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.SCRIPT());
    }

    @Override
    public void enterStop_when_complete(Stop_when_completeContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.STOP_WHEN_COMPLETE());
    }

    @Override
    public void enterValidator(ValidatorContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.VALIDATOR());
    }

    @Override
    public void enterSimulator(SimulatorContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.SIMULATOR());
    }

    @Override
    public void enterMessages(MessagesContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_MESSAGES());
    }

    @Override
    public void enterTemplates(TemplatesContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_TEMPLATES());
    }

    @Override
    public void enterDatasources(DatasourcesContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_DATASOURCES());
    }

    @Override
    public void enterExtractors(ExtractorsContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_EXTRACTORS());
    }

    @Override
    public void enterSchedules(SchedulesContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_SCHEDULES());
    }

    @Override
    public void enterTests(TestsContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_TESTS());
    }

    @Override
    public void enterPropertysets(PropertysetsContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_PROPERTYSETS());
    }

    @Override
    public void enterPassfails(PassfailsContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_PASSFAIL());
    }

    @Override
    public void enterSubstitution_tags(AutotestParser.Substitution_tagsContext ctx) {
        checkCardinality(Namespace.TOP_LEVEL, ctx, ctx.BEGIN_SUBSTITUTION_TAGS());
    }

    //--------------------------------------------------------------------------
    /**
     * check that each element of a block is unique eg for schedules that each
     * schedule name is unique
     *
     * @param ctx
     */
    @Override
    public void enterSchedule(ScheduleContext ctx) {
        checkCardinality(ctx, getFirstTerminalNode(ctx.scheduleName()));
        // any referenced tests
        for (TestNameContext test : ctx.testName()) {
            TerminalNode idc = getFirstTerminalNode(test);
            references.get(Namespace.TEST).add(idc.getText());
        }
    }

    @Override
    public void enterTest(TestContext ctx) {
        checkCardinality(ctx, getFirstTerminalNode(ctx.testName()));

        // any referenced message
        MessageNameContext mnc = ctx.messageName();
        if (mnc != null) {
            references.get(Namespace.MESSAGE).add(mnc.getText());
        }

        // any referenced passfail check
        PassFailCheckNameContext pfc = ctx.passFailCheckName();
        if (pfc != null) {
            references.get(Namespace.PASSFAIL).add(pfc.getText());
        }

        // any referenced property set or httpheader set
        for (TestArgContext tac : ctx.testArg()) {
            TestArgPairContext tapc = tac.testArgPair();
            if (tapc != null) {
                TestPropertySetContext tpsc = tapc.testPropertySet();
                if (tpsc != null) {
                    WithPropertySetContext twpcs = tpsc.withPropertySet();
                    if (twpcs != null) {
                        List<PropertySetNameContext> tpsncs = twpcs.propertySetName();
                        for (PropertySetNameContext tpsnc : tpsncs) {
                            references.get(Namespace.PROPERTYSET).add(tpsnc.getText());
                        }
                    }
                }

                TestHttpHeaderSetContext thsc = tapc.testHttpHeaderSet();
                if (thsc != null) {
                    WithHttpHeaderSetContext twhcs = thsc.withHttpHeaderSet();
                    if (twhcs != null) {
                        List<HttpHeaderSetNameContext> thsncs = twhcs.httpHeaderSetName();
                        for (HttpHeaderSetNameContext thsnc : thsncs) {
                            references.get(Namespace.HTTPHEADER).add(thsnc.getText());
                        }
                    }
                }
            } // if tapc not null
        } // for
    }

    @Override
    public void enterMessage(MessageContext ctx) {
        checkCardinality(ctx, getFirstTerminalNode(ctx.messageName()));
        // any referenced template
        for (MessageArgContext messageArg : ctx.messageArg()) {
            UsingTemplateContext utc = messageArg.usingTemplate();
            if (utc != null) {
                references.get(Namespace.TEMPLATE).add(utc.templateName().getText());
            } else {
                WithDatasourceContext wdc = messageArg.withDatasource();
                // exclude the special case of NULL
                if (wdc != null && !wdc.datasourceName().getText().equals("NULL")) {
                    references.get(Namespace.DATASOURCE).add(wdc.datasourceName().getText());
                }
            }
        }
    }

    @Override
    public void enterTemplate(TemplateContext ctx) {
        checkCardinality(ctx, getFirstTerminalNode(ctx.templateName()));
    }

    @Override
    public void enterNamedPropertySet(NamedPropertySetContext ctx) {
        checkCardinality(ctx, ctx.propertySetName().IDENTIFIER());
    }

    @Override
    public void enterNamedHttpHeaderSet(NamedHttpHeaderSetContext ctx) {
        checkCardinality(ctx, ctx.httpHeaderSetName().IDENTIFIER());
    }

    @Override
    public void enterDatasource(DatasourceContext ctx) {
        checkCardinality(ctx, ctx.datasourceName().IDENTIFIER());
    }

    @Override
    public void enterPassfail(PassfailContext ctx) {
        checkCardinality(ctx, getFirstTerminalNode(ctx.passFailCheckName()));

        // any referenced extractor
        XPathCheckContext xpc = ctx.passFailCheck().xPathCheck();
        if (xpc != null) {
            UsingExtractorContext uec = xpc.usingExtractor();
            if (uec != null) {
                ExtractorNameContext enc = uec.extractorName();
                if (enc != null) {
                    references.get(Namespace.EXTRACTOR).add(enc.getText());
                }
            }
        }
    }

    @Override
    public void enterExtractor(ExtractorContext ctx) {
        checkCardinality(ctx, ctx.extractorName().IDENTIFIER());
    }

    @Override
    public void enterSubstitution_tag(Substitution_tagContext ctx) {
        checkCardinality(ctx, ctx.SUBSTITUTION_TAG());
    }

    /**
     * handle includes recursively
     * @param ctx
     */
    @Override
    public void enterInclude_statement(Include_statementContext ctx) {
        String includedFilename = ctx.PATH().getText();
        //System.out.println("Entering include " + includedFilename);

        // this callback needs to walk the include file
        try {
            AutotestParser ap = getAutotestParser(includedFilename);
            AutotestListener al = new AutotestListener(includedFilename, ap, this);

            ParseTreeWalker walker = new ParseTreeWalker();
            AutotestParser.InputContext inputContext = ap.input();
            // Walk it and attach our listener
            walker.walk(al, inputContext);
        } catch (IOException ex) {
            Logger.getInstance().log(Level.SEVERE, AutotestListener.class.getName(), "IO Error " + ex.getMessage() + " reading test script file " + includedFilename);
        }
    }

    /**
     * To be called after the walk through reports unresolved references and
     * unused declarations return whether the scipt has referential integrity
     * 
     * @return whether the script has referebtial integrity
     */
    public boolean postParseAnalyse() {
        boolean result = true;
        for (Namespace namespace : Namespace.values()) {
            if (namespace != Namespace.TOP_LEVEL && namespace != Namespace.SCHEDULE) {

                // check for unresolved references
                for (String reference : references.get(namespace)) {
                    if (!declarations.get(namespace).contains(reference)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * output fatal errors
     */
    public void dumpErrors() {
        for (Namespace namespace : Namespace.values()) {
            //System.err.println("Checking namespace " + namespace.toString());
            if (namespace != Namespace.TOP_LEVEL && namespace != Namespace.SCHEDULE) {
                // check for unresolved references
                for (String reference : references.get(namespace)) {
                    if (!declarations.get(namespace).contains(reference)) {
                        System.err.println("Error: Unresolved " + namespace + " reference:" + reference);
                    }
                }
            }
        }
    }

    /**
     * output non fatal warnings
     */
    public void dumpWarnings() {
        for (Namespace namespace : Namespace.values()) {
            //System.err.println("Checking namespace " + namespace.toString());
            if (namespace != Namespace.TOP_LEVEL && namespace != Namespace.SCHEDULE) {

                // check for unused declarations
                for (String declaration : declarations.get(namespace)) {
                    if (!references.get(namespace).contains(declaration)) {
                        System.err.println("Warning: Unused " + namespace + " declaration:" + declaration);
                    }
                }
            }
        }
    }
}
