/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

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
public class ProcessRunnerSubstitutionTest {
    
    private ProcessRunnerSubstitution instance;
    
    public ProcessRunnerSubstitutionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ProcessRunnerSubstitution();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class ProcessRunnerSubstitution.
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        String o = "";
        String expResult = "";
        instance.setData("bash -c date");
        String result = instance.getValue(o);
        assertTrue(result.isEmpty());
    }

    /**
     * Test of getValue method, of class ProcessRunnerSubstitution.
     */
    @Test
    public void testResponseImporter() throws Exception {
        System.out.println("responseImporter");
        String o = "";
        
        instance.setData("java -cp "+System.getenv("TKWROOT")+"/TKW-x.jar uk.nhs.digital.mait.tkwx.tk.boot.ResponseImporter FHIR_BaRS 127.0.0.1");
        String result = instance.getValue(o);

        assertTrue(result.length() == 0);
    }

    /**
     * Test of setData method, of class ProcessRunnerSubstitution.
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String s = "";
        instance.setData(s);
    }
    
}
