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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.File;
import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.ORG_SAVE_DIRECTORY;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestTest.deleteFolder;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author SIFA2
 */
public class AutotestFileSaveDistributionEnvelopeHandlerTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private static final String TESTSAVEFOLDER = "src/test/resources/testsavefolder";

    public AutotestFileSaveDistributionEnvelopeHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        deleteFolder(TESTSAVEFOLDER);
    }

    /**
     * Test of handle method, of class
     * AutotestFileSaveDistributionEnvelopeHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        DistributionEnvelopeHelper deh = DistributionEnvelopeHelper.getInstance();
        String des = Utils.readFile2String(TKWROOT + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml");
        DistributionEnvelope d = deh.getDistributionEnvelope(des);
        System.setProperty(ORG_SAVE_DIRECTORY, TESTSAVEFOLDER);
        AutotestFileSaveDistributionEnvelopeHandler instance = new AutotestFileSaveDistributionEnvelopeHandler();
        instance.handle(d);
        File destFolder = new File(TESTSAVEFOLDER);
        File[] fileList = destFolder.listFiles();
        int expResult = 1;
        assertEquals(expResult, fileList.length);
        System.out.println(fileList[0].getCanonicalPath());
    }

}
