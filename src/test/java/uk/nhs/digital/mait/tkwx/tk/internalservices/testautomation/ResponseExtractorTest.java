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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class ResponseExtractorTest {
    private static AutotestParser.ExtractorContext extractorCtx;
    private ResponseExtractorImpl instance;
    
    public ResponseExtractorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        // copy a tdv file which is going to get trashed
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        extractorCtx = visitor.getExtractorContext();
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
        instance = new ResponseExtractorImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class ResponseExtractor.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        String[] line = null;
        instance.init(extractorCtx);
    }

    /**
     * Test of registerDatasourceListener method, of class ResponseExtractor.
     */
    @Test
    public void testRegisterDatasourceListener() {
        System.out.println("registerDatasourceListener");
        DataSource c = null;
        instance.registerDatasourceListener(c);
    }

    /**
     * Test of getName method, of class ResponseExtractor.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class ResponseExtractor.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtract() throws Exception {
        System.out.println("extract");
        String in = "";
        instance.extract(in);
    }

    public class ResponseExtractorImpl implements ResponseExtractor {

        @Override
        public void registerDatasourceListener(DataSource c) {
        }

        @Override
        public String getName() {
            return "name";
        }

        @Override
        public void extract(String in) throws Exception {
        }

        @Override
        public void init(AutotestParser.ExtractorContext extractorCtx) {
        }
    }
   
}
