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

package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

/**
 *
 * @author riro
 */
public class CertData {

    private RSAPrivateKey    rsa = null;
    private X509Certificate cert = null;
    private String certDir = null;
    private String keyDir = null;
    private char[] password = null;
    private String subject = null;
    private String alias = null;

    public String getCertDir() {
        return certDir;
    }

    public void setCertDir(String certDir) {
        this.certDir = certDir;
    }

    public String getKeyDir() {
        return keyDir;
    }

    public void setKeyDir(String keyDir) {
        this.keyDir = keyDir;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    public RSAPrivateKey getKey() {
        return rsa;
    }
    public X509Certificate getCert() {
        return cert;
    }

    public void loadCerts() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream(certDir);
            cert = (X509Certificate)cf.generateCertificate(fis);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadKeys() {
        // TODO: Read the key files into PKCS8EncodedKeySpec instances (as byte[])
        // and then use an "RSA" KeyFactory.generatePrivateKey() from the spec to make
        // the keys. Check how we get passwords into the operation. Passwords are "111test"
        // in all cases.
        //
        // NO. ACTUALLY WANT TO USE EncryptedPrivateKeyInfo
        try {            
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(keyDir);
            ks.load(fis, password);
            KeyStore.PasswordProtection p = new KeyStore.PasswordProtection(password);
            rsa = (RSAPrivateKey)(((KeyStore.PrivateKeyEntry)ks.getEntry(alias, p)).getPrivateKey());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
