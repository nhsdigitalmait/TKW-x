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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.security.InvalidParameterException;
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
public class FormattedTimeSubstitutionTest {
    
    private FormattedTimeSubstitution instance;
    
    public FormattedTimeSubstitutionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new FormattedTimeSubstitution();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class FormattedTimeSubstitution.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String o = "ignored";

        String expResult = "";

        String s = "yyyy-MM-dd'T00:00:00'XXX Europe/London";
        instance.setData(s);
        String result = instance.getValue(o);
        System.out.println(result);
        //assertEquals(expResult, result);

        s = "yyyy-MM-dd'T00:00:00'XXX Europe/London 1 1";
        instance.setData(s);
        result = instance.getValue(o);
        System.out.println(result);
        //assertEquals(expResult, result);

        s = "yyyy-MM-dd'T00:00:00'XXX Europe/London 1 1 true";
        instance.setData(s);
        result = instance.getValue(o);
        System.out.println(result);
        //assertEquals(expResult, result);
    }

    /**
     * Test of setData method, of class FormattedTimeSubstitution.
     * @throws java.lang.Exception
     */
    @Test(expected = InvalidParameterException.class)
    public void testSetData() throws Exception {
        System.out.println("setData");
        String s = "yyyy-MM-dd'T00:00:00'XXX Europe/London";
        instance.setData(s);

        s = "yyyy-MM-dd'T00:00:00'XXX Europe/London 1 0 true x";
        instance.setData(s);
    }
    
}
