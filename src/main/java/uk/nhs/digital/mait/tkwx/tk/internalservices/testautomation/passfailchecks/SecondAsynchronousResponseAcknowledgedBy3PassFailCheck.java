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
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Pass/fail check implementation that works on second asynchronous responses. 
 * Checks the request tracking id matches the response ack by 3 id
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class SecondAsynchronousResponseAcknowledgedBy3PassFailCheck
        extends AbstractSecondAsynchronousRequestResponseComparatorPassFailCheck {

    protected String acknowledgedBy3Xpath = null;
    protected XPathExpression acknowledgedBy3Expression = null;

    @Override
    public void init(PassFailCheckContext passfailCheckCtx)
            throws Exception {

        try {
            givenXpath = "//itk:DistributionEnvelope/itk:header/@trackingid";
            expression = getXpathExtractor(givenXpath);
            acknowledgedBy3Xpath = "//itk:DistributionEnvelope/itk:payloads/itk:payload[1]/hl7:BusinessResponseMessage/hl7:acknowledgedBy3/hl7:conveyingTransmission/hl7:id/@root";
            acknowledgedBy3Expression = getXpathExtractor(acknowledgedBy3Xpath);
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }
    }

    @Override
    protected boolean doChecks(Script s, InputSource request, InputSource response)
            throws Exception {
        boolean result = true;
        String trackingID = expression.evaluate(request);
        String ackBy3Id = acknowledgedBy3Expression.evaluate(response);
        StringBuilder sb = new StringBuilder();
        if (!Utils.isNullOrEmpty(trackingID) && !Utils.isNullOrEmpty(ackBy3Id)) {
            if (trackingID.compareToIgnoreCase(ackBy3Id)==0) {
                result = true;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Tracking ID and Response Acknowledged By 3 id match as expected.", GREEN));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(trackingID, GREEN));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(ackBy3Id, GREEN));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy3Xpath, GREEN));
            } else {
                result = false;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Tracking ID and Response Acknowledged By 3 id unexpectedly do not match.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(trackingID, RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(ackBy3Id, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy3Xpath, RED));
            }
        } else {
            result = false;
            if (Utils.isNullOrEmpty(trackingID)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Tracking ID is malformed or not present.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(trackingID == null ? "Null" : trackingID, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy3Xpath, RED));
            }
            if (Utils.isNullOrEmpty(ackBy3Id)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Response Acknowledged By 3 id is malformed or not present.", RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(ackBy3Id == null ? "Null" : ackBy3Id, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(acknowledgedBy3Xpath, RED));
            }
        }
        setDescription(sb.toString());

        return result;
    }
}
