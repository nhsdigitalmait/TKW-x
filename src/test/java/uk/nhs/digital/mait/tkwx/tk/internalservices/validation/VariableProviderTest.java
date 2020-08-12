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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

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
public class VariableProviderTest {

    private VariableProviderImpl instance;
    
    public VariableProviderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new VariableProviderImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getVariable method, of class VariableProvider.
     */
    @Test
    public void testGetVariable() {
        System.out.println("getVariable");
        String s = "";
        Object expResult = null;
        Object result = instance.getVariable(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariable method, of class VariableProvider.
     */
    @Test
    public void testSetVariable() {
        System.out.println("setVariable");
        String s = "";
        Object o = null;
        instance.setVariable(s, o);
    }

    public class VariableProviderImpl implements VariableProvider {

        public Object getVariable(String s) {
            return null;
        }

        public void setVariable(String s, Object o) {
        }
    }
    
}
