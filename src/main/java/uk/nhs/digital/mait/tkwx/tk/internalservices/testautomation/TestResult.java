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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

/**
 *
 * @author Richard Robinson rrobinson@nhs.net This class represents the state of
 * the outcome of autotest tests taking one of 3 values
 */
public enum TestResult {
    FAIL, PASS, CHECK;

    /**
     * Maps a boolean test result onto the appropriate TestResult state
     *
     * @param b
     * @return corresponding TestResult object
     */
    public static TestResult valueOf(boolean b) {
        return b ? TestResult.PASS : TestResult.FAIL;
    }
}
