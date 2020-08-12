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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 * Class to encapsulate the data and operations around an HTTP request.
 *
 * @author Damian Murphy murff@warlock.org 03.01.2018 Updated to extend a common
 * Request class which can be used for other non-HTTP requests
 */
public class HttpRequest extends Request {

    private InputStream inputStream;
    private String protocol;
    private String requestType;
    private String requestContext;
    private InetAddress remoteAddress;
    private String badRequestReason = null;
    private final HttpHeaderManager httpHeaderManager;
    private boolean handled;
    private int contentLength;
    private HttpResponse response;
    private byte[] buf = null;
    private boolean bodyRead = false;
    private LoggingFileOutputStream loggingFileOutputStream = null;

    /**
     * Creates a new instance of HttpRequest
     *
     * @param id identifier string
     */
    public HttpRequest(String id) {
        super.sourceId = id;
        handled = false;
        contentLength = -1;
        httpHeaderManager = new HttpHeaderManager();
    }

    void setBadRequestReason(String s) {
        badRequestReason = s;
    }

    String getBadRequestReason() {
        return badRequestReason;
    }

    boolean hasBadRequestReason() {
        return (badRequestReason != null);
    }

    public void readyToRead()
            throws Exception {
        inputStream.reset();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getRemoteAddr() {
        return remoteAddress.getHostAddress();
    }
    public String getRemoteAddrName() {
        return remoteAddress.getHostName();
    }

    void setResponse(HttpResponse r) {
        response = r;
    }

    /**
     * Get the HttpResponse object for returning results from this request.
     *
     * @return
     */
    public HttpResponse getResponse() {
        return response;
    }

    public void setRemoteAddress(InetAddress a) {
        remoteAddress = a;
    }

    /**
     *
     * @param h field name
     * @param v value
     * @throws Exception
     */
    @Override
    public void setHeader(String h, String v)
            throws Exception {
        httpHeaderManager.addHttpHeader(h, v);
        if (h.equalsIgnoreCase(CONTENT_LENGTH_HEADER)) {
            contentLength = Integer.parseInt(v);
        }
    }

    /**
     * appends to existsing header values
     *
     * @param h field name
     * @param v value
     * @throws Exception
     */
    @Override
    public void updateHeader(String h, String v)
            throws Exception {
        httpHeaderManager.updateHeader(h, v);
    }

    // Used to set content length from the chunked request reader
    @Override
    public void setContentLength(int c)
            throws Exception {
        if (contentLength != -1) {
            throw new Exception("Protocol error: content length already set");
        }
        contentLength = c;
    }

    /**
     * Get the HTTP header names.
     *
     * @return An ArrayList containing the HTTP header names.
     */
    public ArrayList<String> getFieldNames() {
        return httpHeaderManager.getFieldNames();
    }

    /**
     * Get the value of the given HTTP header.
     *
     * @param h The header name, case-insensitive.
     * @return The header value, or null if no such header is present.
     */
    @Override
    public String getField(String h) {
        return httpHeaderManager.getHttpHeaderValue(h);
    }

    String getSourceId() {
        return super.sourceId;
    }

    /**
     * Get the context path
     *
     * @return The context path
     */
    public String getContext() {
        return requestContext;
    }

    /**
     * Called by a handler when processing is finished, to indicate to the
     * server that the response can be written back to the requestor.
     *
     * @param b
     */
    public void setHandled(boolean b) {
        handled = b;
    }

    /**
     * Test whether setHandled() has been called.
     *
     * @return
     */
    public boolean isHandled() {
        return handled;
    }

    public int getContentLength() {
        return contentLength;
    }

    boolean contentLengthSet() {
        return (contentLength != -1);
    }

    /**
     * Gets the HTTP method used for the request.
     *
     * @return POST PUT etc
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Gets the HTTP version used for the request.
     *
     * @return Http version string
     */
    public String getVersion() {
        return protocol;
    }

    void close() {
        try {
            inputStream.close();
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, HttpRequest.class.getName(), "Error closing request from " + super.sourceId + " : " + e.getMessage());
        }
    }

