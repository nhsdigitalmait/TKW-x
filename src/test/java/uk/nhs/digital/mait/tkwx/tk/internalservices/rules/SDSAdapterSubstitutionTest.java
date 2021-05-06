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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;

/**
 *
 * @author simonfarrow
 */
public class SDSAdapterSubstitutionTest {

    private final static String ROOT = System.getenv("TKWROOT") + "/config/GP_CONNECT/simulator_config/responses";
    
    private static final String ORG_SYSTEM = "https://fhir.nhs.uk/Id/ods-organization-code";
    private static final String INT_SYSTEM = "https://fhir.nhs.uk/Id/nhsServiceInteractionId";
    
    private static final String ORG = "B82617";
    private static final String INT = "urn:nhs:names:services:gpconnect:fhir:rest:search:patient-1";

    private final static String DEVICE_CONTEXT_PATH = "/Device?organization="+ORG_SYSTEM+"|"+ORG+"&identifier="+INT_SYSTEM+"|"+INT;
    private final static String ENDPOINT_CONTEXT_PATH = "/Endpoint?organization="+ORG_SYSTEM+"|"+ORG+"&identifier="+INT_SYSTEM+"|"+INT;

    private final static String TAG_PARMS = ROOT + "/sdsdump_opentest.xml " + ROOT + "/sds_device.xsl " + ROOT + "/sds_endpoint.xsl";

    private SDSAdapterSubstitution instance = null;

    public SDSAdapterSubstitutionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new SDSAdapterSubstitution();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getValueDevice method, of class SDSAdapterSubstitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValueDevice() throws Exception {
        System.out.println("getValueDevice");

        String o = DEVICE_CONTEXT_PATH;
        String expResult = "1";
        instance.setData(TAG_PARMS);
        String response = instance.getValue(o);
        String result = xpathExtractor("/fhir:Bundle/fhir:total/@value", response);
        assertEquals(expResult, result);

        expResult = "Device";
        result = xpathExtractor("local-name(/fhir:Bundle/fhir:entry[1]/fhir:resource/*[1])", response);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValueDevice method, of class SDSAdapterSubstitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValueEndpoint() throws Exception {
        System.out.println("getValueEndpoint");

        String o = ENDPOINT_CONTEXT_PATH;
        String expResult = "1";
        instance.setData(TAG_PARMS);
        String response = instance.getValue(o);
        String result = xpathExtractor("/fhir:Bundle/fhir:total/@value", response);
        assertEquals(expResult, result);

        expResult = "Endpoint";
        result = xpathExtractor("local-name(/fhir:Bundle/fhir:entry[1]/fhir:resource/*[1])", response);
        assertEquals(expResult, result);
    }

    /**
     * Test of setData method, of class SDSAdapterSubstitution.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetData() throws Exception {
        System.out.println("setData");
        String s = TAG_PARMS;
        instance.setData(s);
    }

}
