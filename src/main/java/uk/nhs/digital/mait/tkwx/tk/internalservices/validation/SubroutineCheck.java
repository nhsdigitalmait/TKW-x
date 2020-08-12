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
import javax.xml.xpath.XPathExpression;
import org.xml.sax.InputSource;
import java.io.StringReader;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
 /**
 * Supports the "named subroutine" check type. The validation configuration file
 * parser stores the named subroutines in the ValidatorFactory. This class validates
 * by retrieving the subroutine, and calling its doValidations() method.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class SubroutineCheck 
    implements ValidationCheck
{
    private String subroutineName = null;
    private VariableProvider vProvider = null;    
    private XPathExpression context = null;
    private String contextExpression = null;
    private boolean containsVariable = false;
    private String variable = null;
    private String preVariable = null;
    private String postVariable = null;
    
    @Override
    public void initialise()
            throws Exception
    {
        if (subroutineName == null) {
            throw new Exception("Invalid subroutine name (null)");
        }
    }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {}
    
    @Override
    public void setType(String t) {
        
    }
    
    @Override
    public void setResource(String r) {
        subroutineName = r;
    }
    
    @Override
    public void setData(String d) 
            throws Exception
    {
        if (d != null) {
            containsVariable = false;
            if(d.contains("/$")||d.startsWith("$")){
                containsVariable = true;
                preVariable = d.substring(0, d.indexOf("$"));            
                postVariable = d.substring(d.indexOf("$"));
                Pattern variablePattern = null;
                Matcher m = null;
                variablePattern = Pattern.compile("^[A-Za-z0-9_$]");
                int i=0;
                for(i=0; i<postVariable.length();i++){
                    m = variablePattern.matcher(postVariable.substring(i,i+1));
                    if(!m.find()){
                        break;
                    }
                }
                //more than only the variable appended
                if(i != postVariable.length()){
                    variable = postVariable.substring(0,i);
                    postVariable = postVariable.substring(i);
                }else{
                    variable = postVariable;
                    postVariable = "";
                }
            }else{
                containsVariable = false;
                XpathCompile(d);            
            }
            contextExpression = d;
        }
    }

    private void XpathCompile(String d) throws Exception {
        context = getXpathExtractor(d);
    }
    
    @Override
    public ValidatorOutput validate(String o, HashMap<String,Object> extraMessageInfo, boolean stripHeader) 
            throws Exception
    {
        // Get a reference to the ValidatorFactory, and retrieve the
        // subroutine from it by name. The subroutine is a ValidationSet.
        // Call validate() on it.
        //
        ArrayList<ValidationReport> allReports = new ArrayList<ValidationReport>();
        ValidatorFactory vf = ValidatorFactory.getInstance();
        ValidationSet v = vf.getSubroutine(subroutineName);
        if (v == null) {
            throw new Exception("Subroutine " + subroutineName + " called but not defined.");
        }
        v.setVariableProvider(vProvider);
        if(containsVariable){
            StringBuilder sb = new StringBuilder();
            if(preVariable.trim().length()!=0){
                sb.append(preVariable.trim());
            }
            sb.append(CheckVariable(variable));
            if(postVariable.trim().length()!=0){
                sb.append(postVariable.trim());
            }
            XpathCompile(sb.toString());
        }
        if (context == null) {
            ArrayList<ValidationReport> rep =v.doValidations(o, extraMessageInfo, stripHeader, null);
            allReports.addAll(rep);
        } else {
            // Parse the input so we can get a node set out of it
            InputSource is = new InputSource(new StringReader(o));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document d = dbf.newDocumentBuilder().parse(is);
            NodeList nl = (NodeList)context.evaluate(d, javax.xml.xpath.XPathConstants.NODESET);
            int count = 0;
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element n = (Element)nl.item(i);
                    ++count;
                    Document dn = dbf.newDocumentBuilder().newDocument();
                    dn.adoptNode(n);
                    StringWriter sw = new StringWriter();
                    StreamResult sr = new StreamResult(sw);
                    Transformer tx = TransformManager.getInstance().getTransformerFactory().newTransformer();
                    tx.transform(new DOMSource(n), sr);
                    ArrayList<ValidationReport> rep = v.doValidations(sw.toString(), extraMessageInfo, stripHeader, null);
                    if ((rep != null) && (rep.size() > 0)) {
                        for (ValidationReport val : rep) {
                            val.setContext(contextExpression);
                            val.setIteration(count);
                        }
                        allReports.addAll(rep);
                    }
                }
            }
        }
        ValidationReport[] vrep = new ValidationReport[allReports.size()];
        int i = 0;
        for (ValidationReport r : allReports) {
            vrep[i++] = r;
        }
        return new ValidatorOutput(null, vrep);
    }
    
    @Override
    public ValidationReport[] validate(SpineMessage o) 
            throws Exception
    {
        throw new Exception("ITK validations only");
    }
    
    @Override
    public String getSupportingData() { return null; }

    private String CheckVariable(String s) {
        if(s.startsWith("$")){
            return (String)vProvider.getVariable(s);
        }
        else
        {
            return s;
        }
    }

    @Override
     public void setVariableProvider(VariableProvider vp) {
       vProvider = vp;
    }    
}
