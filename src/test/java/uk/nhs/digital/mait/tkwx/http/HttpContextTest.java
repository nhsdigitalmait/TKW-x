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

import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.handlers.HTTPGetUnixTimestampHandler;

/**
 *
 * @author sifa2
 */
public class HttpContextTest {

    private static HttpContext instance;

    public HttpContextTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new HttpContext();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setContextPath method, of class HttpContext.
     */
    @Test
    public void testSetContextPath() throws Exception {
        System.out.println("setContextPath");
        String p = "ctp";
        instance.setContextPath(p);
    }

    /**
     * Test of getContextPath method, of class HttpContext.
     */
    @Test
    public void testGetContextPath() throws Exception {
        System.out.println("getContextPath");
        String expResult = "ctp1";
        instance.setContextPath(expResult);
        String result = instance.getContextPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of addHandler method, of class HttpContext.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddHandler() throws Exception {
        System.out.println("addHandler");
        instance.addHandler(new HTTPGetUnixTimestampHandler());
    }

    /**
     * Test of getHandlers method, of class HttpContext.
     */
    @Test
    public void testGetHandlers() {
        System.out.println("getHandlers");
        boolean expResult = false;
        Iterator<Handler> result = instance.getHandlers();
        assertEquals(expResult, result.hasNext());
    }

}
