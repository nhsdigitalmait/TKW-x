/*
 Copyright 2015  Simon Farrow <simon.farrow1@hscic.gov.uk>

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

import java.io.IOException;
import java.util.logging.Level;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestLexer;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.commonutils.util.Logger;

abstract public class AbstractAutotestParser {

    /**
     * overload without error listener
     *
     * @param fileName
     */
    public void parseFile(String fileName) {
        parseFile(fileName, null);
    }

    /**
     * set the errorlistener then parse the file
     *
     * @param fileName
     * @param errorListener
     */
    public void parseFile(String fileName, BaseErrorListener errorListener) {
        try {
            AutotestParser parser = getAutotestParser(fileName);

            // how to specify an alternate error handler
            if (errorListener != null) {
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);
            }

            // Specify our entry point
            AutotestParser.InputContext inputContext = parser.input();

            // Walk it and attach our listener
            ParseTreeWalker walker = new ParseTreeWalker();
            AutotestListener listener = new AutotestListener(fileName, parser);
            walker.walk(listener, inputContext);
            listener.postParseAnalyse();

        } catch (IOException ex) {
            Logger.getInstance().log(Level.SEVERE, "parseFile", "IO Error " + ex.getMessage() + " reading test script file " + fileName);
        }
    }

    /**
     * helper function
     * @param fileName tst filename to parse
     * @return populated parser object
     * @throws IOException
     */
    public static AutotestParser getAutotestParser(String fileName) throws IOException {
        AutotestLexer lexer
                = new AutotestLexer(new ANTLRFileStream(fileName));
        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Pass the tokens to the parser
        return new AutotestParser(tokens);
    }
}
