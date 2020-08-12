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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.commonutils.util.ConfigurationStringTokeniser;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 * Apply an XSL transform to the file for validation. This uses a
 * case-insensitive "contains" test on the output of the transform (as a string)
 * to detect failure.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class XsltValidator
        implements ValidationCheck {

    private String transformFile = null;
    private String checkterm = null;
    private VariableProvider vProvider = null;
    private boolean containsVariable = false;
    private String variable = null;
    private String preVariable = null;
    private String postVariable = null;
    private String checkPart = null;
    private int attachmentNo = -1;

    protected String startingXpath = null;
    protected XPathExpression validationRootXpath = null;

    // Subclasses may already have executed the startingXpath, if they have, they'll
    // set this to prevent the getValidationRoot() trying to extract twice
    //
    protected boolean rootFound = false;

    public XsltValidator() {
    }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
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
    public void setResource(String t) {
        transformFile = t;
    }

    @Override
    public void setData(String d) {
        // Modification. Call the configuration tokeniser and see how many elements
        // the "data" string has. If one, that is the check term. If two, the
        // first is the starting Xpath expression and the second is the check term.
        if (d == null) {
            return;
        }
        ConfigurationStringTokeniser cst = new ConfigurationStringTokeniser(d);
        switch (cst.countTokens()) {
            case 1:
                checkterm = d;
                break;
            case 2:
                try {
                    startingXpath = cst.nextToken();
                    checkterm = cst.nextToken();
                } catch (Exception e) {
                    Logger.getInstance().error(XsltValidator.class.getName(), "Error parsing starting Xpath and check term: " + d);
                }
                break;
            default:
                break;
        }
    }

    protected Node getValidationRoot(StreamSource s, boolean stripHeader)
            throws Exception {
        try {
            InputSource is = new InputSource(s.getReader());
            Node n = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            Document doc = dbf.newDocumentBuilder().parse(is);
            if (stripHeader) {
                n = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body").item(0);
                if (n != null) {
                    NodeList nl = n.getChildNodes();
                    for (int i = 0; i < nl.getLength(); i++) {
                        Node x = nl.item(i);
                        if (x.getNodeType() == Node.ELEMENT_NODE) {
                            return x;
                        }
                    }
                }
                throw new Exception("Error evaluating validation root - cannot find SOAP Body content");
            } else {
                if (containsVariable) {
                    StringBuilder sb = new StringBuilder();
                    if (preVariable.trim().length() != 0) {
                        sb.append(preVariable.trim());
                    }
                    sb.append(CheckVariable(variable));
                    if (postVariable.trim().length() != 0) {
                        sb.append(postVariable.trim());
                    }
                    XpathCompile(sb.toString());
                }
                if (validationRootXpath == null) {
                    n = doc.getDocumentElement();
                } else {
                    n = (Node) validationRootXpath.evaluate(doc, XPathConstants.NODE);
                }
            }
            if (n == null) {
                throw new Exception("Failed to evaluate " + startingXpath + " no match");
            }
            return n;
        } catch (Exception e) {
            throw new Exception("Error evaluating validation root: " + e.getMessage());
        }
    }

    @Override
    public String getSupportingData() {
        return null;
    }

    @Override
    public void initialise()
            throws Exception {
        if (checkterm == null) {
            throw new Exception("No term provided to check for success");
        }
        TransformManager t = TransformManager.getInstance();
        try {
            t.addTransform(transformFile, transformFile);
        } catch (Exception e) {
            throw new Exception("Failed to initialise transform " + transformFile + " : " + e.getMessage());
        }
        if (startingXpath != null) {
            containsVariable = false;
            if (startingXpath.contains("/$") || startingXpath.startsWith("$")) {
                containsVariable = true;
                preVariable = startingXpath.substring(0, startingXpath.indexOf("$"));
                postVariable = startingXpath.substring(startingXpath.indexOf("$"));
                Pattern variablePattern = null;
                Matcher m = null;
                variablePattern = Pattern.compile("^[A-Za-z0-9_$]");
                int i = 0;
                for (i = 0; i < postVariable.length(); i++) {
                    m = variablePattern.matcher(postVariable.substring(i, i + 1));
                    if (!m.find()) {
                        break;
                    }
                }
                //more than only the variable appended
                if (i != postVariable.length()) {
                    variable = postVariable.substring(0, i);
                    postVariable = postVariable.substring(i);
                } else {
                    variable = postVariable;
                    postVariable = "";
                }
            } else {
                containsVariable = false;
                XpathCompile(startingXpath);
            }
        }
    }

    private void XpathCompile(String s) throws Exception {
        try {
            validationRootXpath = getXpathExtractor(s);
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Failed to compile validation root Xpath expression: " + startingXpath + " : " + e.toString());
        }
    }

    @Override
    public void setType(String t) {
        int uscore = t.indexOf('_');
        if (uscore == -1) {
        } else {
            checkPart = t.substring(0, uscore);
            attachmentNo = Integer.parseInt(checkPart.substring(10, checkPart.length())) - 1;
        }
    }

    @Override
    public ValidatorOutput validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader) throws Exception {
        ValidationReport report[] = null;
        String x = null;
        try {
            StringBuilder sb = new StringBuilder(transformFile);
            TransformManager t = TransformManager.getInstance();
            x = t.doTransform(transformFile, o);
            String c = checkterm.toUpperCase();
            if (x.toUpperCase().contains(c)) {
                ValidationReport v = new ValidationReport("Failed");
                sb.append(" returned:\n");
                sb.append(x);
                v.setTest(sb.toString());
                report = new ValidationReport[1];
                report[0] = v;
            } else {
                ValidationReport v = new ValidationReport("Pass");
                v.setPassed();
                sb.append(" returned:\n");
                sb.append(x);
                v.setTest(sb.toString());
                report = new ValidationReport[1];
                report[0] = v;
            }
        } catch (Exception e) {
            ValidationReport ex = new ValidationReport("Exception executing validation " + transformFile + " : " + e.getMessage());
            report = new ValidationReport[1];
            report[0] = ex;
        }
        return new ValidatorOutput(x, report);
    }

    private String CheckVariable(String s) {
        if (s.startsWith("$")) {
            return (String) vProvider.getVariable(s);
        } else {
            return s;
        }
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        vProvider = vp;
    }
}