    public void setProtocol(String p)
            throws Exception {
        if (!(p.compareTo("HTTP/1.1") == 0)) {
            throw new Exception(HttpRequest.class.getName() + ".setProtocol Only HTTP/1.1 protocol version accepted, got " + p);
        }
        protocol = p;
    }

    @Override
    public void setRequestType(String r)
            throws Exception {
//        if (!(r.compareTo("POST") == 0)) {
//            throw new Exception("Only POST request type accepted, got " + r);
//        }
        requestType = r;
    }

    @Override
    public void setRequestContext(String c)
            throws Exception {
        if ((c == null) || (c.trim().length() == 0)) {
            throw new Exception("Invalid request, empty context");
        }
        requestContext = c;
    }

    @Override
    public void setInputStream(InputStream h) {
        inputStream = h;
        bodyRead = false;
    }

    /**
     * return a byte array holding the content of the HttpRequest this method
     * may now be called more than once since the body content is cached once
     * read from the input stream.
     *
     * @return byte array containing the content of the request
     * @throws Exception
     */
    public byte[] getBody()
            throws Exception {
        if (!bodyRead) {
            // NB we now cope with GET which is not obliged to set a content length
            int derivedContentLength = getContentLength() == -1 ? 0 : getContentLength();
            buf = new byte[derivedContentLength];
            int totalRead = 0;
            @SuppressWarnings("UnusedAssignment")
            int dataSize = 0;
            while (totalRead < getContentLength()) {
                dataSize = inputStream.read(buf, totalRead, getContentLength() - totalRead);
                if (dataSize == -1) {
                    break;
                }
                totalRead += dataSize;
            }
            if (totalRead != derivedContentLength) {
                throw new Exception("Given data is not the same size as content length: " + totalRead + "/" + getContentLength());
            }
            bodyRead = true;
        }
        return buf;
    }

    public void setLoggingFileOutputStream(LoggingFileOutputStream lfos) {
        loggingFileOutputStream = lfos;

    }

    /**
     * write the Log to the file specified by logFile FileWriter field
     *
     * @param loggingFileOutputStream FileOutputStream
     * @param buffer byte array body of the inbound message
     * @param msg byte array outbound message
     * @throws Exception
     * @throws IOException
     */
    public void log(byte[] buffer, byte[] msg) throws Exception, IOException {
        loggingFileOutputStream.write(getRequestType().getBytes());
        loggingFileOutputStream.write(" ".getBytes());
        loggingFileOutputStream.write(getContext().getBytes());
        loggingFileOutputStream.write(" HTTP/1.1\r\n".getBytes());
        httpHeaderManager.modifyHttpHeadersForLogging(buffer != null ? buffer.length : 0);
        loggingFileOutputStream.write(httpHeaderManager.getHttpHeaders().getBytes());
        loggingFileOutputStream.write("\r\n".getBytes());
        if (getContentLength() > 0) {
            loggingFileOutputStream.write(buffer);
        }
        loggingFileOutputStream.flush();
        loggingFileOutputStream.write(("\r\n" + END_INBOUND_MARKER + "\r\n\r\n").getBytes());
        loggingFileOutputStream.flush();
        loggingFileOutputStream.write("\r\n****\r\n".getBytes());
        loggingFileOutputStream.flush();
        loggingFileOutputStream.write(msg);
        loggingFileOutputStream.flush();
        loggingFileOutputStream.logComplete();
        loggingFileOutputStream.close();
    }

    /**
     * accessor for the httpHeaderManager
     *
     * @return
     */
    public HttpHeaderManager getHeaderManager() {
        return httpHeaderManager;
    }

    public LoggingFileOutputStream getLoggingFileOutputStream() {
        return loggingFileOutputStream;
    }
}
