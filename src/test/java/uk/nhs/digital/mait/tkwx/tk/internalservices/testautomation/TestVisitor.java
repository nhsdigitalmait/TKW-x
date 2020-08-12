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
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractAutotestParser.getAutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParserBaseVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.DatasourceContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ExtractorContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.InputContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.MessageContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.NamedPropertySetContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailsContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ScheduleContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TemplateContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.TestsContext;

/**
 * parses a given file and allows us to access parser context objects typically
 * the first instance of each is captured
 *
 * @author simonfarrow
 */
public class TestVisitor extends AutotestParserBaseVisitor {

    private PassfailContext passfailCtx = null;
    private InputContext start;
    private DatasourceContext datasourceCtx = null;
    private TestContext functionTestCtx = null;
    private ExtractorContext extractorCtx = null;
    private TestsContext testsCtx = null;
    private PassfailsContext passfailsCtx = null;
    private ScheduleContext scheduleCtx = null;
    private TemplateContext templateCtx;
    private NamedPropertySetContext namedPropertySetCtx;
    private MessageContext messageCtx;

    /**
     * public constructor defaults to src/test/resources/test.tst
     * @throws java.io.IOException
     */
    public TestVisitor() throws IOException {
        this("src/test/resources/test.tst");
    }

    /**
     * public constructor taking a file name
     *
     * @param fileName
     * @throws java.io.IOException
     */
    public TestVisitor(String fileName) throws IOException {
        // Pass the tokens to the parser
        AutotestParser parser = getAutotestParser(fileName);

        start = parser.input();

        init();
    }

    private void init() {
        visit(start);
    }

    @Override
    public Object visitPassfail(PassfailContext ctx) {
        if (passfailCtx == null) {
            passfailCtx = ctx;
        }
        return super.visitPassfail(ctx);
    }

    @Override
    public Object visitDatasource(DatasourceContext ctx) {
        if (getDatasourceContext() == null) {
            datasourceCtx = ctx;
        }
        return super.visitDatasource(ctx);
    }

    @Override
    public Object visitTest(TestContext ctx) {
        if (ctx.FUNCTION() != null) {
            if (functionTestCtx == null) {
                functionTestCtx = ctx;
            }
        }
        return super.visitTest(ctx);
    }

    @Override
    public Object visitSchedule(AutotestParser.ScheduleContext ctx) {
        if (scheduleCtx == null) {
            scheduleCtx = ctx;
        }
        return super.visitSchedule(ctx);
    }

    @Override
    public Object visitTemplate(AutotestParser.TemplateContext ctx) {
        if (getTemplateContext() == null) {
            templateCtx = ctx;
        }
        return super.visitTemplate(ctx);
    }

    @Override
    public Object visitTests(AutotestParser.TestsContext ctx) {
        testsCtx = ctx;
        return super.visitTests(ctx);
    }

    @Override
    public Object visitExtractor(ExtractorContext ctx) {
        if (getExtractorContext() == null) {
            extractorCtx = ctx;
        }
        return super.visitExtractor(ctx);
    }

    @Override
    public Object visitPassfails(AutotestParser.PassfailsContext ctx) {
        passfailsCtx = ctx;
        return super.visitPassfails(ctx);
    }

    @Override
    public Object visitNamedPropertySet(AutotestParser.NamedPropertySetContext ctx) {
        if (namedPropertySetCtx == null) {
            namedPropertySetCtx = ctx;
        }
        return super.visitNamedPropertySet(ctx);
    }

    @Override
    public Object visitMessage(AutotestParser.MessageContext ctx) {
        if (getMessageContext() == null) {
            messageCtx = ctx;
        }
        return super.visitMessage(ctx);
    }

    public PassfailContext getPassfailContext() {
        return passfailCtx;
    }

    /**
     * @return the datasourceCtx
     */
    public DatasourceContext getDatasourceContext() {
        return datasourceCtx;
    }

    public TestContext getFunctionTestContext() {
        return functionTestCtx;
    }

    /**
     * @return the extractorCtx
     */
    public ExtractorContext getExtractorContext() {
        return extractorCtx;
    }

    /**
     * @return the testsCtx
     */
    public TestsContext getTestsContext() {
        return testsCtx;
    }

    /**
     * @return the passfailsCtx
     */
    public PassfailsContext getPassfailsContext() {
        return passfailsCtx;
    }

    /**
     *
     * @return
     */
    public ScheduleContext getScheduleContext() {
        return scheduleCtx;
    }

    /**
     * @return the templateCtx
     */
    public TemplateContext getTemplateContext() {
        return templateCtx;
    }

    /**
     * @return the namedPropertySetCtx
     */
    public AutotestParser.NamedPropertySetContext getNamedPropertySetContext() {
        return namedPropertySetCtx;
    }

    /**
     * @return the messageCtx
     */
    public MessageContext getMessageContext() {
        return messageCtx;
    }
}
