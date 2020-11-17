/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.httpinterceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.CONTENT_LENGTH_HEADER;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 *
 * @author riro
 */
public class CDSSFHIRUnpackerTest {
    
    public CDSSFHIRUnpackerTest() {
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
     * Test of unpack method, of class CDSSFHIRUnpacker.
     */
    @Test
    public void testUnpack() throws Exception {
        System.out.println("unpack");
        HttpRequest httpRequest = new HttpRequest("");
        // fails with npe if no remote address
        httpRequest.setRemoteAddress(InetAddress.getByName("127.0.0.1"));
        httpRequest.setRequestContext("/");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        ZipEntry zipEntry = new ZipEntry("entry1");
        
        // This is a tld the code 0x0707 represents full url field id 
        // The code under test breaks if there is no entry
        // the Shorts are byte reversed
        // This is 1 byte long and is a letter A
        zipEntry.setExtra(new byte[]{7,7,1,0,65});
        zipOut.putNextEntry(zipEntry);
        
        // This is actually a fhir json response but ho hum
        String json= readFile2String("src/test/resources/slots.json");
        zipOut.write(json.getBytes());
        zipOut.close();
        bos.close();
        
        httpRequest.setInputStream(new ByteArrayInputStream(bos.toByteArray()));
        httpRequest.setContentLength(bos.toByteArray().length);
        httpRequest.setHeader(CONTENT_LENGTH_HEADER.toLowerCase(), Integer.toString(bos.toByteArray().length));
        String expResult = "Bundle";
        HttpRequest result = CDSSFHIRUnpacker.unpack(httpRequest);
        // This checks that its well formed xml and also that the root node is correct.
        assertEquals(expResult, XPathManager.xpathExtractor("local-name(/*)",new String(result.getBody())));


    }
    
}
