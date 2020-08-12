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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.ISOFORMATDATEMASK_NOZ;

/**
 *
 * @author simonfarrow
 */
public class PropertySetFunctionsTest {

    private static final String JWT_TEMPLATE = System.getenv("TKWROOT")+"/contrib/TKWAutotestManager/tstp/WebServices/host/GP_CONNECT/jwt_templates/gp_connect_jwt_template.fhir3.txt";
    
    public PropertySetFunctionsTest() {
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
     * Test of getNow method, of class PropertySetFunctions.
     */
    @Test
    public void testGetNow() {
        System.out.println("getNow");
        String expResult = "^.*[0-9]{4}$";
        String result = PropertySetFunctions.getNow();
        assertTrue(result.matches(expResult));
    }

    /**
     * Test of getCWD method, of class PropertySetFunctions.
     */
    @Test
    public void testGetCWD() {
        System.out.println("getCWD");
        String expResult = "TKW";
        String result = PropertySetFunctions.getCWD();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of hello method, of class PropertySetFunctions.
     */
    @Test
    public void testHello_0args() {
        System.out.println("hello");
        String expResult = "hello";
        String result = PropertySetFunctions.hello();
        assertEquals(expResult, result);
    }

    /**
     * Test of hello method, of class PropertySetFunctions.
     */
    @Test
    public void testHello_String_String() {
        System.out.println("hello");
        String p1 = "p1";
        String p2 = "p2";
        String expResult = "hello"+p1+p2;
        String result = PropertySetFunctions.hello(p1, p2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getJWT method, of class PropertySetFunctions.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetJWT_5args() throws Exception {
        System.out.println("getJWT");
        String templateFile = JWT_TEMPLATE;
        String practitionerID = "prac";
        String nhsNo = "9999999999";
        String secret = "secret";
        String useBase64URLStr = "true";
        String expResult = "Bearer ewog";
        long iExpResult = 2;
        String result = PropertySetFunctions.getJWT(templateFile, practitionerID, nhsNo, secret, useBase64URLStr);
        assertTrue(result.startsWith(expResult));
        String parts[] = result.split("\\.");
        long iResult = result.chars().filter(ch -> ch == '.').count();
        assertEquals(iExpResult, iResult);
        assertTrue(parts.length == 2);
        for ( int i = 0; i < parts.length; i++){
            assertTrue(!parts[i].isEmpty());
        }

        useBase64URLStr = "false";
        result = PropertySetFunctions.getJWT(templateFile, practitionerID, nhsNo, secret, useBase64URLStr);
        assertTrue(result.startsWith(expResult));
        parts= result.split("\\.");
        iResult = result.chars().filter(ch -> ch == '.').count();
        assertEquals(iExpResult, iResult);

        assertTrue(parts.length == 2);
        for ( int i = 0; i < parts.length; i++){
            assertTrue(!parts[i].isEmpty());
        }
    }

    /**
     * Test of getJWT method, of class PropertySetFunctions.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetJWT_4args() throws Exception {
        System.out.println("getJWT");
        String templateFile = JWT_TEMPLATE;
        String practitionerID = "prac";
        String nhsNo = "9999999999";
        String secret = "secret";
        String expResult = "Bearer ewog";
        String result = PropertySetFunctions.getJWT(templateFile, practitionerID, nhsNo, secret);
        assertTrue(result.startsWith(expResult));
        String parts[] = result.split("\\.");
        assertTrue(parts.length == 2);
        for ( int i = 0; i < parts.length; i++){
            assertTrue(!parts[i].isEmpty());
        }
    }

    /**
     * Test of getJWT method, of class PropertySetFunctions.
     */
    @Test
    public void testGetJWT_6args() throws Exception {
        System.out.println("getJWT");
        String templateFile = JWT_TEMPLATE;
        String practitionerID = "prac";
        String nhsNo = "9999999999";
        String secret = "secret";
        String useBase64URLStr = "";
        String addSignatureStr = "";
        String expResult = "Bearer ewog";
        String result = PropertySetFunctions.getJWT(templateFile, practitionerID, nhsNo, secret, useBase64URLStr, addSignatureStr);
        assertTrue(result.startsWith(expResult));
        String parts[] = result.split("\\.");
        assertTrue(parts.length == 2);
        for ( int i = 0; i < parts.length; i++){
            assertTrue(!parts[i].isEmpty());
        }
    }

    /**
     * Test of getuuid method, of class PropertySetFunctions.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetuuid() throws Exception {
        System.out.println("getuuid");
        String expResult = "^[a-z0-9-]*$";
        String result = PropertySetFunctions.getuuid();
        assertTrue(result.matches(expResult));
    }

    /**
     * Test of getUUID method, of class PropertySetFunctions.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetUUID() throws Exception {
        System.out.println("getUUID");
        String expResult = "^[A-Z0-9-]*$";
        String result = PropertySetFunctions.getUUID();
        assertTrue(result.matches(expResult));
    }


    /**
     * Test of getURLEncodedTime method, of class PropertySetFunctions.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFormattedTime_String() throws Exception {
        System.out.println("GetFormattedTime");
        String format = ISOFORMATDATEMASK_NOZ;
        String timezone = "GMT";
        int  expResult = 19;
        String result = PropertySetFunctions.getFormattedTime(format,timezone);
        assertEquals(expResult, result.length());
    }

    /**
     * Test of getURLEncodedTime method, of class PropertySetFunctions.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFormattedTime_String_String() throws Exception {
        System.out.println("GetFormattedTime");
        String format = "yyyy-MM-dd'T'HH'%3A'mm'%3A'ss'%2B00%3A00'";
        //            HH     MM     SS
        //2018-09-07T 00 %3A 00 %3A 00 %2B 00 %3A 00

        String timezone = "GMT";
        int  expResult = 33;
        String result = PropertySetFunctions.getFormattedTime(format, timezone, "3", "0");
        assertEquals(expResult, result.length());
    }

    /**
     * Test of getFormattedTime method, of class PropertySetFunctions.
     * see testGetFormattedTime_String_String
     */
    @Test
    public void testGetFormattedTime_4args() throws Exception {
        System.out.println("getFormattedTime");
    }
    
}
