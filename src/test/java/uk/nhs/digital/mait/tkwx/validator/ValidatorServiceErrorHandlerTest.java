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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.SchemaValidationReporter;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 *
 * @author sifa2
 */
public class ValidatorServiceErrorHandlerTest {

    private ValidatorServiceErrorHandler instance;
    private Locator locator;

    public ValidatorServiceErrorHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new ValidatorServiceErrorHandler(new ValidatorServiceErrorHandlerTest.SchemaValidationReporterImpl());
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
     * Test of error method, of class ValidatorServiceErrorHandler.
     */
    @Test
    public void testError() throws Exception {
        System.out.println("error");
        SAXParseException e = new SAXParseException("error", locator);
        instance.error(e);
    }

    /**
     * Test of fatalError method, of class ValidatorServiceErrorHandler.
     */
    @Test
    public void testFatalError() throws Exception {
        System.out.println("fatalError");
        SAXParseException e = new SAXParseException("fatal error", locator);
        instance.fatalError(e);
    }

    /**
     * Test of warning method, of class ValidatorServiceErrorHandler.
     */
    @Test
    public void testWarning() throws Exception {
        System.out.println("warning");
        SAXParseException e = new SAXParseException("warning", locator);
        instance.warning(e);
    }

    public class SchemaValidationReporterImpl extends SchemaValidationReporter {

        @Override
        public void addValidationExceptionDetail(String s) {
            System.out.println("Adding: [" + s + "]");
        }
    }

}
