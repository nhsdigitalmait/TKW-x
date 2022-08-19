/*

 Copyright 2014 Health and Social Care Information Centre
 Solution Assurance damian.murphy@hscic.gov.uk

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

import java.io.File;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks.PassFailCheck;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class AutotestGrammarCompilerVisitorTest {

    private AutotestGrammarCompilerVisitor instance;

    public AutotestGrammarCompilerVisitorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        for (String filename : new String[]{"src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        Files.copy(FileSystems.getDefault().getPath(System.getenv("TKWROOT") + "/contrib/TKWAutotestManager/tstp", "patients.tdv"),
                FileSystems.getDefault().getPath("src/test/resources", "test.tdv"), REPLACE_EXISTING);

        Script script = new Script();
        Properties bootProperties = new Properties();
        bootProperties.load(new FileReader(System.getenv("TKWROOT") + "/config/ITK_Autotest/tkw-x.properties"));

        // get the parser initialised with the correct file
        AutotestParser parser = AbstractAutotestParser.getAutotestParser("src/test/resources/test.tst");

        ParseTree pt = parser.input();
        instance = new AutotestGrammarCompilerVisitor(script, bootProperties);

        Object x = instance.visit(pt);
    }

    @After
    public void tearDown() {
    }

    // Not "real" overrides but this stops them being regenerated
    /**
     * Test of visitScript method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitScript() {
    }

    /**
     * Test of visitValidator method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitValidator() {
    }

    /**
     * Test of visitSimulator method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitSimulator() {
    }

    /**
     * Test of visitStop_when_complete method, of class
     * AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitStop_when_complete() {
    }

    /**
     * Test of visitSchedules method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitSchedules() {
    }

    /**
     * Test of visitTests method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitTests() {
    }

    /**
     * Test of visitMessages method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitMessages() {
    }

    /**
     * Test of visitTemplates method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitTemplates() {
    }

    /**
     * Test of visitPropertysets method, of class
     * AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitPropertysets() {
    }

    /**
     * Test of visitDatasources method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitDatasources() {
    }

    /**
     * Test of visitExtractors method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitExtractors() {
    }

    /**
     * Test of visitPassfails method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitPassfails() {
    }

    /**
     * Test of visitNamedHttpHeaderSet method, of class
     * AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitNamedHttpHeaderSet() {
    }

        /**
     * Test of visitSchedule method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitSchedule() {
        System.out.println("visitSchedule");
    }

    /**
     * Test of visitTest method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitTest() {
        System.out.println("visitTest");
    }

    /**
     * Test of visitMessage method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitMessage() {
        System.out.println("visitMessage");
    }

    /**
     * Test of visitTemplate method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitTemplate() {
        System.out.println("visitTemplate");
    }

    /**
     * Test of visitNamedPropertySet method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitNamedPropertySet() {
        System.out.println("visitNamedPropertySet");
    }

    /**
     * Test of visitExtractor method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitExtractor() {
        System.out.println("visitExtractor");
    }

    /**
     * Test of visitPassfail method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitPassfail() {
        System.out.println("visitPassfail");
    }

    /**
     * Test of visitInclude_statement method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitInclude_statement() {
        System.out.println("visitInclude_statement");
    }

    /**
     * Test of visitSubstitution_tag method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testVisitSubstitution_tag() {
        System.out.println("visitSubstitution_tag");
    }
    
    /**
     * Test of getDatasources method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetDatasources() {
        System.out.println("getDatasources");
        int expResult = 1;
        HashMap<String, DataSource> result = instance.getDatasources();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getMessages method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetMessages() {
        System.out.println("getMessages");
        int expResult = 2;
        HashMap<String, Message> result = instance.getMessages();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getPassfailchecks method, of class
     * AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetPassfailchecks() {
        System.out.println("getPassfailchecks");
        int expResult = 23;
        HashMap<String, PassFailCheck> result = instance.getPassfailchecks();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getExtractors method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetExtractors() {
        System.out.println("getExtractors");
        int expResult = 1;
        HashMap<String, ResponseExtractor> result = instance.getExtractors();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getTemplates method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetTemplates() {
        System.out.println("getTemplates");
        int expResult = 2;
        HashMap<String, Template> result = instance.getTemplates();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getTests method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetTests() {
        System.out.println("getTests");
        int expResult = 10;
        HashMap<String, uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test> result = instance.getTests();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getPropertySets method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetPropertySets() {
        System.out.println("getPropertySets");
        int expResult = 8;
        HashMap<String, NamedPropertySet> result = instance.getPropertySets();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getSchedules method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetSchedules() {
        System.out.println("getSchedules");
        int expResult = 1;
        ArrayList<Schedule> result = instance.getSchedules();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getHttpHeaderSets method, of class
     * AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetHttpHeaderSets() {
        System.out.println("getHttpHeaderSets");
        int expResult = 1;
        HashMap<String, HashMap<String, Object>> result = instance.getHttpHeaderSets();
        assertEquals(expResult, result.size());
    }

    @Test
    public void testWalkTstScript() {
        System.out.println("Schedules");
        for (Schedule schedule : instance.getSchedules()) {
            System.out.println("\t" + schedule.getName());
        }

        System.out.println("Tests");
        Iterator<String> iter = instance.getTests().keySet().iterator();
        while (iter.hasNext()) {
            uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test test = instance.getTests().get(iter.next());
            System.out.println("\t" + test.getName());
        }

        System.out.println("Messages");
        iter = instance.getMessages().keySet().iterator();
        while (iter.hasNext()) {
            uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Message message = instance.getMessages().get(iter.next());
            System.out.println("\t" + message.getName());
        }

        System.out.println("Templates");
        iter = instance.getTemplates().keySet().iterator();
        while (iter.hasNext()) {
            uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Template template = instance.getTemplates().get(iter.next());
            System.out.println("\t" + template.getName());
        }

        System.out.println("Datasources");
        iter = instance.getDatasources().keySet().iterator();
        while (iter.hasNext()) {
            uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.DataSource dataSource = instance.getDatasources().get(iter.next());
            System.out.println("\t" + dataSource.getName());
        }

        System.out.println("Passfailchecks");
        iter = instance.getPassfailchecks().keySet().iterator();
        while (iter.hasNext()) {
            PassFailCheck pfc = instance.getPassfailchecks().get(iter.next());
            System.out.println("\t" + pfc.getName());
        }

        System.out.println("Extractors");
        iter = instance.getExtractors().keySet().iterator();
        while (iter.hasNext()) {
            ResponseExtractor extractor = instance.getExtractors().get(iter.next());
            System.out.println("\t" + extractor.getName());
        }

        System.out.println("PropertySets");
        iter = instance.getPropertySets().keySet().iterator();
        while (iter.hasNext()) {
            NamedPropertySet ps = instance.getPropertySets().get(iter.next());
            System.out.println("\t" + ps.getName());
        }

        System.out.println("HttpHeaderSets");
        iter = instance.getHttpHeaderSets().keySet().iterator();
        while (iter.hasNext()) {
            String hsName = iter.next();
            HashMap<String, Object> hs = instance.getHttpHeaderSets().get(hsName);
            System.out.println("\t" + hsName);
        }
    }


    /**
     * Test of getSubstitutionTags method, of class AutotestGrammarCompilerVisitor.
     */
    @Test
    public void testGetSubstitutionTags() {
        System.out.println("getSubstitutionTags");
        HashMap<String, Object> result = instance.getSubstitutionTags();
        assertNotNull(result);
    }

}
