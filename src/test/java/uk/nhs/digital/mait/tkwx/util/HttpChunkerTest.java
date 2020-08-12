/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author simonfarrow
 */
public class HttpChunkerTest {

    public HttpChunkerTest() {
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
     * Test of chunk method, of class HttpChunker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testChunk() throws Exception {
        System.out.println("chunk");

        int chunkSize = 3;

        String message = "abc123";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpChunker.chunk(message.getBytes(), chunkSize, os);
        byte[] result = os.toByteArray();
        byte[] expResult = "3\r\nabc\r\n3\r\n123\r\n0\r\n\r\n".getBytes();
        assertArrayEquals(expResult, result);

        message = "";
        os = new ByteArrayOutputStream();
        HttpChunker.chunk(message.getBytes(), chunkSize, os);
        result = os.toByteArray();
        expResult = "0\r\n\r\n".getBytes();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of unchunk method, of class HttpChunker.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUnchunk() throws Exception {
        System.out.println("unchunk");
        int chunkSize = 3;

        for (String expResult : new String[]{"ab", "abc123", "", "abcd123"}) {
            // chunk it
            System.out.println(expResult);
            byte[] message = expResult.getBytes();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HttpChunker.chunk(message, chunkSize, os);

            // unchunk it
            byte[] unchunked = HttpChunker.unchunk(new ByteArrayInputStream(os.toByteArray()));
            assertEquals(expResult, new String(unchunked));
        }
    }
}
