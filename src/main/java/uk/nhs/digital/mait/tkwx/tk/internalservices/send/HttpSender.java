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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import ca.uhn.fhir.context.FhirVersionEnum;
import java.io.ByteArrayOutputStream;
import uk.nhs.digital.mait.commonutils.util.Logger;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.URL;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import java.text.SimpleDateFormat;
import java.util.Date;
import uk.nhs.digital.mait.tkwx.itklogverifier.LogVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.http.HttpResponse.SUPPRESS_CLOSE_PROPERTY;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.COMPRESSION_DEFLATE;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.COMPRESSION_GZIP;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Compressor;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.FHIRCONVERSIONFAILURE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.fhirConvertXml2Json;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable;
import uk.nhs.digital.mait.commonutils.util.FileLocker;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.util.HttpChunker.chunk;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ConditionalCompilationControls;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.FUNCTION_PREFIX;
import static uk.nhs.digital.mait.tkwx.util.Utils.isBinaryPayloadString;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import static uk.nhs.digital.mait.tkwx.util.Utils.unwrapBinaryPayload;

/**
 * Thread for transmitting SOAP messages.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpSender
        extends java.lang.Thread implements Sender, Reconfigurable {

    private static final String SOAPACTION = "//wsa:Action";
    private static final SimpleDateFormat LOGFILEDATE = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    private static final String HTTP_HEADER_PROPERTY_PREFIX = "tks.transmitter.httpheader.";

    private static boolean notUsingSslContext = false;
    private boolean suppressClose = false;
    private boolean tlsMutualAuthentication = false;
    private boolean useLogFileLocker = false;

    private SSLContext sslContext = null;

    private String action = null;
    private String messageID = null;
    private String message = null;
    private String address = null;
    private String wrapper = null;
    private ToolkitSimulator simulator = null;
    private boolean tls = false;
    private int chunkSize = 0;
    private String logDirectory = null;
    private SenderRequest req = null;
    private FileLocker logFileLock = null;
    private Properties reconfiguredProperties = null;
    private final HttpHeaderManager headerManager;
    private boolean propsSetInternally = false;
    private String httpMethod = null;
    private byte[] messageBytes;

    /**
     * public constructor
     */
    public HttpSender() {
        headerManager = new HttpHeaderManager();
    }

    @Override
    public void init(ToolkitSimulator tk, SenderRequest what, boolean t, String logdir) {
        simulator = tk;
        req = what;
        message = what.getPayload();
        address = what.getAddress();
        wrapper = what.getWrapperTemplate();
        logDirectory = logdir;
        action = what.getAction();
        httpMethod = what.getHttpMethod();

        chunkSize = what.chunkSize();
        tls = t;

        if (tls) {
            try {

                if (System.getProperty(ORG_USESSLCONTEXT_PROPERTY) == null && System.getProperty(ORG_SSLPASS_PROPERTY) == null) {
                    // we need this to support tlsma testing where the keystore may change between invocations
                    // force use of specific SSL context so we work round behaviour of SSLServerSocketFactory.getDefault
                    // which only uses the initial values for keystore and password
                    // see https://bugs.openjdk.java.net/browse/JDK-4773445
                    if (System.getProperty("javax.net.ssl.keyStore") != null && System.getProperty("javax.net.ssl.keyStorePassword") != null) {
                        System.setProperty(ORG_USESSLCONTEXT_PROPERTY, System.getProperty("javax.net.ssl.keyStore"));
                        System.setProperty(ORG_SSLPASS_PROPERTY, System.getProperty("javax.net.ssl.keyStorePassword"));
                        propsSetInternally = true;
                    }
                } else {
                    Logger.getInstance().log(WARNING, HttpSender.class.getName(), "HttpSender invoked using externally set org security properties");
                }
                initSSLContext();
                tlsMutualAuthentication = isY(System.getProperty(SENDTLSMA_PROPERTY));
            } catch (Exception e) {
                Logger.getInstance().log(SEVERE, HttpSender.class.getName(), "Error initialising SSL context for sending: " + e.toString());
            }
        }
        start();

        // autotest mode requires the transmit to block until the log file has been fully populated.
        if (System.getProperty("tkw.internal.runningautotest") != null) {
            try {
                join();
            } catch (InterruptedException ex) {
            }
        }
    }

    private void initSSLContext()
            throws Exception {
        if (!notUsingSslContext && (sslContext == null)) {
            String ksf = System.getProperty(ORG_USESSLCONTEXT_PROPERTY);
            if (ksf == null) {
                notUsingSslContext = true;
                return;
            }
            String p = System.getProperty(ORG_SSLPASS_PROPERTY);
            if (p == null) {
                p = "";
            }
            String alg = System.getProperty(ORG_SSLALGORITHM_PROPERTY);
            KeyManagerFactory kmf = null;
            if (alg == null) {
                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            } else {
                kmf = KeyManagerFactory.getInstance(alg);
            }
            KeyStore ks = KeyStore.getInstance("jks");
            FileInputStream fis = new FileInputStream(ksf);
            ks.load(fis, p.toCharArray());
            kmf.init(ks, p.toCharArray());
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
        }
    }

    @Override
    public void run() {
        this.setName("HttpSenderThread");
        Logger l = Logger.getInstance();
        l.log("Sender", "Sending to " + address);
        if ((message == null) || (message.length() == 0)) {
            l.log("Sender", "Asked to send null or zero-length message");
        }
        if ((address == null) || (address.trim().length() == 0) || (address.contentEquals(ADDRESSING_NONE))) {
            if (address == null) {
                l.log("Sender", "Null address");
            } else {
                l.log("Sender", "Undeliverable address: " + address);
            }
            return;
        }

        URL u = null;
        try {
            u = new URL(address);
        } catch (Exception e) {
            l.log("Sender", "Cannot parse address URL " + address + " : " + e.getMessage());
            return;
        }
        if (wrapper != null) {
            try {
                StringBuilder sb = new StringBuilder(wrapper);
                substitute(sb, PAYLOAD_TAG, message);
                message = sb.toString();
            } catch (Exception e) {
                l.log("Sender", "Error trying to wrap: " + e.getMessage());
                return;
            }
        }
        if (action == null && message.trim().startsWith("<")) {
            try {
                action = xpathExtractor(SOAPACTION, message);
            } catch (XPathExpressionException | XPathFactoryConfigurationException e) {
                l.log("Sender", "Failed to get SOAPaction: " + e.getMessage());
            }
        }
        try {
            sendDirect(u, l);
        } catch (Exception e) {
            l.log("Sender", "Error trying to send: " + e.getMessage());
        }

        if (propsSetInternally) {
            System.getProperties().remove(ORG_USESSLCONTEXT_PROPERTY);
            System.getProperties().remove(ORG_SSLPASS_PROPERTY);
        }

    }

    /**
     *
     * @param u host url string
     * @param logger logger object
     * @throws Exception
     */
    private void sendDirect(URL u, Logger logger)
            throws Exception {

        if (message.trim().startsWith("<")) {
            // extract the MessageID to make the logfile unique
            try {
                messageID = xpathExtractor(SOAP_HEADER_MESSAGEID, message);
            } catch (XPathExpressionException | XPathFactoryConfigurationException e) {
                logger.log("Sender", "Failed to get MessageID: " + e.getMessage());
            }
        }

        if (tls && !u.getProtocol().equalsIgnoreCase("https")) {
            logger.log("Sender-direct", "Mismatch: TLS requested but address URL " + address + " does not agree - continuing");
        }

        String logFileName = getLogFileName(u);

        try ( FileOutputStream logFileWriter = new FileOutputStream(logFileName)) {
            if (useLogFileLocker) {
                logFileLock = new FileLocker(logFileName);
            }
            doSend(u, logFileWriter, logFileName, logger);
        }

        if (logFileLock != null) {
            logFileLock.unlock();
        }
    }

    /**
     * do the actual work of sending and logging the request and receiving and
     * logging the response
     *
     * @param hostURL
     * @param logFileWriter
     * @param logFileName
     * @param logger
     * @return success status
     * @throws IOException
     * @throws Exception
     */
    private void doSend(URL hostURL, final FileOutputStream logFileWriter, String logFileName, Logger logger) throws IOException, Exception {
        String header = processHeaders(hostURL);

        // log the munged sent message
        HttpHeaderManager hm = new HttpHeaderManager();
        hm.parseHttpHeaders(header);
        if (message.length() != messageBytes.length) {
            hm.addHttpHeader("X-was-"+CONTENT_LENGTH_HEADER, ""+messageBytes.length);
        }
        hm.modifyHttpHeadersForLogging(message.length());
        logFileWriter.write(hm.getFirstLine().getBytes());
        logFileWriter.write(hm.getHttpHeaders().getBytes());
        logFileWriter.write("\r\n".getBytes());
        logFileWriter.flush();
        // message.getBytes() has the original unchunked uncompressed content which is what we need for the log
        // as opposed to messageBytes which has the on the wire potentially chunked and/or compressed binary
        logFileWriter.write(message.getBytes());
        logFileWriter.write(("\r\n" + LogMarkers.END_REQUEST_MARKER + "\r\n").getBytes());
        logFileWriter.flush();
        if (System.getProperty(NOORIGINATE_PROPERTY) != null) {
            logFileWriter.write("\n\nSender NOT transmitting due to internal request: ".getBytes());
            logFileWriter.write(System.getProperty(NOORIGINATE_PROPERTY).getBytes());
            logFileWriter.flush();
            logFileWriter.close();
            // requires an explcit Y to inhibit
            if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
                LogVerifier lv = LogVerifier.getInstance();
                lv.makeSignature(logFileName);
            }
            return;
        }
        logFileWriter.flush();
        // initialise the socket
        Socket socket = null;
        SocketFactory sf = null;
        if (tls) {
            try {
                // See if we're doing TLSMA and set things up accordingly
                // if we are.
                if (sslContext == null) {
                    sf = SSLSocketFactory.getDefault();
                } else {
                    sf = sslContext.getSocketFactory();
                }
                socket = sf.createSocket();
                if (tlsMutualAuthentication) {
                    ((SSLSocket) socket).setNeedClientAuth(true);
                    ((SSLSocket) socket).addHandshakeCompletedListener(new MAEstablished());
                }
                InetSocketAddress addr = null;
                if (hostURL.getPort() > 0) {
                    addr = new InetSocketAddress(hostURL.getHost(), hostURL.getPort());
                } else {
                    addr = new InetSocketAddress(hostURL.getHost(), 443);
                }
                socket.connect(addr);
            } catch (Exception e) {
                logger.log("Sender-direct", "Failed to create outbound SSL socket: " + e.getMessage());
                return;
            }
        } else {
            try {
                sf = SocketFactory.getDefault();
                if (hostURL.getPort() > 0) {
                    socket = sf.createSocket(hostURL.getHost(), hostURL.getPort());
                } else {
                    socket = sf.createSocket(hostURL.getHost(), 80);
                }
            } catch (Exception e) {
                String report = "Failed to create outbound socket: " + e.getMessage();
                logger.log("Sender-direct", report);
                logFileWriter.write(report.getBytes());
                logFileWriter.flush();
                logFileWriter.close();
                // requires an explcit Y to inhibit
                if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
                    LogVerifier lv = LogVerifier.getInstance();
                    lv.makeSignature(logFileName);
                }
                return;
            }
        }
        writeToSocketAndLog(socket, header, logFileWriter, logFileName, logger);
    }

    /**
     * write the output to the socket and log the response
     *
     * @param socket
     * @param header
     * @param contentLength
     * @param logFileWriter
     * @param logFileName
     * @param logger
     * @throws IOException
     */
    private void writeToSocketAndLog(Socket socket, String header, final FileOutputStream logFileWriter, String logFileName, Logger logger) throws IOException {
        // write out to the socket
        String activity = null;
        try {
            activity = "Creating reader";
            InputStream is = socket.getInputStream();
            activity = "Creating header";
            OutputStream os = socket.getOutputStream();
            activity = "Writing header";
            os.write(header.getBytes());
            activity = "Flushing header";
            os.flush();
            activity = "Writing message";
            if (chunkSize == 0) {
                //osw.write(header);
                //activity = "Flushing header";
                os.flush();
                activity = "Writing message";
                os.write(messageBytes);
                activity = "Flushing message";
                os.flush();
            } else {
                chunk(messageBytes, chunkSize, os);
            }
            // moved to solve fhir comms connection issues
//            if (!tls) {
//                activity = "Shutting down output";
//                socket.shutdownOutput();
//            }

            updateLogFileWithResponse(is, logFileWriter, socket, logFileName);

        } catch (Exception e) {
            String report = "Failed to send message to address " + address + " : " + e.getMessage() + " : " + activity;
            logger.log("Sender-direct", report);
            System.err.println(report);
            logFileWriter.write(report.getBytes());
            logFileWriter.flush();
        }
        logFileWriter.flush();
    }

    /**
     * log the response from the input stream
     *
     * @param is
     * @param logFileWriter
     * @param socket
     * @param logFileName
     * @throws IOException
     * @throws Exception
     */
    private void updateLogFileWithResponse(InputStream is, final FileOutputStream logFileWriter, Socket socket, String logFileName) throws IOException, Exception {
        String activity;
        // Write the rest of the data back to the log file
        int c = 0;
        activity = "Clearing input data";
        boolean responseReceived = false;
        StringBuilder sbr = new StringBuilder();

        // we need to close the connection from this end after the response has been received
        // this must be detected by either Content-Length or Chunking or the server closing the connection
        boolean chunking = false;
        boolean readingHeader = true;
        int charCount = 0;
        int contentLength = -1;
        boolean readingChunkSize = true;
        int recdChunkSize = -1;
        int chunkCount = -1;
        boolean firstPass = true;
        StringBuilder sbChunkSize = new StringBuilder();
        ByteArrayOutputStream baosHeader = new ByteArrayOutputStream();
        ByteArrayOutputStream baosBody = new ByteArrayOutputStream();
        while ((c = is.read()) != -1) {
            responseReceived = true;
            if (ConditionalCompilationControls.LOG_RAW_RESPONSE || readingHeader || (chunking && !readingChunkSize && chunkCount < recdChunkSize || !chunking)) {
                // perform on the fly unchunking of the response if we are not logging raw
                if (readingHeader) {
                    baosHeader.write(c);
                } else {
                    baosBody.write(c);
                }
            }
            sbr.append((char) c);
            // are we at the end of the http header?
            if (readingHeader && sbr.toString().endsWith("\r\n\r\n")) {
                HttpHeaderManager respHeaderManager = new HttpHeaderManager();
                respHeaderManager.parseHttpHeaders(sbr.toString());
                chunking = respHeaderManager.headerValueCsvIncludes(TRANSFER_ENCODING_HEADER, TRANSFER_ENCODING_CHUNKED);
                if (!chunking) {
                    String contentLengthStr = respHeaderManager.getHttpHeaderValue(CONTENT_LENGTH_HEADER);
                    contentLength = Integer.parseInt(contentLengthStr);
                }
                readingHeader = false;
            }

            if (!readingHeader) {
                // this code is merely tracking the chunks so we know when to quit
                // its causing yet more duplication of chunking code
                charCount++;
                if (chunking) {
                    if (readingChunkSize) {
                        if (!firstPass) {
                            sbChunkSize.append((char) c);
                        } else {
                            // first time through we at the final \n before the payload so skip it
                            firstPass = false;
                        }
                        if (sbChunkSize.toString().endsWith("\r\n")) {
                            // decode the hex string
                            recdChunkSize = Integer.parseInt(sbChunkSize.toString().substring(0, sbChunkSize.length() - 2), 16);
                            readingChunkSize = false;
                            // reset counters
                            chunkCount = 0;
                            sbChunkSize.setLength(0);

                            // terminating condition?
                            if (recdChunkSize == 0) {
                                break;
                            }
                        }
                    } else {
                        chunkCount++;
                        if (chunkCount >= recdChunkSize + 2) {
                            readingChunkSize = true;
                        }
                    }
                } else if (charCount > contentLength) {
                    // checks for terminating condition of a non chunked response
                    break;
                }
            }
        } // while reading input

        if (c == -1) {
            //System.out.println("HttpSender: breaking on closed connection");
        }

        HttpHeaderManager hmResponse = new HttpHeaderManager();
        hmResponse.parseHttpHeaders(new String(baosHeader.toByteArray()));
        byte[] bytes = baosBody.toByteArray();
        int onTheWireLength = bytes.length;
        if (hmResponse.headerValueCsvIncludes(CONTENT_ENCODING_HEADER, COMPRESSION_GZIP)) {
            bytes = Compressor.uncompress(bytes, Compressor.CompressionType.COMPRESSION_GZIP);
        } else if (hmResponse.headerValueCsvIncludes(CONTENT_ENCODING_HEADER, COMPRESSION_DEFLATE)) {
            bytes = Compressor.uncompress(bytes, Compressor.CompressionType.COMPRESSION_DEFLATE);
        }
        
        // if its an incoming binary payload then unencode it
        if (Utils.isBinaryPayload(bytes)) {
            bytes = Utils.wrapBinaryPayload(bytes).toString().getBytes();
        }
        
        if (onTheWireLength != bytes.length){
            hmResponse.addHttpHeader("X-was-"+CONTENT_LENGTH_HEADER, ""+onTheWireLength);
        }
        hmResponse.modifyHttpHeadersForLogging(bytes.length);
        
        logFileWriter.write(hmResponse.getFirstLine().getBytes());
        logFileWriter.write(hmResponse.getHttpHeaders().getBytes());
        logFileWriter.write("\r\n".getBytes());

        logFileWriter.flush();

        // write the body to the log file
        logFileWriter.write(bytes);

        // TODO need to check the contents of this and log any errors
        logFileWriter.flush();
        if (!responseReceived) {
            logFileWriter.write("\r\nNo HTTP response received - input stream already at EOF\r\n".getBytes());
            logFileWriter.flush();
        }
        if (!tls) {
            activity = "Shutting down input";
            socket.shutdownInput();
            // moved from writeToSocketAndLog to stop comms errors in fhir mode
            activity = "Shutting down output";
            socket.shutdownOutput();
        }
        activity = "Closing";
        socket.close();
        logFileWriter.flush();
        // requires an explcit Y to inhibit
        if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
            LogVerifier lv = LogVerifier.getInstance();
            lv.makeSignature(logFileName);
        }
        System.out.println("Finished writing log HttpSender Thread " + getName() + "terminating");
    }

    /**
     * determines the fully qualified path name to the log file
     *
     * @param hostURL host url string
     * @return fully qualified path name to log file
     */
    private String getLogFileName(URL hostURL) {
        StringBuilder fnb = new StringBuilder(logDirectory);
        fnb.append(System.getProperty("file.separator"));
        String runningAuto = System.getProperty("tkw.internal.runningautotest");
        if ((runningAuto != null) && (runningAuto.contentEquals("true"))) {
            if (req.getOriginalFileName() != null) {
                fnb.append(req.getOriginalFileName());
            } else if (action != null) {
                fnb.append(action.substring(action.lastIndexOf(':') + 1));
            } else {
                fnb.append("no-action");
            }
            fnb.append("_");
        } else {
            if (req.getOriginalFileName() != null) {
                fnb.append(req.getOriginalFileName());
                fnb.append("_");
            }
            fnb.append(hostURL.getHost());
            fnb.append("_sent_");
            if (messageID != null) {
                // #2433 make file name dos safe
                fnb.append(messageID.replaceAll(":", "_"));
            }
            fnb.append("_at_");
        }
        fnb.append(LOGFILEDATE.format(new Date()));
        fnb.append(".log");
        return fnb.toString();
    }

    /**
     * Builds the http header set for the request
     *
     * @param hostURL host url string
     * @param contentLength
     * @return string containing the formatted initial part of the http request
     */
    private String processHeaders(URL hostURL) {
        StringBuilder sb = new StringBuilder();

        sb.append(httpMethod).append(" ");
        if ((hostURL.getPath() != null) && (hostURL.getPath().trim().length() > 0)) {
            // NB changed to getFile from getPath this now includes any appended query string eg ?a=b&c=d etc
            sb.append(hostURL.getFile());
        } else {
            sb.append("/");
        }
        sb.append(" HTTP/1.1\r\n");

        headerManager.addHttpHeader("Host", hostURL.getHost());

        // some messages may not have a soap action (mod for REST)
        if (action != null && !action.isEmpty()) {
            if (action.trim().length() > 0 && action.trim().charAt(0) == '"') {
                headerManager.addHttpHeader(SOAP_ACTION_HEADER, action.trim());
            } else {
                headerManager.addHttpHeader(SOAP_ACTION_HEADER, "\"" + action.trim() + "\"");
            }
            // only add by default for SOAP requests, not necessarily true for FHIR requests
            headerManager.addHttpHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);
        }

        if (chunkSize == 0) {
            headerManager.addHttpHeader(CONTENT_LENGTH_HEADER, "" + message.length());
        } else {
            headerManager.addHttpHeader(TRANSFER_ENCODING_HEADER, TRANSFER_ENCODING_CHUNKED);
        }

        headerManager.addHttpHeader("Server", "NHS Digital ITK Test Platform");
        if (!suppressClose) {
            headerManager.addHttpHeader("Connection", "close");
        }

        // add any header values from the tkw.properties file
        try {
            Configurator config = Configurator.getConfigurator();
            Map hm = config.getConfigurationMap("^" + HTTP_HEADER_PROPERTY_PREFIX + ".*$");
            Iterator iter = hm.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = (String) hm.get(key);
                String headerName = key.replaceFirst("^" + HTTP_HEADER_PROPERTY_PREFIX, "");

                // java function specification
                if (value.startsWith(FUNCTION_PREFIX)) {
                    value = Utils.invokeJavaMethod(value);
                }
                headerManager.addHttpHeader(headerName, value);
            }
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, HttpSender.class.getName(), "Failed getting header values from properties file " + ex.getMessage());
        }

        // adds any extra headers defined by propertyset in tstp files
        if (reconfiguredProperties != null) {
            for (Object property : reconfiguredProperties.keySet()) {
                String key = property.toString();
                if (key.startsWith(HTTP_HEADER_PROPERTY_PREFIX)) {
                    headerManager.addHttpHeader(key.replaceFirst("^" + HTTP_HEADER_PROPERTY_PREFIX, ""), reconfiguredProperties.getProperty(key));
                }
            }
        }

        String contentType = headerManager.getHttpHeaderValue(CONTENT_TYPE_HEADER);
        if (contentType != null && contentType.contains("json")) {
            if (contentType.contains("fhir")) {
                // don't convert if it's not valid xml fhir it might well be valid fhir json
                if (message.trim().startsWith("<")) {
                    // do the fhir conversion and reset the content length if it's not chunked
                    String jsonMessage = fhirConvertXml2Json(message);
                    if (jsonMessage.contains(FHIRCONVERSIONFAILURE)) {
                        Logger.getInstance().log(SEVERE, HttpSender.class.getName(), "Failed converting fhir xml - json" + jsonMessage);
                    } else {
                        message = jsonMessage;
                    }
                }
            } else {
                Logger.getInstance().log(WARNING, HttpSender.class.getName(), "Content is json but not fhir");
            }
            if (chunkSize == 0) {
                headerManager.addHttpHeader(CONTENT_LENGTH_HEADER, "" + message.length());
            }
        }

        String encoding = headerManager.getHttpHeaderValue(CONTENT_ENCODING_HEADER);
        if (encoding != null && (encoding.equals(COMPRESSION_GZIP) || encoding.equals(COMPRESSION_DEFLATE))) {
            try {
                if (isBinaryPayloadString(message)) {
                    messageBytes = unwrapBinaryPayload(message);
                    messageBytes = Compressor.compress(messageBytes, Compressor.CompressionType.valueOf("COMPRESSION_" + encoding.toUpperCase()));
                } else {
                    messageBytes = Compressor.compress(message.getBytes(), Compressor.CompressionType.valueOf("COMPRESSION_" + encoding.toUpperCase()));
                }
                if (chunkSize == 0) {
                    headerManager.addHttpHeader(CONTENT_LENGTH_HEADER, "" + messageBytes.length);
                }

            } catch (IOException ex) {
                Logger.getInstance().log(SEVERE, HttpSender.class.getName(), "Failed compressing payload " + ex.getMessage());
            }
        } else {
            if (isBinaryPayloadString(message)) {
                messageBytes = unwrapBinaryPayload(message);
                headerManager.addHttpHeader(CONTENT_LENGTH_HEADER, "" + messageBytes.length);
            } else {
                messageBytes = message.getBytes();
            }
        }

        sb.append(headerManager.getHttpHeaders());

        // essential header terminator
        sb.append("\r\n");

        return sb.toString();
    }

    /**
     *
     * @param what
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public String reconfigure(String what, String value) throws Exception {
        return "";
    }

    /**
     *
     * @param p PropertySet to reconfigure to
     * @throws Exception
     */
    @Override
    public void reconfigure(Properties p) throws Exception {
        reconfiguredProperties = p;
        if (reconfiguredProperties != null) {
            suppressClose = isY(reconfiguredProperties.getProperty(SUPPRESS_CLOSE_PROPERTY, "N"));
            if (reconfiguredProperties.getProperty(FHIR_VERSION_PROPERTY) != null) {
                FHIRJsonXmlAdapter.setFhirVersion(FhirVersionEnum.valueOf(reconfiguredProperties.getProperty(FHIR_VERSION_PROPERTY).toUpperCase()));
            }
        }
    }
}
