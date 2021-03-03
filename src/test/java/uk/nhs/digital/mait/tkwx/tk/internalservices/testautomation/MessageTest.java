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
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.NAME;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TIMESTAMP;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestTest.deleteFolder;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;


/**
 *
 * @author sifa2
 */
public class MessageTest {

    private Message instance;

    public MessageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
    }

    @AfterClass
    public static void tearDownClass() {
        // delete trashed tdv file
        new File("src/test/resources/test.tdv").delete();
    }

    @Before
    public void setUp() throws Exception {
        new File(TIMESTAMP).mkdir();
        instance = BasicMessageIdCorrelatorTest.getMessage(NAME, TIMESTAMP);
    }

    @After
    public void tearDown() {
        deleteFolder(TIMESTAMP);
    }

    /**
     * Test of getName method, of class Message.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = NAME;
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMessageId method, of class Message.
     */
    @Test
    public void testGetMessageId() {
        System.out.println("getMessageId");
        String result = instance.getMessageId();
        assertTrue(result.matches("^[\\p{XDigit}]{8}-[\\p{XDigit}]{4}-[\\p{XDigit}]{4}-[\\p{XDigit}]{4}-[\\p{XDigit}]{12}$"));
    }

    /**
     * Test of getTrackingId method, of class Message.
     */
    @Test
    public void testGetTrackingId() {
        System.out.println("getTrackingId");
        String result = instance.getTrackingId();
        assertTrue(result.matches("^[\\p{XDigit}]{8}-[\\p{XDigit}]{4}-[\\p{XDigit}]{4}-[\\p{XDigit}]{4}-[\\p{XDigit}]{12}$"));
    }

    /**
     * Test of link method, of class Message.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLink() throws Exception {
        System.out.println("link");
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        ScriptParser p = new ScriptParser(props);
        p.parse("src/test/resources/test.tst");
        instance.link(p);
    }


    /**
     * Test of instantiate method, of class Message.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiate_7args() throws Exception {
        System.out.println("instantiate_7args");
        String ts = TIMESTAMP;
        String to = "to";
        String from = "from";
        String replyto = "replyto";
        HashMap<String, ArrayList<String>> pst = null;
        String profileId = "profileid";
        int iterationp = 0;
        String expResult = "DE_EMPPID_9.msg";
        String result = instance.instantiate(null, ts, to, from, replyto, pst, null, profileId, iterationp);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDatasource method, of class Message.
     */
    @Test
    public void testGetDatasource() {
        System.out.println("getDatasource");
        DataSource expResult = null;
        DataSource result = instance.getDatasource();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRecordid method, of class Message.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetRecordid() throws Exception {
        System.out.println("getRecordid");
        String expResult = null;
        String result = instance.getRecordid();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDatasourceName method, of class Message.
     */
    @Test
    public void testGetDatasourceName() {
        System.out.println("getDatasourceName");
        String expResult = "NULL";
        String result = instance.getDatasourceName();
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiate method, of class Message.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiate_8args() throws Exception {
        System.out.println("instantiate_8args");
        String ts = TIMESTAMP;
        String to = "to";
        String from = "from";
        String replyto = "replyto";
        HashMap<String, ArrayList<String>> xslTransforms = new HashMap<>();
        HashMap<String, ArrayList<uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.RegexpSubstitution>> reSubstitutions = new HashMap<>();
        String profileId = "profileid";
        int iterationp = 0;
        String expResult = "DE_EMPPID_11.msg";
        String result = instance.instantiate(null, ts, to, from, replyto, xslTransforms, reSubstitutions, profileId, iterationp);
        assertEquals(expResult, result);
    }

    /**
     * Test of instantiate method, of class Message.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInstantiate_9args() throws Exception {
        System.out.println("instantiate_9args");
        String ts = TIMESTAMP;
        String to = "to";
        String from = "from";
        String replyto = "replyto";
        HashMap<String, ArrayList<String>> xslTransforms = new HashMap<>();
        HashMap<String, ArrayList<uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.RegexpSubstitution>> reSubstitutions = new HashMap<>();
        String profileId = "profileid";
        int iterationp = 0;
        String expResult = "DE_EMPPID_13.msg";
        String result = instance.instantiate(null, ts, to, from, replyto, xslTransforms, reSubstitutions, profileId, iterationp);
        assertEquals(expResult, result);
    }
}
