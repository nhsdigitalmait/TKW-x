/*
 Copyright 2018 Richard Robinson rrobinson@nhs.net

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
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ValidatorMode;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceInterface;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 *
 * @author simonfarrow
 */
public class HttpInterceptorValidatorTest {

    private static File reportsFolder;

    private HttpInterceptorValidator instance;
    private HttpRequest httpRequest;
    
    private final static String TEST_FOLDER = "test_folder";
    private static final String LOGGING_FILE = "src/test/resources/lfos.log";

    public HttpInterceptorValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        reportsFolder = new File(System.getenv("TKWROOT") + "/config/GP_CONNECT/validator_reports/"+TEST_FOLDER);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        // hack to avoid cast error
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";

        ToolkitSimulator tks = new ToolkitSimulator(propertiesFile);
        Mode mode = new ValidatorMode();
        mode.init(tks);
        ServiceManager.getInstance(tks);

        Configurator configurator = Configurator.getConfigurator();
        httpRequest = new HttpRequest("id");
        httpRequest.setRequestType("POST");
        instance = new HttpInterceptorValidator(configurator, SEND_CDA_V2_SERVICE, httpRequest);
        
        reportsFolder.mkdir();
    }

    @After
    public void tearDown() throws IOException {
        deleteFolderAndContents(reportsFolder);
        new File(LOGGING_FILE).delete();
    }

    /**
     * Test of validateRequest method, of class HttpInterceptorValidator.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testValidateRequest() throws IOException, InterruptedException, Exception {
        System.out.println("validateRequest");
        boolean inhibitLogs = isY(System.getProperty(DONTSIGNLOGS_PROPERTY));
       
        int initialCount = reportsFolder.listFiles().length;

        // set discriminator path for |ITK
        Configurator.getConfigurator().setConfiguration("tks.httpinterceptor.itk.discriminator", "replace(replace(/SOAP:Envelope/SOAP:Header/wsa:From/wsa:Address/text(),'http://',''),':','_')");
        byte[] buffer = readFile2String(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml").getBytes();
        httpRequest.setInputStream(new ByteArrayInputStream(buffer));
        httpRequest.setContentLength(buffer.length);
        httpRequest.setRequestContext("/syncsoap");
        httpRequest.setRequestType("POST");
        httpRequest.setHeader("Content-type", "text/xml");
        httpRequest.setHeader("fromASID", TEST_FOLDER); // not sure this is honoured at this point
        LoggingFileOutputStream lfos = new LoggingFileOutputStream(LOGGING_FILE);
        httpRequest.setLoggingFileOutputStream(lfos);
        instance.validateRequest(httpRequest, TEST_FOLDER);
        
        // this is now a thread again so wait for completion
        instance.join();
 
        // four new files in folder if second or subsequent because extracted cda isn't date stampoed so gets overwritten.
        // log file
        // sig file
        // report file
        // redered cda - not for tkw-x
        // supporting data not for tkw-x
        int expResult = initialCount == 0 ? 3 : 2; 
        if (inhibitLogs) {
            expResult--;
        }
        assertEquals(expResult, reportsFolder.listFiles().length - initialCount);
    }

    /**
     * Test of run method, of class HttpInterceptorValidator. invoked by
     * validateRequest
     */
    @Test
    public void testRun() {
        System.out.println("run");
        //instance.run();
    }

    /**
     * Test of registerForReport method, of class HttpInterceptorValidator.
     */
    @Test
    public void testRegisterForReport() {
        System.out.println("registerForReport");
        EvidenceInterface ei = null;
        instance.registerForReport(ei);
    }

}
