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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 *
 * @author sifa2
 */
public class HttpRequestTest {

    private static HttpRequest instance;
    private InputStream istream;
    private OutputStream ostream;

    private static final String TEST_METADATA_FILE = "src/test/resources/test.metadata";

    public HttpRequestTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new HttpRequest("id");
        istream = new ByteArrayInputStream("1234567890".getBytes());
        ostream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws IOException {
        ostream.close();
        istream.close();
    }

    /**
     * Test of readyToRead method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadyToRead() throws Exception {
        System.out.println("readyToRead");
        instance.setInputStream(istream);
        instance.readyToRead();
    }

    /**
     * Test of getInputStream method, of class HttpRequest.
     */
    @Test
    public void testGetInputStream() {
        System.out.println("getInputStream");
        instance.setInputStream(istream);
        InputStream result = instance.getInputStream();
        assertNotNull(result);
    }

    /**
     * Test of getRemoteAddr method, of class HttpRequest.
     */
    @Test
    public void testGetRemoteAddr() {
        System.out.println("getRemoteAddr");
        try {
            String expResult = "127.0.0.1";
            InetAddress inetAddress = InetAddress.getByName(expResult);
            instance.setRemoteAddress(inetAddress);
            String result = instance.getRemoteAddr();
            assertEquals(expResult, result);
        } catch (UnknownHostException ex) {
            fail("InetAddress cannot be created");
        }
    }

