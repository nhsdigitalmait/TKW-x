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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.HttpHeaderCorrelationCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.XpathCorrelationCheckContext;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AutotestGrammarCompilerVisitor.instantiatePassFailCheck;

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
    public DataSource getDataSource(String name) throws Exception {
        DataSource ds = visitor.getDatasources().get(name);
        if (ds != null) {
            return visitor.getDatasources().get(name);
        } else {
            throw new Exception("ScriptParser.getDataSource datasource does not exist " + name);
        }
    }

    /**
     * @param name
     * @return the test
     */
    public Test getTest(String name) throws Exception {
        Test test = visitor.getTests().get(name);
        if (test != null) {
            return test;
        } else {
            throw new Exception("ScriptParser.getTest test does not exist " + name);
        }
    }

    /**
     * @param name
     * @return the passfailcheck
     */
    public PassFailCheck getPassFailCheck(String name) throws Exception {
        PassFailCheck pfc = visitor.getPassfailchecks().get(name);
        if (pfc != null) {
            return pfc;
        } else {
            throw new Exception("ScriptParser.getPassFailCheck passfailcheck does not exist " + name);
        }
    }

    /**
     * @param name
     * @return the message
     */
    public Message getMessage(String name) throws Exception {
        Message message = visitor.getMessages().get(name);
        if (message != null) {
            return message;
        } else {
            throw new Exception("ScriptParser.getMessage message does not exist " + name);
        }
    }

    /**
     * @param name
     * @return the extractor
     */
    public ResponseExtractor getExtractor(String name) throws Exception {
        ResponseExtractor extractor = visitor.getExtractors().get(name);
        if (extractor != null) {
            return extractor;
        } else {
            throw new Exception("ScriptParser.getExtractor extractor does not exist " + name);
        }
    }

    /**
     * @param name
     * @return the template
     */
    public Template getTemplate(String name) throws Exception {
        Template template = visitor.getTemplates().get(name);
        if (template != null) {
            return template;
        } else {
            throw new Exception("ScriptParser.getTemplate template does not exist " + name);
        }
    }

    /**
     * @param name property set name
     * @return the propertySets
     */
    public NamedPropertySet getPropertySet(String name) throws Exception {
        NamedPropertySet ps = visitor.getPropertySets().get(name);
        if ( ps != null ) {
            return ps;
        } else {
            throw new Exception("ScriptParser.getPropertySet property set does not exist " + name);
        }
    }

    /**
     * @param name httpHeaderSet name
     * @return the httpHeaderSet
     */
    public HashMap<String, Object> getHttpHeaderSet(String name) throws Exception {
        HashMap<String, Object> httpHeaderSet = visitor.getHttpHeaderSets().get(name);
        if (httpHeaderSet != null) {
            return httpHeaderSet;
        } else {
            throw new Exception("ScriptParser.getHttpHeaderSet http header set does not exist " + name);
        }
    }

    /**
     * @param name substitution tag name
     * @return the Object containing the substitution tag value
     */
    public HashMap<String, Object> getSubstitutionTags() throws Exception {
        HashMap<String, Object> substitutionTags = visitor.getSubstitutionTags();
        if ( substitutionTags != null ) {
            return substitutionTags;
        } else {
            throw new Exception("ScriptParser.getSubstitutionTags substitution tags does not exist ");
        }
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
     * Use of extractors is recommended with caution.
     *
     * For the Datasource lookup a related test (one with a message containing
     * this Datasource), get the PassFailCheck then lookup the Extractor name.
     *
     * We can only have one extractor associated with a datasource. If we get an
     * attempt to reassign a different extractor to a datasource warn the user
     * This code assumes that the associated Message is the first one specified
     * in the declaration sequence of schedules.
     *
     * Further this is not a static relationship. The same test might be
     * associated with different Datasources since it can be reused.
     *
     * The bottom line is that tests should not be reused if any of the usages
     * refer to the different messages.
     *
     * Datasource references (updated by ) 0..1 Extractors Extractor can have
     * 0..* subscribed (updates) Datasources A Test -- 0..1 --> Message -- 0..1
     * -> Datasource
     *
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
            String rootTestName = visitor.getRootTest(test.getName());
            // use the first test in the schedule which should have a message
            Test rootTest = visitor.getTests().get(rootTestName);
            if (rootTest != null && messageDatasourceMap.get(rootTest.getMsgname()) != null) {
                msgName = rootTest.getMsgname();
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
            }
        } // for test
    }

    /**
     * This method is only called by AbstractLogicalOperatorPassFailCheck 
     * for instantiating subtests used with logical operators
     * @param passfailCheckCtx
     * @return initialised PassFailCheck object
     * @throws Exception
     */
    public PassFailCheck makePassFail(PassFailCheckContext passfailCheckCtx) throws Exception {
        PassFailCheck pf = instantiatePassFailCheck(passfailCheckCtx, bootProperties);
        pf.init(passfailCheckCtx);
        return pf;
    }
}
