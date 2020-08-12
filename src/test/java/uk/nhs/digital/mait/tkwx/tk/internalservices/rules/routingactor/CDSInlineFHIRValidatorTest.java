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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 *
 * @author simonfarrow
 */
public class CDSInlineFHIRValidatorTest {

    private CDSInlineFHIRValidator instance;

    public CDSInlineFHIRValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new CDSInlineFHIRValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of makeResponse method, of class CDSInlineFHIRValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String, Substitution> substitutions = new HashMap<>();
        
        HttpRequest httpRequest = new HttpRequest("");
        // fails with npe if no remote address
        httpRequest.setRemoteAddress(InetAddress.getByName("127.0.0.1"));
        httpRequest.setRequestContext("/");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        ZipEntry zipEntry = new ZipEntry("entry1");
        zipOut.putNextEntry(zipEntry);
        
        // This is actually a fhir json repsonse but ho hum
        String json= readFile2String("src/test/resources/slots.json");
        zipOut.write(json.getBytes());
        zipOut.close();
        bos.close();
        
        httpRequest.setInputStream(new ByteArrayInputStream(bos.toByteArray()));
        httpRequest.setContentLength(bos.toByteArray().length);
        String expResult = "OperationOutcome";
        String result = instance.makeResponse(substitutions, httpRequest);
        
        // This checks that its well formed xml and also that the root node is correct.
        assertEquals(expResult, XPathManager.xpathExtractor("local-name(/*)",result));
    }

    /**
     * Test of setValidationReport method, of class CDSInlineFHIRValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetValidationReport() throws Exception {
        System.out.println("setValidationReport");
        String s = "";
        instance.setValidationReport(s);
    }

}
