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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 *
 * @author simonfarrow
 */
public class FHIRJsonXmlAdapterTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private static String xml;

    public FHIRJsonXmlAdapterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, Exception {
        xml = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/FGM_V2/FGM_Create_1_0.xml")));
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
        ((uk.nhs.digital.mait.commonutils.util.configurator.ResettablePropertiesConfigurator) Configurator.getConfigurator()).setProperties(new Properties());
    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        FHIRJsonXmlAdapter.destroyInstance();
    }

    /**
     * Test of fhirConvertXml2Json method, of class FHIRJsonXmlAdapter.
     * json ->> xml dstu2
     * @throws java.io.IOException
     */
    private void testFhirConvertXml2JsonDstu2() throws Exception {
        System.out.println("fhirConvertXml2JsonDstu2");
        Configurator.getConfigurator().setConfiguration("tks.fhir.version", "Dstu2");

        String result = FHIRJsonXmlAdapter.fhirConvertXml2Json(xml);
        assertTrue(result.startsWith("{"));

        String expResult = "\"resourceType\"";
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of fhirConvertJson2Xml method, of class FHIRJsonXmlAdapter.
     * json -> xml dstu2
     * @throws java.io.IOException
     */
    private void testFhirConvertJson2XmlDstu2() throws Exception {
        System.out.println("fhirConvertJson2XmlDstu2");
        Configurator.getConfigurator().setConfiguration("tks.fhir.version", "Dstu2");

        String json = FHIRJsonXmlAdapter.fhirConvertXml2Json(xml);
        
        String expResult = "<Bundle";
        String result = FHIRJsonXmlAdapter.fhirConvertJson2Xml(json);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of fhirConvertXml2Json method, of class FHIRJsonXmlAdapter.
     * xml -> json dstu3
     * @throws java.io.IOException
     */
    @Test
    public void testFhirConvertXml2JsonDstu3() throws Exception {
        System.out.println("fhirConvertXml2JsonDstu3");
        Configurator.getConfigurator().setConfiguration("tks.fhir.version", "Dstu3");

        String result = FHIRJsonXmlAdapter.fhirConvertXml2Json(xml);
        assertTrue(result.startsWith("{") && !result.contains("fhirconversionfailure"));

        String expResult = "\"resourceType\"";
        assertTrue(result.contains(expResult));

        String otherXml = "<OperationOutcome xmlns=\"http://hl7.org/fhir\">\n"
                + "    <meta>\n"
                + "        <profile value=\"http://fhir.nhs.net/StructureDefinition/gpconnect-operationoutcome-1\" />\n"
                + "    </meta>\n"
                + "    <issue>\n"
                + "        <severity value=\"error\" />\n"
                + "        <code value=\"invalid\" />\n"
                + "        <details>\n"
                + "            <coding>\n"
                + "                <system value=\"http://fhir.nhs.net/ValueSet/gpconnect-error-or-warning-code-1\" />\n"
                + "                <code value=\"INVALID_PARAMETER\" />\n"
                + "                <display value=\"INVALID_PARAMETER\" />\n"
                + "            </coding>\n"
                + "            <text value=\"Invalid attribute value \"ABCD\": Invalid date/time format: \"ABCD\"\" />\n"
                + "        </details>\n"
                + "    </issue>\n"
                + "</OperationOutcome>";

        result = FHIRJsonXmlAdapter.fhirConvertXml2Json(otherXml);
        assertTrue(result.startsWith("{") && result.contains("fhirconversionfailure"));
        
        otherXml = otherXml.replaceAll("\"ABCD\"", "&quot;ABCD&quot;");
        result = FHIRJsonXmlAdapter.fhirConvertXml2Json(otherXml);
        assertTrue(result.startsWith("{") && result.contains("OperationOutcome"));
    }

    /**
     * Test of fhirConvertJson2Xml method, of class FHIRJsonXmlAdapter.
     * json -> xml dstu3
     *
     * @throws java.io.IOException
     */
    @Test
    public void testFhirConvertJson2XmlDstu3() throws Exception {
        System.out.println("fhirConvertJson2XmlDstu3");
        Configurator.getConfigurator().setConfiguration("tks.fhir.version", "Dstu3");

        String json = FHIRJsonXmlAdapter.fhirConvertXml2Json(xml);
        
        String expResult = "<Bundle";
        String result = FHIRJsonXmlAdapter.fhirConvertJson2Xml(json);
        assertTrue(result.startsWith(expResult));

        String jsonFilename = "slots";
        json = Utils.readFile2String("src/test/resources/"+jsonFilename + ".json");
        result = FHIRJsonXmlAdapter.fhirConvertJson2Xml(json);
        assertTrue(result.startsWith(expResult));
        //      Util.writeByteArray2File("test+"jsonFilename+".xml", result.getBytes());
    }

    /**
     * Test of destroyInstance method, of class FHIRJsonXmlAdapter.
     */
    @Test
    public void testDestroyInstance() {
        System.out.println("destroyInstance");
        FHIRJsonXmlAdapter.destroyInstance();
    }

    /**
     * Test of fhirConvertXml2Json method, of class FHIRJsonXmlAdapter.
     * @throws java.lang.Exception
     */
    @Test
    public void testFhirConvertXml2Json() throws Exception {
        System.out.println("fhirConvertXml2Json");
        testFhirConvertXml2JsonDstu2();
    }

    /**
     * Test of fhirConvertJson2Xml method, of class FHIRJsonXmlAdapter.
     * @throws java.lang.Exception
     */
    @Test
    public void testFhirConvertJson2Xml() throws Exception {
        System.out.println("fhirConvertJson2Xml");
        testFhirConvertJson2XmlDstu2();
    }

    /**
     * Test of getFhirVersion method, of class FHIRJsonXmlAdapter.
     */
    @Test
    public void testGetFhirVersion() {
        System.out.println("getFhirVersion");
        String expResult = "";
        String result = FHIRJsonXmlAdapter.getFhirVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFhirVersion method, of class FHIRJsonXmlAdapter.
     */
    @Test
    public void testSetFhirVersion() {
        System.out.println("setFhirVersion");
        String aFhirVersion = "Dstu2";
        FHIRJsonXmlAdapter.setFhirVersion(aFhirVersion);
    }
}
