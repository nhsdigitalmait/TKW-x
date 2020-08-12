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
package uk.nhs.digital.mait.tkwx.meshinterceptor;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import javax.xml.xpath.XPathExpression;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.tk.boot.MeshTransport;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;

/**
 *
 * @author simonfarrow
 */
public class AbstractMeshRequestHandlerTest {

    private AbstractMeshRequestHandlerImpl instance;

    public AbstractMeshRequestHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new AbstractMeshRequestHandlerImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of extractRequestId method, of class AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractRequestId() throws Exception {
        System.out.println("extractRequestId");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
    }

    /**
     * Test of extractResponseId method, of class AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractResponseId() throws Exception {
        System.out.println("extractResponseId");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
    }

    /**
     * Test of extractEventCode method, of class AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractEventCode() throws Exception {
        System.out.println("extractEventCode");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
    }

    /**
     * Test of extractPriorityCode method, of class AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractPriorityCode() throws Exception {
        System.out.println("extractPriorityCode");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
    }

    /**
     * Test of extractPriorityDisplay method, of class
     * AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractPriorityDisplay() throws Exception {
        System.out.println("extractPriorityDisplay");
        // method not overridden but xpath attribute is set in derived class so this will always fail as an abstract
    }

    /**
     * Test of extractNodeXpath method, of class AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractNodeXpath() throws Exception {
        System.out.println("extractNodeXpath");
        XPathExpression x = getXpathExtractor("/a/text()");
        InputSource m = new InputSource(new ByteArrayInputStream("<a>b</a>".getBytes()));
        String expResult = "b";
        Node result = instance.extractNodeXpath(x, m);
        assertEquals(expResult, result.getTextContent());
    }

    /**
     * Test of extractStringXpath method, of class AbstractMeshRequestHandler.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractStringXpath() throws Exception {
        System.out.println("extractStringXpath");
        XPathExpression x = getXpathExtractor("/a/text()");
        InputSource m = new InputSource(new ByteArrayInputStream("<a>b</a>".getBytes()));
        String expResult = "b";
        String result = instance.extractStringXpath(x, m);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSavedMessagesDirectory method, of class
     * AbstractMeshRequestHandler.
     */
    @Test
    public void testGetSavedMessagesDirectory() {
        System.out.println("getSavedMessagesDirectory");
        String expResult = null;
        String result = instance.getSavedMessagesDirectory();
        assertEquals(expResult, result);
    }

    /**
     * Test of getToolkit method, of class AbstractMeshRequestHandler.
     */
    @Test
    public void testGetToolkit() {
        System.out.println("getToolkit");
        MeshTransport expResult = null;
        MeshTransport result = instance.getToolkit();
        assertEquals(expResult, result);
    }

    public class AbstractMeshRequestHandlerImpl extends AbstractMeshRequestHandler {

        @Override
        public void handle(String meshDirectoryType, Path ctlFile) throws Exception {
        }
    }

}
