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
package uk.nhs.digital.mait.tkwx.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.TestVisitor;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;
import static uk.nhs.digital.mait.tkwx.util.Utils.htmlEncode;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import static uk.nhs.digital.mait.tkwx.util.Utils.substituteHandleNull;
import static uk.nhs.digital.mait.tkwx.util.Utils.writeString2File;
import static uk.nhs.digital.mait.tkwx.util.Utils.xmlReformat;

/**
 *
 * @author simonfarrow
 */
public class UtilsTest {

    private static TestVisitor visitor;

    public UtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        visitor = new TestVisitor();
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
     * Test of htmlEncode method, of class Utils.
     */
    @Test
    public void testHtmlEncode() {
        System.out.println("htmlEncode");
        String s = "<";
        String expResult = "&lt;";
        String result = htmlEncode(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of xmlReformat method, of class Utils.
     */
    @Test
    public void testXmlReformat() {
        System.out.println("xmlReformat");

        // check preservation or not of encoding and PI
        String xmlIn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>xxx</b></a>";
        String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<a>"+System.lineSeparator() 
                + "   <b>xxx</b>"+System.lineSeparator()
                + "</a>"+System.lineSeparator()
                + "";
        String result = xmlReformat(xmlIn);
        assertEquals(expResult, result);

        // check preservation of PI
        xmlIn = "<?xml version=\"1.0\"?><a><b>xxx</b></a>";
        expResult = "<?xml version=\"1.0\"?>"
                + "<a>"+System.lineSeparator()
                + "   <b>xxx</b>"+System.lineSeparator()
                + "</a>"+System.lineSeparator()
                + "";
        result = xmlReformat(xmlIn);
        assertEquals(expResult, result);

        xmlIn = "<a><b>xxx</b></a>";
        expResult = "<a>"+System.lineSeparator()
                + "   <b>xxx</b>"+System.lineSeparator()
                + "</a>"+System.lineSeparator()
                + "";
        result = xmlReformat(xmlIn);
        assertEquals(expResult, result);
    }

    /**
     * Test of testWriteString2File method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteString2File_String_String() throws Exception {
        System.out.println("writeString2File_String_String");
        String file = "src/test/resources/test.tmp";
        String expResult = "aaa"+System.getProperty("line.separator")+"bbb"+System.getProperty("line.separator");
        writeString2File(file, expResult);
        String result = readFile2String(file);
        assertEquals(expResult, result);
        new File(file).delete();
    }

    /**
     * Test of writeString2File method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteString2File_File_String() throws Exception {
        System.out.println("writeString2File");
        File file = new File("src/test/resources/test.tmp");
        String s = "content";
        Utils.writeString2File(file, s);
        assertTrue(file.exists());
        file.delete();
    }

    /**
     * Test of substitute method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute() throws Exception {
        System.out.println("substitute");
        StringBuilder sb = new StringBuilder("a__TAG__c");
        String tag = "__TAG__";
        String value = "b";

        boolean result = substitute(sb, tag, value);
        String expResult = "abc";
        boolean expBooleanResult = true;
        assertEquals(expResult, sb.toString());
        assertEquals(expBooleanResult, result);

        sb = new StringBuilder("a__TAG__c");
        result = substitute(sb, tag, "");
        expResult = "ac";
        assertEquals(expResult, sb.toString());
        assertEquals(expBooleanResult, result);

        expBooleanResult = false;
        sb = new StringBuilder("a__TAG1__c");
        result = substitute(sb, tag, "b");
        expResult = "a__TAG1__c";
        assertEquals(expResult, sb.toString());
        assertEquals(expBooleanResult, result);

        // this falls over with a null pointer exception
        sb = new StringBuilder("a__TAG__c");
        try {
            result = substitute(sb, tag, null);
            expResult = "abc";
            expBooleanResult = false;
            assertEquals(expResult, sb.toString());
            assertEquals(expBooleanResult, result);
        } catch (Exception e) {
        }
    }

    /**
     * Test of getTextFromAntlrRule method, of class Utils.
     */
    @Test
    public void testGetTextFromAntlrRule() {
        System.out.println("getTextFromAntlrRule");
        ParserRuleContext ctx = visitor.getExpressionCtx().get("exp_always");
        String expResult = "exp_always always";
        String result = Utils.getTextFromAntlrRule(ctx);
        assertEquals(expResult, result);
    }

    /**
     * Test of loadTemplate method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadTemplate_InputStream() throws Exception {
        System.out.println("loadTemplate");
        String expResult = "abc"+System.lineSeparator()+"123"+System.lineSeparator();
        InputStream is = new ByteArrayInputStream(expResult.getBytes());
        String result = Utils.readFile2String(is);
        assertEquals(expResult, result);
    }

    /**
     * Test of loadTemplate method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadTemplate_BufferedReader() throws Exception {
        System.out.println("loadTemplate");
        String expResult = "abc"+System.lineSeparator()+"123"+System.lineSeparator();
        BufferedReader br = new BufferedReader(new StringReader(expResult));
        String result = Utils.readFile2String(br);
        assertEquals(expResult, result);
    }

    /**
     * Test of streamToByteArray method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testStreamToByteArray() throws Exception {
        System.out.println("streamToByteArray");
        InputStream in = new ByteArrayInputStream(new byte[]{1, 2, 3});
        byte[] expResult = new byte[]{1, 2, 3};
        byte[] result = Utils.streamToByteArray(in);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of jsonReformat method, of class Utils.
     */
    @Test
    public void testJsonReformat() {
        System.out.println("jsonReformat");
        String json = "{ \"x\" : \"y\"}";
        String expResult = System.lineSeparator()+"{"+System.lineSeparator()+"    \"x\":\"y\""+System.lineSeparator()+"}";
        String result = Utils.jsonReformat(json);
        assertEquals(expResult, result);
    }

    /**
     * Test of isNullOrEmpty method, of class Utils.
     */
    @Test
    public void testIsNullOrEmpty() {
        System.out.println("isNullOrEmpty");
        String st = "";
        boolean expResult = true;

        boolean result = Utils.isNullOrEmpty(st);
        assertEquals(expResult, result);

        st = null;
        result = Utils.isNullOrEmpty(st);
        assertEquals(expResult, result);

        expResult = false;
        st = "xx";
        result = Utils.isNullOrEmpty(st);
        assertEquals(expResult, result);
    }

    /**
     * Test of substituteHandleNull method, of class Utils.
     */
    @Test
    public void testSubstituteHandleNull() {
        System.out.println("substituteHandleNull");
        System.out.println("substitute");
        StringBuilder sb = new StringBuilder("a__TAG__c");
        String tag = "__TAG__";
        String value = "b";
        boolean result = substituteHandleNull(sb, tag, value);
        String expResult = "abc";
        assertEquals(expResult, sb.toString());
        boolean expBooleanResult = true;
        assertEquals(expBooleanResult, result);

        value = null;
        result = substituteHandleNull(sb, tag, value);
        assertEquals(expResult, sb.toString());

        sb = new StringBuilder("a__TAG__c");
        result = substituteHandleNull(sb, tag, "");
        expResult = "ac";
        assertEquals(expResult, sb.toString());
        assertEquals(expBooleanResult, result);

        expBooleanResult = false;
        sb = new StringBuilder("a__TAG1__c");
        result = substituteHandleNull(sb, tag, "b");
        expResult = "a__TAG1__c";
        assertEquals(expResult, sb.toString());
        assertEquals(expBooleanResult, result);

        sb = new StringBuilder("a__TAG__c");
        result = substituteHandleNull(sb, tag, null);
        expResult = "ac";
        expBooleanResult = true;
        assertEquals(expResult, sb.toString());
        assertEquals(expBooleanResult, result);
    }

    /**
     * Test of invokeJavaMethod method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInvokeJavaMethod() throws Exception {
        System.out.println("invokeJavaMethod");
        String value = "function:uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.hello xx yyy";
        String expResult = "helloxxyyy";
        String result = Utils.invokeJavaMethod(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of readFile2String method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFile2String_String() throws Exception {
        System.out.println("readFile2String");
        String file = TEST_TEXT_FILE;
        String expResult = "SCRIPT";
        String result = Utils.readFile2String(file);
        assert (result.startsWith(expResult));
    }

    /**
     * Test of readFile2String method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFile2String_String_String() throws Exception {
        System.out.println("readFile2String");
        String dir = "src/test/resources";
        String filename = "correspondence.tst";
        String expResult = "SCRIPT";
        String result = Utils.readFile2String(dir, filename);
        assert (result.startsWith(expResult));
    }

    /**
     * Test of readFile2String method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFile2String_InputStream() throws Exception {
        System.out.println("readFile2String");
        try (InputStream is = new FileInputStream(TEST_TEXT_FILE)) {
            String expResult = "SCRIPT";
            String result = Utils.readFile2String(is);
            assertTrue (result.startsWith(expResult));
        }
    }

    /**
     * Test of readFile2String method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFile2String_BufferedReader() throws Exception {
        System.out.println("readFile2String");
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_TEXT_FILE))) {
            String expResult = "SCRIPT";
            String result = Utils.readFile2String(br);
            assertTrue (result.startsWith(expResult));
        }
    }
    private static final String TEST_TEXT_FILE = "src/test/resources/correspondence.tst";

    /**
     * Test of readColonSeparatedFile2Map method, of class Utils.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadColonSeparatedFile2Map() throws Exception {
        System.out.println("readColonSeparatedFile2Map");
        final String FILENAME = "src/test/resources/csf.csf";
        try (FileWriter fw = new FileWriter(FILENAME)) {
            fw.write("a:b\r\nc:d\r\n");
        }
        String file = FILENAME;
        Map<String, String> expResult = new HashMap<>();
        expResult.put("a","b");
        expResult.put("c","d");
        Map<String, String> result = Utils.readColonSeparatedFile2Map(file);
        assertEquals(expResult, result);
        new File(FILENAME).delete();
    }

    /**
     * Test of isY method, of class Utils.
     */
    @Test
    public void testIsY() {
        System.out.println("isY");
        String s = "y";
        boolean expResult = true;
        boolean result = Utils.isY(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of readPropertiesFile method, of class Utils.
     */
    @Test
    public void testReadPropertiesFile() {
        System.out.println("readPropertiesFile");
        String propertiesFile = System.getenv("TKWROOT")+"/config/GP_CONNECT/tkw-x.properties";
        Properties p = new Properties();
        boolean expResult = true;
        boolean result = Utils.readPropertiesFile(propertiesFile, p);
        assertEquals(expResult, result);
    }

    /**
     * Test of doEscape method, of class Utils.
     */
    @Test
    public void testDoEscape() {
        System.out.println("doEscape");
        String s = "s&m";
        String expResult = "s&amp;m";
        String result = Utils.doEscape(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of fileExists method, of class Utils.
     */
    @Test
    public void testFileExists() {
        System.out.println("fileExists");
        String path = System.getenv("TKWROOT")+"/config/GP_CONNECT/tkw-x.properties";
        boolean expResult = true;
        boolean result = Utils.fileExists(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of folderExists method, of class Utils.
     */
    @Test
    public void testFolderExists() {
        System.out.println("folderExists");
        String path = System.getenv("TKWROOT")+"/config/GP_CONNECT";
        boolean expResult = true;
        boolean result = Utils.folderExists(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of copyFile method, of class Utils.
     */
    @Test
    public void testCopyFile() {
        System.out.println("copyFile");
        String source = System.getenv("TKWROOT")+"/config/GP_CONNECT/tkw-x.properties";
        String dest = "src/test/resources/tkw-x.properties";
        assertTrue(!fileExists(dest));
        boolean expResult = true;
        boolean result = Utils.copyFile(source, dest);
        assertEquals(expResult, result);
        assertTrue(fileExists(dest));
        new File(dest).delete();
    }

    /**
     * Test of createFolderIfMissing method, of class Utils.
     * 
     * @throws java.io.IOException
     */
    @Test
    public void testCreateFolderIfMissing() throws IOException {
        System.out.println("createFolderIfMissing");
        String path = "src/test/resources/testfolder";
        Utils.createFolderIfMissing(path);
        boolean expResult = true;
        boolean result = Utils.folderExists(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of isYDefaultY method, of class Utils.
     */
    @Test
    public void testIsYDefaultY() {
        System.out.println("isYDefaultY");
        String s = null;
        boolean expResult = true;
        boolean result = Utils.isYDefaultY(s);
        assertEquals(expResult, result);
        s = "y";
        result = Utils.isYDefaultY(s);
        assertEquals(expResult, result);
    }

}
