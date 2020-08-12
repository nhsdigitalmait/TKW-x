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

import java.io.StringReader;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.CfHNamespaceContext;
import org.xml.sax.InputSource;

/**
 * List the templates in a CDA document. Provide a unique list with a count
 * under test details. Fails if no ClinicalDocument found.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class CDATemplateLister 
    implements ValidationCheck
{
    private VariableProvider vProvider = null;
    private String checkPart = null;
    private int attachmentNo = -1;

    @Override
    public void initialise() throws Exception {}

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
    public ValidatorOutput validate(String o, HashMap<String,Object> extraMessageInfo, boolean stripHeader) 
            throws Exception
    {
        InputSource is = new InputSource(new StringReader(o));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document d = dbf.newDocumentBuilder().parse(is);
        NodeList nl = d.getElementsByTagNameNS(CfHNamespaceContext.HL7NAMESPACE, "templateId");
        if (nl.getLength() == 0) {
            ValidationReport v = new ValidationReport("Failed");
            v.setTest("Did not find any templateId elements in the HL7v3 namespace");
            ValidationReport[] report = new ValidationReport[1];
            report[0] = v;    
            return new ValidatorOutput("Template list", report);            
        }
        
        HashMap<String,Integer> templates = new HashMap<String,Integer>();
        for (int i = 0; i < nl.getLength(); i++) {
            Element t = (Element)nl.item(i);
            Attr oid = t.getAttributeNode("root");
            if (oid != null) {
                String oidcheck = oid.getTextContent();
                if ((oidcheck != null) && (oidcheck.contentEquals("2.16.840.1.113883.2.1.3.2.4.18.2"))) {
                    Attr ext = t.getAttributeNode("extension");
                    if (ext != null) {
                        String tcheck = ext.getTextContent();
                        if (tcheck != null) {
                            if (templates.containsKey(tcheck)) {
                                Integer tcount = templates.get(tcheck);
                                int count = tcount.intValue();
                                count++;
                                templates.put(tcheck, new Integer(count));
                            } else {
                                templates.put(tcheck, new Integer(1));
                            }
                        }
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder("Template references found (with counts):<br/>");
        Object[] templateNames = templates.keySet().toArray();
        java.util.Arrays.sort(templateNames);
        for (Object ob : templateNames) {
            String s = (String)ob;
            sb.append(s);
            sb.append(" (");
            sb.append(templates.get(s));
            sb.append(")<br/>");
        }
        ValidationReport v = new ValidationReport("Template list");
        v.setPassed();
        v.setTest(sb.toString());
        ValidationReport[] report = new ValidationReport[1];
        report[0] = v;    
        return new ValidatorOutput("Template list", report);
    }
    
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
    public String getSupportingData() { return null; }
    

    @Override
    public void setVariableProvider(VariableProvider vp) {
       vProvider = vp;
    }
}
