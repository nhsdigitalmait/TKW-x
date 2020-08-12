/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.File;
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
public class EvidenceInterfaceTest {
    
    public EvidenceInterfaceTest() {
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
     * Test of setValidationReportFile method, of class EvidenceInterface.
     */
    @Test
    public void testSetValidationReport() {
        System.out.println("setValidationReportFile");
        EvidenceInterface instance = new EvidenceInterfaceImpl();
        instance.setValidationReport("evidence");
    }

    public class EvidenceInterfaceImpl implements EvidenceInterface {

        @Override
        public void setValidationReport(String s) {
        }
    }
}
