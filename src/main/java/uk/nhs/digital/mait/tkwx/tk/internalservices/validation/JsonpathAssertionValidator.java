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

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.ConfigurationTokenSplitter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.AUTHORIZATION_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.CONTENT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.JWT_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.JWT_PAYLOAD;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import static uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor.BODY_EXTRACTOR_LABEL;

/**
 * ValidationCheck parameterised with an Jsonpath expression, a check type and for
 * check types based on equivalence, an assertion value. Comparisons are case
 * sensitive. Supported check types are:
 *
 * "jsonpathequals" Pass if Jsonpath expression result equals the assertion value
 * "jsonpathnotequals" Pass if Jsonpath expression result does not equal the assertion
 * value "jsonpathcontains" Pass if the Jsonpath expression contains the assertion
 * value "jsonpathnotcontains" Pass if the Jsonpath expression does not contain the
 * assertion value "jsonpathexists" Pass if the Jsonpath expression points to a node
 * (element, attribute or text) which exists "jsonpathnotexists" Pass if the Jsonpath
 * expression does not point to a node which exists "jsonpathmatches" Pass if the
 * Jsonpath expression matches the assertion value where the assertion is a regular
 * expression "jsonpathnotmatches" Pass if the Jsonpath expression does not match the
 * assertion value where the assertion is a regular expression "jsonpathin" Pass if
 * the Jsonpath expression result is one of the given list of assertion values
 * "jsonpathcompare" Pass if the 1st Jsonpath expression result matched the second
 * jsonpath expression result "jsonpathnotcompare" Pass if the 1st Jsonpath expression
 * result does not match the second jsonpath expression result
 *
 * @author Damian Murphy murff@warlock.org
 */
