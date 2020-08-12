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

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SEND_CDA_V2_SERVICE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;

/**
 *
 * @author sifa2
 */
public class TemplateTest {

    private static AutotestParser.TemplateContext templateCtx;
    private Template instance;

    public TemplateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        templateCtx = visitor.getTemplateContext();
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        for (String filename : new String[]{"src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        instance = new Template(templateCtx);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class Template.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "DE_EMPPID_"+SEND_CDA_V2_SERVICE+"_template";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of makeMessage method, of class Template.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeMessage() throws Exception {
        System.out.println("makeMessage");
        String recordid = "id";
        DataSource d = new CircularUpdatableOrderedPersistentFileDataSource();
        String result = instance.makeMessage(recordid, d);
        assertTrue(result.length() > 0);
        assertTrue(result.startsWith("<?xml"));
    }

    /**
     * Test of substitute method, of class Template.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute() throws Exception {
        System.out.println("substitute");
        StringBuilder sb = new StringBuilder("aaa __XXX__ bbb");
        String tag = "__XXX__";
        String value = "ccc";
        substitute(sb, tag, value);
        String expResult = "aaa ccc bbb";
        assertEquals(expResult, sb.toString());
    }

    /**
     * Test of resolveDataValue method, of class Template.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testResolveDataValue() throws Exception {
        System.out.println("resolveDataValue");

        String v = "__HL7_DATE__";
        String expResult = "^[0-9]{14}$";
        String result = Template.resolveDataValue(v);
        assertTrue(result.matches(expResult));

        v = "__ISO_8601_DATE__";
        expResult = "^[0-9]{4}-[0-9]{2}-[0-9]{2}.[0-9]{2}:[0-9]{2}:[0-9]{2}.$";
        result = Template.resolveDataValue(v);
        assertTrue(result.matches(expResult));

        v = "__UCASE_UUID__";
        expResult = "^[0-9A-Z]{8}-[0-9A-Z]{4}-[0-9A-Z]{4}-[0-9A-Z]{4}-[0-9A-Z]{12}$";
        result = Template.resolveDataValue(v);
        assertTrue(result.matches(expResult));

        v = "__LCASE_UUID__";
        expResult = "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$";
        result = Template.resolveDataValue(v);
        assertTrue(result.matches(expResult));

        v = "__SYSTEM_PROPERTY__:user.dir";
        expResult = System.getProperty("user.dir");
        result = Template.resolveDataValue(v);
        assertEquals(expResult, result);
    }

}
