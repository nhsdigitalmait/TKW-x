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

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author sifa2
 */
public class SubroutineCheckTest {

    private SubroutineCheck instance;

    public SubroutineCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new SubroutineCheck();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initialise method, of class SubroutineCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitialise() throws Exception {
        System.out.println("initialise");
        instance.setResource("subroutinename");
        // only checks that subroutine name has been set
        instance.initialise();
    }

    /**
     * Test of writeExternalOutput method, of class SubroutineCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWriteExternalOutput() throws Exception {
        System.out.println("writeExternalOutput");
        // does nothing
        String reportDirectory = "";
        instance.writeExternalOutput(reportDirectory);
    }

    /**
     * Test of setType method, of class SubroutineCheck.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        // ignored
        String t = "";
        instance.setType(t);
    }

    /**
     * Test of setResource method, of class SubroutineCheck.
     */
    @Test
    public void testSetResource() {
        System.out.println("setResource");
        String r = "subroutinename";
        instance.setResource(r);
    }

    /**
     * Test of setData method, of class SubroutineCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String d = "/local-name()";
        instance.setData(d);

        // a variable declaration
        d = "$ACTION urn:nhs-itk:services:201005:SendSection5NotificationAccept-v1-0";
        instance.setData(d);
    }

    /**
     * Test of validate method, of class SubroutineCheck.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate_String_boolean() throws Exception {
        System.out.println("validate");
        String o = new String(Files.readAllBytes(
                Paths.get("src/test/resources/get_structured_record.xml")));

        boolean stripHeader = false;

        Properties p = new Properties();
        p.load(new FileReader("src/main/resources/uk/nhs/digital/mait/tkwx/tk/boot/tkw.internal.properties"));
        p.load(new FileReader(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"));
        // substitute TKW_ROOT this will have already been done in the executable
        p = Utils.interpretEnvVars(p);

        ValidatorFactory.getInstance().init(p);

        instance.setResource("check_bundle");
        int expResult = 1;
        ValidatorOutput result = instance.validate(o, null, stripHeader);
        ValidationReport[] reports = result.getReport();
        for ( ValidationReport report : reports ) {
            System.out.println(report.getServiceName()+" annotation: "+report.getAnnotation());
        }
        assertEquals(expResult, result.report.length);
    }

    /**
     * Test of validate method, of class SubroutineCheck.
     *
     * @throws java.lang.Exception
     */
    @Test(expected = Exception.class)
    public void testValidate_SpineMessage() throws Exception {
        System.out.println("validate");
        SpineMessage o = null;
        ValidationReport[] expResult = null;
        ValidationReport[] result = instance.validate(o);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getSupportingData method, of class SubroutineCheck.
     */
    @Test
    public void testGetSupportingData() {
        System.out.println("getSupportingData");
        // hard coded to return null
        String expResult = null;
        String result = instance.getSupportingData();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVariableProvider method, of class SubroutineCheck.
     */
    @Test
    public void testSetVariableProvider() {
        System.out.println("setVariableProvider");
        VariableProvider vp = new VariableProvider() {
            @Override
            public Object getVariable(String s) {
                return "variableValue";
            }

            @Override
            public void setVariable(String name, Object value) {
                System.out.println("Setting variable " + name + " to " + value);
            }
        };
        instance.setVariableProvider(vp);
    }

    /**
     * Test of validate method, of class SubroutineCheck.
     * see testValidate_String_boolean
     */
    @Test
    public void testValidate_3args() throws Exception {
        System.out.println("validate");
    }

}
