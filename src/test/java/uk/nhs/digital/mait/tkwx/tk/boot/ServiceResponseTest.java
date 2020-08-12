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
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;

/**
 *
 * @author sifa2
 */
public class ServiceResponseTest {

    private ServiceResponse instance;
    
    public ServiceResponseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ServiceResponse();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setAction method, of class ServiceResponse.
     */
    @Test
    public void testSetAction() {
        System.out.println("setAction");
        String a = "";
        instance.setAction(a);
    }

    /**
     * Test of setArray method, of class ServiceResponse.
     */
    @Test
    public void testSetArray() {
        System.out.println("setArray");
        Object[] a = null;
        instance.setArray(a);
    }

    /**
     * Test of setScalar method, of class ServiceResponse.
     */
    @Test
    public void testSetScalar() {
        System.out.println("setScalar");
        Object o = null;
        instance.setScalar(o);
    }

    /**
     * Test of setCode method, of class ServiceResponse.
     */
    @Test
    public void testSetCode() {
        System.out.println("setCode");
        int i = 0;
        instance.setCode(i);
    }

    /**
     * Test of setCodePhrase method, of class ServiceResponse.
     */
    @Test
    public void testSetCodePhrase() {
        System.out.println("setCodePhrase");
        String s = "";
        instance.setCodePhrase(s);
    }

    /**
     * Test of setResponse method, of class ServiceResponse.
     */
    @Test
    public void testSetResponse() {
        System.out.println("setResponse");
        String s = "";
        instance.setResponse(s);
    }

    /**
     * Test of getAction method, of class ServiceResponse.
     */
    @Test
    public void testGetAction() {
        System.out.println("getAction");
        String expResult = "action";
        instance.setAction(expResult);
        String result = instance.getAction();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCode method, of class ServiceResponse.
     */
    @Test
    public void testGetCode() {
        System.out.println("getCode");
        int expResult = 0;
        int result = instance.getCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResponse method, of class ServiceResponse.
     */
    @Test
    public void testGetResponse() {
        System.out.println("getResponse");
        String expResult = "response";
        instance.setResponse(expResult);
        String result = instance.getResponse();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCodePhrase method, of class ServiceResponse.
     */
    @Test
    public void testGetCodePhrase() {
        System.out.println("getCodePhrase");
        String expResult = "codephrase";
        instance.setCodePhrase(expResult);
        String result = instance.getCodePhrase();
        assertEquals(expResult, result);
    }

    /**
     * Test of getScalar method, of class ServiceResponse.
     */
    @Test
    public void testGetScalar() {
        System.out.println("getScalar");
        Object expResult = new Integer(99);
        instance.setScalar(expResult);
        Object result = instance.getScalar();
        assertEquals(expResult, result);
    }

    /**
     * Test of getArray method, of class ServiceResponse.
     */
    @Test
    public void testGetArray() {
        System.out.println("getArray");
        Object[] expResult = {"a","b"};
        instance.setArray(expResult);
        Object[] result = instance.getArray();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of setHttpHeaders method, of class ServiceResponse.
     */
    @Test
    public void testSetHttpHeaders() {
        System.out.println("setHttpHeaders");
        HttpHeaderManager httpHeaders = new HttpHeaderManager();
        instance.setHttpHeaders(httpHeaders);
    }

    /**
     * Test of getHttpHeaders method, of class ServiceResponse.
     */
    @Test
    public void testGetHttpHeaders() {
        System.out.println("getHttpHeaders");
        HttpHeaderManager expResult = new HttpHeaderManager();
        instance.setHttpHeaders(expResult);
        HttpHeaderManager result = instance.getHttpHeaders();
        assertEquals(expResult, result);
    }
    
}
