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

import static com.helger.commons.mock.CommonsAssert.fail;
import java.io.IOException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractAutotestParser.getAutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 * All but one of these are call backs so no test required
 *
 * @author simonfarrow
 */
public class AutotestListenerTest {

    private AutotestListener instance;
    private AutotestParser ap;

    public AutotestListenerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        String filename = "src/test/resources/test.tst";
        ap = getAutotestParser(filename);
        instance = new AutotestListener(filename,ap);
    }

    @After
    public void tearDown() {
    }

   
    /**
     * Test of postParseAnalyse method, of class AutotestListener.
     */
    @Test
    public void testPostParseAnalyse() {
        System.out.println("postParseAnalyse");
        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        AutotestParser.InputContext inputContext = ap.input();
        walker.walk(instance, inputContext);

        // do the test
        instance.postParseAnalyse();
    }
    
    
    // These are all here just to stop the wizard from recreating them they are callbacks not tests    
    /**
     * Test of enterScriptName method, of class AutotestListener.
     */
    @Test
    public void testEnterScriptName() {
        //System.out.println("enterScriptName");
    }

    /**
     * Test of enterStop_when_complete method, of class AutotestListener.
     */
    @Test
    public void testEnterStop_when_complete() {
        //System.out.println("enterStop_when_complete");
    }

    /**
     * Test of enterValidator method, of class AutotestListener.
     */
    @Test
    public void testEnterValidator() {
        //System.out.println("enterValidator");
    }

    /**
     * Test of enterSimulator method, of class AutotestListener.
     */
    @Test
    public void testEnterSimulator() {
        //System.out.println("enterSimulator");
    }

    /**
     * Test of enterMessages method, of class AutotestListener.
     */
    @Test
    public void testEnterMessages() {
        //System.out.println("enterMessages");
    }

    /**
     * Test of enterTemplates method, of class AutotestListener.
     */
    @Test
    public void testEnterTemplates() {
        //System.out.println("enterTemplates");
    }

    /**
     * Test of enterDatasources method, of class AutotestListener.
     */
    @Test
    public void testEnterDatasources() {
        //System.out.println("enterDatasources");
    }

    /**
     * Test of enterExtractors method, of class AutotestListener.
     */
    @Test
    public void testEnterExtractors() {
        //System.out.println("enterExtractors");
    }

    /**
     * Test of enterSchedules method, of class AutotestListener.
     */
    @Test
    public void testEnterSchedules() {
        //System.out.println("enterSchedules");
    }

    /**
     * Test of enterTests method, of class AutotestListener.
     */
    @Test
    public void testEnterTests() {
        //System.out.println("enterTests");
    }

    /**
     * Test of enterPropertysets method, of class AutotestListener.
     */
    @Test
    public void testEnterPropertysets() {
        //System.out.println("enterPropertysets");
    }

    /**
     * Test of enterPassfails method, of class AutotestListener.
     */
    @Test
    public void testEnterPassfails() {
        //System.out.println("enterPassfails");
    }

    /**
     * Test of enterSchedule method, of class AutotestListener.
     */
    @Test
    public void testEnterSchedule() {
        //System.out.println("enterSchedule");
    }

    /**
     * Test of enterTest method, of class AutotestListener.
     */
    @Test
    public void testEnterTest() {
        //System.out.println("enterTest");
    }

    /**
     * Test of enterMessage method, of class AutotestListener.
     */
    @Test
    public void testEnterMessage() {
        //System.out.println("enterMessage");
    }

    /**
     * Test of enterTemplate method, of class AutotestListener.
     */
    @Test
    public void testEnterTemplate() {
        //System.out.println("enterTemplate");
    }

    /**
     * Test of enterNamedPropertySet method, of class AutotestListener.
     */
    @Test
    public void testEnterNamedPropertySet() {
        //System.out.println("enterNamedPropertySet");
    }

    /**
     * Test of enterDatasource method, of class AutotestListener.
     */
    @Test
    public void testEnterDatasource() {
        //System.out.println("enterDatasource");
    }

    /**
     * Test of enterPassfail method, of class AutotestListener.
     */
    @Test
    public void testEnterPassfail() {
        //System.out.println("enterPassfail");
    }

    /**
     * Test of enterExtractor method, of class AutotestListener.
     */
    @Test
    public void testEnterExtractor() {
        //System.out.println("enterExtractor");
    }

    /**
     * Test of enterInclude_statement method, of class AutotestListener.
     */
    @Test
    public void testEnterInclude_statement() {
        //System.out.println("enterInclude_statement");
    }
    /**
     * Test of enterScript method, of class AutotestListener.
     */
    @Test
    public void testEnterScript() {
        System.out.println("enterScript");
    }

    /**
     * Test of enterSubstitution_tags method, of class AutotestListener.
     */
    @Test
    public void testEnterSubstitution_tags() {
        System.out.println("enterSubstitution_tags");
    }

    /**
     * Test of enterNamedHttpHeaderSet method, of class AutotestListener.
     */
    @Test
    public void testEnterNamedHttpHeaderSet() {
        System.out.println("enterNamedHttpHeaderSet");
    }

    /**
     * Test of enterSubstitution_tag method, of class AutotestListener.
     */
    @Test
    public void testEnterSubstitution_tag() {
        System.out.println("enterSubstitution_tag");
    }
    
    // end of callbacks

}
