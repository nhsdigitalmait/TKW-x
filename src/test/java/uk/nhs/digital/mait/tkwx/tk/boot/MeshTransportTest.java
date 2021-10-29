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
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.TOOLKIT_SIMULATOR;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class MeshTransportTest {

    private final static String SERVICE = "MeshTransport";
    private ToolkitSimulator t;
    private Properties p;
    private MeshTransport instance;

    public MeshTransportTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        // NB This set of tests expects two MESH mailbox folder structure in the ROOT of the filesystem
        // under /MESH-DATA-HOME
        // as defined in the tkw.properties file
        
        String propsFile = System.getenv("TKWROOT") + "/config/FHIR_MESH/tkw-x.properties";
        t = new ToolkitSimulator(propsFile);
        p = new Properties();
        p.load(new FileReader(propsFile));
        instance = new MeshTransport();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBootProperties method, of class MeshTransport.
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
     * Test of reconfigure method, of class MeshTransport.
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
     * Test of reconfigure method, of class MeshTransport.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = "X";
        String value = "";
        instance.boot(t, p, SERVICE);
        String result = instance.reconfigure(what, value);
    }

    /**
     * Test of boot method, of class MeshTransport.
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
     * Test of execute method, of class MeshTransport.
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
     * Test of execute method, of class MeshTransport.
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
     * Test of execute method, of class MeshTransport.
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
     * Test of execute method, of class MeshTransport.
     * See testExecute_String_Object
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

}
