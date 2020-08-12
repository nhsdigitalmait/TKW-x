/*
  Copyright 2012  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 *
 * @author simonfarrow
 */
public class FHIRITKInlineValidatorTest {
    
    public FHIRITKInlineValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of makeResponse method, of class FHIRITKInlineValidator.
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        // This is returning an OperationOutcome with validation results
        // there wont be any substitutions
        HashMap<String,Substitution> substitutions = new HashMap<>();
        HttpRequest httpRequest = new HttpRequest("id");
        byte[] bytes = readFile2String("src/test/resources/slots.json").getBytes();
        httpRequest.setInputStream(new ByteArrayInputStream(bytes));
        httpRequest.setContentLength(bytes.length);
        
        Object obj = httpRequest;
        FHIRITKInlineValidator instance = new FHIRITKInlineValidator();
        String expResult = "<OperationOutcome";
        String result = instance.makeResponse(substitutions, obj);
        assertTrue(result.startsWith(expResult));
    }
    
}
