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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 *
 * @author sifa2
 */
public class TestFunctionTest {
    private TestFunctionImpl instance;
    
    public TestFunctionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new TestFunctionImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class TestFunction.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(null);
    }

    /**
     * Test of execute method, of class TestFunction.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        String instanceName = "";
        Schedule s = null;
        uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test t = null;
        boolean expResult = false;
        boolean result = instance.execute(instanceName, s, t);
        assertEquals(expResult, result);
    }

    public class TestFunctionImpl implements TestFunction {

        @Override
        public boolean execute(String instanceName, Schedule s, uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test t) throws Exception {
            return false;
        }

        @Override
        public void init(AutotestParser.TestContext testCtx) throws Exception {
        }
    }
}
