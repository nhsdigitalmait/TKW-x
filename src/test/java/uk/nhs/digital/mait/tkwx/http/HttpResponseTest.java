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
package uk.nhs.digital.mait.tkwx.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sifa2
 */
public class HttpResponseTest {

    private HttpResponse instance;
    private OutputStream ostream;

    public HttpResponseTest() {
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
        instance = new HttpResponse(ostream);
    }

    @After
    public void tearDown() throws IOException {
        ostream.close();
    }

    /**
     * Test of setContentType method, of class HttpResponse.
     */
    @Test
    public void testSetContentType() {
        System.out.println("setContentType");
        String c = "text/plain";
        instance.setContentType(c);
    }

    /**
     * Test of getOutputStream method, of class HttpResponse.
     */
    @Test
    public void testGetOutputStream() {
        System.out.println("getOutputStream");
        OutputStream result = instance.getOutputStream();
        assertNotNull(result);
    }

    /**
     * Test of getContentType method, of class HttpResponse.
     */
    @Test
    public void testGetContentType() {
        System.out.println("getContentType");
        String expResult = "text/xml";
        instance.setContentType(expResult);
        String result = instance.getContentType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setField method, of class HttpResponse.
     */
    @Test
    public void testSetField() {
        System.out.println("setField");
        String f = "f1";
        String v = "v1";
        instance.setField(f, v);
    }

    /**
     * Test of canWrite method, of class HttpResponse.
     */
    @Test
    public void testCanWrite() {
        System.out.println("canWrite");
        instance.canWrite();
    }

    /**
     * Test of isResponseReady method, of class HttpResponse.
     */
    @Test
    public void testIsResponseReady() {
        System.out.println("isResponseReady");
        boolean expResult = false;
        boolean result = instance.isResponseReady();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpHeader method, of class HttpResponse.
     */
    @Test
    public void testGetHttpHeader() {
        System.out.println("getHttpHeader");
        String expResult = null;
        String result = instance.getHttpHeader();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasHttpHeader method, of class HttpResponse.
     */
    @Test
    public void testHasHttpHeader() {
        System.out.println("hasHttpHeader");
        boolean b = false;
        instance.hasHttpHeader(b);
    }

    /**
     * Test of getHttpBuffer method, of class HttpResponse.
     */
    @Test
    public void testGetHttpBuffer() {
        System.out.println("getHttpBuffer");
        byte[] result = instance.getHttpBuffer();
        assertNull(result);
    }

    /**
     * Test of flush method, of class HttpResponse.
     */
    @Test
    public void testFlush() {
        System.out.println("flush");
        instance.flush();
    }

    /**
     * Test of setStatusText method, of class HttpResponse.
     */
    @Test
    public void testSetStatusText() {
        System.out.println("setStatusText");
        String s = "";
        instance.setStatusText(s);
    }

    /**
     * Test of forceClose method, of class HttpResponse.
     */
    @Test
    public void testForceClose() {
        System.out.println("forceClose");
        instance.forceClose();
    }

    /**
     * Test of setStatus method, of class HttpResponse.
     */
    @Test
    public void testSetStatus_int() {
        System.out.println("setStatus");
        int s = 0;
        instance.setStatus(s);
    }

    /**
     * Test of setStatus method, of class HttpResponse.
     */
    @Test
    public void testSetStatus_int_String() {
        System.out.println("setStatus");
        int i = 0;
        String s = "";
        instance.setStatus(i, s);
    }

}
