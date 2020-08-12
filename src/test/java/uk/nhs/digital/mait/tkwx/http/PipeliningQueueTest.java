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
package uk.nhs.digital.mait.tkwx.http;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static org.mockito.Mockito.mock;
import static uk.nhs.digital.mait.tkwx.http.HttpResponse.SUPPRESS_CLOSE_PROPERTY;

/**
 * The subject class is intimately connected to RequestReader which instantiates
 * it
 *
 * @author sifa2
 */
public class PipeliningQueueTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private PipeliningQueue instance;
    private OutputStream ostream;

    public PipeliningQueueTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("tks.HttpTransport.pipeline.persistperiod", "100");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws InterruptedException {
        ostream = new ByteArrayOutputStream();
        RequestReader rr = mock(RequestReader.class);
        instance = new PipeliningQueue(ostream, rr);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class PipeliningQueue.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // tested by constructor which invokes start
    }

    /**
     * Test of addQueueEntry method, of class PipeliningQueue.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public synchronized void testAddQueueEntry() throws IOException, InterruptedException {
        System.out.println("addQueueEntry");
        HttpRequest req = new HttpRequest("id");
        HttpResponse resp = new HttpResponse(ostream);
        req.setResponse(resp);

        instance.addQueueEntry(req);

        String expResult = "response";
        resp.getOutputStream().write(expResult.getBytes());
        // this is essential
        resp.getOutputStream().flush();

        // close alone does not work
        resp.getOutputStream().close();

        // need this also
        instance.closeOnCompletion();

        // TODO this test fails without the 1ms delay
        wait(1);

        // tells the pipeline its ok to return the response
        req.setHandled(true);
        String result = ostream.toString();
        System.out.println(result);
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of addQueueEntry method, of class PipeliningQueue.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public synchronized void testAddMultipleQueueEntry() throws IOException, InterruptedException {
        System.out.println("addMultipleQueueEntry");

        // this needs setting for pipelining to work properly with multiple responses
        System.setProperty(SUPPRESS_CLOSE_PROPERTY, "y");

        HttpRequest[] reqs = new HttpRequest[]{new HttpRequest("id"), new HttpRequest("id1")};
        HttpResponse[] resps = new HttpResponse[]{new HttpResponse(ostream), new HttpResponse(ostream)};
        String[] expResults = new String[reqs.length];
        for (int i = 0; i < reqs.length; i++) {
            reqs[i].setResponse(resps[i]);
            instance.addQueueEntry(reqs[i]);
            expResults[i] = "response" + i;
            resps[i].setStatus(200, "OK");
            if (i == 1) {
                // TODO this fails witha final connection close
                //resps[i].setField("Connection", "close");
            }
            resps[i].getOutputStream().write(expResults[i].getBytes());
            // this is essential
            resps[i].getOutputStream().flush();

            // close alone does not work
            resps[i].getOutputStream().close();
        }

        // need this also
        instance.closeOnCompletion();

        // TODO this test does not fail without the 1ms delay
        wait(1);

        // tells the pipeline its ok to return the response
        for (int i = 0; i < reqs.length; i++) {
            reqs[i].setHandled(true);
        }

        String result = ostream.toString();
        System.out.println(result);
        for (int i = 0; i < reqs.length; i++) {
            assertTrue("resp " + i, result.contains(expResults[i]));
        }
        System.setProperty(SUPPRESS_CLOSE_PROPERTY, "");
    }

    /**
     * Test of continueResponse method, of class PipeliningQueue.
     */
    @Test
    public void testContinueResponse() {
        System.out.println("continueResponse");
        String s = "header";
        instance.continueResponse(s);
        System.out.println(ostream.toString());
    }

    /**
     * Test of closeOnCompletion method, of class PipeliningQueue.
     */
    @Test
    public void testCloseOnCompletion() {
        System.out.println("closeOnCompletion");
        instance.closeOnCompletion();
    }

    /**
     * Test of stopQueue method, of class PipeliningQueue.
     */
    @Test
    public void testStopQueue() {
        System.out.println("stopQueue");
        instance.stopQueue();
    }
}
