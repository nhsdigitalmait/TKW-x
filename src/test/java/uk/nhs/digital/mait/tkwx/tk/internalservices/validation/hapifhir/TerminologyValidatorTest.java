/*
 Copyright 2018 Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationReport;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidatorOutput;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.VariableProvider;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author simonfarrow
 */
public class TerminologyValidatorTest {

    private TerminologyValidator instance;

    public TerminologyValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new TerminologyValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class TerminologyValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of setType method, of class TerminologyValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class TerminologyValidator.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class TerminologyValidator.
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
     * Test of validate method, of class TerminologyValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");

        instance.initialise();

        // This is a mesh message dat file with a fhir ITK message bundle in it
        String o = Utils.readFile2String("src/test/resources/DigitalMedicinesTest.dat");
        HashMap<String, Object> extraMessageInfo = new HashMap<>();
        boolean stripHeader = false;
        ValidatorOutput expResult = null;
        ValidatorOutput result = instance.validate(o, extraMessageInfo, stripHeader);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParamsString method, of class TerminologyValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetParamsString() throws Exception {
        System.out.println("getParamsString");
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        params.put("key1", "value1");
        String expResult = "key1=value1&key=value";
        String result = TerminologyValidator.getParamsString(params);
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class TerminologyValidator.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = null;
        ValidationReport[] expResult = null;
        ValidationReport[] result = instance.validate(o);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getSupportingData method, of class TerminologyValidator.
     * Hard coded to return null
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSupportingData() throws Exception {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeExternalOutput method, of class TerminologyValidator.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "rd";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setVariableProvider method, of class TerminologyValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

}
