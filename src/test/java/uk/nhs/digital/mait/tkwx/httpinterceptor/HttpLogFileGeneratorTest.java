/*
 Copyright 2012-19  Simon Farrow <simon.farrow1@nhs.net>

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
package uk.nhs.digital.mait.tkwx.httpinterceptor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_TYPE_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SEND_CDA_V2_SERVICE;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SOAP_ACTION_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.XML_MIMETYPE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.ORG_CONFIGURATOR;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.ORG_DEFAULT_SYSTEM_PROPERTIES_CONFIGURATOR;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class HttpLogFileGeneratorTest {

    private static final String TEST_FILE = "test.txt";

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    public HttpLogFileGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // point configurator at system properties
        System.setProperty(ORG_CONFIGURATOR, ORG_DEFAULT_SYSTEM_PROPERTIES_CONFIGURATOR);

        // set the http header discriminator
        System.setProperty("tks.httpinterceptor.http.header.discriminator", "Custom_Header ^xxx_(.*)_zzz$");

        // set the itk discriminator
        System.setProperty("tks.httpinterceptor.itk.discriminator",
                "replace(replace(/SOAP:Envelope/SOAP:Header/wsa:From/wsa:Address/text(),'http://',''),':','_')");

        // set the spine discriminator
        System.setProperty("tks.httpinterceptor.spine.discriminator",
                "/SOAP:Envelope/SOAP:Header/hl7:communicationFunctionSnd/hl7:device/hl7:id/@extension");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of generateSubFolderName_Spine method, of class
     * HttpLogFileGenerator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateSubFolderName_Spine() throws Exception {
        System.out.println("generateSubFolderName_Spine");

        String spineMessage = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml")));

        HttpRequest req = new HttpRequest("id");
        req.setInputStream(new ByteArrayInputStream(spineMessage.getBytes()));
        req.setContentLength(spineMessage.length());
        String service = "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01";
        req.setHeader(SOAP_ACTION_HEADER, service);
        String expResult = "SIAB-Client-200";
        String result = HttpLogFileGenerator.generateSubFolderName(req, service);
        assertEquals(expResult, result);
    }

    /**
     * Test of generateSubFolderName_ITK method, of class HttpLogFileGenerator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateSubFolderName_ITK() throws Exception {
        System.out.println("generateSubFolderName_ITK");

        String itkMessage = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));

        HttpRequest req = new HttpRequest("id");
        req.setInputStream(new ByteArrayInputStream(itkMessage.getBytes()));
        req.setContentLength(itkMessage.length());
        String service = SEND_CDA_V2_SERVICE;
        req.setHeader(SOAP_ACTION_HEADER, service);
        req.setHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);
        String expResult = "localhost_4000";
        String result = HttpLogFileGenerator.generateSubFolderName(req, service);
        assertEquals(expResult, result);
    }

    /**
     * Test of generateSubFolderName_HttpHeader method, of class
     * HttpLogFileGenerator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateSubFolderName_HttpHeader() throws Exception {
        System.out.println("generateSubFolderName_HttpHeader");

        String itkMessage = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));

        HttpRequest req = new HttpRequest("id");
        req.setInputStream(new ByteArrayInputStream(itkMessage.getBytes()));
        req.setContentLength(itkMessage.length());
        String service = SEND_CDA_V2_SERVICE;
        req.setHeader(SOAP_ACTION_HEADER, service);
        req.setHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);
        req.setHeader("custom_header", "xxx_yyy_zzz");
        String expResult = "yyy";
        String result = HttpLogFileGenerator.generateSubFolderName(req, service);
        assertEquals(expResult, result);
    }

    /**
     * Test of createLogFile method, of class HttpLogFileGenerator.see
     * testCreateLogFile param 1 is HttpRequest
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateLogFile_3args_1() throws Exception {
        System.out.println("createLogFile_3args_1");
        HttpRequest req = new HttpRequest("id");
        String requestType = "POST";
        req.setRequestType(requestType);
        String remoteAddress = "255.255.255.255";
        req.setRemoteAddress(InetAddress.getByName(remoteAddress));
        String savedMessagesDir = "src/test/resources/savedmessages";
        String subDir = "subdir";
        String expResult = new File(savedMessagesDir).getCanonicalPath();
        expResult += "/" + subDir + "/" + requestType + "_" + remoteAddress + "_[0-9]+.log";
        expResult = expResult.replaceAll("\\\\","/");
        String result = HttpLogFileGenerator.createLogFile(req, savedMessagesDir, subDir);
        result = result.replaceAll("\\\\","/");
        assertTrue(result.matches(expResult));

        // this creates the folder but not the log file
        File folder = new File(savedMessagesDir + "/" + subDir);
        assertTrue(folder.exists());

        // tidy up
        folder.delete();
        folder.getParentFile().delete();
    }

    /**
     * Test of createLogFile method, of class HttpLogFileGenerator.param 1 is
     * String
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateLogFile_3args_2() throws Exception {
        System.out.println("createLogFile");
        try ( FileWriter fw = new FileWriter(TEST_FILE)) {
            fw.write("message");
        }
        String fileName = TEST_FILE;
        String savedMessagesDir = "src/test/resources/savedmessages";
        String subDir = "subdir";
        String result = HttpLogFileGenerator.createLogFile(fileName, savedMessagesDir, subDir);
        result = result.replaceAll("\\\\","/");

        assertTrue(result.matches("^.*"+ "/" + savedMessagesDir + "/" + subDir + "/" + TEST_FILE + "_" + "[0-9]{17}\\.log$"));

        new File(TEST_FILE).delete();

        // this creates the folder but not the log file
        File folder = new File(savedMessagesDir + "/" + subDir);
        assertTrue(folder.exists());

        // tidy up
        folder.delete();
        folder.getParentFile().delete();
    }

    /**
     * Test of generateSubFolderName method, of class HttpLogFileGenerator. see
     * testGenerateSubFolderName_Spine
     */
    @Test
    public void testGenerateSubFolderName() throws Exception {
        System.out.println("generateSubFolderName");
        // TODO why no content?
    }
}
