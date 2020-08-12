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
 * takes a single test and returns a logical inversion of the test result.
 *
 * @author sifa2
 */
public class NotPassFailCheck extends AbstractLogicalOperatorPassFailCheck {

    /**
     * runs the logical negation operator on the the result of the test
     *
     * @param s
     * @param copiedInResponse
     * @param copiedInRequest
     * @return
     * @throws Exception
     */
    @Override
    protected TestResult processSubTests(Script s, byte[] copiedInResponse, byte[] copiedInRequest) throws Exception {

        if (subTests.length != 1) {
            throw new Exception("Logical Operator Not must take one argument");
        }

        testResult = copyStreamsRunTest(subTests[0], s, copiedInResponse, copiedInRequest);
        description = "Not of :" + subTests[0].getDescription();
        return (testResult == TestResult.PASS) ? TestResult.FAIL : TestResult.PASS;
    }
}
