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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class AbstractWrapperTest {

    private AbstractWrapperImpl instance;

    public AbstractWrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new AbstractWrapperImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class AbstractWrapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        String n = "";
        Properties p = new Properties();
        p.load(new FileReader(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"));
        instance.init(t, n, p);
    }

    /**
     * Test of sendMessage method, of class AbstractWrapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMessage() throws Exception {
        System.out.println("sendMessage");
        String dir = "";
        String file = "";
        String address = "";
        boolean expResult = false;
        boolean result = instance.sendMessage(dir, file, address);
        assertEquals(expResult, result);
    }

    /**
     * Test of wrap method, of class AbstractWrapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrap() throws Exception {
        System.out.println("wrap");
        String msg = "";
        String expResult = "";
        String result = instance.wrap(msg);
        assertEquals(expResult, result);
    }

    /**
     * Test of doSender method, of class AbstractWrapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoSender() throws Exception {
        System.out.println("doSender");
        String msg = "";
        String addr = "";
        String ofile = "";
        int chunkSize = 0;
        boolean expResult = false;
        boolean result = instance.doSender(msg, addr, ofile, chunkSize);
        assertEquals(expResult, result);
    }


    /**
     * Test of signDistributionEnvelope method, of class AbstractWrapper.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testSignDistributionEnvelope() throws Exception {
        System.out.println("signDistributionEnvelope");
        String de = new String(Files.readAllBytes(Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence_DE/Ambulance/POCD_MT030001UK01_DIST_Primary.xml")));
        String expResult = "";
        String result = instance.signDistributionEnvelope(de);
        assertEquals(expResult, result);
    }

    public class AbstractWrapperImpl extends AbstractWrapper {

        @Override
        public boolean sendMessage(String dir, String file, String address) throws Exception {
            return false;
        }

        @Override
        public String wrap(String msg) throws Exception {
            return "";
        }
    }

}
