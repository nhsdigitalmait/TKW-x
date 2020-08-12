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

import java.util.Properties;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sifa2
 */
public class ToolkitSimulatorTest {

    private ToolkitSimulator instance;

    public ToolkitSimulatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getProperties method, of class ToolkitSimulator.
     */
    @Test
    public void testGetProperties() {
        System.out.println("getProperties");
        Properties result = instance.getProperties();
        assertNotNull(result);
    }

    /**
     * Test of getConfigurationName method, of class ToolkitSimulator.
     */
    @Test
    public void testGetConfigurationName() {
        System.out.println("getConfigurationName");
        String expResult = "FHIR_GPCONNECT";
        String result = instance.getConfigurationName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVersion method, of class ToolkitSimulator.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        String expResult = "NHS Digital Interoperability Toolkit";
        String result = instance.getVersion();
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of getOrganisationName method, of class ToolkitSimulator.
     */
    @Test
    public void testGetOrganisationName() {
        System.out.println("getOrganisationName");
        String user = System.getenv("USER");
        String expResult = "__USER_NAME_AND_ORGANISATION_NOT_SET__";
        String result = instance.getOrganisationName();
        assertEquals(expResult, result);
    }

    /**
     * Test of boot method, of class ToolkitSimulator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
        Mode mode = new SimulatorMode();

        // required to avoid missing tks.servicenames property error
        mode.init(instance);

        instance.boot();
        ServiceManager sm = ServiceManager.getInstance();
        Set<String> set = sm.getServiceNames();
        System.out.println("# of services " + set.size());
    }

    /**
     * Test of finalize method, of class ToolkitSimulator.
     */
    @Test
    public void testFinalize() {
        System.out.println("finalize");
        instance.finalize();
    }

    /**
     * Test of main method, of class ToolkitSimulator.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = {"-simulator", System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"};
        ToolkitSimulator.main(args);
    }

    /**
     * Test of startService method, of class ToolkitSimulator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testStartService() throws Exception {
        System.out.println("startService");
        String sname = "HttpTransport";
        instance.startService(sname);
    }

}
