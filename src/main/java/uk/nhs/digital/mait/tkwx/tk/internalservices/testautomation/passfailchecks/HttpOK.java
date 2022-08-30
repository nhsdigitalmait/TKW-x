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

/**
 * Synchronous pass/fail check that considers finding an HTTP 200 response, to
 * be a "pass". Calls any extractor.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpOK
        extends AbstractPassFailCheck {

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
        if (httpResponse == null) {
            setDescription(colourString("Expected: ", BLACK));
            appendDescription(colourString("HTTP 200.", RED));
            appendDescription(colourString(" Actual: ", BLACK));
            appendDescription(colourString("Null HTTP response", RED));
            return TestResult.FAIL;
        }
        if (extractor != null) {
            extract(inResponse);
        }

        setDescription(colourString("Expected: ", BLACK));

        if (httpResponse.contains("200")) {
            appendDescription(colourString("HTTP 200.", GREEN));
            appendDescription(colourString(" Actual: ", BLACK));
            appendDescription(colourString("HTTP 200.", GREEN));
            return TestResult.PASS;
        }
        appendDescription(colourString("HTTP 200.", RED));
        appendDescription(colourString(" Actual: ", BLACK));
        appendDescription(colourString(httpResponse, RED));
        return TestResult.FAIL;
    }
}
