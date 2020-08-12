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
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.RESPONSE_FILE_HEADER_MARKER;

/**
 *
 * @author sifa2
 */
public class SecondAsynchronousResponseBodyExtractorTest {
    
    public SecondAsynchronousResponseBodyExtractorTest() {
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
     * Test of getBody method, of class SecondAsynchronousResponseBodyExtractor.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBody() throws Exception {
        System.out.println("getBody");
        String expResult = "stuff\r\n";
        InputStream in = new ByteArrayInputStream((RESPONSE_FILE_HEADER_MARKER+"\r\n"+RESPONSE_FILE_HEADER_MARKER+"\r\n\r\n"+expResult).getBytes());
        SecondAsynchronousResponseBodyExtractor instance = new SecondAsynchronousResponseBodyExtractor();
        String result = instance.getBody(in);
        assertEquals(expResult, result);
    }
    
}
