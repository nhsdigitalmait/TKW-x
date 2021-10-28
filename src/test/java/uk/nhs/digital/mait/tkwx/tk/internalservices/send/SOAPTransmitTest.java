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
import java.io.IOException;
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
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class SOAPTransmitTest {

    private SOAPTransmit instance;

    public SOAPTransmitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        instance = new SOAPTransmit();
        Utils.writeString2File("src/test/resources/out.txt","<a>content</a>");
    }

    @After
    public void tearDown() {
        new File("src/test/resources/out.txt").delete();
    }

    /**
     * Test of init method, of class SOAPTransmit.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        String n = "";
        Properties p = new Properties();
        p.put("tks.wrapper.SOAP.template", "src/test/resources/soapwrapper.txt");
        instance.init(t, n, p);
    }

    /**
     * Test of sendMessage method, of class SOAPTransmit.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMessage() throws Exception {
        System.out.println("sendMessage");
        // hard coded to return false
        String dir = "src/test/resources";
        String file = "out.txt";
        String address = "http://localhost:4848";
        boolean expResult = false;
        boolean result = instance.sendMessage(dir, file, address);
        assertEquals(expResult, result);
    }

    /**
     * Test of wrap method, of class
     * @throws java.lang.Exception SOAPTransmit.
     */
    @Test
    public void testWrap() throws Exception {
        System.out.println("wrap");
        String msg = "msg";
        String expResult = null;
        String result = instance.wrap(msg);
        assertEquals(expResult, result);
    }

}
