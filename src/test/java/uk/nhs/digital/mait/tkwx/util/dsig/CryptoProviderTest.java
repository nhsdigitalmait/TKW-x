/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.util.dsig;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializer;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.tkwx.util.dsig.PrescriptionSignatureVerifierTest.FRAGMENTEXTRACTOR;

/**
 *
 * @author simonfarrow
 */
public class CryptoProviderTest implements URIDereferencer {

    private CryptoProvider instance = null;
    private String fragments = null;
    private TransformManager m;

    public CryptoProviderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new CryptoProvider(System.getenv("TKWROOT") + "/config/GP_CONNECT/certs/tls.jks", "password", "selfsigned");
        m = TransformManager.getInstance();
        m.addTransform(FRAGMENTEXTRACTOR, FRAGMENTEXTRACTOR);
        String payload = Utils.readFile2String("src/test/resources/sig.xml");
        fragments = m.doTransform(FRAGMENTEXTRACTOR, payload);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of sign method, of class CryptoProvider.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSign() throws Exception {
        System.out.println("sign");
        String rx = fragments;
        String expResult = "Signature";
        URIDereferencer urid = this;
        Element result = instance.sign(rx, urid);
        System.out.println(dumpElement(result));
        assertEquals(expResult, result.getLocalName());
    }

    private String dumpElement(Element result) throws DOMException, LSException {
        DOMImplementationLS domImplLS = (DOMImplementationLS) result.getOwnerDocument().getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        String str = serializer.writeToString(result);
        return str;
    }

    /**
     * Test of verify method, of class CryptoProvider.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testVerify() throws Exception {
        System.out.println("verify");
        URIDereferencer urid = this;
        Element signatureNode = instance.sign(fragments, urid);

        String rx = fragments;
        ArrayList<String> reports = new ArrayList<>();
        boolean expResult = true;
        boolean result = instance.verify(signatureNode, rx, urid, reports);
        assertEquals(expResult, result);
        
        int intExpResult = 1;
        assertEquals(intExpResult, reports.size());
        for ( String report : reports) {
            System.out.println(report);
        }

        fragments = fragments.replaceFirst("(?s)COCKSHOOT", "COCKSHOOTER");
        rx = fragments;

        reports = new ArrayList<>();
        expResult = false;
        
        // get more reports on a failure
        intExpResult = 4;
        result = instance.verify(signatureNode, rx, urid, reports);
        assertEquals(expResult, result);
        assertEquals(intExpResult, reports.size());
        for ( String report : reports) {
            System.out.println(report);
        }
    }

    @Override
    public Data dereference(URIReference uriReference, XMLCryptoContext context) throws URIReferenceException {
        OctetStreamData oct = new OctetStreamData(new ByteArrayInputStream(fragments.getBytes()));
        return oct;
    }
}
