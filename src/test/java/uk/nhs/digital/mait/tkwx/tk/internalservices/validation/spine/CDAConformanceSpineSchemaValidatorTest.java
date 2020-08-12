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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;

/**
 *
 * @author sifa2
 */
public class CDAConformanceSpineSchemaValidatorTest {

    private CDAConformanceSpineSchemaValidator instance;

    public CDAConformanceSpineSchemaValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new CDAConformanceSpineSchemaValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSupportingData method, of class
     * CDAConformanceSpineSchemaValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of initialise method, of class CDAConformanceSpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of validate method, of class CDAConformanceSpineSchemaValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate() throws Exception {
        System.out.println("validate");

        // NB Not sure these apply any more cant find CDA docs in any spine messages
        SpineMessage sm = new SpineMessage(System.getenv("TKWROOT") + "/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/", "QUPA_IN000005UK01_QUPA_IN000007UK01_1902.xml");
        boolean expResult = false;
        instance.schemaFile = System.getenv("TKWROOT") + "//config/SPINE_schema_20120723/omvt/Schemas/QUPC_IN160104UK05.xsd";
        instance.initialise();
        ValidationReport[] result = instance.validate(sm);
        assertEquals(expResult, result[0].getPassed());
    }

    /**
     * Test of setVariableProvider method, of class
     * CDAConformanceSpineSchemaValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }
}
