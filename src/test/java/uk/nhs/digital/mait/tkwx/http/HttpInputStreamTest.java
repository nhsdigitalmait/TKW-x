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
package uk.nhs.digital.mait.tkwx.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 *
 * @author sifa2
 */
public class HttpInputStreamTest {

    public HttpInputStreamTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        Utils.writeString2File("test.txt","1234567890");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        new File("test.txt").delete();
    }

    /**
     * Test of getContentLength method, of class HttpInputStream.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetContentLength() throws Exception {
        HttpInputStream instance = null;
        System.out.println("getContentLength");
            instance = new HttpInputStream(new FileInputStream("test.txt"), 99);
            int expResult = 99;
            int result = instance.getContentLength();
            assertEquals(expResult, result);
    }

}
