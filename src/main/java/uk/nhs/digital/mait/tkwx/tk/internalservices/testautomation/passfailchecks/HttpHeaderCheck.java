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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_REQUEST_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractPassFailCheck;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.HttpHeaderCheckContext;
import uk.nhs.digital.mait.commonutils.util.ConfigurationTokenSplitter;

/**
 * Http Header pass/fail check
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpHeaderCheck
        extends AbstractPassFailCheck {

    private String headerName = null;
    private String givenType = null ;
    private String assertionValue = null;
    private String[] inList = null;

    /**
     *
     * @param passfailCtx
     * @throws java.lang.Exception
     */
    @Override
    public void init(AutotestParser.PassfailContext passfailCtx)
            throws Exception {
        super.init(passfailCtx);
        HttpHeaderCheckContext httpHeaderCheckContext = passfailCtx.passFailCheck().httpHeaderCheck();

        // get the parameters here
        headerName = httpHeaderCheckContext.httpHeaderName().getText();
        if (httpHeaderCheckContext.xpathArg() != null) {
            if (httpHeaderCheckContext.xpathArg().xpathTypeArg() != null) {
                givenType = httpHeaderCheckContext.xpathArg().xpathTypeArg().getChild(0).getText();
                assertionValue = httpHeaderCheckContext.xpathArg().matchString().getText().replaceFirst("^\"(.*)\"$","$1");
            } else {
                givenType = httpHeaderCheckContext.xpathArg().xpathTypeNoArg().getChild(0).getText();
            }
        }
        
        if (givenType.equals("in") ) {
            assertionValue = assertionValue.replaceFirst("^\"(.*)\"$","$1");
            inList = (new ConfigurationTokenSplitter(assertionValue)).split();
        }
    }

    @Override
    public TestResult passed(Script s, InputStream in, InputStream inRequest)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String headerLine = null;
        String line = null;
        boolean rq = false;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(END_REQUEST_MARKER)) {
                rq = true;
                continue;
            }
            if (rq) {
                // match ignores case of http header name
                if (line.matches("(?i)^"+headerName+".*$")) {
                    headerLine = line;
                    break;
                }
            }
        }

        String headerValue = null;
        if (headerLine != null) {
            headerValue = headerLine.replaceFirst("(?i)"+headerName + ":\\s+(.*)$", "$1");
        }

        switch (givenType) {
            case "exists":
                setDescription(colourString("Result: ", BLACK));
                if (headerLine != null && headerLine.length() > 0) {
                    appendDescription(colourString("Http header exists as expected", GREEN));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, GREEN));
                    return TestResult.PASS;
                } else {
                    setDescription(colourString("Http header unexpectedly does not exist", RED));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, RED));
                    return TestResult.FAIL;
                }

            case "doesnotexist":
                setDescription(colourString("Result: ", BLACK));
                if (headerLine != null && headerLine.length() > 0) {
                    setDescription(colourString("Http header unexpectedly exists when it is expected to be absent", RED));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, RED));
                    return TestResult.FAIL;
                } else {
                    setDescription(colourString(headerName, BLACK));
                    appendDescription(colourString("Http header does not exist as expected", GREEN));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, GREEN));
                    return TestResult.PASS;
                }

            case "matches":
                setDescription(colourString("Expected: ", BLACK));
                if (headerValue != null && headerValue.matches(assertionValue)) {
                    appendDescription(colourString(assertionValue, GREEN));
                    appendDescription(colourString("<BR/>Actual: ", BLACK));
                    appendDescription(colourString(headerValue, GREEN));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, GREEN));
                    return TestResult.PASS;
                } else {
                    appendDescription(colourString(assertionValue, RED));
                    appendDescription(colourString("<BR/>Actual: ", BLACK));
                    appendDescription(colourString(headerValue, RED));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, RED));
                    return TestResult.FAIL;
                }

            case "doesnotmatch":
                setDescription(colourString("Not Expected: ", BLACK));
                if (headerValue != null && headerValue.matches(assertionValue)) {
                    appendDescription(colourString(assertionValue, RED));
                    appendDescription(colourString("<BR/>Actual: ", BLACK));
                    appendDescription(colourString(headerValue, RED));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, RED));
                    return TestResult.FAIL;
                } else {
                    appendDescription(colourString(assertionValue, GREEN));
                    appendDescription(colourString("<BR/>Actual: ", BLACK));
                    appendDescription(colourString(headerValue, GREEN));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, GREEN));
                    return TestResult.PASS;
                }

            case "in":
                setDescription(colourString("Expected: ", BLACK));
                if (headerValue == null || headerValue.length() == 0) {
                    appendDescription(colourString(assertionValue, RED));
                    appendDescription(colourString("<BR/>Actual: ", BLACK));
                    appendDescription(colourString("returned no data", RED));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, RED));
                    return TestResult.FAIL;
                } else {
                    for (String l : inList) {
                        if (headerValue.equals(l)) {
                            appendDescription(colourString(assertionValue, GREEN));
                            appendDescription(colourString("<BR/>Actual: ", BLACK));
                            appendDescription(colourString(headerValue + " found as expected", GREEN));
                            appendDescription(colourString("<BR/>Http header name: ", BLACK));
                            appendDescription(colourString(headerName, GREEN));
                            return TestResult.PASS;
                        }
                    }
                    appendDescription(colourString(assertionValue, RED));
                    appendDescription(colourString("<BR/>Actual: ", BLACK));
                    appendDescription(colourString(headerValue +" not allowed in set", RED));
                    appendDescription(colourString("<BR/>Http header name: ", BLACK));
                    appendDescription(colourString(headerName, RED));
                    return TestResult.FAIL;
                }

            case "check":
                setDescription(colourString("Check value is: " + headerValue, BLUE));
                appendDescription(colourString("<BR/>Http header name: ", BLACK));
                appendDescription(colourString(headerName, BLUE));
                return TestResult.CHECK;

            default:
                throw new Exception("Unrecognised assertion type: " + givenType);
        }
    }
}
