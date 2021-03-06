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

/**
 *
 * @author sifa2
 */
public class ListenerTest {
    
    public ListenerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setPort method, of class Listener.
     */
    @Test
    public void testSetPort() {
        System.out.println("setPort");
        int p = 0;
        Listener instance = new ListenerImpl();
        instance.setPort(p);
    }

    /**
     * Test of setHost method, of class Listener.
     */
    @Test
    public void testSetHost() {
        System.out.println("setHost");
        String h = "";
        Listener instance = new ListenerImpl();
        instance.setHost(h);
    }

    /**
     * Test of stopListening method, of class Listener.
     */
    @Test
    public void testStopListening() {
        System.out.println("stopListening");
        Listener instance = new ListenerImpl();
        instance.stopListening();
    }

    /**
     * Test of startListening method, of class Listener.
     */
    @Test
    public void testStartListening() throws Exception {
        System.out.println("startListening");
        HttpServer s = null;
        Listener instance = new ListenerImpl();
        instance.startListening(s);
    }

    public class ListenerImpl implements Listener {

        public void setPort(int p) {
        }

        public void setHost(String h) {
        }

        public void stopListening() {
        }

        public void startListening(HttpServer s) throws HttpServerException {
        }
    }
    
}
