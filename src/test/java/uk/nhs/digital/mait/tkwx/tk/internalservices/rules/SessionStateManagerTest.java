/*
  Copyright 2019  Simon farrow simon.farrow1@nhs.net

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
public class SessionStateManagerTest {

    private SessionStateManager instance;
    private String sessionID;
    private String variableName;

    public SessionStateManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = SessionStateManager.getInstance();
        sessionID = "s1";
        variableName = "v1";
    }

    @After
    public void tearDown() {
        instance.resetSession("s1");
    }

    /**
     * Test of getInstance method, of class SessionStateManager.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        SessionStateManager result = SessionStateManager.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of getValue method, of class SessionStateManager.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        String expResult = "l1";
        instance.setValue(sessionID, variableName, expResult);
        String result = instance.getValue(sessionID, variableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class SessionStateManager.
     */
    @Test
    public void testSetValue() {
        System.out.println("setValue");
        String expResult = "l1";
        instance.setValue(sessionID, variableName, expResult);
        String result = instance.getValue(sessionID, variableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of resetSession method, of class SessionStateManager.
     */
    @Test
    public void testResetSession() {
        System.out.println("resetSession");
        String expResult = "l1";
        instance.setValue(sessionID, variableName, expResult);
        String result = instance.getValue(sessionID, variableName);
        assertEquals(expResult, result);
        instance.resetSession(sessionID);
        expResult = null;
        result = instance.getValue(sessionID, variableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class SessionStateManager.
     */
    @Test
    public void testGetValue_String() {
        System.out.println("getValue");
        
        instance.setCurrentSessionID(sessionID);

        String expResult = "l1";
        instance.setValue(sessionID, variableName, expResult);

        String result = instance.getValue(variableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class SessionStateManager. see getValue
     */
    @Test
    public void testGetValue_String_String() {
        System.out.println("getValue");
    }

    /**
     * Test of setValue method, of class SessionStateManager. see setValue
     */
    @Test
    public void testSetValue_String_String() {
        System.out.println("setValue");
    }

    /**
     * Test of setValue method, of class SessionStateManager.
     */
    @Test
    public void testSetValue_3args() {
        System.out.println("setValue");
        String sessionID = "";
        String variableName = "";
        String value = "";
        instance.setValue(sessionID, variableName, value);
    }

    /**
     * Test of getCurrentSessionID method, of class SessionStateManager.
     */
    @Test
    public void testGetCurrentSessionID() {
        System.out.println("getCurrentSessionID");
        String expResult = "sid";
        instance.setCurrentSessionID(expResult);
        String result = instance.getCurrentSessionID();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCurrentSessionID method, of class SessionStateManager.
     */
    @Test
    public void testSetCurrentSessionID() {
        System.out.println("setCurrentSessionID");
        String currentSessionID = "sessid";
        instance.setCurrentSessionID(currentSessionID);
    }

}
