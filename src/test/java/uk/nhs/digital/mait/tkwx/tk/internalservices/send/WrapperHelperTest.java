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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

/**
 *
 * @author sifa2
 */
public class WrapperHelperTest {
    @Rule
    public final RestoreSystemProperties restoreSystemProperties
            = new RestoreSystemProperties();

    private WrapperHelper instance;

    public WrapperHelperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.setProperty("uk.nhs.digital.mait.tkwx.spine.sds.reference", "src/test/resources/yea_s2_int.xml");
        instance = WrapperHelper.getInstance();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class WrapperHelper.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        WrapperHelper result = WrapperHelper.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of getVirtualSDSEntry method, of class WrapperHelper.
     */
    @Test
    public void testGetVirtualSDSEntry() {
        System.out.println("getVirtualSDSEntry");
        // this searches records having empty cpaid
        String asid = "655159266510";
        String interactionid = "QUPC_IN180000UK04";
        boolean service = false;
        HashMap<String, String> result = instance.getVirtualSDSEntry(asid, interactionid, service);
        assertNotNull(result);
        String expResult = "YEA";
        assertEquals(expResult, result.get("nacs"));
        assertEquals(asid, result.get("asid"));
        assertEquals(interactionid, result.get("interaction"));
    }

    /**
     * Test of getSDSInteractionEntry method, of class WrapperHelper.
     */
    @Test
    public void testGetSDSInteractionEntry() {
        System.out.println("getSDSInteractionEntry");
        // populated cpaid
        String asid = "976551373518";
        String interactionid = "REPC_IN130002UK01";
        HashMap<String, String> result = instance.getSDSInteractionEntry(asid, interactionid);
        assertNotNull(result);
        String expResult = "YEA";
        assertEquals(expResult, result.get("nacs"));
        assertEquals(asid, result.get("asid"));
        assertEquals(interactionid, result.get("interaction"));
    }

    /**
     * Test of getMessageHeaderTemplate method, of class WrapperHelper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetMessageHeaderTemplate() throws Exception {
        System.out.println("getMessageHeaderTemplate");
        System.setProperty("uk.nhs.digital.mait.tkwx.spine.message.headertemplate", "src/test/resources/soapwrapper.txt");
        String expResult = "text"+System.lineSeparator();
        StringBuilder result = instance.getMessageHeaderTemplate();
        assertEquals(expResult, result.toString());
    }

    /**
     * Test of getWebServiceHeaderTemplate method, of class WrapperHelper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetWebServiceHeaderTemplate() throws Exception {
        System.out.println("getWebServiceHeaderTemplate");
        System.setProperty("uk.nhs.digital.mait.tkwx.spine.webservice.headertemplate", "src/test/resources/soapwrapper.txt");
        String expResult = "text"+System.lineSeparator();
        StringBuilder result = instance.getWebServiceHeaderTemplate();
        assertEquals(expResult, result.toString());
    }

    /**
     * Test of resolveServiceEndpoint method, of class WrapperHelper.
     */
    @Test
    public void testResolveServiceEndpoint() {
        System.out.println("resolveServiceEndpoint");
        String soapaction = "urn:nhs:names:services:sds/REPC_IN130002UK01";
        String toParty = "YEA-801248";
        String expResult = "https://10.239.14.26/reliablemessaging/reliablerequest";
        String result = instance.resolveServiceEndpoint(soapaction, toParty);
        assertEquals(expResult, result);
    }

}
