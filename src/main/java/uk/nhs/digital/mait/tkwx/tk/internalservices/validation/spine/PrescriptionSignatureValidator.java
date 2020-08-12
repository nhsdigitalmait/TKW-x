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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationCheck;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorOutput;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;
import uk.nhs.digital.mait.tkwx.util.dsig.CryptoProvider;
import uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author damian
 */
public class PrescriptionSignatureValidator 
implements ValidationCheck, URIDereferencer
{
    static final String FRAGMENTEXTRACTOR = "Prescriptionv0r1.xslt";
    static final String SPACESTRIPPER = "SignedInfowhitespacev0r1.xslt";    
    private String fragmentExtractorTransform = null;
    private String spaceStripperTransform = null;
    
    private String fragments = null;
    private CryptoProviderFactory cryptoFactory = null;
    
    @Override
    public void initialise() throws Exception {
        TransformManager t = TransformManager.getInstance();
        
        StringBuilder sb = new StringBuilder(FRAGMENTEXTRACTOR);
        sb.append(this.hashCode());
        fragmentExtractorTransform = sb.toString();
        sb = new StringBuilder(SPACESTRIPPER);
        sb.append(this.hashCode());
        spaceStripperTransform = sb.toString();
        t.addTransform(fragmentExtractorTransform, this.getClass().getResourceAsStream(FRAGMENTEXTRACTOR));
        t.addTransform(spaceStripperTransform, this.getClass().getResourceAsStream(SPACESTRIPPER));       
        cryptoFactory = new CryptoProviderFactory(false);
    }

    @Override
    public void setType(String t) {
        
    }

    @Override
    public void setResource(String r) {
        
    }

    @Override
    public void setData(String d) throws Exception {
        
    }

    @Override
    public Data dereference(URIReference r, XMLCryptoContext xcc) {
        OctetStreamData oct = new OctetStreamData(new ByteArrayInputStream(fragments.getBytes()));
        return oct;
    }
    
    @Override
    public ValidatorOutput validate(String o, HashMap<String,Object> extraMessageInfo, boolean stripHeader) throws Exception {
        throw new Exception("Only call validate(String o, boolean stripHeader) for ITK validations");
    }

    @Override
    public ValidationReport[] validate(SpineMessage sm)
            throws Exception 
    {
        ValidationReport ve = null;        
        String xml = sm.getHL7Part();
        if (xml == null) {
            ve = new ValidationReport("Failed because the HL7 part could not be read.");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        Document doc = null;
        Element sig = null;
        TransformManager tm = TransformManager.getInstance();
        try {
            fragments = tm.doTransform(fragmentExtractorTransform, xml);
        }
        catch (Exception e) {
            ve = new ValidationReport("Cannot read prescription or fragment extract failed");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        CryptoProvider cp = cryptoFactory.getProvider(CryptoProviderFactory.GOOD);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        try {
            doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(xml.toCharArray())));
            sig = (Element)doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0);
        }
        catch (IOException | ParserConfigurationException | SAXException e) {
            ve = new ValidationReport("Cannot read signature");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        if (sig == null){ 
            ve = new ValidationReport("Signature not found");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        ArrayList<String> reports = new ArrayList<>();
        // scf see mod below
        boolean p = cp.verify(sig, /*xml*/ fragments, this, reports);

        ValidationReport[] vreport = new ValidationReport[reports.size()];
        int i = 0;
        for (String s : reports) {
            ve = new ValidationReport(s);
            if (p)
                ve.setPassed();
        // scf see mod below
            vreport[i++] = ve;
        }
        return vreport;
    }

    @Override
    public String getSupportingData() {
        return "";
    }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
        
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        
    }
    
}