public class JsonpathAssertionValidator
        implements ValidationCheck, VariableConsumer {

    private enum JsonpathValidationType {
        JSONPATHNOTDEFINED,
        JSONPATHEQUALS,
        JSONPATHNOTEQUALS,
        JSONPATHCONTAINS,
        JSONPATHNOTCONTAINS,
        JSONPATHEXISTS,
        JSONPATHNOTEXISTS,
        JSONPATHMATCHES,
        JSONPATHNOTMATCHES,
        JSONPATHIN,
        JSONPATHCOMPARE,
        JSONPATHNOTCOMPARE
    }

    private String[] inList = null;
    private String jsonpath = null;
    private String type = null;
    private String value = null;
    private JsonpathValidationType comparisonType = JsonpathValidationType.JSONPATHNOTDEFINED;
    private String jsonSource;

    private VariableProvider vProvider = null;
    private Pattern regexPattern = null;
    private String checkExpression = null;
    private String comparisonExpression = null;
    private boolean containsVariable = false;
    private String variable = null;
    private String preVariable = null;
    private String postVariable = null;
    private String checkPart = null;
    private int attachmentNo = -1;
    private boolean existsTest = false;
    private JWTParser jwtParser = null;

    @Override
    public void writeExternalOutput(String reportDirectory) throws Exception {
    }

    /**
     * sets the required jsonpath String for the validation
     *
     * @param jsonpath
     */
    @Override
    public void setResource(String jsonpath) {
        this.jsonpath = jsonpath;
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
            throw new Exception("ITK validation of tertiary MIME part of spine message. Incorrect validation class used");
    }

    /**
     * set the type of the assertion (lower case) eg jsonpathexists (Assumes part
     * name is 10 chars + a number) for a spine message this can be a multi part
     * string of the form
     * &lt;attachment123456_jsonpathexists&gt;&lt;number&gt;_&lt;type&gt;
     *
     * @param t
     */
    @Override
    public void setType(String t) {
        type = t;
    }

    /**
     * String containing comparison value or second jsonpath against which to check
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
        if (jsonSource != null && (jsonSource.equals(JWT_HEADER) || jsonSource.equals(JWT_PAYLOAD)) && jwtParser != null) {
            return jwtParser.getSupportingData();
        }
        return null;
    }

    @Override
    public void initialise()
            throws Exception {

        jsonSource = CONTENT; // default to content
        // type may be followed by an optional xml source string
        String[] params = type.split("\\s+");
        switch (params.length) {
            case 2:
                jsonSource = params[1];
            // drop through
            case 1:
                type = params[0];
                break;
            default:
                Logger.getInstance().log(SEVERE, JsonpathAssertionValidator.class.getName(), "Invalid number of parameters in JsonpathAssertion type " + params.length);
        }

        comparisonType = JsonpathValidationType.valueOf(type.toUpperCase());

        existsTest = false;
        switch (comparisonType) {
            case JSONPATHNOTDEFINED:
                throw new Exception("Unrecognised Jsonpath check type " + type);
            case JSONPATHCOMPARE:
            case JSONPATHNOTCOMPARE:
                if (comparisonExpression != null) {
                    return;
                }
                if (value != null) {
                   comparisonExpression = value;
                }
                break;
            case JSONPATHEXISTS:
            case JSONPATHNOTEXISTS:
                existsTest = true;
        }

        containsVariable = false;
        // TODO suspect the first of these tests does not work as intended
        // $. is valid json path but a variable would never look like that
        if (jsonpath.contains("/$") || (jsonpath.startsWith("$") && ! jsonpath.startsWith("$.")) ) {
            containsVariable = true;
            preVariable = jsonpath.substring(0, jsonpath.indexOf("$"));
            postVariable = jsonpath.substring(jsonpath.indexOf("$"));
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
            case JSONPATHMATCHES:
            case JSONPATHNOTMATCHES:
                regexPattern = Pattern.compile(value);
                break;
            case JSONPATHIN:
                if (value != null) {
                    inList = (new ConfigurationTokenSplitter(value)).split();
                }
                break;
            case JSONPATHCOMPARE:
            case JSONPATHNOTCOMPARE:
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

        if (extraMessageInfo != null) {
            AbstractBodyExtractor be = null;
            for (String key : extraMessageInfo.keySet()) {
                switch (key) {
                    case BODY_EXTRACTOR_LABEL:
                        be = (AbstractBodyExtractor) extraMessageInfo.get(key);
                        break;
                    default:
                        Logger.getInstance().log(SEVERE, JsonpathAssertionValidator.class.getName(), "Unexpected extraMessageInfo key type " + key);
                }
            }

            if (be != null) {
                HttpHeaderManager headerManager = be.getRelevantHttpHeaders();
                String jwtb64 = headerManager != null ? headerManager.getHttpHeaderValue(AUTHORIZATION_HEADER) : null;
                if (jwtb64 != null) {
                    jwtParser = new JWTParser(jwtb64);
                    switch (jsonSource) {
                        case JWT_HEADER:
                            // the json to be analysed is from the JWT
                            o = jwtParser.getJsonHeader();
                            break;
                        case JWT_PAYLOAD:
                            // the json to be analysed is from the JWT
                            o = jwtParser.getJsonPayload();
                            break;
                        case CONTENT:
                            break;
                        default:
                            Logger.getInstance().log(SEVERE, JsonpathAssertionValidator.class.getName(), "Unrecognised xmlSource " + jsonSource);
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
            jsonpath = sb.toString();
        }
        StringBuilder sb = new StringBuilder("Jsonpath ");
        sb.append(Utils.htmlEncode(jsonpath));
        String r = null;
        if (!Utils.isNullOrEmpty(o)) {
            try {
                DocumentContext jsonContext = JsonPath.parse(o);
                r = jsonContext.read(jsonpath);
            } catch (PathNotFoundException ex) {
            }
        }
        String v = null;
        if (comparisonExpression != null) {
            if (!Utils.isNullOrEmpty(o)) {
                try {
                    DocumentContext jsonContext = JsonPath.parse(o);
                    v = jsonContext.read(comparisonExpression);
                } catch (PathNotFoundException ex) {
                }
            }
        }
        switch (comparisonType) {
            case JSONPATHCOMPARE:
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

            case JSONPATHNOTCOMPARE:
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

            case JSONPATHIN:
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

            case JSONPATHEXISTS:
                if (r != null ) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned ").append(r != null);
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned ").append(r != null);
                }
                break;
            case JSONPATHNOTEXISTS:
                if (r == null) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned ").append(r == null);
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned ").append(r == null);
                    sb.append("\" when it was expected to return 0");
                }
                break;
            case JSONPATHEQUALS:
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
            case JSONPATHNOTEQUALS:
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
            case JSONPATHCONTAINS:
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
            case JSONPATHNOTCONTAINS:
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
            case JSONPATHMATCHES:
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
            case JSONPATHNOTMATCHES:
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
            case JSONPATHNOTDEFINED:
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
