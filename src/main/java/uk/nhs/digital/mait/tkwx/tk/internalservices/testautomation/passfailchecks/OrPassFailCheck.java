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
 * takes one or more subTests and performs a partially evaluated logical or on
 * the test results.
 *
 * @author sifa2
 */
public class OrPassFailCheck extends AbstractLogicalOperatorPassFailCheck {

    /**
     * runs one or more tests which are logically or'ed under partial evaluation
     * (ie stop evaluating when you get a success)
     * @param s
     * @param copiedInSync
     * @param copiedInAsync
     * @return TestResult
     * @throws Exception
     */
    @Override
    protected TestResult processSubTests(Script s, byte[] copiedInSync, byte[] copiedInAsync) throws Exception {

        for (PassFailCheck pf : subTests) {
            testResult = copyStreamsRunTest(pf, s, copiedInSync, copiedInAsync);
            // concatenate fail reasons
            description += pf.getDescription() + "<BR/>";
            if (testResult == TestResult.PASS) {
                // return the first encountered success reason (if there is one)
                description = pf.getDescription();
                break;
            }
        }
        // this will be a fail, return a concatenation of fail reasons in the description.
        return testResult;
    }

}
