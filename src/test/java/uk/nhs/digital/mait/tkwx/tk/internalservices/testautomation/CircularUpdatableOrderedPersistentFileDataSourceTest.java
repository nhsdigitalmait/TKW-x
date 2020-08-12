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
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.DataSourceTest.FIRST_ID;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class CircularUpdatableOrderedPersistentFileDataSourceTest {

    private static AutotestParser.DatasourceContext datasourceCtx;
    private CircularUpdatableOrderedPersistentFileDataSource instance;

    public CircularUpdatableOrderedPersistentFileDataSourceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
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
        instance = new CircularUpdatableOrderedPersistentFileDataSource();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getNextId method, of class
     * CircularUpdatableOrderedPersistentFileDataSource.
     */
    @Test
    public void testGetNextId() {
        try {
            System.out.println("getNextId");
            instance.init(datasourceCtx);
            String expResult = FIRST_ID;
            String result = instance.getNextId();
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Exception thrown " + ex.getMessage());
        }
    }
}
