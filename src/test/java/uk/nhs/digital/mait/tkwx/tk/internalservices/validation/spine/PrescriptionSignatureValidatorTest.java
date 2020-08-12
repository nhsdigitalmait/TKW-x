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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLCryptoContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorOutput;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD_KEYSTORE;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD_PASSWORD;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD_USERID;

/**
 *
 * @author simonfarrow
 */
public class PrescriptionSignatureValidatorTest {

    private PrescriptionSignatureValidator instance;
    private static SpineMessage spineMessage;
    private static SpineMessage badSpineMessage;

    public PrescriptionSignatureValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        spineMessage = new SpineMessage(System.getenv("TKWROOT") + "/config/SPINE_ETP_Prescribing_Validation/messages_for_validation",
                "20170824080922015662_1F1E2A_16231864152232495815_anon.xml");
        
        String badSpineMessageStr = new String(Files.readAllBytes(Paths.get(System.getenv("TKWROOT") + 
                "/config/SPINE_ETP_Prescribing_Validation/messages_for_validation/20170824080922015662_1F1E2A_16231864152232495815_anon.xml")));
        // Modify the patients name so that it should now fail verification
        badSpineMessageStr = badSpineMessageStr.replaceFirst("(?s)STRZODA", "STRZODAXXXX");
        badSpineMessage = new SpineMessage(badSpineMessageStr);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        System.setProperty(GOOD_KEYSTORE, System.getenv("TKWROOT") + "/config/GP_CONNECT/certs/tls.jks");
        System.setProperty(GOOD_USERID, "selfsigned");
        System.setProperty(GOOD_PASSWORD, "password");

        instance = new PrescriptionSignatureValidator();
        instance.initialise();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class PrescriptionSignatureValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of setType method, of class PrescriptionSignatureValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        // null call
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class PrescriptionSignatureValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        // null call
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class PrescriptionSignatureValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        // null call
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of dereference method, of class PrescriptionSignatureValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testDereference() throws Exception {
        System.out.println("dereference");
        URIReference r = new MyReference();
        XMLCryptoContext xcc = null;
        // have to call validate spine message first to set fragments to be non null
        instance.validate(spineMessage);

        Data result = instance.dereference(r, xcc);
        assertNotNull(result);
    }

    /**
     * Test of validate method, of class PrescriptionSignatureValidator.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
        String o = "";
        HashMap<String, Object> extraMessageInfo = null;
        boolean stripHeader = false;
        ValidatorOutput expResult = null;
        ValidatorOutput result = instance.validate(o, extraMessageInfo, stripHeader);
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class PrescriptionSignatureValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        boolean expResult = true;
        ValidationReport[] result = instance.validate(spineMessage);
        
        int intExpResult = 1;
        assertEquals(intExpResult, result.length);
        System.out.println(result[0].getDetail());
        assertEquals(expResult, result[0].getPassed());

        // we get extra diagnostic information when there's a failure
        intExpResult = 4;
        expResult = false;
        result = instance.validate(badSpineMessage);
        assertEquals(intExpResult, result.length);
        
        for (ValidationReport report : result) {
            assertEquals(expResult, report.getPassed());
            System.out.println(report.getDetail());
        }
    }

    /**
     * Test of getSupportingData method, of class
     * PrescriptionSignatureValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        // hard coded to return an empty string
        String expResult = "";
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeExternalOutput method, of class
     * PrescriptionSignatureValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        // null call
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setVariableProvider method, of class
     * PrescriptionSignatureValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        // null call
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    class MyReference implements URIReference {

        @Override
        public String getURI() {
            return "uri";
        }

        @Override
        public String getType() {
            return "typ";
        }

    }
}
