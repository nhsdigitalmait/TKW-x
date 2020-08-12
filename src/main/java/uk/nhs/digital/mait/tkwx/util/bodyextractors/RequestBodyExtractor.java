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
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;

/**
 * Retrieve the body of a synchronous request from a transmitter or simulator
 * log file.
 */
public class RequestBodyExtractor extends AbstractBodyExtractor {

    /**
     * extracts a string containing the xml request message from a transmitter
     * or simulator log file
     *
     * @param in InputStream
     * @param getXML
     * @return String containing the body of the request uncompressesd unchunked
     * and unjsonsed
     * @throws java.lang.Exception
     */
    @Override
    public String getBody(InputStream in, boolean getXML)
            throws Exception {

        // byte array representation of log required for extracting binary content (eg compressed)
        byte[] logfile = loadFile(in);

        // String representation of log its ok to use for getting the http character based parts of the log
        String logStr = new String(logfile);

        int start = 0;
        int bodystart = findBodyStart(logfile, start, "request");

        // 1. Find "**** END REQUEST ****" 
        byte[] endSearch = LogMarkers.END_REQUEST_MARKER.getBytes();
        int requestEnd = find(endSearch, logfile, 0);

        // also look for ************ END OF INBOUND MESSAGE **************
        if (requestEnd == -1) {
            endSearch = LogMarkers.END_INBOUND_MARKER.getBytes();
            requestEnd = find(endSearch, logfile, 0);
        }

        if (requestEnd == -1) {
            throw new Exception("Invalid log file, cannot find request end");
        }

        String requestHeader = logStr.trim().replaceFirst("(?s)^(.*?)HTTP/1.1.*$", "$1").trim();
        httpRequestMethod = requestHeader.replaceFirst("^([^ ]+).*$", "$1");
        httpRequestContextPath = requestHeader.replaceFirst("^[^ ]+ +([^ ]+).*$", "$1");

        // parse the request headers
        httpRequestHeaders = new HttpHeaderManager();
        httpRequestHeaders.parseHttpHeaders(logStr.replaceFirst("(?s)\r?\n\r?\n.*$", ""));

        byte[] messageBytes = handleTransferEncoding(httpRequestHeaders, logfile, bodystart, requestEnd);

        // check for any decompression required on the response
        messageBytes = handleCompression(httpRequestHeaders, messageBytes);

        String message = new String(messageBytes);
        // TODO removing the trim call breaks on reformatting for log files table in TKWATM
        message = message.trim();
        if ( getXML ) {
            message = handleJsonContent(httpRequestHeaders, message);
        } 
        return message;
    }
    /**
     * return request headers for request be and response headers for response be
     * @return 
     */
    @Override
    public HttpHeaderManager getRelevantHttpHeaders() {
        return httpRequestHeaders;
    }
}
