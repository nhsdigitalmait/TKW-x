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

import java.util.logging.Level;
import static java.util.logging.Level.WARNING;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Interval;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Customises default ANTLR error reporting reports errors to stderr and returns
 * an overall count of errors
 *
 * @author sifa2
 */
public class ErrorCountingErrorListener extends BaseErrorListener {

    /**
     * error count
     */
    private int errors = 0;

    /**
     * overridden handler for error events called by the framework when an error
     * is detected
     *
     * @param recognizer Recogniser object
     * @param offendingSymbol object
     * @param line integer
     * @param charPositionInLine integer
     * @param msg String
     * @param recognitionException RecognitionException
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException recognitionException) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, recognitionException);
        boolean logged = false;

        CommonToken commonToken = (CommonToken) offendingSymbol;
        if (commonToken != null) {
            // recover the source script so we can get the offending line
            CharStream charStream = commonToken.getInputStream();
            String[] scriptArray = charStream.getText(new Interval(0, charStream.size() - 1)).split("\r\n");
            String ruler = new String(new char[charPositionInLine]).replace("\\0", " ") + "^";

            // output the full line from the source with a ruler pointing at the char pos where the problem is
            if (line <= scriptArray.length) {
                Logger.getInstance().log(WARNING, ErrorCountingErrorListener.class.getName(), String.format("Line %d:%d %s\r\n%s\r\n%s",
                        line, charPositionInLine, msg, scriptArray[line - 1].replaceAll("\t", " "), ruler));
                logged = true;
            }
        }
        if (!logged) {
            Logger.getInstance().log(WARNING, ErrorCountingErrorListener.class.getName(), String.format("Line %d:%d %s", line, charPositionInLine, msg));
        }
        errors++;
    }

    /**
     *
     * @return integer count of errors detected parsing the tst file
     */
    public int getErrorCount() {
        return errors;
    }
}
