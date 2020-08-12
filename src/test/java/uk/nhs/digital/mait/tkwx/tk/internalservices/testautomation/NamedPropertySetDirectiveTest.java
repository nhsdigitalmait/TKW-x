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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.NamedPropertySetDirective.PropertySetOperation;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.NamedPropertySetDirective.PropertySetOperation.SET;

/**
 *
 * @author sifa2
 */
public class NamedPropertySetDirectiveTest {

    private NamedPropertySetDirective instance;

    public NamedPropertySetDirectiveTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new NamedPropertySetDirective(SET, "prop", "value");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of resolveOperation method, of class NamedPropertySetDirective.
     */
    @Test
    public void testResolveOperation() {
        System.out.println("resolveOperation");
        String op = "SET";
        PropertySetOperation expResult = SET;
        PropertySetOperation result = NamedPropertySetDirective.resolveOperation(op);
        assertEquals(expResult, result);
    }

    /**
     * Test of getPropertyName method, of class NamedPropertySetDirective.
     */
    @Test
    public void testGetPropertyName() {
        System.out.println("getPropertyName");
        String expResult = "prop";
        String result = instance.getPropertyName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOp method, of class NamedPropertySetDirective.
     */
    @Test
    public void testGetOp() {
        System.out.println("getOp");
        PropertySetOperation expResult = SET;
        PropertySetOperation result = instance.getOp();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class NamedPropertySetDirective.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String expResult = "value";
        String result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class NamedPropertySetDirective.
     * using a java function string
     * These have to be static taking no parameters and returning String
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFunctionValue() throws Exception {
        System.out.println("getFunctionValue");
        String expResult = System.getProperty("user.dir");
        instance = new NamedPropertySetDirective(SET, "funcprop", "function:uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.getCWD");
        String result = instance.getValue();
        assertEquals(expResult, result);
    }

}
