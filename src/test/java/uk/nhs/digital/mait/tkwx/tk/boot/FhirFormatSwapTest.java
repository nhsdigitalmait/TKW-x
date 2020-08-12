 /*
 Copyright 2012  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class FhirFormatSwapTest {
    
    private static PrintStream oldOut;
    private ByteArrayOutputStream bos;
    
    public FhirFormatSwapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        oldOut = System.out;
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        bos = new ByteArrayOutputStream();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testJson2Xml() throws FileNotFoundException, IOException, Exception {
        System.out.println("Json2Xml");
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(new String[]{"src/test/resources/slots.json"});
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "<";
        assertEquals(expResult,result.trim().substring(0,1));
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testXml2Json() throws FileNotFoundException, IOException, Exception {
        System.out.println("Xml2Json");
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(new String[]{"src/test/resources/slots.xml"});
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "{";
        assertEquals(expResult,result.trim().substring(0,1));
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testJson2Xml_2() throws FileNotFoundException, IOException, Exception {
        System.out.println("Json2Xml_2");
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(new String[]{"-2","src/test/resources/slots.json"});
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "<";
        assertEquals(expResult,result.trim().substring(0,1));
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testXml2Json_2() throws FileNotFoundException, IOException, Exception {
        System.out.println("Xml2Json_2");
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(new String[]{"-2","src/test/resources/slots.xml"});
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "{";
        assertEquals(expResult,result.trim().substring(0,1));
    }
    
    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testJson2Xml_3() throws FileNotFoundException, IOException, Exception {
        System.out.println("Json2Xml_3");
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(new String[]{"-3","src/test/resources/slots.json"});
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "<";
        assertEquals(expResult,result.trim().substring(0,1));
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testXml2Json_3() throws FileNotFoundException, IOException, Exception {
        System.out.println("Xml2Json_3");
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(new String[]{"-3","src/test/resources/slots.xml"});
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "{";
        assertEquals(expResult,result.trim().substring(0,1));
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testXml2Json_stdin3() throws FileNotFoundException, IOException, Exception {
        System.out.println("Xml2Json_stdin3");
        System.setOut(new PrintStream(bos));
        InputStream oldIn = System.in;
        System.setIn(new FileInputStream("src/test/resources/slots.xml"));
        FhirFormatSwap.main(new String[]{"-3","-"});
        System.setOut(oldOut);
        System.setIn(oldIn);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "{";
        assertEquals(expResult,result.trim().substring(0,1));
    }

    /**
     * Test of main method, of class FhirFormatSwap.
     * @throws java.lang.Exception
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = new String[]{"src/test/resources/slots.xml"};
        System.setOut(new PrintStream(bos));
        FhirFormatSwap.main(args);
        System.setOut(oldOut);
        String result = bos.toString();
        System.out.println(result);
        String expResult = "{";
        assertEquals(expResult,result.trim().substring(0,1));
    }
}
