/*
 Copyright 2017  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.COMPRESSION_DEFLATE;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.COMPRESSION_GZIP;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;

/**
 * Handle collections of http headers the collection is keyed on the lower cased
 * header name the actual header name to be used is in the HttpFields object
 *
 * @author simonfarrow
 */
public class HttpHeaderManager {

    private final HashMap<String, HttpFields> headers;
    private String firstLine;

    /**
     * public constructor
     */
    public HttpHeaderManager() {
        headers = new HashMap<>();
    }

    /**
     * @param name
     * @param value
     */
    public void addHttpHeader(String name, String value) {
        headers.put(name.toLowerCase(), new HttpFields(name, value));
    }

    /**
     *
     * @param name (this is case insensitive)
     * @return
     */
    public String getHttpHeaderValue(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String key = name.toLowerCase();
        if (!headers.containsKey(key)) {
            return null;
        }
        return headers.get(key).getValue();
    }

    /**
     *
     * @return string containing formatted http headers each header is followed
     * by a cr/lf but the final cr lf is not included.
     */
    public String getHttpHeaders() {
        StringBuilder sb = new StringBuilder();
        for (String key : headers.keySet()) {
            HttpFields httpHeader = headers.get(key);
            if ( !key.equalsIgnoreCase(CONTENT_LENGTH_HEADER) ) {
                sb.append(httpHeader.getActualHeader()).append(": ").append(httpHeader.getValue()).append("\r\n");
            } else {
                // for a get a content length of zero should not be sent the header should be omitted
                int contentLength = Integer.parseInt(httpHeader.getValue());
                if (contentLength > 0) {
                    sb.append(httpHeader.getActualHeader()).append(": ").append(httpHeader.getValue()).append("\r\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * parse the given log extract to return a map of http headers spec says
     * value can be surrounded either side by optional whitespace
     *
     * @param logStr
     */
    public void parseHttpHeaders(String logStr) {
        String[] lines = logStr.split("\r\n");
        boolean start = false;
        for (String line : lines) {
            if (start) {
                String headerName = line.replaceFirst(":.*$", "");
                String value = line.replaceFirst("^.*?:\\s*", "").trim();
                headers.put(headerName.toLowerCase(), new HttpFields(headerName, value));
            }
            // look for a request line or a response line
            if (line.endsWith("HTTP/1.1") || line.startsWith("HTTP/1.1")) {
                firstLine = line;
                start = true;
            }
        }
    }

    /**
     * appends v to existing header value
     *
     * @param h header name
     * @param v value to append
     * @throws Exception
     *
     * NOTE: This method appends the new information to the existing header but
     * replaces the "actual" header name with the lowercase version. The
     * question is what should happen when two of the same header names are
     * appended and one of those names is different case to the other?
     */
    public void updateHeader(String h, String v)
            throws Exception {
        String key = h.toLowerCase();
        if (headers.containsKey(key)) {
            String previousValue = headers.get(key).getValue();
            headers.remove(key);
            headers.put(key, new HttpFields(key, previousValue + v));
        } else {
            throw new Exception("Header malformed - unfolded header lines must not start with space or horizontal tab");
        }
    }

    /**
     * Get the HTTP header names.
     *
     * @return An ArrayList containing the HTTP header names.
     */
    public ArrayList<String> getFieldNames() {
        ArrayList<String> fields = new ArrayList<>();
        for (String s : headers.keySet()) {
            fields.add(headers.get(s).getActualHeader());
        }
        return fields;
    }

    /**
     * does this header exist and contain the given value in a csv
     *
     * @param headerName
     * @param value eg looks for gzip in a comma separated list of encodings
     * @return boolean
     */
    public boolean headerValueCsvIncludes(String headerName, String value) {
        String header = getHttpHeaderValue(headerName);
        if (header != null) {
            return valueInCsv(getHttpHeaderValue(headerName), value);
        } else {
            return false;
        }
    }

    /**
     * helper function for case insensitive checks whether a csv contains a
     * value check if a particular value is in a header which is commas
     * separated
     *
     * @param fieldValue full field header value
     * @param value to search for
     * @return boolean
     */
    public static boolean valueInCsv(String fieldValue, String value) {
        List<String> values = Arrays.asList(fieldValue.toLowerCase().split(","));
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i).trim());
        }
        return values.contains(value.toLowerCase());
    }

    /**
     * Change the headers to correspond to the saved form preserving the
     * original values as renamed headers
     *
     * @param contentLength
     */
    public void modifyHttpHeadersForLogging(int contentLength) {
        String header = CONTENT_ENCODING_HEADER.toLowerCase();
        if (headerValueCsvIncludes(header, COMPRESSION_GZIP) || headerValueCsvIncludes(header, COMPRESSION_DEFLATE)) {
            addHttpHeader("X-was-" + headers.get(header).getActualHeader(), headers.get(header.toLowerCase()).getValue());
            headers.remove(header);
        }

        header = TRANSFER_ENCODING_HEADER.toLowerCase();
        if (headerValueCsvIncludes(header, TRANSFER_ENCODING_CHUNKED)) {
            addHttpHeader("X-was-" + headers.get(header).getActualHeader(), headers.get(header).getValue());
            headers.remove(header);

            addHttpHeader(CONTENT_LENGTH_HEADER, Integer.toString(contentLength));
        }
    }

    /**
     * @return the firstLine for a request this is the "Request Line", for a
     * response this is the "Response Line"
     */
    public String getFirstLine() {
        return firstLine + "\r\n";
    }

    /**
     * @param firstLine This has been added so that the manager can be
     * copied/cloned
     */
    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }
}
