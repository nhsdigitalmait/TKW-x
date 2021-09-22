/*
 Copyright 2012  Richard Robinson rick@robinsonhq.com

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
package uk.nhs.digital.mait.tkwx.http;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * A class to orchestrate the processing of pipelining HTTP requests. Each
 * process is added to the queue and the response is sent as each is ready in
 * order. A closeOnCompletion request can be made which will wait until all
 * responses have been written before ending
 *
 * @author RIRO
 */
public class PipeliningQueue extends Thread {

    private final LinkedList<HttpRequest> tQueue;
    private boolean queueRunning = true;
    private final OutputStream outputStream;
    private boolean closeOnCompletion = false;
    private RequestReader reader = null;
    private String reporterClass;

    /**
     *
     * @param os OutputStream Typically a socket OutputStream
     * @param r RequestReader
     */
    PipeliningQueue(OutputStream os, RequestReader r) {
        try {
            Configurator config = Configurator.getConfigurator();
            reporterClass = config.getConfiguration("tks.classname.LastResortReporter");
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, PipeliningQueue.class.getName(),
                    "Error getting configurator : " + e.getMessage());
        }
        this.tQueue = new LinkedList<>();
        outputStream = os;
        reader = r;
        this.start();
    }

    /**
     * Each process is added to the queue and the response is sent as each is
     * ready in order. A closeOnCompletion request can be made which will wait
     * until all responses have been written before ending
     */
    @Override
    synchronized public void run() {
//      System.out.format("PipeliningQueue.run started @" + new Date()+" - %#x\r\n", hashCode());
        this.setName("PipeliningQueueThread");

        while (queueRunning) {
            try {
//                System.out.println("Waiting CoC = " + closeOnCompletion + " queue length = " + tQueue.size());
//                wait();
//                System.out.println("Notified CoC = " + closeOnCompletion + " queue length = " + tQueue.size());
                if (tQueue.size() > 0) {
//                  System.out.format("PipeliningQueue.run@" + new Date()+" - %#x queue has %d requests\r\n", hashCode(),tQueue.size());
                    HttpRequest req = tQueue.removeFirst();
                    HttpResponse resp = req.getResponse();
                    while (!resp.isResponseReady()) {
                        //do nothing while waiting for the response
                        wait(100);
//                      System.out.format("PipeliningQueue.innerloop@" + new Date()+" %#x\r\n", hashCode());
                    }
                    write(resp.getHttpHeader(), resp.getHttpBuffer());
//                  System.out.format("1. Message sent " + this.toString()+ resp.getHttpHeader()+" in response to "+req.getField(SOAP_ACTION_HEADER)+" %#x\r\n", hashCode());
                } else {
//                  System.out.format("PipeliningQueue.run@" + new Date() + " - %#x queue empty waiting 10ms\r\n", hashCode());
                    wait(10); // required to stop tight loop stealing cycles
                    if (closeOnCompletion) {
//                      System.out.format("2. queue running - hash %#x", hashCode());
                        if (tQueue.isEmpty()) {
                            queueRunning = false;
                            try {
//                        System.out.println("3. outputStream closed - hash "+ this.hashCode());
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                            };
                        } else {
//                          System.out.format("PipeliningQueue.run@" + new Date() + " - %#x CoC with Non Empty queue size = %d\r\n", hashCode(), tQueue.size());
                        }
                    }
                }
            } catch (InterruptedException i) {
//              System.out.format("PipeliningQueue.run@" + new Date() + " - %#x InterruptedException %s\r\n", hashCode(), i.getMessage());
            }
        } // while queue running
        synchronized (reader) {
//          System.out.format("Pipeline.notify@" + new Date() +" %#x\r\n", hashCode());
            reader.notify();
        }
        //System.out.format("PipeliningQueue.run@" + new Date() + " - %#x thread terminating\r\n", hashCode());
    } // run

    /**
     * A method for adding a request to be handled.
     *
     * @param req a HttpRequest object to be added
     */
    public synchronized void addQueueEntry(HttpRequest req) {
        //System.out.format("PipeliningQueue.addQueueEntry@" + new Date() + " - %#x request %s added\r\n", hashCode(), req.getRequestType());
        tQueue.addLast(req);
        notify();
    }

    /**
     * send a continue response
     *
     * @param s Header string
     */
    public void continueResponse(String s) {
        write(s, new byte[0]);
    }

    /**
     * Start the queue running
     */
    /**
     * A method to close the HTTP connection after successfully responding to
     * all remaining responses on the queue
     */
    public synchronized void closeOnCompletion() {
        //System.out.format("PipeliningQueue.closeOnCompletion@" + new Date() + " - %#x\r\n", hashCode());
        closeOnCompletion = true;
        notify();
    }

    /**
     * Stop the queue running
     */
    public synchronized void stopQueue() {
        queueRunning = false;
        notify();
    }

    /**
     * send response back down socket
     *
     * @param header String
     * @param b byte array containing body
     */
    private void write(String header, byte[] b) {
        try {
            if (header != null) {
                outputStream.write(header.getBytes());
                outputStream.flush();
            }
            outputStream.write(b);
            outputStream.flush();
        } catch (IOException e) {
            String s = "Error writing response: " + e.getClass().toString() + " : " + e.getMessage();
            System.err.println(s);
//            System.out.println("pipeliningLast - hash "+ this.hashCode());
            try {
                LastResortReporter reporter =
                        (LastResortReporter) Class
                                .forName(reporterClass)
                                .getConstructor()
                                .newInstance();
                reporter.report(s, outputStream);
            } catch (ClassNotFoundException
                    | NoSuchMethodException
                    | InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException ex) {
                Logger.getInstance().log(SEVERE, PipeliningQueue.class.getName(),
                        "Cannot start last resort reporter");
            }
        }
    }
}
