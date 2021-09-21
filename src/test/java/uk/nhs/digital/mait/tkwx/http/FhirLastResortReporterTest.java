/*
  Copyright 2021  Vipul

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
package uk.nhs.digital.mait.tkwx.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vipul
 */
public class FhirLastResortReporterTest {

    private OutputStream ostream;

    public FhirLastResortReporterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ostream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws IOException {
        ostream.close();
    }

    /**
     * Test of report method, of class FhirLastResortReporter.
     */
    @Test
    public void testReport() {
        System.out.println("report");
        FhirLastResortReporter reporter = new FhirLastResortReporter();
        reporter.report("hello", ostream);
        String s = ostream.toString();
        assertTrue("report must start with a http 500 response",
                s.startsWith("HTTP/1.1 500 OK"));
        assertTrue("report must contain a fhir operation outcome",
                s.contains("<OperationOutcome xmlns=\"http://hl7.org/fhir\">"));
    }

}
