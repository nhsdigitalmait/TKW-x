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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.boot.AutoTestMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author sifa2
 */
public class AutoTestServiceTest {

    private AutoTestService instance;

    public AutoTestServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
        for (String filename : new String[]{"src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        instance = new AutoTestService();
        boot();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBootProperties method, of class AutoTestService.
     */
    @Test
    public void testGetBootProperties() {
        System.out.println("getBootProperties");

        String expResult = "HttpTransport";
        Properties result = instance.getBootProperties();
        assertEquals(expResult, result.get("tks.transportlist"));
    }

    /**
     * Test of boot method, of class AutoTestService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBoot() throws Exception {
        System.out.println("boot");
    }

    /**
     * Test of execute method, of class AutoTestService.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Object() throws Exception {
        System.out.println("execute");

        Files.copy(FileSystems.getDefault().getPath(System.getenv("TKWROOT") + "/contrib/TKWAutotestManager/tstp", "patients.tdv"),
                FileSystems.getDefault().getPath("src/test/resources", "test.tdv"), REPLACE_EXISTING);

        Object param = "src/test/resources/test.tst";
        ServiceResponse expResult = null;
        ServiceResponse result = instance.execute(param);
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, of class AutoTestService.
     *
     * @throws java.lang.Exception
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
     * Test of execute method, of class AutoTestService.
     *
     * @throws java.lang.Exception
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
        String propertiesFile = System.getenv("TKWROOT") + "/config/ITK_Autotest/tkw-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        Properties p = new Properties();
        // dont forget to load the internal props
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(propertiesFile));
        String s = "AutoTest";
        instance.boot(t, p, s);
        // this sets up the required services
        new AutoTestMode().init(t);
    }

    /**
     * Test of execute method, of class AutoTestService.see testExecute_Object
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute() throws Exception {
        System.out.println("execute");
    }

}
