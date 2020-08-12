/**
 * Copyright 2013  Simon Farrow simon.farrow1@hscic.gov.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @author SIFA2
 */
package uk.nhs.digital.mait.tkwx.util.dsig;

import java.io.ByteArrayInputStream;
import java.io.File;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;

/**
 *
 * @author simonfarrow
 */
public class PrescriptionSignatureVerifierTest implements URIDereferencer {

    public static final String FRAGMENTEXTRACTOR = "src/main/resources/uk/nhs/digital/mait/tkwx/tk/internalservices/validation/spine/Prescriptionv0r1.xslt";
    public static final String SPACESTRIPPER = "src/main/resources/uk/nhs/digital/mait/tkwx/tk/internalservices/validation/spine/SignedInfowhitespacev0r1.xslt";

    private String fragments = null;
    private TransformManager t;

    public PrescriptionSignatureVerifierTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        t = TransformManager.getInstance();
        t.addTransform(FRAGMENTEXTRACTOR, FRAGMENTEXTRACTOR);
        t.addTransform(SPACESTRIPPER, SPACESTRIPPER);
        fragments = t.doTransform(FRAGMENTEXTRACTOR, Utils.readFile2String("src/test/resources/sig.xml"));
 }

    @After
    public void tearDown() {
    }

    /**
     * Test of verify method, of class PrescriptionSignatureVerifier.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testVerify() throws Exception {
        System.out.println("verify");
        File sig = new File("src/test/resources/sig.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dbFactory.setIgnoringComments(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(sig);
        NodeList nlList = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        Element signatureNode = (Element) nlList.item(0);

        // run xslt org/warlock/tk/internalservices/validation/spine/Prescriptionv0r1.xslt on sig.xml
        String rx = fragments;

        URIDereferencer urid = this;
        boolean expResult = true;
        ValidationReport[] result = PrescriptionSignatureVerifier.verify(signatureNode, rx, urid);

        System.out.println(result[0].getDetail());
        assertEquals(expResult, result[0].getPassed());

        expResult = false;
        System.out.println(result[1].getDetail());
        assertEquals(expResult, result[1].getPassed());
    }

    @Override
    public Data dereference(URIReference uriReference, XMLCryptoContext context) throws URIReferenceException {
        OctetStreamData oct = new OctetStreamData(new ByteArrayInputStream(fragments.getBytes()));
        return oct;
    }

}
