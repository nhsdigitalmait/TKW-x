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

import java.io.IOException;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 *
 * @author sifa2
 */
public class ResponseTest {

    private static TestVisitor visitor;
    private static final String SESSION_ID = "s1";

    private Response instance;

    public ResponseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        visitor = new TestVisitor();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new Response("name", "NONE");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setCode method, of class Response.
     */
    @Test
    public void testSetCode() {
        System.out.println("setCode");
        int i = 0;
        instance.setCode(i);
    }

    /**
     * Test of setResponsePhrase method, of class Response.
     */
    @Test
    public void testSetResponsePhrase() {
        System.out.println("setResponsePhrase");
        String s = "";
        instance.setResponsePhrase(s);
    }

    /**
     * Test of setAction method, of class Response.
     */
    @Test
    public void testSetAction() {
        System.out.println("setAction");
        String a = "action";
        instance.setAction(a);
    }

    /**
     * Test of instantiate method, of class Response.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiate() throws Exception {
        System.out.println("instantiate");
        HashMap<String,Substitution> substitutions = new HashMap<>();
        substitutions.put("__LITERAL_TAG__",new Substitution(visitor.getSubstitutionCtx().get("__LITERAL_TAG__")));
//      substitutions.add(new Substitution("__TAG__","Literal",new String[]{"aaa"}));
        String o = "body __TAG__ body";
        int expResult = 0;
        ServiceResponse result = instance.instantiate(substitutions, o);
        // this expected when noResponse
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of addHttpHeader method, of class Response.
     */
    @Test
    public void testAddHttpHeader() {
        System.out.println("addHttpHeader");
        String headerName = "h3";
        String headerValue = "v3";
        instance.addHttpHeader(headerName, headerValue);
    }

    /**
     * Test of setVariableAssignment method, of class Response.
     */
    @Test
    public void testSetVariableAssignment() throws Exception {
        System.out.println("setVariableAssignment");
        String variableName = "$v1";
        String value = "l1";
        instance.setVariableAssignment(variableName, value);

        HashMap<String,Substitution> substitutions = new HashMap<>();
        substitutions.put("__LITERAL_TAG__",new Substitution(visitor.getSubstitutionCtx().get("__LITERAL_TAG__")));
//      substitutions.put("__TAG__",new Substitution("__TAG__","Literal",new String[]{"aaa"}));
        String o = "body __TAG__ body";
        String expResult = value;
        SessionStateManager ssm = SessionStateManager.getInstance();
        ssm.setCurrentSessionID(SESSION_ID);
        ServiceResponse result = instance.instantiate(substitutions, o);
        
        // for development purposes the sessionId is set to s1
        String resultStr = ssm.getValue(SESSION_ID,variableName);
        assertEquals(expResult, resultStr);
    }

}
