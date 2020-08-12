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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import static java.util.logging.Level.SEVERE;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * parses JWT header strings
 *
 * @author simonfarrow
 */
public class JWTParser {

    private String jsonPayload;
    private String xmlPayload;
    private String jsonHeader;
    private String xmlHeader;
    private byte[] signature;
    private String[] b64Blocks;
    private String jwt;
    private boolean hasNoPadding;
    private boolean isURLEncoded;

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final static int HEADER_INDEX = 0;
    private final static int PAYLOAD_INDEX = 1;
    private final static int SIGNATURE_INDEX = 2;

    private static String lastJWT = null;

    /**
     * public constructor
     *
     * @param b64 base64 dot separated header
     * @throws Exception
     */
    public JWTParser(String b64) throws Exception {
        jsonPayload = null;
        xmlPayload = null;
        jsonHeader = null;
        signature = null;
        hasNoPadding = true;
        isURLEncoded = true;
        processJWT(b64);
    }

    /**
     * @param jwt string containing the encoded jwt
     * @throws Exception
     */
    private void processJWT(String aJwt) throws Exception {
        if (aJwt != null) {
            jwt = aJwt;
            // TODO how to communicate failures back to the caller
            // JWT Spec RFC7519 https://www.rfc-editor.org/rfc/rfc7519.txt says we must use base64 URL encoding as defined in the 
            // JWS Spec RFC 7515 https://www.rfc-editor.org/rfc/rfc7515.txt this states padding must *not* be used
            Decoder decoder = Base64.getUrlDecoder();

            // RFC6750 defines an Authoriization field of the form Bearer <token>
            jwt = jwt.replaceFirst("^Bearer +", "");
            b64Blocks = jwt.split("\\.");
            // three b64 blocks separated by a period
            // header, payload and signature
            switch (b64Blocks.length) {
                case 3:
                    checkBase64URLWithoutPadding(b64Blocks[SIGNATURE_INDEX]);
                    signature = decoder.decode(b64Blocks[SIGNATURE_INDEX].getBytes());
                case 2:
                    checkBase64URLWithoutPadding(b64Blocks[HEADER_INDEX]);
                    jsonHeader = new String(decoder.decode(b64Blocks[HEADER_INDEX].getBytes()));
                    checkBase64URLWithoutPadding(b64Blocks[PAYLOAD_INDEX]);
                    jsonPayload = new String(decoder.decode(b64Blocks[PAYLOAD_INDEX].getBytes()));
                    break;
                default:
                    Logger.getInstance().log(SEVERE, JWTParser.class.getName(), "Error splitting JWT base64 expected 2 or 3 blocks got " + b64Blocks.length);
            }
            if (2 <= b64Blocks.length && b64Blocks.length <= 3) {
                xmlPayload = JsonXmlConverter.jsonToXmlString(jsonPayload.toCharArray());
                xmlHeader = JsonXmlConverter.jsonToXmlString(jsonHeader.toCharArray());
            }
        } // header is populated
    }

    /**
     * @return the jwt_json_payload
     */
    public String getJsonPayload() {
        return jsonPayload;
    }

    /**
     * @return the jwt_xml_payload
     */
    public String getXmlPayload() {
        return xmlPayload;
    }

    /**
     * @return the jwt_header
     */
    public String getJsonHeader() {
        return jsonHeader;
    }

    /**
     * @return the jwt_json_payload
     */
    public String getXmlHeader() {
        return xmlHeader;
    }

    /**
     * Check that the concatenation of the header and the payload are signed
     * correctly
     *
     * @param key secret key to be used in hmac algorithm
     * @return boolean indicating whether signature is valid
     */
    public boolean hMacValidate(String key) {
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), HMAC_ALGORITHM);
            hmac.init(secret_key);
            byte[] hmacsig = hmac.doFinal((b64Blocks[HEADER_INDEX] + "." + b64Blocks[PAYLOAD_INDEX]).getBytes("UTF-8"));
            return Arrays.equals(signature, hmacsig);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | IllegalStateException ex) {
            Logger.getInstance().log(SEVERE, JWTParser.class.getName(), "Failed to get mac instance " + ex.getMessage());
        }
        return false;
    }

    /**
     * only do it once per JWT String
     * @return a formatted string detailing info about the JWT
     */
    public String getSupportingData() {
        if (lastJWT == null || !lastJWT.equals(jwt)) {
            lastJWT = jwt;
            String key = "secret";
            return "JWT json header\r\n"
                    + getJsonHeader() + "\r\n\r\n"
                    + "JWT xml header\r\n"
                    + getXmlHeader() + "\r\n\r\n"
                    + "JWT json payload\r\n"
                    + getJsonPayload() + "\r\n\r\n"
                    + "JWT xml payload\r\n"
                    + Utils.xmlReformat(getXmlPayload()) + "\r\n\r\n"
                    + (hasNoPadding ? "" : "Error: JWT base 64 string(s) not conformant - has padding\r\n\r\n")
                    + (isURLEncoded ? "" : "Error: JWT base 64 string(s) not conformant - not URL encoded\r\n\r\n")
                    + (b64Blocks.length == 3 ? "JWT Signature validity using key " + key + ": "+ hMacValidate(key) + "\r\n" : "");
        } else {
            return null;
        }
    }

    /**
     * it is not always possible to detect that the encoding has not been
     * performed base url no padding depending on the the characters in the
     * string to be encoded and the strings length.
     *
     * @param b64Block
     */
    private void checkBase64URLWithoutPadding(String b64Block) {
        if (b64Block.endsWith("=")) {
//          Logger.getInstance().log(WARNING, JWTParser.class.getName(), "Base 64 block unexpectedly ends with padding character '=' " + b64Block);
            if (hasNoPadding) {
                // latched to false
                hasNoPadding = false;
            }
        }
        // for base64 URL encodng + is replaced by - and / by _
        if (b64Block.contains("+") || b64Block.contains("/")) {
//          Logger.getInstance().log(WARNING, JWTParser.class.getName(), "Base 64 block is not URL Encoded (contains '+' or '/') " + b64Block);
            if (isURLEncoded) {
                // latched to false
                isURLEncoded = false;
            }
        }
    }

    /**
     * true is good false is bad
     * @return the hasNoPadding
     */
    public boolean hasNoPadding() {
        return hasNoPadding;
    }

    /**
     * true is good false is bad
     * @return the isURLEncoded
     */
    public boolean isURLEncoded() {
        return isURLEncoded;
    }
}
