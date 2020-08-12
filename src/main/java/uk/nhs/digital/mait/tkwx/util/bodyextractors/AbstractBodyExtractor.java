/**
 * Copyright 2013  Simon Farrow simon.farrow1@hscic.gov.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @author SIFA2
 */
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.COMPRESSION_DEFLATE;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.COMPRESSION_GZIP;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Compressor;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.FHIRCONVERSIONFAILURE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertJson2Xml;
import static uk.nhs.digital.mait.tkwx.util.HttpChunker.unchunk;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ConditionalCompilationControls;

/**
 * Abstract base class for classes enabling extraction of xml trees from TKLW
 * log files
 *
 * @author SIFA2
 */
abstract public class AbstractBodyExtractor {

    protected HttpHeaderManager httpRequestHeaders;
    protected HttpHeaderManager httpResponseHeaders;

    protected String httpRequestContextPath;
    protected String httpRequestMethod;

    // used to label the enyry in the extra message info hashmap
    public static final String BODY_EXTRACTOR_LABEL = "body_extractor";

    private static final int BLOCKSIZE = 10240;

    /**
     * protected constructor
     */
    protected AbstractBodyExtractor() {
        httpRequestHeaders = null;
        httpResponseHeaders = null;
        httpRequestContextPath = null;
        httpRequestMethod = null;
    }

    public String getBody(InputStream is) throws Exception {return getBody(is, false);};
    
    public abstract String getBody(InputStream is, boolean getXML) throws Exception;

    /**
     * @return the httpRequestHeaders
     */
    public HttpHeaderManager getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    /**
     * @return the httpResponseHeaders
     */
    public HttpHeaderManager getHttpResponseHeaders() {
        return httpResponseHeaders;
    }
    
    /**
     * return request headers for request be and response headers for response be
     * @return 
     */
    public HttpHeaderManager getRelevantHttpHeaders() {
        return httpResponseHeaders;
    }

    /**
     * @return the httpRequestContextPath
     */
    public String getHttpRequestContextPath() {
        return httpRequestContextPath;
    }

    /**
     * @return the httpRequestMethod
     */
    public String getHttpRequestMethod() {
        return httpRequestMethod;
    }

    /**
     * perform a byte based find looking for
     *
     * @param needle byte[] to search for
     * @param haystack byte[] to look in
     * @param start int offset
     * @return int location within haystack or -1
     */
    protected int find(byte[] needle, byte[] haystack, int start) {
        int i = start;
        int j = 0;
        while (i < haystack.length) {
            if (haystack[i++] == needle[j++]) {
                if (j == needle.length) {
                    return i - j;
                }
            } else {
                j = 0;
            }
        }
        return -1;
    }

    /**
     * slurp all the bytes from the inputstream
     *
     * @param in
     * @return the byte array containing all the content
     * @throws Exception
     */
    protected byte[] loadFile(InputStream in)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BLOCKSIZE];
        while (true) {
            int r = in.read(buffer);
            if (r == -1) {
                break;
            }
            out.write(buffer, 0, r);
        }

        return out.toByteArray();
    }

    /**
     * convert incoming request or response from json to xml
     * @param headerManager
     * @param message String object containing payload  body
     * @return String object containing Json content converted to xml
     */
    protected String handleJsonContent(HttpHeaderManager headerManager, String message) {
        String contentType = headerManager.getHttpHeaderValue(CONTENT_TYPE_HEADER);
        if (contentType != null && contentType.contains("fhir") && contentType.contains("json")) {
            if (message.trim().startsWith("{")) {
                String xmlMessage = fhirConvertJson2Xml(message.trim());
                if (xmlMessage.contains(FHIRCONVERSIONFAILURE)) {
                    Logger.getInstance().log(Level.SEVERE, AbstractBodyExtractor.class.getName(), "fhir conversion json -> xml failed message [" + message + "] error [" + xmlMessage + "]");
                } else {
                    // adjust http headers for json to xml conversion
                    headerManager.addHttpHeader("X-was-Content-Type", contentType);
                    headerManager.addHttpHeader(CONTENT_TYPE_HEADER, contentType.contains("json+fhir") ? FHIR_XML_MIMETYPE_DSTU2 : FHIR_XML_MIMETYPE_STU3);

                    headerManager.addHttpHeader("X-was-Content-Length", ""+message.length());
                    headerManager.addHttpHeader(CONTENT_LENGTH_HEADER, ""+xmlMessage.length());

                    message = xmlMessage;
                }
            }
        }
        return message;
    }

    /**
     * uncompresses an incoming request or response
     * @param headerManager
     * @param messageBytes
     * @return uncompressed byte array
     * @throws DataFormatException
     * @throws IOException 
     */
    protected byte[] handleCompression(HttpHeaderManager headerManager, byte[] messageBytes) throws DataFormatException, IOException {
        try {
            if (headerManager.headerValueCsvIncludes(CONTENT_ENCODING_HEADER, COMPRESSION_GZIP)) {
                messageBytes = Compressor.uncompress(messageBytes, Compressor.CompressionType.COMPRESSION_GZIP);
            } else if (headerManager.headerValueCsvIncludes(CONTENT_ENCODING_HEADER, COMPRESSION_DEFLATE)) {
                messageBytes = Compressor.uncompress(messageBytes, Compressor.CompressionType.COMPRESSION_DEFLATE);
            }
        } catch (ZipException ex) {
            Logger.getInstance().log(Level.SEVERE, RequestBodyExtractor.class.getName(), "Decompress failed: " + ex.getMessage());
        }
        return messageBytes;
    }

    /**
     * unchunks an incoming request or response
     * @param headerManager
     * @param logfile
     * @param from start position of body
     * @param to end position of body
     * @return unchunked byte array
     * @throws Exception 
     */
    protected byte[] handleTransferEncoding(HttpHeaderManager headerManager, byte[] logfile, int from, int to) throws Exception {
        byte[] messageBytes = null;
        if (ConditionalCompilationControls.LOG_RAW_RESPONSE && headerManager.headerValueCsvIncludes(TRANSFER_ENCODING_HEADER, TRANSFER_ENCODING_CHUNKED)) {
            messageBytes = unchunk(new ByteArrayInputStream(logfile, from, to - from));
        } else {
            messageBytes = Arrays.copyOfRange(logfile, from, to);
        }
        return messageBytes;
    }

    /**
     * search through the byte array
     * @param logfile
     * @param start
     * @param type "request" or "response"
     * @return integer offset
     * @throws Exception 
     */
    protected int findBodyStart(byte[] logfile, int start, String type) throws Exception {
        int bodystart = find("\r\n\r\n".getBytes(), logfile, start);
        if (bodystart != -1) {
            bodystart += 4;
        } else {
            bodystart = find("\n\n".getBytes(), logfile, start);
            if (bodystart == -1) {
                throw new Exception("Invalid log file, cannot find start of " + type + " body");
            }
            bodystart += 2;
        }
        return bodystart;
    }
}
