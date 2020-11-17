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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.nhs.digital.mait.spinetools.spine.connection.ConnectionManager;
import uk.nhs.digital.mait.spinetools.spine.messaging.ITKTrunkHandler;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import static uk.nhs.digital.mait.tkwx.tk.boot.SpineToolsTransport.ASYNCWAIT;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ReconfigureTags;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Schedule.SPINETOOLS_TRANSMITTER_MODE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.SendHow.FUNCTION;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.SendHow.NO_SEND;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.SendHow.UNDEFINED;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PreSubstituteContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PreSubstitutionPointsContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PreTransformContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PreTransformPointsContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.SubstPairContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestArgContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestArgPairContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestHttpHeaderSetContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestIntArgContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestPropertySetContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestStringArgContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestSynchronicityContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestURLArgContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TransformPointContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassFailCheck;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Copyable;
import uk.nhs.digital.mait.commonutils.util.FileLocker;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;

/**
 * Class representing a single test - either construct a message from a template
 * and send it, or execute a TestFunction.
 *
 * NOTE: This version is SOAP/WS only. Before editing this too much the
 * developer should be aware that the next version will contain a substantial
 * re-factoring of this class, to handle testing in a transport-independent
 * environment.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Test
        implements Linkable, Copyable, Cloneable {

    enum SendHow {
        UNDEFINED,
        SEND_TKW,
        SEND_RAW, // TODO does this even work?
        FUNCTION,
        NO_SEND
    };

    private static final SimpleDateFormat FILEDATE = new SimpleDateFormat(FILEDATEMASK);

    private Message tosend = null;
    private PassFailCheck synccheck = null;
    private PassFailCheck asynccheck = null;
    private AsynchronousLogCorrelator correlator = null;
    private ArrayList<NamedPropertySet> propertySets = null;
    private ArrayList<HashMap<String, Object>> httpHeaderSets = null;
    private HashMap<String, Object> substitutionTags = null;

    private List<AutotestParser.HttpHeaderSetNameContext> httpHeaderSetNames;
    private List<AutotestParser.PropertySetNameContext> propertySetNames;

    private HashMap<String, ArrayList<String>> preTransforms = null;
    private HashMap<String, ArrayList<RegexpSubstitution>> preSubstitutions = null;

    private SendHow sendhow = UNDEFINED;

    private String msgname = null;
    private String toUrl = null;
    private String fromUrl = null;
    private String replyTo = null;
    private String synccheckname = null;
    private String asynccheckname = null;
    private String correlatorName = null;
    private String promptName = null;
    private String chainName = null;
    private String chainType = null; // SYNC or ASYNC
    private String commentText = null;

    private PreTransformContext preSendTransformCtx = null;
    private PreTransformPointsContext applyPreSendTransformToCtx = null;

    private PreSubstituteContext preSendSubstituteCtx = null;
    private PreSubstitutionPointsContext applyPreSendSubstitutionToCtx = null;

    private String profileId = null;

    private String additionalResultText = null;

    private int wait = 0;
    private int txTimestampOffset = 0;
    private int asyncTimestampOffset = 0;
    private int correlationCount = 1;

    private Script script = null;
    private TestFunction function = null;
    private TestContext functionCtx = null;

    private String testName = null;
    private Test previousTest = null;
    private Test nextTest = null;
    private File transmitterLog = null;
    private File asynchronousResponseLog = null;
    private String transmitMode;
    private boolean correlationCountError;
    private boolean hasBase;

    // for accessing expected results columns from a datasource
    private DataSource datasource = null;
    private String recordid = null;

    private final static HashMap<String, String> TRANSMIT_MODE_SUFFIXES = new HashMap<>();
    private static final String SEND_BUSINESS_ACK = "SendBusinessAck-v1-0";
    private static final String SEND_INFRASTRUCTURE_ACK = "SendInfrastructureAck-v1-0";

    /**
     * regular expression substitution class
     */
    public static class RegexpSubstitution {

        /**
         * public constructor
         *
         * @param matchRegExp
         * @param substituteRegExp
         */
        public RegexpSubstitution(String matchRegExp, String substituteRegExp) {
            this.matchRegExp = matchRegExp;
            this.substituteRegExp = substituteRegExp;
        }

        /**
         * performs the substitution operation on the source string
         *
         * @param source
         * @return the substituted String object
         */
        public String substitute(String source) {
            if (matchRegExp == null) {
                return source;
            } else if (substituteRegExp == null) {
                return source.replaceAll(matchRegExp, "$1");
            } else {
                return source.replaceAll(matchRegExp, substituteRegExp);
            }
        }

        private final String matchRegExp;
        private final String substituteRegExp;
    }

    /**
     * antlr parser constructor
     *
     * @param testCtx
     */
    public Test(TestContext testCtx) {
        testName = testCtx.testName().getText();

        sendhow = NO_SEND;
        switch (testCtx.getChild(1).getText()) {
            case "ASYNC":
                promptName = testCtx.QUOTED_STRING().getText();
                asynccheckname = testCtx.passFailCheckName().getText();
                parseTestArgs(testCtx);
                break;
            case "CHAIN":
                // 2 should be SYNC or ASYNC 
                chainType = testCtx.testSynchronicity().getText();
                chainName = testCtx.passFailCheckName().getText();
                parseTestArgs(testCtx);
                break;
            case "FUNCTION":
                sendhow = FUNCTION;
                functionCtx = testCtx;
                break;
            default:
                msgname = testCtx.messageName().getText();
                sendhow = SendHow.valueOf(testCtx.sendType().getText().toUpperCase());
                parseTestArgs(testCtx);
        }

        correlationCountError = false;
    }

    /**
     * Implementation of Copyable interface method exporting the protected clone
     * interface and allowing a shallow copy
     *
     * @return a copy of this object
     */
    @Override
    public Object copy() {
        try {
            Test cp = (Test) super.clone();
            //System.out.println("Copying test "+getName()+ " "+ Integer.toHexString(hashCode()) + " to "+Integer.toHexString(cp.hashCode()));
            return cp;
        } catch (CloneNotSupportedException ex) {
            Logger.getInstance().log(SEVERE, Test.class.getName(), ex.getMessage());
        }
        return null;
    }

    /**
     * @return the message name
     */
    public String getMsgname() {
        return msgname;
    }

    /**
     * return the synchronous PassFailCheck name
     *
     * @return String
     */
    public String getSynchronousPassFailCheckName() {
        return synccheckname;
    }

    /**
     *
     * @param testCtx
     * @throws NumberFormatException
     */
    private void parseTestArgs(TestContext testCtx) throws NumberFormatException {
        for (TestArgContext testArgCtx : testCtx.testArg()) {

            // testArgPair
            TestArgPairContext testArgPairCtx = testArgCtx.testArgPair();
            if (testArgPairCtx != null) {

                // testIntArg
                TestIntArgContext testIntArgCtx = testArgPairCtx.testIntArg();
                if (testIntArgCtx != null) {
                    int intVal = Integer.parseInt(testArgPairCtx.INTEGER().getText());
                    switch (testIntArgCtx.getText()) {
                        case "TXTIMESTAMPOFFSET":
                            txTimestampOffset = intVal;
                            break;
                        case "ASYNCTIMESTAMPOFFSET":
                            asyncTimestampOffset = intVal;
                            break;
                        case "WAIT":
                            wait = intVal;
                            break;
                        case "CORRELATIONCOUNT":
                            correlationCount = intVal;
                            break;
                    }
                    continue;
                }

                // testStringArg
                TestStringArgContext testStringArg = testArgPairCtx.testStringArg();
                if (testStringArg != null) {
                    String stringVal = testArgPairCtx.testString().getText();
                    switch (testStringArg.getText()) {
                        case "TEXT":
                            commentText = stringVal;
                            break;
                        case "PROFILEID":
                            profileId = stringVal;
                            break;
                        case "CORRELATOR":
                            correlatorName = stringVal;
                            break;
                    }
                    continue;
                }

                // testPassfailCheckArg
                TestSynchronicityContext testPassfailCheckArgCtx = testArgPairCtx.testSynchronicity();
                if (testPassfailCheckArgCtx != null) {
                    String checkName = testArgPairCtx.passFailCheckName().getText();
                    switch (testPassfailCheckArgCtx.getText()) {
                        case "SYNC":
                            synccheckname = checkName;
                            break;
                        case "ASYNC":
                            asynccheckname = checkName;
                            break;
                    }
                    continue;
                }

                // testURLArg
                TestURLArgContext testURLArgCtx = testArgPairCtx.testURLArg();
                if (testURLArgCtx != null) {
                    String url = testArgPairCtx.testURL().getText();
                    switch (testURLArgCtx.getText()) {
                        case "TO":
                            toUrl = url;
                            break;
                        case "FROM":
                            fromUrl = url;
                            break;
                        case "REPLYTO":
                            replyTo = url;
                            break;
                    }
                    continue;
                }

                // testPropertySet
                TestPropertySetContext testPropertySetCtx = testArgPairCtx.testPropertySet();
                if (testPropertySetCtx != null) {
                    // use of 'base' is  a special case for propertysets
                    hasBase = testPropertySetCtx.withPropertySet().BASE() != null;
                    propertySetNames = testPropertySetCtx.withPropertySet().propertySetName();
                }

                // testHttpHeaders
                TestHttpHeaderSetContext testHttpHeaderSet = testArgPairCtx.testHttpHeaderSet();
                if (testHttpHeaderSet != null) {
                    httpHeaderSetNames = testHttpHeaderSet.withHttpHeaderSet().httpHeaderSetName();
                }

                continue;
            }  // testArgPair

            // preTransform
            PreTransformContext ptc = testArgCtx.preTransform();
            if (ptc != null) {
                preSendTransformCtx = ptc;
                continue;
            }

            // preTransformPoints
            PreTransformPointsContext ptpc = testArgCtx.preTransformPoints();
            if (ptpc != null) {
                applyPreSendTransformToCtx = ptpc;
                continue;
            }

            // preSubstitute
            PreSubstituteContext psc = testArgCtx.preSubstitute();
            if (psc != null) {
                preSendSubstituteCtx = psc;
                continue;
            }

            // preSubstitutePoints
            PreSubstitutionPointsContext pspc = testArgCtx.preSubstitutionPoints();
            if (pspc != null) {
                applyPreSendSubstitutionToCtx = pspc;
                continue;
            }
        } // testArg loop
    } // parseTestArgs

    /**
     * for chained test sets attributes based on the previous test in the chain
     *
     * @throws Exception
     */
    private void getChainVariables() throws Exception {
        //System.out.println("Calling getChainVariables on "+getName()+ " "+Integer.toHexString(hashCode()));
        Test logTest = getChain(previousTest);
        asynchronousResponseLog = logTest.asynchronousResponseLog;
        additionalResultText = logTest.additionalResultText;
        propertySets = logTest.propertySets;
        httpHeaderSets = logTest.httpHeaderSets;
        sendhow = logTest.sendhow;
        function = logTest.function;
        transmitterLog = logTest.transmitterLog;
        txTimestampOffset = logTest.txTimestampOffset;
        asyncTimestampOffset = logTest.asyncTimestampOffset;
        correlator = logTest.correlator;
        substitutionTags = logTest.substitutionTags;

        // used for accessing expected values in a TDV
        datasource = logTest.datasource;
        recordid = logTest.recordid;
    }

    /**
     *
     * @param script
     */
    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * implements linkable interface populates functions, passfailchecks,
     * correlator, transforms, propertysets, httpheadersets
     *
     * @param scriptParser
     * @throws Exception
     */
    @Override
    public void link(ScriptParser scriptParser)
            throws Exception {
        //System.out.println("link called on "+getName()+ " "+Integer.toHexString(hashCode()));
        if (sendhow == FUNCTION) {
            String fname = "tks.autotest.testfunction." + functionCtx.testFunctionName().getText();
            fname = scriptParser.getBootProperties().getProperty(fname);
            if (fname == null) {
                throw new Exception("No class defined for test function " + fname);
            }
            function = (uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestFunction) Class.forName(fname).newInstance();
            function.init(functionCtx);
        } else {
            if (msgname != null) {
                tosend = scriptParser.getMessage(msgname);
                if (tosend == null) {
                    throw new Exception("Test " + testName + " : message " + msgname + " not found");
                }
                if (tosend.getDatasourceName() != null) {
                    // we need to peek ahead because we need these set before the message is instantiated
                    // so that we can do the httpheader datasource substitutions
                    datasource = tosend.getDatasource();
                    recordid = tosend.getRecordid();
                }
            }
            if (synccheckname != null) {
                synccheck = scriptParser.getPassFailCheck(synccheckname);
                if (synccheck == null) {
                    throw new Exception("Test " + testName + " requested synchronous check " + synccheckname + " not found");
                }
            }
            if (asynccheckname != null) {
                asynccheck = scriptParser.getPassFailCheck(asynccheckname);
                if (asynccheck == null) {
                    throw new Exception("Test " + testName + " requested asynchronous check " + asynccheckname + " not found");
                }
            }
            if (correlatorName != null) {
                StringBuilder cn = new StringBuilder("tks.correlator.");
                cn.append(correlatorName);
                cn.append(".class");
                String c = cn.toString();
                String className = script.getProperty(c);
                if (className == null) {
                    throw new Exception("No such correlator: " + c);
                }
                try {
                    correlator = (AsynchronousLogCorrelator) Class.forName(className).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new Exception("Cannot instantiate correlator " + c + " : " + className + " : " + e.toString());
                }
            }

            // xslt transforms
            if (preSendTransformCtx != null) {
                if ((applyPreSendTransformToCtx == null)
                        || (applyPreSendTransformToCtx.plusDelimTransformPoints().transformPoint().isEmpty())) {
                    throw new Exception("Mismatch: PRETRANSFORM specified without APPLYPRETRANSFORMTO");
                }

                List<TerminalNode> pathsCtx = preSendTransformCtx.plusDelimPaths().PATH();
                List<TransformPointContext> transformPointsCtx = applyPreSendTransformToCtx.plusDelimTransformPoints().transformPoint();

                if (pathsCtx.size() != transformPointsCtx.size()) {
                    throw new Exception("Mismatch: PRETRANSFORM and APPLYPRETRANSFORMTO must have the same number of elements");
                }

                preTransforms = new HashMap<>();
                for (int i = 0; i < pathsCtx.size(); i++) {
                    String transformPath = pathsCtx.get(i).getText();
                    try ( FileInputStream fis = new FileInputStream(transformPath)) {
                        TransformManager.getInstance().addTransform(transformPath, fis);
                    }
                    String loc = transformPointsCtx.get(i).getText();
                    if (preTransforms.containsKey(loc)) {
                        preTransforms.get(loc).add(transformPath);
                    } else {
                        // this is an array because you can have > 1 transform for a transform point
                        ArrayList<String> a = new ArrayList<>();
                        a.add(transformPath);
                        preTransforms.put(loc, a);
                    }
                }
            } // preSendTransform

            if (applyPreSendTransformToCtx != null) {
                if ((preSendTransformCtx == null)
                        || (preSendTransformCtx.plusDelimPaths().PATH().isEmpty())) {
                    throw new Exception("Mismatch: APPLYPRETRANSFORMTO specified without PRETRANSFORM");
                }
            }

            // reg exp substitutions
            // Split this on +, make and load an arraylist, and check the applyTo
            if (preSendSubstituteCtx != null) {
                if ((applyPreSendSubstitutionToCtx == null)
                        || (applyPreSendSubstitutionToCtx.plusDelimTransformPoints().transformPoint().isEmpty())) {
                    throw new Exception("Mismatch: PRESUBSTITUTE specified without APPLYPRESUBSTITUTETO");
                }

                List<SubstPairContext> substPairCtx = preSendSubstituteCtx.substPairs().substPair();
                List<AutotestParser.TransformPointContext> transformPointCtx = applyPreSendSubstitutionToCtx.plusDelimTransformPoints().transformPoint();

                if (substPairCtx.size() != transformPointCtx.size()) {
                    throw new Exception("Mismatch: PRESUBSTITUTE and APPLYPRESUBSTITUTETO must have the same number of elements");
                }

                preSubstitutions = new HashMap<>();
                for (int i = 0; i < substPairCtx.size(); i++) {
                    RegexpSubstitution s = new RegexpSubstitution(substPairCtx.get(i).matchRegexp().getText(), substPairCtx.get(i).substituteRegexp().getText());
                    String loc = transformPointCtx.get(i).getText();
                    if (preSubstitutions.containsKey(loc)) {
                        // add to existing array
                        preSubstitutions.get(loc).add(s);
                    } else {
                        // create new array
                        ArrayList<RegexpSubstitution> a = new ArrayList<>();
                        a.add(s);
                        preSubstitutions.put(loc, a);
                    }
                }
            }

            if (applyPreSendSubstitutionToCtx != null) {
                if ((preSendSubstituteCtx == null)
                        || (preSendSubstituteCtx.substPairs().substPair().isEmpty())) {
                    throw new Exception("Mismatch: APPLYPRESUBSTITUTETO specified without PRESUBSTITUTE");
                }
            }

            if (propertySetNames != null || hasBase) {
                propertySets = new ArrayList<>();
                if (hasBase) {
                    propertySets.add(scriptParser.getPropertySet("base"));
                }
                if (propertySetNames != null) {
                    Iterator<AutotestParser.PropertySetNameContext> iter = propertySetNames.iterator();
                    while (iter.hasNext()) {
                        String propertySetName = iter.next().IDENTIFIER().getText();
                        NamedPropertySet namedPropertySet = scriptParser.getPropertySet(propertySetName);
                        if (namedPropertySet == null) {
                            throw new Exception("Test " + propertySetName + " requested named property set " + propertySetName + " not found");
                        }
                        propertySets.add(namedPropertySet);
                    }
                }
            }

            substitutionTags = scriptParser.getSubstitutionTags();
            for (String key : substitutionTags.keySet()) {
                String value = processParameterObject(substitutionTags.get(key));

                // currently the ATM substitution tags only apply to urls and http headers but they could be useful for other entities
                if (toUrl != null) {
                    toUrl = toUrl.replaceAll(key, value);
                }
                if (fromUrl != fromUrl) {
                    fromUrl = fromUrl.replaceAll(key, value);
                }

            }

            if (httpHeaderSetNames != null) {
                httpHeaderSets = new ArrayList<>();
                Iterator<AutotestParser.HttpHeaderSetNameContext> iter = httpHeaderSetNames.iterator();
                while (iter.hasNext()) {
                    String httpHeaderSetName = iter.next().IDENTIFIER().getText();
                    HashMap<String, Object> httpHeaderSet = scriptParser.getHttpHeaderSet(httpHeaderSetName);
                    if (httpHeaderSet == null) {
                        throw new Exception("Test " + httpHeaderSetName + " requested named http header set " + httpHeaderSetName + " not found");
                    }
                    for (String key : substitutionTags.keySet()) {
                        // only do this for literals not functions
                        if (substitutionTags.get(key) instanceof String) {
                            String value = (String) substitutionTags.get(key);
                            for (String httpHeaderKey : httpHeaderSet.keySet()) {
                                Object httpHeaderValue = httpHeaderSet.get(httpHeaderKey);
                                if (httpHeaderValue instanceof String) {
                                    httpHeaderSet.put(httpHeaderKey, ((String) httpHeaderValue).replaceAll(key, value));
                                } else if (httpHeaderValue instanceof Object[]) {
                                    Object[] oa = (Object[]) httpHeaderValue;
                                    // in this invocation oa[1] is an array of string parameters which may be substituted
                                    if (oa.length > 1) {
                                        Object[] params = (Object[]) oa[1];
                                        for (int i = 0; i < params.length; i++) {
                                            params[i] = ((String) params[i]).replaceAll(key, value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    httpHeaderSets.add(httpHeaderSet);
                }
            }
        }
    } // link

    /**
     * dequotes quoted string and invokes methods depending on Object type
     *
     * @param o Object parameter to be processed
     * @return processed String value
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String processParameterObject(Object o) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        String value = null;
        if (o instanceof String) {
           // ensure we only remove matching paired surrounding quotes
           value = o.toString().replaceFirst("^\"(.*)\"$", "$1");
        } else if (o instanceof Method) {
            Method method = (Method) o;
            // This is a slighly premature execution but there's not much in it.
            value = (String) method.invoke(null, (Object[]) null);
        } else if (o instanceof Object[]) {
            // we use this form if the method takes parameters
            Object[] oa = (Object[]) o;
            if (oa.length != 2) {
                Logger.getInstance().log(SEVERE, Test.class.getName(), "Unexpected length for object array " + oa.length);
            }
            Method method = (Method) oa[0];
            Object[] params = (Object[]) oa[1];
            value = (String) method.invoke(null, params);
        }
        return value;
    }

    /**
     * accessor for test name
     *
     * @return
     */
    public String getName() {
        return testName;
    }

    /**
     * creates references to support CHAIN tests
     *
     * @param prevTest The prior test to chain this test to test can be chained
     * to use the results of the last synchronous and asynchronous response
     */
    public void setChain(Test prevTest) {
        if (chainName != null) {
//            System.out.println("setChain on "+getName()+ " "+Integer.toHexString(hashCode())+ " with test "+
//                    prevTest.getName() + " "+Integer.toHexString(prevTest.hashCode()));
            previousTest = prevTest;
            prevTest.nextTest = this;
            if (chainType.equals("SYNC")) {
                synccheckname = chainName;
            } else if (chainType.equals("ASYNC")) {
                asynccheckname = chainName;
            } else {
                Logger.getInstance().log(WARNING, Test.class.getName(), "Unexpected chain type for test " + getName());
            }
        }
    }

    /**
     * overload
     *
     * @param instanceName
     * @param schedule
     * @return
     * @throws Exception
     */
    public boolean execute(String instanceName, Schedule schedule)
            throws Exception {
        return execute(instanceName, schedule, 0);
    }

    /**
     * execute the test: send the message, do the checks, log everything
     *
     * @param instanceName
     * @param schedule
     * @param iteration loop count or 0 of not a LOOP test
     * @return whether all tests passed or not
     * @throws Exception
     */
    public boolean execute(String instanceName, Schedule schedule, int iteration)
            throws Exception {
        // 1. The transmitter and simulator should be running at this point with the
        //      appropriate directories configured.
        // 2. Make the necessary messages and put them in the transmitter send directory
        // 3. Call execute() on the transmitter to do the sending
        // 4. Do the checks to see if it worked
        // 5. Log everything
        // 6. Clean up the transmitter directory

        //System.out.println("Calling execute on "+getName()+" "+Integer.toHexString(hashCode()));
        transmitterLog = null;
        asynchronousResponseLog = null;

        if (chainName != null) {
            getChainVariables();
        }

        // need to get the datasource and record id from the message ahead of sending
        // so that we can substitute any httpheaders
        // at present the datasource is only available *after* the message is created
        // by callingSendRequest which is too late
        // If we have any property sets to apply, do it here before anything else.
        processPropertySets(schedule);

        Properties p = schedule.getScript().getBootProperties();
        transmitMode = p.getProperty(TRANSMITTERMODE_PROPERTY);
        if (transmitMode.equals(SPINETOOLS_TRANSMITTER_MODE)) {
            setupITKTrunkRequestHandlers();
        }

        // we need this to set the the properties correctly
        ToolkitService transport = ServiceManager.getInstance().getService(transmitMode + "Transport");
        ((Reconfigurable) transport).reconfigure(ReconfigureTags.SAVED_MESSAGES, schedule.getSimulatorDirectory());

        if (sendhow == FUNCTION) {
            return function.execute(instanceName, schedule, this);
        }

        boolean allPassed = true;

        if (sendhow != NO_SEND) {
            allPassed = sendRequest(schedule, allPassed, iteration);
        }

        // TODO handle failed sends ie transmitterLog == null ?
        if (chainListRequiresAsync() || asynccheck != null) {

            // do all the async checks if correlation count > 0 this will loop for typically 30s
            allPassed = asyncCheck(schedule, allPassed);

            // This is to close down the SpineToolsSender. If we get here then the Test has all the returns it needs
            // so no point in waiting the remainder of the 30s 
            // if the correlation count > 1 then the thread may well have already terminated.
            if (chainName == null && transmitMode.equals(SPINETOOLS_TRANSMITTER_MODE)) {

                // Wait until SpineToolsSender thread has finished before starting another test
                Thread spineToolsSenderThread = getThreadByName("SpineToolsSender");

                // if its null then the thread has already finished
                if (spineToolsSenderThread != null) {

                    // try to interrupt the SpineToolsSender to close down cleanly
                    spineToolsSenderThread.interrupt();

                    // default to 30s but there should be an entry there anyway
                    // wait until the SpineToolsSender stops or we timeout on the SpineToolsSender configured delay
                    spineToolsSenderThread.join(getAsyncWait(30) * 1000);
                }
            }
        }
        return allPassed;
    }

    /**
     *
     * @param asyncWait default value in seconds
     * @return
     */
    private int getAsyncWait(int asyncWait) {
        try {
            asyncWait = Integer.parseInt(System.getProperty(ASYNCWAIT, "" + asyncWait));
        } catch (NumberFormatException e) {
            Logger.getInstance().log(SEVERE, Test.class.getName(), "Asynchronous wait period not a valid integer - " + e.toString());
        }
        return asyncWait;
    }

    private Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) {
                return t;
            }
        }
        return null;
    }

    /**
     * look ahead to see if any tests in the list of chained tests requires
     * async passfail check
     *
     * @return whether one of tests in the list does as an async pass fail check
     */
    private boolean chainListRequiresAsync() {
        boolean result = false;

        if (chainName == null) {
            Test test = this;
            do {
                if (test.asynccheck != null) {
                    result = true;
                    break;
                }
                test = test.nextTest;
                // stop if there are no more tests in the chain list
            } while (test != null);
        }
        return result;
    }

    /**
     *
     * @param schedule
     * @param allPassed
     * @param iteration if in LOOP 1..n otherwise 0
     * @return whether all tests passed or not
     * @throws Exception
     */
    private boolean sendRequest(Schedule schedule, boolean allPassed, int iteration) throws Exception {

        ToolkitService transmitter = ServiceManager.getInstance().getService(transmitMode + "Transmitter");

        // this is not a chain so we have to send a request
        if (chainName == null) {
            String filename = tosend.instantiate(schedule.getTransmitterDirectory(), toUrl, fromUrl, replyTo, preTransforms, preSubstitutions, profileId, iteration);
//            if (tosend != null) {
//                datasource = tosend.getDatasource();
//                recordid = tosend.getRecordid();
//            }

            if (transmitMode.equals(SPINETOOLS_TRANSMITTER_MODE)) {
                // set the required SpineTools request captor object values
                AutotestSessionCaptor.getInstance().setMessagesFilename(filename);
                AutotestSessionCaptor.getInstance().setMessagesFolder(schedule.getSentMessagesDirectory());
            }

            handleReconfigurables(transmitter);

            ServiceResponse r = transmitter.execute(null);

            transmitterLog = getTransmitterLogFile(schedule, filename);
        }
        if (transmitterLog != null) {
            if (synccheck != null) {
                if (datasource != null) {
                    if (recordid != null) {
                        synccheck.setDatasourceDetails(datasource, recordid);
                    }
                }
                allPassed = syncCheck(schedule, allPassed);
            }

            tidyLogs(schedule);

        } else {
            handleNoTransmitterLog(schedule);
            allPassed = false;
        }
        resetTimestampOffsets(transmitter);
        return allPassed;
    }

    /**
     * configure SpineTools with TKWATM specific handlers for ITK Trunk request
     * and responses
     *
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    private void setupITKTrunkRequestHandlers() throws FileNotFoundException, IllegalArgumentException {
        ConnectionManager cm = ConnectionManager.getInstance();
        ITKTrunkHandler itkTrunkHandler = new uk.nhs.digital.mait.spinetools.spine.messaging.ITKTrunkHandler();
        cm.addHandler(ITK_TRUNK_SERVICE, itkTrunkHandler);
        // the extra quoted name is required for this to work in atm client test mode
        cm.addHandler("\"" + ITK_TRUNK_SERVICE + "\"", itkTrunkHandler);

        // set a handler for saving the following incoming async messaages
        AutotestFileSaveDistributionEnvelopeHandler atdefh = new AutotestFileSaveDistributionEnvelopeHandler();

        for (String service : new String[]{INFACKSERVICE, BIZACKSERVICE, SEND_CDA_V2_SERVICE}) {
            itkTrunkHandler.addHandler(service, atdefh);
        }
    }

    /**
     * calls reconfigure for appropriate attributes on the transmitter and
     * simulator handles specific ReconfigureTags values
     *
     * @param transmitter ToolkitService object to reconfigure
     * @throws Exception
     */
    private void handleReconfigurables(ToolkitService transmitter) throws Exception {
        if (toUrl != null) {
            ((Reconfigurable) transmitter).reconfigure(ReconfigureTags.ADDRESS, toUrl);
        }
        // TX offsets
        if (txTimestampOffset != 0) {
            ((Reconfigurable) transmitter).reconfigure(ReconfigureTags.TIMESTAMP_OFFSET, Integer.toString(txTimestampOffset));
        }
        if (asyncTimestampOffset != 0) {
            ((Reconfigurable) script.getSimulator()).reconfigure(ReconfigureTags.ASYNCHRONOUS_TIMESTAMP_OFFSET, Integer.toString(asyncTimestampOffset));
        }
    }

    /**
     * Clean up.
     *
     * @param schedule
     */
    private void tidyLogs(Schedule schedule) {
        File tsource = new File(schedule.getTransmitterDirectory());
        File[] sources = tsource.listFiles();
        for (File f : sources) {
            f.delete();
        }
    }

    /**
     * reset the reconfigurable offsets to zero if they have been modified
     *
     * @param transmitter
     * @throws Exception
     */
    private void resetTimestampOffsets(ToolkitService transmitter) throws Exception {
        // reconfigure timestamp offsets
        if (txTimestampOffset != 0) {
            ((Reconfigurable) transmitter).reconfigure(ReconfigureTags.TIMESTAMP_OFFSET, "0");
        }
        if (asyncTimestampOffset != 0) {
            ((Reconfigurable) script.getSimulator()).reconfigure(ReconfigureTags.ASYNCHRONOUS_TIMESTAMP_OFFSET, "0");
        }
    }

    /**
     * perform tests on the synchronous response
     *
     * @param schedule
     * @param allPassed
     * @return whether all tests passed or not
     * @throws Exception
     */
    private boolean syncCheck(Schedule schedule, boolean allPassed) throws Exception {
        TestResult t = synccheck.passed(schedule.getScript(), new FileInputStream(transmitterLog), null);
        if (t == TestResult.FAIL) {
            allPassed = false;
        }
        ReportItem ri = new ReportItem(schedule.getName(), testName, t, synccheck.getDescription());
        ri.addDetail(getCommentText());
        ri.setLogFile(transmitterLog.getAbsolutePath());
        schedule.getScript().log(ri);
        return allPassed;
    }

    /**
     * report when there's no transmitter log
     *
     * @param schedule
     * @throws Exception
     */
    private void handleNoTransmitterLog(Schedule schedule) throws Exception {
        ReportItem ri = new ReportItem(schedule.getName(), testName, TestResult.FAIL, "Transmission failed: No transmitter log file found");
        ri.addDetail(getCommentText());
        schedule.getScript().log(ri);
    }

    /**
     *
     * @param schedule
     * @throws Exception
     */
    private void processPropertySets(Schedule schedule) throws Exception {
        ServiceManager smr = ServiceManager.getInstance();
        Properties p = schedule.getScript().getBootProperties();
        boolean updateProps = false;

        if (propertySets != null) {
            for (NamedPropertySet n : propertySets) {
                if (n.isBase()) {
                    n.resetBase(p);
                } else {
                    n.update(p);
                }
            }

            // if the property change relates to a dfferent transport we may have to start new services
            String newTransmitterMode = p.get(TRANSMITTERMODE_PROPERTY).toString();
            // start a required but as yet unstarted Transmitter service here
            if (!smr.getServiceNames().contains(newTransmitterMode + "Transmitter")) {
                ServiceManager.getToolkitSimulator().startService(newTransmitterMode + "Transmitter");
            }

            // start a required but as yet unstarted Transport service 
            if (!smr.getServiceNames().contains(newTransmitterMode + "Transport")) {
                ServiceManager.getToolkitSimulator().startService(newTransmitterMode + "Transport");
            }
            updateProps = true;
        } // if non null property sets

        if (httpHeaderSets != null) {
            setTKWHttpHeaderProperties(p);
            updateProps = true;
        }

        if (updateProps) {
            // Reconfigure reconfigurable services
            for (String n : smr.getServiceNames()) {
                ToolkitService ts = smr.getService(n);
                if (ts instanceof uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable) {
                    ((Reconfigurable) ts).reconfigure(p);
                }
            }
        }
    } // processPropertySets

    private void setTKWHttpHeaderProperties(Properties p) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (HashMap<String, Object> httpHeaderSet : httpHeaderSets) {
            for (String key : httpHeaderSet.keySet()) {
                Object o = httpHeaderSet.get(key);
                if (o instanceof String) {
                    String headerValue = o.toString();
                    // perform Substitution tag substitutions
                    for (String tag : substitutionTags.keySet()) {
                        String value = processParameterObject(substitutionTags.get(tag));
                        headerValue = headerValue.replaceAll(tag, value);
                    }
                    // perform datasource substitutions
                    if (datasource != null) {
                        String id = datasource.getNextId();
                        Iterator<String> iter = datasource.getTags().iterator();
                        while (iter.hasNext()) {
                            String tag = iter.next();
                            try {
                                headerValue = headerValue.replaceAll(tag, datasource.getValue(id, tag));
                            } catch (Exception ex) {
                            }
                        }
                    }
                    // ensure we only remove matching paired surrounding quotes
                    p.put("tks.transmitter.httpheader." + key, headerValue.replaceFirst("^\"(.$)\"$", "$1"));
                } else if (o instanceof Method) {
                    Method method = (Method) o;
                    // This is a slighly premature execution but there's not much in it.
                    String result = (String) method.invoke(null, (Object[]) null);
                    p.put("tks.transmitter.httpheader." + key, result);
                } else if (o instanceof Object[]) {
                    // we use this form if the method takes parameters
                    Object[] oa = (Object[]) o;
                    if (oa.length != 2) {
                        Logger.getInstance().log(SEVERE, Test.class.getName(), "Unexpected length for header set object array " + oa.length);
                    }
                    Method method = (Method) oa[0];
                    Object[] params = (Object[]) oa[1];
                    String result = (String) method.invoke(null, params);
                    p.put("tks.transmitter.httpheader." + key, result);
                }
            }
        }
    }

    /**
     * do the asynchronous checks
     *
     * @param schedule
     * @param allPassed
     * @return allPassed value
     * @throws Exception
     */
    private boolean asyncCheck(Schedule schedule, boolean allPassed) throws Exception {
        // Default if we've not been told which correlator to use.
        //
        if (correlator == null) {
            correlator = new BasicMessageIdCorrelator();
        }
        if (chainName == null) {
            if (sendhow == NO_SEND) {
                asynchronousResponseLog = getClientLogFile(schedule);
            } else {
                asynchronousResponseLog = getAsynchronousResponseLogFile(schedule);
            }
        }

        TestResult testResult = null;
        if (asynchronousResponseLog != null) {
            FileInputStream fis = null;
            if (transmitterLog != null) {
                fis = new FileInputStream(transmitterLog);
            }
            if (asynccheck != null) {
                testResult = asynccheck.passed(schedule.getScript(), new FileInputStream(asynchronousResponseLog), fis);
            } else {
                // handles situation where there's a requirement for an async test somewhere in the chain but no async check
                // defined in this test 
                testResult = TestResult.PASS;
            }
            if (testResult == TestResult.FAIL) {
                allPassed = false;
            }
        } else {
            allPassed = false;
            testResult = TestResult.FAIL;
        }

        if (asynccheck != null) {
            ReportItem ri;
            if (additionalResultText == null) {
                ri = new ReportItem(schedule.getName(), testName, testResult, asynccheck.getDescription());
            } else {
                StringBuilder sb = new StringBuilder();
                if (asynccheck.getDescription() != null) {
                    sb.append(asynccheck.getDescription());
                    sb.append(" : ");
                }
                sb.append(additionalResultText);
                ri = new ReportItem(schedule.getName(), testName, testResult, sb.toString());
            }
            ri.addDetail(getCommentText());
            if (asynchronousResponseLog != null) {
                ri.setLogFile(asynchronousResponseLog.getAbsolutePath());
            }
            schedule.getScript().log(ri);
        } else if (correlationCount > 0 && correlationCountError) { // handles scenario where theres no initial async test but the correlation count check has failed
            ReportItem ri;
            ri = new ReportItem(schedule.getName(), testName + "-correlation-count", testResult, additionalResultText);
            ri.addDetail("Asynchronous response correlation count check");
            schedule.getScript().log(ri);
        }
        return allPassed;
    }

    /**
     * Code specifically handles acks coming from Spine ITK Trunk which appear
     * as .message files. Creates .log files from the incoming async business or
     * infrastructure messages containing the acknowledgement and infrastructure
     * response messages in a form which is usable by the Autotest classes.
     * After processing moves the .message file to the ITKtrunk subfolder
     *
     * @param messagesFolder
     * @throws IOException
     */
    private void handleITKTrunkAcknowledgements(String messagesFolder) throws Exception {
        FilenameFilter filterMessageSuffix = (File directory, String fileName) -> {
            return fileName.endsWith(".message");
        };

        File dir = new File(messagesFolder);
        String[] foundFiles = dir.list(filterMessageSuffix);

        // concatenate the Acknowledgement and http response
        // NB add an extra linefeed so getResponse parses file correctly
        if (foundFiles.length > 0) {
            for (String fileName : foundFiles) {
                StringBuilder sb = new StringBuilder("\r\n\r\n");
                String ackContents = Utils.readFile2String(messagesFolder + "/" + fileName);
                sb.append(ackContents);
                /*
                 extract a timestamp
                 IA format timestamp = "2015-07-02T11:48:41Z"
                 BA format creationTime value="20150701114322"
                 */
                String timestamp = null;
                String messageFileString = null;
                if (fileName.contains(SEND_INFRASTRUCTURE_ACK)) {
                    timestamp = ackContents.replaceFirst(
                            // note the magic sequence at the start (?s) which is the equivalent of applying DOTALL to a pattern
                            // this forces the . character to include linefeeds the default is to exclude them.
                            "(?s)^.*timestamp\\s*=\\s*\"([0-9]{4})-([0-9]{2})-([0-9]{2}).([0-9]{2}):([0-9]{2}):([0-9]{2}).\".*$",
                            "$1$2$3$4$5$6");
                    messageFileString = SEND_INFRASTRUCTURE_ACK;
                } else if (fileName.contains(SEND_BUSINESS_ACK)) {
                    timestamp = ackContents.replaceFirst(
                            // note the magic sequence at the start (?s) which is the equivalent of applying DOTALL to a pattern
                            // this forces the . character to include linefeeds the default is to exclude them.
                            "(?s)^.*creationTime\\s+value\\s*=\\s*\"([0-9]{14})\".*$", "$1");
                    messageFileString = SEND_BUSINESS_ACK;
                } else {
                    Logger.getInstance().log(SEVERE, Test.class.getName(), "Unexpected message file found (" + fileName + ")");
                }

                // TODO should we check this or just default to an automatically generated time?
                if (timestamp != null) {
                    sb.append("\r\n" + END_INBOUND_MARKER + "\r\n");
                    writeConcatenatedContentLogFile(messagesFolder, messageFileString, sb.toString(), timestamp);
                    // move the original file into ITKtrunk subfolder to avoid multiple validation or reprocessing
                    final Path ITK_TRUNK_PATH = FileSystems.getDefault().getPath(messagesFolder + "/ITKtrunk");

                    if (Files.notExists(ITK_TRUNK_PATH)) {
                        Files.createDirectory(ITK_TRUNK_PATH);
                    }
                    Path sourceFile = FileSystems.getDefault().getPath(messagesFolder + "/" + fileName);
                    final Path destPath = FileSystems.getDefault().getPath(ITK_TRUNK_PATH.toString(), fileName);
                    Files.move(sourceFile, destPath, REPLACE_EXISTING);
                }
            }
        } else {
            // System.err.println("No matching files found in " + dir.getCanonicalPath());
        }
    }

    /**
     * write a timestamped message .log file
     *
     * @param messagesFolder
     * @param messageFileName
     * @param logContent
     * @throws IOException
     */
    private void writeConcatenatedContentLogFile(String messagesFolder, String messageFileName, String logContent, String timestamp) throws IOException {
        try {
            if (timestamp == null || timestamp.isEmpty()) {
                timestamp = FILEDATE.format(new Date());
            }
            String fileName = messagesFolder + "/" + messageFileName + "_" + timestamp + ".log";
            File f = new File(fileName);
            try ( FileLocker fl = new FileLocker(f.getAbsolutePath())) {
                Utils.writeString2File(fileName, logContent);
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            // rebrand the exception as an IOException
            throw new IOException("File lock/unlock failed " + ex.getMessage());
        }
    }

    /**
     * sets a flag if the correlation count fails. waits the full term even for
     * correlation count == 1 in case there are extra unexpected responses
     *
     * @param s schedule
     * @return file containing asynchronous response(s)
     * @throws Exception
     */
    private File getAsynchronousResponseLogFile(Schedule s)
            throws Exception {
        // Correlate simulator saved messages by "RelatesTo"
        // with the current message id.
        //
        ArrayList<File> matches = null;
        if (correlationCount > 0) {
            matches = new ArrayList<>();
        }
        int retryCount = Integer.parseInt(s.getScript().getProperty("tks.autotest.asynchronous.log.retries"));
        int retryDelay = Integer.parseInt(s.getScript().getProperty("tks.autotest.asynchronous.log.delay"));
        for (; retryCount != 0; --retryCount) {
            File f = new File(s.getSimulatorDirectory());

            // this ensures any .message files are transformed to .log
            // and the original message file is moved to the ITK Trunk subfolder
            if (transmitMode.equals(SPINETOOLS_TRANSMITTER_MODE)) {
                handleITKTrunkAcknowledgements(s.getSimulatorDirectory());
            }

            File[] list = f.listFiles();
            for (File x : list) {
                // Mod for ITK Trunk ignore folders
                if (!x.isDirectory() && !x.getName().startsWith("MULTIPLE_") && !FileLocker.hasLock(x.getAbsolutePath())) {
                    if (correlator.correlate(x, tosend)) {
                        if (matches != null && !matches.contains(x)) {
                            matches.add(x);
                        }
                    }
                }
            }
            try {
                System.out.println("Waiting " + retryDelay + " ms for Async response log file, retries left :" + retryCount);
                synchronized (this) {
                    wait(retryDelay);
                }
            } catch (InterruptedException e) {
            }
        }
        // Check matches...
        if (matches == null) {
            return null;
        } else if (matches.size() == correlationCount) {
            return concatenateMultipleResponses(matches);
        } else {
            correlationCountError = true;
            StringBuilder sb = new StringBuilder("Expected to get ");
            sb.append(correlationCount);
            sb.append(" asynchronous responses, but actually got ");
            sb.append(matches.size());
            additionalResultText = sb.toString();
            return null;
        }
    }

    /**
     * Handles client mode tests loops awaiting suts to send messages into
     * TKWATM exits when there is a new log file in the folder
     *
     * @param schedule
     * @return a file object containing the log
     * @throws Exception
     */
    private File getClientLogFile(Schedule schedule)
            throws Exception {
        // To receive unsolicited messages in autotest mode and validate them 
        //

        // take a snapshot of the files in the simulator folder to compare against
        // in order to pick out the latest when there are multiple files present
        File f = new File(schedule.getSimulatorDirectory());
        File[] previousList = f.listFiles();
        ArrayList<File> delta = new ArrayList<>();

        System.out.println("*************************************************************");
        System.out.println("***********************ACTION REQUIRED***********************");
        System.out.println("*************************************************************");
        System.out.println(promptName);

        // for tlsma testing to communicate folder to write log to for SSLSocketListener
        if (schedule.getName().matches("^111_SO_MA[123]$")) {
            System.setProperty(ORG_SSL_SOCKET_LISTENER_SAVED_MESSAGES_FOLDER_PROPERTY, schedule.getSimulatorDirectory());
        } else {
            System.getProperties().remove(ORG_SSL_SOCKET_LISTENER_SAVED_MESSAGES_FOLDER_PROPERTY);
        }

        final int DELAY = 100; //ms
        final boolean KEEP_GOING = true;
        boolean listening = true;
        while (KEEP_GOING) {
            while (listening) {
                File[] list = f.listFiles();
                if (previousList.length != list.length) {
                    for (File x : list) {
                        boolean found = false;
                        for (File y : previousList) {
                            if (x.getName().equals(y.getName())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            delta.add(x);
                        }
                    }
                    listening = false;
                }
                // don't hog the cpu even if there is more than one 
                Thread.sleep(DELAY);
            }
            for (File x : delta) {
                if (x.getName().endsWith(TRANSMIT_MODE_SUFFIXES.get(transmitMode))) {
                    if (!FileLocker.hasLock(x.getAbsolutePath())) {
                        return x;
                    } else {
                        listening = true;
                    }
                }
            }
            listening = true;
        }
    }

    /**
     * Make a single file...
     *
     * @param files
     * @return the single MULTIPLE file
     * @throws Exception
     */
    private File concatenateMultipleResponses(ArrayList<File> files)
            throws Exception {

        //System.err.println("Concatenating:");
        //files.stream().forEach((f) -> {
        //    System.err.println("\t" + f.getAbsolutePath());
        //});
        StringBuilder sb = new StringBuilder("MULTIPLE_");
        TreeMap<String, File> sortedFileMap = new TreeMap();
        // NB Need this to be sorted in ascending timestamp order
        files.stream().forEach((f) -> {
            String timestamp = f.getName().replaceFirst("^.*_([0-9]+).*$", "$1");
            sortedFileMap.put(timestamp, f);
        });

        File f = sortedFileMap.get(sortedFileMap.firstKey());
        sb.append(f.getName());
        File output = new File(f.getParent(), sb.toString());
        try ( FileOutputStream fos = new FileOutputStream(output)) {
            for (File x : sortedFileMap.values()) {
                byte[] b;
                try ( FileInputStream fis = new FileInputStream(x)) {
                    b = new byte[(int) x.length()];
                    fis.read(b);
                }
                fos.write((LogMarkers.RESPONSE_FILE_HEADER_MARKER + " ").getBytes());
                fos.write(x.getName().getBytes());
                fos.flush();
                fos.write(b);
                fos.flush();
                fos.write("\n\n".getBytes());
                fos.flush();
            }
        }
        return output;
    }

    /**
     * return a file object representing the transmitter log file
     *
     * @param s
     * @param source
     * @return
     * @throws Exception
     */
    private File getTransmitterLogFile(Schedule s, String source)
            throws Exception {
        // Need a configurable delay in here to prevent the transmitter log check
        // failing because it looks before the sender has had a chance to write the file

        int retryCount = Integer.parseInt(s.getScript().getProperty("tks.autotest.synchronous.log.retries"));
        int retryDelay = Integer.parseInt(s.getScript().getProperty("tks.autotest.synchronous.log.delay"));
        for (; retryCount != 0; --retryCount) {
            File f = new File(s.getSentMessagesDirectory());
            File[] list = f.listFiles();
            for (File x : list) {
                // updated to check that the file ends with .log as UNIX systems were sometimes confusing it with the .signature file
                if (x.getName().startsWith(source) && x.getName().endsWith(".log")) {
                    if (!FileLocker.hasLock(x.getAbsolutePath())) {
                        return x;
                    }
                }
            }
            try {
                System.out.println("Waiting " + retryDelay + " ms for Transmitter log file retries, left :" + retryCount);
                synchronized (this) {
                    wait(retryDelay);
                }
            } catch (InterruptedException e) {
            }
        }
        return null;
    }

    /**
     * recursive call to return the test object that starts the chain
     *
     * @param test
     * @return the originating test object
     * @throws Exception
     */
    private Test getChain(Test test)
            throws Exception {
        if (test == null) {
            throw new Exception("Failure in test Chain");
        }
        if (test.chainName == null) {
            return test;
        } else {
            return test.getChain(test.previousTest);
        }
    }

    /**
     * @return the chainType
     */
    public String getChainType() {
        return chainType;
    }

    /**
     * @return the chainName
     */
    public String getChainName() {
        return chainName;
    }

    /**
     * @return the commentText with tdv tag values substituted where appropriate
     */
    private String getCommentText() throws Exception {
        String s = commentText;
        if (datasource != null && recordid != null && s != null && s.contains("__")) {
            for (String tag : datasource.getTags()) {
                s = s.replaceAll(tag, datasource.getValue(recordid, tag));
            }
        }
        return s;
    }

    static { // valid suffixes for Http and SpineTools messaging
        TRANSMIT_MODE_SUFFIXES.put("SpineTools", ".message");
        TRANSMIT_MODE_SUFFIXES.put("Http", ".log");
    }
}
