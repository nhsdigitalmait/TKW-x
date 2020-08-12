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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author sifa2
 */
public class SpineMessageTest {

    private SpineMessage instance;

    public SpineMessageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new SpineMessage("src/test/resources", "ITK_Trunk.message");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getEbXmlDoc method, of class SpineMessage.
     */
    @Test
    public void testGetEbXmlDoc() {
        System.out.println("getEbXmlDoc");
        String expResult = "SOAP:Envelope";
        Document result = instance.getEbXmlDoc();
        assertEquals(expResult, result.getDocumentElement().getTagName());
    }

    /**
     * Test of getHL7Doc method, of class SpineMessage.
     */
    @Test
    public void testGetHL7Doc() {
        System.out.println("getHL7Doc");
        String expResult = "COPC_IN000001GB01";
        Document result = instance.getHL7Doc();
        assertEquals(expResult, result.getDocumentElement().getTagName());
    }

    /**
     * Test of getSOAPDoc method, of class SpineMessage.
     */
    @Test
    public void testGetSOAPDoc() {
        System.out.println("getSOAPDoc");
        String expResult = "COPC_IN000001GB01";
        Document result = instance.getSOAPDoc();
        assertEquals(expResult, result.getDocumentElement().getTagName());
    }

    /**
     * Test of getSOAPIncludedInHL7Part method, of class SpineMessage.
     */
    @Test
    public void testGetSOAPIncludedInHL7Part() {
        System.out.println("getSOAPIncludedInHL7Part");
        Boolean expResult = false;
        Boolean result = instance.getSOAPIncludedInHL7Part();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSOAPIncludedInHL7Part method, of class SpineMessage.
     */
    @Test
    public void testSetSOAPIncludedInHL7Part() {
        System.out.println("setSOAPIncludedInHL7Part");
        Boolean s = null;
        instance.setSOAPIncludedInHL7Part(s);
    }

    /**
     * Test of getPresentedFile method, of class SpineMessage.
     */
    @Test
    public void testGetPresentedFile() {
        System.out.println("getPresentedFile");
        String expResult = "POST";
        String result = instance.getPresentedFile();
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getEbXmlPart method, of class SpineMessage.
     */
    @Test
    public void testGetEbXmlPart() {
        System.out.println("getEbXmlPart");
        String expResult = "SOAP:Envelope";
        String result = instance.getEbXmlPart();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of getHL7Part method, of class SpineMessage.
     */
    @Test
    public void testGetHL7Part() {
        System.out.println("getHL7Part");
        String expResult = "<?xml version";
        String result = instance.getHL7Part();
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getSoap method, of class SpineMessage.
     */
    @Test
    public void testGetSoap() {
        System.out.println("getSoap");
        String expResult = "<?xml version";
        String result = instance.getSoap();
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getService method, of class SpineMessage.
     */
    @Test
    public void testGetService() {
        System.out.println("getService");
        String expResult = "COPC_IN000001GB01";
        String result = instance.getService();
        assertEquals(expResult, result);
    }

    /**
     * Test of getATTACHMENTDoc method, of class SpineMessage.
     */
    @Test
    public void testGetATTACHMENTDoc() {
        System.out.println("getATTACHMENTDoc");
        int i = 0;
        Document expResult = null;
        Document result = instance.getATTACHMENTDoc(i);
        assertEquals(expResult, result);
    }

    /**
     * Test of getATTACHMENTPart method, of class SpineMessage.
     */
    @Test
    public void testGetATTACHMENTPart() {
        System.out.println("getATTACHMENTPart");
        int i = 0;
        String expResult = "\n<itk:DistributionEnvelope";
        String result = instance.getATTACHMENTPart(i);
        assertTrue(result.startsWith(expResult));
    }

}
