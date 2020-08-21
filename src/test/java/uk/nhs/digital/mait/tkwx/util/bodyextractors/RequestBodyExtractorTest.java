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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ConditionalCompilationControls;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 *
 * @author sifa2
 */
public class RequestBodyExtractorTest {

    private RequestBodyExtractor instance;
    private ByteArrayInputStream in;
    private static String JSON;

    public RequestBodyExtractorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        JSON = readFile2String("src/test/resources/slots.json");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        in = new ByteArrayInputStream(("POST /cp HTTP/1.1\r\nh1: v1\r\n\r\n<a>stuff</a>\r\n" + END_INBOUND_MARKER).getBytes());
        instance = new RequestBodyExtractor();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBody method, of class RequestBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        String expResult = "<a>stuff</a>";
        String result = instance.getBody(in);
        assertEquals(expResult, result);

        // json format with and without getXML call
        in = new ByteArrayInputStream(("POST /cp HTTP/1.1\r\nContent-Type: json+fhir\r\n\r\n"+JSON+"\r\n" + END_INBOUND_MARKER).getBytes());
        // default false returns json
        expResult = "\"resourceType\": \"Bundle\"";
        result = instance.getBody(in);
        assertTrue(result.contains(expResult));
        assertTrue(result.startsWith("{"));
        
        expResult = "json+fhir";
        result = instance.httpRequestHeaders.getHttpHeaderValue(CONTENT_TYPE_HEADER);
        assertEquals(expResult, result);
        
        // boolean true returns xml
        in = new ByteArrayInputStream((
                "POST /cp HTTP/1.1\r\nContent-Type: json+fhir\r\nContent-Length: "+JSON.length()+"\r\n\r\n"+JSON+"\r\n" + END_INBOUND_MARKER).getBytes());
        expResult = "<Bundle";
        result = instance.getBody(in, true);
        assertTrue(result.startsWith(expResult));

        expResult = FHIR_XML_MIMETYPE_DSTU2;
        result = instance.httpRequestHeaders.getHttpHeaderValue(CONTENT_TYPE_HEADER);
        assertEquals(expResult, result);

        expResult = "json+fhir";
        result = instance.httpRequestHeaders.getHttpHeaderValue("X-was-Content-Type");
        assertEquals(expResult, result);

        expResult = "4317";
        result = instance.httpRequestHeaders.getHttpHeaderValue(CONTENT_LENGTH_HEADER);
        assertEquals(expResult, result);
   }

    /**
     * Test of getHttpRequestHeaders method, of class RequestBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpRequestHeaders() throws Exception {
        System.out.println("getHttpRequestHeaders");
        instance.getBody(in);
        int expResult = 1;
        HttpHeaderManager result = instance.getHttpRequestHeaders();
        assertEquals(expResult, result.getFieldNames().size());
        String expStrResult = "h1";
        assertEquals(expStrResult, result.getFieldNames().get(0));
    }

    /**
     * Test of getHttpRequestContextPath method, of class RequestBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpRequestContextPath() throws Exception {
        System.out.println("getHttpRequestContextPath");
        instance.getBody(in);
        String expResult = "/cp";
        String result = instance.getHttpRequestContextPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestMethod method, of class RequestBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpRequestMethod() throws Exception {
        System.out.println("getHttpRequestMethod");
        instance.getBody(in);
        String expResult = "POST";
        String result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);
    }

    @Test
    public void testRealTransmitterLog() throws Exception {
        System.out.println("realTransmitterLog");
        String expResult = "Parameters";
        String result = instance.getBody(new FileInputStream("src/test/resources/get_care_record_ADM_3.msg_20170829093302.402.msg"));
        assertTrue(result.contains(expResult));

        expResult = "POST";
        result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);

        HttpHeaderManager requestHeaders = instance.getHttpRequestHeaders();
        ArrayList<String> fieldNames = requestHeaders.getFieldNames();
        int intExpResult = 11;
        assertEquals(intExpResult, fieldNames.size());

//        HttpHeaderManager responseHeaders = instance.getHttpResponseHeaders();
//        fieldNames = responseHeaders.getFieldNames();
//        intExpResult = 1;
//        assertEquals(intExpResult, fieldNames.size());
    }

    @Test
    public void testRealSimulatorLog() throws Exception {
        System.out.println("realSimulatorLog");
        String expResult = "<soap:Envelope";
        String result = instance.getBody(new FileInputStream("src/test/resources/urn_nhs-itk_services_201005_SendCDADocument-v2-0_20171005151927_31C71492-A9D8-11E7-B542-E3BA913BABA9.msg"));
        assertTrue(result.contains(expResult));

        expResult = "POST";
        result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);

        HttpHeaderManager requestHeaders = instance.getHttpRequestHeaders();
        ArrayList<String> fieldNames = requestHeaders.getFieldNames();
        int intExpResult = 6;
        assertEquals(intExpResult, fieldNames.size());

//        HttpHeaderManager responseHeaders = instance.getHttpResponseHeaders();
//        fieldNames = responseHeaders.getFieldNames();
//        intExpResult = 1;
//        assertEquals(intExpResult, fieldNames.size());
    }

    @Test
    public void testMockedupChunkedRequest() throws Exception {
        if (ConditionalCompilationControls.LOG_RAW_RESPONSE) {
            System.out.println("testMockedupChunkedRequest");
            String expResult = "<a>stuff</a>";
            String result = instance.getBody(
                    new ByteArrayInputStream(
                            ("POST /cp HTTP/1.1\r\nTransfer-Encoding: chunked\r\n\r\n"
                            + Integer.toHexString(expResult.length())
                            + "\r\n" + expResult + "\r\n0\r\n\r\n" + END_INBOUND_MARKER).getBytes()));
            assertEquals(expResult, result);

            expResult = "POST";
            result = instance.getHttpRequestMethod();
            assertEquals(expResult, result);

            expResult = "/cp";
            result = instance.getHttpRequestContextPath();
            assertEquals(expResult, result);

            HttpHeaderManager requestHeaders = instance.getHttpRequestHeaders();
            ArrayList<String> fieldNames = requestHeaders.getFieldNames();
            int intExpResult = 1;
            assertEquals(intExpResult, fieldNames.size());
        }
    }

    /**
     * Test of getRelevantHttpHeaders method, of class RequestBodyExtractor.
     */
    @Test
    public void testGetRelevantHttpHeaders() {
        System.out.println("getRelevantHttpHeaders");
        HttpHeaderManager expResult =  instance.getHttpRequestHeaders();
        HttpHeaderManager result = instance.getRelevantHttpHeaders();
        assertEquals(expResult, result);
    }
}
