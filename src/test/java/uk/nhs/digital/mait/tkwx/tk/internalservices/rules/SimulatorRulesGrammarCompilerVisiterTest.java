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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners.ErrorCountingErrorListener;
import uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners.VerboseErrorListener;

/**
 *
 * @author simonfarrow
 */
public class SimulatorRulesGrammarCompilerVisiterTest {
    
    @org.junit.Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();


    private SimulatorRulesGrammarCompilerVisiter instance;
    private ErrorCountingErrorListener countingErrorListener;

    private static final String TEST_SIMULATOR_CONFIG = "src/test/resources/test_tks_rule_config.txt";

    public SimulatorRulesGrammarCompilerVisiterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // required for the local FHIR_REST example which depends on properties set to the appropriate files
        // These are async responders for ITK FHIR interactions
        String simFolder = System.getenv("TKWROOT") + "/config/FHIR_REST/simulator_config/fhir_routing_actor_templates/";
        System.setProperty("tks.routingactor.fhir.appacktemplate", simFolder + "ITK-InfAck-Success-Example-1.xml");
        System.setProperty("tks.routingactor.fhir.busacktemplate", simFolder + "ITK-BusinessAck-Success-Example-1.xml");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new SimulatorRulesGrammarCompilerVisiter();
        instance.setCustomErrorListener(countingErrorListener = new ErrorCountingErrorListener());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of visitResponse method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testVisitResponse() {
    }

    /**
     * Test of visitSubstitution method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testVisitSubstitution() {
    }

    /**
     * Test of visitExpression method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testVisitExpression() {
    }

    /**
     * Test of visitIf_statement method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testVisitIf_statement() {
    }

    /**
     * Test of visitInclude_statement method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testVisitInclude_statement() {
        System.out.println("visitInclude_statement");
    }

    /**
     * Test of visitSimrule_block method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testVisitSimrule_block() {
    }

    /**
     * Test of parsing all rules files.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testParseRulesFiles() throws IOException {
        System.out.println("ParseRulesFiles");

        VerboseErrorListener verboseErrorListener = new VerboseErrorListener();
        instance.setCustomErrorListeners(new BaseErrorListener[]{countingErrorListener, verboseErrorListener});

        // run the parser on all the rules files under %TKWROOT%/config
        Path simulatorFiles = FileSystems.getDefault().getPath(System.getenv("TKWROOT") + "/config/");

        HashMap<String, Integer> hm = new HashMap<>();

        final List<String> VALID_TKWX_DOMAINS = Arrays.asList(new String[]{
            "ITK_Autotest", 
            "FHIR_MESH", 
            "SPINE_MTH", 
            "SPINE_ITKTrunk_Client", 
            "HTTP_INTERCEPTOR", 
            "GP_CONNECT", 
            "FHIR_111_UEC"});

        Files.walkFileTree(simulatorFiles, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                String fileName = file.toString();
                if (fileName.contains("simulator_config") && fileName.toLowerCase().contains("rule") && fileName.endsWith(".txt")) {
                    String domainName = fileName.replaceFirst("^(.*/config/)([^/]+)(/.*)$","$2");
                    if (VALID_TKWX_DOMAINS.contains(domainName)) {
                        System.out.println("Parsing " + fileName);
                        instance.parse(fileName);
                        if (countingErrorListener.getErrorCount() > 0) {
                            hm.put(fileName, countingErrorListener.getErrorCount());
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        if (!hm.isEmpty()) {
            for (String key : hm.keySet()) {
                System.out.println(key + " : " + hm.get(key));
            }
            fail("Parsing errors detected in " + hm.keySet().size() + " files");
        }
    }

    /**
     * Test of parsing in depth of a single rules file.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testParseTestFile() throws IOException {
        System.out.println("ParseTestFile");

        instance.parse(TEST_SIMULATOR_CONFIG);

        HashMap<String, Expression> expressions = instance.getExpressions();
        assertNotNull(expressions);
        assertTrue(expressions.size() > 0);

        HashMap<String, Response> responses = instance.getResponses();
        assertNotNull(responses);
        assertTrue(responses.size() > 0);

        HashMap<String, RuleSet> rules = instance.getRules();
        assertNotNull(rules);
        assertTrue(rules.size() > 0);

        HashMap<String, Substitution> substitutions = instance.getSubstitutions();
        assertNotNull(substitutions);
        assertTrue(substitutions.size() > 0);
    }

    /**
     * Test of parse method, of class SimulatorRulesGrammarCompilerVisiter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        String fileName = TEST_SIMULATOR_CONFIG;
        instance.parse(fileName);
    }

    /**
     * Test of getResponses method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetResponses() throws IOException {
        System.out.println("getResponses");
        instance.parse(TEST_SIMULATOR_CONFIG);

        int expResult = 5;
        HashMap<String, Response> result = instance.getResponses();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getExpressions method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetExpressions() throws IOException {
        System.out.println("getExpressions");
        instance.parse(TEST_SIMULATOR_CONFIG);

        int expResult = 39;
        HashMap<String, Expression> result = instance.getExpressions();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getSubstitutions method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetSubstitutions() throws IOException {
        System.out.println("getSubstitutions");
        instance.parse(TEST_SIMULATOR_CONFIG);

        int expResult = 16;
        HashMap<String, Substitution> result = instance.getSubstitutions();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getRules method, of class SimulatorRulesGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetRules() throws IOException {
        System.out.println("getRules");
        instance.parse(TEST_SIMULATOR_CONFIG);

        int expResult = 1;
        HashMap<String, RuleSet> result = instance.getRules();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of setCustomErrorListeners method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testSetCustomErrorListeners() {
        System.out.println("setCustomErrorListeners");
        BaseErrorListener[] customErrorListeners = new BaseErrorListener[]{countingErrorListener};
        instance.setCustomErrorListeners(customErrorListeners);
    }

    /**
     * Test of setCustomErrorListener method, of class
     * SimulatorRulesGrammarCompilerVisiter.
     */
    @Test
    public void testSetCustomErrorListener() {
        System.out.println("setCustomErrorListener");
        BaseErrorListener errorListener = countingErrorListener;
        instance.setCustomErrorListener(errorListener);
    }

}
