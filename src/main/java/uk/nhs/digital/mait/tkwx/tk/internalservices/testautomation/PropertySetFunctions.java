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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import uk.nhs.digital.mait.jwttools.AuthorisationGenerator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Holder for static functions taking 0..n String params and returning a String
 * suitable for invocation via the function: prefix in a PropertySet/HeaderSet
 *
 * @author simonfarrow
 */
public class PropertySetFunctions {

    // This class not to be instantiated
    private PropertySetFunctions() {
    }

    public static String getNow() {
        return new Date().toString();
    }

    public static String getCWD() {
        return System.getProperty("user.dir");
    }

    public static String hello() {
        System.out.println("hello");
        return "hello";
    }

    public static String hello(String p1, String p2) {
        System.out.println("hello with parms p1=" + p1 + " p2=" + p2);
        return "hello" + p1 + p2;
    }

    /**
     * Used for Setting JWT http authorisation header
     *
     * @param templateFile path to JWT json template file to use
     * @param practitionerID substitution parameter
     * @param nhsNo substitution parameter
     * @param secret string used by hmac algorithm when generating hash
     * @param useBase64URLStr "true" or "false"
     * @param addSignatureStr "true" or "false" whether to append the third part
     * @return Base 64 URL Encoded HttpHeader value for JWT
     * @throws Exception
     */
    public static String getJWT(String templateFile, String practitionerID, String nhsNo, String secret, String useBase64URLStr, String addSignatureStr) throws Exception {
        AuthorisationGenerator authorisationGenerator = new AuthorisationGenerator(templateFile);
        boolean useBase64URL = Boolean.valueOf(useBase64URLStr);
        boolean addSignature = Boolean.valueOf(addSignatureStr);
        // prepend Bearer prefix and apply transform to base64URL format if specified
        return "Bearer " + authorisationGenerator.getAuthorisationString(practitionerID, nhsNo, secret, useBase64URL, addSignature);
    }

    /**
     * Used for Setting JWT http authorisation header
     *
     * @param templateFile path to JWT json template file to use
     * @param practitionerID substitution parameter
     * @param nhsNo substitution parameter
     * @param secret string used by hmac algorithm when generating hash
     * @param useBase64URLStr
     * @return Base 64 URL Encoded HttpHeader value for JWT
     * @throws Exception
     */
    public static String getJWT(String templateFile, String practitionerID, String nhsNo, String secret, String useBase64URLStr) throws Exception {
        AuthorisationGenerator authorisationGenerator = new AuthorisationGenerator(templateFile);
        boolean useBase64URL = Boolean.valueOf(useBase64URLStr);
        // prepend Bearer prefix and apply transform to base64URL format if specified
        return "Bearer " + authorisationGenerator.getAuthorisationString(practitionerID, nhsNo, secret, useBase64URL, false);
    }

    /**
     * Used for Setting JWT http authorisation header
     *
     * @param templateFile path to JWT json template file to use
     * @param practitionerID substitution parameter
     * @param nhsNo substitution parameter
     * @param secret string used by hmac algorithm when generating hash
     * @return Base 64 URL Encoded HttpHeader value for JWT
     * @throws Exception
     */
    public static String getJWT(String templateFile, String practitionerID, String nhsNo, String secret) throws Exception {
        AuthorisationGenerator authorisationGenerator = new AuthorisationGenerator(templateFile);
        boolean useBase64URL = false;
        // prepend Bearer prefix 
        return "Bearer " + authorisationGenerator.getAuthorisationString(practitionerID, nhsNo, secret, useBase64URL, false);
    }

    /**
     * Used for setting uuids
     *
     * @return Lowercase uuid string
     * @throws Exception
     */
    public static String getuuid() throws Exception {
        return java.util.UUID.randomUUID().toString().toLowerCase();
    }

    /**
     * Used for setting uuids
     *
     * @return Uppercase uuid string
     * @throws Exception
     */
    public static String getUUID() throws Exception {
        return getuuid().toUpperCase();
    }

    /**
     *
     * @param format SimpleDateFormat string
     * @param timezone eg GMT
     * @return now formatted as per format string (not url encoded)
     * @throws Exception
     */
    public static String getFormattedTime(String format, String timezone) throws Exception {
        return getFormattedTime(format, timezone, "0", "0", "false");
    }

    /**
     *
     * @param format SimpleDateFormat string
     * @param timezone eg GMT
     * @param daysoffsetStr String containing offset from in seconds
     * @param secondsoffsetStr String containing offset from now in days
     * @return now + offset days and seconds from now (not url encoded)
     * @throws Exception
     */
    public static String getFormattedTime(String format, String timezone, String daysoffsetStr, String secondsoffsetStr) throws Exception {
        return getFormattedTime(format, timezone, daysoffsetStr, secondsoffsetStr, "false");
    }

    /**
     *
     * @param format SimpleDateFormat string
     * @param timezone eg GMT
     * @param daysoffsetStr String containing offset from in seconds
     * @param secondsoffsetStr String containing offset from now in days
     * @param urlEncodeStr boolean whether to url encode the result.
     * @return now + offset days and seconds from now
     * @throws Exception
     */
    public static String getFormattedTime(String format, String timezone, String daysoffsetStr, String secondsoffsetStr, String urlEncodeStr) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone(timezone));

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int daysOfffset = Integer.parseInt(daysoffsetStr);
        int secondsOffset = Integer.parseInt(secondsoffsetStr);
        cal.add(Calendar.DAY_OF_YEAR, daysOfffset);
        cal.add(Calendar.SECOND, secondsOffset);

        boolean urlEncode = Boolean.parseBoolean(urlEncodeStr);
        return urlEncode ? URLEncoder.encode(df.format(cal.getTime()), StandardCharsets.UTF_8.toString()) : df.format(cal.getTime());
    }
    
    /**
     * 
     * @param propertyName
     * @return the required system property value
     */
    public static String getSystemProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        return value == null ? "" : value;
    }
}
