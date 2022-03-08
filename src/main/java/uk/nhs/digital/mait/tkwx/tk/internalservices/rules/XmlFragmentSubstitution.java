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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import static java.util.logging.Level.SEVERE;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.om.NodeInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;
import static uk.nhs.digital.mait.tkwx.util.Utils.xmlReformat;

/**
 * Returns a string containing a serialised nodeset from the given xpath
 *
 * @author simonfarrow
 */
public class XmlFragmentSubstitution implements SubstitutionValue {

    private String xpath = null;
    private String transformFile = null;
    private String[] transformParams = null;
    

    /**
     * public constructor
     */
    public XmlFragmentSubstitution() {
    }

    /**
     * evaluated at test time
     *
     * @param o the payload of the received message
     * @return the String containing the serialised nodeset of the given xpath
     * @throws Exception
     */
    @Override
    public String getValue(String o) throws Exception {
        if(o.trim().length()==0){
            return "";
        }
        XPathExpression extractor = getXpathExtractor(xpath);
        try {
            // see https://stackoverflow.com/questions/1985509/saxon-xpath-api-returns-tinyelementimpl-instead-of-org-w3c-dom-node
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            
            TransformerFactory txFactory = TransformManager.getInstance().getTransformerFactory();
            Transformer tx = txFactory.newTransformer();
            tx.setOutputProperty("omit-xml-declaration", "yes");
            
            Node node = NodeOverNodeInfo.wrap((NodeInfo) extractor.evaluate(new InputSource(new StringReader(o)), XPathConstants.NODE));
            if(node==null){
                return "";
            }
            tx.transform(new DOMSource(node), sr);
            
            // if there's a trasform file supplied then post processs by applying it to the fragment with any optional parameters
            if (transformFile != null) {
                Transformer postTransformer = txFactory.newTransformer(new StreamSource(transformFile));
                
                // these come in pairs of name value
                if(transformParams!=null){
                    for (int i  = 0; i < transformParams.length; i += 2){
                        postTransformer.setParameter(transformParams[i], transformParams[i+1]);
                    }
                }
  
                // do the post extract transform
                StringWriter sw1 = new StringWriter();
                StreamResult sr1 = new StreamResult(sw1);
                postTransformer.transform(new StreamSource(new StringReader(sw.toString())), sr1);
                return xmlReformat(sw1.toString());
            } else {
                return xmlReformat(sw.toString());
            }
            
        } catch (javax.xml.xpath.XPathExpressionException | NullPointerException ex) {
                Logger.getInstance().log(SEVERE, XmlFragmentSubstitution.class.getName(),
                        "xsl Transform error "+ex.getMessage());
            return "";
        }
    }

    /**
     * One time setup for substitution
     *
     * @param s xpath to a single node in the request optional transform and pairs of parameter name values
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
        String[] params = s.split("\\s");
        switch ( params.length ) {
            case 0:
                throw new IllegalArgumentException("XmlFragmentSubstitution: Illegal number of arguments "+params.length);
            case 1:
                xpath = params[0];
                break;
            case 2:
                xpath = params[0];
                transformFile = params[1];
                break;
            default:
                xpath = params[0];
                transformFile = params[1];
                if (!fileExists(transformFile)) {
                    throw new IllegalArgumentException(String.format("XmlFragmentSubstitution: Transform file %s does not exist\n",transformFile));
                }
                transformParams = Arrays.copyOfRange(params, 2, params.length);
                if (transformParams.length % 2 != 0) {
                    throw new IllegalArgumentException(String.format("XmlFragmentSubstitution: Invalid number of transform parameters %d (must be even)\n",transformParams.length ));
                }
        }
    }
}