    /**
     * Test of setResponse method, of class HttpRequest.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testSetResponse() throws IOException {
        System.out.println("setResponse");
        HttpResponse r = new HttpResponse(ostream);
        instance.setResponse(r);
        ostream.close();
    }

    /**
     * Test of getResponse method, of class HttpRequest.
     */
    @Test
    public void testGetResponse() {
        System.out.println("getResponse");
        HttpResponse expResult = null;
        HttpResponse result = instance.getResponse();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoteAddress method, of class HttpRequest.
     */
    @Test
    public void testSetRemoteAddress() {
        System.out.println("setRemoteAddress");
        try {
            String expResult = "127.0.0.1";
            InetAddress inetAddress = InetAddress.getByName(expResult);
            instance.setRemoteAddress(inetAddress);
        } catch (UnknownHostException ex) {
            fail("InetAddress cannot be created");
        }
    }

    /**
     * Test of setHeader method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetHeader() throws Exception {
        System.out.println("setHeader");
        String h = "h1";
        String v = "v1";
        instance.setHeader(h, v);
    }

    /**
     * Test of setContentLength method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetContentLength() throws Exception {
        System.out.println("setContentLength");
        int c = 0;
        instance.setContentLength(c);
    }

    /**
     * Test of getFieldNames method, of class HttpRequest.
     */
    @Test
    public void testGetFieldNames() {
        System.out.println("getFieldNames");
        ArrayList<String> expResult = new ArrayList<>();
        ArrayList<String> result = instance.getFieldNames();
        assertEquals(expResult, result);
    }

    /**
     * Test of getField method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetField() throws Exception {
        System.out.println("getField");
        String h = "h1";
        String expResult = "v1";
        instance.setHeader(h, expResult);
        String result = instance.getField(h);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSourceId method, of class HttpRequest.
     */
    @Test
    public void testGetSourceId() {
        System.out.println("getSourceId");
        String expResult = "id";
        String result = instance.getSourceId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContext method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetContext() throws Exception {
        System.out.println("getContext");
        String expResult = "aa";
        instance.setRequestContext(expResult);
        String result = instance.getContext();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHandled method, of class HttpRequest.
     */
    @Test
    public void testSetHandled() {
        System.out.println("setHandled");
        boolean b = false;
        instance.setHandled(b);
    }

    /**
     * Test of isHandled method, of class HttpRequest.
     */
    @Test
    public void testIsHandled() {
        System.out.println("isHandled");
        boolean expResult = false;
        boolean result = instance.isHandled();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContentLength method, of class HttpRequest.
     */
    @Test
    public void testGetContentLength() throws Exception {
        System.out.println("getContentLength");
        int expResult = 99;
        instance.setContentLength(expResult);
        int result = instance.getContentLength();
        assertEquals(expResult, result);
    }

    /**
     * Test of contentLengthSet method, of class HttpRequest.
     */
    @Test
    public void testContentLengthSet() {
        System.out.println("contentLengthSet");
        boolean expResult = false;
        boolean result = instance.contentLengthSet();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMethod method, of class HttpRequest.
     */
    @Test
    public void testGetMethod() {
        System.out.println("getMethod");
        String expResult = null;
        String result = instance.getRequestType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVersion method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetVersion() throws Exception {
        System.out.println("getVersion");
        String expResult = "HTTP/1.1";
        instance.setProtocol(expResult);
        String result = instance.getVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of close method, of class HttpRequest.
     */
    @Test
    public void testClose() {
        System.out.println("close");
        instance.setInputStream(istream);
        instance.close();
    }

    /**
     * Test of setProtocol method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetProtocol() throws Exception {
        System.out.println("setProtocol");
        String p = "HTTP/1.1";
        instance.setProtocol(p);
    }

    /**
     * Test of setRequestType method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetRequestType() throws Exception {
        System.out.println("setRequestType");
        String r = "";
        instance.setRequestType(r);
    }

    /**
     * Test of setRequestContext method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetRequestContext() throws Exception {
        System.out.println("setRequestContext");
        String c = "context";
        instance.setRequestContext(c);
    }

    /**
     * Test of setInputStream method, of class HttpRequest.
     */
    @Test
    public void testSetInputStream() {
        System.out.println("setInputStream");
        instance.setInputStream(istream);
    }

    /**
     * Test of updateHeader method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateHeader() throws Exception {
        System.out.println("updateHeader");
        String h = "h1";
        String v = "v1";
        instance.setHeader(h, v);
        v = "v2";
        instance.updateHeader(h, v);
    }

    /**
     * Test of setBadRequestReason method, of class HttpRequest.
     */
    @Test
    public void testSetBadRequestReason() {
        System.out.println("setBadRequestReason");
        String s = "bad request";
        instance.setBadRequestReason(s);
    }

    /**
     * Test of getBadRequestReason method, of class HttpRequest.
     */
    @Test
    public void testGetBadRequestReason() {
        System.out.println("getBadRequestReason");
        String expResult = "bad request";
        instance.setBadRequestReason(expResult);
        String result = instance.getBadRequestReason();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasBadRequestReason method, of class HttpRequest.
     */
    @Test
    public void testHasBadRequestReason() {
        System.out.println("hasBadRequestReason");
        boolean expResult = false;
        boolean result = instance.hasBadRequestReason();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBody method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        byte[] expResult = "1234567890".getBytes();
        instance.setInputStream(istream);
        instance.setContentLength(10);
        byte[] result = instance.getBody();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of log method, of class HttpRequest.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLog() throws Exception {
        System.out.println("log");
        try ( LoggingFileOutputStream logfile = new LoggingFileOutputStream("fw")) {
            byte[] buffer = new byte[255];
            String msg = "abc123";
            instance.setRequestType("POST");
            instance.setRequestContext("rqcontext");
            instance.setLoggingFileOutputStream(logfile);
            instance.log(buffer, msg.getBytes());
        }
        new File("fw").delete();
    }

    /**
     * Test of getRemoteAddrName method, of class HttpRequest.
     */
    @Test
    public void testGetRemoteAddrName() {
        System.out.println("getRemoteAddrName");
        try {
            String expResult = "localhost";
            String ip = "127.0.0.1";
            InetAddress inetAddress = InetAddress.getByName(ip);
            instance.setRemoteAddress(inetAddress);
            String result = instance.getRemoteAddrName();
            assertNotNull(result);
            assertTrue(result.length() > 0);
        } catch (UnknownHostException ex) {
            fail("InetAddress cannot be created");
        }
    }

    /**
     * Test of getRequestType method, of class HttpRequest.
     */
    @Test
    public void testGetRequestType() {
        System.out.println("getRequestType");
        try {
            String expResult = "POST";
            instance.setRequestType(expResult);
            String result = instance.getRequestType();
            assertEquals(expResult, result);
        } catch (Exception ex) {
            Logger.getLogger(HttpRequestTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of setLoggingFileOutputStream method, of class HttpRequest.
     */
    @Test
    public void testSetLoggingFileOutputStream() {
        System.out.println("setLoggingFileOutputStream");
        LoggingFileOutputStream lfos = null;
        try {
            lfos = new LoggingFileOutputStream(TEST_METADATA_FILE);
            instance.setLoggingFileOutputStream(lfos);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HttpRequestTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                lfos.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpRequestTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Test of getLoggingFileOutputStream method, of class HttpRequest.
     */
    @Test
    public void testGetLoggingFileOutputStream() {
        System.out.println("getLoggingFileOutputStream");
        LoggingFileOutputStream lfos = null;
        try {
            lfos = new LoggingFileOutputStream(TEST_METADATA_FILE);
            instance.setLoggingFileOutputStream(lfos);
            LoggingFileOutputStream result = instance.getLoggingFileOutputStream();
            assertEquals(TEST_METADATA_FILE, result.getFileName());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HttpRequestTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                lfos.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpRequestTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Test of getHeaderManager method, of class HttpRequest.
     */
    @Test
    public void testGetHeaderManager() {
        System.out.println("getHeaderManager");
        HttpHeaderManager result = instance.getHeaderManager();
        assertNotNull(result);
    }
}
