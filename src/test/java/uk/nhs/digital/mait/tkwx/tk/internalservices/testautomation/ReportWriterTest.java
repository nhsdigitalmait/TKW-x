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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestTest.deleteFolder;

/**
 *
 * @author sifa2
 */
public class ReportWriterTest {

    private ReportWriter instance;

    public ReportWriterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Script s = new Script();
        s.setName("testreport");
        File f = new File("reportfolder");
        f.mkdir();
        instance = new ReportWriter(s, f);
    }

    @After
    public void tearDown() {
        deleteFolder("reportfolder");
    }

    /**
     * Test of setValidatorReport method, of class ReportWriter.
     */
    @Test
    public void testSetValidatorReport() {
        System.out.println("setValidatorReport");
        String v = "";
        instance.setValidatorReport(v);
    }

    /**
     * Test of finalize method, of class ReportWriter.
     */
    @Test
    public void testFinalize() throws Exception, Throwable {
            System.out.println("finalize");
            instance.finalize();
    }

    /**
     * Test of log method, of class ReportWriter.
     */
    @Test
    public void testLog() {
        System.out.println("log");
        ReportItem r = new ReportItem("schedule", "test", TestResult.PASS, "comment");
        instance.log(r);
    }

    /**
     * Test of dump method, of class ReportWriter.
     */
    @Test
    public void testDump() throws Exception {
        System.out.println("dump");
        instance.dump();
    }

}
