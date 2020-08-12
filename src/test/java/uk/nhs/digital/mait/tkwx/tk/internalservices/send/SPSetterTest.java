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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.configurator.ResettablePropertiesConfigurator;

/**
 *
 * @author SIFA2 Encapsulates setting system properties from
 * Configurator/Properties allows execution of lambda on success and log of
 * failure text or exception thrown on failure
 */
public class SPSetterTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private SPSetter instance;
    private ResettablePropertiesConfigurator configurator;
    private Properties p;

    public SPSetterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        // This ensures the required configurator is created (the default is one based on system properties)
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        configurator = (uk.nhs.digital.mait.commonutils.util.configurator.ResettablePropertiesConfigurator) Configurator.getConfigurator();
        p = new Properties();
        p.put("testprop1", "testval1");
        p.put("testprop2", "testval2");
        configurator.setProperties(p);
        //  tksproperty systemproperty warning Consumer
        instance = new SPSetter("testprop1", "systemtestprop1", "Warning text", null);
    }

    @After
    public void tearDown() {
        System.getProperties().remove("systemtestprop1");
        System.getProperties().remove("systemtestprop2");
    }

    /**
     * Test of execute method, of class SPSetter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Configurator() throws Exception {
        System.out.println("execute");

        String expResult = null;
        assertEquals(expResult, System.getProperty("systemtestprop1"));

        instance.execute(configurator);

        expResult = "testval1";
        assertEquals(expResult, System.getProperty("systemtestprop1"));

        instance = new SPSetter("testpropnotthere", "systemtestprop1", "Warning text", null);
        instance.execute(configurator);

        try {
            instance = new SPSetter("testpropnotthere", "systemtestprop1", new Exception("Warning text exception"), null);
            instance.execute(configurator);
            fail("Exception not thrown");
        } catch (Exception ex) {
        }
        instance = new SPSetter("testprop1", "systemtestprop1", "Warning text", st -> {
            System.err.println("lambda on successfully set system prop " + st);
        });
        instance.execute(configurator);
    }

    /**
     * Test of execute method, of class SPSetter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecute_Properties() throws Exception {
        System.out.println("execute");
        instance.execute(p);
        String expResult = "testval1";
        assertEquals(expResult, System.getProperty("systemtestprop1"));
    }

    /**
     * Test of executeSettings method, of class SPSetter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecuteSettings_Properties_StringArrArr() throws Exception {
        System.out.println("executeSettings");
        String[][] settings = new String[][]{
            {"testprop1", "systemtestprop1"},
            {"testprop2", "systemtestprop2"}
        };
        SPSetter.executeSettings(p, settings);
        String expResult = "testval1";
        assertEquals(expResult, System.getProperty("systemtestprop1"));
        expResult = "testval2";
        assertEquals(expResult, System.getProperty("systemtestprop2"));
    }

    /**
     * Test of executeSettings method, of class SPSetter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecuteSettings_Configurator_StringArrArr() throws Exception {
        System.out.println("executeSettings");
        String[][] settings = new String[][]{
            {"testprop1", "systemtestprop1"},
            {"testprop2", "systemtestprop2"}
        };
        SPSetter.executeSettings(configurator, settings);
        String expResult = "testval1";
        assertEquals(expResult, System.getProperty("systemtestprop1"));
        expResult = "testval2";
        assertEquals(expResult, System.getProperty("systemtestprop2"));
    }

    /**
     * Test of executeSettings method, of class SPSetter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecuteSettings_Properties_SPSetterArr() throws Exception {
        System.out.println("executeSettings");
        SPSetter[] settings = new SPSetter[]{
            new SPSetter("testprop1", "systemtestprop1", "Warning text", null),
            new SPSetter("testprop2", "systemtestprop2", "Warning text", null)
        };
        SPSetter.executeSettings(p, settings);
        String expResult = "testval1";
        assertEquals(expResult, System.getProperty("systemtestprop1"));
        expResult = "testval2";
        assertEquals(expResult, System.getProperty("systemtestprop2"));
    }

    /**
     * Test of executeSettings method, of class SPSetter.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExecuteSettings_Configurator_SPSetterArr() throws Exception {
        System.out.println("executeSettings");
        SPSetter[] settings = new SPSetter[]{
            new SPSetter("testprop1", "systemtestprop1", "Warning text", null),
            new SPSetter("testprop2", "systemtestprop2", "Warning text", null)
        };
        SPSetter.executeSettings(configurator, settings);
        String expResult = "testval1";
        assertEquals(expResult, System.getProperty("systemtestprop1"));
        expResult = "testval2";
        assertEquals(expResult, System.getProperty("systemtestprop2"));
    }

}
