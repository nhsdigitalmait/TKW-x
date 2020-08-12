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

package uk.nhs.digital.mait.tkwx.util.dsig;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.commonutils.util.dsig.SimpleKeySelector;
import org.xml.sax.InputSource;

/**
 *
 * @author damian
 */
public class PrescriptionSignatureVerifier 
{
    public static ValidationReport[] verify(Element signatureNode, String rx, URIDereferencer urid)
            throws Exception
    {
        // 1. Get the certificate
        // 2. Add it to a SimpleKeySelector
        // 3. Make a DOMStructure from the signature
        // 4. Make a DOMValidateContext from the key selector and a Node made from rx
        // 5. Unmarshal the signature
        // 6. Validate
        Node crt = signatureNode.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0);
        NodeList crtChildren = crt.getChildNodes();
        String encodedKey = null;
        for (int i = 0; i < crtChildren.getLength(); i++) {
            Node kn = crtChildren.item(i);
            if (kn.getNodeType() == Node.TEXT_NODE) {
                encodedKey = kn.getNodeValue();
                break;
            }
        }
        if (encodedKey == null) {
            ValidationReport ve = new ValidationReport("Unable to resolve encoded certificate in <X509Certificate>");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;

        }
        StringBuilder sb = new StringBuilder("-----BEGIN CERTIFICATE-----\n");
        sb.append(encodedKey);
        if (encodedKey.charAt(encodedKey.length() - 1) != '\n') {
            sb.append("\n");
        }
        sb.append("-----END CERTIFICATE-----");
        String skey = sb.toString();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x = null;
        try {
            x = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(sb.toString().getBytes()));
        }
        catch (Exception e) {
            ValidationReport ve = new ValidationReport("Unable to parse certificate");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
                
        String cIssuer = x.getIssuerX500Principal().getName();
        String cSubject = x.getSubjectX500Principal().getName();
        String cStart = x.getNotBefore().toString();
        String cEnd = x.getNotAfter().toString();
        StringBuilder certDetails = new StringBuilder("Certificate details");
        Date now = new Date();
        if (x.getNotBefore().compareTo(now) > 0) 
            certDetails.append("\tWARNING: NOT YET VALID ");
        if (x.getNotAfter().compareTo(now) < 0)
            certDetails.append("\tWARNING: EXPIRED ");
        
        certDetails.append("\tIssuer: ");
        certDetails.append(cIssuer);
        certDetails.append(" Subject: ");
        certDetails.append(cSubject);
        certDetails.append(" From: ");
        certDetails.append(cStart);
        certDetails.append(" To: ");
        certDetails.append(cEnd);
        ValidationReport ve = new ValidationReport(certDetails.toString());
        ve.setPassed();
        ValidationReport[] vreport = new ValidationReport[2];
        vreport[0] = ve;
        
        SimpleKeySelector sks = new SimpleKeySelector();
        sks.setFixedKey(x.getPublicKey());
        
        DOMStructure sig = new DOMStructure(signatureNode); 
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(rx.toCharArray())));
        Node n = doc.getElementsByTagName("FragmentsToBeHashed").item(0);
        XMLSignatureFactory xsf = XMLSignatureFactory.getInstance("DOM");
        DOMValidateContext dvc = new DOMValidateContext(sks, n);
        dvc.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
        dvc.setURIDereferencer(urid);
        XMLSignature xmlsig = xsf.unmarshalXMLSignature(sig);
        boolean isvalid = xmlsig.validate(dvc);
        
        if (!isvalid) {
            boolean sigvalid = xmlsig.getSignatureValue().validate(dvc);
            if (sigvalid) {
                StringBuilder sbInvalid = new StringBuilder("Validation failed: ");
                java.util.Iterator it = xmlsig.getSignedInfo().getReferences().iterator();
                boolean invalidreffound = false;
                for (int j = 0; it.hasNext(); j++) {
                    Reference refr = (Reference)it.next();
                    boolean refValid = refr.validate(dvc);
                    if (!refValid) {            
                        invalidreffound = true;
                        sbInvalid.append("\tReference ");
                        sbInvalid.append(j);
                        sbInvalid.append(" is invalid:\t");
                        java.io.InputStreamReader sis = new java.io.InputStreamReader(refr.getDigestInputStream());
                        char[] b = new char[10240];
                        int r = sis.read(b);
                        if (r != -1) {
                            sbInvalid.append(new String(b, 0, r));
                        } else {
                            sbInvalid.append("Failed to read reference");
                        }
                    }
                }
                if (invalidreffound) {
                    ValidationReport vf = new ValidationReport(sbInvalid.toString());
                    vreport[1] = vf;
                } else {
                    ValidationReport vf = new ValidationReport("All references OK");
                    vf.setPassed();
                }
            } else {                
                ValidationReport vf = new ValidationReport("Invalid signature for valid digest");
                vreport[1] = vf;
            }
        }
        return vreport;
    }
    
}
