/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.handlers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;
import uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants;

/**
 *
 * @author riro
 */
public class EvidenceMetaDataHandlerTest {

    EvidenceMetaDataHandler instance;
    
    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();


    public EvidenceMetaDataHandlerTest() {
    }

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUpClass() {
        System.setProperty(PropertyNameConstants.EVIDENCE_METADATA_LOCATION_PROPERTY, folder.getRoot().getAbsolutePath());
        System.setProperty(PropertyNameConstants.GENERATE_EVIDENCE_METADATA_PROPERTY, "y");
        System.setProperty("tks.configname", "TEST CONFIG");
        System.setProperty("tks.environment", "TEST ENVIRONMENT");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new EvidenceMetaDataHandler("127.0.0.1", "localhost");
    }

    @After
    public void tearDown() {

    }

    /**
     * Test of addMetaData method, of class EvidenceMetaDataHandler.
     */
    @Test
    public void testAddMetaData() {
        System.out.println("addMetaData");
        String type = "interactoin-log";
        String description = "Test description";
        String docurl = "test/document/location";
        String fileLocation = "test/file/location";
        instance.addMetaData(type, description, docurl, fileLocation);
    }

    /**
     * Test of addNote method, of class EvidenceMetaDataHandler.
     */
    @Test
    public void testAddNote() {
        System.out.println("addNote");
        String note = "Test notes";
        instance.addNote(note);
    }

    /**
     * Test of mainThreadEnded method, of class EvidenceMetaDataHandler.
     */
    @Test
    public void testMainThreadEnded() {
        System.out.println("mainThreadEnded");
        instance.mainThreadEnded();
    }

    /**
     * Test of subThreadStarted method, of class EvidenceMetaDataHandler.
     */
    @Test
    public void testSubThreadStarted() {
        System.out.println("subThreadStarted");
        String id = "123456789";
        instance.subThreadStarted(id);
     }

    /**
     * Test of subThreadEnded method, of class EvidenceMetaDataHandler.
     */
    @Test
    public void testSubThreadEnded() {
        System.out.println("subThreadEnded");
        String id = "123456789";
        instance.subThreadStarted(id);
        instance.subThreadEnded(id);
    }

}
