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
package uk.nhs.digital.mait.tkwx.http;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Class to encapsulate data and operations on the response to an HTTP request.
 * Spawns a Pipelining Queue to handle more than 1 HTTP requests on a socket
 * connection
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpResponse {

    private final OutputStream outputStream;
    private String status;
    private String statusText;
    private final NotifyOnFlushOutputStream outbuffer;
    private String contentType;
    private HttpHeaderManager headers;
    private boolean responseReady = false;
    private boolean hasHttpHeader = true;
    private String httpHeader = null;
    private byte[] httpBuffer = null;
    private boolean suppressClose = false;

    public static final String SUPPRESS_CLOSE_PROPERTY = "tks.HttpTransport.suppress.close";

    /**
     * public constructor o OutputStream
     *
     * @param o Typically a socket OutputStream object
     */
    public HttpResponse(OutputStream o) {
        try {
            Configurator configurator = Configurator.getConfigurator();
            suppressClose = isY(configurator.getConfiguration(SUPPRESS_CLOSE_PROPERTY));
        } catch (Exception ex) {
            Logger.getInstance().log(WARNING, HttpResponse.class.getName(),
                    "Error getting configurator : " + ex.getMessage());
        }
        outputStream = o;
        status = null;
        statusText = "";
        contentType = null;
        headers = null;
        outbuffer = new NotifyOnFlushOutputStream(this);
    }

    /**
     * Set the MIME content type for the response.
     *
     * @param c
     */
    public void setContentType(String c) {
        contentType = c;
    }

    /**
     * Get the output stream.
     *
     * @return
     */
    public OutputStream getOutputStream() {
        return outbuffer;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * Set an HTTP response field. This should only be used for "other" response
     * headers that are not handled explicitly, for example SOAPaction (MTH
     * calling code needs to know about this).
     *
     * @param f Header name
     * @param v Header value
     */
    public void setField(String f, String v) {
        if ((f == null) || (f.trim().length() == 0) || (v == null) || (v.trim().length() == 0)) {
            return;
        }
        if (headers == null) {
            headers = new HttpHeaderManager();
        }
        headers.addHttpHeader(f, v);
    }

    void canWrite() {
        if (hasHttpHeader) {
            // Write the buffer and HTTP headers to the outputStream via the 
            // Streamed queue 
            //
            // HTTP/1.1 status statusText
            // Content-Length
            // Content Type
            // Any other headers
            // blank line
            // response
            StringBuilder sb = new StringBuilder("HTTP/1.1 ");
            if (status != null) {
                sb.append(status);
                sb.append(" ");
            }
            sb.append(statusText);
            sb.append("\r\n");
            boolean chunked = false;
            if (headers != null && headers.getHttpHeaderValue(TRANSFER_ENCODING_HEADER) != null) {
                List<String> encodings = Arrays.asList(headers.getHttpHeaderValue(TRANSFER_ENCODING_HEADER).toLowerCase().split(","));
                if (encodings.contains(TRANSFER_ENCODING_CHUNKED)) {
                    chunked = true;
                }
            }

            if ( !chunked ) {
                sb.append("Content-Length: ");
                        sb.append(Long.toString(outbuffer.size()));
                        sb.append("\r\n");
            }
            
            // at the time of writing this has always been hard coded in a response.
            // For fhir this is not applicable.
            if (!suppressClose) {
                sb.append("Connection: close\r\n");
            }

            if ((contentType != null) && (contentType.trim().length() > 0)) {
                sb.append("Content-type: ");
                sb.append(contentType);
                sb.append("\r\n");
            }
            if (headers != null) {
                sb.append(headers.getHttpHeaders());
            }
            sb.append("\r\n");
            httpHeader = sb.toString();
        }
        httpBuffer = outbuffer.toByteArray();
        responseReady = true;
//        System.out.println("HttpResponse.responseReady@" + new Date());
//        outbuffer.reset();
    }

    public boolean isResponseReady() {
        return responseReady;
    }

    public String getHttpHeader() {
        return httpHeader;
    }

    public void hasHttpHeader(boolean b) {
        hasHttpHeader = b;
    }

    public byte[] getHttpBuffer() {
        return httpBuffer;
    }

    public void flush() {
        this.canWrite();
    }

    /**
     * Sets the HTTP response text.
     *
     * @param s
     */
    public void setStatusText(String s) {
        statusText = s;
    }

    /**
     * Provided to allow the response to be aborted, forcing the connection
     * closed as a protocol violation for test purposes.
     */
    public void forceClose() {
        try {
            outputStream.close();
        } catch (java.io.IOException e) {
            Logger.getInstance().log(WARNING, HttpResponse.class.getName(),
                    "Error closing connection on null response: " + e.getMessage());
        }
    }

    /**
     * Sets the HTTP response code.
     *
     * @param s
     */
    public void setStatus(int s) {
        if ((s < 100) || (s > 600)) {
            return;
        }
        status = Integer.toString(s);
    }

    /**
     * Set both HTTP response code and the status text.
     *
     * @param i status code
     * @param s status text
     */
    public void setStatus(int i, String s) {
        setStatusText(s);
        setStatus(i);
    }
}
