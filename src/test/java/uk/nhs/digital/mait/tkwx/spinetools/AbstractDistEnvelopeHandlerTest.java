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
package uk.nhs.digital.mait.tkwx.spinetools;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;

/**
 *
 * @author simonfarrow
 */
public class AbstractDistEnvelopeHandlerTest {

    private AbstractDistEnvelopeHandlerImpl instance;
    
    public AbstractDistEnvelopeHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        instance = new AbstractDistEnvelopeHandlerImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of send method, of class AbstractDistEnvelopeHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testSend() throws Exception {
        System.out.println("send");
        DistributionEnvelope incoming = DistributionEnvelope.newInstance();
        DistributionEnvelope outgoingDE = DistributionEnvelope.newInstance();
        // TODO
        //instance.send(incoming, outgoingDE);
    }

    /**
     * Test of substitute method, of class AbstractDistEnvelopeHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testSubstitute() throws Exception {
        System.out.println("substitute");
        StringBuilder sb = new StringBuilder("aaa __X__ bb");
        String tag = "__X__";
        String value = "hello";
        instance.substitute(sb, tag, value);
        String expResult = "aaa hello bb";
        assertEquals(expResult,sb.toString());
    }

    /**
     * Test of getFileSafeMessageID method, of class AbstractDistEnvelopeHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFileSafeMessageID() throws Exception {
        System.out.println("getFileSafeMessageID");
        String s = "a:b";
        String expResult = "a_b";
        String result = instance.getFileSafeMessageID(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of readTemplate method, of class AbstractDistEnvelopeHandler.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadTemplate() throws Exception {
        System.out.println("readTemplate");
        String tname = "business-ack-payload-template.txt";
        String expResult = "<hl7";
        StringBuilder result = instance.readTemplate(tname);
        assertTrue(result.toString().startsWith(expResult));
    }

    public class AbstractDistEnvelopeHandlerImpl extends AbstractDistEnvelopeHandler {

        public AbstractDistEnvelopeHandlerImpl() throws Exception {
            super();
        }
    }

}
