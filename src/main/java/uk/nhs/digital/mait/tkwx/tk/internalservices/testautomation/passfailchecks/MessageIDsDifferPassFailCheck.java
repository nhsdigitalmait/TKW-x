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

import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 * Pass/fail check implementation used for all responses 
 * Checks that the messageid in response is not the same as that in the request
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class MessageIDsDifferPassFailCheck
        extends AbstractRequestResponseComparatorPassFailCheck {

    public MessageIDsDifferPassFailCheck(AbstractRequestResponseComparatorPassFailCheck invoker)
            throws Exception {
        this.invoker = invoker;
        try {
            givenXpath = "/soap:Envelope/soap:Header/wsa:MessageID/text()";
            expression = getXpathExtractor(givenXpath);
        } catch (Exception e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }
    }

    /**
     * The request and response payloads have already been extracted from the logs.
     * @param s Script object
     * @param request InputSource Payload
     * @param response InputSource Payload
     * @return boolean result of test
     * @throws Exception
     */
    @Override
    protected boolean doChecks(Script s, InputSource request, InputSource response)
            throws Exception {
        boolean result = true;
        String requestMessageID = expression.evaluate(request);
        String responseMessageID = expression.evaluate(response);
        StringBuilder sb = new StringBuilder();
        if (!Utils.isNullOrEmpty(requestMessageID) && !Utils.isNullOrEmpty(responseMessageID)) {

            if (!(requestMessageID.compareToIgnoreCase(responseMessageID)==0)) {
                result = true;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Message IDs differ as expected.", GREEN));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(requestMessageID, GREEN));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseMessageID, GREEN));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, GREEN));
            } else {
                result = false;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Message IDs are unexpectedly identical.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(requestMessageID, RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseMessageID, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
        } else {
            result = false;
            if (Utils.isNullOrEmpty(requestMessageID)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request message ID is malformed or not present.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(requestMessageID == null ? "Null" : requestMessageID, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
            if (Utils.isNullOrEmpty(responseMessageID)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Response message ID is malformed or not present.", RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseMessageID == null ? "Null" : responseMessageID, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
        }
        invoker.setDescription(sb.toString());

        return result;
    }
    private AbstractRequestResponseComparatorPassFailCheck invoker;
}
