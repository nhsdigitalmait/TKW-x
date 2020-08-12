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

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 * Render a CDA document and provide it as "supportingData". Error if transform
 * fails or no ClinicalDocument found. Do multiples using "for each"
 * functionality in the subroutine check.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class CDARenderer
        implements ValidationCheck, Copyable, Cloneable {

    // internally derived when validate is called must be retained for writeExternalOutput
    private String rendererOutput = null;
    private String outputFilename = null;
        
    // set when the object is initialised
    private VariableProvider vProvider = null;
    private XPathExpression cdaExtractor = null;
    
    private String checkPart = null;
    private int attachmentNo = -1;

    @Override
    public void initialise() throws Exception {
        TransformManager t = TransformManager.getInstance();
        try {
            t.addTransform("CDA_NPfIT_Document_Renderer.xsl", getClass().getResourceAsStream("CDA_NPfIT_Document_Renderer.xsl"));
        } catch (Exception e) {
            throw new Exception("Failed to initialise internal transform CDA_NPfIT_Document_Renderer.xsl : " + e.getMessage());
        }
        cdaExtractor = getXpathExtractor(CDAEXTRACTORXPATH);
    }

    @Override
    public void setType(String t) {
        int uscore = t.indexOf('_');
        if (uscore != -1) {
            checkPart = t.substring(0, uscore);
            attachmentNo = Integer.parseInt(checkPart.substring(10, checkPart.length())) - 1;
        }
    }

    /**
     * when the reportDirectory path is known then the rendered output can be
     * written to the file
     *
     * @param reportDirectory
     * @throws Exception
     */
    @Override
    public void writeExternalOutput(String reportDirectory)
            throws Exception {
        File f = new File(reportDirectory, outputFilename);
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(rendererOutput);
            fw.flush();
        }
    }

    @Override
    public void setResource(String r) {
    }

    @Override
    public void setData(String d) throws Exception {
    }

    /**
     * Generates the rendered output, creates a filename for it, creates a
     * validation report and returns it in ValidatorOutput
     *
     * @param o cda output
     * @param stripHeader
     * @return ValidatorOutput
     * @throws Exception
     */
    @Override
    public ValidatorOutput validate(String o, HashMap<String,Object> extraMessageInfo, boolean stripHeader)
            throws Exception {
        // Parse the input so we can get a node set out of it
        InputSource is = new InputSource(new StringReader(o));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document d = dbf.newDocumentBuilder().parse(is);
        NodeList nl = (NodeList) cdaExtractor.evaluate(d, javax.xml.xpath.XPathConstants.NODESET);

        if (nl.getLength() == 0) {
            ValidationReport v = new ValidationReport("Failed to render");
            v.setTest("Did not find any ClinicalDocument nodes in the HL7v3 namespace");
            return new ValidatorOutput("HTML rendering", new ValidationReport[]{ v });
        }
        TransformManager tm = TransformManager.getInstance();
        Element cda = (Element) nl.item(0);
        String serialisedcda = serialiseCDA(cda);
        rendererOutput = tm.doTransform("CDA_NPfIT_Document_Renderer.xsl", serialisedcda);
        outputFilename = rendererOutput.hashCode() + ".html";
        StringBuilder titleLink = new StringBuilder("<a href=\"./");
        titleLink.append(outputFilename);
        titleLink.append("\">Rendered</a>");

        ValidationReport v = new ValidationReport(titleLink.toString());
        v.setValidationCheck(this);
        v.setPassed();
        v.setTest("CDA rendered as HTML using standard transform for visual inspection");
        return new ValidatorOutput("HTML rendering", new ValidationReport[] { v });
    }

    private String extractBody(String h)
            throws Exception {
        return null;
    }

    private String serialiseCDA(Element cda)
            throws Exception {
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformManager.getInstance().getTransformerFactory().newTransformer();
        tx.transform(new DOMSource(cda), sr);
        return sw.toString();
    }

    @Override
    public ValidationReport[] validate(SpineMessage o)
            throws Exception {
        if (checkPart == null || checkPart.toLowerCase().startsWith("attachment")) {
            return validate(o.getATTACHMENTPart(attachmentNo), null, false).getReport();
        } else {
            throw new Exception("ITK validation of tertiary MIME part of spine message. Incorrect validation class used");
        }
    }

    @Override
    public String getSupportingData() {
        return null;
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        vProvider = vp;
    }

    @Override
    public Object copy()  {
        try {
            return (CDARenderer) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getInstance().log(SEVERE,CDARenderer.class.getName(), ex.getMessage());
        }
        return null;
    }
}
