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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractorTest;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AsynchronousResponseBodyExtractorTest;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.RequestBodyExtractorTest;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SecondAsynchronousResponseBodyExtractorTest;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractorTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassfailchecksSuite;

/**
 *
 * @author sifa2
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DataSourceTest.class, BasicMessageIdCorrelatorTest.class, ScriptParserTest.class, AutotestFileSaveDistributionEnvelopeHandlerTest.class, PassfailchecksSuite.class, ReportItemTest.class, TestTest.class, SingleRecordXpathResponseExtractorTest.class, TestResultTest.class, AsynchronousLogCorrelatorTest.class, AbstractUpdatableOrderedPersistentFileDataSourceTest.class, CircularUpdatableOrderedPersistentFileDataSourceTest.class, ScriptTest.class, OnceThroughUpdatableOrderedPersistentFileDataSourceTest.class, ScheduleElementTest.class, SynchronousResponseBodyExtractorTest.class, LinkableTest.class, TemplateTest.class, BasicSoapMessageIdCorrelatorTest.class, AsynchronousResponseBodyExtractorTest.class, RequestBodyExtractorTest.class, ScheduleTest.class, AbstractBodyExtractorTest.class, TestFunctionTest.class, BasicTrackingIdCorrelatorTest.class, AbstractPassFailCheckTest.class, DelayFunctionTest.class, NamedPropertySetTest.class, ResponseExtractorTest.class, ReportWriterTest.class, AutotestSessionCaptorTest.class, SecondAsynchronousResponseBodyExtractorTest.class, MessageTest.class, NamedPropertySetDirectiveTest.class})
public class TestautomationSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
