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
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.ConfigurationTokenSplitter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import static uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor.BODY_EXTRACTOR_LABEL;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;

/**
 * ValidationCheck parameterised with an Xpath expression, a check type and for
 * check types based on equivalence, an assertion value. Comparisons are case
 * sensitive. Supported check types are:
 *
 * "xpathequals" Pass if Xpath expression result equals the assertion value
 * "xpathnotequals" Pass if Xpath expression result does not equal the assertion
 * value "xpathcontains" Pass if the Xpath expression contains the assertion
 * value "xpathnotcontains" Pass if the Xpath expression does not contain the
 * assertion value "xpathexists" Pass if the Xpath expression points to a node
 * (element, attribute or text) which exists "xpathnotexists" Pass if the Xpath
 * expression does not point to a node which exists "xpathmatches" Pass if the
 * Xpath expression matches the assertion value where the assertion is a regular
 * expression "xpathnotmatches" Pass if the Xpath expression does not match the
 * assertion value where the assertion is a regular expression "xpathin" Pass if
 * the Xpath expression result is one of the given list of assertion values
 * "xpathcompare" Pass if the 1st Xpath expression result matched the second
 * xpath expression result "xpathnotcompare" Pass if the 1st Xpath expression
 * result does not match the second xpath expression result
 *
 * @author Damian Murphy murff@warlock.org
 */
