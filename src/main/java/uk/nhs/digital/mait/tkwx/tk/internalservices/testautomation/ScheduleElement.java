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

import java.util.ArrayList;
import java.io.File;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ScheduleContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestNameContext;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Class to hold a single "element" in a Schedule. An "element" is either a
 * single test, or a "loop" construct containing one or more tests (note that at
 * present, loops cannot contain other loops).
 *
 * @author Damian Murphy murff@warlock.org
 */
public class ScheduleElement {

    public enum ScheduleElementType {
        SINGLE,
        LOOP
    };

    private ScheduleElementType type = null;
    private String testName = null;
    private Test test = null;
    private ArrayList<String> subElements = null;
    private ArrayList<Test> testList = null;
    private int loopCount = 0; // means infinite

    /**
     * Antlr parser version
     *
     * @param t integer element type 0 +. Single 1 =&gt; loop
     * @param scheduleCtx
     */
    public ScheduleElement(ScheduleElementType t, ScheduleContext scheduleCtx) {
        type = t;
        subElements = new ArrayList<>();
        for (TestNameContext testNameCtx : scheduleCtx.testName()) {
            subElements.add(testNameCtx.getText());
        }
        
        if (scheduleCtx.FOR() != null) {
            loopCount = Integer.parseInt(scheduleCtx.INTEGER().getText());
        }
    }

    /**
     * Single test.
     *
     * @param n element name string
     */
    public ScheduleElement(String n) {
        type = ScheduleElementType.SINGLE;
        testName = n;
    }

    public boolean isMultiple() {
        return (subElements != null);
    }

    /**
     * Link to the actual specified Test instance(s), retrieved from the parser.
     * populates testList for loop tests , performs chaining for LOOP tests NB
     * This method copies tests so that they could be reused in more than one
     * schedule
     *
     * @param p ScriptParser
     * @throws Exception
     */
    public void getTests(ScriptParser p)
            throws Exception {
        if (type == ScheduleElementType.SINGLE) {
            test = (Test) p.getTest(testName).copy();
            if (test == null) {
                throw new Exception("Test " + testName + " not found");
            }
        } else {
            testList = new ArrayList<>();
            Test prevTest = null;
            for (String s : subElements) {
                Test t = (Test) p.getTest(s).copy();
                if (t == null) {
                    throw new Exception("Test " + t + " not found");
                }
                testList.add(t);
                if (prevTest != null && t.getChainType() != null) {
                    t.setChain(prevTest);
                }
                // Must relink the LOOP tests here
                t.link(p);
                prevTest = t;
            }
        }
    }

    /**
     * Called from Schedule.execute() to execute the Test(s) in this element.
     *
     * @param schedule
     * @param runName
     * @param logroot
     * @return
     * @throws Exception
     */
    boolean execute(Schedule schedule, String runName, File logroot)
            throws Exception {
        boolean pass = true;
        String sof = schedule.getScript().getProperty(STOP_ON_FAIL_PROPERTY);
        boolean stoponfail = sof == null || isY(sof);
        if (isMultiple()) {
            int iteration = 1;
            boolean keepGoing = true;
            while (keepGoing) {
                StringBuilder sb = new StringBuilder(schedule.getName());
                sb.append("/");
                sb.append(testName);
                sb.append("/");
                sb.append(iteration);
                for (Test t : testList) {
                    System.out.println("ITK Testbench running test " + t.getName());
                    keepGoing = t.execute(sb.toString(), schedule, iteration);
                    if (!stoponfail) {
                        keepGoing = true;
                    }
                    if (!keepGoing) {
                        ReportItem r = new ReportItem(schedule.getName(), testName, "Loop break on test failure");
                        schedule.getScript().log(r);
                        pass = false;
                        break;
                    }
                }
                if (loopCount != 0 && iteration >= loopCount) {
                    break;
                }
                ++iteration;
            }
        } else {
            StringBuilder sb = new StringBuilder(schedule.getName());
            sb.append("/");
            sb.append(testName);
            System.out.println("ITK Testbench running test " + testName);
            pass = test.execute(sb.toString(), schedule);
        }
        return pass;
    }

    /**
     * @return the test
     */
    public Test getTest() {
        return test;
    }
}
