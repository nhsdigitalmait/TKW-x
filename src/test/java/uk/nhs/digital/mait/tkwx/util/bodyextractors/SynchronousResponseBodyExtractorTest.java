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
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_REQUEST_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ConditionalCompilationControls;

/**
 *
 * @author sifa2
 */
public class SynchronousResponseBodyExtractorTest {

    private SynchronousResponseBodyExtractor instance;
    private ByteArrayInputStream in;

    public SynchronousResponseBodyExtractorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        in = new ByteArrayInputStream(("POST path HTTP/1.1\r\nhreq1: v1\r\n\r\n" + END_REQUEST_MARKER + "\r\nHTTP/1.1 200 OK\r\nhresp1: v2\r\n\r\nstuff").getBytes());
        instance = new SynchronousResponseBodyExtractor();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBody method, of class SynchronousResponseBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        String expResult = "stuff";
        in = new ByteArrayInputStream((END_REQUEST_MARKER + "\r\n\r\nHTTP/1.1 400\r\n\r\n" + expResult).getBytes());
        String result = instance.getBody(in);
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestHeaders method, of class
     * SynchronousResponseBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpRequestHeaders() throws Exception {
        System.out.println("getHttpRequestHeaders");
        // must call getBody first
        String body = instance.getBody(in);

        int expResult = 1;
        HttpHeaderManager result = instance.getHttpRequestHeaders();
        assertEquals(expResult, result.getFieldNames().size());
        String expStrResult = "v1";
        String headerValue = result.getHttpHeaderValue("hreq1");
        assertEquals(expStrResult, headerValue);
    }

    /**
     * Test of getHttpRequestMethod method, of class
     * SynchronousResponseBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpRequestMethod() throws Exception {
        System.out.println("getHttpRequestMethod");
        String expResult = "POST";
        String body = instance.getBody(in);
        String result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestContextPath method, of class
     * SynchronousResponseBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpRequestContextPath() throws Exception {
        System.out.println("getHttpRequestContextPath");
        String body = instance.getBody(in);
        String expResult = "path";
        String result = instance.getHttpRequestContextPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpResponseHeaders method, of class
     * SynchronousResponseBodyExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHttpResponseHeaders() throws Exception {
        System.out.println("getHttpResponseHeaders");
        String body = instance.getBody(in);
        int expResult = 1;
        HttpHeaderManager result = instance.getHttpResponseHeaders();
        assertEquals(expResult, result.getFieldNames().size());
        String expStrResult = "v2";
        String headerValue = result.getHttpHeaderValue("hresp1");
        assertEquals(expStrResult, headerValue);
    }

    @Test
    public void testRealTransmitterLogC() throws Exception {
        System.out.println("realTransmitterLog");
        String expResult = "<Bundle";
        String result = instance.getBody(new FileInputStream("src/test/resources/get_care_record_ADM_3.msg_20170829093302.402.msg"));
        assertTrue(result.contains(expResult));

        expResult = "POST";
        result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);

        HttpHeaderManager httpRequestHeaders = instance.getHttpRequestHeaders();
        int intExpResult = 11;
        ArrayList<String> fieldNames = httpRequestHeaders.getFieldNames();
        assertEquals(intExpResult, fieldNames.size());

        HttpHeaderManager httpResponseHeaders = instance.getHttpResponseHeaders();
        intExpResult = 12;
        fieldNames = httpResponseHeaders.getFieldNames();
        assertEquals(intExpResult, fieldNames.size());
    }

    @Test
    public void testRealTransmitterLogChunkedResponse() throws Exception {
        System.out.println("realTransmitterLogChunkedResponse");
        String expResult = "<Bundle";
        String result = instance.getBody(new FileInputStream("src/test/resources/get_care_record_ADM_3.msg_20170829093302.402.msg"));
        assertTrue(result.contains(expResult));

        expResult = "POST";
        result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);
    }

    @Test
    public void testRealTransmitterLogJsonResponse() throws Exception {
        if (ConditionalCompilationControls.LOG_RAW_RESPONSE) {
            System.out.println("realTransmitterLogJsonResponse");
            // this is also a chunked response
            String expResult = "<Bundle";
            String result = instance.getBody(new FileInputStream("src/test/resources/get_care_record_SUM_2.msg_20171004143724.445.msg"));
            assertTrue(result.contains(expResult));

            expResult = "POST";
            result = instance.getHttpRequestMethod();
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testRealTransmitterLogGzipResponse() throws Exception {
        if (ConditionalCompilationControls.LOG_RAW_RESPONSE) {
            System.out.println("realTransmitterLogGzipResponse");
            // this is also a chunked response
            String expResult = "<Bundle";
            String result = instance.getBody(new FileInputStream("src/test/resources/get_care_record_SUM_9.msg_20171004154331.320.msg"));
            assertTrue(result.contains(expResult));

            expResult = "POST";
            result = instance.getHttpRequestMethod();
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testRealSimulatorLog() throws Exception {
        System.out.println("realSimulatorLog");
        String expResult = "<soap:Envelope";
        String result = instance.getBody(new FileInputStream("src/test/resources/127.0.0.1_sent_4132EE35-F76F-4BEB-9F4E-250AA42AE2C3_at_20171005083844.688.msg"));
        assertTrue(result.contains(expResult));

        expResult = "POST";
        result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);

        HttpHeaderManager httpRequestHeaders = instance.getHttpRequestHeaders();
        int intExpResult = 6;
        ArrayList<String> fieldNames = httpRequestHeaders.getFieldNames();
        assertEquals(intExpResult, fieldNames.size());

        HttpHeaderManager httpResponseHeaders = instance.getHttpResponseHeaders();
        intExpResult = 3;
        fieldNames = httpResponseHeaders.getFieldNames();
        assertEquals(intExpResult, fieldNames.size());
    }
}
