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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 *
 * @author simonfarrow
 */
public class SynchronousXPathCorrelatorPassFailCheckTest {
    
    private static AutotestParser.PassfailContext passfailCtx;
    private SynchronousXPathCorrelatorPassFailCheck instance;

    
    public SynchronousXPathCorrelatorPassFailCheckTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        AutotestParser.PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (AutotestParser.PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("synchronousxpathcorrelation")) {
                passfailCtx = lpassfailCtx;
                break;
            }
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new SynchronousXPathCorrelatorPassFailCheck();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class SynchronousXPathCorrelatorPassFailCheck.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of doChecks method, of class SynchronousXPathCorrelatorPassFailCheck.
     */
    @Test
    public void testDoChecks() throws Exception {
        System.out.println("doChecks");
        instance.init(passfailCtx);
        Script s = null;
        InputSource request = new InputSource(new ByteArrayInputStream("<el>xxx</el>".getBytes()));
        InputSource response = new InputSource(new ByteArrayInputStream("<el>xxx</el>".getBytes()));
        boolean expResult = true;
        boolean result = instance.doChecks(s, request, response);
        assertEquals(expResult, result);

        request = new InputSource(new ByteArrayInputStream("<el>xxx</el>".getBytes()));
        response = new InputSource(new ByteArrayInputStream("<el>yyy</el>".getBytes()));
        expResult = false;
        result = instance.doChecks(s, request, response);
        assertEquals(expResult, result);
    }
    
}
