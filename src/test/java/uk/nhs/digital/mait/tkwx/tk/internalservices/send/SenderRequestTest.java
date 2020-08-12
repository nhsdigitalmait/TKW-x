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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;

/**
 *
 * @author sifa2
 */
public class SenderRequestTest {

    private SenderRequest instance;
    
    public SenderRequestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new SenderRequest("payload","wrapper","address");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setOriginalFileName method, of class SenderRequest.
     */
    @Test
    public void testSetOriginalFileName() {
        System.out.println("setOriginalFileName");
        String s = "";
        instance.setOriginalFileName(s);
    }

    /**
     * Test of getOriginalFileName method, of class SenderRequest.
     */
    @Test
    public void testGetOriginalFileName() {
        System.out.println("getOriginalFileName");
        String expResult = "ofn";
        instance.setOriginalFileName(expResult);
        String result = instance.getOriginalFileName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setChunkSize method, of class SenderRequest.
     */
    @Test
    public void testSetChunkSize() {
        System.out.println("setChunkSize");
        int c = 0;
        instance.setChunkSize(c);
    }

    /**
     * Test of setRelatesTo method, of class SenderRequest.
     */
    @Test
    public void testSetRelatesTo() {
        System.out.println("setRelatesTo");
        String r = "";
        instance.setRelatesTo(r);
    }

    /**
     * Test of setAction method, of class SenderRequest.
     */
    @Test
    public void testSetAction() {
        System.out.println("setAction");
        String a = "";
        instance.setAction(a);
    }

    /**
     * Test of getAction method, of class SenderRequest.
     */
    @Test
    public void testGetAction() {
        System.out.println("getAction");
        String expResult = "action";
        instance.setAction(expResult);
        String result = instance.getAction();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRelatesTo method, of class SenderRequest.
     */
    @Test
    public void testGetRelatesTo() {
        System.out.println("getRelatesTo");
        String expResult = "rt";
        instance.setRelatesTo(expResult);
        String result = instance.getRelatesTo();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddress method, of class SenderRequest.
     */
    @Test
    public void testGetAddress() {
        System.out.println("getAddress");
        String expResult = "address";
        String result = instance.getAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPayload method, of class SenderRequest.
     */
    @Test
    public void testGetPayload() {
        System.out.println("getPayload");
        String expResult = "payload";
        String result = instance.getPayload();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWrapperTemplate method, of class SenderRequest.
     */
    @Test
    public void testGetWrapperTemplate() {
        System.out.println("getWrapperTemplate");
        String expResult = "wrapper";
        String result = instance.getWrapperTemplate();
        assertEquals(expResult, result);
    }

    /**
     * Test of chunkSize method, of class SenderRequest.
     */
    @Test
    public void testChunkSize() {
        System.out.println("chunkSize");
        int expResult = 0;
        int result = instance.chunkSize();
        assertEquals(expResult, result);
    }

    /**
     * Test of setODSCode method, of class SenderRequest.
     */
    @Test
    public void testSetODSCode() {
        System.out.println("setODSCode");
        String s = "";
        instance.setODSCode(s);
    }

    /**
     * Test of getODSCode method, of class SenderRequest.
     */
    @Test
    public void testGetODSCode() {
        System.out.println("getODSCode");
        String expResult = "ods";
        instance.setODSCode(expResult);
        String result = instance.getODSCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAsid method, of class SenderRequest.
     */
    @Test
    public void testSetAsid() {
        System.out.println("setAsid");
        String s = "";
        instance.setAsid(s);
    }

    /**
     * Test of getAsid method, of class SenderRequest.
     */
    @Test
    public void testGetAsid() {
        System.out.println("getAsid");
        String expResult = "asid";
        instance.setAsid(expResult);
        String result = instance.getAsid();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPartyKey method, of class SenderRequest.
     */
    @Test
    public void testSetPartyKey() {
        System.out.println("setPartyKey");
        String s = "";
        instance.setPartyKey(s);
    }

    /**
     * Test of getPartyKey method, of class SenderRequest.
     */
    @Test
    public void testGetPartyKey() {
        System.out.println("getPartyKey");
        String expResult = "pk";
        instance.setPartyKey(expResult);
        String result = instance.getPartyKey();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpMethod method, of class SenderRequest.
     */
    @Test
    public void testGetHttpMethod() {
        System.out.println("getHttpMethod");
        String expResult = "POST";
        String result = instance.getHttpMethod();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHttpMethod method, of class SenderRequest.
     */
    @Test
    public void testSetHttpMethod() {
        System.out.println("setHttpMethod");
        String httpMethod = "";
        instance.setHttpMethod(httpMethod);
    }

    /**
     * Test of setEvidenceMetaDataHandler method, of class SenderRequest.
     */
    @Test
    public void testSetEvidenceMetaDataHandler() {
        System.out.println("setEvidenceMetaDataHandler");
        EvidenceMetaDataHandler emdh = null;
        instance.setEvidenceMetaDataHandler(emdh);
    }

    /**
     * Test of getEvidenceMetaDataHandler method, of class SenderRequest.
     */
    @Test
    public void testGetEvidenceMetaDataHandler() {
        System.out.println("getEvidenceMetaDataHandler");
        EvidenceMetaDataHandler expResult = null;
        EvidenceMetaDataHandler result = instance.getEvidenceMetaDataHandler();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLoggingSubDir method, of class SenderRequest.
     */
    @Test
    public void testGetLoggingSubDir() {
        System.out.println("getLoggingSubDir");
        String expResult = "subdir";
        instance.setLoggingSubDir(expResult);
        String result = instance.getLoggingSubDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLoggingSubDir method, of class SenderRequest.
     */
    @Test
    public void testSetLoggingSubDir() {
        System.out.println("setLoggingSubDir");
        String subDir = "subdir";
        instance.setLoggingSubDir(subDir);
    }
    
}
