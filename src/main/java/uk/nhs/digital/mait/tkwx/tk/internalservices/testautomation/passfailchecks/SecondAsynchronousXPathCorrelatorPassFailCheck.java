/*
 Copyright 2021  Simon Farrow <simon.farrow1@nhs.net>

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

import org.xml.sax.InputSource;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;

/**
 *
 * @author simonfarrow
 */
public class SecondAsynchronousXPathCorrelatorPassFailCheck
        extends AbstractSecondAsynchronousRequestResponseComparatorPassFailCheck {

    @Override
    public void init(PassFailCheckContext passfailCheckCtx)
            throws Exception {
        pfc = new XPathCorrelatorPassFailCheck(this);
        pfc.init(passfailCheckCtx);
    }

    @Override
    protected boolean doChecks(Script s, InputSource request, InputSource response)
            throws Exception {
        return pfc.doChecks(s, request, response);
    }
    private AbstractRequestResponseComparatorPassFailCheck pfc;

}
