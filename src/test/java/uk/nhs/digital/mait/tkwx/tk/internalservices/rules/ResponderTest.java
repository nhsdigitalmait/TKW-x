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

import java.util.HashMap;
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
public class ResponderTest {

    private ResponderImpl instance;
    
    public ResponderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ResponderImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of makeResponse method, of class Responder.
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String,Substitution> substitutions = null;
        String o = "";
        String expResult = "";
        String result = instance.makeResponse(substitutions, o);
        assertEquals(expResult, result);
    }

    /**
     * Test of forceClose method, of class Responder.
     */
    @Test
    public void testForceClose() {
        System.out.println("forceClose");
        Boolean expResult = null;
        Boolean result = instance.forceClose();
        assertEquals(expResult, result);
    }

    public class ResponderImpl implements Responder {

        public String makeResponse(HashMap<String,Substitution> substitutions, String o) throws Exception {
            return "";
        }

        @Override
        public Boolean forceClose() {
            return null;
        }

        @Override
        public String makeResponse(HashMap<String,Substitution> substitutions, Object o) throws Exception {
            return null;
        }
    }
}
