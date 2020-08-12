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
package uk.nhs.digital.mait.tkwx.jsonconverter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter.XMLTOJSONCONVERTERATTRIBUTEMAXLENGTH;

/**
 *
 * @author sifa2
 */
public class JsonXmlConverterTest {
    private final static String S70 = "1234567890123456789012345678901234567890123456789012345678901234567890";

    private final static char[] JSON = ("{\n"
            + "	\"Org\": \""+S70+"\",\n"
            + "	\"SvcIA\": \"urn:nhs:names:services:itk:COPC_IN000001GB01\",\n"
            + "	\"Service\": \"urn:nhs:names:services:itk\",\n"
            + "	\"InteractionId\": \"COPC_IN000001GB01\",\n"
            + "	\"RetryInterval\": 10,\n"
            + "	\"PersistDuration\": 50,\n"
            + "	\"Retries\": 3,\n"
            + "	\"Url\": \"http://localhost:xxxx/reliablemessaging/intermediary\",\n"
            + "	\"Asid\": [\"000000000000\"],\n"
            + "	\"PartyKey\": \"AAA-123456\",\n"
            + "	\"CPAid\": \"S3024519A3110234\",\n"
            + "	\"SyncReply\": \"None\",\n"
            + "	\"IsSynchronous\": true,\n"
            + "	\"DuplicateElimination\": \"always\",\n"
            + "	\"AckRequested\": \"always\",\n"
            + "	\"SoapActor\": \"urn:oasis:names:tc:ebxml-msg:actor:toPartyMSH\"\n"
            + "}").toCharArray();

    public JsonXmlConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of jsonToXmlString method, of class JsonXmlConverter.
     * @throws java.lang.Exception
     */
    @Test
    public void testJsonToXmlString() throws Exception {
        System.out.println("jsonToXmlString");
        String result = JsonXmlConverter.jsonToXmlString(JSON);
        assertNotNull(result);
        
        assertTrue(result.startsWith("<?xml version=\"1.0\""));
    }

    /**
     * Test of jsonToXmlDocument method, of class JsonXmlConverter.
     * @throws java.lang.Exception
     */
    @Test
    public void testJsonToXmlDocument() throws Exception {
        System.out.println("jsonToXmlDocument");
        
        // set the upper limit to 80 for the final test
        System.setProperty(XMLTOJSONCONVERTERATTRIBUTEMAXLENGTH,"80");
        Document result = JsonXmlConverter.jsonToXmlDocument(JSON);
        assertNotNull(result);
        
        Element root = result.getDocumentElement();
        String expResult ="json";
        String strResult = root.getTagName();
        assertEquals(expResult,strResult);
        
        strResult = root.getAttribute("IsSynchronous");
        expResult = "true";
        assertEquals(expResult,strResult);

        // This blows the 64 char limit so the text is rendered in a text element instead
        // other conditions can cause this eg the presence xml special characters which can't be rendered in attributes
        strResult = root.getAttribute("Org");
        expResult = S70;
        assertEquals(expResult,strResult);
    }
}
