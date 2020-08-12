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

import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

/**
 *
 * @author sifa2
 */
public class HttpTimerTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();
    
    private HttpTimer instance;

    public HttpTimerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("tks.HttpTransport.pipeline.persistperiod", "100");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new HttpTimer(new Socket());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class HttpTimer.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // called via start in constructor
    }

    /**
     * Test of reset method, of class HttpTimer.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        instance.reset();
    }

    /**
     * Test of stopTimer method, of class HttpTimer.
     */
    @Test
    public void testStopTimer() {
        System.out.println("stopTimer");
        instance.stopTimer();
    }

}
