/**
 * Copyright 2013 Simon Farrow <simon.farrow1@nhs.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.InputStream;
import java.io.StringReader;
import javax.xml.xpath.XPathExpression;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.RequestBodyExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.util.Utils;
import org.xml.sax.InputSource;

/**
 * Abstract base class for asynchronous pass fail checks looking across the
 * request and response messages. For asynchronous interactions these are
 * contained in different log files, typically in transmitter_sent_messages and
 * simulator_saved_messages.
 *
 * @author SIFA2
 */
public abstract class AbstractRequestResponseComparatorPassFailCheck extends AbstractSynchronousPassFailCheck {

    protected String givenXpath = null;
    protected String assertionValue = null;
    protected XPathExpression expression = null;

    /**
     *
     * @param s script
     * @param inResponse response input steam
     * @param inRequest request input stream
     * @return TestResult
     * @throws Exception
     */
    @Override
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest)
            throws Exception {
        TestResult p = TestResult.FAIL;
        String requestBody = getRequestBody(inRequest);
        if (!Utils.isNullOrEmpty(requestBody)) {
            String responseBody = getResponseBody(inResponse);
            if (!Utils.isNullOrEmpty(responseBody)) {
                p = TestResult.valueOf(doChecks(s, new InputSource(new StringReader(requestBody)), new InputSource(new StringReader(responseBody))));
            } else {
                setDescription(colourString("Zero Length Response Content", RED));
            }
        } else {
            setDescription(colourString("Zero Length Request Content", RED));
        }
        return p;
    }

    /**
     * overridden by subclasses to perform the specific checks
     *
     * @param s script
     * @param request InputSource
     * @param response InputSource
     * @return result of checks across the request and response messages
     * @throws Exception
     */
    protected abstract boolean doChecks(Script s, InputSource request, InputSource response) throws Exception;

    /**
     * extracts a string containing the xml request message
     *
     * @param in request input stream
     * @return request body string
     * @throws java.lang.Exception
     */
    protected String getRequestBody(InputStream in) throws Exception {
        return requestbe.getBody(in);
    }

    private final AbstractBodyExtractor requestbe = new RequestBodyExtractor();
}