public class XpathAssertionValidator
        implements ValidationCheck, VariableConsumer {

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
        XPATHIN,
        XPATHNOTIN,
        XPATHCOMPARE,
        XPATHNOTCOMPARE
    }

    private String[] inList = null;
    private String xpath = null;
    private String type = null;
    private String value = null;
    private XpathValidationType comparisonType = XpathValidationType.XPATHNOTDEFINED;
    private String xmlSource;

    private VariableProvider vProvider = null;
    private Pattern regexPattern = null;
    private XPathExpression checkExpression = null;
    private XPathExpression comparisonExpression = null;
    private boolean containsVariable = false;
    private String variable = null;
    private String preVariable = null;
    private String postVariable = null;
    private String checkPart = null;
    private int attachmentNo = -1;
    private boolean existsTest = false;
    private JWTParser jwtParser = null;

    // xml source types
    public static final String CONTENT = "content"; // xml body of message
    public static final String JWT_HEADER = "jwt_header"; // xml version of json header of Json Web Token
    public static final String JWT_PAYLOAD = "jwt_payload"; // xml version of json payload of Json Web Token

    // http header name
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
    }

    /**
     * sets the required xpath String for the validation
     *
     * @param xpath
     */
    @Override
    public void setResource(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Do the validation for a SpineMessage
     *
     * @param o SpineMessage object
     * @return Array of ValidationReport
     * @throws Exception
     */
    @Override
    public ValidationReport[] validate(SpineMessage o)
            throws Exception {
        if (checkPart == null || checkPart.toLowerCase().startsWith("attachment")) {
            return validate(o.getATTACHMENTPart(attachmentNo), null, false).getReport();
        } else {
            throw new Exception("ITK validation of tertiary MIME part of spine message. Incorrect validation class used");
        }
    }

    /**
     * set the type of the assertion (lower case) eg xpathexists (Assumes part
     * name is 10 chars + a number) for a spine message this can be a multi part
     * string of the form
     * &lt;attachment123456_xpathexists&gt;&lt;number&gt;_&lt;type&gt;
     *
     * @param t
     */
    @Override
    public void setType(String t) {
        final String MATCH = "attachment";
        if (t.matches("^" + MATCH + "[0-9]+_.*$")) {
            type = t.replaceFirst("^.*_(.*)$", "$1");
            checkPart = t.replaceFirst("^(.*)_.*$", "$1");
            attachmentNo = Integer.parseInt(checkPart.substring(MATCH.length(), checkPart.length())) - 1;
        } else {
            type = t;
        }
    }

    /**
     * String containing comparison value or second xpath against which to check
     *
     * @param d
     */
    @Override
    public void setData(String d) {
        value = d;
    }

    /**
     * returns JWT Details or null as appropriate
     *
     * @return null or any supporting text to be written to an appropriately
     * named file in the validation reports folder
     */
    @Override
    public String getSupportingData() {
        if (xmlSource != null && (xmlSource.equals(JWT_HEADER) || xmlSource.equals(JWT_PAYLOAD)) && jwtParser != null) {
            return jwtParser.getSupportingData();
        }
        return null;
    }

    @Override
    public void initialise()
            throws Exception {
        
        comparisonExpression = null;

        xmlSource = CONTENT; // default to content
        // type may be followed by an optional xml source string
        String[] params = type.split("\\s+");
        switch (params.length) {
            case 2:
                xmlSource = params[1];
            // drop through
            case 1:
                type = params[0];
                break;
            default:
                Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(), "Invalid number of parameters in XpathAssertion type " + params.length);
        }

        comparisonType = XpathValidationType.valueOf(type.toUpperCase());

        existsTest = false;
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
                        Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(), "Failed to compile second XPath expression: " + value);
                    }
                }
                break;
            case XPATHEXISTS:
            case XPATHNOTEXISTS:
                existsTest = true;
        }

        containsVariable = false;
        // TODO suspect the first of these tests does not work as intended
        if (xpath.contains("/$") || xpath.startsWith("$")) {
            containsVariable = true;
            preVariable = xpath.substring(0, xpath.indexOf("$"));
            postVariable = xpath.substring(xpath.indexOf("$"));
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
            if ((existsTest)) {
                xpath = "count(" + xpath + ")";
            }
            xpathCompile(xpath);
        }

        switch (comparisonType) {
            case XPATHMATCHES:
            case XPATHNOTMATCHES:
                regexPattern = Pattern.compile(value);
                break;
            case XPATHIN:
            case XPATHNOTIN:
                if (value != null) {
                    inList = (new ConfigurationTokenSplitter(value)).split();
                }
                break;
            case XPATHCOMPARE:
            case XPATHNOTCOMPARE:
                if (comparisonExpression == null) {
                    if (value != null) {
                        xpathCompile(xpath);
                    }
                }
                break;
        }
    }

    private void xpathCompile(String s) throws Exception {
        checkExpression = getXpathExtractor(s);
    }

    /**
     * perform the validation
     *
     * @param o the xml string on which the assertion is to be tested
     * @param extraMessageInfo
     * @param stripHeader boolean
     * @return ValidatorOutput
     * @throws Exception
     */
    @Override
    public ValidatorOutput validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader)
            throws Exception {

        if (extraMessageInfo != null) {
            AbstractBodyExtractor be = null;
            for (String key : extraMessageInfo.keySet()) {
                switch (key) {
                    case BODY_EXTRACTOR_LABEL:
                        be = (AbstractBodyExtractor) extraMessageInfo.get(key);
                        break;
                    default:
                        Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(), "Unexpected extraMessageInfo key type " + key);
                }
            }

            if (be != null) {
                HttpHeaderManager headerManager = be.getRelevantHttpHeaders();
                String jwtb64 = headerManager != null ? headerManager.getHttpHeaderValue(AUTHORIZATION_HEADER) : null;
                if (jwtb64 != null) {
                    switch (xmlSource) {
                        case JWT_HEADER:
                            jwtParser = new JWTParser(jwtb64);
                            // the xml to be analysed is from the JWT
                            o = jwtParser.getXmlHeader();
                            break;
                        case JWT_PAYLOAD:
                            jwtParser = new JWTParser(jwtb64);
                            // the xml to be analysed is from the JWT
                            o = jwtParser.getXmlPayload();
                            break;
                        case CONTENT:
                            break;
                        default:
                            Logger.getInstance().log(SEVERE, XpathAssertionValidator.class.getName(), "Unrecognised xmlSource " + xmlSource);
                    }
                }
            } // no be
        } // no extra message info

        ValidationReport ve = null;
        Matcher m = null;
        if (containsVariable) {
            StringBuilder sb = new StringBuilder();
            if (preVariable.trim().length() != 0) {
                sb.append(preVariable.trim());
            }
            sb.append(CheckVariable(variable));
            if (postVariable.trim().length() != 0) {
                sb.append(postVariable.trim());
            }
            if ((existsTest)) {
                sb.insert(0, "count(");
                sb.append(")");
            }
            xpathCompile(sb.toString());
        }
        StringBuilder sb = new StringBuilder("Xpath ");
        sb.append(Utils.htmlEncode(xpath));
        InputSource is = null;
        String r = null;
        if (!Utils.isNullOrEmpty(o)) {
            is = new InputSource(new StringReader(o));
            try {
                r = checkExpression.evaluate(is);
            } catch (XPathExpressionException ex) {
                // log this here so we can find locus more quickly. The exception still needs to be thrown and managed by the reporter though
                Logger.getInstance().log(WARNING, XpathAssertionValidator.class.getName(), "Error evaluating checkExpression InputSource " + ex.getMessage());
                throw ex;
            }
        }
        String v = null;
        if (comparisonExpression != null) {
            if (!Utils.isNullOrEmpty(o)) {
                is = new InputSource(new StringReader(o));
                try {
                    v = comparisonExpression.evaluate(is);
                } catch (XPathExpressionException ex) {
                    // log this here so we can find locus more quickly. The exception still needs to be thrown and managed by the reporter though
                    Logger.getInstance().log(WARNING, XpathAssertionValidator.class.getName(), "Error evaluating comparisonExpression InputSource " + ex.getMessage());
                    throw ex;
                }
            }
        }
        int nodeCount = 0;
        if (existsTest) {
            if (r != null) {
                nodeCount = Integer.parseInt(r);
            }
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

            case XPATHIN:
                ve = null;
                if ((inList == null) || (inList.length == 0)) {
                    sb.append(" no match list given");
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    break;
                }
                if ((r == null) || (r.trim().length() == 0)) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match");
                    break;
                }
                for (String s : inList) {
                    if (r.contentEquals(CheckVariable(s))) {
                        sb.append(" matches ");
                        sb.append(CheckVariable(s));
                        sb.append(" in ");
                        sb.append(value);
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        break;
                    }
                }
                if (ve == null) {
                    ve = new ValidationReport("Failed");
                    if (r.length() < 128) {
                        sb.append(r);
                    } else {
                        sb.append(r.substring(0, 128));
                        sb.append("...");
                    }
                    sb.append(" does not match any item in list ");
                    sb.append(value);
                }
                break;

            case XPATHNOTIN:
                ve = null;
                if ((inList == null) || (inList.length == 0)) {
                    sb.append(" no match list given");
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    break;
                }
                if ((r == null) || (r.trim().length() == 0)) {
                    ve = new ValidationReport("Pass");
                    sb.append(" returned no match");
                    ve.setPassed();
                    break;
                }
                for (String s : inList) {
                    if (r.contentEquals(CheckVariable(s))) {
                        sb.append(" matches ");
                        sb.append(CheckVariable(s));
                        sb.append(" in ");
                        sb.append(value);
                        ve = new ValidationReport("Failed");
                        break;
                    }
                }
                if (ve == null) {
                    ve = new ValidationReport("Pass");
                    if (r.length() < 128) {
                        sb.append(r);
                    } else {
                        sb.append(r.substring(0, 128));
                        sb.append("...");
                    }
                    ve.setPassed();
                    sb.append(" does not match any item in list ");
                    sb.append(value);
                }
                break;

            case XPATHEXISTS:
                if (nodeCount > 0) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned ").append(nodeCount);
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned ").append(nodeCount);
                }
                break;
            case XPATHNOTEXISTS:
                if (nodeCount == 0) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned ").append(nodeCount);
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned ").append(nodeCount);
                    sb.append("\" when it was expected to return 0");
                }
                break;
            case XPATHEQUALS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (r.contentEquals(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned \"");
                    if (r.length() < v.length()) {
                        sb.append(r);
                    } else {
                        sb.append(r.substring(0, v.length()));
                        sb.append("...");
                    }
                    sb.append("\" when \"");
                    sb.append(v);
                    sb.append("\" was expected");
                }
                break;
            case XPATHNOTEQUALS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    sb.append(" returned no match, it was expected to return a value other than \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (!r.contentEquals(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" does not return \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\", it was expected to return something other than that.");
                }
                break;
            case XPATHCONTAINS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value containing \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (r.contains(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" result contains \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" result does not contain \"");
                    sb.append(v);
                    sb.append("\"");
                }
                break;
            case XPATHNOTCONTAINS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    sb.append(" expected to return a value not containing \"");
                    sb.append(v);
                    sb.append("\" but returned no match");
                } else if (r.contains(v)) {
                    ve = new ValidationReport("Failed");
                    sb.append(" contains \"");
                    sb.append(v);
                    sb.append("\" but was expected not to");
                } else {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" does not contain \"");
                    sb.append(v);
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
        return new ValidatorOutput(r, vreport);
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
