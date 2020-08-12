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

import uk.nhs.digital.mait.tkwx.validator.SaxValidator;
import uk.nhs.digital.mait.tkwx.validator.DomValidator;
import uk.nhs.digital.mait.tkwx.validator.ValidatorServiceErrorHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Document;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;

/**
 * Parameterised with the schema file name and optionally an Xpath expression to
 * identify the validation root. Applies the given schema to the explicit
 * validation root, the document root, or the SOAP body depending on whether the
 * Xpath expression is given, the file to validate is a "bare" body (using the
 * VALIDATE-AS mechanism or by reading a distribution envelope "service"
 * attribute, or is presented as a complete SOAP envelope.
 *
 * This class does not produce supportingData.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SchemaValidator
        extends SchemaValidationReporter
        implements ValidationCheck {

    private VariableProvider vProvider = null;
    private boolean containsVariable = false;
    private String variable = null;
    private String preVariable = null;
    private String postVariable = null;
    private String checkPart = null;
    private int attachmentNo = -1;

    protected String schemaFile = null;
    protected String startingXpath = null;

    // Subclasses may already have executed the startingXpath, if they have, they'll
    // set this to prevent the getValidationRoot() trying to extract twice
    //
    protected boolean rootFound = false;

    protected XPathExpression validationRootXpath = null;
    protected XPathExpression headerStripper = null;

    public SchemaValidator()
            throws Exception {
        try {
            headerStripper = getXpathExtractor(HEADERSTRIPPER);
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Failed to compile validation root Xpath expression: " + HEADERSTRIPPER + " : " + e.getMessage());
        }
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
    public void writeExternalOutput(String reportDirectory) throws Exception {
    }

    @Override
    public void setResource(String s) {
        schemaFile = s;
    }

    @Override
    public void setData(String d)
            throws Exception {
//        if ((d != null) && (d.trim().length() > 0)) {
//            System.err.println("Warning: Additional data passed to schema validator check, currently not supported");
//        }
//        return;
        String data = null;
        if ((d != null) && (d.trim().length() > 0)) {
            data = d;
        }
        startingXpath = data;
        if (data == null) {
            return;
        }
        containsVariable = false;
        if (d.contains("/$") || d.startsWith("$")) {
            containsVariable = true;
            preVariable = d.substring(0, d.indexOf("$"));
            postVariable = d.substring(d.indexOf("$"));
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
            XpathCompile();
        }
    }

    private void XpathCompile() throws Exception {
        try {
            validationRootXpath = getXpathExtractor(startingXpath);
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Failed to compile validation root Xpath expression: " + startingXpath + " : " + e.getMessage());
        }
    }

    @Override
    public void addValidationExceptionDetail(String s) {
        ValidationReport r = new ValidationReport(s);
        validatorExceptions.add(r);
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
    public void initialise()
            throws Exception {

    }

    /**
     *
     * @param message
     * @param extraMessageInfo
     * @param stripHeader boolean
     * @return ValidatorOutput
     * @throws Exception
     */
    @Override
    public ValidatorOutput validate(String message, HashMap<String, Object> extraMessageInfo, boolean stripHeader) throws Exception {
        boolean actuallyRemoveSoapBody = stripHeader;
        if (containsVariable) {
            StringBuilder sb = new StringBuilder();
            if (preVariable.trim().length() != 0) {
                sb.append(preVariable.trim());
            }
            sb.append(CheckVariable(variable));
            if (postVariable.trim().length() != 0) {
                sb.append(postVariable.trim());
            }
            startingXpath = sb.toString();
            XpathCompile();
        }
        if (startingXpath != null) {
            actuallyRemoveSoapBody = false;
        }
        if (message == null) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error: null content");
            return new ValidatorOutput(null, e);
        }
        try {
            validatorExceptions = new ArrayList<>();
            ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(this);
            StringReader subjectReader = new StringReader(message);
            StreamSource subjectSource = new StreamSource(subjectReader);
            File f = new File(schemaFile);
            StreamSource schemaSource = new StreamSource(f);
            if (actuallyRemoveSoapBody) {
                doDomValidation(subjectSource, schemaSource, v, (startingXpath == null));
            } else if ((rootFound) || (startingXpath == null)) {
                InputSource is = new InputSource(subjectReader);
                SaxValidator sv = new SaxValidator(is, schemaSource);
                sv.validate(v);
            } else {
                doDomValidation(subjectSource, schemaSource, v, false);
            }
        } catch (Exception e1) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error during validation: " + e1.getMessage());
            return new ValidatorOutput(null, e);
        }
        if (validatorExceptions.isEmpty()) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Pass");
            e[0].setTest(schemaFile);
            e[0].setPassed();
            return new ValidatorOutput(null, e);
        }
        return new ValidatorOutput(null, (ValidationReport[]) validatorExceptions.toArray(new ValidationReport[validatorExceptions.size()]));
    }

    protected void doDomValidation(StreamSource toValidate, StreamSource schema, ValidatorServiceErrorHandler v, boolean stripHeader)
            throws Exception {
        Node n = getValidationRoot(toValidate, stripHeader);
        DomValidator dv = new DomValidator(n, schema);
        dv.validate(v);
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
            } else if (validationRootXpath == null) {
                n = doc.getDocumentElement();
            } else {
                n = (Node) validationRootXpath.evaluate(doc, XPathConstants.NODE);
            }
            if (n == null) {
                throw new Exception("Failed to evaluate " + startingXpath + " no match");
            }
            return n;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error evaluating validation root: " + e.getMessage());
        }
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
