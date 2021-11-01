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

import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class ServiceManagerTest {

    private ToolkitSimulator t;
    private ServiceManager instance;

    public ServiceManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        instance = ServiceManager.getInstance();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class ServiceManager.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        ServiceManager result = ServiceManager.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of addService method, of class ServiceManager.
     */
    @Test
    public void testAddService() {
        System.out.println("addService");
        String n = "HttpTransport";
        ToolkitService s = new HttpTransport();
        instance.addService(n, s);
    }

    /**
     * Test of getServiceNames method, of class ServiceManager.
     */
    @Test
    public void testGetServiceNames() {
        System.out.println("getServiceNames");
        int expResult = 1;
        Set<String> result = instance.getServiceNames();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getService method, of class ServiceManager.
     */
    @Test
    public void testGetService() {
        System.out.println("getService");
        String n = "HttpTransport";
        ToolkitService s = new HttpTransport();
        instance.addService(n, s);

        ToolkitService result = instance.getService(n);
        assertNotNull(result);
    }

    /**
     * Test of getInstance method, of class ServiceManager.
     */
    @Test
    public void testGetInstance_0args() {
        System.out.println("getInstance");
        ServiceManager result = ServiceManager.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of getInstance method, of class ServiceManager.
     */
    @Test
    public void testGetInstance_ToolkitSimulator() {
        System.out.println("getInstance");
        ServiceManager result = ServiceManager.getInstance(t);
        assertNotNull(result);
    }

    /**
     * Test of getToolkitSimulator method, of class ServiceManager.
     */
    @Test
    public void testGetToolkitSimulator() {
        System.out.println("getToolkitSimulator");
        ServiceManager sresult = ServiceManager.getInstance(t);

        // requires initialising with the call above
        ToolkitSimulator result = ServiceManager.getToolkitSimulator();
        assertNotNull(result);
    }

}
