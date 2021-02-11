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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.util.Utils.xmlReformat;

/**
 *
 * @author simonfarrow
 */
public class JWTParserTest {

    private JWTParser instance = null;

    public JWTParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
      // Not URL encoded
      instance = new JWTParser("Bearer ewogICJhbGciOiAiSFMyNTYiLAogICJ0eXAiOiAiSldUIgp9.ew0KICAiaXNzIjogImh0dHBzOi8vQ29uc3VtZXJTeXN0ZW1VUkwiLA0KICAic3ViIjogIjEiLA0KICAiYXVkIjogImh0dHBzOi8vYXV0aG9yaXplLmZoaXIubmhzLm5ldC90b2tlbiIsDQogICJleHAiOiAxNTAzOTk1ODgyLA0KICAiaWF0IjogMTUwMzk5NTU4MiwNCiAgInJlYXNvbl9mb3JfcmVxdWVzdCI6ICJkaXJlY3RjYXJlIiwNCiAgInJlcXVlc3RpbmdfZGV2aWNlIjogew0KICAgICJyZXNvdXJjZVR5cGUiOiAiRGV2aWNlIiwNCiAgICAiaWQiOiAiMSIsDQogICAgImlkZW50aWZpZXIiOiBbDQogICAgICB7DQogICAgICAgICJzeXN0ZW0iOiAiR1BDb25uZWN0VGVzdFN5c3RlbSIsDQogICAgICAgICJ2YWx1ZSI6ICJDbGllbnQiDQogICAgICB9DQogICAgXSwNCiAgICAidHlwZSI6IHsNCiAgICAgICJjb2RpbmciOiBbDQogICAgICAgIHsNCiAgICAgICAgICAic3lzdGVtIjogIkRldmljZUlkZW50aWZpZXJTeXN0ZW0iLA0KICAgICAgICAgICJjb2RlIjogIkRldmljZUlkZW50aWZpZXIiDQogICAgICAgIH0NCiAgICAgIF0NCiAgICB9LA0KICAgICJtb2RlbCI6ICJ2MSIsDQogICAgInZlcnNpb24iOiAiMS4xIg0KICB9LA0KICAicmVxdWVzdGluZ19vcmdhbml6YXRpb24iOiB7DQogICAgInJlc291cmNlVHlwZSI6ICJPcmdhbml6YXRpb24iLA0KICAgICJpZCI6ICIxIiwNCiAgICAiaWRlbnRpZmllciI6IFsNCiAgICAgIHsNCiAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vZmhpci5uaHMubmV0L0lkL29kcy1vcmdhbml6YXRpb24tY29kZSIsDQogICAgICAgICJ2YWx1ZSI6ICJHUENBMDAwMSINCiAgICAgIH0NCiAgICBdLA0KICAgICJuYW1lIjogIkdQIENvbm5lY3QgQXNzdXJhbmNlIg0KICB9LA0KICAicmVxdWVzdGluZ19wcmFjdGl0aW9uZXIiOiB7DQogICAgInJlc291cmNlVHlwZSI6ICJQcmFjdGl0aW9uZXIiLA0KICAgICJpZCI6ICIxIiwNCiAgICAiaWRlbnRpZmllciI6IFsNCiAgICAgIHsNCiAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vZmhpci5uaHMubmV0L3Nkcy11c2VyLWlkIiwNCiAgICAgICAgInZhbHVlIjogIkdDQVNEUzAwMDEiDQogICAgICB9LA0KICAgICAgew0KICAgICAgICAic3lzdGVtIjogIkxvY2FsSWRlbnRpZmllclN5c3RlbSIsDQogICAgICAgICJ2YWx1ZSI6ICIxIg0KICAgICAgfQ0KICAgIF0sDQogICAgIm5hbWUiOiB7DQogICAgICAiZmFtaWx5IjogWw0KICAgICAgICAiQXNzdXJhbmNlUHJhY3RpdGlvbmVyIg0KICAgICAgXSwNCiAgICAgICJnaXZlbiI6IFsNCiAgICAgICAgIkFzc3VyYW5jZVRlc3QiDQogICAgICBdLA0KICAgICAgInByZWZpeCI6IFsNCiAgICAgICAgIk1yIg0KICAgICAgXQ0KICAgIH0sDQogICAgInByYWN0aXRpb25lclJvbGUiOiBbDQogICAgICB7DQogICAgICAgICJyb2xlIjogew0KICAgICAgICAgICJjb2RpbmciOiBbDQogICAgICAgICAgICB7DQogICAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL2ZoaXIubmhzLm5ldC9WYWx1ZVNldC9zZHMtam9iLXJvbGUtbmFtZS0xIiwNCiAgICAgICAgICAgICAgImNvZGUiOiAiQXNzdXJhbmNlSm9iUm9sZSINCiAgICAgICAgICAgIH0NCiAgICAgICAgICBdDQogICAgICAgIH0NCiAgICAgIH0NCiAgICBdDQogIH0sDQogICJyZXF1ZXN0ZWRfc2NvcGUiOiAicGF0aWVudC8qLnJlYWQiLA0KICAicmVxdWVzdGVkX3JlY29yZCI6IHsNCiAgICAicmVzb3VyY2VUeXBlIjogIlBhdGllbnQiLA0KICAgICJpZGVudGlmaWVyIjogWw0KICAgICAgew0KICAgICAgICAic3lzdGVtIjogImh0dHBzOi8vZmhpci5uaHMudWsvSWQvbmhzLW51bWJlciIsDQogICAgICAgICJ2YWx1ZSI6ICI5NDc2NzE5OTMxIg0KICAgICAgfQ0KICAgIF0NCiAgfQ0KfQ0K.nhdifAUeBHme0Qaqd2jmsEwW0tlaxmCarClBfamIA5c=");
      // URL encoded
      //instance = new JWTParser("Bearer ewogICJhbGciOiAiSFMyNTYiLAogICJ0eXAiOiAiSldUIgp9.ew0KICAiaXNzIjogImh0dHBzOi8vQ29uc3VtZXJTeXN0ZW1VUkwiLA0KICAic3ViIjogIjEiLA0KICAiYXVkIjogImh0dHBzOi8vYXV0aG9yaXplLmZoaXIubmhzLm5ldC90b2tlbiIsDQogICJleHAiOiAxNTA1ODk5NTQ3LA0KICAiaWF0IjogMTUwNTg5OTI0NywNCiAgInJlYXNvbl9mb3JfcmVxdWVzdCI6ICJkaXJlY3RjYXJlIiwNCiAgInJlcXVlc3RpbmdfZGV2aWNlIjogew0KICAgICJyZXNvdXJjZVR5cGUiOiAiRGV2aWNlIiwNCiAgICAiaWQiOiAiMSIsDQogICAgImlkZW50aWZpZXIiOiBbDQogICAgICB7DQogICAgICAgICJzeXN0ZW0iOiAiR1BDb25uZWN0VGVzdFN5c3RlbSIsDQogICAgICAgICJ2YWx1ZSI6ICJDbGllbnQiDQogICAgICB9DQogICAgXSwNCiAgICAidHlwZSI6IHsNCiAgICAgICJjb2RpbmciOiBbDQogICAgICAgIHsNCiAgICAgICAgICAic3lzdGVtIjogIkRldmljZUlkZW50aWZpZXJTeXN0ZW0iLA0KICAgICAgICAgICJjb2RlIjogIkRldmljZUlkZW50aWZpZXIiDQogICAgICAgIH0NCiAgICAgIF0NCiAgICB9LA0KICAgICJtb2RlbCI6ICJ2MSIsDQogICAgInZlcnNpb24iOiAiMS4xIg0KICB9LA0KICAicmVxdWVzdGluZ19vcmdhbml6YXRpb24iOiB7DQogICAgInJlc291cmNlVHlwZSI6ICJPcmdhbml6YXRpb24iLA0KICAgICJpZCI6ICIxIiwNCiAgICAiaWRlbnRpZmllciI6IFsNCiAgICAgIHsNCiAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vZmhpci5uaHMubmV0L0lkL29kcy1vcmdhbml6YXRpb24tY29kZSIsDQogICAgICAgICJ2YWx1ZSI6ICJHUENBMDAwMSINCiAgICAgIH0NCiAgICBdLA0KICAgICJuYW1lIjogIkdQIENvbm5lY3QgQXNzdXJhbmNlIg0KICB9LA0KICAicmVxdWVzdGluZ19wcmFjdGl0aW9uZXIiOiB7DQogICAgInJlc291cmNlVHlwZSI6ICJQcmFjdGl0aW9uZXIiLA0KICAgICJpZCI6ICIxIiwNCiAgICAiaWRlbnRpZmllciI6IFsNCiAgICAgIHsNCiAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vZmhpci5uaHMubmV0L3Nkcy11c2VyLWlkIiwNCiAgICAgICAgInZhbHVlIjogIkdDQVNEUzAwMDEiDQogICAgICB9LA0KICAgICAgew0KICAgICAgICAic3lzdGVtIjogIkxvY2FsSWRlbnRpZmllclN5c3RlbSIsDQogICAgICAgICJ2YWx1ZSI6ICIxIg0KICAgICAgfQ0KICAgIF0sDQogICAgIm5hbWUiOiB7DQogICAgICAiZmFtaWx5IjogWw0KICAgICAgICAiQXNzdXJhbmNlUHJhY3RpdGlvbmVyIg0KICAgICAgXSwNCiAgICAgICJnaXZlbiI6IFsNCiAgICAgICAgIkFzc3VyYW5jZVRlc3QiDQogICAgICBdLA0KICAgICAgInByZWZpeCI6IFsNCiAgICAgICAgIk1yIg0KICAgICAgXQ0KICAgIH0sDQogICAgInByYWN0aXRpb25lclJvbGUiOiBbDQogICAgICB7DQogICAgICAgICJyb2xlIjogew0KICAgICAgICAgICJjb2RpbmciOiBbDQogICAgICAgICAgICB7DQogICAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL2ZoaXIubmhzLm5ldC9WYWx1ZVNldC9zZHMtam9iLXJvbGUtbmFtZS0xIiwNCiAgICAgICAgICAgICAgImNvZGUiOiAiQXNzdXJhbmNlSm9iUm9sZSINCiAgICAgICAgICAgIH0NCiAgICAgICAgICBdDQogICAgICAgIH0NCiAgICAgIH0NCiAgICBdDQogIH0sDQogICJyZXF1ZXN0ZWRfc2NvcGUiOiAicGF0aWVudC8qLnJlYWQiLA0KICAicmVxdWVzdGVkX3JlY29yZCI6IHsNCiAgICAicmVzb3VyY2VUeXBlIjogIlBhdGllbnQiLA0KICAgICJpZGVudGlmaWVyIjogWw0KICAgICAgew0KICAgICAgICAic3lzdGVtIjogImh0dHBzOi8vZmhpci5uaHMudWsvSWQvbmhzLW51bWJlciIsDQogICAgICAgICJ2YWx1ZSI6ICI5NDc2NzE5OTMxIg0KICAgICAgfQ0KICAgIF0NCiAgfQ0KfQ0K.t-BVZw-n5uehGjJg7xAKXdTcV1Izg5YrUe7xbDPgy98");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getJsonPayload method, of class JWTParser.
     */
    @Test
    public void testGetJsonPayload() {
        System.out.println("getJsonPayload");
        String expResult = ("{\n"
                + "  \"iss\": \"https://ConsumerSystemURL\",\n"
                + "  \"sub\": \"1\",\n"
                + "  \"aud\": \"https://authorize.fhir.nhs.net/token\",\n"
                + "  \"exp\": 1503995882,\n"
                + "  \"iat\": 1503995582,\n"
                + "  \"reason_for_request\": \"directcare\",\n"
                + "  \"requesting_device\": {\n"
                + "    \"resourceType\": \"Device\",\n"
                + "    \"id\": \"1\",\n"
                + "    \"identifier\": [\n"
                + "      {\n"
                + "        \"system\": \"GPConnectTestSystem\",\n"
                + "        \"value\": \"Client\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"type\": {\n"
                + "      \"coding\": [\n"
                + "        {\n"
                + "          \"system\": \"DeviceIdentifierSystem\",\n"
                + "          \"code\": \"DeviceIdentifier\"\n"
                + "        }\n"
                + "      ]\n"
                + "    },\n"
                + "    \"model\": \"v1\",\n"
                + "    \"version\": \"1.1\"\n"
                + "  },\n"
                + "  \"requesting_organization\": {\n"
                + "    \"resourceType\": \"Organization\",\n"
                + "    \"id\": \"1\",\n"
                + "    \"identifier\": [\n"
                + "      {\n"
                + "        \"system\": \"http://fhir.nhs.net/Id/ods-organization-code\",\n"
                + "        \"value\": \"GPCA0001\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"name\": \"GP Connect Assurance\"\n"
                + "  },\n"
                + "  \"requesting_practitioner\": {\n"
                + "    \"resourceType\": \"Practitioner\",\n"
                + "    \"id\": \"1\",\n"
                + "    \"identifier\": [\n"
                + "      {\n"
                + "        \"system\": \"http://fhir.nhs.net/sds-user-id\",\n"
                + "        \"value\": \"GCASDS0001\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"system\": \"LocalIdentifierSystem\",\n"
                + "        \"value\": \"1\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"name\": {\n"
                + "      \"family\": [\n"
                + "        \"AssurancePractitioner\"\n"
                + "      ],\n"
                + "      \"given\": [\n"
                + "        \"AssuranceTest\"\n"
                + "      ],\n"
                + "      \"prefix\": [\n"
                + "        \"Mr\"\n"
                + "      ]\n"
                + "    },\n"
                + "    \"practitionerRole\": [\n"
                + "      {\n"
                + "        \"role\": {\n"
                + "          \"coding\": [\n"
                + "            {\n"
                + "              \"system\": \"http://fhir.nhs.net/ValueSet/sds-job-role-name-1\",\n"
                + "              \"code\": \"AssuranceJobRole\"\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"requested_scope\": \"patient/*.read\",\n"
                + "  \"requested_record\": {\n"
                + "    \"resourceType\": \"Patient\",\n"
                + "    \"identifier\": [\n"
                + "      {\n"
                + "        \"system\": \"https://fhir.nhs.uk/Id/nhs-number\",\n"
                + "        \"value\": \"9476719931\"\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "}\n").replaceAll("\n", "\r\n");
        String result = instance.getJsonPayload();
        assertEquals(expResult, result);
    }

