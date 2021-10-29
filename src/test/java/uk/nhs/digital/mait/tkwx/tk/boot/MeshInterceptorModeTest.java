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
package uk.nhs.digital.mait.tkwx.tk.boot;

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
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class MeshInterceptorModeTest {

    private static ToolkitSimulator t;

    private MeshInterceptorMode instance;

    public MeshInterceptorModeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/FHIR_MESH/tkw-x.properties");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new MeshInterceptorMode();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class MeshInterceptorMode.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance.init(t);
    }

    /**
     * Test of executeService method, of class MeshInterceptorMode.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecuteService() throws Exception {
        System.out.println("executeService");
        String arg = "";
        instance.executeService(arg);
    }

}
