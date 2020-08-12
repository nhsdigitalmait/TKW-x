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
package uk.nhs.digital.mait.tkwx.meshinterceptor;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonSetup;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.commonTeardown;
import static uk.nhs.digital.mait.tkwx.mesh.MeshDataTest.deleteFolderAndContents;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.MAILBOXID;
import static uk.nhs.digital.mait.tkwx.meshinterceptor.MeshInterceptHandlerTest.SAVED_MESSAGES_FOLDER;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.FHIR_SERVICE_LOCATION;
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ValidatorMode;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorBusAckOnlyTest.makeMeshRequest;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;

/**
 * The MeshInterceptorValidator creates a validation report in the config reports folder
 * then copies it and the request message to the mailboxid validation reports folder
 * @author simonfarrow
 */
public class MeshInterceptorValidatorTest {

    private MeshInterceptorValidator instance = null;
    private MeshInterceptHandler meshInterceptHandler;
    private Configurator configurator;

    private static final File  MAILBOX_VALIDATOR_REPORTS_FOLDER = new File(SAVED_MESSAGES_FOLDER + "/" + MAILBOXID + "/validation_reports");
   
    public MeshInterceptorValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.setUpClass();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        MESHFHIRITKRoutingActorBusAckOnlyTest.tearDownClass();
    }

    @Before
    public void setUp() throws Exception {
        commonSetup();

        String propertiesFile = System.getenv("TKWROOT") + "/config/FHIR_MESH/tkw-x.properties";

        ToolkitSimulator tks = new ToolkitSimulator(propertiesFile);
        Mode mode = new ValidatorMode();
        mode.init(tks);
        ServiceManager.getInstance(tks);
        meshInterceptHandler = new MeshInterceptHandler();
        // put the reports in saved messages under the test folder, they will appear in src/test/resources/mailboxid/validation_reports
        meshInterceptHandler.setSavedMessagesDirectory(SAVED_MESSAGES_FOLDER);

        if (!MAILBOX_VALIDATOR_REPORTS_FOLDER.exists()) {
            MAILBOX_VALIDATOR_REPORTS_FOLDER.mkdirs();
        }

        configurator = Configurator.getConfigurator();
        configurator.setConfiguration("tks.MeshTransport."+MAILBOXID+".out", "Dont care not used stops warning that this property is not set");
        instance = new MeshInterceptorValidator(configurator, meshInterceptHandler, MAILBOXID);
    }

    @After
    public void tearDown() throws IOException {
        // delete the reports folder and contents
        deleteFolderAndContents(MAILBOX_VALIDATOR_REPORTS_FOLDER);
        new File(SAVED_MESSAGES_FOLDER + "/" + MAILBOXID).delete();
        commonTeardown();
    }

    /**
     * Test of validateRequest method, of class MeshInterceptorValidator.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testValidateRequest() throws InterruptedException, Exception {
        System.out.println("validateRequest");
        boolean inhibitLogs = isY(System.getProperty(DONTSIGNLOGS_PROPERTY));

        MeshRequest req = makeMeshRequest();
        // eventCode should be "ITK008M"; 
        // TODO unfortunately this isn't one of the eventCodes that the validator validates
        String eventCode = xpathExtractor(FHIR_SERVICE_LOCATION, new String(req.getRequestMeshMessage().getDataFile().getContent()));
        String configuredReportsFolderName = configurator.getConfiguration(VALIDATOR_REPORT_PROPERTY);
        File reportsFolder = new File(configuredReportsFolderName);
        int initialCount = reportsFolder.listFiles().length;
        instance.validateRequest(eventCode, req);
        // block until validation thread has completed
        instance.join();
        int expResult = 3;
        if (inhibitLogs) {
            expResult--;
        }
        
        // The reports folder is located according to the setting in Configuration tks.validator.reports
        // an html validation report of the form validation_report_<EventCode>_<15 digit Timestamp>.html
        // plus optional associated signature file depending on setting of inhibitLogs
        // This is a staging area for generating the report that goes in the table below
        //assertEquals(expResult, reportsFolder.listFiles().length - initialCount);
        
        // under src/test/resources/mailboxid/validation_reports there should be two files
        // the same html report as above copied from the folder 
        // as above plus a MESH log file of the form <EventCode>_<17 digit Timestamp>.log containing the xml request
        assertEquals(expResult,MAILBOX_VALIDATOR_REPORTS_FOLDER.listFiles().length);

        // test some file names and tidy up
        for ( String fn : MAILBOX_VALIDATOR_REPORTS_FOLDER.list()) {
            System.out.println(fn);
            // tidy up the configured reports folder from this run
            if (fn.endsWith(".html")){
                assertEquals(true, fn.matches(("^validation_report_"+eventCode+"_[0-9]{17}\\.html$")));
                //File configuredReportFile = new File(configuredReportsFolderName+"/"+fn);
                // check the report file exists in the mailbox validation reports folder
                //assertEquals(true, configuredReportFile.exists());
                
                //configuredReportFile.delete();
                //if (!inhibitLogs){
                //    new File(configuredReportsFolderName+"/"+fn+".signature").delete();
                //}
            } else if (fn.endsWith(".log")) {
                assertEquals(true, fn.matches(("^"+eventCode+"_[0-9]{17}\\.log$")));
            } else {
                assertEquals(true, fn.matches(("^validation_report_"+eventCode+"_[0-9]{17}\\.html.signature$")));
            }
        } // for
    }

    /**
     * called by validateRequest Test of run method, of class
     * MeshInterceptorValidator.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        //instance.run();
    }

}
