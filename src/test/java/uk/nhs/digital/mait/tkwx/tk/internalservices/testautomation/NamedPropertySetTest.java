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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.BasicMessageIdCorrelatorTest.TKWROOT;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.configurator.SingleSetPropertiesConfigurator;
import static uk.nhs.digital.mait.tkwx.util.Utils.copyFile;

/**
 *
 * @author sifa2
 */
public class NamedPropertySetTest {

    private static AutotestParser.NamedPropertySetContext namedPropertySetCtx;

    private NamedPropertySet instance;
    private Properties base;

    public NamedPropertySetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        copyFile(TKWROOT + "/contrib/TKWAutotestManager/tstp/patients.tdv", "src/test/resources/test.tdv");
        TestVisitor visitor = new TestVisitor();
        namedPropertySetCtx = visitor.getNamedPropertySetContext();
    }

    @AfterClass
    public static void tearDownClass() {
        for (String filename : new String[]{"src/test/resources/test.tdv","src/test/resources/test.tdv.backup"}) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        instance = new NamedPropertySet(namedPropertySetCtx);
        base = new Properties();
        base.setProperty("prop1", "basevalue1");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of updateBase method, of class NamedPropertySet.
     */
    @Test
    public void testUpdateBase() {
        System.out.println("updateBase");
        String p = "prop1";
        String v = "value2";
        instance.updateBase(p, v);
    }

    /**
     * Test of getName method, of class NamedPropertySet.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "breaksignature";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of resetBase method, of class NamedPropertySet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testResetBase_Properties() throws Exception {
        System.out.println("resetBase");
        instance = new NamedPropertySet(base);

        base.setProperty("prop1", "value2");
        String expResult = "value2";
        String result = base.getProperty("prop1");
        assertEquals(expResult, result);

        instance.resetBase(base);
        expResult = "basevalue1";
        result = base.getProperty("prop1");
        assertEquals(expResult, result);
    }

    /**
     * Test of isBase method, of class NamedPropertySet.
     */
    @Test
    public void testIsBase() throws Exception {
        System.out.println("isBase");
        boolean expResult = false;
        boolean result = instance.isBase();
        assertEquals(expResult, result);

        expResult = true;
        instance = new NamedPropertySet(base);
        result = instance.isBase();
        assertEquals(expResult, result);
    }

    /**
     * Test of resetBase method, of class NamedPropertySet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testResetBase_Configurator() throws Exception {
        System.out.println("resetBase");
        Configurator c = SingleSetPropertiesConfigurator.getConfigurator();
        instance = new NamedPropertySet(new Properties());
        instance.resetBase(c);
    }

    /**
     * Test of update method, of class NamedPropertySet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdate_Properties() throws Exception {
        System.out.println("update");
        Properties p = new Properties();
        instance.update(p);
    }

    /**
     * Test of update method, of class NamedPropertySet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdate_Configurator() throws Exception {
        System.out.println("update");
        Configurator c = SingleSetPropertiesConfigurator.getConfigurator();
        instance.update(c);
    }

    /**
     * Test of link method, of class NamedPropertySet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLink() throws Exception {
        System.out.println("link");
        Properties props = new Properties();
        props.load(new FileReader(TKWROOT + "/config/ITK_Autotest/tkw-x.properties"));
        ScriptParser p = new ScriptParser(props);
        instance.link(p);
    }

}
