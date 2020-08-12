/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * allows JUnit tests to show stdout and stderr from processes spawned for
 * testing using ProcessBuilder
 *
 * @author simonfarrow
 */
public class ProcessStreamDumper implements Runnable {

    private final Process process;
    private final PrintStream os;

    /**
     * 
     * @param process 
     */
    public static void dumpProcessStreams(Process process) {
        new Thread(new ProcessStreamDumper(process, System.out)).start();
        new Thread(new ProcessStreamDumper(process, System.err)).start();
    }

    /**
     * Public constructor
     *
     * @param os output stream to write to eg System.out or System.err
     * @param process process to get stream from
     */
    public ProcessStreamDumper(Process process, PrintStream os) {
        this.process = process;
        this.os = os;
    }

    @Override
    public void run() {
        try {
            InputStream is = os == System.out ? process.getInputStream() : process.getErrorStream();
            int c;
            while ((c = is.read()) != -1) {
                os.print((char) c);
            }
            os.close();
        } catch (IOException ex) {
        }
    }
}
