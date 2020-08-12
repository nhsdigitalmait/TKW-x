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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.io.FileReader;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ReconfigureTags;

/**
 *
 * @author SIFA2
 */
public class SpineToolsTransportTest {

    private final static String SERVICE = "SpineToolsTransport";
    private ToolkitSimulator t;
    private Properties p;
    private SpineToolsTransport instance;

    public SpineToolsTransportTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        String propsFile = System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties";
        t = new ToolkitSimulator(propsFile);
        p = new Properties();
        p.load(new FileReader(propsFile));
        instance = new SpineToolsTransport();
    }

    @After
    public void tearDown() throws Exception {
        instance.stop();
    }

    /**
     * Test of getBootProperties method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBootProperties() throws Exception {
        System.out.println("getBootProperties");
        Properties expResult = p;
        instance.boot(t, p, SERVICE);
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result);
    }

    /**
     * Test of stop method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testStop() throws Exception {
        System.out.println("stop");
        instance.boot(t, p, SERVICE);
        instance.stop();
    }

    /**
     * Test of reconfigure method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_Properties() throws Exception {
        System.out.println("reconfigure");
        instance.boot(t, p, SERVICE);
        instance.reconfigure(p);
    }

    /**
     * Test of reconfigure method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = ReconfigureTags.SAVED_MESSAGES;
        String value = "";
        String expResult = "";
        instance.boot(t, p, SERVICE);
        String result = instance.reconfigure(what, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of boot method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
        instance.boot(t, p, SERVICE);
    }

    /**
     * Test of execute method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");
        Object param = null;
        String expResult = "SpineTools simulator";
        instance.boot(t, p, SERVICE);
        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result.getResponse());
    }

    /**
     * Test of execute method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String type = "";
        String param = "";
        String expResult = "SpineTools simulator";
        instance.boot(t, p, SERVICE);
        ServiceResponse result = instance.execute(new String[]{type, param});
        assertEquals(expResult, result.getResponse());
    }

    /**
     * Test of execute method, of class SpineToolsTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");
        String type = "";
        Object param = null;
        String expResult = "SpineTools simulator";
        instance.boot(t, p, SERVICE);
        ServiceResponse result = instance.execute(new Object[]{type, param});
        assertEquals(expResult, result.getResponse());
    }

    /**
     * Test of execute method, of class SpineToolsTransport.
     * @throws java.lang.Exception
     * see testExecute_Object code duplicate to evade npe on tearDown
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
        Object param = null;
        String expResult = "SpineTools simulator";
        instance.boot(t, p, SERVICE);
        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result.getResponse());
    }

}
