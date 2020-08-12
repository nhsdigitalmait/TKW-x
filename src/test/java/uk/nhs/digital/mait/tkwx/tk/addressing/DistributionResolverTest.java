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
package uk.nhs.digital.mait.tkwx.tk.addressing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author sifa2
 */
public class DistributionResolverTest {

    private static ToolkitSimulator t;
    private DistributionResolver instance;

    public DistributionResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/ITK_AddressRoutingExample/outbound.properties");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new DistributionResolver(t);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of resolve method, of class DistributionResolver.
     */
    @Test
    public void testResolve_String() throws Exception {
        System.out.println("resolve");
        String a = "urn:nhs-uk:addressing:org-a:dept1";
        int expResult = 2;
        RoutingSolution result = instance.resolve(a);
        System.out.println(result.getAddress().getAddress());
        assertEquals(expResult, result.getSolutions().size());
    }

    /**
     * Test of resolve method, of class DistributionResolver.
     */
    @Test
    public void testResolve_String_String() throws Exception {
        System.out.println("resolve");
        String a = "urn:nhs-uk:addressing:org-a:dept1";
        String s = "urn:nhs-itk:services:201005:SendCDADocument-v1-0";
        int expResult = 2;
        RoutingSolution result = instance.resolve(a, s);
        System.out.println(result.getAddress().getAddress());
        assertEquals(expResult, result.getSolutions().size());
    }

}
