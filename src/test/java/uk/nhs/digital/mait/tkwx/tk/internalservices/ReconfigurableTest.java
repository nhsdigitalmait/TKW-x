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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.util.Properties;
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
public class ReconfigurableTest {
    
    public ReconfigurableTest() {
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
     * Test of reconfigure method, of class Reconfigurable.
     */
    @Test
    public void testReconfigure_Properties() throws Exception {
        System.out.println("reconfigure");
        Properties p = null;
        Reconfigurable instance = new ReconfigurableImpl();
        instance.reconfigure(p);
    }

    /**
     * Test of reconfigure method, of class Reconfigurable.
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = "";
        String value = "";
        Reconfigurable instance = new ReconfigurableImpl();
        String expResult = "";
        String result = instance.reconfigure(what, value);
        assertEquals(expResult, result);
    }

    public class ReconfigurableImpl implements Reconfigurable {

        public void reconfigure(Properties p) throws Exception {
        }

        public String reconfigure(String what, String value) throws Exception {
            return "";
        }
    }

}
