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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.HttpHeaderCorrelationCheckContext;

/**
 * Http Header pass/fail check
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpHeaderCorrelationCheck
        extends AbstractPassFailCheck {

    private String requestHeaderName = null;
    private String responseHeaderName = null;

    /**
     *
     * @param passfailCtx
     * @throws java.lang.Exception
     */
    @Override
    public void init(AutotestParser.PassfailContext passfailCtx)
            throws Exception {
        super.init(passfailCtx);
        HttpHeaderCorrelationCheckContext httpHeaderCorrelationCheckContext = passfailCtx.passFailCheck().httpHeaderCorrelationCheck();

        // get the parameters here
        requestHeaderName = httpHeaderCorrelationCheckContext.httpHeaderName().get(0).getText();
        responseHeaderName = httpHeaderCorrelationCheckContext.httpHeaderName().get(1).getText();
    }

    @Override
    public TestResult passed(Script s, InputStream inSync, InputStream inAsync)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(inSync));
        String headerLine = null;
        String line = null;
        while ((line = br.readLine()) != null) {
            // match ignores case of http header name
            if (headerLine == null && line.matches("(?i)^" + requestHeaderName + ".*$")) {
                headerLine = line;
            }
            if (line.startsWith(END_REQUEST_MARKER)) {
                break;
            }
        }

        String requestHeaderValue = null;
        if (headerLine != null) {
            requestHeaderValue = headerLine.replaceFirst("(?i)" + requestHeaderName + ":\\s+(.*)$", "$1");
        }

        headerLine = null;
        line = null;
        while ((line = br.readLine()) != null) {
            // match ignores case of http header name
            if (line.matches("(?i)^" + responseHeaderName + ".*$")) {
                headerLine = line;
                break;
            }
        }
        br.close();
        
        String responseHeaderValue = null;
        if (headerLine != null) {
            responseHeaderValue = headerLine.replaceFirst("(?i)" + responseHeaderName + ":\\s+(.*)$", "$1");
        }

        if (requestHeaderValue != null && requestHeaderValue.equals(responseHeaderValue)) {
            setDescription(colourString("Result: ", BLACK));
            appendDescription(colourString("HttpHeader values match as expected.", GREEN));
            appendDescription(colourString("<BR/>Request Http header name: ", BLACK));
            appendDescription(colourString(requestHeaderName, BLACK));
            appendDescription(colourString("<BR/>Header value: ", BLACK));
            appendDescription(colourString(requestHeaderValue, GREEN));
            appendDescription(colourString("<BR/>Response Http header name: ", BLACK));
            appendDescription(colourString(responseHeaderName, BLACK));
            appendDescription(colourString("<BR/>Header value: ", BLACK));
            appendDescription(colourString(responseHeaderValue, GREEN));
            return TestResult.PASS;
        } else {
            setDescription(colourString("Result: ", BLACK));
            appendDescription(colourString("HttpHeader values unexpectedly do not match.", RED));
            appendDescription(colourString("<BR/>Request Http header name: ", BLACK));
            appendDescription(colourString(requestHeaderName, BLACK));
            appendDescription(colourString("<BR/>Header value: ", BLACK));
            appendDescription(colourString(requestHeaderValue, RED));
            appendDescription(colourString("<BR/>Response Http header name: ", BLACK));
            appendDescription(colourString(responseHeaderName, BLACK));
            appendDescription(colourString("<BR/>Header value: ", BLACK));
            appendDescription(colourString(responseHeaderValue, RED));
            return TestResult.FAIL;
        }
    }
}