    /**
     * Test of getXmlPayload method, of class JWTParser.
     */
    @Test
    public void testGetXmlPayload() {
        System.out.println("getXmlPayload");
        String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><json xmlns=\"uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter\" aud=\"https://authorize.fhir.nhs.net/token\" exp=\"1503995882\" iat=\"1503995582\" iss=\"https://ConsumerSystemURL\" reason_for_request=\"directcare\" requested_scope=\"patient/*.read\" sub=\"1\"><requesting_device id=\"1\" model=\"v1\" resourceType=\"Device\" version=\"1.1\"><identifiers><identifier system=\"GPConnectTestSystem\" value=\"Client\"/></identifiers><type><codings><coding code=\"DeviceIdentifier\" system=\"DeviceIdentifierSystem\"/></codings></type></requesting_device><requesting_organization id=\"1\" name=\"GP Connect Assurance\" resourceType=\"Organization\"><identifiers><identifier system=\"http://fhir.nhs.net/Id/ods-organization-code\" value=\"GPCA0001\"/></identifiers></requesting_organization><requesting_practitioner id=\"1\" resourceType=\"Practitioner\"><identifiers><identifier system=\"http://fhir.nhs.net/sds-user-id\" value=\"GCASDS0001\"/><identifier system=\"LocalIdentifierSystem\" value=\"1\"/></identifiers><name><familys><family>AssurancePractitioner</family></familys><givens><given>AssuranceTest</given></givens><prefixs><prefix>Mr</prefix></prefixs></name><practitionerRoles><practitionerRole><role><codings><coding code=\"AssuranceJobRole\" system=\"http://fhir.nhs.net/ValueSet/sds-job-role-name-1\"/></codings></role></practitionerRole></practitionerRoles></requesting_practitioner><requested_record resourceType=\"Patient\"><identifiers><identifier system=\"https://fhir.nhs.uk/Id/nhs-number\" value=\"9476719931\"/></identifiers></requested_record></json>";
        String result = instance.getXmlPayload();
        System.out.println(xmlReformat(result));
        assertEquals(expResult, result);
    }

