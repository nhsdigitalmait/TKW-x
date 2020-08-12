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
public class ValidationCheckTest {

    private ValidationCheckImpl instance;

    public ValidationCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new ValidationCheckImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class ValidationCheck.
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of setType method, of class ValidationCheck.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class ValidationCheck.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class ValidationCheck.
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of validate method, of class ValidationCheck.
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = "";
        boolean stripHeader = false;
        ValidatorOutput expResult = null;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class ValidationCheck.
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = null;
        ValidationReport[] expResult = null;
        ValidationReport[] result = instance.validate(o);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getSupportingData method, of class ValidationCheck.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = "";
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeExternalOutput method, of class ValidationCheck.
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setVariableProvider method, of class ValidationCheck.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }

    public class ValidationCheckImpl implements ValidationCheck {

        @Override
        public void initialise() throws Exception {
        }

        @Override
        public void setType(String t) {
        }

        @Override
        public void setResource(String r) {
        }

        @Override
        public void setData(String d) throws Exception {
        }

        @Override
        public ValidatorOutput validate(String o, HashMap<String, Object> extraMessageInfo, boolean stripHeader) throws Exception {
            return null;
        }

        @Override
        public ValidationReport[] validate(SpineMessage o) throws Exception {
            return null;
        }

        @Override
        public String getSupportingData() {
            return "";
        }

        @Override
        public void writeExternalOutput(String reportDirectory) throws Exception {
        }

        @Override
        public void setVariableProvider(VariableProvider vp) {
        }
    }

    /**
     * Test of validate method, of class ValidationCheck.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }
}