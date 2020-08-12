/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author simonfarrow
 */
public class ExpressionValueTest {
    
    public ExpressionValueTest() {
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
     * Test of getValue method, of class ExpressionValue.
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String[] arg0 = null;
        String[] arg1 = null;
        ExpressionValue instance = new ExpressionValueImpl();
        boolean expResult = false;
        boolean result = instance.getValue(arg0, arg1);
        assertEquals(expResult, result);
    }

    public class ExpressionValueImpl implements ExpressionValue {

        public boolean getValue(String[] arg0, String[] arg1) throws Exception {
            return false;
        }
    }
    
}
