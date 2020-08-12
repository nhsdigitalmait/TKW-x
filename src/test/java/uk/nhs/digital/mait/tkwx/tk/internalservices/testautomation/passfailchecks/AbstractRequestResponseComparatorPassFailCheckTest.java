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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import net.sourceforge.htmlunit.corejs.javascript.Script;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 *
 * @author simonfarrow
 */
public class AbstractRequestResponseComparatorPassFailCheckTest {

    private  AbstractRequestResponseComparatorPassFailCheck instance;
 
    public AbstractRequestResponseComparatorPassFailCheckTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        instance = new AbstractRequestResponseComparatorPassFailCheckImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of passed method, of class AbstractRequestResponseComparatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testPassed() throws Exception {
        System.out.println("passed");
    }

    /**
     * Test of doChecks method, of class AbstractRequestResponseComparatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
    }

    /**
     * Test of getRequestBody method, of class AbstractRequestResponseComparatorPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetRequestBody() throws Exception {
        System.out.println("getRequestBody");
    }

    public class AbstractRequestResponseComparatorPassFailCheckImpl extends AbstractRequestResponseComparatorPassFailCheck {

        @Override
        protected boolean doChecks(uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script arg0, InputSource arg1, InputSource arg2) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
