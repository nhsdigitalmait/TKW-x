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
public class ToolkitServiceTest {
    
    public ToolkitServiceTest() {
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
     * Test of boot method, of class ToolkitService.
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
        ToolkitSimulator t = null;
        Properties p = null;
        String s = "";
        ToolkitService instance = new ToolkitServiceImpl();
        instance.boot(t, p, s);
    }

    /**
     * Test of execute method, of class ToolkitService.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");
        Object param = null;
        ToolkitService instance = new ToolkitServiceImpl();
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class ToolkitService.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String type = "";
        String param = "";
        ToolkitService instance = new ToolkitServiceImpl();
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(new String[]{type, param});
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class ToolkitService.
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");
        String type = "";
        Object param = null;
        ToolkitService instance = new ToolkitServiceImpl();
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(new Object[]{type, param});
        assertEquals(expResult, result);
    }

    /**
     * Test of getBootProperties method, of class ToolkitService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        ToolkitService instance = new ToolkitServiceImpl();
        Properties expResult = null;
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result);
    }

    public class ToolkitServiceImpl implements ToolkitService {

        @Override
        public void boot(ToolkitSimulator t, Properties p, String s) throws Exception {
        }

        @Override
        public ServiceResponse execute(Object param) throws Exception {
            return null;
        }

        @Override
        public Properties getBootProperties() {
            return null;
        }
    }

    /**
     * Test of execute method, of class ToolkitService.
     * See testExecute_String_Object
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }
}
