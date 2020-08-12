/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
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
public class VerboseErrorListenerTest {
    
    public VerboseErrorListenerTest() {
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
     * Test of syntaxError method, of class VerboseErrorListener.
     */
    @Test
    public void testSyntaxError() {
        System.out.println("syntaxError");
        // tested elsewhere
        Recognizer rcgnzr = null;
        Object offendingSymbol = null;
        int line = 0;
        int charPositionInLine = 0;
        String msg = "";
        RecognitionException re = null;
        VerboseErrorListener instance = new VerboseErrorListener();
        //instance.syntaxError(rcgnzr, offendingSymbol, line, charPositionInLine, msg, re);
    }
    
}
