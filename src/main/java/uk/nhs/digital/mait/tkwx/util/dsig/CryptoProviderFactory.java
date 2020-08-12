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

package uk.nhs.digital.mait.tkwx.util.dsig;
import java.util.HashMap;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
/**
 *
 * @author DAMU2
 */
public class CryptoProviderFactory {
    
    public static final String GOOD_KEYSTORE = "uk.nhs.digital.mait.tkwx.prescriptionsignature.keystore";
    public static final String GOOD_USERID = "uk.nhs.digital.mait.tkwx.prescriptionsignature.userid"; 
    public static final String GOOD_PASSWORD = "uk.nhs.digital.mait.tkwx.prescriptionsignature.password";

    public static final String BADCA_KEYSTORE = "uk.nhs.digital.mait.tkwx.prescriptionsignature.wrongca.keystore";
    public static final String BADCA_USERID = "uk.nhs.digital.mait.tkwx.prescriptionsignature.wrongca.userid"; 
    public static final String BADCA_PASSWORD = "uk.nhs.digital.mait.tkwx.prescriptionsignature.wrongca.password";

    public static final String EXPIRED_KEYSTORE = "uk.nhs.digital.mait.tkwx.prescriptionsignature.expired.keystore";
    public static final String EXPIRED_USERID = "uk.nhs.digital.mait.tkwx.prescriptionsignature.expired.userid"; 
    public static final String EXPIRED_PASSWORD = "uk.nhs.digital.mait.tkwx.prescriptionsignature.expired.password";
   
    public static final String EXPIRED = "E";
    public static final String BAD_CA = "C";
    public static final String BADHASH = "G";
    public static final String BADDIGEST = "H";
    public static final String BADSIGVALUE = "I";
    public static final String BADCERT = "X";
    public static final String GOOD = "S";
    public static final String NO = "U";
    
    private HashMap<String,CryptoProvider> providers = new HashMap<>();
    
    public CryptoProviderFactory(boolean includeBad)
            throws Exception
    {
        String ks = null;
        String u = null;
        String p = null;
        CryptoProvider cp = null;
        Configurator config = Configurator.getConfigurator();
        
        // Good
        ks = config.getConfiguration(GOOD_KEYSTORE);
        u = config.getConfiguration(GOOD_USERID);
        p = config.getConfiguration(GOOD_PASSWORD);
        cp = new CryptoProvider(ks, p, u);
        providers.put(GOOD, cp);
        
        if (!includeBad)
            return;
        
        // Also for "bad scenarios" based on an otherwise good signature
        //
        providers.put(BADHASH, cp);
        providers.put(BADDIGEST, cp);
        providers.put(BADSIGVALUE, cp);
        providers.put(BADCERT, cp);

        // Bad
        ks = config.getConfiguration(BADCA_KEYSTORE);
        if (ks != null) {
            u = config.getConfiguration(BADCA_USERID);
            p = config.getConfiguration(BADCA_PASSWORD);
            cp = new CryptoProvider(ks, p, u);
            providers.put(BAD_CA, cp);
        }
        // Expired
        ks = config.getConfiguration(EXPIRED_KEYSTORE);
        if (ks != null) {
            ks = config.getConfiguration(EXPIRED_KEYSTORE);
            u = config.getConfiguration(EXPIRED_USERID);
            p = config.getConfiguration(EXPIRED_PASSWORD);
            cp = new CryptoProvider(ks, p, u);
            providers.put(EXPIRED, cp);
        }
    }
    
    public CryptoProvider getProvider(String s) { 
        if (s.contentEquals(GOOD)) {
            return providers.get(GOOD); 
        }
        if (s.contentEquals(NO)) {
            return null;
        }
        return providers.get(s);
    }
}
