/**
 * Copyright 2012-13 Simon Farrow <simon.farrow1@nhs.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * @author sifa2
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners;

import org.antlr.v4.runtime.CommonToken;
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
public class ErrorCountingErrorListenerTest {

    private ErrorCountingErrorListener instance;
    
    public ErrorCountingErrorListenerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
          instance = new ErrorCountingErrorListener();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of syntaxError method, of class ErrorCountingErrorListener.
     */
    @Test
    public void testSyntaxError() {
        System.out.println("syntaxError");
          // tested via  ErrorCountingAutotestParser
        Recognizer recognizer = null;
        Object offendingSymbol = new CommonToken(0,"");
        int line = 0;
        int charPositionInLine = 0;
        String msg = "";
        RecognitionException recognitionException = null;
        //instance.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, recognitionException);
    }

    /**
     * Test of getErrorCount method, of class ErrorCountingErrorListener.
     */
    @Test
    public void testGetErrorCount() {
        System.out.println("getErrorCount");
        int expResult = 0;
        int result = instance.getErrorCount();
        assertEquals(expResult, result);
    }
    
}
