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
 * Delay function. Configured as a test takes first argument is delay in
 * milliseconds. Optional second argument is a threshold (again in milliseconds)
 * below which the delay will be retried if interrupted.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class DelayFunction
        implements TestFunction {

    private static final int DELAYSPEC = 0;
    private static final int THRESHOLDSPEC = 1;

    private long threshold;
    private long delay;

    /**
     * public constructor
     */
    public DelayFunction() {
        threshold = 0;
        delay = 0;
    }

    @Override
    public void init(TestContext testCtx)
            throws Exception {
        switch (testCtx.functionArg().size()) {
            case 2:
                threshold = Long.parseLong(testCtx.functionArg(THRESHOLDSPEC).getText());
                // drop through
            case 1:
                delay = Long.parseLong(testCtx.functionArg(DELAYSPEC).getText());
                break;
            default:
                throw new Exception("Invalid number of parameters for DelayFunction "+testCtx.functionArg().size());
        }
    }

    @Override
    public boolean execute(String instanceName, Schedule s, Test t)
            throws Exception {
        s.getScript().log(new ReportItem(s.getName(), t.getName(), "Delay " + delay + " milliseconds"));
        Long start = System.currentTimeMillis();
        Long wtime = delay;
        boolean retry = true;

        while (retry) {
            try {
                synchronized (this) {
                    wait(wtime);
                }
                retry = false;
            } catch (InterruptedException e) {
                if (threshold != 0) {
                    Long period = System.currentTimeMillis() - start;
                    if ((delay - period) < threshold) {
                        retry = false;
                    } else {
                        wtime = wtime - period;
                        if (wtime < 0) {
                            retry = false;
                        }
                    }
                } else {
                    retry = false;
                }
            }
        }
        return true;
    }
}
