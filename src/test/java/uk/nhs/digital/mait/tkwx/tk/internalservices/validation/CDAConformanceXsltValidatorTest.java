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
public class CDAConformanceXsltValidatorTest {

    private CDAConformanceXsltValidator instance;

    public CDAConformanceXsltValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new CDAConformanceXsltValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class CDAConformanceXsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setData("ERROR");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.initialise();
    }

    /**
     * Test of getSupportingData method, of class CDAConformanceXsltValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class CDAConformanceXsltValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate() throws Exception {
        System.out.println("validate");
        String o = new String(Files.readAllBytes(
                Paths.get(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml")));
        boolean stripHeader = false;
        boolean expResult = false;
        instance.setData("ERROR");
        instance.setResource("src/test/resources/testtransform.xsl");
        instance.initialise();
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result.getReport()[0].getPassed());
    }

    /**
     * Test of setVariableProvider method, of class CDAConformanceXsltValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

}
