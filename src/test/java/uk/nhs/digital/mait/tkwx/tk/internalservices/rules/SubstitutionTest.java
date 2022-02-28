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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;

/**
 *
 * @author sifa2
 */
public class SubstitutionTest {

    private static TestVisitor visitor;

    private Substitution instance;

    public SubstitutionTest() {
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
        // literal tag has value aaa
        SimulatorRulesParser.SubstitutionContext ctx = visitor.getSubstitutionCtx().get("__LITERAL_TAG__");
        instance = new Substitution(ctx);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of substitute method, of class Substitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute() throws Exception {
        System.out.println("substitute");

        // Literal substitution
        StringBuffer sbTemplate = new StringBuffer("body __LITERAL_TAG__ body");
        String requestContent = "xxx not used";
        instance.substitute(sbTemplate, requestContent);
        String expResult = "body aaa body";
        assertEquals(expResult, sbTemplate.toString());

        // Upper case UUID substitution
        instance = new Substitution(visitor.getSubstitutionCtx().get("__UC_UUID_TAG__"));
        sbTemplate = new StringBuffer("__UC_UUID_TAG__");
        instance.substitute(sbTemplate, requestContent);
        assertTrue(sbTemplate.toString().matches("^[0-9A-Z]{8}(-[0-9A-Z]{4}){3}-[0-9A-Z]{12}$"));

        // Lower case UUID substitution
        instance = new Substitution(visitor.getSubstitutionCtx().get("__LC_UUID_TAG__"));
        sbTemplate = new StringBuffer("__LC_UUID_TAG__");
        instance.substitute(sbTemplate, requestContent);
        assertTrue(sbTemplate.toString().matches("^[0-90a-z]{8}(-[0-9a-z]{4}){3}-[0-9a-z]{12}$"));

        // System property substitution
        instance = new Substitution(visitor.getSubstitutionCtx().get("__PROPERTY_TAG__"));
        // System property tag is user.dir ie current directory, get the fully qualified path
        sbTemplate = new StringBuffer("__PROPERTY_TAG__");
        instance.substitute(sbTemplate, requestContent);
        assertEquals(new File(".").getCanonicalPath(), sbTemplate.toString());

        // Xpath substitution
        requestContent = "<a>b</a>";
        instance = new Substitution(visitor.getSubstitutionCtx().get("__XPATH_TAG__"));
        sbTemplate = new StringBuffer("__XPATH_TAG__");
        instance.substitute(sbTemplate, requestContent);
        assertEquals("b", sbTemplate.toString());

        // Class substitution this invokes the delay substitution which returns an empty string
        requestContent = "key";
        instance = new Substitution(visitor.getSubstitutionCtx().get("__CLASS_TAG__"));
        sbTemplate = new StringBuffer("__CLASS_TAG__");
        instance.substitute(sbTemplate, requestContent);
        assertEquals("", sbTemplate.toString());
        
        // Reg exp substitution match source = content
        requestContent = "ContentStartStuffContentEnd";
        instance = new Substitution(visitor.getSubstitutionCtx().get("__RE_CONTENT_TAG__"));
        sbTemplate = new StringBuffer("aaa __RE_CONTENT_TAG__ aaa");
        instance.substitute(sbTemplate, requestContent);
        expResult = "aaa Stuff aaa";
        assertEquals(expResult, sbTemplate.toString());

        // Reg exp substitution match source = content
        // using HttpRequest
        HttpRequest req = new HttpRequest("");
        req.setInputStream(new ByteArrayInputStream(requestContent.getBytes()));
        req.setContentLength(requestContent.getBytes().length);
        instance.substitute(sbTemplate, req);
        assertEquals(expResult, sbTemplate.toString());

        // Reg exp substitution match source = context path
        instance = new Substitution(visitor.getSubstitutionCtx().get("__RE_CP_TAG__"));
        sbTemplate = new StringBuffer("bbb __RE_CP_TAG__ bbb");
        expResult = "bbb thiscpbit bbb";
        req.setRequestContext("/DocumentReferencethiscpbit");
        req.setHeader("h1", "StartHeaderthisheaderbitEndHeader");

        instance.substitute(sbTemplate, req);
        assertEquals(expResult, sbTemplate.toString());

        // Reg exp substitution match source = http header
        instance = new Substitution(visitor.getSubstitutionCtx().get("__RE_HTTP_HEADER_TAG__"));
        sbTemplate = new StringBuffer("ccc __RE_HTTP_HEADER_TAG__ ccc");
        instance.substitute(sbTemplate, req);
        expResult = "ccc thisheaderbit ccc";
        assertEquals(expResult, sbTemplate.toString());

        // Multiple Reg exp substitution match source = http header
        requestContent = "ContentStartStuffContentEnd";
        instance = new Substitution(visitor.getSubstitutionCtx().get("__MULTIPLE_RE_CONTENT_TAG__"));
        sbTemplate = new StringBuffer("aaa __MULTIPLE_RE_CONTENT_TAG__ aaa");
        instance.substitute(sbTemplate, requestContent);
        expResult = "aaa StuffStuff aaa";
        assertEquals(expResult, sbTemplate.toString());

        req.setRequestContext("/DocumentReferencethiscpbit");
        req.setHeader("h1", "StartHeaderthisheaderbitEndHeader");

        // Class substitution from context_path this invokes the delay substitution which returns an empty string
        instance = new Substitution(visitor.getSubstitutionCtx().get("__CLASS_TAG_CP__"));
        sbTemplate = new StringBuffer("__CLASS_TAG_CP__");
        instance.substitute(sbTemplate, req);
        assertEquals("", sbTemplate.toString());

        // Class substitution from http header this invokes the delay substitution which returns an empty string
        instance = new Substitution(visitor.getSubstitutionCtx().get("__CLASS_TAG_HEADER__"));
        sbTemplate = new StringBuffer("__CLASS_TAG_HEADER__");
        instance.substitute(sbTemplate, req);
        assertEquals("", sbTemplate.toString());

        // Jsonpath substitution
        requestContent = "{ \"a\" : \"b\"}";
        instance = new Substitution(visitor.getSubstitutionCtx().get("__JSONPATH_TAG__"));
        sbTemplate = new StringBuffer("__JSONPATH_TAG__");
        instance.substitute(sbTemplate, requestContent);
        assertEquals("b", sbTemplate.toString());
        
        // absent header
        instance = new Substitution(visitor.getSubstitutionCtx().get("__HEADER_B64__"));
        sbTemplate = new StringBuffer("__HEADER_B64__");
        instance.substitute(sbTemplate, req);
        assertEquals("", sbTemplate.toString());
        
        // present header
        req.setHeader("NHSD-Target-Identifier","ewogICJ2YWx1ZSI6ICJUS1cwMDA0IiwKICAic3lzdGVtIjogImh0dHA6Ly9kaXJlY3RvcnlvZnNlcnZpY2VzLm5ocy51ayIKfQo=");
        instance = new Substitution(visitor.getSubstitutionCtx().get("__HEADER_B64__"));
        sbTemplate = new StringBuffer("__HEADER_B64__");
        instance.substitute(sbTemplate, req);
        assertEquals("TKW0004", sbTemplate.toString());

        // matching jsonpath
        instance = new Substitution(visitor.getSubstitutionCtx().get("__HEADER_B64_JSONPATH__"));
        sbTemplate = new StringBuffer("__HEADER_B64_JSONPATH__");
        instance.substitute(sbTemplate, req);
        assertEquals("http://directoryofservices.nhs.uk", sbTemplate.toString());

        // non matching jsonpath
        instance = new Substitution(visitor.getSubstitutionCtx().get("__HEADER_B64_JSONPATH__"));
        sbTemplate = new StringBuffer("__HEADER_B64_NON_MATCHING_JSONPATH__");
        instance.substitute(sbTemplate, req);
        assertEquals("__HEADER_B64_NON_MATCHING_JSONPATH__", sbTemplate.toString());
    }

    /**
     * Test of substitute method, of class Substitution. Only test one instance
     * of Service Response as all the different types of Substitution are tested
     * above for Strings
     */
    @Test
    public void testSubstitute_ServiceResponse() throws Exception {
        System.out.println("substitute");
        // Literal substitution
        ServiceResponse sr = new ServiceResponse(0, "body __LITERAL_TAG__ body");
        HttpHeaderManager hm = new HttpHeaderManager();
        hm.addHttpHeader("Accept", "header __LITERAL_TAG__ header");
        sr.setHttpHeaders(hm);
        String requestContent = "xxx not used";
        instance.substitute(sr, requestContent);
        String expResult = "body aaa body";
        assertEquals(expResult, sr.getResponse());

        expResult = "header aaa header";
        assertEquals(expResult, sr.getHttpHeaders().getHttpHeaderValue("Accept"));

    }

    /**
     * Test of getTag method, of class Substitution.
     */
    @Test
    public void testGetTag() {
        System.out.println("getTag");
        String expResult = "__LITERAL_TAG__";
        String result = instance.getTag();
        assertEquals(expResult, result);
    }

    /**
     * Test of substitute method, of class Substitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute_StringBuffer_String() throws Exception {
        System.out.println("substitute");
        StringBuffer sb = new StringBuffer("000 __LITERAL_TAG__ 111");
        String o = "ignored for a literal";
        instance.substitute(sb, o);
        String expResult = "000 aaa 111";
        assertEquals(expResult, sb.toString());
    }

    /**
     * Test of substitute method, of class Substitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute_ServiceResponse_String() throws Exception {
        System.out.println("substitute");
        ServiceResponse sr = new ServiceResponse();
        sr.setResponse("000 __LITERAL_TAG__ 111");
        String o = "ignored for a literal";
        instance.substitute(sr, o);
        String expResult = "000 aaa 111";
        assertEquals(expResult, sr.getResponse());
    }

    /**
     * Test of substitute method, of class Substitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute_StringBuffer_Request() throws Exception {
        System.out.println("substitute");
        StringBuffer sb = new StringBuffer("000 __LITERAL_TAG__ 111");
        Request req = new HttpRequest("");
        instance.substitute(sb, req);
        String expResult = "000 aaa 111";
        assertEquals(expResult, sb.toString());
    }

    /**
     * Test of substitute method, of class Substitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute_ServiceResponse_Request() throws Exception {
        System.out.println("substitute");
        ServiceResponse sr = new ServiceResponse();
        sr.setResponse("000 __LITERAL_TAG__ 111");
        Request req = new HttpRequest("");
        instance.substitute(sr, req);
        String expResult = "000 aaa 111";
        assertEquals(expResult, sr.getResponse());
    }
    
}
