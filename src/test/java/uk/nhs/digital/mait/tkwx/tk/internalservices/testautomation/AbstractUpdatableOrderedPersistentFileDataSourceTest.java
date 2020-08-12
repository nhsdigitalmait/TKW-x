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
import java.util.ArrayList;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.DataSourceTest.FIRST_ID;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;

/**
 *
 * @author sifa2
 */
public class AbstractUpdatableOrderedPersistentFileDataSourceTest {

    private static AutotestParser.DatasourceContext datasourceCtx;
    private static final String BACKUP_FILE = "src/test/resources/test.tdv.backup";

    private AbstractUpdatableOrderedPersistentFileDataSource instance;

    public AbstractUpdatableOrderedPersistentFileDataSourceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        // copy a tdv file which is going to get trashed
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        datasourceCtx = visitor.getDatasourceContext();
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
    public void setUp() {
        new File(BACKUP_FILE).delete();
        instance = new CircularUpdatableOrderedPersistentFileDataSource();
        instance.init(datasourceCtx);
    }

    @After
    public void tearDown() throws Exception {
        instance.close();
    }

    /**
     * Test of init method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        // already done in the setup
    }

    /**
     * Test of close method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        instance.close();
        assertTrue(fileExists(BACKUP_FILE));
    }

    /**
     * Test of getName method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "patients";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of link method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLink() throws Exception {
        System.out.println("link");
        ScriptParser p = null;
        instance.link(p);
    }

    /**
     * Test of getTags method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testGetTags() {
        System.out.println("getTags");
        String expResult = "__ID__";
        Iterable<String> result = instance.getTags();
        Iterator<String> tagIterator = result.iterator();
        assertEquals(expResult, tagIterator.next());
    }

    /**
     * Test of hasId method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testHasId() {
        System.out.println("hasId");
        String id = FIRST_ID;
        boolean expResult = true;
        boolean result = instance.hasId(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of hasValue method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testHasValue() {
        System.out.println("hasValue");
        String id = FIRST_ID;
        String tag = "__TITLE__";
        boolean expResult = true;
        boolean result = instance.hasValue(id, tag);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String id = FIRST_ID;
        String tag = "__TITLE__";
        String expResult = "Mrs";
        String result = instance.getValue(id, tag);
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetValue() throws Exception {
        System.out.println("setValue");
        String id = FIRST_ID;
        String tag = "__TITLE__";
        String value = "MRS";
        instance.setValue(id, tag, value);
        assertEquals(value, instance.getValue(id, tag));

        instance.setValue(id, tag, "Mrs");
        assertEquals("Mrs", instance.getValue(id, tag));
    }

    /**
     * Test of isReadOnly method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testIsReadOnly() {
        System.out.println("isReadOnly");
        boolean expResult = false;
        boolean result = instance.isReadOnly();
        assertEquals(expResult, result);
    }

    /**
     * Test of finalize method, of class
     * AbstractUpdatableOrderedPersistentFileDataSource.
     * @throws java.lang.Exception
     */
    @Test
    public void testFinalize() throws Exception, Throwable {
        System.out.println("finalize");
        instance.finalize();
    }

    public class AbstractUpdatableOrderedPersistentFileDataSourceImpl extends AbstractUpdatableOrderedPersistentFileDataSource {

        @Override
        public String getNextId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * Test of getRecordids method, of class AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testGetRecordids() {
        System.out.println("getRecordids");
        int expResult = 66;
        ArrayList<String> result = (ArrayList<String> )instance.getRecordids();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of setExtractorName method, of class AbstractUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testSetExtractorName() {
        System.out.println("setExtractorName");
        String extractorName = "en";
        instance.setExtractorName(extractorName);
    }
}
