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
import static java.util.logging.Level.SEVERE;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.TextAssertionValidator.TextComparisonType.NOTDEFINED;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.AUTHORIZATION_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.CONTENT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.JWT_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.JWT_PAYLOAD;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.Encoding;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import static uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor.BODY_EXTRACTOR_LABEL;

/**
 * ValidationCheck parameterised with a check type and for check types based on
 * equivalence, an assertion value. Comparisons are case sensitive. Supported
 * check types are:
 *
 * "equals" Pass if text equals the assertion value "notequals" Pass if text
 * does not equal the assertion value "contains" Pass if text contains the
 * assertion value "notcontains" Pass if text does not contain the assertion
 * value "matches" Pass if the text matches the regaular expression
 * assertionvalue "notmatches" Pass if the text does not match the regaular
 * expression assertion value
 *
 * @author Damian Murphy murff@warlock.org
 */
public class TextAssertionValidator
        implements ValidationCheck, VariableConsumer {

    enum TextComparisonType {
        NOTDEFINED,
        EQUALS,
        NOTEQUALS,
        CONTAINS,
        NOTCONTAINS,
        MATCHES,
        NOTMATCHES
    }

    private String type = null;
    private String value = null;
    private TextComparisonType comparisonType = NOTDEFINED;

    private VariableProvider vProvider = null;
    private Pattern regexPattern = null;
    private boolean containsVariable = false;
    private String variable = null;
    private String preVariable = null;
    private String postVariable = null;
    private String checkPart = null;
    private int attachmentNo = -1;
    private String matchSource = null;
    private JWTParser jwtParser;
    private String headerName = null;
    private Encoding encoding = null;

    private static final String CONTEXT_PATH = "context_path";
    private static final String HTTP_HEADER = "http_header";

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
    }

    /**
     * sets the string to be matched against
     *
     * @param d
     */
    @Override
    public void setResource(String d) {
        value = d;
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
     * &lt;attachment123456_xpathexists_&gt;&lt;number&gt;_&lt;type&gt; can also
     * be an enhanced text assertion eg matches context_path
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
     * Not used
     *
     * @param d
     */
    @Override
    public void setData(String d) {
    }

    /**
     * @return JWT Details or null as appropriate
     */
    @Override
    public String getSupportingData() {
        if (matchSource != null && (matchSource.equals(JWT_HEADER) || matchSource.equals(JWT_PAYLOAD)) && jwtParser != null) {
            return jwtParser.getSupportingData();
        }
        return null;
    }

    @Override
    public void initialise()
            throws Exception {

        // split type and optional match source
        String[] params = type.split("\\s+");
        switch (params.length) {
            case 4: // http_header + encoding
                type = params[0];
                matchSource = params[1];
                headerName = params[3];
                encoding = Encoding.valueOf(params[2].toUpperCase());
                break;
            case 3: // http_header
                type = params[0];
                matchSource = params[1];
                headerName = params[2];
                break;
            case 2: // context_path or content or jwt_payload  or jwt_header
                type = params[0];
                matchSource = params[1];
                break;
            case 1: // default content
                type = params[0];
                matchSource = CONTENT;
                break;
            default:
                Logger.getInstance().log(SEVERE, TextAssertionValidator.class.getName(),
                        "Error splitting text assertion type unexpected number of elements " + params.length + " from " + type);
        }

        comparisonType = TextComparisonType.valueOf(type.toUpperCase());

        containsVariable = false;
        // TODO suspect the first of these tests does not work as intended
        if (value.contains("/$") || value.startsWith("$")) {
            containsVariable = true;
            preVariable = value.substring(0, value.indexOf("$"));
            postVariable = value.substring(value.indexOf("$"));
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
        }

        switch (comparisonType) {
            case MATCHES:
            case NOTMATCHES:
                regexPattern = Pattern.compile(value);
                break;
            case EQUALS:
            case NOTEQUALS:
            case CONTAINS:
            case NOTCONTAINS:
                break;
        }
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

        String contextPath = null;
        HttpHeaderManager headerManager = null;
        if (extraMessageInfo != null) {
            AbstractBodyExtractor be = null;
            for (String key : extraMessageInfo.keySet()) {
                switch (key) {
                    case BODY_EXTRACTOR_LABEL:
                        be = (AbstractBodyExtractor) extraMessageInfo.get(key);
                        break;
                    default:
                        Logger.getInstance().log(SEVERE, TextAssertionValidator.class.getName(), "Unexpected extraMessageInfo key type " + key);
                }
            }
            if (be != null) {
                contextPath = be.getHttpRequestContextPath();
                headerManager = be.getRelevantHttpHeaders();
            }
        }

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
        }
        StringBuilder sb = new StringBuilder("");
        String r = null;
        switch (matchSource) {
            case CONTEXT_PATH:
                r = contextPath;
                break;
            case CONTENT:
                r = o;
                break;
            case JWT_HEADER:
                if (headerManager != null) {
                    String jwtb64 = headerManager.getHttpHeaderValue(AUTHORIZATION_HEADER);
                    if (jwtb64 != null) {
                        jwtParser = new JWTParser(jwtb64);
                        r = jwtParser.getXmlHeader();
                    }
                }
                break;
            case JWT_PAYLOAD:
                if (headerManager != null) {
                    String jwtb64 = headerManager.getHttpHeaderValue(AUTHORIZATION_HEADER);
                    if (jwtb64 != null) {
                        jwtParser = new JWTParser(jwtb64);
                        r = jwtParser.getXmlPayload();
                    }
                }
                break;
            case HTTP_HEADER:
                if (headerManager != null) {
                    r = headerManager.getHttpHeaderValue(headerName);
                    if (encoding != null && !isNullOrEmpty(r)) {
                        r = encoding.decode(r);
                    }
                }
                break;
            default:
                Logger.getInstance().log(SEVERE, TextAssertionValidator.class.getName(), "Unrecognised match source " + matchSource);
        }
        String v = value;
        switch (comparisonType) {

            case EQUALS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (r.contentEquals(v)) {
                    ve = new ValidationReport("Pass");
                    appendSourceDetails(sb);
                    ve.setPassed();
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
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

            case NOTEQUALS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    appendSourceDetails(sb);
                    sb.append(" returned no match, it was expected to return a value other than \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (!r.contentEquals(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    appendSourceDetails(sb);
                    sb.append(" does not return \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" returned \"");
                    sb.append(v);
                    sb.append("\", it was expected to return something other than that.");
                }
                break;

            case CONTAINS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" returned no match, it was expected to return a value containing \"");
                    sb.append(v);
                    sb.append("\"");
                } else if (r.contains(v)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    appendSourceDetails(sb);
                    sb.append(" result contains \"");
                    sb.append(v);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" result does not contain \"");
                    sb.append(v);
                    sb.append("\"");
                }
                break;

            case NOTCONTAINS:
                v = CheckVariable(value);
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    appendSourceDetails(sb);
                    sb.append(" expected to return a value not containing \"");
                    sb.append(v);
                    sb.append("\" but returned no match");
                } else if (r.contains(v)) {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" contains \"");
                    sb.append(v);
                    sb.append("\" but was expected not to");
                } else {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    appendSourceDetails(sb);
                    sb.append(" does not contain \"");
                    sb.append(v);
                    sb.append("\"");
                }
                break;

            case MATCHES:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" expected to return a value matching \"");
                    sb.append(v);
                    sb.append("\" but does not exist");
                    break;
                }
                m = regexPattern.matcher(r);
                if (m.find()) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    appendSourceDetails(sb);
                    sb.append(" matches \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" does not match \"");
                    sb.append(value);
                    sb.append("\" when expected");
                }
                break;

            case NOTMATCHES:
                if (r == null) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    appendSourceDetails(sb);
                    sb.append(" expected to return a value not matching \"");
                    sb.append(v);
                    sb.append("\" and does not exist");
                    break;
                }
                m = regexPattern.matcher(r);
                if (m.find()) {
                    ve = new ValidationReport("Failed");
                    appendSourceDetails(sb);
                    sb.append(" matches \"");
                    sb.append(value);
                    sb.append("\" when not expected");
                } else {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    appendSourceDetails(sb);
                    sb.append(" does not match \"");
                    sb.append(value);
                    sb.append("\"");
                }
                break;

            case NOTDEFINED:
            default:
                throw new Exception("No or invalid comparison type defined");
        }
        ve.setTest(sb.toString());
        ValidationReport[] vreport = new ValidationReport[1];
        vreport[0] = ve;
        return new ValidatorOutput(r, vreport);
    }

    private void appendSourceDetails(StringBuilder sb) {
        sb.append(matchSource.replaceAll("_"," "));
        if (headerName != null ) {
            sb.append(" ").append(headerName);
        }
        if (encoding != null) {
            sb.append(" (after ").append(encoding).append(" decoding)");
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
