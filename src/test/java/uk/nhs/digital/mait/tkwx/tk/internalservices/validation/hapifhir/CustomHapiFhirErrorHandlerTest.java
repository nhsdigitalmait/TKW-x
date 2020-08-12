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

import ca.uhn.fhir.parser.IParserErrorHandler;
import ca.uhn.fhir.parser.json.JsonLikeValue;
import ca.uhn.fhir.validation.SingleValidationMessage;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class CustomHapiFhirErrorHandlerTest {
    private CustomHapiFhirErrorHandler instance = null;
    
    public CustomHapiFhirErrorHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new CustomHapiFhirErrorHandler();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of containedResourceWithNoId method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testContainedResourceWithNoId() {
        System.out.println("containedResourceWithNoId");
        IParserErrorHandler.IParseLocation theLocation = null;
        instance.containedResourceWithNoId(theLocation);
    }

    /**
     * Test of incorrectJsonType method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testIncorrectJsonType() {
        System.out.println("incorrectJsonType");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theElementName = "element";
        JsonLikeValue.ValueType theExpected = JsonLikeValue.ValueType.ARRAY;
        JsonLikeValue.ScalarType theExpectedScalarType = JsonLikeValue.ScalarType.NUMBER;
        JsonLikeValue.ValueType theFound = JsonLikeValue.ValueType.OBJECT;
        JsonLikeValue.ScalarType theFoundScalarType = JsonLikeValue.ScalarType.STRING;
        instance.incorrectJsonType(theLocation, theElementName, theExpected, theExpectedScalarType, theFound, theFoundScalarType);
        ArrayList<SingleValidationMessage> wm = instance.getWarningMessages();
        int expResult = 1;
        assertEquals(expResult,wm.size());
    }

    /**
     * Test of invalidValue method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testInvalidValue() {
        System.out.println("invalidValue");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theValue = "";
        String theError = "";
        instance.invalidValue(theLocation, theValue, theError);
    }

    /**
     * Test of isErrorOnInvalidValue method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testIsErrorOnInvalidValue() {
        System.out.println("isErrorOnInvalidValue");
        boolean expResult = true;
        boolean result = instance.isErrorOnInvalidValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of missingRequiredElement method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testMissingRequiredElement() {
        System.out.println("missingRequiredElement");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theElementName = "";
        instance.missingRequiredElement(theLocation, theElementName);
    }

    /**
     * Test of setErrorOnInvalidValue method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testSetErrorOnInvalidValue() {
        System.out.println("setErrorOnInvalidValue");
        boolean theErrorOnInvalidValue = false;
        ArrayList<SingleValidationMessage> expResult = new ArrayList<>();
        CustomHapiFhirErrorHandler cehResult = instance.setErrorOnInvalidValue(theErrorOnInvalidValue);
        ArrayList<SingleValidationMessage> result = cehResult.getWarningMessages();
        assertEquals(expResult, result);
    }

    /**
     * Test of unexpectedRepeatingElement method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testUnexpectedRepeatingElement() {
        System.out.println("unexpectedRepeatingElement");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theElementName = "";
        instance.unexpectedRepeatingElement(theLocation, theElementName);
    }

    /**
     * Test of unknownAttribute method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testUnknownAttribute() {
        System.out.println("unknownAttribute");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theElementName = "";
        instance.unknownAttribute(theLocation, theElementName);
    }

    /**
     * Test of unknownElement method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testUnknownElement() {
        System.out.println("unknownElement");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theElementName = "";
        instance.unknownElement(theLocation, theElementName);
    }

    /**
     * Test of unknownReference method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testUnknownReference() {
        System.out.println("unknownReference");
        IParserErrorHandler.IParseLocation theLocation = null;
        String theReference = "";
        instance.unknownReference(theLocation, theReference);
    }

    /**
     * Test of createIncorrectJsonTypeMessage method, of class CustomHapiFhirErrorHandler.
     * The method under test does not appear to be invoked anywhere but here
     */
    @Test
    public void testCreateIncorrectJsonTypeMessage() {
        System.out.println("createIncorrectJsonTypeMessage");
        String theElementName = "element";
        JsonLikeValue.ValueType theExpected = JsonLikeValue.ValueType.ARRAY;
        JsonLikeValue.ScalarType theExpectedScalarType = JsonLikeValue.ScalarType.NUMBER;
        JsonLikeValue.ValueType theFound = JsonLikeValue.ValueType.OBJECT;
        JsonLikeValue.ScalarType theFoundScalarType = JsonLikeValue.ScalarType.STRING;
        String expResult = "Found incorrect type for element element - Expected ARRAY (NUMBER) and found OBJECT (STRING)";
        String result = CustomHapiFhirErrorHandler.createIncorrectJsonTypeMessage(theElementName, theExpected, theExpectedScalarType, theFound, theFoundScalarType);
        assertEquals(expResult, result);
    }

    /**
     * Test of getWarningMessages method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testGetWarningMessages() {
        System.out.println("getWarningMessages");
        ArrayList<SingleValidationMessage> expResult = new ArrayList<>();
        ArrayList<SingleValidationMessage> result = instance.getWarningMessages();
        assertEquals(expResult, result);
    }

    /**
     * Test of resetWarningMessages method, of class CustomHapiFhirErrorHandler.
     */
    @Test
    public void testResetWarningMessages() {
        System.out.println("resetWarningMessages");
        instance.resetWarningMessages();
    }
    
}
