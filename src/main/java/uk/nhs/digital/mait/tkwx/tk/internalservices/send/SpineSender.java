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

import uk.nhs.digital.mait.commonutils.util.Logger;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.URL;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.InputStreamReader;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
import uk.nhs.digital.mait.tkwx.itklogverifier.LogVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import static uk.nhs.digital.mait.tkwx.util.HttpChunker.chunk;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpLogFileGenerator;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Thread for transmitting SOAP messages.
 *
 * @author DAMU2
 */
public class SpineSender
        extends java.lang.Thread {

    private static final SimpleDateFormat LOGFILEDATE = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

    private static boolean notUsingSslContext = false;
    private boolean tlsMutualAuthentication = false;

    private static SSLContext sslContext = null;

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
    private EvidenceMetaDataHandler evidenceMetaDataHandler = null;
    private String metaDataThreadId = null;

    public SpineSender(ToolkitSimulator tk, SenderRequest what, boolean t, String logdir) {
        simulator = tk;
        req = what;
        messageID = what.getRelatesTo();
        message = what.getPayload();
        address = what.getAddress();
        wrapper = what.getWrapperTemplate();
        logDirectory = logdir;
        action = what.getAction();
        evidenceMetaDataHandler = what.getEvidenceMetaDataHandler();

        chunkSize = what.chunkSize();
        tls = t;
        if (tls) {
            try {
                initSSLContext();
                tlsMutualAuthentication = isY(System.getProperty(SENDTLSMA_PROPERTY));
            } catch (Exception e) {
                Logger.getInstance().log(SEVERE, SpineSender.class.getName(), "Error initialising SSL context for sending: " + e.toString());
            }
        }
        // register that a subthread has started which will require the EvidenceMetaDataHandler to remain open 
        if (evidenceMetaDataHandler != null) {
            metaDataThreadId = UUID.randomUUID().toString();
            evidenceMetaDataHandler.subThreadStarted(metaDataThreadId);
        }
        start();
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
        this.setName("SpineSenderThread");
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
        try {
            if (action == null) {
                action = xpathExtractor(SOAP_HEADER_WSASPINE_ACTION, message);
            }
        } catch (Exception e) {
            l.log("Sender", "Failed to get SOAPaction: " + e.getMessage());
        }
        try {
            sendDirect(u, l);
        } catch (Exception e) {
            l.log("Sender", "Error trying to send: " + e.getMessage());
        }
        // indicate to the EvidecnMetatDataHandler that this subthread has ended and the evidecnce can be attempted to be written
        if (evidenceMetaDataHandler != null) {
            evidenceMetaDataHandler.subThreadEnded(metaDataThreadId);
        }
    }

    private void sendDirect(URL u, Logger l)
            throws Exception {
// extract the MessageID to make the logfile unique
//        try {
//             XPath x = XPathManager.newInstance().newXPath();
//             x.setNamespaceContext(SpineCfHNamespaceContext.getXMLNamespaceContext());
//             XPathExpression actionXpath = x.compile(MESSAGEID);
//             InputSource is = new InputSource(new StringReader(message));
//             messageID = actionXpath.evaluate(is);
//             if(messageID.indexOf(":")!= -1){
//                 messageID = messageID.replace(":","_");
//             }
//        }
//        catch (Exception e) {
//                l.log("Sender", "Failed to get MessageID: " + e.getMessage());
//        }
        String header = null;
        if (tls && !u.getProtocol().equalsIgnoreCase("https")) {
            l.log("Sender-direct", "Mismatch: TLS requested but address URL " + address + " does not agree - continuing");
        }
        StringBuilder fileName = new StringBuilder();
        if (req.getOriginalFileName() != null) {
            fileName.append(req.getOriginalFileName());
            fileName.append("_");
        }
        fileName.append(u.getHost());
        fileName.append("_sent_");
        fileName.append(messageID);
        fileName.append("_at_");
        
        // add a subdirectory if it is set
        String fname = HttpLogFileGenerator.createLogFile(fileName.toString(), logDirectory, req.getLoggingSubDir());
        LoggingFileOutputStream lfos = new LoggingFileOutputStream(fname);
        lfos.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
        lfos.setMetaDataDescription("interaction-log", "Transmitter");
        StringBuilder sb = new StringBuilder();
        sb.append("POST ");
        if ((u.getPath() != null) && (u.getPath().trim().length() > 0)) {
            sb.append(u.getPath());
        } else {
            sb.append("/");
        }
        sb.append(" HTTP/1.1\r\nHost: ");
        sb.append(u.getHost());
        sb.append("\r\nSOAPaction: ");
        if (action != null) {
            if (action.trim().length() > 0 && action.trim().charAt(0) == '"') {
                sb.append(action.trim());
            } else {
                sb.append("\"");
                sb.append(action.trim());
                sb.append("\"");
            }
        }
        if (chunkSize == 0) {
            sb.append("\r\nContent-Length: ");
            sb.append(message.length());
        } else {
            sb.append("\r\nTransfer-Encoding: chunked");
        }
        sb.append("\r\n");
        // is the message a multipart mime?
        if (message.trim().startsWith("--")) {
            sb.append("Content-type: multipart/related; boundary=\"--=_MIME-Boundary\"; type =\"text/xml\"; start=\"<ebXMLHeader@spine.nhs.uk>\"\r\n");
        } else {
            sb.append("Content-type: text/xml\r\n");
        }
        sb.append("Connection: close\r\n\r\n");
        // sb.append(message);
        header = sb.toString();
        lfos.write(header);
        lfos.flush();
        lfos.write(message);
        lfos.write("\r\n" + LogMarkers.END_REQUEST_MARKER + "\r\n");
        lfos.flush();

        if (System.getProperty(NOORIGINATE_PROPERTY) != null) {
            lfos.write("\n\nSender NOT transmitting due to internal request: ");
            lfos.write(System.getProperty(NOORIGINATE_PROPERTY));
            lfos.flush();
            lfos.logComplete();
            lfos.close();
            // only inhibit on a specific Y for this value
            if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
                LogVerifier lv = LogVerifier.getInstance();
                lv.makeSignature(fname);
            }
            return;
        }

        lfos.flush();

        Socket s = null;
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
                s = sf.createSocket();
                if (tlsMutualAuthentication) {
                    ((SSLSocket) s).setNeedClientAuth(true);
                    ((SSLSocket) s).addHandshakeCompletedListener(new MAEstablished());
                }
                InetSocketAddress addr = null;
                if (u.getPort() > 0) {
                    addr = new InetSocketAddress(u.getHost(), u.getPort());
                } else {
                    addr = new InetSocketAddress(u.getHost(), 443);
                }
                s.connect(addr);
            } catch (Exception e) {
                l.log("Sender-direct", "Failed to create outbound SSL socket: " + e.getMessage());
                return;
            }
        } else {
            try {

                sf = SocketFactory.getDefault();
                if (u.getPort() > 0) {
                    s = sf.createSocket(u.getHost(), u.getPort());
                } else {
                    s = sf.createSocket(u.getHost(), 80);
                }
            } catch (Exception e) {
                String report = "Failed to create outbound socket: " + e.getMessage();
                l.log("Sender-direct", report);
                lfos.write(report);
                lfos.flush();
                lfos.logComplete();
                lfos.close();
                // only inhibit on a specific Y for this value
                if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
                    LogVerifier lv = LogVerifier.getInstance();
                    lv.makeSignature(fname);
                }
                return;
            }
        }
        String activity = null;
        try {
            activity = "Creating reader";
            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            activity = "Creating header";
            OutputStream os = s.getOutputStream();
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
                os.write(message.getBytes());
                activity = "Flushing message";
                os.flush();
            } else {
                chunk(message.getBytes(), chunkSize, os);
            }
            if (!tls) {
                activity = "Shutting down output";
                s.shutdownOutput();
            }

            // Write the rest of the data back to the log file
            int c = 0;
            activity = "Clearing input data";
            boolean responseReceived = false;
            StringBuilder sbr = new StringBuilder();
            while ((c = isr.read()) != -1) {
                responseReceived = true;
                sbr.append(Character.toString((char) c));
            }
            lfos.write(sbr.toString());
            lfos.flush();

            if (!responseReceived) {
                lfos.write("\r\nNo HTTP response received - input stream already at EOF\r\n");
                lfos.flush();
            }

            if (!tls) {
                activity = "Shutting down input";
                s.shutdownInput();
            }
            activity = "Closing";
            s.close();
            lfos.flush();
            // only inhibit on a specific Y for this value
            if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
                LogVerifier lv = LogVerifier.getInstance();
                lv.makeSignature(fname);
            }
        } catch (Exception e) {
            String report = "Failed to send message to address " + address + " : " + e.getMessage() + " : " + activity;
            l.log("Sender-direct", report);
            System.err.println(report);
            lfos.write(report);
            lfos.flush();
        }
        lfos.flush();
        lfos.logComplete();
        lfos.close();
    }
}
