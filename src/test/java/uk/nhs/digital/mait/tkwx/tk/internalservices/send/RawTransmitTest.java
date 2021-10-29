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
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class RawTransmitTest {

    private RawTransmit instance;
    
    public RawTransmitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException {
        instance = new RawTransmit();
        Utils.writeString2File("src/test/resources/out.txt","content");
    }
    
    @After
    public void tearDown() {
        new File("src/test/resources/out.txt").delete();
    }

    /**
     * Test of init method, of class RawTransmit.
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT")+"/config/GP_CONNECT/tkw-x.properties");
        String n = "";
        Properties p = new Properties();
        instance.init(t, n, p);
    }

    /**
     * Test of sendMessage method, of class RawTransmit.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMessage() throws Exception {
        System.out.println("sendMessage");
        ToolkitSimulator t = new ToolkitSimulator(System.getenv("TKWROOT")+"/config/GP_CONNECT/tkw-x.properties");
        
        Mode mode = new TransmitterMode();
        mode.init(t);
        
        String n = "SenderService";
        Properties p = new Properties();
        instance.init(t, n, p);

        // TODO needs a senderservice setting up 
        // Currently it seems to set up an http sender, NB the class under test is not referenced elsewhere
        String dir = "src/test/resources";
        String file = "out.txt";
        String address = "http://localhost:4848";
        boolean expResult = true;
        boolean result = instance.sendMessage(dir, file, address);
        assertEquals(expResult, result);
    }

    /**
     * Test of wrap method, of class RawTransmit.
     * @throws java.lang.Exception
     */
    @Test
    public void testWrap() throws Exception {
        System.out.println("wrap");
        String msg = "msg";
        String expResult = "msg";
        // Raw.wrap does not add anything which makes sense 
        String result = instance.wrap(msg);
        assertEquals(expResult, result);
    }
    
}
