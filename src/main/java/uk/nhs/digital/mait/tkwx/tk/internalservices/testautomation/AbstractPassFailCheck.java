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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.InputStream;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.XPathCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassFailCheck;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;

/**
 * Abstract superclass for checks for test outcome.
 *
 * @author Damian Murphy murff@warlock.org
 */
abstract public class AbstractPassFailCheck
        implements PassFailCheck {

    protected String name = null;
    protected String type = null;
    protected String responseExtractorName = null;
    protected ResponseExtractor extractor = null;

    protected DataSource datasource = null;
    protected String recordid = null;

    private String description = null;

    protected static final String BLACK = "#000000";
    protected static final String GREEN = "#008000";
    protected static final String RED = "#900000";
    protected static final String BLUE = "#3A5FCD";

    /**
     * antlr parser version
     *
     * @param passfailCtx
     * @throws java.lang.Exception
     */
    @Override
    public void init(PassfailContext passfailCtx)
            throws Exception {
        name = passfailCtx.passFailCheckName().getText();
        init(passfailCtx.passFailCheck());
    }

    /**
     * antlr parser default version this may be overridden
     *
     * @throws java.lang.Exception
     */
    @Override
    public void init(PassFailCheckContext passfailCheckCtx)
            throws Exception {
        type = determinePassFailCheckType(passfailCheckCtx);
        XPathCheckContext xPathCheckCtx = passfailCheckCtx.xPathCheck();
        if (xPathCheckCtx != null && xPathCheckCtx.usingExtractor() != null) {
            responseExtractorName = xPathCheckCtx.usingExtractor().extractorName().getText();
        }
    }

    /**
     * This method needs extending when new pass fail check types are added
     * @param passfailCheckCtx
     * @return String containing the type of the PFC
     */
    public static String determinePassFailCheckType(PassFailCheckContext passfailCheckCtx) {
        String lType = null;
        if (passfailCheckCtx.xPathCheck() != null) {
            lType = passfailCheckCtx.xPathCheck().xpathType().getText();
        } else if (passfailCheckCtx.httpHeaderCheck() != null) {
            lType = passfailCheckCtx.httpHeaderCheck().HTTPHEADERCHECK().getText();
        } else if (passfailCheckCtx.httpStatusCheck() != null) {
            lType = passfailCheckCtx.httpStatusCheck().HTTPSTATUSCHECK().getText();
        } else if (passfailCheckCtx.httpHeaderCorrelationCheck() != null) {
            lType = passfailCheckCtx.httpHeaderCorrelationCheck().HTTPHEADERCORRELATIONCHECK().getText();
        } else if (passfailCheckCtx.xpathCorrelationCheck() != null) {
            lType = passfailCheckCtx.xpathCorrelationCheck().xpathCorrelationType().getText();
        } else if ( passfailCheckCtx.nullCheck() != null ) {
            lType = passfailCheckCtx.nullCheck().nullCheckType().getText();
        } else {
            lType = passfailCheckCtx.getChild(0).getText();
        }
        return lType;
    }

    /**
     * Called internally to populate references to other objects via the
     * ScriptParser.
     *
     * @param p Reference to the ScriptParser
     * @throws Exception if an extractor is specified, but not found.
     */
    @Override
    public void link(ScriptParser p)
            throws Exception {
        if (responseExtractorName != null) {
            ResponseExtractor r = p.getExtractor(responseExtractorName);
            if (r == null) {
                throw new Exception("PassFailCheck " + name + " response extractor " + responseExtractorName + " not found");
            }
            extractor = r;
        }
    }
    
    @Override
    public String getExtractorName() {
        return responseExtractorName;
    }

    /**
     *
     * @return The name of this check.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Implemented by concrete sub-classes to provide the checks on test
     * outcome.
     *
     * @param s Reference to the current script
     * @param inResponse InputStream carrying the log file for the test result
     * @param inRequest optional parameter supplying the request log if present
     * @return TestResult reporting whether the test passed
     * @throws Exception
     */
    @Override
    abstract public TestResult passed(Script s, InputStream inResponse, InputStream inRequest) throws Exception;


    /**
     * Get the description of the check as set by the concrete class.
     *
     * @return The description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Call the "extract()" method on an extractor class if there is one.
     *
     * @param s String containing the response body from the test message.
     * @param hm HttpHeaderManager containing response headers
     * @throws Exception If there is an error during the extract.
     */
    protected void doExtract(String s, HttpHeaderManager hm)
            throws Exception {
        if (extractor == null) {
            return;
        }
        extractor.extract(s, hm);
    }

    protected String colourString(String content, String clr) {
        StringBuilder sb = new StringBuilder("<font color=\"");
        sb.append(clr);
        sb.append("\">");
        sb.append(content);
        sb.append("</font>");
        return sb.toString();
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param s append the text to the description
     */
    public void appendDescription(String s) {
        this.description += s;
    }

    protected void extract(InputStream in)
            throws Exception {
        SynchronousResponseBodyExtractor bodyExtractor = new SynchronousResponseBodyExtractor();
        // xml response
        String body = bodyExtractor.getBody(in, true);
        extractor.extract(body, bodyExtractor.getHttpResponseHeaders());
    }

    /**
     * allows data source details to be set at runtime for matchstring tag
     * substitution in xpath tests from tdv files
     *
     * @param datasource
     * @param recordid
     */
    @Override
    public void setDatasourceDetails(DataSource datasource, String recordid) {
        this.datasource = datasource;
        this.recordid = recordid;
    }
}
