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
package uk.nhs.digital.mait.tkwx.itklogverifier;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author sifa2
 */
public class LogVerifierTest {

    private LogVerifier instance;
    private final static String LOGFILE = "test.log";
    private File sig;

    public LogVerifierTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        Utils.writeString2File(LOGFILE, "1234567890");
    }

    @AfterClass
    public static void tearDownClass() {
        new File(LOGFILE).delete();
    }

    @Before
    public void setUp() throws Exception {
        instance = LogVerifier.getInstance();
        sig = new File(LOGFILE + ".signature");
    }

    @After
    public void tearDown() {
        sig.delete();
    }

    /**
     * Test of getInstance method, of class LogVerifier.
     */
    @Test
    public void testGetInstance() throws Exception {
        System.out.println("getInstance");
        LogVerifier result = LogVerifier.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of makeSignature method, of class LogVerifier.
     */
    @Test
    public void testMakeSignature() throws Exception {
        System.out.println("makeSignature");
        String filename = LOGFILE;
        sig.delete();
        instance.makeSignature(filename);
        assertTrue(sig.exists());
    }

    /**
     * Test of verifySignature method, of class LogVerifier.
     */
    @Test
    public void testVerifySignature() throws Exception {
        System.out.println("verifySignature");
        String filename = LOGFILE;
        String expResult = "Signature OK: " + LOGFILE;
        instance.makeSignature(filename);
        String result = instance.verifySignature(filename);
        assertEquals(expResult, result);
    }
}
