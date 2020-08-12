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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;

/**
 *
 * @author sifa2
 */
public class CDATemplateListerTest {

    private CDATemplateLister instance;

    public CDATemplateListerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new CDATemplateLister();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class CDATemplateLister.
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of writeExternalOutput method, of class CDATemplateLister.
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "src/test/resources";
        // no op
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setType method, of class CDATemplateLister.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class CDATemplateLister.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class CDATemplateLister.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of validate method, of class CDATemplateLister.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));
        boolean stripHeader = false;
        boolean expResult = true;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result.getReport()[0].getPassed());
        // This should retuirn html fragments desribing the template list
        System.out.println(result.getReport()[0].getTestDetails());
    }

    /**
     * Test of validate method, of class CDATemplateLister.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = new SpineMessage("src/test/resources", "ITK_Trunk.message");
        boolean expResult = true;
        instance.setType("attachment1_xxx");
        ValidationReport[] result = instance.validate(o);
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of getSupportingData method, of class CDATemplateLister.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariableProvider method, of class CDATemplateLister.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    /**
     * Test of validate method, of class CDATemplateLister.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }

}
