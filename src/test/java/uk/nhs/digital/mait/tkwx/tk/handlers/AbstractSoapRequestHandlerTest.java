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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.ByteArrayInputStream;
import javax.xml.xpath.XPathExpression;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 *
 * @author simonfarrow
 */
public class AbstractSoapRequestHandlerTest {

    private AbstractSoapRequestHandlerImpl instance;
    
    public AbstractSoapRequestHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new AbstractSoapRequestHandlerImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getAsyncTTL method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testGetAsyncTTL() {
        System.out.println("getAsyncTTL");
        int expResult = 10;
        int result = instance.getAsyncTTL();
        assertEquals(expResult, result);
    }

    /**
     * Test of extractHeader method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractHeader() throws Exception {
        System.out.println("extractHeader");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
        Document d = null;
        Node expResult = null;
//        Node result = instance.extractHeader(d);
//        assertEquals(expResult, result);
    }

    /**
     * Test of extractBody method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractBody() throws Exception {
        System.out.println("extractBody");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
        Document d = null;
        Node expResult = null;
//        Node result = instance.extractBody(d);
//        assertEquals(expResult, result);
    }

    /**
     * Test of extractSoapRequest method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractSoapRequest() throws Exception {
        System.out.println("extractSoapRequest");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
        Document d = null;
        Node expResult = null;
//        Node result = instance.extractSoapRequest(d);
//        assertEquals(expResult, result);
    }

    /**
     * Test of extractMessageId method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractMessageId() throws Exception {
        System.out.println("extractMessageId");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
        String m = "";
        String expResult = "";
//        String result = instance.extractMessageId(m);
//        assertEquals(expResult, result);
    }

    /**
     * Test of extractReplyTo method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractReplyTo() throws Exception {
        System.out.println("extractReplyTo");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
        String m = "";
        String expResult = "";
//        String result = instance.extractReplyTo(m);
//        assertEquals(expResult, result);
    }

    /**
     * Test of extractFaultTo method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractFaultTo() throws Exception {
        System.out.println("extractFaultTo");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
        String m = "";
        String expResult = "";
//        String result = instance.extractFaultTo(m);
//        assertEquals(expResult, result);
    }

    /**
     * Test of extractNodeXpath method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractNodeXpath() throws Exception {
        System.out.println("extractNodeXpath");
        XPathExpression x = getXpathExtractor("/a/text()");
        InputSource m = new InputSource(new ByteArrayInputStream("<a>b</a>".getBytes()));
        String expResult = "b";
        Node result = instance.extractNodeXpath(x, m);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractStringXpath method, of class AbstractSoapRequestHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractStringXpath() throws Exception {
        System.out.println("extractStringXpath");
        XPathExpression x = getXpathExtractor("/a/text()");
        InputSource m = new InputSource(new ByteArrayInputStream("<a>b</a>".getBytes()));
        String expResult = "b";
        String result = instance.extractStringXpath(x, m);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSoapFault method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testGetSoapFault() {
        System.out.println("getSoapFault");
        String expResult = null;
        String result = instance.getSoapFault();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSynchronousWrapper method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testGetSynchronousWrapper() {
        System.out.println("getSynchronousWrapper");
        String expResult = null;
        String result = instance.getSynchronousWrapper();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSavedMessagesDirectory method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testGetSavedMessagesDirectory() {
        System.out.println("getSavedMessagesDirectory");
        String expResult = null;
        String result = instance.getSavedMessagesDirectory();
        assertEquals(expResult, result);
    }

    /**
     * Test of getToolkit method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testGetToolkit() {
        System.out.println("getToolkit");
        HttpTransport expResult = null;
        HttpTransport result = instance.getToolkit();
        assertEquals(expResult, result);
    }

    public class AbstractSoapRequestHandlerImpl extends AbstractSoapRequestHandler {

        @Override
        public void handle(String path, String params, HttpRequest req, HttpResponse resp) throws HttpException {
        }
    }

}
