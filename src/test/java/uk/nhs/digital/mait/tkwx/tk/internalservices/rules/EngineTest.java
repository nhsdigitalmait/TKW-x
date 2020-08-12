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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.ByteArrayInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 *
 * @author sifa2
 */
public class EngineTest {

    private EngineImpl instance;
    
    public EngineTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new EngineImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class Engine.
     * @throws java.lang.Exception
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        String filename = "";
        instance.load(filename);
    }

    /**
     * Test of execute method, of class Engine.
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String typedata = "";
        String input = "";
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(typedata, input);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class Engine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Node() throws Exception {
        System.out.println("execute");
        String typedata = "";
        Node input = null;
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(typedata, input);
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiateResponse method, of class Engine.
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse serviceResponse = null;
        String input = "";
        ServiceResponse expResult = null;
        ServiceResponse result = instance.instantiateResponse(serviceResponse, input);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class Engine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_5args() throws Exception {
        System.out.println("execute_5args");
        String httpMethod = "";
        String contextPath = "";
        String content = "";
        ServiceResponse expResult = null;
        
        HttpRequest httpRequest = new HttpRequest("");
        //httpRequest.setRequestType(type);
        //httpRequest.setRequestContext(contextPath);
        httpRequest.setInputStream(new ByteArrayInputStream(content.getBytes()));
        httpRequest.setContentLength(content.length());

        // req action method contextpath content
        ServiceResponse result = instance.execute(httpRequest);
        assertEquals(expResult, result);
    }

    /**
     * Test of isRestful method, of class Engine.
     */
    @Test
    public void testIsRestful() {
        System.out.println("isRestful");
        boolean expResult = false;
        boolean result = instance.isRestful();
        assertEquals(expResult, result);
    }

    public class EngineImpl implements Engine {

        @Override
        public void load(String filename) throws Exception {
        }

        /**
         *
         * @param typedata
         * @param input
         * @return
         * @throws RulesException
         * @throws Exception
         */
        @Override
        public ServiceResponse execute(String typedata, String input) throws RulesException, Exception {
            return null;
        }

        @Override
        public ServiceResponse execute(String typedata, Node input) throws RulesException, Exception {
            return null;
        }

        @Override
        public ServiceResponse instantiateResponse(ServiceResponse serviceResponse, String input) throws RulesException, Exception {
            return null;
        }

        @Override
        public boolean isRestful() {
            return false;
        }

        @Override
        public ServiceResponse execute(Request req) throws RulesException, Exception {
            return null;
        }

        @Override
        public ServiceResponse instantiateResponse(ServiceResponse serviceResponse, HttpRequest req) throws RulesException, Exception {
            return null;
        }
    }

    /**
     * Test of execute method, of class Engine.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Request() throws Exception {
        System.out.println("execute");
        String content = "";
        ServiceResponse expResult = null;
        
        Request httpRequest = new HttpRequest("");
        //httpRequest.setRequestType(type);
        //httpRequest.setRequestContext(contextPath);
        httpRequest.setInputStream(new ByteArrayInputStream(content.getBytes()));
        httpRequest.setContentLength(content.length());

        // req action method contextpath content
        ServiceResponse result = instance.execute(httpRequest);
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiateResponse method, of class Engine.see testInstantiateResponse
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_String() throws Exception {
        System.out.println("instantiateResponse");
    }

    /**
     * Test of instantiateResponse method, of class Engine.
     */
    @Test
    public void testInstantiateResponse_ServiceResponse_HttpRequest() throws Exception {
        System.out.println("instantiateResponse");
        ServiceResponse arg0 = new ServiceResponse();
        HttpRequest arg1 = new HttpRequest("");
        ServiceResponse expResult = null;
        ServiceResponse result = instance.instantiateResponse(arg0, arg1);
        assertEquals(expResult, result);
    }
}
