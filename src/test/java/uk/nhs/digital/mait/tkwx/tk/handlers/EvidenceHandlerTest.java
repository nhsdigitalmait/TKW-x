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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 *
 * @author simonfarrow
 */
public class EvidenceHandlerTest {

    private EvidenceHandler instance;
    private static final String TEST_EVIDENCE_FILE = "src/test/resources/evidence.txt";
    
    public EvidenceHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new EvidenceHandler();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addEvidence method, of class EvidenceHandler.
     */
    @Test
    public void testAddEvidence_HttpRequest() {
        System.out.println("addEvidence");
        HttpRequest req = new HttpRequest("id");
        instance.addEvidence(req);
    }

    /**
     * Test of addEvidence method, of class EvidenceHandler.
     */
    @Test
    public void testAddEvidence_ServiceResponse() {
        System.out.println("addEvidence");
        ServiceResponse r = new ServiceResponse();
        instance.addEvidence(r);
    }

    /**
     * Test of addEvidence method, of class EvidenceHandler.
     * @throws java.io.IOException
     */
    @Test
    public void testAddEvidence_String() throws IOException {
        System.out.println("addEvidence");
        try (FileWriter fw = new FileWriter(TEST_EVIDENCE_FILE)) {
            fw.write("evidence");
        }
        File validationReportFile = new File(TEST_EVIDENCE_FILE);
        instance.addEvidence("evidence");
        
        validationReportFile.delete();
    }

    /**
     * Test of sendEvidence method, of class EvidenceHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendEvidence() throws Exception {
        System.out.println("sendEvidence");
        // TODO Uncomment this when it is working
        //instance.sendEvidence();
    }
    
}