    /**
     * Test of getJsonHeader method, of class JWTParser.
     */
    @Test
    public void testGetJsonHeader() {
        System.out.println("getJsonHeader");
        String expResult = "{\n"
                + "  \"alg\": \"HS256\",\n"
                + "  \"typ\": \"JWT\"\n"
                + "}";
        String result = instance.getJsonHeader();
        assertEquals(expResult, result);
    }

    /**
     * Test of getXmlHeader method, of class JWTParser.
     */
    @Test
    public void testGetXmlHeader() {
        System.out.println("getXmlHeader");
        String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><json xmlns=\"uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter\" alg=\"HS256\" typ=\"JWT\"/>";
        String result = instance.getXmlHeader();
        assertEquals(expResult, result);
    }

    /**
     * Test of hMacValidate method, of class JWTParser.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHMacValidate() throws Exception {
        System.out.println("hMacValidate");
        String key = "secret";
        boolean expResult = true;
        boolean result = instance.hMacValidate(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of hMacValidate method, of class JWTParser.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrongNoBlocks() throws Exception {
        System.out.println("wrongNoBlocks");
        instance = new JWTParser("Bearer ewogICJhbGciOiAiSFMyNTYiLAogICJ0eXAiOiAiSldUIgp9.ew0KICAiaXNzIjogImh0dHBzOi8vQ29uc3VtZXJTeXN0ZW1VUkwiLA0KICAic3ViIjogIjEiLA0KICAiYXVkIjogImh0dHBzOi8vYXV0aG9yaXplLmZoaXIubmhzLm5ldC90b2tlbiIsDQogICJleHAiOiAxNTAzOTk1ODgyLA0KICAiaWF0IjogMTUwMzk5NTU4MiwNCiAgInJlYXNvbl9mb3JfcmVxdWVzdCI6ICJkaXJlY3RjYXJlIiwNCiAgInJlcXVlc3RpbmdfZGV2aWNlIjogew0KICAgICJyZXNvdXJjZVR5cGUiOiAiRGV2aWNlIiwNCiAgICAiaWQiOiAiMSIsDQogICAgImlkZW50aWZpZXIiOiBbDQogICAgICB7DQogICAgICAgICJzeXN0ZW0iOiAiR1BDb25uZWN0VGVzdFN5c3RlbSIsDQogICAgICAgICJ2YWx1ZSI6ICJDbGllbnQiDQogICAgICB9DQogICAgXSwNCiAgICAidHlwZSI6IHsNCiAgICAgICJjb2RpbmciOiBbDQogICAgICAgIHsNCiAgICAgICAgICAic3lzdGVtIjogIkRldmljZUlkZW50aWZpZXJTeXN0ZW0iLA0KICAgICAgICAgICJjb2RlIjogIkRldmljZUlkZW50aWZpZXIiDQogICAgICAgIH0NCiAgICAgIF0NCiAgICB9LA0KICAgICJtb2RlbCI6ICJ2MSIsDQogICAgInZlcnNpb24iOiAiMS4xIg0KICB9LA0KICAicmVxdWVzdGluZ19vcmdhbml6YXRpb24iOiB7DQogICAgInJlc291cmNlVHlwZSI6ICJPcmdhbml6YXRpb24iLA0KICAgICJpZCI6ICIxIiwNCiAgICAiaWRlbnRpZmllciI6IFsNCiAgICAgIHsNCiAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vZmhpci5uaHMubmV0L0lkL29kcy1vcmdhbml6YXRpb24tY29kZSIsDQogICAgICAgICJ2YWx1ZSI6ICJHUENBMDAwMSINCiAgICAgIH0NCiAgICBdLA0KICAgICJuYW1lIjogIkdQIENvbm5lY3QgQXNzdXJhbmNlIg0KICB9LA0KICAicmVxdWVzdGluZ19wcmFjdGl0aW9uZXIiOiB7DQogICAgInJlc291cmNlVHlwZSI6ICJQcmFjdGl0aW9uZXIiLA0KICAgICJpZCI6ICIxIiwNCiAgICAiaWRlbnRpZmllciI6IFsNCiAgICAgIHsNCiAgICAgICAgInN5c3RlbSI6ICJodHRwOi8vZmhpci5uaHMubmV0L3Nkcy11c2VyLWlkIiwNCiAgICAgICAgInZhbHVlIjogIkdDQVNEUzAwMDEiDQogICAgICB9LA0KICAgICAgew0KICAgICAgICAic3lzdGVtIjogIkxvY2FsSWRlbnRpZmllclN5c3RlbSIsDQogICAgICAgICJ2YWx1ZSI6ICIxIg0KICAgICAgfQ0KICAgIF0sDQogICAgIm5hbWUiOiB7DQogICAgICAiZmFtaWx5IjogWw0KICAgICAgICAiQXNzdXJhbmNlUHJhY3RpdGlvbmVyIg0KICAgICAgXSwNCiAgICAgICJnaXZlbiI6IFsNCiAgICAgICAgIkFzc3VyYW5jZVRlc3QiDQogICAgICBdLA0KICAgICAgInByZWZpeCI6IFsNCiAgICAgICAgIk1yIg0KICAgICAgXQ0KICAgIH0sDQogICAgInByYWN0aXRpb25lclJvbGUiOiBbDQogICAgICB7DQogICAgICAgICJyb2xlIjogew0KICAgICAgICAgICJjb2RpbmciOiBbDQogICAgICAgICAgICB7DQogICAgICAgICAgICAgICJzeXN0ZW0iOiAiaHR0cDovL2ZoaXIubmhzLm5ldC9WYWx1ZVNldC9zZHMtam9iLXJvbGUtbmFtZS0xIiwNCiAgICAgICAgICAgICAgImNvZGUiOiAiQXNzdXJhbmNlSm9iUm9sZSINCiAgICAgICAgICAgIH0NCiAgICAgICAgICBdDQogICAgICAgIH0NCiAgICAgIH0NCiAgICBdDQogIH0sDQogICJyZXF1ZXN0ZWRfc2NvcGUiOiAicGF0aWVudC8qLnJlYWQiLA0KICAicmVxdWVzdGVkX3JlY29yZCI6IHsNCiAgICAicmVzb3VyY2VUeXBlIjogIlBhdGllbnQiLA0KICAgICJpZGVudGlmaWVyIjogWw0KICAgICAgew0KICAgICAgICAic3lzdGVtIjogImh0dHBzOi8vZmhpci5uaHMudWsvSWQvbmhzLW51bWJlciIsDQogICAgICAgICJ2YWx1ZSI6ICI5NDc2NzE5OTMxIg0KICAgICAgfQ0KICAgIF0NCiAgfQ0KfQ0K.nhdifAUeBHme0Qaqd2jmsEwW0tlaxmCarClBfamIA5c=.XXXXXX");
        String key = "secret";
        boolean expResult = false;
        boolean result = instance.hMacValidate(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSupportingData method, of class JWTParser.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = "JWT json header";
        String result = instance.getSupportingData();
        assertTrue(result.startsWith(expResult));
        expResult = "true\r\n";
        assertTrue(result.endsWith(expResult));
    }

    /**
     * Test of hasPadding method, of class JWTParser.
     */
    @Test
    public void testHasNoPadding() {
        System.out.println("hasNoPadding");
        boolean expResult = false;
        boolean result = instance.hasNoPadding();
        assertEquals(expResult, result);
    }

    /**
     * Test of isURLEncoded method, of class JWTParser.
     */
    @Test
    public void testIsURLEncoded() {
        System.out.println("isURLEncoded");
        boolean expResult = true;
        boolean result = instance.isURLEncoded();
        assertEquals(expResult, result);
    }

}
