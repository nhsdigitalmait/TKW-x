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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.nhs.digital.mait.tkwx.RestartJVMTest;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;

/**
 *
 * @author sifa2
 */
@Category(RestartJVMTest.class)
public class SenderTest {

    public SenderTest() {
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
     * Test of init method, of class Sender.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        ToolkitSimulator simulator = new ToolkitSimulator(System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties");
        SenderRequest sr = new SenderRequest("payload", "wrapper", "address");
        boolean useTls = false;
        String destinationDirectory = "";
        Sender instance = new SenderImpl();
        instance.init(simulator, sr, useTls, destinationDirectory);
    }

    public class SenderImpl implements Sender {

        @Override
        public void init(ToolkitSimulator simulator, SenderRequest sr, boolean useTls, String destinationDirectory) {
        }
    }

}
