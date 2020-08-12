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

import java.io.File;
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

/**
 *
 * @author sifa2
 */
public class CDARendererTest {

    private CDARenderer instance;
    private static final String RENDERED_FILE_ID = "1704219734";
    
    public CDARendererTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new CDARenderer();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class CDARenderer.
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.initialise();
    }

    /**
     * Test of setType method, of class CDARenderer.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        // no op
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of writeExternalOutput method, of class CDARenderer.
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        
        String reportDirectory = "src/test/resources";

        File outputLogFile = new File(reportDirectory+"/"+RENDERED_FILE_ID+".html");
        
        instance.initialise();
        SpineMessage sm = new SpineMessage(reportDirectory,"wrapped_cda.xml");
        ValidationReport[] result = instance.validate(sm);

        instance.writeExternalOutput(reportDirectory);
        
        boolean expResult = true;
        assertEquals(expResult,outputLogFile.exists());
        outputLogFile.delete();
    }

    /**
     * Test of setResource method, of class CDARenderer.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        // no op
        String r = "";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class CDARenderer.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        // no op
        String d = "";
        instance.setData(d);
    }

    /**
     * Test of validate method, of class CDARenderer.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        instance.initialise();
        SpineMessage sm = new SpineMessage("src/test/resources","wrapped_cda.xml");
        int expResult = 1;
        ValidationReport[] result = instance.validate(sm);
        assertEquals(expResult, result.length);
        assertEquals(true,result[0].getPassed());
        String expStrResult = "<a href=\"./"+RENDERED_FILE_ID+".html\">Rendered</a>";
        assertEquals(expStrResult,result[0].getDetail());
        expStrResult = "CDA rendered as HTML using standard transform for visual inspection";
        assertEquals(expStrResult,result[0].getTestDetails());
    }

    /**
     * Test of validate method, of class CDARenderer.
     * The method under test is hard coded to throw an exception
     * @throws java.lang.Exception
     */
    @Test(expected=Exception.class)
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        // hard coded to return null;
        String msg = null;
        boolean stripHeader = false;

        ValidatorOutput result = instance.validate(msg, null, stripHeader);
    }

    /**
     * Test of getSupportingData method, of class CDARenderer.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        // hard coded to return null;
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariableProvider method, of class CDARenderer.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = new VariableProvider() {
            @Override
            public Object getVariable(String s) {
                return null;
            }

            @Override
            public void setVariable(String name, Object value) {
            }
        };
        instance.setVariableProvider(vp);
    }

    /**
     * Test of copy method, of class CDARenderer.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        CDARenderer result = (CDARenderer)instance.copy();
        assertNotNull(result);
    }

    /**
     * Test of validate method, of class CDARenderer.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }
    
}
