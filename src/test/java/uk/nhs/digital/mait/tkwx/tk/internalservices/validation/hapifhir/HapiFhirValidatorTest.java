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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import java.util.HashMap;
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

/**
 *
 * @author simonfarrow
 */
public class HapiFhirValidatorTest {

    private HapiFhirValidator instance;
    
    public HapiFhirValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        instance = new HapiFhirValidator();
        instance.initialise();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class HapiFhirValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of setType method, of class HapiFhirValidator.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class HapiFhirValidator.
     * null method
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class HapiFhirValidator.
     * null method
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of validate method, of class HapiFhirValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
        String o = "<OperationOutcome xmlns=\"http://hl7.org/fhir\">"
                    + "<issue>"
                        + "<severity value=\"error\"/>"
                        + "<code value=\"code-invalid\"/>"
                    + "</issue>"
                + "</OperationOutcome>";
        HashMap<String, Object> extraMessageInfo = null;
        boolean stripHeader = false;
        boolean expResult = true;
        ValidatorOutput result = instance.validate(o, extraMessageInfo, stripHeader);
        ValidationReport report = result.getReport()[0];
        System.out.println(report.getDetail());
        System.out.println(report.getTestDetails());
        System.out.println(report.getPassed());
        assertEquals(expResult, report.getPassed());
    }

    /**
     * Test of validate method, of class HapiFhirValidator.
     * @throws java.lang.Exception
     */
    @Test(expected=Exception.class)
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = null;
        ValidationReport[] expResult = null;
        ValidationReport[] result = instance.validate(o);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getSupportingData method, of class HapiFhirValidator.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeExternalOutput method, of class HapiFhirValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setVariableProvider method, of class HapiFhirValidator.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = null;
        instance.setVariableProvider(vp);
    }
}
