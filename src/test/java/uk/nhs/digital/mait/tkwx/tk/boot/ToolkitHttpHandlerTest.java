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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;

/**
 *
 * @author sifa2
 */
public class ToolkitHttpHandlerTest {
    
    public ToolkitHttpHandlerTest() {
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
     * Test of setToolkit method, of class ToolkitHttpHandler.
     */
    @Test
    public void testSetToolkit() throws Exception {
        System.out.println("setToolkit");
        HttpTransport t = null;
        ToolkitHttpHandler instance = new ToolkitHttpHandlerImpl();
        instance.setToolkit(t);
    }

    /**
     * Test of setAsynchronousTTL method, of class ToolkitHttpHandler.
     */
    @Test
    public void testSetAsynchronousTTL() {
        System.out.println("setAsynchronousTTL");
        int i = 0;
        ToolkitHttpHandler instance = new ToolkitHttpHandlerImpl();
        instance.setAsynchronousTTL(i);
    }

    /**
     * Test of setSavedMessagesDirectory method, of class ToolkitHttpHandler.
     */
    @Test
    public void testSetSavedMessagesDirectory() {
        System.out.println("setSavedMessagesDirectory");
        String s = "";
        ToolkitHttpHandler instance = new ToolkitHttpHandlerImpl();
        instance.setSavedMessagesDirectory(s);
    }

    /**
     * Test of setAsynchronousOffsetSeconds method, of class ToolkitHttpHandler.
     */
    @Test
    public void testSetAsynchronousOffsetSeconds() {
        System.out.println("setAsynchronousOffsetSeconds");
        int s = 0;
        ToolkitHttpHandler instance = new ToolkitHttpHandlerImpl();
        instance.setAsynchronousOffsetSeconds(s);
    }

    /**
     * Test of handle method, of class ToolkitHttpHandler.
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("handle");
        String path = "";
        String params = "";
        HttpRequest req = null;
        HttpResponse resp = null;
        ToolkitHttpHandler instance = new ToolkitHttpHandlerImpl();
        instance.handle(path, params, req, resp);
    }

    public class ToolkitHttpHandlerImpl extends ToolkitHttpHandler {

        public void handle(String path, String params, HttpRequest req, HttpResponse resp) throws HttpException {
        }
    }

}
