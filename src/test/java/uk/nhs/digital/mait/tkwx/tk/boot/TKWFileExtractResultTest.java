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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.FHIR_MESSAGING_JSON;

/**
 *
 * @author simonfarrow
 */
public class TKWFileExtractResultTest {

    private TKWFileExtractResult instance;
    
    public TKWFileExtractResultTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new TKWFileExtractResult("source", TKWFileType.FHIR_MESSAGING_JSON,"sourceIP","destIP",new Date(),"serviceID","requestBody");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toString method, of class TKWFileExtractResult.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "source,FHIR_MESSAGING_JSON,serviceID";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFileType method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetFileType() {
        System.out.println("getFileType");
        TKWFileType expResult = FHIR_MESSAGING_JSON;
        TKWFileType result = instance.getFileType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFileType method, of class TKWFileExtractResult.
     */
    @Test
    public void testSetFileType() {
        System.out.println("setFileType");
        TKWFileType fileType = null;
        instance.setFileType(fileType);
    }

    /**
     * Test of getRequestServiceID method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetRequestServiceID() {
        System.out.println("getRequestServiceID");
        String expResult = "serviceID";
        String result = instance.getRequestServiceID();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRequestServiceID method, of class TKWFileExtractResult.
     */
    @Test
    public void testSetRequestServiceID() {
        System.out.println("setRequestServiceID");
        String requestServiceID = "";
        instance.setRequestServiceID(requestServiceID);
    }

    /**
     * Test of getRequestBody method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetRequestBody() {
        System.out.println("getRequestBody");
        String expResult = "requestBody";
        String result = instance.getRequestBody();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRequestBody method, of class TKWFileExtractResult.
     */
    @Test
    public void testSetRequestBody() {
        System.out.println("setRequestBody");
        String requestBody = "requestBody";
        instance.setRequestBody(requestBody);
    }

    /**
     * Test of getResponseServiceID method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetResponseServiceID() {
        System.out.println("getResponseServiceID");
        String expResult = null;
        String result = instance.getResponseServiceID();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResponseServiceID method, of class TKWFileExtractResult.
     */
    @Test
    public void testSetResponseServiceID() {
        System.out.println("setResponseServiceID");
        String responseServiceID = "";
        instance.setResponseServiceID(responseServiceID);
    }

    /**
     * Test of getResponseBody method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetResponseBody() {
        System.out.println("getResponseBody");
        String expResult = null;
        String result = instance.getResponseBody();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResponseBody method, of class TKWFileExtractResult.
     */
    @Test
    public void testSetResponseBody() {
        System.out.println("setResponseBody");
        String responseBody = "";
        instance.setResponseBody(responseBody);
    }

    /**
     * Test of getFileSource method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetFileSource() {
        System.out.println("getFileSource");
        String expResult = "source";
        String result = instance.getFileSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFileSource method, of class TKWFileExtractResult.
     */
    @Test
    public void testSetFileSource() {
        System.out.println("setFileSource");
        String fileSource = "source";
        instance.setFileSource(fileSource);
    }

    /**
     * Test of getSourceIP method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetSourceIP() {
        System.out.println("getSourceIP");
        String expResult = "sourceIP";
        String result = instance.getSourceIP();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDestIP method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetDestIP() {
        System.out.println("getDestIP");
        String expResult = "destIP";
        String result = instance.getDestIP();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimestamp method, of class TKWFileExtractResult.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        Date result = instance.getTimestamp();
        assertNotNull(result);
    }
}
