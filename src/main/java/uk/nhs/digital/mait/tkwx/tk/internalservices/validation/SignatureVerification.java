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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import org.xml.sax.InputSource;
import java.io.CharArrayReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dom.DOMStructure;
import uk.nhs.digital.mait.commonutils.util.dsig.SimpleKeySelector;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.DEBUG_DISPLAY_DIGEST_PROPERTY;
import uk.nhs.digital.mait.commonutils.util.dsig.DOMURIdereferencer;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * This class is substantially based on the "verify()" method in the
 * SignerService internal services class, modified to have the verification
 * process populate a ValidationReport array as per the ValidationCheck
 * interface contract.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SignatureVerification
        implements ValidationCheck {
    private VariableProvider vProvider = null;
    private String checkPart = null;
    private int attachmentNo = -1;
    
    @Override
    public void initialise()
            throws Exception {
    }

    @Override
    public String getSupportingData() { return null; }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {}

    @Override
    public ValidationReport[] validate(SpineMessage o)
            throws Exception
    {
        if (checkPart == null ||checkPart.toLowerCase().startsWith("attachment")) {
            return validate(o.getATTACHMENTPart(attachmentNo), null, false).getReport();
        }else{
            throw new Exception("ITK validation of tertiary MIME part of spine message. Incorrect validation class used");
        }
    }

    @Override
    public void setType(String t) {
        int uscore = t.indexOf('_');
        if(uscore == -1){
        }else{
            checkPart = t.substring(0, uscore);
            attachmentNo = Integer.parseInt(checkPart.substring(10, checkPart.length()))-1;
        }
    }       

    @Override
    public void setResource(String r) {
    }

    @Override
    public void setData(String d) throws Exception {
    }

    @Override
    public ValidatorOutput validate(String s, HashMap<String,Object> extraMessageInfo, boolean stripHeader)
            throws Exception {
        ArrayList<ValidationReport> r = new ArrayList<ValidationReport>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(s.toCharArray())));

        // Get the key from the BinarySecurityToken

        Node bst = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "BinarySecurityToken").item(0);
        if (bst == null) {
            ValidationReport v = new ValidationReport("BinarySecurityToken not present");
            r.add(v);
            return new ValidatorOutput(null, (ValidationReport[]) r.toArray(new ValidationReport[r.size()]));
        }
        Node un = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Username").item(0);
        NodeList bstChildren = bst.getChildNodes();
        String encodedKey = null;
        for (int i = 0; i < bstChildren.getLength(); i++) {
            Node kn = bstChildren.item(i);
            if (kn.getNodeType() == Node.TEXT_NODE) {
                encodedKey = kn.getNodeValue();
                break;
            }
        }
        if (encodedKey == null) {
            ValidationReport v = new ValidationReport("Unable to resolve encoded certificate in <BinarySecurityToken>");
            v.setTest("Signature checking cannot proceed");
            ValidationReport vrep[] = new ValidationReport[1];
            vrep[0] = v;
            return new ValidatorOutput(null, vrep);
        }
        StringBuilder sb = new StringBuilder("-----BEGIN CERTIFICATE-----\n");
        sb.append(encodedKey);
        if (encodedKey.charAt(encodedKey.length() - 1) != '\n') {
            sb.append("\n");
        }
        sb.append("-----END CERTIFICATE-----");
        String skey = sb.toString();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(sb.toString().getBytes()));
        SimpleKeySelector sks = new SimpleKeySelector();
        sks.setFixedKey(x.getPublicKey());
        String sname = x.getSubjectX500Principal().getName();
        if (sname == null) {
            ValidationReport v = new ValidationReport("No subject name in message signing certificate!");
            v.setTest("Username checking cannot be done");
            r.add(v);
        } else {
            if (un != null) {
                String unfield = un.getTextContent();
                // Handle the case where the declared username is just a bare CN, i.e. remove the
                // CN= from the beginning of the subject name. Otherwise we'll compare the whole lot.
                if (!unfield.toUpperCase().startsWith("CN=")) {
                    sname = sname.substring(3);
                }
                if (!sname.contentEquals(unfield)) {
                    ValidationReport v = new ValidationReport("User name and certificate owner do not match");
                    v.setTest("Rejecting message: Username/certificate owner mismatch: Username: " + unfield + " / Certificate: " + sname);
                    r.add(v);
                }
            } else {
                ValidationReport v = new ValidationReport("User name not present");
                r.add(v);
            }
        }

        Node n = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security").item(0);
        DOMStructure sig = new DOMStructure(doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0));
        XMLSignatureFactory xsf = XMLSignatureFactory.getInstance("DOM");
        DOMValidateContext dvc = new DOMValidateContext(sks, n);
        dvc.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
        dvc.setURIDereferencer(new DOMURIdereferencer());
        XMLSignature signature = xsf.unmarshalXMLSignature(sig);
        boolean isvalid = signature.validate(dvc);

        if (!isvalid) {
            ValidationReport v = new ValidationReport("Signature not valid");
            boolean sigvalid = signature.getSignatureValue().validate(dvc);
            if (sigvalid) {
                StringBuilder refreport = new StringBuilder();
                java.util.Iterator it = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; it.hasNext(); j++) {
                    Reference refr = (Reference) it.next();
                    // rido2 - debug point. If select in tkw.properties file, will display actual and calculated digest values
                    // for the SignedInfo reference point.
                    if (isY(System.getProperty(DEBUG_DISPLAY_DIGEST_PROPERTY,"Y"))) {
                        org.apache.commons.codec.binary.Base64 b64 = new org.apache.commons.codec.binary.Base64();
                        String certDigest = new String(b64.encode(refr.getDigestValue()));
                        String calcDigest = new String(b64.encode(refr.getCalculatedDigestValue()));
                        System.err.println("Certificate digest = " + certDigest);
                        System.err.println("Calculated digest  = " + calcDigest);
                    }
                    // end digest debug code    
                    boolean refValid = refr.validate(dvc);
                    if (!refValid) {
                        refreport.append("Reference ");
                        refreport.append(j);
                        refreport.append(" is invalid: ");
                        java.io.InputStreamReader sis = new java.io.InputStreamReader(refr.getDigestInputStream());
                        char[] b = new char[10240];
                        sis.read(b);
                        String eref = new String(b);
                        refreport.append(eref.trim());
                        doHtmlEscapes(refreport);
                    }
                }
                v.setTest(refreport.toString());
                r.add(v);
            } else {
                v.setTest("Signature validation of included digest failed");
                r.add(v);
            }
        } else {
            ValidationReport v = new ValidationReport("Signature valid");
            v.setTest(" Issuer: " + x.getIssuerX500Principal().getName());
            v.setPassed();
            r.add(v);
        }
        return new ValidatorOutput(null, (ValidationReport[]) r.toArray(new ValidationReport[r.size()]));
    }

    static void doHtmlEscapes(StringBuilder sb) {
        doEscape(sb, "&", "&amp;");
        doEscape(sb, ">", "&gt;");
        doEscape(sb, "<", "&lt;");
        doEscape(sb, "\"", "&quot;");
        doEscape(sb, "'", "&apos;");
    }

        static void doEscape(StringBuilder sb, String s, String w) {
        int i = -1;
        int j = 0;
        while ((i = sb.indexOf(s, j)) != -1) {
            sb.replace(i, i + s.length(), w);
            j = i + 1;
            if (j == sb.length()) {
                return;
            }
        }
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
       vProvider = vp;
    }

}
