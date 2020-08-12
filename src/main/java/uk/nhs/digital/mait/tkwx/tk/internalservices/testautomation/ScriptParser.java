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
import java.util.HashMap;
import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import org.antlr.v4.runtime.tree.ParseTree;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.HttpHeaderCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.HttpStatusCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.NullCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.XPathCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassFailCheck;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 *
 * @author SIFA2
 */
public class ScriptParser {

    private Script script = null;
    private Properties bootProperties = null;
    private AutotestGrammarCompilerVisitor visitor;

    /**
     * public constructor
     *
     * @param p
     * @throws Exception
     */
    public ScriptParser(Properties p)
            throws Exception {
        script = new Script();
        bootProperties = new Properties();
        for (String s : p.stringPropertyNames()) {
            bootProperties.setProperty(s, p.getProperty(s));
        }
        script.setProperties(bootProperties);
    }

    /**
     * @return bootProperties
     */
    Properties getBootProperties() {
        return bootProperties;
    }

    /**
     *
     * @param fileName
     * @return parsed script object
     * @throws java.lang.Exception
     */
    public Script parse(String fileName) throws Exception {
        try {
            AutotestParser parser = AbstractAutotestParser.getAutotestParser(fileName);

            ParseTree pt = parser.input();
            visitor = new AutotestGrammarCompilerVisitor(script, bootProperties);
            Object x = visitor.visit(pt);

            link();

        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, ScriptParser.class.getName(), "IO Error " + ex.getMessage() + " reading test script file " + fileName);
        }
        return script;
    }

    /**
     * @param name
     * @return the DataSource
     */
    public DataSource getDataSource(String name) {
        return visitor.getDatasources().get(name);
    }

    /**
     * @param name
     * @return the test
     */
    public Test getTest(String name) {
        return visitor.getTests().get(name);
    }

    /**
     * @param name
     * @return the passfailcheck
     */
    public PassFailCheck getPassFailCheck(String name) {
        return visitor.getPassfailchecks().get(name);
    }

    /**
     * @param name
     * @return the message
     */
    public Message getMessage(String name) {
        return visitor.getMessages().get(name);
    }

    /**
     * @param name
     * @return the extractor
     */
    public ResponseExtractor getExtractor(String name) {
        return visitor.getExtractors().get(name);
    }

    /**
     * @param name
     * @return the template
     */
    public Template getTemplate(String name) {
        return visitor.getTemplates().get(name);
    }

    /**
     * @param name property set name
     * @return the propertySets
     */
    public NamedPropertySet getPropertySet(String name) {
        return visitor.getPropertySets().get(name);
    }

    /**
     * @param name httpHeaderSet name
     * @return the httpHeaderSet
     */
    public HashMap<String, Object> getHttpHeaderSet(String name) {
        return visitor.getHttpHeaderSets().get(name);
    }

    /**
     * @param name substition tag name
     * @return the Object containing the substitution tag value
     */
    public HashMap<String, Object> getSubstitutionTags() {
        return visitor.getSubstitutionTags();
    }

    private void link()
            throws Exception {

        HashMap<String, String> messageDatasourceMap = new HashMap<>();

        // For each of the linkables, call link()
        for (Message message : visitor.getMessages().values()) {
            if (message.getDatasourceName() != null) {
                // build an message map associating message with 0..1 datasource
                // These are static relationships
                messageDatasourceMap.put(message.getName(), message.getDatasourceName());
            }
            message.link(this);
        }

        HashMap<String, String> passFailCheckExtractorMap = new HashMap<>();
        for (PassFailCheck passFailCheck : visitor.getPassfailchecks().values()) {
            passFailCheck.link(this);
            if (passFailCheck.getExtractorName() != null) {
                // build an extractor map associating passfailcheck with 0..1 extractor
                // These are static relationships
                passFailCheckExtractorMap.put(passFailCheck.getName(), passFailCheck.getExtractorName());
            }
        }

        // Prelink all the test templates prior to copying
        // chain tests may need to be relinked after copying from the template into the scheduleElement.
        for (Test test : visitor.getTests().values()) {
            test.link(this);
        }

        // used for tracking associations between Dataource and Extractor
        HashMap<String, String> dataSourceExtractorMap = new HashMap<>();
        // associate data sources with extractors if there are any
        for (DataSource datasource : visitor.getDatasources().values()) {
            setDatasoureExtractor(datasource, messageDatasourceMap, passFailCheckExtractorMap, dataSourceExtractorMap);

            // this registers data source with it's extractor using the extractorName if there is one
            datasource.link(this);
        }

        // Dump the static mapping. This erroneously depends on the naming of tests
//        for ( String key :dataSourceExtractorMap.keySet()){
//            System.out.println(key+":"+dataSourceExtractorMap.get(key));
//        }
        for (Schedule schedule : visitor.getSchedules()) {
            schedule.link(this);
            script.addSchedule(schedule);
            // ensure tests are chained by sequence in schedule rather than by declaration order in tstp file
            // this method also relinks chained tests
            schedule.chainTests(this);
        }
    }

    /**
     * This is experimental code. Use of extractors is recommended with caution.
     * For the Datasource lookup a related test (one with a message containing
     * this Datasource), get the PassFailCheck then lookup the Extractor name.
     * We can only have one extractor associated with a datasource. If we get an
     * attempt to reassign a different extractor to a datasource warn the user
     * This code assumes that the associated Message is the last one specified
     * in the declaration sequence. This is actually alphabetic after the merge.
     * This way of working cannot be relied upon now tests can be reused.
     * Further this is not a static relationship. The same test might be
     * associated with different Datasources since it can be reused. At the end
     * of the day if the tests are named assuming evaluation in alpha prder this
     * works. This is a workaround. Datasource references (updated by ) 0..1
     * Extractors Extractor can have 0..* subscribed (updates) Datasources A
     * Test -- 0..1 --> Message -- 0..1 -> Datasource
     *
     * Depending on which chain a Test is it may be associated with a different
     * Datasource Meaning an Extractor may be related to different DataSources
     *
     * @param datasource the Datasource
     * @param messageDatasourceMap input map associates a message with a
     * datasource if there is one
     * @param passFailCheckExtractorMap input map associates a pfc with an
     * extractor if there is one
     * @param dataSourceExtractorMap output map populated with the extractor
     * associated with a Datasource
     */
    private void setDatasoureExtractor(DataSource datasource, HashMap<String, String> messageDatasourceMap, HashMap<String, String> passFailCheckExtractorMap, HashMap<String, String> dataSourceExtractorMap) {
        String msgName = null;
        for (Test test : visitor.getTests().values()) {
            if (messageDatasourceMap.get(test.getMsgname()) != null) {
                // not all tests have a message associated chained tests dont so remember what the last message was
                msgName = test.getMsgname();
            }
            if (msgName != null) {
                //System.out.println("Test " + test.getName() + " assuming message " + msgName);
                String dataSourceName = messageDatasourceMap.get(msgName);
                if (dataSourceName.equals(datasource.getName())) {
                    String pfcName = test.getSynchronousPassFailCheckName() != null ? test.getSynchronousPassFailCheckName() : test.getChainName();
                    String extractorName = passFailCheckExtractorMap.get(pfcName);
                    if (extractorName != null) {
                        if (dataSourceExtractorMap.get(dataSourceName) == null) {
                            dataSourceExtractorMap.put(dataSourceName, extractorName);
                            datasource.setExtractorName(extractorName);
                            //System.out.println("Setting extractor " + extractorName + " in Datasource " + datasource.getName());
                            // dont break because we need to report subsequent attempts to overwrite
                        } else if (!dataSourceExtractorMap.get(dataSourceName).equals(extractorName)) {
                            Logger.getInstance().log(WARNING, ScriptParser.class.getName(),
                                    "Test:" + test.getName()
                                    + " - attempt to reassign a different extractor: " + extractorName
                                    + " to datasource: " + dataSourceName
                                    + " with an existing extractor: " + dataSourceExtractorMap.get(dataSourceName));
                        }
                    } // non null extractor name
                } // if data source match
            } // non null msgName
        } // for test
    }

    /**
     * This method in only called by AbstractLogicalOperatorPassFailCheck Its a
     * near duplicate of the one in AutotestManagerCompilerVisitor
     *
     * @param passfailCheckCtx
     * @return initialised PassFailCheck object
     * @throws Exception
     */
    public PassFailCheck makePassFail(PassFailCheckContext passfailCheckCtx) throws Exception {
        XPathCheckContext xPathCheckCtx = passfailCheckCtx.xPathCheck();
        HttpHeaderCheckContext httpHeaderCheckCtx = passfailCheckCtx.httpHeaderCheck();
        HttpStatusCheckContext httpStatusCheckCtx = passfailCheckCtx.httpStatusCheck();

        String checkType;
        if (xPathCheckCtx != null) {
            checkType = xPathCheckCtx.xpathType().getText();
        } else if (httpHeaderCheckCtx != null) {
            checkType = httpHeaderCheckCtx.HTTPHEADERCHECK().getText();
        } else if (httpStatusCheckCtx != null) {
            checkType = httpStatusCheckCtx.HTTPSTATUSCHECK().getText();
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
        pf.init(passfailCheckCtx);
        return pf;
    }
}
