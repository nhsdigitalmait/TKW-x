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
import java.io.ByteArrayOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;

/**
 *
 * @author simonfarrow
 */
public class HttpRequestBodyExtractorAdapterTest {
    private HttpRequestBodyExtractorAdapter instance;
    private ByteArrayInputStream istream;
    private ByteArrayOutputStream ostream;
    
    public HttpRequestBodyExtractorAdapterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        istream = new ByteArrayInputStream("1234567890".getBytes());
        ostream = new ByteArrayOutputStream();

        HttpRequest rq = new HttpRequest("id");
        rq.setContentLength(10);
        rq.setInputStream(istream);
        rq.setRequestType("POST");
        rq.setRequestContext("/");
        rq.setHeader("h1","v1");
        instance = new HttpRequestBodyExtractorAdapter(rq);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getBody method, of class HttpRequestBodyExtractorAdapter.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        String expResult = "1234567890";
        String result = instance.getBody(istream);
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestHeaders method, of class HttpRequestBodyExtractorAdapter.
     */
    @Test
    public void testGetHttpRequestHeaders() {
        System.out.println("getHttpRequestHeaders");
        String expResult = "v1";
        HttpHeaderManager result = instance.getHttpRequestHeaders();
        assertEquals(expResult, result.getHttpHeaderValue("h1"));
    }

    /**
     * Test of getHttpResponseHeaders method, of class HttpRequestBodyExtractorAdapter.
     */
    @Test
    public void testGetHttpResponseHeaders() {
        System.out.println("getHttpResponseHeaders");
        HttpHeaderManager expResult = null;
        HttpHeaderManager result = instance.getHttpResponseHeaders();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestContextPath method, of class HttpRequestBodyExtractorAdapter.
     */
    @Test
    public void testGetHttpRequestContextPath() {
        System.out.println("getHttpRequestContextPath");
        String expResult = "/";
        String result = instance.getHttpRequestContextPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpRequestMethod method, of class HttpRequestBodyExtractorAdapter.
     */
    @Test
    public void testGetHttpRequestMethod() {
        System.out.println("getHttpRequestMethod");
        String expResult = "POST";
        String result = instance.getHttpRequestMethod();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRelevantHttpHeaders method, of class HttpRequestBodyExtractorAdapter.
     */
    @Test
    public void testGetRelevantHttpHeaders() {
        System.out.println("getRelevantHttpHeaders");
        HttpHeaderManager expResult = instance.getHttpRequestHeaders();
        HttpHeaderManager result = instance.getRelevantHttpHeaders();
        assertEquals(expResult, result);
    }
    
}
