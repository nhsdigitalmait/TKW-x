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
 *
 * @author SIFA2
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.streamToByteArray;
import org.xml.sax.InputSource;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.isJsonFhir;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_TYPE_HEADER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;

/**
 * abstract base class for synchronous pass fail checks looking across the
 * request and response messages For synchronous interactions these are
 * contained in the same log file, typically transmitter_sent_messages.
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public abstract class AbstractSynchronousRequestResponseComparatorPassFailCheck 
        extends AbstractRequestResponseComparatorPassFailCheck {

    /**
     *
     * @param s Script object
     * @param inResponse response log input stream
     * @param inRequest request log input stream
     * @return TestResult
     * @throws Exception
     */
    @Override
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest)
            throws Exception {
        TestResult p = TestResult.FAIL;
        String log = inputStream2String(inResponse);

        // submit with effectively cloned input streams
        String requestBody = getRequestBody(new ByteArrayInputStream(log.getBytes()));
        String rqContentType = getRequestHeaders().getHttpHeaderValue(CONTENT_TYPE_HEADER);
        if ( requestBody != null && requestBody.trim().startsWith("{") && isJsonFhir(rqContentType)) {
                requestBody = FHIRJsonXmlAdapter.fhirConvertJson2Xml(requestBody);
        }
        if (!Utils.isNullOrEmpty(requestBody)) {
            String responseBody = getResponseBody(new ByteArrayInputStream(log.getBytes()));
            // conversion to xml has already happened automatically for responses
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
     * @param request InputSource Payload
     * @param response InputSource Payload
     * @return result of checks across the request and response messages
     * @throws Exception
     */
    @Override
    protected abstract boolean doChecks(Script s, InputSource request, InputSource response) throws Exception;

    /**
     * get a string containing the contents of the input stream This is a util
     * function really
     *
     * @param in the input stream
     * @return the contents of the stream
     * @throws Exception
     */
    private String inputStream2String(InputStream in) throws Exception {
        return new String(streamToByteArray(in));    
    }

}
