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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.LOGFILE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.NAME;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TIMESTAMP;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.getMessage;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.writeLogFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class BasicTrackingIdCorrelatorTest {

    public BasicTrackingIdCorrelatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        for (String filename : new String[]{LOGFILE, "src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of correlate method, of class BasicTrackingIdCorrelator.
     */
    @Test
    public void testCorrelate() throws Exception {
        System.out.println("correlate");

        new File(TIMESTAMP).mkdir();
        Message request = getMessage(NAME, TIMESTAMP);
        BasicTrackingIdCorrelator instance = new BasicTrackingIdCorrelator();

        writeLogFile("stuff\r\nRelatesTo " + request.getTrackingId() + "\r\nstuff");
        File log = new File(LOGFILE);

        boolean expResult = true;
        boolean result = instance.correlate(log, request);
        assertEquals(expResult, result);

        writeLogFile("stuff\r\nRelatesTo " + "xxx" + "\r\nstuff");
        expResult = false;
        result = instance.correlate(log, request);
        assertEquals(expResult, result);

        writeLogFile("stuff\r\nRelatesXX " + "xxx" + "\r\nstuff");
        result = instance.correlate(log, request);
        assertEquals(expResult, result);

        if (!log.delete()) {
            fail("delete log file failed (not closed?)");
        }
        new File(TIMESTAMP + "/" + NAME + "_1.msg").delete();
        new File(TIMESTAMP).delete();
    }

}
