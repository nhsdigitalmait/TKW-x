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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceInterface;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.AbstractValidatorService.ValidationResults;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.RulesetMetadata;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorFactory;

/**
 *
 * @author simonfarrow
 */
public class AbstractValidatorServiceTest {

    private AbstractValidatorServiceImpl instance;

    public AbstractValidatorServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException, Exception {
        instance = new AbstractValidatorServiceImpl();
        instance.bootProperties = new Properties();

        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        instance.bootProperties.load(new FileInputStream(propertiesFile));
        instance.bootProperties.put("tks.validator.check.cdaconformancexslt", "uk.nhs.digital.mait.tkwx.tk.internalservices.validation.CDAConformanceXsltValidator");
        ToolkitSimulator tks = new ToolkitSimulator(propertiesFile);
        instance.toolkitSimulator = tks;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setValidationSet method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetValidationSet() throws Exception {
        System.out.println("setValidationSet");
        String service = "";
        String validationMessage = "";
        ValidatorFactory validationFactory = ValidatorFactory.getInstance();
        List<RulesetMetadata> metadata = new ArrayList<>();
        int expResult = 1;
        List<RulesetMetadata> result = instance.setValidationSet(service, validationMessage, validationFactory, metadata);
        assertEquals(expResult, result.size());
        String expResultStr = "FHIR REST GP Connect validations";
        assertEquals(expResultStr, result.get(0).getName());
    }

    /**
     * Test of getBootProperties method, of class AbstractValidatorService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        Properties result = instance.getBootProperties();
        String expResult = "__USER_NAME_AND_ORGANISATION_NOT_SET__";
        assertEquals(expResult, result.get("tks.username"));
    }

    /**
     * Test of execute method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    //@Test(expected = Exception.class)
    public void testExecute() throws Exception {
        System.out.println("execute");
        String validationType = "";
        Object o = null;
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(new Object[]{validationType, o});
        assertEquals(expResult, result);
    }

    /**
     * Test of boot method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
        ToolkitSimulator toolkitSimulator = instance.toolkitSimulator;
        Properties bootProperties = instance.getBootProperties();
        String serviceName = "";
        instance.boot(toolkitSimulator, bootProperties, serviceName);
    }

    /**
     * Test of writeSupportingData method, of class AbstractValidatorService.
     */
    @Test
    public void testWriteSupportingData() {
        System.out.println("writeSupportingData");
        String s = "";
        instance.writeSupportingData(s);
    }

    /**
     * Test of reconfigure method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_Properties() throws Exception {
        System.out.println("reconfigure");
        Properties p = instance.getBootProperties();
        instance.reconfigure(p);
    }

    /**
     * Test of reconfigure method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = VALIDATOR_SOURCE_PROPERTY;
        String value = "src/test/resources/validator_source";
        String expResult = null;

        File folder = new File(value);
        folder.mkdirs();

        String result = instance.reconfigure(what, value);
        assertEquals(expResult, result);

        folder.delete();
    }

    /**
     * Test of createPreamble method, of class AbstractValidatorService.
     */
    @Test
    public void testCreatePreamble() {
        System.out.println("createPreamble");
        ArrayList<ValidationReport> vr = new ArrayList<>();
        String dateString = "";
        int numfiles = 0;
        List<RulesetMetadata> metadata = new ArrayList<>();
        ValidationResults validationResults = instance.new ValidationResults();
        String expResult = "<html><head>";
        StringBuilder result = instance.createPreamble(vr, dateString, numfiles, metadata, validationResults);
        assertTrue(result.toString().startsWith(expResult));
    }

    /**
     * Test of appendPostamble method, of class AbstractValidatorService.
     */
    @Test
    public void testAppendPostamble() {
        System.out.println("appendPostamble");
        // start with the insert point
        StringBuilder sb = new StringBuilder("<h2>Results by file</h2>");
        instance.appendPostamble(sb);
        System.out.println(sb.toString());
    }

    /**
     * Test of appendPassFail method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAppendPassFail() throws Exception {
        System.out.println("appendPassFail");
        instance.reportDirectory = new File("src/test/resources/test_report_directory");
        int row = 0;
        ValidationReport v = new ValidationReport("test_report");
        StringBuilder sb = new StringBuilder("test_result");
        instance.appendPassFail(row, v, sb);
    }

    /**
     * Test of writeFile method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteFile() throws Exception {
        System.out.println("writeFile");
        File outputDirectory = new File("src/test/resources/test_output_folder");
        outputDirectory.mkdirs();
        File outputFile = new File("src/test/resources/test_output_folder/testfile.txt");
        outputFile.delete();

        String filename = "testfile.txt";
        String s = "test text";
        instance.writeFile(outputDirectory, filename, s);

        assertTrue(outputFile.exists());

        outputFile.delete();
        outputDirectory.delete();
    }

    /**
     * Test of appendALink method, of class AbstractValidatorService.
     */
    @Test
    public void testAppendALink() {
        System.out.println("appendALink");
        String url = "url";
        String text = "text";
        StringBuilder sb = new StringBuilder();
        instance.appendALink(url, text, sb);
        String expResult = "<a href=\"" + url + "\">" + text + "</a>";
        assertEquals(expResult, sb.toString());
    }

    /**
     * Test of service2Filename method, of class AbstractValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testService2Filename() throws Exception {
        System.out.println("service2Filename");
        String service = "A/B";
        String expResult = "A_B";
        String result = instance.service2Filename(service);
        assertEquals(expResult, result);
    }

    /**
     * Test of isAllPassed method, of class AbstractValidatorService.
     */
    @Test
    public void testIsAllPassed() {
        System.out.println("isAllPassed");
        boolean expResult = true;
        boolean result = instance.isAllPassed();
        assertEquals(expResult, result);
    }

    public class AbstractValidatorServiceImpl extends AbstractValidatorService {

        @Override
        public ServiceResponse execute(Object param) throws Exception {
            return null;
        }
    }

    /**
     * Test of resetSummaryCounts method, of class AbstractValidatorService.
     */
    @Test
    public void testResetSummaryCounts() {
        System.out.println("resetSummaryCounts");
        instance.resetSummaryCounts();
    }

    /**
     * Test of registerForReport method, of class AbstractValidatorService.
     */
    @Test
    public void testRegisterForReport() {
        System.out.println("registerForReport");
        EvidenceInterface ei = null;
        instance.registerForReport(ei);
    }

    /**
     * Test of setEvidenceMetaData method, of class AbstractValidatorService.
     */
    @Test
    public void testSetEvidenceMetaData() {
        System.out.println("setEvidenceMetaData");
        EvidenceMetaDataHandler emd = null;
        instance.setEvidenceMetaData(emd);
    }
}
