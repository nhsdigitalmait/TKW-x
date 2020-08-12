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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestContext;

 /**
 * The test automation system supports "Tests" which are function executions,
 * rather than messages built by templates and then sent. What the function does is
 * undefined and can be anything useful (for example, the "Delay" function class 
 * implemented in this package). Such "function" classes implement this TestFunction
 * interface. 
 * 
 * @author Damian Murphy murff@warlock.org
 */
public interface TestFunction {
    /**
     * Initialisation using a tokenised list of the line from the test automation
     * script. This is needed (and will be called by the parser) because TestFunction
     * implementations are identified by name, and hence as instantiated via a no-
     * argument constructor.
     * 
     * @param testCtx antlr test context object
     * @throws Exception 
     */
    public void init(TestContext testCtx) throws Exception;
    
    /**
     * Implement the logic for the function in this method. The Schedule and Test 
     * parameters provide access to logging, and log entries should quote the
     * instanceName. 
     * 
     * @param instanceName
     * @param schedule
     * @param test
     * @return True or false depending on whether the function should be considered a "pass".
     * @throws Exception 
     */
    public boolean execute(String instanceName, Schedule schedule, Test test) throws Exception;
}
