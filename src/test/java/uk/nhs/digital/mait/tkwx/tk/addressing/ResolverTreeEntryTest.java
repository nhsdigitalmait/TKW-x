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

import java.util.ArrayList;
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
public class ResolverTreeEntryTest {
    private ResolverTreeEntry instance;
    
    public ResolverTreeEntryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ResolverTreeEntry("a");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of resolve method, of class ResolverTreeEntry.
     */
    @Test
    public void testResolve() throws Exception {
        System.out.println("resolve");
        ArrayList<String> a = new ArrayList<>();
        a.add("a");
        String service = "service";
        int expResult = 1;
        instance.add(a, "", "url", "", "");
        RoutingSolution result = instance.resolve(a, service);
        assertEquals(expResult, result.getSolutions().size());
    }

    /**
     * Test of add method, of class ResolverTreeEntry.
     */
    @Test
    public void testAdd() throws Exception {
        System.out.println("add");
        ArrayList<String> address = new ArrayList<>();
        address.add("a");
        String svc = "service";
        String type = "url";
        String r = "";
        String auth = "";
        instance.add(address, svc, type, r, auth);
    }
    
}
