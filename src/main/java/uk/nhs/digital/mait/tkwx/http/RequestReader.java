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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import static java.util.logging.Level.SEVERE;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.util.HttpChunker.unchunk;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Package-private class for reading HTTP headers, and then the input stream.
 *
 * @author Damian Murphy murff@warlock.org
 */
class RequestReader
        extends Thread {

    private final Socket socket;
    private final HttpServer server;
    private final String connectionId;
    private Exception exception;
    private boolean timeout;
    private boolean clientClosedConnection;

//    static long cumulative = 0;
//    static long count = 0;
//    static long min = 999999999;
//    static long max = 0;
    private static final int MARKSIZE = 1024;

    /**
     * Creates a new instance of RequestReader
     */
    RequestReader(Socket s, HttpServer h, String id) {
        socket = s;
        server = h;
        connectionId = id;
        exception = null;
        start();
    }

    Exception getException() {
        return exception;
    }

    private String getLine()
            throws Exception {
        StringBuilder sb = new StringBuilder();
        int r = 0;
        try {
            InputStream in = socket.getInputStream();
            while ((r = in.read()) != (int) '\r') {
                if (r == -1) {
                    // if we get then we have a client disconnect event 
                    clientClosedConnection = true;
                    break;
                }
                sb.append((char) r);
            }
            r = in.read();
            // this IOException is here so that when the socket is closed due to a timeout 
            // from HttpTimer class. It returns "" and close connection exist as it would
            // if connection: close were handled
        } catch (IOException e) {
            timeout = true;
        }
        return sb.toString();
    }

    /**
     * The class has been instantiated with a reference to the inbound socket,
     * and the server. This thread is responsible for: - Creating the request
     * instance - Creating the buffered input stream that sits on the socket -
     * Reading the header and identifying the request type, context path, and
     * headers - Passing the HttpRequest to the server (the server will ask the
     * request to make the response) - Responding to pipelined HTTP requests as
     * per RFC 2616 - exitting
     */
    @Override
    public synchronized void run() {
        this.setName("RequestReaderThread");
        //System.out.printf("RequestReader.run %x started on port %d\r\n",hashCode(),socket.getLocalPort());
        long start = System.currentTimeMillis();
        this.setName("RequestReader");
        boolean connectionClose = false;
        boolean readingHeader;
        boolean gotProtocol;
        String line;
        PipeliningQueue queue = null;
        HttpTimer timer = new HttpTimer(socket);
        timeout = false;
        clientClosedConnection = false;
        try {
            queue = new PipeliningQueue(socket.getOutputStream(), this);
            connectionLoop:
            while (!connectionClose) {
                readingHeader = true;
                gotProtocol = false;
                line = null;
                timer.reset();
                HttpResponse resp = new HttpResponse(socket.getOutputStream());
                HttpRequest req = new HttpRequest(connectionId);
                req.setRemoteAddress(socket.getInetAddress());
                String previousLineHeader = "";
                while (readingHeader) {
                    line = getLine();
                    if (timeout || clientClosedConnection) {
                        queue.closeOnCompletion();
                        if (clientClosedConnection) {
                            timer.stopTimer();
                        }
                        break connectionLoop;
                    }
                    if (line != null) {
                        // System.err.println("http header "+line);
                        if (line.trim().length() == 0) {
                            if (gotProtocol) {
                                readingHeader = false;
                            } else {
                                continue;
                            }
                        } else if (!gotProtocol) {
                            StringTokenizer st = new StringTokenizer(line);
                            int protocolCheck = st.countTokens();
                            if (protocolCheck != 3) {
                                throw new Exception("Protocol error in request line: " + line);
                            }
                            int i = 0;
                            while (st.hasMoreTokens()) {
                                switch (i) {
                                    case 0:
                                        req.setRequestType(st.nextToken());
                                        break;
                                    case 1:
                                        req.setRequestContext(st.nextToken());
                                        break;
                                    case 2:
                                        req.setProtocol(st.nextToken());
                                        break;
                                }
                                ++i;
                            }
                            gotProtocol = true;
                        } else {
                            previousLineHeader = splitField(line, req, previousLineHeader);
                        }
                    }
                } // while reading header

                if (req.getField("expect") != null && req.getField("expect").equals("100-continue")) {
                    queue.continueResponse("HTTP/1.1 100 Continue\r\n\r\n");
                }
                if (req.getField("connection") != null && req.getField("connection").toLowerCase().equals("close")) {
                    connectionClose = true;
                    timer.stopTimer();
                }
                // See if we've been sent a chunked request, if so read the stream
                // and calculate the actual content length with all the chunking stripped.
                //
                // Read the input into a byte array, and make the request input stream a
                // ByteArrayInputStream. We might as well process the chunking and read the
                // whole thing at this point, because the request handler will anyway, and
                // it means that we can set the content length correctly.
                //
                if (req.getHeaderManager().headerValueCsvIncludes(TRANSFER_ENCODING_HEADER, TRANSFER_ENCODING_CHUNKED)) {
                    if (req.getContentLength() > 0) {
                        req.setBadRequestReason("Protocol error, content length is set for a chunked request");
                    }
                    // Chunked, need to pre-read the stream, de-chunk, and set the content
                    // length to the real value. Also set the request input stream to a
                    // ByteArrayInputStream based on the buffered input
                    req.setInputStream(bufferChunkedInput(req, socket.getInputStream()));
                    // Not chunked, last element or no content 
                } else if (connectionClose || req.getContentLength() <= 0) {
                    req.setInputStream(socket.getInputStream());
                } else {
                    req.setInputStream(bufferStreamedInput(req, socket.getInputStream()));
                }

                req.setResponse(resp);
                queue.addQueueEntry(req);
                server.addRequest(req);
                if (connectionClose) {
                    queue.closeOnCompletion();
                }
            } // while not connection close
//          System.out.println("wait@" + new Date());
            this.wait();
//          System.out.println("end wait@" + new Date());
        } catch (Exception e) {
            exception = e;

            queue.stopQueue();
            String report = "Exception creating request on socket " + connectionId + ", request processing exiting: " + e.getMessage();
            Logger.getInstance().log(SEVERE, RequestReader.class.getName(), report);
            try {
                LastResortReporter.report(report, socket.getOutputStream());
                this.wait();
            } catch (IOException | InterruptedException e1) {
                Logger.getInstance().log(SEVERE, RequestReader.class.getName(), "... socket output stream dead");
            }
        }
//        long duration = System.currentTimeMillis() - start;
//        cumulative += duration;
//        count++;
//        if ( duration > max ) max = duration;
//        if ( duration < min ) min = duration;
//        System.out.printf("RequestReader.run %x terminated after %d ms, count = %d, total time %d s, avg %d ms, min %d ms, max %d ms on port %d close status %b\r\n",
//                hashCode(),duration, count, cumulative / 1000, cumulative / count, min, max, socket.getLocalPort(), socket.isClosed());

//        System.out.println("4. RequestReader closed");
    }

    private ByteArrayInputStream bufferChunkedInput(HttpRequest req, InputStream is)
            throws Exception {
        byte[] ba = unchunk(is);
        req.setContentLength(ba.length);
        return new ByteArrayInputStream(ba);
    }

    private ByteArrayInputStream bufferStreamedInput(HttpRequest req, InputStream is)
            throws Exception {
        byte[] buf = new byte[req.getContentLength()];
        int totalRead = 0;
        @SuppressWarnings("UnusedAssignment")
        int dataSize = 0;
        while (totalRead < req.getContentLength()) {
            dataSize = is.read(buf, totalRead, req.getContentLength() - totalRead);
            if (dataSize == -1) {
                break;
            }
            totalRead += dataSize;
        }
        if (totalRead != req.getContentLength()) {
            throw new Exception("Given data is not the same size as content length: " + totalRead + "/" + req.getContentLength());
        }

        return new ByteArrayInputStream(buf);
    }

    private String splitField(String line, HttpRequest req, String previousHeaderKey)
            throws Exception {
        int i;
        int colon = -1;
        String value;

        if ((line == null) || (line.trim().length() == 0)) {
            return null;
        }
        boolean foldedLine = false;
        for (i = 0; i < line.length(); i++) {
            if (i == 0) {
                int tab = 9;
                int space = 32;
                if (line.charAt(i) == (char) tab || line.charAt(i) == (char) space) {
                    foldedLine = true;
                }
            }
            if (line.charAt(i) == ':') {
                colon = i;
                break;
            }
        }
        if (colon == -1 && !foldedLine) {
            throw new Exception("Invalid header: " + line);
        }
        String thisKey;
        if (!foldedLine) {
            thisKey = line.substring(0, colon);
            req.setHeader(thisKey, line.substring(colon + 1).trim());
        } else {
            thisKey = previousHeaderKey;
            req.updateHeader(previousHeaderKey, line);
        }
        return thisKey;
    }
}
