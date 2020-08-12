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

import javax.xml.xpath.XPathExpression;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.StringReader;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.*;

/**
 * SpineMessage version of the XpathAssertionValidator from the TKW validate
 * mode for ITK messages. Additionally supports case-insensitive assertions.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class XpathAssertionValidator
        implements ValidationCheck {

    private enum XpathValidationType {
        XPATHNOTDEFINED,
        XPATHEQUALS,
        XPATHNOTEQUALS,
        XPATHCONTAINS,
        XPATHNOTCONTAINS,
        XPATHEXISTS,
        XPATHNOTEXISTS,
        XPATHMATCHES,
        XPATHNOTMATCHES,
        XPATHCOMPARE,
        XPATHNOTCOMPARE,
        XPATHEQUALSIGNORECASE,
        XPATHNOTEQUALSIGNORECASE,
        XPATHCONTAINSIGNORECASE,
        XPATHNOTCONTAINSIGNORECASE
    };

    public enum CheckPartType {
        CHECKUNDEFINED,
        EBXML,
        HL7,
        SOAP
    }

    private String xpath = null;
    private String originalXpath = null;
    private String type = null;
    private String value = null;
    private XpathValidationType comparisonType = XpathValidationType.XPATHNOTDEFINED;

    private CheckPartType checkPart = CheckPartType.CHECKUNDEFINED;

    private Pattern regexPattern = null;
    private XPathExpression checkExpression = null;
    private XPathExpression checkExpressionSOAPHL7 = null;
    private XPathExpression comparisonExpression = null;
    private VariableProvider vProvider = null;

    @Override
    public void setResource(String t) {
        xpath = t;
    }

    @Override
    public void setType(String t) {
        type = t;
    }

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
    }

    @Override
    public void setData(String d) {
        value = d;
        if ((comparisonType == XpathValidationType.XPATHEQUALSIGNORECASE) || (comparisonType == XpathValidationType.XPATHNOTEQUALSIGNORECASE)
                || (comparisonType == XpathValidationType.XPATHNOTCONTAINSIGNORECASE) || (comparisonType == XpathValidationType.XPATHCONTAINSIGNORECASE)) {
            value = value.toLowerCase();
            return;
        }
    }

    @Override
    public String getSupportingData() {
        return null;
    }

    @Override
    public void initialise()
            throws Exception {
        String tgt = null;
        String ctype = null;
        int uscore = type.indexOf('_');
        if (uscore == -1) {
            System.err.println(XpathAssertionValidator.class.getName() + ".initialise Spine message validation part not given, assuming HL7");
            ctype = type;
            checkPart = CheckPartType.HL7;
        } else {
            tgt = type.substring(0, uscore);
            switch (tgt) {
                case "ebxml":
                case "hl7":
                case "soap":
                    checkPart = CheckPartType.valueOf(tgt.toUpperCase());
                    break;
                default:
            }
            ctype = type.substring(uscore + 1);
        }

        comparisonType = XpathValidationType.valueOf(ctype.toUpperCase());

        switch (comparisonType) {
            case XPATHNOTDEFINED:
                throw new Exception("Unrecognised Xpath check type " + type);

            case XPATHCOMPARE:
            case XPATHNOTCOMPARE:
                if (comparisonExpression != null) {
                    return;
                }
                if (value != null) {
                    try {
                        comparisonExpression = getXpathExtractor(value);
                    } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
                        Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(),
                                "Failed to compile second XPath expression: " + value + "\r\n" + e.getMessage());
                    }
                }
                break;
        }

        try {
            checkExpression = getXpathExtractor(xpath);
            if (checkPart == CheckPartType.HL7) {
                checkExpressionSOAPHL7 = getXpathExtractor(xpath.replace("/*[1]", HEADERSTRIPPER));
            }
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(),
                    "Error compiling expression: " + xpath + "\r\n" + e.getMessage());
            throw e;
        }

        switch (comparisonType) {
            case XPATHMATCHES:
            case XPATHNOTMATCHES:
                regexPattern = Pattern.compile(value);
                break;

            case XPATHCOMPARE:
            case XPATHNOTCOMPARE:
                if (comparisonExpression == null) {
                    if (value != null) {
                        try {
                            comparisonExpression = getXpathExtractor(value);
                        } catch (Exception e) {
                            Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(),
                                    "Error compiling expression: " + value + "\r\n" + e.getMessage());
                            throw e;
                        }
                    }
                }
                break;

            case XPATHEQUALSIGNORECASE:
            case XPATHNOTEQUALSIGNORECASE:
            case XPATHCONTAINSIGNORECASE:
            case XPATHNOTCONTAINSIGNORECASE:
                if (value != null) {
                    value = value.toLowerCase();
                }
                break;
        }
    }

    @Override
    public ValidatorOutput validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader)
            throws Exception {
        throw new Exception("Only call validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader) for ITK validations");
    }

    private ValidationReport[] doExistsValidation(SpineMessage sm)
            throws Exception {
        Document d = null;
        ValidationReport ve = null;
        StringBuilder sb = new StringBuilder("Xpath ");
        if (sm.getSOAPIncludedInHL7Part() && (checkPart == CheckPartType.HL7)) {
            sb.append(Utils.htmlEncode(xpath.replace("/*[1]", HEADERSTRIPPER)));
        } else {
            sb.append(Utils.htmlEncode(xpath));
        }

        switch (checkPart) {
            case EBXML:
                d = sm.getEbXmlDoc();
                break;

            case HL7:
                d = sm.getHL7Doc();
                break;

            case SOAP:
                if (sm.getSOAPIncludedInHL7Part()) {
                    d = sm.getSOAPDoc();
                } else {
                    d = sm.getEbXmlDoc();
                }
                break;
        }
        if (d == null) {
            ve = new ValidationReport("Failed");
            sb.append(" returned no match because the sample could not be read correctly.");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        NodeList nl;
        if (sm.getSOAPIncludedInHL7Part() && (checkPart == CheckPartType.HL7)) {
            nl = (NodeList) checkExpressionSOAPHL7.evaluate(d, javax.xml.xpath.XPathConstants.NODESET);
        } else {
            nl = (NodeList) checkExpression.evaluate(d, javax.xml.xpath.XPathConstants.NODESET);
        }
        switch (comparisonType) {
            case XPATHEXISTS:
                if (nl.getLength() > 0) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned content");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match");
                }
                break;
            case XPATHNOTEXISTS:
                if (nl.getLength() == 0) {
                    sb.append(" returned no match");
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned content when it was expected to return no match");
                }
                break;

        }
        ve.setTest(sb.toString());
        ValidationReport[] vreport = new ValidationReport[1];
        vreport[0] = ve;
        return vreport;
    }

    @Override
    public ValidationReport[] validate(SpineMessage sm)
            throws Exception {
        if ((comparisonType == XpathValidationType.XPATHEXISTS) || (comparisonType == XpathValidationType.XPATHNOTEXISTS)) {
            return doExistsValidation(sm);
        }
        ValidationReport ve = null;
        Matcher m = null;
        StringBuilder sb = new StringBuilder("Xpath ");
        if (sm.getSOAPIncludedInHL7Part() && (checkPart == CheckPartType.HL7)) {
            sb.append(xpath.replace("/*[1]", HEADERSTRIPPER));
        } else {
            sb.append(xpath);
        }
        String o = null;
        // Get the appropriate string out of the SpineMessage depending on what
        // part of the message we're wanting to validate.
        switch (checkPart) {
            case EBXML:
                o = sm.getEbXmlPart();
                break;

            case HL7:
                o = sm.getHL7Part();
                break;

            case SOAP:
                o = sm.getSoap();
                break;
        }
        if (o == null) {
            ve = new ValidationReport("Failed");
            sb.append(" returned no match because the sample could not be read correctly.");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        InputSource is = new InputSource(new StringReader(o));
        String r;
        if (sm.getSOAPIncludedInHL7Part() && (checkPart == CheckPartType.HL7)) {
            r = checkExpressionSOAPHL7.evaluate(is);
        } else {
            r = checkExpression.evaluate(is);
        }
        String v = null;
        if (comparisonExpression != null) {
            is = new InputSource(new StringReader(o));
            v = comparisonExpression.evaluate(is);
        }
        switch (comparisonType) {
            case XPATHCOMPARE:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (r.contentEquals(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" and ");
                    sb.append(value);
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed ");
                    sb.append(" returned \"");
                    sb.append(r);
                    sb.append("\" when ");
                    sb.append(value);
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\" when a match was expected");
                }
                break;
            case XPATHNOTCOMPARE:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (!r.contentEquals(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned \"");
                    sb.append(r);
                    sb.append("\" and ");
                    sb.append(value);
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed ");
                    sb.append(" and ");
                    sb.append(value);
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\" when no  match was expected");
                }
                break;
            case XPATHEQUALSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHEQUALS:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(value);
                    sb.append("\"");
                } else if (r.contentEquals(value)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned \"");
                    sb.append(r);
                    sb.append("\" when \"");
                    sb.append(value);
                    sb.append("\" was expected");
                }
                break;
            case XPATHNOTEQUALSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHNOTEQUALS:
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    sb.append(" returned no match, it was expected to return a value other than \"");
                    sb.append(value);
                    sb.append("\"");
                } else if (!r.contentEquals(value)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" does not return \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned \"");
                    sb.append(value);
                    sb.append("\", it was expected to return something other than that.");
                }
                break;
            case XPATHCONTAINSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHCONTAINS:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value containing \"");
                    sb.append(value);
                    sb.append("\"");
                } else if (r.contains(value)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" result contains \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" result does not contain \"");
                    sb.append(value);
                    sb.append("\"");
                }
                break;
            case XPATHNOTCONTAINSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHNOTCONTAINS:
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    sb.append(" expected to return a value not containing \"");
                    sb.append(value);
                    sb.append("\" but returned no match");
                } else if (r.contains(value)) {
                    ve = new ValidationReport("Failed");
                    sb.append(" contains \"");
                    sb.append(value);
                    sb.append("\" but was expected not to");
                } else {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" does not contain \"");
                    sb.append(value);
                    sb.append("\"");
                }
                break;
            case XPATHMATCHES:
                m = regexPattern.matcher(r);
                if (m.find()) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" matches \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" does not match \"");
                    sb.append(value);
                    sb.append("\" when expected");
                }
                break;
            case XPATHNOTMATCHES:
                m = regexPattern.matcher(r);
                if (m.find()) {
                    ve = new ValidationReport("Failed");
                    sb.append(" matches \"");
                    sb.append(value);
                    sb.append("\" when not expected");
                } else {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" does not match \"");
                    sb.append(value);
                    sb.append("\"");
                }
                break;
            case XPATHNOTDEFINED:
            default:
                throw new Exception("No or invalid comparison type defined");
        }
        ve.setTest(sb.toString());
        ValidationReport[] vreport = new ValidationReport[1];
        vreport[0] = ve;
        return vreport;
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        vProvider = vp;
    }
}
