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
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractPassFailCheck;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Synchronous pass/fail check that considers finding a request starting with
 * a reg exp to be a "pass". Required for testing null requests for tlsma testing
 * Does not call any extractor.
 * @author Damian Murphy murff@warlock.org
 */
public class NullRequest
        extends AbstractPassFailCheck {

    private String regExp;

    @Override
    public void init(AutotestParser.PassFailCheckContext passfailCheckCtx)
            throws Exception {
            regExp = passfailCheckCtx.nullCheck().matchString().getText();
            regExp = regExp.replaceFirst("^\"(.*)\"$", "$1");
    }

    @Override
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(inResponse));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(END_INBOUND_MARKER)) {
                break;
            }
            sb.append(line);
        }

        String request = sb.toString();
        if (!request.matches(regExp)) {
            setDescription(colourString("Expected: ", BLACK));
            appendDescription(colourString(regExp, RED));
            appendDescription(colourString(" Actual: ", BLACK));
            // we are probably getting an unexpected valid message which contains xml so do some escaping
            appendDescription(colourString(Utils.htmlEncode(request), RED));
            return TestResult.FAIL;
        }

        setDescription(colourString("Expected: ", BLACK));
        appendDescription(colourString(regExp, GREEN));
        appendDescription(colourString(" Actual: ", BLACK));
        appendDescription(colourString(request, GREEN));
        return TestResult.PASS;
    }
}
