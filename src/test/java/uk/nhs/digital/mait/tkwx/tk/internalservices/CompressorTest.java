/*
  Copyright 2012  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Compressor.CompressionType;

/**
 *
 * @author simonfarrow
 */
public class CompressorTest {

    private static byte[] zippedContent;
    private static byte[] deflatedContent;
    private static final String TESTSTRING = "teststring";
    
    public CompressorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        zippedContent = Compressor.compress(TESTSTRING.getBytes(), CompressionType.COMPRESSION_GZIP);
        deflatedContent = Compressor.compress(TESTSTRING.getBytes(), CompressionType.COMPRESSION_DEFLATE);
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
     * Test of compress method, of class Compressor.
     * @throws java.lang.Exception
     */
    @Test
    public void testCompressGzip() throws Exception {
        System.out.println("compressGzip");
        String uncompressed = TESTSTRING;
        Compressor.CompressionType method = CompressionType.COMPRESSION_GZIP;
        byte[] result = Compressor.compress(uncompressed.getBytes(), method);
        assertTrue(result.length > 0);
    }

    /**
     * Test of uncompress method, of class Compressor.
     * @throws java.lang.Exception
     */
    @Test
    public void testUncompressGzip() throws Exception {
        System.out.println("uncompressGzip");
        byte[] compressed = zippedContent;
        Compressor.CompressionType method = CompressionType.COMPRESSION_GZIP;
        String expResult = TESTSTRING;
        String result = new String(Compressor.uncompress(compressed, method));
        assertEquals(expResult,result);
    }
    
    /**
     * Test of compress method, of class Compressor.
     * @throws java.lang.Exception
     */
    @Test
    public void testCompressDeflater() throws Exception {
        System.out.println("compressDeflater");
        String uncompressed = TESTSTRING;
        Compressor.CompressionType method = CompressionType.COMPRESSION_DEFLATE;
        byte[] result = Compressor.compress(uncompressed.getBytes(), method);
        assertTrue(result.length > 0);
    }

    /**
     * Test of uncompress method, of class Compressor.
     * @throws java.lang.Exception
     */
    @Test
    public void testUncompressDeflater() throws Exception {
        System.out.println("uncompressDeflater");
        byte[] compressed = deflatedContent;
        Compressor.CompressionType method = CompressionType.COMPRESSION_DEFLATE;
        String expResult = TESTSTRING;
        String result = new String(Compressor.uncompress(compressed, method));
        assertEquals(expResult,result);
    }

    /**
     * Test of compress method, of class Compressor.
     */
    @Test
    public void testCompress() throws Exception {
        System.out.println("compress");
    }

    /**
     * Test of uncompress method, of class Compressor.
     */
    @Test
    public void testUncompress() throws Exception {
        System.out.println("uncompress");
    }
}
