/*
  Copyright 2012  Damian Murphy murff@warlock.org

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

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;

/**
 *
 * @author simonfarrow
 */
public class HttpHeaderManagerTest {

    private HttpHeaderManager instance;
    
    public HttpHeaderManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new HttpHeaderManager();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addHttpHeader method, of class HttpHeaderManager.
     */
    @Test
    public void testAddHttpHeader() {
        System.out.println("addHttpHeader");
        String name = "headerName";
        String value = "headervalue";
        instance.addHttpHeader(name, value);
        
        // make sure it doesn't fall over doing this
        instance.addHttpHeader(name, value);
    }

    /**
     * Test of getHttpHeader method, of class HttpHeaderManager.
     */
    @Test
    public void testGetHttpHeaderValue() {
        System.out.println("getHttpHeader");
        String name = "headerName";
        String expResult = "headervalue";
        instance.addHttpHeader(name, expResult);
        String result = instance.getHttpHeaderValue(name);
        assertEquals(expResult, result);

        name = "dne";
        expResult = null;
        result = instance.getHttpHeaderValue(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpHeaders method, of class HttpHeaderManager.
     */
    @Test
    public void testGetHttpHeaders() {
        System.out.println("getHttpHeaders");
        String name = "headerName";
        String value = "headervalue";
        instance.addHttpHeader(name, value);
        String expResult = "headerName: headervalue\r\n";
        String result = instance.getHttpHeaders();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of parseHttpHeaders method, of class HttpHeaderManager.
     * spec says there can be optional whitespace after the colon
     */
    @Test
    public void testParseHttpHeaders() {
        System.out.println("parseHttpHeaders");
        String logStr = "stuff HTTP/1.1\r\nhn1: hv1\r\nhn2:hv2\r\n";
        instance.parseHttpHeaders(logStr);

        String expResult = "hv1";
        String result = instance.getHttpHeaderValue("hn1");
        assertEquals(expResult, result);

        expResult = "hv2";
        result = instance.getHttpHeaderValue("hn2");
        assertEquals(expResult, result);
    }

    /**
     * Test of updateHeader method, of class HttpHeaderManager.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateHeader() throws Exception {
        System.out.println("updateHeader");
        String h = "h1";
        String v = "v1";
        instance.addHttpHeader(h, v);

        v = "v2";
        instance.updateHeader(h, v);
        
        String expResult = "v1v2";
        String result = instance.getHttpHeaderValue(h);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFieldNames method, of class HttpHeaderManager.
     */
    @Test
    public void testGetFieldNames() {
        System.out.println("getFieldNames");
        ArrayList<String> expResult = new ArrayList(Arrays.asList(new String[]{"h1","h2"}));
        instance.addHttpHeader("h1", "v1");
        instance.addHttpHeader("h2", "v2");
        ArrayList<String> result = instance.getFieldNames();
        assertEquals(expResult, result);
    }

    /**
     * Test of inCsv method, of class HttpHeaderManager.
     */
    @Test
    public void testInCsv() {
        System.out.println("inCsv");
        String headerName = "headername";
        String values = "headervalue,headervalue2";
        String value = "headervalue2";
        instance.addHttpHeader(headerName, values);
        boolean expResult = true;
        boolean result = instance.headerValueCsvIncludes(headerName.toUpperCase(), value.toUpperCase());
        assertEquals(expResult, result);
    }

    /**
     * Test of valueInCsv method, of class HttpHeaderManager.
     */
    @Test
    public void testValueInCsv() {
        System.out.println("valueInCsv");
     
        String fieldValue = "AA,BB,CC";
        String value = "bb";
        boolean expResult = true;
        boolean result = HttpHeaderManager.valueInCsv(fieldValue, value);
        assertEquals(expResult, result);

        value = "BB";
        result = HttpHeaderManager.valueInCsv(fieldValue, value);
        assertEquals(expResult, result);

        expResult = false;
        value = "BBC";
        result = HttpHeaderManager.valueInCsv(fieldValue, value);
        assertEquals(expResult, result);


    }

    /**
     * Test of headerValueCsvIncludes method, of class HttpHeaderManager.
     */
    @Test
    public void testHeaderValueCsvIncludes() {
        System.out.println("headerValueCsvIncludes");
        String name = "headerName";
        String value = "headervalue1, headervalue2";
        instance.addHttpHeader(name, value);
        boolean expResult = true;
        boolean result = instance.headerValueCsvIncludes(name, "headervalue1");
        assertEquals(expResult, result);

        result = instance.headerValueCsvIncludes(name, "headervalue2");
        assertEquals(expResult, result);
    }

    /**
     * Test of mungeForLogging method, of class HttpHeaderManager.
     */
    @Test
    public void testMungeForLogging() {
        System.out.println("mungeForLogging");
        instance.parseHttpHeaders("GET / HTTP/1.1\r\nTransfer-Encoding: chunked\r\nContent-Encoding: gzip\r\n\r\n");
        int contentLength = 99;
        instance.modifyHttpHeadersForLogging(contentLength);
        String expResult = "99";
        String result = instance.getHttpHeaderValue(CONTENT_LENGTH_HEADER);
        assertEquals(expResult, result);

        expResult = TRANSFER_ENCODING_CHUNKED;
        result = instance.getHttpHeaderValue("X-was-Transfer-Encoding");
        assertEquals(expResult, result);

        expResult = null;
        result = instance.getHttpHeaderValue(TRANSFER_ENCODING_HEADER);
        assertEquals(expResult, result);

        expResult = "gzip";
        result = instance.getHttpHeaderValue("X-was-Content-Encoding");
        assertEquals(expResult, result);

        expResult = null;
        result = instance.getHttpHeaderValue(CONTENT_ENCODING_HEADER);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFirstLine method, of class HttpHeaderManager.
     */
    @Test
    public void testGetFirstLine() {
        System.out.println("getFirstLine");
        instance.parseHttpHeaders("GET / HTTP/1.1\r\nHost: h\r\n\r\n");
        String expResult = "GET / HTTP/1.1\r\n";
        String result = instance.getFirstLine();
        assertEquals(expResult, result);
    }

    /**
     * Test of updateHeader method, of class HttpHeaderManager.
     */
    @Test
    public void testUpdateHeader_String_String() throws Exception {
         System.out.println("updateHeader");
        String h = "h1";
        String v = "v1";
        instance.addHttpHeader(h, v);

        v = "v2";
        instance.updateHeader(h, v);
        
        String expResult = "v1v2";
        String result = instance.getHttpHeaderValue(h);
        assertEquals(expResult, result);
    }

    /**
     * Test of updateHeader method, of class HttpHeaderManager.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateHeader_3args() throws Exception {
        System.out.println("updateHeader");
        String h = "h1";
        String v = "v1";
        instance.addHttpHeader(h, v);

        // NB This is effectively slightly broken if the case of the field name is not consistent
        // you can't express add ("h1","v1") 
        // add ("H1","v1") 
        // Neither H1: v1v2 nor h1: v1v2 is really correct 
        v = "v2";
        instance.updateHeader(h, v);
        
        String expResult = "v1v2";
        String result = instance.getHttpHeaderValue(h);
        assertEquals(expResult, result);
    }

    /**
     * Test of modifyHttpHeadersForLogging method, of class HttpHeaderManager.
     */
    @Test
    public void testModifyHttpHeadersForLogging() {
        System.out.println("modifyHttpHeadersForLogging");
        int contentLength = 0;
        instance.modifyHttpHeadersForLogging(contentLength);
    }

    /**
     * Test of setFirstLine method, of class HttpHeaderManager.
     */
    @Test
    public void testSetFirstLine() {
        System.out.println("setFirstLine");
        String firstLine = "";
        instance.setFirstLine(firstLine);
    }

}
