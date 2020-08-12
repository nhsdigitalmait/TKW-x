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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author sifa2
 */
public class PartTest {

    private Part instance;
    
    public PartTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new Part();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setDocument method, of class Part.
     */
    @Test
    public void testSetDocument() {
        System.out.println("setDocument");
        Document d = null;
        instance.setDocument(d);
    }

    /**
     * Test of setBody method, of class Part.
     */
    @Test
    public void testSetBody() {
        System.out.println("setBody");
        String b = "body";
        instance.setBody(b);
    }

    /**
     * Test of addHeader method, of class Part.
     */
    @Test
    public void testAddHeader() {
        System.out.println("addHeader");
        String hdr = "header";
        String val = "value";
        instance.addHeader(hdr, val);
    }

    /**
     * Test of getBody method, of class Part.
     */
    @Test
    public void testGetBody() {
        System.out.println("getBody");
        String expResult = "xx";
        instance.setBody(expResult);
        String result = instance.getBody();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDocument method, of class Part.
     */
    @Test
    public void testGetDocument() {
        System.out.println("getDocument");
        Document expResult = null;
        Document result = instance.getDocument();
        assertEquals(expResult, result);
    }
    
}
