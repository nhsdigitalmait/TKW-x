/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.File;
import java.io.FileNotFoundException;
import org.antlr.v4.runtime.misc.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;

/**
 *
 * @author riro
 */
public class LoggingFileOutputStreamTest {

    private LoggingFileOutputStream instance;
    private EvidenceMetaDataHandler evidenceMetaDataHandler;
    private String fileName;
    private static final String TEST_METADATA_FILE = "src/test/resources/test.metadata";

    public LoggingFileOutputStreamTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws FileNotFoundException {
        fileName = TEST_METADATA_FILE;
        instance = new LoggingFileOutputStream(fileName);
        evidenceMetaDataHandler = new EvidenceMetaDataHandler("127.0.0.1", "localhost");

    }

    @After
    public void tearDown() {
        File file = new File(fileName);
        file.delete();
    }

    /**
     * Test of setMetaDataDescription method, of class LoggingFileOutputStream.
     */
    @Test
    public void testSetMetaDataDescription() throws FileNotFoundException {
        System.out.println("setMetaDataDescription");
        String type = "interaction-log";
        String description = "Test Description";
        instance.setMetaDataDescription(type, description);
    }

    /**
     * Test of setMetaDataDocUrl method, of class LoggingFileOutputStream.
     */
    @Test
    public void testSetMetaDataDocUrl() {
        System.out.println("setMetaDataDocUrl");
        String docurl = "Test document location";
        instance.setMetaDataDocUrl(docurl);
    }

    /**
     * Test of setEvidenceMetaDataHandler method, of class
     * LoggingFileOutputStream.
     */
    @Test
    public void testSetEvidenceMetaDataHandler() {
        System.out.println("setEvidenceMetaDataHandler");
        instance.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
    }

    /**
     * Test of write method, of class LoggingFileOutputStream.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        String expResult = "test info";
        instance.write(expResult);
        String result = String.valueOf(Utils.readFile("src/test/resources/test.metadata"));
        assertEquals(expResult, result);

    }

    /**
     * Test of endMainThread method, of class LoggingFileOutputStream.
     */
    @Test
    public void testEndMainThread() {
        System.out.println("endMainThread");
        instance.endMainThread();
    }

    /**
     * Test of getEvidenceMetaDataHandler method, of class
     * LoggingFileOutputStream.
     */
    @Test
    public void testGetEvidenceMetaDataHandler() {
        System.out.println("getEvidenceMetaDataHandler");
        instance.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
        EvidenceMetaDataHandler result = instance.getEvidenceMetaDataHandler();
        assertEquals(evidenceMetaDataHandler, result);
    }

    /**
     * Test of getFileName method, of class LoggingFileOutputStream.
     */
    @Test
    public void testGetFileName() {
        System.out.println("getFileName");
        String expResult = TEST_METADATA_FILE;
        String result = instance.getFileName();
        assertEquals(expResult, result);
    }

    /**
     * Test of logComplete method, of class LoggingFileOutputStream.
     */
    @Test
    public void testLogComplete() {
        System.out.println("logComplete");
        instance.logComplete();
    }

}
