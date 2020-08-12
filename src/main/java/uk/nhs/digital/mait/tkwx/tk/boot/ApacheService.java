/*
 Copyright 2012-13  Simon Farrow <simon.farrow1@nhs.net>

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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.util.Arrays;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Provides support for running TKW as a Windows service using the Apache Commons Daemon
 *
 * @author SIFA2
 */
public class ApacheService {

    static String[] tkwargs;

    static class RunnableTKW implements Runnable {

        @Override
        public void run() {
            ToolkitSimulator.main(tkwargs);
        }
    }

    private static Thread tkwThread = null;

    /**
     * apache runsrv is configured to call this method on this class with
     * "start" + tkw params or "stop"
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("ApacheService started with " + args.length + " params");
        for (String param : args) {
            System.out.println(param);
        }
        switch (args[0]) {
            case "start":
                tkwargs = Arrays.copyOfRange(args, 1, args.length);
                RunnableTKW runnableTKW = new RunnableTKW();
                tkwThread = new Thread(runnableTKW);
                tkwThread.start();
                break;
            case "stop":
                if (tkwThread != null) {
                    System.out.println("ApacheService: Stopping Service");
                    System.exit(0);
                } else {
                    Logger.getInstance().log(WARNING,ApacheService.class.getName(),"No thread to kill");
                }
                break;
            default:
                Logger.getInstance().log(WARNING,ApacheService.class.getName(),"Unexpected verb " + args[0]);
        }
    }
};
