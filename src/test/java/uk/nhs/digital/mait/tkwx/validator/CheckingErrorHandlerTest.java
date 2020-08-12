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
package uk.nhs.digital.mait.tkwx.validator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.transform.stream.StreamSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 *
 * @author sifa2
 */
public class CheckingErrorHandlerTest {

    private InputSource message;
    private StreamSource schema;
    private CheckingErrorHandler instance;
    private Locator locator;

    public CheckingErrorHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws FileNotFoundException {
            message = new InputSource(new FileInputStream(System.getenv("TKWROOT") + "/contrib/ITK_2_01_Test_Messages/Correspondence/Ambulance/POCD_MT030001UK01_SOAPandDIST_Primary.xml"));
            schema = new StreamSource(new FileInputStream(System.getenv("TKWROOT") + "/contrib/DMS_Schema/Ambulance/Schemas/distributionEnvelope-v2-0.xsd"));
            instance = new CheckingErrorHandler(new SaxValidator(message, schema));
        
        locator = new Locator() {

            @Override
            public String getPublicId() {
                return "publicid";
            }

            @Override
            public String getSystemId() {
                return "systemid";
            }

            @Override
            public int getLineNumber() {
                return 99;
            }

            @Override
            public int getColumnNumber() {
                return 88;
            }
        };
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of error method, of class CheckingErrorHandler.
     */
    @Test
    public void testError() {
        System.out.println("error");
        SAXParseException e = new SAXParseException("error",locator); 
        instance.error(e);
    }

    /**
     * Test of fatalError method, of class CheckingErrorHandler.
     */
    @Test
    public void testFatalError() {
        System.out.println("fatalError");
        SAXParseException e = new SAXParseException("fatal error",locator); 
        instance.fatalError(e);
    }

    /**
     * Test of warning method, of class CheckingErrorHandler.
     */
    @Test
    public void testWarning() {
        System.out.println("warning");
        SAXParseException e = new SAXParseException("warning",locator); 
        instance.warning(e);
    }

}
