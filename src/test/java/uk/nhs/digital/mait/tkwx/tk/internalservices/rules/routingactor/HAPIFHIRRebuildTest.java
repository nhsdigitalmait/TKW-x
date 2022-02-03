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
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.SimulatorMode;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.tkwx.tk.boot.Mode;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;

/**
 *
 * @author simonfarrow
 */
@Category(RestartJVMTest.class)
public class HAPIFHIRRebuildTest {
    
    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();


    
    public HAPIFHIRRebuildTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        // This required to make a RuleSErvice vailable via the service mgr
        // hack to avoid cast error
        System.setProperty(ORG_CONFIGURATOR, ORG_RESETTABLE_PROPERTIES_CONFIGURATOR);
        String propertiesFile = System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties";

        ToolkitSimulator tks = new ToolkitSimulator(propertiesFile);
        Mode mode = new SimulatorMode();
        mode.init(tks);
        ServiceManager.getInstance(tks);
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
     * Test of makeResponse method, of class HAPIFHIRRebuild.
     * @throws java.lang.Exception
     */
    @Test
    public void testMakeResponse() throws Exception {
        System.out.println("makeResponse");
        HashMap<String, Substitution> substitutions = new HashMap<>();
        Object obj = "body";
        HAPIFHIRRebuild instance = new HAPIFHIRRebuild();
        String expResult = "<OperationOutcome";
        String result = instance.makeResponse(substitutions, obj);
        assertTrue(result.startsWith(expResult));
    }
}
