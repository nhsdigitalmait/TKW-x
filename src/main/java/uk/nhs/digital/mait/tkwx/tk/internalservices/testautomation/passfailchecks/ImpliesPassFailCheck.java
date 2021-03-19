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
 * takes two subTests and performs an implication operation on the test results.
 * ie a &ge; b if a is false then return true otherwise return b. Used for conditional subTests.
 *
 * @author sifa2
 */
public class ImpliesPassFailCheck extends AbstractLogicalOperatorPassFailCheck {

    /**
     * clones the input streams and runs two tests the results of which 
     * are operated on by the logical implication operator ie if x is true then
     * return the value of y otherwise return true useful for conditional tests.
     * eg if an optional element is present then it must contain certain text otherwise its a pass
     * @param s
     * @param copiedInSync
     * @param copiedInAsync
     * @return
     * @throws Exception 
     */
    @Override
    protected TestResult processSubTests(Script s, byte[] copiedInSync, byte[] copiedInAsync) throws Exception {

        if (subTests.length != 2) {
            throw new Exception("Logical Operator Implies must take two arguments");
        }
        testResult = copyStreamsRunTest(subTests[0], s, copiedInSync, copiedInAsync);
        description = colourString("Result: ", BLACK);
        if (testResult == TestResult.PASS) {
            description += colourString("Pre-condition evaluates true.", GREEN);
            testResult = copyStreamsRunTest(subTests[1],s, copiedInSync, copiedInAsync);
            description +=  "<BR/>" + subTests[1].getDescription();
        } else {
            description += colourString("Pre-condition evaluates false.", GREEN);
            testResult = TestResult.PASS;
        }
        return testResult;
    }
}
