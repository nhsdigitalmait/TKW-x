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

import static com.helger.commons.http.CHttpHeader.CONTENT_LENGTH;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_TYPE_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SOAP_ACTION_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.XML_MIMETYPE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.VALIDATOR_REPORT_PROPERTY;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class SpineValidatorServiceTest {

    private SpineValidatorService instance;
    private String reportsFolderPath;
    private static final String LOGGING_FILE = "src/test/resources/lfos.log";

    public SpineValidatorServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new SpineValidatorService();
        boot();
        reportsFolderPath = instance.bootProperties.getProperty(VALIDATOR_REPORT_PROPERTY);
    }

    @After
    public void tearDown() {
        new File(LOGGING_FILE).delete();
    }

    /**
     * Test of getBootProperties method, of class SpineValidatorService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        String expResult = "uk.nhs.digital.mait.tkwx.tk.internalservices.SpineSenderService";
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result.get("tks.classname.SpineSender"));
    }

    /**
     * Test of boot method, of class SpineValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
    }

    /**
     * Test of writeSupportingData method, of class SpineValidatorService.
     */
    @Test
    public void testWriteSupportingData() {
        System.out.println("writeSupportingData");
        String s = "";
        instance.writeSupportingData(s);
    }

    /**
     * Test of execute method, of class SpineValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");

        // interceptor mode needs a populated validation map
        instance.getBootProperties().setProperty("tks.validator.validationmap", System.getenv("TKWROOT") + "/config/HTTP_Interceptor/validationmap.properties");
        instance.getBootProperties().setProperty("tks.validator.config.pds", System.getenv("TKWROOT") + "/config/SPINE_PDS_Validation/validator_config/ITK_Config_PDS.conf");

        // This is copied from the HttpInterceptHandler tests
        String pdsrq = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml")));

        // remove the http preamble we are adding our own
        pdsrq = pdsrq.replaceFirst("(?s)^.*<\\?", "<?");

        HttpRequest req = new HttpRequest("id");
        String validationType = "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01";
        req.setHeader(SOAP_ACTION_HEADER, validationType);
        req.setHeader("Host", "127.0.0.1");
        req.setRequestType("POST");
        String path = "/syncservice-pds/pds";
        req.setRequestContext(path);
        req.setProtocol("HTTP/1.1");
        req.setHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);
        // This doesn't work for some reason
        //req.setContentLength(pdsrq.length());
        req.setHeader(CONTENT_LENGTH, "" + pdsrq.getBytes().length);
        req.setInputStream(new ByteArrayInputStream(pdsrq.getBytes()));
        LoggingFileOutputStream lfos = new LoggingFileOutputStream(LOGGING_FILE);
        req.setLoggingFileOutputStream(lfos);

        Object o = req;
        int expResult = 1;
        File reportsFolder = new File(reportsFolderPath);
        int count = reportsFolder.listFiles().length;
        ServiceResponse result = instance.execute(new Object[]{validationType, o});
        assertEquals(expResult, result.getCode());

        // TODO Need more tests here to check for generation of the validation report
        // check for new report present
        assertEquals(expResult, reportsFolder.listFiles().length - count);
        deleteLatestFile(reportsFolder);
    }

    /**
     * Test of execute method, of class SpineValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String validationType = null;
        String content = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008B_Example_Input_Msg/QUPA_IN000008UK05_QUPA_IN000009UK06.xml")));

        File reportsFolder = new File(reportsFolderPath);
        int count = reportsFolder.listFiles().length;
        ServiceResponse result = instance.execute(new String[]{content, validationType});

        int expResult = 1;
        assertEquals(expResult, result.getCode());

        // check for new report present
        assertEquals(expResult, reportsFolder.listFiles().length - count);
        deleteLatestFile(reportsFolder);
    }

    /**
     * Test of execute method, of class SpineValidatorService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");
        // looking to validate any files in TKWROOT/config/SPINE_ITKTrunk_Client/messages_for_validation
        Object content = null; // interface states that this should be null

        String messageFile = System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/messages_for_validation/QUPA_IN000008UK05_QUPA_IN000009UK06.xml";

        copyFile(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008B_Example_Input_Msg/QUPA_IN000008UK05_QUPA_IN000009UK06.xml",
                messageFile);
        File reportsFolder = new File(reportsFolderPath);
        int count = reportsFolder.listFiles().length;
        ServiceResponse result = instance.execute(content);

        int expResult = 0;
        assertEquals(expResult, result.getCode());
        new File(messageFile).delete();

        // check for new report present
        expResult = 1;
        assertEquals(expResult, reportsFolder.listFiles().length - count);

        deleteLatestFile(reportsFolder);
    }

    /**
     *
     * @param reportsFolder
     */
    public void deleteLatestFile(File reportsFolder) {
        File[] files = reportsFolder.listFiles();
        Arrays.sort(files, (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));
        files[files.length - 1].delete();
    }

    private void boot() throws IOException, Exception {
        String propertiesFile = System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        Properties p = new Properties();
        // dont forget to load the internal props
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(propertiesFile));
        String s = "";
        instance.boot(t, p, s);
    }

    /**
     * Test of execute method, of class SpineValidatorService.
     * see testExecute_Object
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

}
