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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.InputStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.commonutils.util.ConfigurationTokenSplitter;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 * Pass/fail check implementation that works on synchronous responses - i.e.
 * those found in the same log file as the request.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SynchronousXPathAssertionPassFailCheck
        extends AbstractSynchronousPassFailCheck {

    protected String givenXpath = null;
    protected String givenType = null;

    protected String assertionValue = null;
    protected XPathExpression expression = null;
    protected int assertionType = UNDEFINED;
    protected Pattern assertionPattern = null;
    protected String[] inList = null;
    private String originalXpath;

    protected static final String[] TYPES = {"exists", "doesnotexist", "matches", "doesnotmatch", "in", "check"};
    protected static final int UNDEFINED = -1;
    protected static final int EXISTS = 0;
    protected static final int NOTEXISTS = 1;
    protected static final int MATCHES = 2;
    protected static final int NOTMATCHES = 3;
    protected static final int ISIN = 4;
    protected static final int CHECK = 5;
    private String tagName;

    @Override
    public void init(PassFailCheckContext passfailCheckCtx)
            throws Exception {

        // line elements 0 and 1 have already been processed by super.init(), as
        // has any extractor, the rest of the elements (if present) ae processed
        // here.
        //
        // Parameters are:
        // 2 XPath expression
        // 3 type (equals, not equals etc)
        // 4 (optional assertion value, if not "EXTRACTOR")
        AutotestParser.XPathCheckContext xCheckCtx = passfailCheckCtx.xPathCheck();
        try {
            givenType = xCheckCtx.xpathArg().getChild(0).getText();
            for (int i = 0; i < TYPES.length; i++) {
                if (givenType.contentEquals(TYPES[i])) {
                    assertionType = i;
                    break;
                }
            }
            if (assertionType == UNDEFINED) {
                throw new Exception("Unrecognised assertion type: " + givenType);
            }
        } catch (Exception e) {
            throw new Exception("Xpath pass/fail check syntax error, type not found: " + e.toString());
        }

        try {
            givenXpath = xCheckCtx.xpathExpression().getText();
            if (assertionType == EXISTS || assertionType == NOTEXISTS) {
                originalXpath = givenXpath;
                givenXpath = "count(" + givenXpath + ")";
            }
            expression = getXpathExtractor(givenXpath);

            if (xCheckCtx.usingExtractor() != null) {
                responseExtractorName = xCheckCtx.usingExtractor().extractorName().getText();
            }

        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }

        switch (assertionType) {
            // Syntactic checks that we have everything, and make
            // the Pattern if we're doing a regex. Also make the "in"
            // array where necessary.
            case EXISTS:
            case CHECK:
            case NOTEXISTS:
                if (assertionValue != null) {
                    throw new Exception("Syntax error: exists/notexists check does not assert a value");
                }
                break;
            case MATCHES:
            case NOTMATCHES:
                if (xCheckCtx.xpathArg().matchString().QUOTED_STRING() != null) {
                    assertionValue = xCheckCtx.xpathArg().matchString().getText();
                    assertionPattern = Pattern.compile(assertionValue);
                    if (assertionValue == null) {
                        throw new Exception("Syntax error: matches/notmatches check must assert a value");
                    }
                } else if (xCheckCtx.xpathArg().matchString().SUBSTITUTION_TAG() != null) {
                    tagName = xCheckCtx.xpathArg().matchString().getText();
                }
                break;

            case ISIN:
                assertionValue = xCheckCtx.xpathArg().matchString().getText().replaceFirst("^\"(.*)\"$", "$1");
                if (assertionValue == null) {
                    throw new Exception("Syntax error: in check must assert a value");
                }
                // All values in one set of quotes?
                // Need to hand off to CST parser...
                inList = (new ConfigurationTokenSplitter(assertionValue)).split();
                break;
        }
    }

    protected TestResult doChecks(Script s, InputSource is)
            throws Exception {

        String r = null;
        try {
            r = expression.evaluate(is);
        } catch (XPathExpressionException ex) {
            // input does not parse return an empty string
            r = "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher m = null;
        TestResult t = null;
        int nodeCount = 0;
        if ((assertionType == EXISTS || assertionType == NOTEXISTS) && r != null) {
            nodeCount = Integer.parseInt(r);
        }

        // we require delayed pattern compilation when the match string is a substitution tag
        // since the datasource and recordid are only available at "runtime"
        if (tagName != null) {
            handleTdvTag();
        }

        switch (assertionType) {
            case EXISTS:
                if (nodeCount > 0) {
                    t = TestResult.PASS;
                    sb.append(colourString("Result: ", BLACK));
                    sb.append(colourString("Xpath location exists as expected.", GREEN));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(originalXpath, GREEN));
                } else {
                    t = TestResult.FAIL;
                    sb.append(colourString("Result: ", BLACK));
                    sb.append(colourString("Xpath location unexpectedly does not exist", RED));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(originalXpath, RED));
                }
                break;

            case NOTEXISTS:
                if ((nodeCount > 0)) {
                    t = TestResult.FAIL;
                    sb.append(colourString("Result: ", BLACK));
                    sb.append(colourString("Xpath location unexpectedly exists when it is expected to be absent", RED));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(originalXpath, RED));
                } else {
                    t = TestResult.PASS;
                    sb.append(colourString("Result: ", BLACK));
                    sb.append(colourString("Xpath location does not exist as expected.", GREEN));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(originalXpath, GREEN));
                }
                break;

            case MATCHES:
                m = assertionPattern.matcher(r);
                if (m.find()) {
                    t = TestResult.PASS;
                    sb.append(colourString("Expected: ", BLACK));
                    sb.append(colourString(assertionValue, GREEN));
                    sb.append(colourString("<BR/>Actual: ", BLACK));
                    sb.append(colourString(m.group(), GREEN));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(givenXpath, GREEN));
                } else {
                    t = TestResult.FAIL;
                    sb.append(colourString("Expected: ", BLACK));
                    sb.append(colourString(assertionValue, RED));
                    sb.append(colourString("<BR/>Actual: ", BLACK));
                    sb.append(colourString(r, RED));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(givenXpath, RED));
                }
                break;

            case NOTMATCHES:
                m = assertionPattern.matcher(r);
                if (m.find()) {
                    t = TestResult.FAIL;
                    sb.append(colourString("Not Expected: ", BLACK));
                    sb.append(colourString(assertionValue, RED));
                    sb.append(colourString("<BR/>Actual: ", BLACK));
                    sb.append(colourString(m.group(), RED));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(givenXpath, RED));
                } else {
                    t = TestResult.PASS;
                    sb.append(colourString("Not Expected: ", BLACK));
                    sb.append(colourString(assertionValue, GREEN));
                    sb.append(colourString("<BR/>Actual: ", BLACK));
                    sb.append(colourString(r, GREEN));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(givenXpath, GREEN));
                }
                break;

            case ISIN:
                if ((r == null) || (r.length() == 0)) {
                    t = TestResult.FAIL;
                    sb.append(colourString("Expected: ", BLACK));
                    sb.append(colourString(assertionValue, RED));
                    sb.append(colourString("<BR/>Actual: ", BLACK));
                    sb.append(colourString("returned no data.", RED));
                    sb.append(colourString("<BR/>XPATH: ", BLACK));
                    sb.append(colourString(givenXpath, RED));

                } else {
                    boolean foundIn = false;
                    for (String l : inList) {
                        if (r.contentEquals(l)) {
                            t = TestResult.PASS;
                            foundIn = true;
                            sb.append(colourString("Expected: ", BLACK));
                            sb.append(colourString(assertionValue, GREEN));
                            sb.append(colourString("<BR/>Actual: ", BLACK));
                            sb.append(colourString(r + " found as expected", GREEN));
                            sb.append(colourString("<BR/>XPATH: ", BLACK));
                            sb.append(colourString(givenXpath, GREEN));
                            break;
                        }
                    }
                    if (!foundIn) {
                        t = TestResult.FAIL;
                        sb.append(colourString("Expected: ", BLACK));
                        sb.append(colourString(assertionValue, RED));
                        sb.append(colourString("<BR/>Actual: ", BLACK));
                        sb.append(colourString(r + " not in allowed set", RED));
                        sb.append(colourString("<BR/>XPATH: ", BLACK));
                        sb.append(colourString(givenXpath, RED));
                    }
                }
                break;
            case CHECK:
                t = TestResult.CHECK;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Check value is: " + r, BLUE));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, BLUE));
                break;

        }
        setDescription(sb.toString());
        return t;
    }

    /**
     * extract the value from the associated tdv using the given tag name. Sets
     * assertionPattern and assertionValue. Expects that the string may be
     * quoted This is currently only implemented for xpath matches/not matches
     * so an empty string is not a useful regexp
     *
     * @throws Exception
     */
    private void handleTdvTag() throws Exception {
        if (datasource == null) {
            throw new Exception("Runtime error: matches/notmatches check datasource is null");
        }
        if (recordid == null) {
            throw new Exception("Runtime error: matches/notmatches check recordid is null for datasource " + datasource.getName());
        }
        if (!datasource.hasId(recordid)) {
            throw new Exception("Runtime error: matches/notmatches check recordid " + recordid + " does not exist for datasource " + datasource.getName());
        }
        String tagValue = datasource.getValue(recordid, tagName);
        if (tagValue == null) {
            throw new Exception(
                    "Runtime error: matches/notmatches check tag " + tagName + " has null value for datasource " + datasource.getName() + " at recordid " + recordid);
        }

        tagValue = tagValue.replaceFirst("^\"(.*)\"$", "$1");
        if (tagValue.isEmpty()) {
            throw new Exception(
                    "Runtime error: matches/notmatches check tag " + tagName + " is empty for datasource " + datasource.getName() + " at recordid " + recordid);
        }

        assertionPattern = Pattern.compile(tagValue);
        assertionValue = tagValue;
    }

    @Override
    public TestResult passed(Script s, InputStream in, InputStream inSync)
            throws Exception {
        TestResult p = TestResult.FAIL;
        String responseBody = getResponseBody(in);
        if (!Utils.isNullOrEmpty(responseBody)) {
            InputSource is = new InputSource(new StringReader(responseBody));
            p = doChecks(s, is);
            doExtract(responseBody);
        } else {
            setDescription(colourString("Zero Length Content", RED));
        }
        return p;
    }

}
