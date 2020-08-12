/*
 Copyright 2017  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.meshinterceptor;

import java.io.StringReader;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.om.NodeInfo;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.tk.boot.MeshTransport;
import org.xml.sax.InputSource;

/**
 * Class to enable extract of MESH elements from the XML request
 * @author Richard Robinson 
 */
public abstract class AbstractMeshRequestHandler extends uk.nhs.digital.mait.tkwx.tk.boot.ToolkitMeshHandler {

    protected XPathExpression getRequestId = null;
    protected XPathExpression getResponseId = null;
    protected XPathExpression getEventCode = null;
    protected XPathExpression getPriorityCode = null;
    protected XPathExpression getPriorityDisplay = null;


    protected String extractRequestId(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getRequestId, s);
    }
    protected String extractResponseId(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getResponseId, s);
    }
    protected String extractEventCode(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getEventCode, s);
    }

    protected String extractPriorityCode(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getPriorityCode, s);
    }

    protected String extractPriorityDisplay(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getPriorityDisplay, s);
    }

    protected synchronized Node extractNodeXpath(XPathExpression x, InputSource m)
            throws Exception {
        // this fails with cannot cast saxon tinyElementImpl to dom Node
        // see http://stackoverflow.com/questions/1985509/saxon-xpath-api-returns-tinyelementimpl-instead-of-org-w3c-dom-node
        //return (Node) x.evaluate(m, XPathConstants.NODE);

        // this is recommended workaround
        return (Node) NodeOverNodeInfo.wrap((NodeInfo) x.evaluate(m, XPathConstants.NODE));
    }

    protected synchronized String extractStringXpath(XPathExpression x, InputSource m)
            throws Exception {
        return (String) x.evaluate(m, XPathConstants.STRING);
    }

    protected String getSavedMessagesDirectory() {
        return savedMessagesDirectory;
    }

    protected MeshTransport getToolkit() {
        return super.toolkit;
    }
}
