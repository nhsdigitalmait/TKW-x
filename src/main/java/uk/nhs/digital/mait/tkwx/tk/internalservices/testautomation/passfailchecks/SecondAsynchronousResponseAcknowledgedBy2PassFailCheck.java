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
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 * Pass/fail check implementation that works on second asynchronous responses. 
 * Checks the request payload id matches the response ack by 2 id
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class SecondAsynchronousResponseAcknowledgedBy2PassFailCheck
        extends AbstractSecondAsynchronousRequestResponseComparatorPassFailCheck {

    protected String acknowledgedBy2Xpath = null;
    protected XPathExpression acknowledgedBy2Expression = null;

    @Override
    public void init(PassFailCheckContext passfailCheckCtx)
            throws Exception {

        try {
            givenXpath = "//itk:DistributionEnvelope/itk:payloads/itk:payload[1]/@id";
            expression = getXpathExtractor(givenXpath);
            acknowledgedBy2Xpath = "//itk:DistributionEnvelope/itk:payloads/itk:payload[1]/hl7:BusinessResponseMessage/hl7:acknowledgedBy2/hl7:conveyingTransmission/hl7:id/@root";
            acknowledgedBy2Expression = getXpathExtractor(acknowledgedBy2Xpath);
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }
    }

    @Override
    protected boolean doChecks(Script s, InputSource request, InputSource response)
            throws Exception {
        boolean result = true;
        String payloadID = expression.evaluate(request);
        String ackBy2Id = acknowledgedBy2Expression.evaluate(response);
        StringBuilder sb = new StringBuilder();
        if (!Utils.isNullOrEmpty(payloadID) && !Utils.isNullOrEmpty(ackBy2Id)) {

            if (payloadID.compareToIgnoreCase(ackBy2Id)==0) {
                result = true;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Payload ID and Response Acknowledged By 2 id match as expected.", GREEN));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(payloadID, GREEN));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(ackBy2Id, GREEN));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy2Xpath, GREEN));
            } else {
                result = false;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Payload ID and Response Acknowledged By 2 id unexpectedly do not match.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(payloadID, RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(ackBy2Id, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy2Xpath, RED));
            }
        } else {
            result = false;
            if (Utils.isNullOrEmpty(payloadID)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Payload ID is malformed or not present.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(payloadID == null ? "Null" : payloadID, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy2Xpath, RED));
            }
            if (Utils.isNullOrEmpty(ackBy2Id)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Response Acknowledged By 2 id is malformed or not present.", RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(ackBy2Id == null ? "Null" : ackBy2Id, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy2Xpath, RED));
            }
        }
        setDescription(sb.toString());

        return result;
    }
}
