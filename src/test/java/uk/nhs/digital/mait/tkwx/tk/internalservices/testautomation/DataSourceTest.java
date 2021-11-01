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
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.DatasourceContext;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class DataSourceTest {

    private static DatasourceContext datasourceCtx;
    private DataSource instance;
    public static final String FIRST_ID = "urn:nhs-itk:interaction:primaryRecipientNonCodedCDADocument-v2-0_IA_NS_BA_T";

    public DataSourceTest() {
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
        for (String filename : new String[]{"src/test/resources/test.tdv", "src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() {
        instance = new CircularUpdatableOrderedPersistentFileDataSource();
        instance.init(datasourceCtx);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class DataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        // running init twice seems to cause a problem
    }

    /**
     * Test of close method, of class DataSource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        instance.close();
    }

    /**
     * Test of getName method, of class DataSource.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "patients";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNextId method, of class DataSource.
     */
    @Test
    public void testGetNextId() {
        System.out.println("getNextId");
        String expResult = FIRST_ID;
        String result = instance.getNextId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTags method, of class DataSource.
     */
    @Test
    public void testGetTags() {
        System.out.println("getTags");
        Iterable<String> result = instance.getTags();
        Iterator<String> tagIterator = result.iterator();
        String expResult = "__ID__";
        assertEquals(expResult, tagIterator.next());
    }

    /**
     * Test of getValue method, of class DataSource.
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
     * Test of setValue method, of class DataSource.
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
        instance.getValue(FIRST_ID, tag);
        assertEquals(value, instance.getValue(FIRST_ID, tag));
    }

    /**
     * Test of isReadOnly method, of class DataSource.
     */
    @Test
    public void testIsReadOnly() {
        System.out.println("isReadOnly");
        boolean expResult = false;
        boolean result = instance.isReadOnly();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasId method, of class DataSource.
     */
    @Test
    public void testHasId() {
        System.out.println("hasId");
        String id = "";
        boolean expResult = false;
        boolean result = instance.hasId(id);
        assertEquals(expResult, result);

        id = FIRST_ID;
        expResult = true;
        result = instance.hasId(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of hasValue method, of class DataSource.
     */
    @Test
    public void testHasValue() {
        System.out.println("hasValue");
        String id = FIRST_ID;
        String value = "__TITLE__";
        boolean expResult = true;
        boolean result = instance.hasValue(id, value);
        assertEquals(expResult, result);

        value = "__DNE__";
        expResult = false;
        result = instance.hasValue(id, value);
        assertEquals(expResult, result);
    }

    public class DataSourceImpl implements DataSource {

        @Override
        public void close() throws Exception {
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getNextId() {
            return "";
        }

        @Override
        public Iterable<String> getTags() {
            return null;
        }

        @Override
        public String getValue(String id, String tag) throws Exception {
            return "";
        }

        @Override
        public void setValue(String id, String tag, String value) throws Exception {
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public boolean hasId(String id) {
            return false;
        }

        @Override
        public boolean hasValue(String id, String value) {
            return false;
        }

        @Override
        public void link(ScriptParser p) throws Exception {
        }

        @Override
        public void init(DatasourceContext datasourceCtx) {
        }

        @Override
        public void setExtractorName(String extractorName) {
        }

        @Override
        public Iterable<String> getRecordids() {
            return null;
        }
    }

    /**
     * Test of getRecordids method, of class DataSource.
     */
    @Test
    public void testGetRecordids() {
        System.out.println("getRecordids");
        int expResult = 66;
        ArrayList<String> result = (ArrayList<String>) instance.getRecordids();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of setExtractorName method, of class DataSource.
     */
    @Test
    public void testSetExtractorName() {
        System.out.println("setExtractorName");
        String extractorName = "";
        instance.setExtractorName(extractorName);
    }
}
