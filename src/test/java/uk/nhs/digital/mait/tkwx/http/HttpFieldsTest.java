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
public class HttpFieldsTest {
    private static HttpFields instance;
    
    public HttpFieldsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = new HttpFields("h1","v1");
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
     * Test of getValue method, of class HttpFields.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        String expResult = "v1";
        String result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getActualHeader method, of class HttpFields.
     */
    @Test
    public void testGetActualHeader() {
        System.out.println("getActualHeader");
        String expResult = "h1";
        String result = instance.getActualHeader();
        assertEquals(expResult, result);
    }
    
}
