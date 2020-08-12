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
package uk.nhs.digital.mait.tkwx.tk.boot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 *
 * @author simonfarrow
 */
public class ApacheServiceTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    
    public ApacheServiceTest() {
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
        exit.expectSystemExitWithStatus(0);

        // calls system exit 
        ApacheService.main(new String[]{"stop"});
    }

    /**
     * Test of main method, of class ApacheService.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = {"start", "-transmit", System.getenv("TKWROOT") + "/config/GP_CONNECT/tkw-x.properties"};
        ApacheService.main(args);
    }

}
