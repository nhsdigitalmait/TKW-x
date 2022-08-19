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

import javax.xml.xpath.XPathExpression;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 * Pass/fail check implementation used for all responses 
 * Takes one or two xpaths
 * If only one xpath check that the expressions evaluate the same in both request and response
 * If two xpaths check that the first expression evaluated in the request equals the second xpath evaluated in the response
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class SynchronousRequestResponseXPathCorrelatorPassFailCheck
        extends AbstractSynchronousRequestResponseComparatorPassFailCheck {
    
    private String secondXpath  = null;
    private XPathExpression secondExpression = null;
    
        /**
     *
     * @param passfailCtx
     * @throws java.lang.Exception
     */
    @Override
    public void init(AutotestParser.PassfailContext passfailCtx)
            throws Exception {
        super.init(passfailCtx);
        
        AutotestParser.XpathCorrelationCheckContext xpathCorrelationCheckContext = passfailCtx.passFailCheck().xpathCorrelationCheck();

        // The second parameter currently does not bacause CST is a one shot
        
        switch ( xpathCorrelationCheckContext.xpathExpression().size() ) {
            case 1:
                givenXpath = xpathCorrelationCheckContext.xpathExpression(0).CST().getText();
                secondXpath = givenXpath;
                break;
            case 2:
                givenXpath = xpathCorrelationCheckContext.xpathExpression(0).CST().getText();
                secondXpath = xpathCorrelationCheckContext.xpathExpression(1).CST().getText();
                break;
            default:
                throw new IllegalArgumentException("Unexpected number of xpaths for SynchronousRequestResponseXPathCorrelatorPassFailCheck");
        }

        try {
            expression = getXpathExtractor(givenXpath);
            secondExpression = getXpathExtractor(secondXpath);
        } catch (Exception e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }
    }

    @Override
    protected boolean doChecks(Script s, InputSource request, InputSource response)
            throws Exception {
        boolean result = true;
        String requestValue = expression.evaluate(request);
        String responseValue = secondExpression.evaluate(response);
        StringBuilder sb = new StringBuilder();
        if (!Utils.isNullOrEmpty(requestValue) && !Utils.isNullOrEmpty(responseValue)) {

            if ((requestValue.compareToIgnoreCase(responseValue)==0)) {
                result = true;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("XPath values are equals as expected.", GREEN));
                sb.append(colourString("<BR/>Request XPATH: ", BLACK));
                sb.append(colourString(givenXpath, GREEN));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(requestValue, GREEN));
                sb.append(colourString("<BR/>Response XPATH: ", BLACK));
                sb.append(colourString(secondXpath, GREEN));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseValue, GREEN));
            } else {
                result = false;
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Xpath values unexpectedly differ.", RED));
                sb.append(colourString("<BR/>Request XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(requestValue, RED));
                sb.append(colourString("<BR/>Response XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseValue, RED));
            }
        } else {
            result = false;
            if (Utils.isNullOrEmpty(requestValue)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Request XPath value is malformed or not present.", RED));
                sb.append(colourString("<BR/>Request: ", BLACK));
                sb.append(colourString(requestValue == null ? "Null" : requestValue, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(givenXpath, RED));
            }
            if (Utils.isNullOrEmpty(responseValue)) {
                sb.append(colourString("Result: ", BLACK));
                sb.append(colourString("Response Xpath value is malformed or not present.", RED));
                sb.append(colourString("<BR/>Response: ", BLACK));
                sb.append(colourString(responseValue == null ? "Null" : responseValue, RED));
                sb.append(colourString("<BR/>XPATH: ", BLACK));
                sb.append(colourString(secondXpath, RED));
            }
        }
        
        setDescription(sb.toString());

        return result;
    }
}
