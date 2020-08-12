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
package uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners;

import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Customises default ANTLR error reporting
 * @author sifa2
 */
public class VerboseErrorListener extends BaseErrorListener {

    private final String fileName;
    
    /**
     * public constructor no source file
     */
    public VerboseErrorListener() {
        this(null);
    }

    /**
     * overloaded public constructor taking the source file
     * @param fileName 
     */
    public VerboseErrorListener(String fileName) {
        super();
        this.fileName = fileName;
    }

    /**
     *
     * @param rcgnzr
     * @param offendingSymbol
     * @param line
     * @param charPositionInLine
     * @param msg
     * @param re
     */
    @Override
    public void syntaxError(Recognizer<?, ?> rcgnzr, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException re) {
       // super.syntaxError(rcgnzr, offendingSymbol, line, charPositionInLine, msg, re);
        if (fileName != null ) {
            System.err.println("Source file "+fileName);
        }
        List<String> stack = ((Parser) rcgnzr).getRuleInvocationStack();
        Collections.reverse(stack);
        System.err.println("rule stack: " + stack);
        System.err.println("line " + line + ":" + charPositionInLine + " at "
                + offendingSymbol + ": " + msg);
   }
    
}
