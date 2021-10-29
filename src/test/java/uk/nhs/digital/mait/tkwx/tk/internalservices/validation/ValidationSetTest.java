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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import uk.nhs.digital.mait.tkwx.tk.internalservices.SpineValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class ValidationSetTest {

    private ValidationSet instance;

    public ValidationSetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new ValidationSet();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setServiceName method, of class ValidationSet.
     */
    @Test
    public void testSetServiceName() {
        System.out.println("setServiceName");
        String n = "servicename";
        instance.setServiceName(n);
    }

    /**
     * Test of getServiceName method, of class ValidationSet.
     */
    @Test
    public void testGetServiceName() {
        System.out.println("getServiceName");
        String expResult = "sn";
        instance.setServiceName(expResult);
        String result = instance.getServiceName();
        assertEquals(expResult, result);
    }

    /**
     * Test of addValidation method, of class ValidationSet.
     */
    @Test
    public void testAddValidation() {
        System.out.println("addValidation");
        Validation v = new Validation("CHECK");
        instance.addValidation(v);
    }

    /**
     * Test of doSpineValidations method, of class ValidationSet.
     */
    @Test
    public void testDoSpineValidations() throws Exception {
        System.out.println("doSpineValidations");
        SpineMessage sm = new SpineMessage("src/test/resources", "ITK_Trunk.message");
        SpineValidatorService vs = new SpineValidatorService();

        String propertiesFile = System.getenv("TKWROOT") + "/config/SPINE_ITKTrunk_Client/tkwClient-x.properties";
        ToolkitSimulator t = new ToolkitSimulator(propertiesFile);
        Properties p = new Properties();
        // dont forget to load the internal props
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(propertiesFile));
        String s = "";
        vs.boot(t, p, s);

        instance.addValidation(new Validation("CHECK"));

        int expResult = 1;
        ArrayList<ValidationReport> result = instance.doSpineValidations(sm, vs);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of doValidations method, of class ValidationSet.
     */
    @Test
    public void testDoValidations() throws IOException {
        System.out.println("doValidations");
        String o = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));
        boolean stripHeader = false;
        ValidatorService vs = new ValidatorService();
        boolean expResult = false;

        Validation validation = new Validation("SET");
        instance.addValidation(validation);

        ArrayList<ValidationReport> result = instance.doValidations(o, null, stripHeader, vs);
        assertEquals(expResult, result.get(0).getPassed());
    }

    /**
     * Test of getVariable method, of class ValidationSet.
     */
    @Test
    public void testGetVariable() {
        System.out.println("getVariable");
        String s = "v";
        Object expResult = "value";
        instance.setVariable(s, expResult);
        Object result = instance.getVariable(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariable method, of class ValidationSet.
     */
    @Test
    public void testSetVariable() {
        System.out.println("setVariable");
        String s = "";
        Object o = null;
        instance.setVariable(s, o);
    }

    /**
     * Test of setVariableProvider method, of class ValidationSet.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    /**
     * Test of getValidations method, of class ValidationSet.
     */
    @Test
    public void testGetValidations() {
        System.out.println("getValidations");
        ArrayList<Validation> expResult = new ArrayList<>();
        ArrayList<Validation> result = instance.getValidations();
        assertEquals(expResult, result);
    }

}
