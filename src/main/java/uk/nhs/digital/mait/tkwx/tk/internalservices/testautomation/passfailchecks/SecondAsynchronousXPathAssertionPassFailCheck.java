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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SecondAsynchronousResponseBodyExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.util.Utils;
import org.xml.sax.InputSource;

/**
 * In some tests, more than one asynchronous response will be received. The
 * basic asynchronous XPath pass fail check will extract the first of these, and
 * apply its assertion to it. This class extracts the second response. It is
 * used for example, to check "Business Acks" returned from ITK Correspondence
 * systems, where the first asynchronous response is an Infrastructure Ack.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SecondAsynchronousXPathAssertionPassFailCheck
        extends SynchronousXPathAssertionPassFailCheck {

    public SecondAsynchronousXPathAssertionPassFailCheck() {
        responseBodyExtractor = new SecondAsynchronousResponseBodyExtractor();
    }

    @Override
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest)
            throws Exception {
        TestResult p = TestResult.FAIL;
        String responseBody = getResponseBody(inResponse);
        if (!Utils.isNullOrEmpty(responseBody)) {
            InputSource is = new InputSource(new StringReader(responseBody));
            p = doChecks(s, is);
            doExtract(responseBody, getResponseHeaders());
        } else {
            setDescription(colourString("Zero Length Content", RED));
        }
        return p;
    }
}
