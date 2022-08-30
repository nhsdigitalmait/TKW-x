/*
 Copyright 2014  Damian Murphy murff@warlock.org

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScriptParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.BracketedPassfailContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.streamToByteArray;

/**
 * abstract base class for logical operator PassFailCheck classes which
 * logically join the results of subtests.<BR> Provides cloning of the request
 * and response input streams so that more than one test can be run on a message
 * pair
 *
 * @author sifa2
 */
public abstract class AbstractLogicalOperatorPassFailCheck extends AbstractSynchronousPassFailCheck {

    protected PassFailCheck[] subTests = null;
    protected String description = "";
    protected PassfailContext passfailCtx = null;
    protected TestResult testResult = null;


    @Override
    public void init(PassfailContext passfailCtx) throws Exception {
        super.init(passfailCtx);
        // remember the contents for delayed creation of subTests
        this.passfailCtx = passfailCtx;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param p scriptparser
     */
    @Override
    public void link(ScriptParser p) {
        // delayed initialisation since we need the script parser to create the subTests
        // we can have multiple predicates for the or and and logical operators
        try {
            // first two params are <testname> <testtype> then follows ( <args1>+ ) ( <args2>* ) etc
            // the rest are all bracketed subTests
            PassFailCheckContext passFailCheckCtx = passfailCtx.passFailCheck();
            if (passFailCheckCtx.bracketedPassfail() != null ) {
                List<BracketedPassfailContext> bracketedPassFailList = passFailCheckCtx.bracketedPassfail();
                subTests = new PassFailCheck[bracketedPassFailList.size()];
                for (int i = 0; i < bracketedPassFailList.size(); i++) {
                    // use the script parser to construct the appropriate test objects
                    BracketedPassfailContext bpfc = bracketedPassFailList.get(i);
                    PassFailCheckContext subPassFailCheckCtx = bpfc.passFailCheck();
                    subTests[i] = p.makePassFail(subPassFailCheckCtx);
                }
            }
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE,AbstractLogicalOperatorPassFailCheck.class.getName(), "Link failed "+ex.getMessage());
        }
    }

    /**
     * copies the input streams to byte arrays and runs 1 or more tests the
     * results of which which are logically joined depending on test type
     *
     * @param s script
     * @param inResponse response input stream
     * @param inRequest request input stream
     * @return test result
     * @throws Exception
     */
    @Override
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest) throws Exception {
        return processSubTests(s, streamToByteArray(inResponse), streamToByteArray(inRequest));
    }

    /**
     * Clones input streams from the byte arrays and runs the test on the cloned
     * streams
     *
     * @param pfc Passfail check to run
     * @param s ScriptParser
     * @param copiedInResponse byte array containing the response message
     * @param copiedInRequest byte array containing the request message
     * @return TestResult
     * @throws Exception
     */
    protected TestResult copyStreamsRunTest(PassFailCheck pfc, Script s, byte[] copiedInResponse, byte[] copiedInRequest) throws Exception {
        return pfc.passed(s, new ByteArrayInputStream(copiedInResponse), copiedInRequest != null ? new ByteArrayInputStream(copiedInRequest) : null);
    }

    /**
     * Provide an implementation of this to perform the subTests and to make the
     * required conjunction of the test results (and, or etc).
     *
     * @param s The containing script
     * @param inResponse byte array containing the response message which is
     * then cloned to a fresh inputStream
     * @param inRequest byte array containing the request message which is then
     * cloned to a fresh inputStream
     * @return TestResult
     * @throws Exception
     */
    abstract protected TestResult processSubTests(Script s, byte[] inResponse, byte[] inRequest) throws Exception;
}
