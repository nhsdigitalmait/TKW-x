/*
 Copyright 2014  Damian Murphy murff@warlock.org

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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;

/**
 * takes multiple passfail expressions and does a partial evaluation logical and on them
 * ie continues evaluation until a false is found or until all are true
 *
 * @author sifa2
 */
public class AndPassFailCheck extends AbstractLogicalOperatorPassFailCheck {

    /**
     * runs one or more tests the results of which are logically anded under partial
     * evaluation (ie stop evaluation when there is a fail)
     * @param s script object
     * @param copiedInResponse
     * @param copiedInRequest
     * @return
     * @throws Exception 
     */
    @Override
    protected TestResult processSubTests(Script s, byte[] copiedInResponse, byte[] copiedInRequest) throws Exception {
        for (PassFailCheck pf : subTests) {
            testResult = copyStreamsRunTest(pf, s, copiedInResponse, copiedInRequest);
            // concatenate success reasons
            description += pf.getDescription() + "<BR/>";
            if (testResult == TestResult.FAIL) {
                // return the first encountered fail reason (if there is one)
                description = pf.getDescription();
                break;
            }
        }
        // this will be a success, return a concatenation of success reasons
        return testResult;
    }
}
