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
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.TOOLKIT_SIMULATOR;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ReconfigureTags;

/**
 *
 * @author sifa2
 */
public class HttpTransportTest {

    private final static String SERVICE = "HttpTransport";
    private ToolkitSimulator t;
    private Properties p;
    private HttpTransport instance;

    public HttpTransportTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        String propsFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        t = new ToolkitSimulator(propsFile);
        p = new Properties();
        // dont forget to load the internal props
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(propsFile));
        instance = new HttpTransport();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBootProperties method, of class HttpTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBootProperties() throws Exception {
        System.out.println("getBootProperties");
        instance.boot(t, p, SERVICE);
        Properties expResult = p;
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result);
    }

    /**
     * Test of stop method, of class HttpTransport.
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
     * Test of reconfigure method, of class HttpTransport.
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
     * Test of reconfigure method, of class HttpTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = ReconfigureTags.SAVED_MESSAGES;
        String value = "";
        instance.boot(t, p, SERVICE);
        String expResult = null;
        String result = instance.reconfigure(what, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of boot method, of class HttpTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
        String s = SERVICE;
        instance.boot(t, p, s);
    }

    /**
     * Test of isUsingTLS method, of class HttpTransport.
     */
    @Test
    public void testIsUsingTLS() {
        System.out.println("isUsingTLS");
        boolean expResult = false;
        boolean result = instance.isUsingTLS();
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class HttpTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");
        Object param = null;
        String expResult = TOOLKIT_SIMULATOR;
        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result.getResponse());
    }

    /**
     * Test of execute method, of class HttpTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String type = "";
        String param = "";
        String expResult = TOOLKIT_SIMULATOR;
        ServiceResponse result = instance.execute(new String[]{type, param});
        assertEquals(expResult, result.getResponse());
    }

    /**
     * Test of execute method, of class HttpTransport.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");
        String type = "";
        Object param = null;
        String expResult = TOOLKIT_SIMULATOR;
        ServiceResponse result = instance.execute(new Object[]{type, param});
        assertEquals(expResult, result.getResponse());
    }

    /**
     * Test of execute method, of class HttpTransport.
     * See testExecute_Object
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

}
