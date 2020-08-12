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
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 *
 * @author sifa2
 */
public class SingleRecordXpathResponseExtractorTest {

    private static AutotestParser.ExtractorContext extractorCtx;
    private static AutotestParser.DatasourceContext datasourceCtx;

    private SingleRecordXpathResponseExtractor instance;

    public SingleRecordXpathResponseExtractorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {

        // replaced with actual persistent file
//        try (FileWriter fw = new FileWriter("src/test/resources/extract.cfg")) {
//            fw.write("__ID__ /a/b/@id ID\r\n");
//            fw.write("__DATA__ /a/b/text()\r\n");
//        }

        // create a new datasource file each time (this will get overwritten)
        try (FileWriter fw = new FileWriter("src/test/resources/test.tdv")) {
            fw.write("__ID__\t__DATA__\r\n");
            fw.write("id1\tdata1\r\n");
        }

        TestVisitor visitor = new TestVisitor();
        extractorCtx = visitor.getExtractorContext();
        datasourceCtx = visitor.getDatasourceContext();
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
    public void setUp() {
        instance = new SingleRecordXpathResponseExtractor();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class SingleRecordXpathResponseExtractor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(extractorCtx);
    }

    /**
     * Test of extract method, of class SingleRecordXpathResponseExtractor.
     * extracts from xml using a cfg file containing xpaths into a DataSource
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtract() throws Exception {
        System.out.println("extract");
        instance.init(extractorCtx);
        DataSource c = new CircularUpdatableOrderedPersistentFileDataSource();
        c.init(datasourceCtx);

        // check initial value from file
        assertEquals("data1", c.getValue(RECORD_KEY, TAG_NAME));

        String expResult = "xxx";
        String in = "<a><b id=\""+RECORD_KEY+"\">"+expResult+"</b></a>";
        instance.registerDatasourceListener(c);
        
        // do the extrcation on the string input
        instance.extract(in);
        
        // check the new value is set
        assertEquals(expResult, c.getValue(RECORD_KEY, TAG_NAME));

        c.close();
    }
    
    private static final String TAG_NAME = "__DATA__";
    private static final String RECORD_KEY = "id1";

    /**
     * Test of registerDatasourceListener method, of class
     * SingleRecordXpathResponseExtractor.
     */
    @Test
    public void testRegisterDatasourceListener() {
        System.out.println("registerDatasourceListener");
        DataSource c = new CircularUpdatableOrderedPersistentFileDataSource();
        instance.registerDatasourceListener(c);
    }

    /**
     * Test of getName method, of class SingleRecordXpathResponseExtractor.
     */
    @Test
    public void testGetName() {
        try {
            System.out.println("getName");
            String expResult = "extractor_name";
            instance.init(extractorCtx);
            String result = instance.getName();
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Exception thrown " + ex.getMessage());
        }
    }

}
