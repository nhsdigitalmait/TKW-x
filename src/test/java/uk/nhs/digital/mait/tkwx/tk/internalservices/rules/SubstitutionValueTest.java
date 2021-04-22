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
public class SubstitutionValueTest {

    private SubstitutionValueImpl instance;
    
    public SubstitutionValueTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new SubstitutionValueImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class SubstitutionValue.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String o = "";
        String expResult = "";
        String result = instance.getValue(o);
        assertEquals(expResult, result);
    }

    /**
     * Test of setData method, of class SubstitutionValue.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String s = "";
        instance.setData(s);
    }

    public class SubstitutionValueImpl implements SubstitutionValue {

        @Override
        public String getValue(String o) throws Exception {
            return "";
        }

        @Override
        public void setData(String s) throws Exception {
        }
    }
}
