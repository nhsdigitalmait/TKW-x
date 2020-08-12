/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;

/**
 *
 * @author simonfarrow
 */
public class GenericInlineFHIRValidatorTest {

    private GenericInlineFHIRValidator instance;

    public GenericInlineFHIRValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new GenericInlineFHIRValidator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of makeResponse method, of class GenericInlineFHIRValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String, Substitution> substitutions = new HashMap<>();
        Object obj = new HttpRequest("");
        String expResult = "<OperationOutcom";
        String result = instance.makeResponse(substitutions, obj);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of setValidationReport method, of class GenericInlineFHIRValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetValidationReport() throws Exception {
        System.out.println("setValidationReport");
        String s = "";
        instance.setValidationReport(s);
    }

}
