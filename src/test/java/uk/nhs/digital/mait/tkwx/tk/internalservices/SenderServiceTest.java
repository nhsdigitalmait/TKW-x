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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;

/**
 *
 * @author sifa2
 */
public class SenderServiceTest {

    private SenderService instance;

    public SenderServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new SenderService();
        boot();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBootProperties method, of class SenderService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");
        String expResult = "uk.nhs.digital.mait.tkwx.tk.internalservices.SpineSenderService";
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result.get("tks.classname.SpineSender"));
    }

    /**
     * Test of reconfigure method, of class SenderService.
     */
    @Test
    public void testReconfigure_Properties() throws Exception {
        System.out.println("reconfigure");
        Properties p = instance.getBootProperties();
        instance.reconfigure(p);
    }

    /**
     * Test of reconfigure method, of class SenderService.
     */
    @Test
    public void testReconfigure_String_String() throws Exception {
        System.out.println("reconfigure");
        String what = ReconfigureTags.DESTINATION_DIRECTORY;
        String value = "newfolder";
        String expResult = System.getenv("TKWROOT") + "/config/GP_CONNECT/transmitter_sent_messages";
        String result = instance.reconfigure(what, value);
        assertTrue(expResult.equalsIgnoreCase(result));
    }

    /**
     * Test of boot method, of class SenderService.
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
    }

    /**
     * Test of execute method, of class SenderService.
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");
        SenderRequest senderRequest = new SenderRequest("payload", "wrapper_template", "address");
        Object param = senderRequest;
        int expResult = 0;
        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class SenderService.
     */
    @Test
    public void testExecute_String_String() throws Exception {
        System.out.println("execute");
        String type = "";
        String param = "";
        int expResult = 200;
        ServiceResponse result = instance.execute(new String[]{type, param});
        assertEquals(expResult, result.getCode());
    }

    /**
     * Test of execute method, of class SenderService.
     */
    @Test
    public void testExecute_String_Object() throws Exception {
        System.out.println("execute");
        String type = "";
        Object param = null;
        int expResult = 200;
        ServiceResponse result = instance.execute(new Object[]{type, param});
        assertEquals(expResult, result.getCode());
    }

    private void boot() throws IOException, Exception {
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        Properties p = new Properties();
        // dont forget to load the internal props
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(propertiesFile));
        String s = "";
        instance.boot(t, p, s);
    }

    /**
     * Test of execute method, of class SenderService.
     * see testExecute_Object
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

}
