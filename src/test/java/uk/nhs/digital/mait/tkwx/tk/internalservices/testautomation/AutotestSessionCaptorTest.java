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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.spinetools.spine.messaging.Sendable;

/**
 *
 * @author SIFA2
 */
public class AutotestSessionCaptorTest {
    
    public AutotestSessionCaptorTest() {
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
     * Test of getInstance method, of class AutotestSessionCaptor.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        // expects this class to be instatntaited by SpineTools in real operation
        AutotestSessionCaptor result = new AutotestSessionCaptor();
        
        result = AutotestSessionCaptor.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of capture method, of class AutotestSessionCaptor.
     */
    @Test
    public void testCapture() {
        System.out.println("capture");
        Sendable s = null;
        AutotestSessionCaptor instance = new AutotestSessionCaptor();
        instance.capture(s);
    }

    /**
     * Test of setMessagesFolder method, of class AutotestSessionCaptor.
     */
    @Test
    public void testSetMessagesFolder() {
        System.out.println("setMessagesFolder");
        String messagesFolder = "";
        AutotestSessionCaptor instance = new AutotestSessionCaptor();
        instance.setMessagesFolder(messagesFolder);
    }

    /**
     * Test of setMessagesFilename method, of class AutotestSessionCaptor.
     */
    @Test
    public void testSetMessagesFilename() {
        System.out.println("setMessagesFilename");
        String messagesFilename = "";
        AutotestSessionCaptor instance = new AutotestSessionCaptor();
        instance.setMessagesFilename(messagesFilename);
    }
    
}
