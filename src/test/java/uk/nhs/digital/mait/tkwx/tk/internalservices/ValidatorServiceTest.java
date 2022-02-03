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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import static uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse.NOTALLPASSED;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class ValidatorServiceTest {
    
    private ValidatorService instance;
    private Properties properties;
    private static final String LOG_FILE_NAME = "simulator_log_request.txt";
    private static final String TEST_LOG_FILE = "src/test/resources/testlog.txt";

    public ValidatorServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // ensure the correct Configurator is set
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new ValidatorService();
        // initialises with an GP_CONNECT instance
        boot(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");

        // copy a file for validation check if it's not already there.
        String fqlfn = System.getenv("TKWROOT") + "/config/GP_CONNECT/messages_for_validation/" + LOG_FILE_NAME;
        if (!fileExists(fqlfn)) {
            copyFile("src/test/resources/" + LOG_FILE_NAME, fqlfn);
        }
    }

    @After
    public void tearDown() {
        new File(TEST_LOG_FILE).delete();
    }

    /**
     * Test of getBootProperties method, of class ValidatorService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        String expResult = "uk.nhs.digital.mait.tkwx.tk.internalservices.SpineSenderService";
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result.get("tks.classname.SpineSender"));
    }

    /**
     * Test of boot method, of class ValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
    }

    /**
     * Test of writeSupportingData method, of class ValidatorService.
     */
    @Test
    public void testWriteSupportingData() {
        System.out.println("writeSupportingData");
        String s = "";
        instance.writeSupportingData(s);
    }

    /**
     * Test of execute method, of class ValidatorService. validates all files in
     * GP_CONNECT/messages_for_validation (There is one)
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");
        HashMap<String, String> linkSet = new HashMap<>();
        // This should be parts of an html element a pair of filename path (href) and displayed text which is also a matching filename
        // expects the source message to be in the same folder.. This puts a working link into the validation report
        linkSet.put(LOG_FILE_NAME, LOG_FILE_NAME);
        Object content = linkSet;
        int expResult = NOTALLPASSED; // 0 = Not Run, 1 => All Passed, 2 => Not all passed
        ServiceResponse result = instance.execute(content);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class ValidatorService. used by
     * HttpInterceptWorker for in flight validation
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");
        String service = "urn:nhs:names:services:gpconnect:fhir:rest:search:slot-1";
        HttpRequest httpRequest = new HttpRequest("id");
        String message = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/config/GP_CONNECT/simulator_config/responses/slots.xml")));
        httpRequest.setInputStream(new ByteArrayInputStream(message.getBytes()));
        httpRequest.setContentLength(message.length());
        httpRequest.setLoggingFileOutputStream(new LoggingFileOutputStream(TEST_LOG_FILE));
        int expResult = ServiceResponse.NOTALLPASSED;
        Object o = httpRequest;

        // puts a validation report into GP_CONNECT/validator_reports
        ServiceResponse result = instance.execute(new Object[]{service, o});

        assertEquals(expResult, result.getCode());
        String reportName = result.getResponse();
        assertTrue(reportName.contains(service.replaceAll(":", "_")));

        String reportFolder = properties.get(VALIDATOR_REPORT_PROPERTY).toString();
        File reportFile = new File(reportFolder + "/" + reportName);
        assertTrue(reportFile.exists());

        reportFile.delete();
    }

    /**
     * Test of reconfigure method, of class ValidatorService.
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
     * Test of reconfigure method, of class ValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = VALIDATOR_SOURCE_PROPERTY;
        File newsource = new File("newsource");
        newsource.mkdirs();
        String value = "newsource";
        String expResult = null;
        String result = instance.reconfigure(what, value);
        assertEquals(expResult, result);
        newsource.delete();
    }

    /**
     * start up an instance with a correspondence config called by setup
     *
     * @throws IOException
     * @throws Exception
     */
    private void boot(String propertiesFile) throws IOException, Exception {
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        properties = new Properties();
        // dont forget to load the internal props
        properties.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        properties.load(new FileReader(propertiesFile));
        
        // substitute TKW_ROOT this will have already been done in the executable
        properties = Utils.interpretEnvVars(properties);

        String s = "";
        instance.boot(t, properties, s);
    }

    /**
     * Test of execute method, of class ValidatorService.
     * see testExecute_Object
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

}
