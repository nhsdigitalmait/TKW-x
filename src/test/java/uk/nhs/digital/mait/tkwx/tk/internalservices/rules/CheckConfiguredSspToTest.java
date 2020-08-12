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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author simonfarrow
 */
public class CheckConfiguredSspToTest {

    private CheckConfiguredSspTo instance = null;
    private static final String TEST_PROPS_FILE = "src/test/resources/property.properties";
    private static final String PROVIDER_ROUTING_FILE = "src/test/resources/providerRouting.json";
    private static final String ROUTING_PROPERTY_NAME = "gp.connect.provider.routing.filename";

    public CheckConfiguredSspToTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        try (FileWriter fw = new FileWriter(TEST_PROPS_FILE)) {
            // NB relative to path of parent of props file
            // required to be this way to be acceptable to both demonstrator and tkw
            fw.write(ROUTING_PROPERTY_NAME + " = " + "resources/providerRouting.json");
        }
        try (FileWriter fw = new FileWriter(PROVIDER_ROUTING_FILE)) {
            fw.write("{\n"
                    + "    \"spineProxy\": \"\",\n"
                    + "    \"ASID\": \"918999198993\",\n"
                    + "    \"practices\": [\n"
                    + "        {\n"
                    + "            \"id\": \"1\",\n"
                    + "            \"odsCode\": \"A20047\",\n"
                    + "            \"name\": \"Dr Legg's Surgery\",\n"
                    + "            \"interactionIds\": [\"*\"],\n"
                    + "            \"endpointURL\": \"fhir\",\n"
                    + "            \"apiEndpointURL\": \"api\",\n"
                    + "            \"ASID\": \"918999198993\",\n"
                    + "			\"orgType\": \"GP Practice\"\n"
                    + "        }\n"
                    + "    ]\n"
                    + "}");
        }
    }

    @AfterClass
    public static void tearDownClass() {
        new File(TEST_PROPS_FILE).delete();
        new File(PROVIDER_ROUTING_FILE).delete();
    }

    @Before
    public void setUp() {
        instance = new CheckConfiguredSspTo();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getValue method, of class CheckConfiguredSspTo.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetValue() throws Exception {
        System.out.println("getValue");
        // returns true if they differ
        String[] values = new String[]{"018999198993"};
        String[] args = new String[]{TEST_PROPS_FILE, ROUTING_PROPERTY_NAME};
        boolean expResult = true;
        boolean result = instance.getValue(values, args);
        assertEquals(expResult, result);
    }

}
