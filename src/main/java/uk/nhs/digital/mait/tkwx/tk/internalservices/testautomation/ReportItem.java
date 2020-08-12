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
import java.util.Date;
 /**
 * Class representing the outcome of a single test. Constructed by the Test instances
 * and used by the ReportWriter to produce the report of the test.
 * 
 * @author Damian Murphy murff@warlock.org
 */
class ReportItem {
    
   
    private String added = ReportWriter.FORMAT.format(new Date());
    private String scheduleName = null;
    private String testName = null;
    private TestResult testPassed = null;
    private String logComment = null;
    private String detail = null;
    private String logfile = null;
    
    /**
     * Create a ReportItem with a result (passedPresent() will return true)
     * 
     * @param schedule String schedule name
     * @param test String test name
     * @param passed Whether this test passed
     * @param comment String comment
     */
    ReportItem(String schedule, String test, TestResult passed, String comment) {
        scheduleName = schedule;
        testName = test;
        testPassed = passed;
        logComment = comment;        
    }

    /**
     * Create a ReportItem without a result (passedPresent() will return false)
     * 
     * @param schedule String schedule name
     * @param test String test name
     * @param comment String comment
     */    
    ReportItem(String schedule, String test, String comment) {
        scheduleName = schedule;
        testName = test;
        logComment = comment;                
    }
    
    void setLogFile(String f) { logfile = f; }
    void addDetail(String s) { detail = s; }
    
    String getLogFile() { return logfile; }
    String getTime() { return added; }
    String getSchedule() { return scheduleName; }
    String getTest() { return testName; }
    String getComment() { return logComment; }
    String getDetail() { return detail; }
    TestResult getPassed() { return testPassed; }
    boolean passedPresent() { return testPassed != null; }
    boolean passed() { return ((testPassed != null) && (testPassed != TestResult.FAIL)); }
}
