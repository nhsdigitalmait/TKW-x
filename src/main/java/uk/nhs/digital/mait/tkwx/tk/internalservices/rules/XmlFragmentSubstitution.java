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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.om.NodeInfo;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.xmlReformat;

/**
 * Returns a string containing a serialised nodeset from the given xpath
 *
 * @author simonfarrow
 */
public class XmlFragmentSubstitution implements SubstitutionValue {

    private String xpath = null;

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
        XPathExpression extractor = getXpathExtractor(xpath);
        try {
            // see https://stackoverflow.com/questions/1985509/saxon-xpath-api-returns-tinyelementimpl-instead-of-org-w3c-dom-node
            Node node = NodeOverNodeInfo.wrap((NodeInfo) extractor.evaluate(new InputSource(new StringReader(o)), XPathConstants.NODE));
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            TransformerFactory txFactory = TransformManager.getInstance().getTransformerFactory();
            Transformer tx = txFactory.newTransformer();
            tx.setOutputProperty("omit-xml-declaration", "yes");
            tx.transform(new DOMSource(node), sr);
            return xmlReformat(sw.toString());
        } catch (javax.xml.xpath.XPathExpressionException ex) {
            return "";
        }
    }

    /**
     * One time setup for substitution
     *
     * @param s xpath to a single node in the request
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
        xpath = s;
    }
}
