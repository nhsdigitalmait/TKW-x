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
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AsynchronousResponseBodyExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.util.Utils;
import org.xml.sax.InputSource;

/**
 * Implements pass/fail conditions based on executing an XPath expression in an
 * asynchronous message log file (i.e. not the log file for the request).
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class AsynchronousXPathAssertionPassFailCheck
        extends SynchronousXPathAssertionPassFailCheck {

    public AsynchronousXPathAssertionPassFailCheck() {
        responseBodyExtractor = new AsynchronousResponseBodyExtractor();
    }

    /**
     * Check based on a log file correlated with the request.
     *
     * @param s Currently executing script.
     * @param in InputStream for reading the correlated asynchronous log file
     * @param inRequest not used for synchronous passfail checks
     * @return TestResult true/false/check depending on whether the assertion is
     * passed.
     * @throws Exception
     */
    @Override
    public TestResult passed(Script s, InputStream in, InputStream inRequest)
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
