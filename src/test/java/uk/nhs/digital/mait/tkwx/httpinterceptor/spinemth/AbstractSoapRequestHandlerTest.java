/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.httpinterceptor.spinemth;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;

/**
 *
 * @author simonfarrow
 */
public class AbstractSoapRequestHandlerTest {

    private AbstractSoapRequestHandler instance;
    private String mStr;
    private Document document;

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
        mStr = "<a><b>xxx</b></a>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(mStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     */
    @Test
    public void testExtractHeader() throws Exception {
        System.out.println("extractHeader");
        instance.getHeader = getXpathExtractor("/a/b");
        String expResult = "xxx";
        Node result = instance.extractHeader(document);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractBody method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractBody() throws Exception {
        System.out.println("extractBody");
        String expResult = "xxx";
        instance.getBody = getXpathExtractor("/a/b");
        Node result = instance.extractBody(document);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractSoapRequest method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractSoapRequest() throws Exception {
        System.out.println("extractSoapRequest");
        instance.getSoapRequest = getXpathExtractor("/a/b");
        String expResult = "xxx";
        Node result = instance.extractSoapRequest(document);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractMessageId method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractMessageId() throws Exception {
        System.out.println("extractMessageId");
        instance.getMessageID = getXpathExtractor("/a/b");
        String expResult = "xxx";
        String result = instance.extractMessageId(mStr);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractReplyTo method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractReplyTo() throws Exception {
        System.out.println("extractReplyTo");
        instance.getReplyTo = getXpathExtractor("/a/b");
        String expResult = "xxx";
        String result = instance.extractReplyTo(mStr);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractFaultTo method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractFaultTo() throws Exception {
        System.out.println("extractFaultTo");
        instance.getFaultTo = getXpathExtractor("/a/b");
        String expResult = "xxx";
        String result = instance.extractFaultTo(mStr);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractNodeXpath method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractNodeXpath() throws Exception {
        System.out.println("extractNodeXpath");
        XPathExpression x = getXpathExtractor("/a/b");
        InputSource m = new InputSource(new ByteArrayInputStream(mStr.getBytes()));
        String expResult = "xxx";
        Node result = instance.extractNodeXpath(x, m);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractStringXpath method, of class AbstractSoapRequestHandler.
     */
    @Test
    public void testExtractStringXpath() throws Exception {
        System.out.println("extractStringXpath");
        XPathExpression x = getXpathExtractor("/a/b");
        InputSource m = new InputSource(new ByteArrayInputStream(mStr.getBytes()));
        String expResult = "xxx";
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
     * Test of getSynchronousWrapper method, of class
     * AbstractSoapRequestHandler.
     */
    @Test
    public void testGetSynchronousWrapper() {
        System.out.println("getSynchronousWrapper");
        String expResult = null;
        String result = instance.getSynchronousWrapper();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSavedMessagesDirectory method, of class
     * AbstractSoapRequestHandler.
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
        public void handle(String arg0, String arg1, HttpRequest arg2, HttpResponse arg3) throws HttpException {
        }
    }

}
