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

import org.antlr.v4.runtime.BaseErrorListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;

/**
 *
 * @author simonfarrow
 */
public class AbstractAutotestParserTest {

    private static final String FILENAME = "src/test/resources/test.tst";

    public AbstractAutotestParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseFile method, of class AbstractAutotestParser.
     */
    @Test
    public void testParseFile_String() {
        System.out.println("parseFile");
        AbstractAutotestParser instance = new AbstractAutotestParserImpl();
        instance.parseFile(FILENAME);
    }

    /**
     * Test of parseFile method, of class AbstractAutotestParser.
     */
    @Test
    public void testParseFile_String_BaseErrorListener() {
        System.out.println("parseFile");
        BaseErrorListener errorListener = new BaseErrorListener();
        AbstractAutotestParser instance = new AbstractAutotestParserImpl();
        instance.parseFile(FILENAME, errorListener);
    }

    /**
     * Test of getAutotestParser method, of class AbstractAutotestParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetAutotestParser() throws Exception {
        System.out.println("getAutotestParser");
        AutotestParser result = AbstractAutotestParser.getAutotestParser(FILENAME);
        assertNotNull(result);
        String filename = result.getGrammarFileName();
        assertEquals("AutotestParser.g4",filename);
    }

    public class AbstractAutotestParserImpl extends AbstractAutotestParser {
    }

}
