/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.util.dsig;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD_KEYSTORE;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD_PASSWORD;
import static uk.nhs.digital.mait.tkwx.util.dsig.CryptoProviderFactory.GOOD_USERID;

/**
 *
 * @author simonfarrow
 */
public class CryptoProviderFactoryTest {
    
    public CryptoProviderFactoryTest() {
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
     * Test of getProvider method, of class CryptoProviderFactory.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetProvider() throws Exception {
        System.out.println("getProvider");

        System.setProperty(GOOD_KEYSTORE,System.getenv("TKWROOT") + "/config/GP_CONNECT/certs/tls.jks");
        System.setProperty(GOOD_USERID,"selfsigned");
        System.setProperty(GOOD_PASSWORD,"password");

        String s = GOOD;
        CryptoProviderFactory instance = new CryptoProviderFactory(false);
        CryptoProvider result = instance.getProvider(s);
        assertNotNull(result);
    }
    
}
