/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class SynchronousRequestResponseXPathCorrelatorPassFailCheckTest {

    private static AutotestParser.PassfailContext passfailCtx;
    private SynchronousRequestResponseXPathCorrelatorPassFailCheck instance;
    
    public SynchronousRequestResponseXPathCorrelatorPassFailCheckTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        TestVisitor visitor = new TestVisitor();
        AutotestParser.PassfailsContext passfailsCtx = visitor.getPassfailsContext();
        for (AutotestParser.PassfailContext lpassfailCtx : passfailsCtx.passfail()) {
            if (lpassfailCtx.passFailCheckName().getText().equals("xpathcorrelation")) {
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
        instance = new SynchronousRequestResponseXPathCorrelatorPassFailCheck();
    }
    
    @After
    public void tearDown() {
    }
    

    /**
     * Test of init method, of class SynchronousRequestResponseXPathCorrelatorPassFailCheck.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(passfailCtx);
    }

    /**
     * Test of doChecks method, of class SynchronousRequestResponseXPathCorrelatorPassFailCheck.
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
