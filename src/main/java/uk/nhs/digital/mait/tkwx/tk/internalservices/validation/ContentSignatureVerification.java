/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.CfHNamespaceContext;
import uk.nhs.digital.mait.commonutils.util.dsig.SimpleKeySelector;
import org.xml.sax.InputSource;

/**
 *
 * @author DAMU2
 */
public class ContentSignatureVerification 
    implements ValidationCheck
{
    private VariableProvider vProvider = null;
    private String checkPart = null;
    private int attachmentNo = -1;
    
    @Override
    public void initialise()
            throws Exception
    {

    }

    @Override
    public String getSupportingData() { return null; }
    
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
    public void writeExternalOutput(String reportDirectory) throws Exception {}    
    
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
    public void setResource(String r) {}

    @Override
    public void setData(String d) throws Exception {}

    @Override
    public ValidatorOutput validate(String s, HashMap<String,Object> extraMessageInfo, boolean stripHeader)
            throws Exception
    {
        ArrayList<ValidationReport> r = new ArrayList<ValidationReport>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);      
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(s.toCharArray())));
        
        // 1. Get the distribution envelope
        // 2. Find all the ds:Signature nodes in it
        // 3. For each, validate

        NodeList signatures = doc.getElementsByTagNameNS(CfHNamespaceContext.DSNAMESPACE, "Signature");
        boolean sigsFound = false;
        for (int i = 0; i < signatures.getLength(); i++) {
            sigsFound = true;
            r.addAll(validateSignature(doc, (Element)signatures.item(i)));
        }
        if (!sigsFound) {
            ValidationReport v = new ValidationReport("No content signatures found");
            v.setPassed();
            r.add(v);            
        }
        return new ValidatorOutput(null, (ValidationReport[])r.toArray(new ValidationReport[r.size()]));
    }
    
    private ArrayList<ValidationReport> validateSignature(Document doc, Element sig) 
    {        
        ArrayList<ValidationReport> r = new ArrayList<ValidationReport>();
        try {            
            SimpleKeySelector sks = resolveKey(doc, sig);
            DOMStructure ds = new DOMStructure(sig);
            XMLSignatureFactory xsf = XMLSignatureFactory.getInstance("DOM");
            // DOMValidateContext dvc = new DOMValidateContext(sks, (Element)sig.getParentNode());
            DOMValidateContext dvc = new DOMValidateContext(sks, sig);
            dvc.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
            XMLSignature signature = xsf.unmarshalXMLSignature(ds);
//            XMLSignature signature = xsf.unmarshalXMLSignature(dvc);
            boolean isvalid = signature.validate(dvc);
            java.io.InputStreamReader isrcd = new java.io.InputStreamReader(signature.getSignedInfo().getCanonicalizedData());
            if (!isvalid) {
                ValidationReport v = new ValidationReport("Signature not valid");
                boolean sigvalid = signature.getSignatureValue().validate(dvc);
                if (sigvalid) {
                    StringBuilder refreport = new StringBuilder();
                    java.util.Iterator it = signature.getSignedInfo().getReferences().iterator();
                    for (int j = 0; it.hasNext(); j++) {
                        Reference refr = (Reference)it.next();
                        // rido2 - debug point. If select in tkw.properties file, will display actual and calculated digest values
                        // for the SignedInfo reference point.
//                            String digestDebug = System.getProperty(DEBUG_DISPLAY_DIGEST_PROPERTY);
//                            if ((digestDebug == null) || (digestDebug.toUpperCase().startsWith("Y"))) {
//                            org.apache.commons.codec.binary.Base64 b64 = new org.apache.commons.codec.binary.Base64();                        String certDigest = new String(b64.encode(refr.getDigestValue()));
//                            String calcDigest = new String(b64.encode(refr.getCalculatedDigestValue()));
//                            System.err.println("Certificate digest = " + certDigest);
//                            System.err.println("Calculated digest  = " + calcDigest);                
//                        }
                        // end digest debug code    
                        boolean refValid = refr.validate(dvc);
                        if (!refValid) {
                            refreport.append("Reference ");
                            refreport.append(j);
                            refreport.append(" is invalid: ");
                            java.io.InputStreamReader sis = new java.io.InputStreamReader(refr.getDigestInputStream());
                            char[] b = new char[10240];
                            sis.read(b);
                            String eref = (new String(b)).trim();
                            refreport.append(eref);
                            SignatureVerification.doHtmlEscapes(refreport);
                        }
                    }
                    v.setTest(refreport.toString());
                    r.add(v);
                } else {
                    v.setTest("Signature validation of included digest failed");
                    r.add(v);
                }
            } else {
                ValidationReport v = new ValidationReport("Content Signature valid");
                v.setTest(" Enveloping element: " + sig.getTagName());
                v.setPassed();
                r.add(v);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            r.add(new ValidationReport("Cannot resolve certificate for validation: " + e.toString()));
            return r;
        }
        return r;
    }
    
    private SimpleKeySelector resolveKey(Document doc, Element sig) 
            throws Exception
    {
        SimpleKeySelector sks = new SimpleKeySelector();
        NodeList cert = sig.getElementsByTagNameNS(CfHNamespaceContext.DSNAMESPACE, "X509Certificate");
        if (cert.getLength() == 0) {
            // Look for a URI
            cert = sig.getElementsByTagNameNS(CfHNamespaceContext.DSNAMESPACE, "RetrievalMethod");
            if (cert.getLength() == 0) {
                throw new Exception("No X509Certificate or RetrievalMethod given in KeyInfo - certificate cannot be resolved to verify signature");
            }
            Element certRef = (Element)cert.item(0);
            String u = certRef.getAttribute("URI");
            if ((u == null) || (u.trim().length() == 0)) {
                throw new Exception("No X509Certificate resolved: KeyInfo/RetrievalMethod/@URI absent or empty");
            }
            X509Certificate x = resolveUri(doc, u);
            if (x == null) {
                throw new Exception("Cannot resolve X509Certificate at URI " + u);
            }
            sks.setFixedKey(x.getPublicKey());            
        } else {
            // Certificate should be the text content of the KeyInfo/X509Certificate element
            Element x509 = (Element)cert.item(0);
            String encodedKey = x509.getTextContent();
            StringBuilder sb = new StringBuilder("-----BEGIN CERTIFICATE-----\n");
            sb.append(encodedKey);
            if (encodedKey.charAt(encodedKey.length() - 1) != '\n') {
                sb.append("\n");
            }
            sb.append("-----END CERTIFICATE-----");
            String skey = sb.toString();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate x = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(skey.getBytes()));
            sks.setFixedKey(x.getPublicKey());            
        }        
        return sks;
    }

    private X509Certificate resolveUri(Document doc, String u) 
            throws Exception
    {
        // Resolve the URI at u to a certificate and return it, or throw
        // an exception
        X509Certificate x = null;
        if (u.startsWith("#")) {
            x = resolveId(doc, u);
        } else {
            // External
            URI uri = new URI(u);
            URL url = uri.toURL();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            x = (X509Certificate)cf.generateCertificate(url.openStream());            
        }
        return x;
    }
    
    private X509Certificate resolveId(Document doc, String u)
            throws Exception
    {
        String id = u.substring(1);
        X509Certificate x = null;
        x = lookupReferences(doc, "id", id);
        if (x == null) {
            x = lookupReferences(doc, "Id", id);
        }
        return x;
    }
    
    private X509Certificate lookupReferences(Document doc, String name, String id)
            throws Exception
    {
        NodeList nl = doc.getElementsByTagName(name);
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
                Attr a = (Attr)n;
                if (a.getValue().contentEquals(id)) {
                   String s = ((Element)a.getParentNode()).getTextContent();
                   if ((s == null) || (s.trim().length() == 0)) {
                       throw new Exception("URI " + id + " resolved but no certificate found");
                   }
                   return getCertificate(s);
                }
            }
        }
        return null;
    }
    
    private X509Certificate getCertificate(String s)
            throws Exception
    {
        String skey = null;
        if (s.contains("-----BEGIN CERTIFICATE-----")) {
            skey = s;
        } else {
            StringBuilder sb = new StringBuilder("-----BEGIN CERTIFICATE-----\n");
            sb.append(s);
            if (s.charAt(s.length() - 1) != '\n') {
                sb.append("\n");
            }
            sb.append("-----END CERTIFICATE-----");
            skey = sb.toString();            
        }
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(skey.getBytes()));
        return x;
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
       vProvider = vp;
    }
}
