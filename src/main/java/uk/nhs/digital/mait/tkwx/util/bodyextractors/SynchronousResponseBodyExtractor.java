/**
 * Copyright 2013 Simon Farrow simon.farrow1@hscic.gov.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * @author SIFA2
 */
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import java.io.InputStream;
import java.util.Arrays;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage.VALIDATE_AS;

/**
 * Retrieve the body of a synchronous response from a transmitter or simulator
 * log file.
 */
public class SynchronousResponseBodyExtractor extends AbstractBodyExtractor {

    /**
     * Retrieve the body of a synchronous response from a log file.
     *
     * @param in InputStream opened for reading the log file
     * @return String containing the unchunked unzipped extracted response.
     * @throws Exception If something goes wrong, most likely
     * java.io.IOException.
     */
    @Override
    public String getBody(InputStream in, boolean getXML)
            throws Exception {

        // byte array representation of log required for extracting binary content (eg compressed)
        byte[] logfile = loadFile(in);
        // String representation of log its ok to use for getting the http character based parts of the log
        String logStr = new String(logfile);
        String requestHeader = logStr.trim().replaceFirst("(?s)^(.*?)HTTP/1.1.*$", "$1").trim();
        String validateAs = null;
        if (logStr.startsWith("VALIDATE-AS")) {
            // handle an initial VALIDATE-AS line
            validateAs = requestHeader.replaceFirst("(?s)^(VALIDATE-AS.*?)\r\n.*$","$1");
            requestHeader = requestHeader.replaceFirst("(?s)^VALIDATE-AS.*?\r\n(.*)$","$1");
        }
        httpRequestMethod = requestHeader.replaceFirst("^([^ ]+).*$", "$1");
        httpRequestContextPath = requestHeader.replaceFirst("^[^ ]+ +([^ ]+).*$", "$1");

        // parse the request headers
        httpRequestHeaders = new HttpHeaderManager();
        httpRequestHeaders.parseHttpHeaders(logStr.replaceFirst("(?s)\r?\n\r?\n.*$", ""));

        // 1. Find "**** END REQUEST ****" 
        // 2. Find \r\n\r\n, looking for chunked transfer encoding
        // 3. Then read it. Modify unchunk as needed to read the array.l
        //        
        byte[] startSearch = LogMarkers.END_REQUEST_MARKER.getBytes();
        int start = find(startSearch, logfile, 0);

        // also look for ************ END OF INBOUND MESSAGE **************
        if (start == -1) {
            startSearch = LogMarkers.END_INBOUND_MARKER.getBytes();
            start = find(startSearch, logfile, 0);
        }

        if (start == -1) {
            throw new Exception("Invalid log file, cannot find request end");
        }

        start = find("HTTP/1.1".getBytes(), logfile, start);
        if (start == -1) {
            throw new Exception("Invalid log file, cannot find start of HTTP Response");
        }

        int bodystart = findBodyStart(logfile, start, "response");

        // get the purely UTF-8 response http header into a String
        String responseHeader = new String(Arrays.copyOfRange(logfile, start, bodystart));
        responseHeader = responseHeader.replaceFirst("(?s)^.*(HTTP/1.1 +[0-9]+ +\r\n)", "$1");
        httpResponseHeaders = new HttpHeaderManager();
        httpResponseHeaders.parseHttpHeaders(responseHeader);

        byte[] messageBytes = handleTransferEncoding(httpResponseHeaders, logfile, bodystart, logfile.length);

        // check for any decompression required on the response
        messageBytes = handleCompression(httpResponseHeaders, messageBytes);

        String message = new String(messageBytes);
        // TODO removing the trim call breaks on reformatting for log files table in TKWATM
        message = message.trim();
        if (getXML) {
            message = handleJsonContent(httpResponseHeaders, message);
        }
        return message;
    }
}
