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
package uk.nhs.digital.mait.tkwx.tk;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import uk.nhs.digital.mait.tkwx.tk.addressing.AddressingSuite;
import uk.nhs.digital.mait.tkwx.tk.boot.BootSuite;
import uk.nhs.digital.mait.tkwx.tk.handlers.HandlersSuite;
import uk.nhs.digital.mait.tkwx.tk.internalservices.InternalservicesSuite;

/**
 *
 * @author sifa2
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({InternalservicesSuite.class, AddressingSuite.class, HandlersSuite.class, BootSuite.class})
public class TkSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
