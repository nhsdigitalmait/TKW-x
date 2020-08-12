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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sifa2
 */
public class CertDataTest {

    private CertData instance;
    
    public CertDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new CertData();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCertDir method, of class CertData.
     */
    @Test
    public void testGetCertDir() {
        System.out.println("getCertDir");
        String expResult = "certdir";
        instance.setCertDir(expResult);
        String result = instance.getCertDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCertDir method, of class CertData.
     */
    @Test
    public void testSetCertDir() {
        System.out.println("setCertDir");
        String certDir = "";
        instance.setCertDir(certDir);
    }

    /**
     * Test of getKeyDir method, of class CertData.
     */
    @Test
    public void testGetKeyDir() {
        System.out.println("getKeyDir");
        String expResult = "keydir";
        instance.setKeyDir(expResult);
        String result = instance.getKeyDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of setKeyDir method, of class CertData.
     */
    @Test
    public void testSetKeyDir() {
        System.out.println("setKeyDir");
        String keyDir = "";
        instance.setKeyDir(keyDir);
    }

    /**
     * Test of getPassword method, of class CertData.
     */
    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        char[] expResult = null;
        char[] result = instance.getPassword();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of setPassword method, of class CertData.
     */
    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        char[] password = null;
        instance.setPassword(password);
    }

    /**
     * Test of getSubject method, of class CertData.
     */
    @Test
    public void testGetSubject() {
        System.out.println("getSubject");
        String expResult = "subject";
        instance.setSubject(expResult);
        String result = instance.getSubject();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSubject method, of class CertData.
     */
    @Test
    public void testSetSubject() {
        System.out.println("setSubject");
        String subject = "";
        instance.setSubject(subject);
    }

    /**
     * Test of getAlias method, of class CertData.
     */
    @Test
    public void testGetAlias() {
        System.out.println("getAlias");
        String expResult = "alias";
        instance.setAlias(expResult);
        String result = instance.getAlias();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAlias method, of class CertData.
     */
    @Test
    public void testSetAlias() {
        System.out.println("setAlias");
        String alias = "";
        instance.setAlias(alias);
    }

    /**
     * Test of getKey method, of class CertData.
     */
    @Test
    public void testGetKey() {
        System.out.println("getKey");
        RSAPrivateKey expResult = null;
        RSAPrivateKey result = instance.getKey();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCert method, of class CertData.
     */
    @Test
    public void testGetCert() {
        System.out.println("getCert");
        X509Certificate expResult = null;
        X509Certificate result = instance.getCert();
        assertEquals(expResult, result);
    }

    /**
     * Test of loadCerts method, of class CertData.
     */
    @Test
    public void testLoadCerts() {
        System.out.println("loadCerts");
        instance.setCertDir(System.getenv("TKWROOT")+"/contrib/Test_Certificates/TLS_Test_Certificates/Test01/test01.crt");
        instance.loadCerts();
        X509Certificate cert = instance.getCert();
        assertNotNull(cert);
    }

    /**
     * Test of loadKeys method, of class CertData.
     */
    @Test
    public void testLoadKeys() {
        System.out.println("loadKeys");
        instance.setKeyDir(System.getenv("TKWROOT")+"/contrib/Test_Certificates/TLS_Test_Certificates/Test01/test01.pkcs12");
        instance.setPassword("test01tls_moscow".toCharArray());
        instance.setAlias("1");
        instance.loadKeys();
        RSAPrivateKey rsa = instance.getKey();
        assertNotNull(rsa);
    }
    
}
