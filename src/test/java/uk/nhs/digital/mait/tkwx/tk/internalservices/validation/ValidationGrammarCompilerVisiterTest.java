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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.antlr.v4.runtime.BaseErrorListener;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners.ErrorCountingErrorListener;

/**
 *
 * @author simonfarrow
 */
public class ValidationGrammarCompilerVisiterTest {

    private static Properties bootProperties;

    private ValidationGrammarCompilerVisiter instance;
    private ErrorCountingErrorListener countingErrorListener;

    public ValidationGrammarCompilerVisiterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        bootProperties = new Properties();

        bootProperties.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        bootProperties.load(new FileReader(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"));

        // add the extra checks from other domains
        bootProperties.put("tks.validator.check.hl7_xslt", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XsltValidator");
        bootProperties.put("tks.validator.check.ebxml_xslt", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XsltValidator");

        bootProperties.put("tks.validator.check.hl7_xpathequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.hl7_xpathnotequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.ebxml_xpathequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.ebxml_xpathnotequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.soap_xpathequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.soap_xpathnotequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");

        bootProperties.put("tks.validator.check.hl7_xpathexists", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.hl7_xpathnotexists", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.ebxml_xpathexists", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.ebxml_xpathnotexists", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.soap_xpathexists", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.soap_xpathnotexists", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");

        bootProperties.put("tks.validator.check.hl7_xpathmatches", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");
        bootProperties.put("tks.validator.check.hl7_xpathnotmatches", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.XpathAssertionValidator");

        // add the text assertion class refs
        bootProperties.put("tks.validator.check.matches", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");
        bootProperties.put("tks.validator.check.notmatches", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");
        bootProperties.put("tks.validator.check.equals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");
        bootProperties.put("tks.validator.check.notequals", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");
        bootProperties.put("tks.validator.check.contains", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");
        //bootProperties.put("tks.validator.check.notcontains", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");

        // add an unchecked test
        bootProperties.put("tks.validator.check.notcontains", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator");

        // add the hapifhirvalidatortest
        bootProperties.put("tks.validator.check.hapifhirvalidator", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir.HapiFhirValidator");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        instance = new ValidationGrammarCompilerVisiter(bootProperties);
        instance.setCustomErrorListener(countingErrorListener = new ErrorCountingErrorListener());

        //testParse("GP_CONNECT");
    }

    @After
    public void tearDown() {
    }

    /**
     *
     * @throws IOException
     */
    @Test
    public void testParseTestfile() throws IOException {
        System.out.println("parseTestFile");
        String fileName = "src/test/resources/testvalidation.conf";
        instance.parse(fileName);
    }

    /**
     *
     * @param domain
     * @throws IOException
     */
    private void testParse(String domain) throws IOException {
        System.out.println("parse" + domain + "ValidatorFile");
        String fileName = System.getenv("TKWROOT") + "/config/" + domain + "/validator_config/validator.conf";
        instance.parse(fileName);
    }

    /**
     * Test of parse method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseValidatorFiles() throws Exception {
        System.out.println("parseValidatorFiles");

        Path[] paths = new Path[]{
            FileSystems.getDefault().getPath(System.getenv("TKWROOT") + "/config/"),
            FileSystems.getDefault().getPath(System.getenv("TKWROOT") + "/contrib/Common/include/")
        };

        final List<String> VALID_TKWX_DOMAINS = Arrays.asList(new String[]{
            "ITK_Autotest",
            "FHIR_MESH",
            "SPINE_MTH",
            "SPINE_ITKTrunk_Client",
            "HTTP_INTERCEPTOR",
            "GP_CONNECT",
            "FHIR_111_UEC"});

        for (Path path : paths) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    String fileName = file.toString();
                    if ((fileName.contains("validator_config") || fileName.contains("include"))
                            && fileName.endsWith(".conf")) {
                        String domainName = fileName.replaceFirst("^(.*/config/)([^/]+)(/.*)$", "$2");
                        if (VALID_TKWX_DOMAINS.contains(domainName)) {
                            System.out.println("Parsing " + fileName);
                            instance.parse(fileName);
                            assertEquals(0, countingErrorListener.getErrorCount());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Test of parse method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        testParse("GP_CONNECT");
    }

    /**
     * Test of parse method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParseITK_Dashboards() throws Exception {
        System.out.println("parseITK_Dashboards");
        testParse("ITK_Dashboards");
    }

    /**
     * Test of getMetadata method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetMetadata() throws IOException {
        System.out.println("getMetadata");
        testParse("GP_CONNECT");
        int expResult = 1;
        ArrayList<RulesetMetadata> result = instance.getMetadata();
        assertEquals(expResult, result.size());
        Iterator<RulesetMetadata> iter = result.iterator();
        while (iter.hasNext()) {
            RulesetMetadata r = iter.next();
            System.out.println(r.getName());
        }
    }

    /**
     * Test of getSubroutine method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetSubroutine() throws IOException {
        System.out.println("getSubroutine");
        testParse("GP_CONNECT");
        String r = "check_bundle";
        String expResult = r;
        ValidationSet result = instance.getSubroutine(r);
        assertEquals(expResult, result.getServiceName());
        // TODO have added a package protected accessor for debug testing
        ArrayList<Validation> validations = result.getValidations();
        for (Validation validation : validations) {
            validation.walk();
        }
    }

    /**
     * Test of clear method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testClear() throws IOException {
        System.out.println("clear");
        testParse("GP_CONNECT");
        instance.clear();
    }

    /**
     * Test of getValidationSet method, of class
     * ValidationGrammarCompilerVisiter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValidationSet() throws Exception {
        System.out.println("getValidationSet");
        testParse("GP_CONNECT");
        String s = "urn:nhs:names:services:gpconnect:fhir:operation:gpc.getcarerecord";
        String expResult = s;
        ValidationSet result = instance.getValidationSet(s);
        assertEquals(expResult, result.getServiceName());
    }

    /**
     * Test of visitInclude_statement method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitInclude_statement() {
    }

    /**
     * Test of visitValidation_header method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitValidation_header() {
    }

    /**
     * Test of visitValidate_statement method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitValidate_statement() {
    }

    /**
     * Test of visitSubset_statement method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitSubset_statement() {
    }

    /**
     * Test of visitAnnotation_directive method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitAnnotation_directive() {
    }

    /**
     * Test of visitIf_directive method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitIf_directive() {
    }

    /**
     * Test of visitThen_clause method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitThen_clause() {
    }

    /**
     * Test of visitElse_clause method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitElse_clause() {
    }

    /**
     * Test of visitEndif method, of class ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitEndif() {
    }

    /**
     * Test of visitSet_directive method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitSet_directive() {
    }

    /**
     * Test of visitCheck_directive method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testVisitCheck_directive() {
    }

    /**
     * Test of getValidationSets method, of class
     * ValidationGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetValidationSets() throws IOException {
        System.out.println("getValidationSets");
        String fileName = "src/test/resources/testvalidation.conf";
        instance.parse(fileName);

        int expResult = 1;
        HashMap<String, ValidationSet> result = instance.getValidationSets();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getSubroutines method, of class ValidationGrammarCompilerVisiter.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetSubroutines() throws IOException {
        System.out.println("getSubroutines");
        String fileName = "src/test/resources/testvalidation.conf";
        instance.parse(fileName);

        int expResult = 1;
        HashMap<String, ValidationSet> result = instance.getSubroutines();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of setCustomErrorListeners method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testSetCustomErrorListeners() {
        System.out.println("setCustomErrorListeners");
        BaseErrorListener[] customErrorListeners = new BaseErrorListener[]{countingErrorListener};
        instance.setCustomErrorListeners(customErrorListeners);
    }

    /**
     * Test of setCustomErrorListener method, of class
     * ValidationGrammarCompilerVisiter.
     */
    @Test
    public void testSetCustomErrorListener() {
        System.out.println("setCustomErrorListener");
        BaseErrorListener errorListener = countingErrorListener;
        instance.setCustomErrorListener(errorListener);
    }
}
