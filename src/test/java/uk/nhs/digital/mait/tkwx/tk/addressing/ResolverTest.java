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

/**
 *
 * @author sifa2
 */
public class ResolverTest {
    
    public ResolverTest() {
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
     * Test of resolve method, of class Resolver.
     */
    @Test
    public void testResolve_String() throws Exception {
        System.out.println("resolve");
        String address = "http://localhost:4000";
        Resolver instance = new ResolverImpl();
        RoutingSolution expResult = null;
        RoutingSolution result = instance.resolve(address);
        assertEquals(expResult, result);
    }

    /**
     * Test of resolve method, of class Resolver.
     */
    @Test
    public void testResolve_String_String() throws Exception {
        System.out.println("resolve");
        String address = "http://localhost:4000";
        String service = "service";
        Resolver instance = new ResolverImpl();
        RoutingSolution expResult = null;
        RoutingSolution result = instance.resolve(address, service);
        assertEquals(expResult, result);
    }

    public class ResolverImpl implements Resolver {

        public RoutingSolution resolve(String address) throws AddressingException {
            return null;
        }

        public RoutingSolution resolve(String address, String service) throws AddressingException {
            return null;
        }
    }
}
