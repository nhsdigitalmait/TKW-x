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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_REQUEST_MARKER;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;

/**
 *
 * @author sifa2
 */
public class AbstractSynchronousPassFailCheckTest {
    
    public AbstractSynchronousPassFailCheckTest() {
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
     * Test of getResponseBody method, of class AbstractSynchronousPassFailCheck.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetResponseBody() throws Exception {
        System.out.println("getResponseBody");
        InputStream in = new ByteArrayInputStream((END_REQUEST_MARKER+"\r\n\r\nHTTP/1.1 200\r\n\r\nBody").getBytes());
        AbstractSynchronousPassFailCheck instance = new AbstractSynchronousPassFailCheckImpl();
        String expResult = "Body";
        String result = instance.getResponseBody(in);
        assertEquals(expResult, result);
    }

    public class AbstractSynchronousPassFailCheckImpl extends AbstractSynchronousPassFailCheck {

        @Override
        public TestResult passed(Script s, InputStream in, InputStream inRequest) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
