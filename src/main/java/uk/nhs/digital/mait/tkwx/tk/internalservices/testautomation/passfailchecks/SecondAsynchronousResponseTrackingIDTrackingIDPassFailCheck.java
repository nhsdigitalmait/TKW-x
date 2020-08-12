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

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 * Pass/fail check implementation that works on second asynchronous responses. 
 * Checks the request tracking id does not match the second response trackingid 
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class SecondAsynchronousResponseTrackingIDTrackingIDPassFailCheck
        extends AbstractSecondAsynchronousRequestResponseComparatorPassFailCheck {


    @Override
    public void init(PassFailCheckContext passfailCheckCtx)
            throws Exception {
        try {
            givenXpath = "//itk:DistributionEnvelope/itk:header/@trackingid";
            expression = getXpathExtractor(givenXpath);
        } catch (XPathFactoryConfigurationException | XPathExpressionException e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }
    }

    @Override
    protected boolean doChecks(Script s, InputSource request, InputSource response)
            throws Exception {
        boolean result = true;
        String trackingID = expression.evaluate(request);
        String responseTrackingId = expression.evaluate(response);
        StringBuilder sb = new StringBuilder();
        if (!Utils.isNullOrEmpty(trackingID) && !Utils.isNullOrEmpty(responseTrackingId)) {

            if (trackingID.compareToIgnoreCase(responseTrackingId)!=0) {
                result = true;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Tracking ID and Bus Ack Tracking ID do not match as expected.", GREEN));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(trackingID, GREEN));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseTrackingId, GREEN));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, GREEN));
            } else {
                result = false;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Tracking ID and Bus Ack Tracking ID unexpectedly match.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(trackingID, RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseTrackingId, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
        } else {
            result = false;
            if (Utils.isNullOrEmpty(trackingID)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request Tracking ID is malformed or not present.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(trackingID == null ? "Null" : trackingID, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
            if (Utils.isNullOrEmpty(responseTrackingId)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Bus Ack Tracking ID is malformed or not present.", RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseTrackingId == null ? "Null" : responseTrackingId, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
        }
        setDescription(sb.toString());

        return result;
    }
}
