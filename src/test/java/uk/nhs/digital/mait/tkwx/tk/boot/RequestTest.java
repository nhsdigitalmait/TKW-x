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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class RequestTest {

    private RequestImpl instance;
    
    public RequestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new RequestImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getField method, of class Request.
     */
    @Test
    public void testGetField() {
        System.out.println("getField");
        String h = "";
        String expResult = "";
        String result = instance.getField(h);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSourceId method, of class Request.
     */
    @Test
    public void testGetSourceId() {
        System.out.println("getSourceId");
        String expResult = "id";
        instance.sourceId = expResult;
        String result = instance.getSourceId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRequestContext method, of class Request.
     */
    @Test
    public void testSetRequestContext() throws Exception {
        System.out.println("setRequestContext");
        String c = "";
        instance.setRequestContext(c);
    }

    /**
     * Test of setRequestType method, of class Request.
     */
    @Test
    public void testSetRequestType() throws Exception {
        System.out.println("setRequestType");
        String r = "";
        instance.setRequestType(r);
    }

    /**
     * Test of setInputStream method, of class Request.
     */
    @Test
    public void testSetInputStream() {
        System.out.println("setInputStream");
        InputStream h = null;
        instance.setInputStream(h);
    }

    /**
     * Test of setContentLength method, of class Request.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetContentLength() throws Exception {
        System.out.println("setContentLength");
        int c = 0;
        instance.setContentLength(c);
    }

    public class RequestImpl extends Request {

        @Override
        public String getField(String h) {
            return "";
        }

        @Override
        public void setRequestContext(String c) throws Exception {
        }

        @Override
        public void setRequestType(String r) throws Exception {
        }

        @Override
        public void setInputStream(InputStream h) {
        }

        @Override
        public void setContentLength(int c) throws Exception {
        }

        @Override
        public void setHeader(String h, String v) throws Exception {
        }

        @Override
        public void updateHeader(String h, String v) throws Exception {
        }
    }

    /**
     * Test of setHeader method, of class Request.
     */
    @Test
    public void testSetHeader() throws Exception {
        System.out.println("setHeader");
        String arg0 = "";
        String arg1 = "";
        instance.setHeader(arg0, arg1);
    }

    /**
     * Test of updateHeader method, of class Request.
     */
    @Test
    public void testUpdateHeader() throws Exception {
        System.out.println("updateHeader");
        String arg0 = "";
        String arg1 = "";
        instance.updateHeader(arg0, arg1);
    }
   
}
