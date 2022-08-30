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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.HttpStatusCheckContext;

/**
 * Http Status code pass/fail check Only calls an extractor on status 200
 *
 * @author Simon Farrow simon.farrow1@nhs.net
 */
public class HttpStatusCheck
        extends AbstractPassFailCheck {

    private int assertionValue = -1;

    /**
     *
     * @param passfailCtx
     * @throws java.lang.Exception
     */
    @Override
    public void init(AutotestParser.PassfailContext passfailCtx)
            throws Exception {
        super.init(passfailCtx);
        HttpStatusCheckContext httpStatusCheckContext = passfailCtx.passFailCheck().httpStatusCheck();

        // get the parameters here the grammar insists that this is a pareable integer
        assertionValue = Integer.parseInt(httpStatusCheckContext.INTEGER().getText());
    }

    @Override
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(inResponse));
        String httpResponse = null;
        String line = null;
        boolean rq = false;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(END_REQUEST_MARKER)) {
                rq = true;
                continue;
            }
            if (rq) {
                if (line.startsWith("HTTP/1.1 ")) {
                    httpResponse = line;
                    break;
                }
            }
        }

        setDescription(colourString("Expected: ", BLACK));
        if (httpResponse == null) {
            appendDescription(colourString("HTTP " + assertionValue + ".", RED));
            appendDescription(colourString(" Actual: ", BLACK));
            appendDescription(colourString("Null HTTP response", RED));
            return TestResult.FAIL;
        }

        if (assertionValue == 200 && extractor != null) {
            extract(inResponse);
        }

        if (httpResponse.contains("" + assertionValue)) {
            appendDescription(colourString("HTTP " + assertionValue + ".", GREEN));
            appendDescription(colourString(" Actual: ", BLACK));
            appendDescription(colourString("HTTP " + assertionValue + ".", GREEN));
            return TestResult.PASS;
        }
        appendDescription(colourString("HTTP " + assertionValue + ".", RED));
        appendDescription(colourString(" Actual: ", BLACK));
        appendDescription(colourString(httpResponse, RED));
        return TestResult.FAIL;
    }
}
