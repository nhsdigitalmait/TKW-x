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

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.xmlReformat;

/**
 *
 * @author sifa2
 */
public class JsonXmlConverterTest {
    private final static String S70 = "1234567890123456789012345678901234567890123456789012345678901234567890";

    private final static char[] JSON = ("{\n"
            + "	\"Org\": \"" + S70 + "\",\n"
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

    private final static char[] JSON_JWT = ("{\n"
            + "    \"iss\": \"https://ConsumerSystemURL\",\n"
            + "    \"sub\": \"1\",\n"
            + "    \"aud\": \"__NHS_NUMBER__\",\n"
            + "	\"exp\": 1,\n"
            + "	\"iat\": 2,\n"
            + "    \"reason_for_request\": \"directcare\",\n"
            + "    \"requested_scope\": \"organization/*.read\",\n"
            + "    \"requesting_device\": {\n"
            + "        \"resourceType\": \"Device\",\n"
            + "        \"id\": \"1\",\n"
            + "        \"identifier\": [{\n"
            + "              \"system\": \"https://consumersupplier.com/Id/device-identifier\",\n"
            + "              \"value\": \"CONS-APP-4\"\n"
            + "            }],\n"
            + "        \"model\": \"Consumer product name\",\n"
            + "        \"version\": \"1.1\"\n"
            + "    },\n"
            + "    \"requesting_organization\": {\n"
            + "        \"resourceType\": \"Organization\",\n"
            + "        \"identifier\": [{\n"
            + "                \"system\": \"https://fhir.nhs.uk/Id/ods-organization-code\",\n"
            + "                \"value\": \"GPCA0001\"\n"
            + "            }],\n"
            + "		\"name\": \"Emergency Booking Assurance\"\n"
            + "    },\n"
            + "    \"requesting_practitioner\": {\n"
            + "        \"resourceType\": \"Practitioner\",\n"
            + "        \"id\": \"1\",\n"
            + "        \"identifier\": [{\n"
            + "                \"system\": \"https://fhir.nhs.uk/Id/sds-user-id\",\n"
            + "                \"value\": \"__PRACTITIONER_ID__\"\n"
            + "            },\n"
            + "            {\n"
            + "              \"system\": \"https://fhir.nhs.uk/Id/sds-role-profile-id\",\n"
            + "              \"value\": \"444555666777\"\n"
            + "            },\n"
            + "			   {\n"
            + "                \"system\": \"LocalIdentifierSystem\",\n"
            + "                \"value\": \"1\"\n"
            + "            }],\n"
            + "            \"name\": [{\n"
            + "            \"family\": \"AssurancePractitioner\",\n"
            + "            \"given\": [\"AssuranceTest\"],\n"
            + "            \"prefix\": [\"Mr\"]\n"
            + "        }]\n"
            + "    }\n"
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
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testJsonToXmlString() throws Exception {
        System.out.println("jsonToXmlString");
        String result = JsonXmlConverter.jsonToXmlString(JSON);
        assertNotNull(result);

        assertTrue(result.startsWith("<?xml version=\"1.0\""));
        System.out.println(result);
        //System.out.println(xmlReformat(result));

        result = JsonXmlConverter.jsonToXmlString(JSON_JWT);
        assertNotNull(result);
        assertTrue(result.startsWith("<?xml version=\"1.0\""));

        System.out.println(xmlReformat(result));
        String expResult = "3";
        String result1 = XPathManager.xpathExtractor("count(//json:requesting_practitioner/json:identifiers/json:identifier)", result);
        assertEquals(expResult, result1);

        expResult = "1";
        result1 = XPathManager.xpathExtractor("count(//json:requesting_practitioner/json:names/json:name/json:givens/json:given)", result);
        assertEquals(expResult, result1);

        result1 = XPathManager.xpathExtractor("count(//json:requesting_practitioner/json:names/json:name/json:prefixs/json:prefix)", result);
        assertEquals(expResult, result1);

    }

    @Test
    public void testJsonStringArrayExtractToXmlString() throws Exception {
        System.out.println("jsonStringArrayExtractToXmlString");

        /*
        {
            "name" : [ 
                        "n1",
                        "n2"
                     ]
        }
         */
        String result = JsonXmlConverter.jsonToXmlString("{\"name\": [\"n1\",\"n2\"]}".toCharArray());
        assertNotNull(result);
        System.out.println(xmlReformat(result));

        assertTrue(result.startsWith("<?xml version=\"1.0\""));

        String expResult = "1";
        String result1 = XPathManager.xpathExtractor("count(//json:names)", result);
        assertEquals(expResult, result1);

        expResult = "2";
        result1 = XPathManager.xpathExtractor("count(//json:names/json:name)", result);
        assertEquals(expResult, result1);
    }

    @Test
    public void testJsonRecordArrayExtractToXmlString() throws Exception {
        System.out.println("jsonExtractToXmlString");

        /*
        {
            "identifier" : [ 
                {"r1" : "n1"},
                {"r2" : "n2"}
             ]
        }
         */
        String result = JsonXmlConverter.jsonToXmlString("{\"identifier\": [{\"r1\" : \"n1\"},{\"r2\" : \"n2\"}]}".toCharArray());
        assertNotNull(result);
        System.out.println(xmlReformat(result));

        assertTrue(result.startsWith("<?xml version=\"1.0\""));

        String expResult = "1";
        String result1 = XPathManager.xpathExtractor("count(//json:identifiers)", result);
        assertEquals(expResult, result1);

        expResult = "2";
        result1 = XPathManager.xpathExtractor("count(//json:identifiers/json:identifier)", result);
        assertEquals(expResult, result1);
    }

    /**
     * Test of jsonToXmlDocument method, of class JsonXmlConverter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testJsonToXmlDocument() throws Exception {
        System.out.println("jsonToXmlDocument");

        Document result = JsonXmlConverter.jsonToXmlDocument(JSON);
        assertNotNull(result);

        Element root = result.getDocumentElement();
        String expResult = "json";
        String strResult = root.getTagName();
        assertEquals(expResult, strResult);

        strResult = root.getAttribute("IsSynchronous");
        expResult = "true";
        assertEquals(expResult, strResult);

        // This blows the 64 char limit so the text is rendered in a text element instead
        // other conditions can cause this eg the presence xml special characters which can't be rendered in attributes
        // NB Max attribute length is set in the configurator as a static value so you cant just set the system property and expect 
        // the converter to apply that value. It's a one off before the first call to the converter
        NodeList nodes = root.getElementsByTagName("Org");
        Node node = nodes.item(0);
        System.out.println(node.getNodeName());
        strResult = node.getFirstChild().getTextContent();
        expResult = S70;
        assertEquals(expResult, strResult);
    }
}
