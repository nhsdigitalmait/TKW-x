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
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Compressor;

/**
 * Most of this is present just to stop "create new tests" changing the file
 *
 * @author sifa2
 */
public class AbstractBodyExtractorTest {

    private AbstractBodyExtractorImpl instance;

    private static final String FHIR_XML = "<Parameters xmlns=\"http://hl7.org/fhir\">\n"
            + "    <parameter>\n"
            + "        <name value=\"patientNHSNumber\" />\n"
            + "        <valueIdentifier>\n"
            + "            <system value=\"http://fhir.nhs.net/Id/nhs-number\" />\n"
            + "            <value value=\"__NHS_NO__\" />\n"
            + "        </valueIdentifier>\n"
            + "    </parameter>\n"
            + "    <parameter>\n"
            + "        <name value=\"recordSection\" />\n"
            + "        <valueCodeableConcept>\n"
            + "            <coding>\n"
            + "                <system value=\"http://fhir.nhs.net/ValueSet/gpconnect-record-section-1\" />\n"
            + "                <code value=\"__CODE__\" />\n"
            + "            </coding>\n"
            + "        </valueCodeableConcept>\n"
            + "    </parameter>\n"
            + "</Parameters>";

    public AbstractBodyExtractorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new AbstractBodyExtractorImpl();
        instance.getBody(new ByteArrayInputStream("POST / HTTP/1.1\r\n\r\nabcd".getBytes()));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBody method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        InputStream is = null;
        String expResult = "";
        String result = instance.getBody(is);
        assertEquals(expResult, result);
    }

    /**
     * Test of getBody method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFind() throws Exception {
        System.out.println("find");
        int expResult = 2;
        int result = instance.find("cd".getBytes(), "abcd".getBytes(), 0);
        assertEquals(expResult, result);

        expResult = 6;
        result = instance.find("cd".getBytes(), "abcdabcd".getBytes(), 4);
        assertEquals(expResult, result);
    }

    /**
     * Test of getBody method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindBodyStart() throws Exception {
        System.out.println("findBodyStart");
        int expResult = 9;
        int result = instance.findBodyStart("stuff\r\n\r\nmorestuff".getBytes(), 0, "request");
        assertEquals(expResult, result);
    }

    /**
     * Test of getBody method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody_InputStream() throws Exception {
        System.out.println("getBody");
        InputStream is = null;
        String expResult = "";
        String result = instance.getBody(is);
        //assertEquals(expResult, result);
    }

    /**
     * Test of getBody method, of class AbstractBodyExtractor.
     */
    @Test
    public void testGetBody_InputStream_boolean() throws Exception {
        System.out.println("getBody");
        InputStream is = null;
        boolean getXML = false;
        String expResult = "";
        String result = instance.getBody(is, getXML);
        //assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestHeaders method, of class AbstractBodyExtractor.
     */
    @Test
    public void testGetHttpRequestHeaders() {
        System.out.println("getHttpRequestHeaders");
        HttpHeaderManager expResult = null;
        HttpHeaderManager result = instance.getHttpRequestHeaders();
        //assertEquals(expResult, result);
    }

    /**
     * Test of getHttpResponseHeaders method, of class AbstractBodyExtractor.
     */
    @Test
    public void testGetHttpResponseHeaders() {
        System.out.println("getHttpResponseHeaders");
        HttpHeaderManager expResult = null;
        HttpHeaderManager result = instance.getHttpResponseHeaders();
        //assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestContextPath method, of class AbstractBodyExtractor.
     */
    @Test
    public void testGetHttpRequestContextPath() {
        System.out.println("getHttpRequestContextPath");
        String expResult = "";
        String result = instance.getHttpRequestContextPath();
        //assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestMethod method, of class AbstractBodyExtractor.
     */
    @Test
    public void testGetHttpRequestMethod() {
        System.out.println("getHttpRequestMethod");
        String expResult = "";
        String result = instance.getHttpRequestMethod();
        //assertEquals(expResult, result);
    }

    /**
     * Test of loadFile method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadFile() throws Exception {
        System.out.println("loadFile");
        byte[] expResult = "abcd".getBytes();
        InputStream in = new ByteArrayInputStream(expResult);
        byte[] result = instance.loadFile(in);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of handleJsonContent method, of class AbstractBodyExtractor.
     */
    @Test
    public void testHandleJsonContent() {
        System.out.println("handleJsonContent");
        HttpHeaderManager headerManager = new HttpHeaderManager();
        headerManager.addHttpHeader(CONTENT_TYPE_HEADER, "fhir+json");
        String message = FHIR_XML;
        String expResult = "<Parameters";
        String result = instance.handleJsonContent(headerManager, message);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of handleCompression method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandleCompression() throws Exception {
        System.out.println("handleCompression");
        HttpHeaderManager headerManager = new HttpHeaderManager();
        headerManager.addHttpHeader(CONTENT_ENCODING_HEADER, "gzip");
        byte[] expResult = "abcd".getBytes();
        byte[] messageBytes = Compressor.compress(expResult, Compressor.CompressionType.COMPRESSION_GZIP);
        byte[] result = instance.handleCompression(headerManager, messageBytes);
        assertArrayEquals(expResult, result);

        headerManager = new HttpHeaderManager();
        headerManager.addHttpHeader(CONTENT_ENCODING_HEADER, "deflate");
        messageBytes = Compressor.compress(expResult, Compressor.CompressionType.COMPRESSION_DEFLATE);
        result = instance.handleCompression(headerManager, messageBytes);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of handleTransferEncoding method, of class AbstractBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHandleTransferEncoding() throws Exception {
        System.out.println("handleTransferEncoding");
        HttpHeaderManager headerManager = null;
        byte[] logfile = "abcd".getBytes();
        int from = 0;
        int to = 3;
        byte[] expResult = "abc".getBytes();
        byte[] result = instance.handleTransferEncoding(headerManager, logfile, from, to);
        assertArrayEquals(expResult, result);
    }


    /**
     * Test of getRelevantHttpHeaders method, of class AbstractBodyExtractor.
     */
    @Test
    public void testGetRelevantHttpHeaders() {
        System.out.println("getRelevantHttpHeaders");
        HttpHeaderManager expResult = instance.getHttpResponseHeaders();
        HttpHeaderManager result = instance.getRelevantHttpHeaders();
        assertEquals(expResult, result);
    }

    public class AbstractBodyExtractorImpl extends AbstractBodyExtractor {

        @Override
        public String getBody(InputStream is, boolean getXML) throws Exception {
            return "";
        }
    }
}
