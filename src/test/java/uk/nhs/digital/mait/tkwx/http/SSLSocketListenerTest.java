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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sifa2
 */
public class SSLSocketListenerTest {

    private SSLSocketListener instance;

    public SSLSocketListenerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new SSLSocketListener();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setPort method, of class SSLSocketListener.
     */
    @Test
    public void testSetPort() {
        System.out.println("setPort");
        int p = 0;
        instance.setPort(p);
    }

    /**
     * Test of setHost method, of class SSLSocketListener.
     */
    @Test
    public void testSetHost() {
        System.out.println("setHost");
        String h = "";
        instance.setHost(h);
    }

    /**
     * Test of stopListening method, of class SSLSocketListener.
     */
    @Test
    public void testStopListening() {
        System.out.println("stopListening");
        instance.stopListening();
    }

    /**
     * Test of startListening method, of class SSLSocketListener.
     */
    @Test
    public void testStartListening() throws Exception {
        System.out.println("startListening");
        HttpServer s = new HttpServer();
        instance.startListening(s);
    }

    /**
     * Test of getException method, of class SSLSocketListener.
     */
    @Test
    public void testGetException() {
        System.out.println("getException");
        Exception expResult = null;
        Exception result = instance.getException();
        assertEquals(expResult, result);
    }

    /**
     * Test of run method, of class SSLSocketListener.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        // called by startListening
    }
}
