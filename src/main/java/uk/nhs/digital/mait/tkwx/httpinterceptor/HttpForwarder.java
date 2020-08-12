/*
 Copyright 2014 Damian Murphy <damian.murphy@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.httpinterceptor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Level;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.http.HttpTimer;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.MAEstablished;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SPSetter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Class used to forward an incoming request to an http server. The class will
 * create an appropriate header and forward to the endpoint. The synchronous
 * response is then received and passed back to the initial requestor ASAP.<BR>
 *
 * Validation is performed on the request and the log written to a folder named
 * for the value extracted from the request using a configurable xpath. The
 * default configuration extracts the sending asid.<BR>
 *
 * By default generic validation is applied but if the soap action in the
 * request has a match in a mapping table then a more domain specific validation
 * can be applied. If there is no matching entry in the table then the default
 * validation is applied.<BR><BR>
 *
 * <table align="center">
 * <caption><b>HttpInterceptor tkw properties</b></caption>
 * <tr><td>tks.httpinterceptor.forwardingaddress</td><td>ip address to forward
 * request to</td></tr>
 * <tr><td>tks.httpinterceptor.forewardingport</td><td>port number to forward
 * request to</td></tr>
 * <tr><td>tks.httpinterceptor.spine.discriminator</td><td>xpath to use to
 * extract folder name for spine messages</td></tr>
 * <tr><td>tks.httpinterceptor.itk.discriminator</td><td>xpath to use to extract
 * folder name for itk messages</td></tr>
 * <tr><td>tks.validator.config</td><td>default validation conf to use if no
 * soap action match</td></tr>
 * <tr><td>tks.validator.config.&lt;domain&gt;</td><td>domain specific
 * validation set named for domain eg tks.validator.config.pds</td></tr>
 * <tr><td>tks.validator.validationmap</td><td>path to soap action validation
 * domain lookup properties file</td></tr>
 * </table>
 *
 * @author Damian Murphy damian.murphy@hscic.gov.uk
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class HttpForwarder
        extends Thread {

    private HttpInterceptWorker httpInterceptWorker = null;
    private byte[] buffer = null;
    private HttpResponse response = null;
    private HttpRequest incomingRequest = null;
    private static SSLContext sslContext = null;
    private boolean tlsMutualAuthentication = false;
    private boolean tls = false;
    private Configurator configurator;
    private static boolean notUsingSslContext = false;
    private Logger logger;
    //private long start = System.currentTimeMillis();

    //private static long cumulative = 0;
    private static final String WARNING_PREFIX = "Warning: Property ";
    private static final String WARNING_SUFFIX = " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail";
    private String metaDataThreadId = null;

    /**
     * A constructor passing the Http Request allowing access to the log
     *
     * @param r
     */
    public HttpForwarder(HttpInterceptWorker r) {
        try {
            httpInterceptWorker = r;
            configurator = Configurator.getConfigurator();
            tls = isY(configurator.getConfiguration(SEND_USETLS_PROPERTY));
            if (tls) {
                try {
                    initSSLContext();
                    tlsMutualAuthentication = isY(configurator.getConfiguration(CLIENT_TLSMA_PROPERTY));
                } catch (Exception e) {
                    Logger.getInstance().log(SEVERE, HttpForwarder.class.getName(), "initialising SSL context for sending: " + e.toString());
                }
            }
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, HttpForwarder.class.getName(), "Error getting configurator");
        }
    }

    /**
     * Method used to create a new thread in which to forward the request
     * message from
     *
     * @param buf the content of the request body
     * @param resp object of the outgoing HTTP response
     * @param req object of the incoming HTTP request
     */
    public void forward(byte[] buf, HttpResponse resp, HttpRequest req) {
        buffer = buf;
        response = resp;
        incomingRequest = req;
        // register that a subthread has started which will require the EvidenceMetaDataHandler to remain open 
        if (httpInterceptWorker.evidenceMetaDataHandler != null) {
            metaDataThreadId = UUID.randomUUID().toString();
            httpInterceptWorker.evidenceMetaDataHandler.subThreadStarted(metaDataThreadId);
        }
        start();
    }

    private void initSSLContext()
            throws Exception {
        if (!notUsingSslContext && (sslContext == null)) {
            String ksf = configurator.getConfiguration(ORG_USESSLCONTEXT_PROPERTY);
            if (ksf == null) {
                notUsingSslContext = true;
                return;
            }
            String p = configurator.getConfiguration(ORG_SSLPASS_PROPERTY);
            if (p == null) {
                p = "";
            }
            String alg = configurator.getConfiguration(ORG_SSLALGORITHM_PROPERTY);
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

    /**
     *
     * @param url
     * @return the opened socket
     */
    private Socket createSocket(URL url) {
        Socket socket = null;
        SocketFactory sf = null;
        if (tls) {
            // if the forwarder has been configured in the clear to receive but via ssl to send then these system properties may need setting 
            if (System.getProperty("javax.net.ssl.trustStore") == null) {
                try {
                    SPSetter.executeSettings(configurator, new String[][]{
                        {TRUSTSTORE_PROPERTY, "javax.net.ssl.trustStore", WARNING_PREFIX + TRUSTSTORE_PROPERTY + WARNING_SUFFIX},
                        {TRUSTPASS_PROPERTY, "javax.net.ssl.trustStorePassword", WARNING_PREFIX + TRUSTPASS_PROPERTY + WARNING_SUFFIX},
                        {KEYSTORE_PROPERTY, "javax.net.ssl.keyStore", WARNING_PREFIX + KEYSTORE_PROPERTY + WARNING_SUFFIX},
                        {KEYPASS_PROPERTY, "javax.net.ssl.keyStorePassword", WARNING_PREFIX + KEYPASS_PROPERTY + WARNING_SUFFIX}});
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, HttpForwarder.class.getName() + ".createSocket", "Failed to set system properties " + ex.getMessage());
                }
            }

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
                if (url.getPort() > 0) {
                    addr = new InetSocketAddress(url.getHost(), url.getPort());
                } else {
                    addr = new InetSocketAddress(url.getHost(), url.getDefaultPort());
                }
                socket.connect(addr);
            } catch (Exception e) {
                logger.log(HttpForwarder.class.getName() + ".run", "Failed to create outbound SSL socket: " + e.getMessage());
                return null;
            }
        } else {
            try {
                sf = SocketFactory.getDefault();
                if (url.getPort() > 0) {
                    socket = sf.createSocket(url.getHost(), url.getPort());
                } else {
                    socket = sf.createSocket(url.getHost(), url.getDefaultPort());
                }
            } catch (Exception e) {
                logger.log(SEVERE, HttpForwarder.class.getName(), "Failed to create outbound socket: " + e.getMessage());
                return null;
            }
        }
        //System.out.println("Opening socket on " + url);
        return socket;
    }

    /**
     * Runnable thread override open socket forward request onto forwarding
     * endpoint receive response back from forwarding endpoint
     */
    @Override
    public void run() {
        this.setName("HttpForwarderThread");
       //System.out.printf(HttpForwarder.class.getName() + ".run started with request %x\r\n", response.hashCode());
        // TODO NEXT: Implement. Make sure all exceptions are reported but otherwise
        // absorbed. 
        logger = Logger.getInstance();

        try {
            String listenPortStr = configurator.getConfiguration("tks.HttpTransport.listenport");
            int listenPort = Integer.parseInt(listenPortStr);
            if (httpInterceptWorker.getForwardingPort() == listenPort) {
                String listenAddress = configurator.getConfiguration("tks.HttpTransport.listenaddr");
                if (httpInterceptWorker.getForwardingAddress().equals(listenAddress)
                        || (Arrays.asList(new String[]{"0.0.0.0", "localhost", "127.0.0.1"}).contains(listenAddress)
                        && (Arrays.asList(new String[]{"localhost", "127.0.0.1"}).contains(httpInterceptWorker.getForwardingAddress())))) {
                    logger.log(WARNING, HttpForwarder.class.getName(),
                            "Forwarding url (" + httpInterceptWorker.getForwardingAddress() + ":" + httpInterceptWorker.getForwardingPort()
                            + ") matches listen url (" + listenAddress + ":" + listenPort + "). Unterminated simulator configs may cause infinite looping.");
                }
            }
        } catch (Exception ex) {
        }

        // destination address from the request header
//      String hostHeader = incomingRequest.getField("Host");
        // assume proxy aware so default to forwarding details from the request header
//        URL forwardingURL = getForwardingURL(getURL(hostHeader));
        // disable handling for proxy aware all messages are forwarded to the configured endpoint
        URL forwardingURL = getURL(httpInterceptWorker.getForwardingAddress() + ":" + httpInterceptWorker.getForwardingPort());
        Socket forwardingSocket = createSocket(forwardingURL);
        String activity = null;

        // TODO this code fragment was cloned (the header close manipulation) from ers it may well not be applicable for a generic http interceptor
        // Creates a header with all the parts of the incoming request but making sure that all requests are closed
        StringBuilder sbHeaders = new StringBuilder();
        sbHeaders.append(incomingRequest.getRequestType()).append(" ").append(incomingRequest.getContext()).append(" ").append(incomingRequest.getVersion()).append("\r\n");

        // set this to true if we want to add a connection close when no connection header is present
        // this is to mimic ers but is probably not required for a more general interceptor
        // NB the connection to the forwarding endpoint is closed after the response is sent regardless
        boolean requiresClose = false; // NB the equivalent boolean was set true in the superseded ers code but this seems to work anyway.

        for (String header : incomingRequest.getFieldNames()) {
            sbHeaders.append(header);
            sbHeaders.append(": ");
            String value = incomingRequest.getField(header);
            // Modified so that close is only added if there is no connection header present
            if (header.toLowerCase().trim().equals("connection")) {
                requiresClose = false;
            }
            sbHeaders.append(value);
            sbHeaders.append("\r\n");
        }
        if (requiresClose) {
            sbHeaders.append("Connection: close\r\n");
        }

        sbHeaders.append("\r\n");
        try {
            activity = "Creating reader";
            InputStream forwardingInputStream = forwardingSocket.getInputStream();
            activity = "Creating header";
            OutputStream forwardingOutputStream = forwardingSocket.getOutputStream();
            activity = "Writing header";
            forwardingOutputStream.write(sbHeaders.toString().getBytes());
            activity = "Flushing header";
            forwardingOutputStream.flush();
            if (incomingRequest.getContentLength() > 0) {
                activity = "Writing body";
                forwardingOutputStream.write(buffer);
                activity = "Flushing body";
                forwardingOutputStream.flush();
            }

            if (!tls) {
                activity = "Shutting down output";
                forwardingSocket.shutdownOutput();
            }
            // sets the response so that no header will be created as it is already part of the incoming response from the forwarding endpoint
            response.hasHttpHeader(false);

            HttpTimer timer = new HttpTimer(forwardingSocket);
            // Write the rest of the data back to the log file and client
            int c = 0;
            activity = "Clearing input data";
            boolean responseReceived = false;
            StringBuilder sbResponse = new StringBuilder();
            // receive incoming response from the forwarding endpoint character by character
            while ((c = forwardingInputStream.read()) != -1) {
                responseReceived = true;
                // send the response character back to the client
                response.getOutputStream().write(c);
                sbResponse.append(Character.toString((char) c));
            }
//          System.out.println("Finished reading and forwarding response from forwarding endpoint after " + (System.currentTimeMillis() - start) + " ms");
            //response.getOutputStream().flush();
            if (!responseReceived) {
// DO SOMETHING FOR THIS
//                fw.write("\r\nNo HTTP response received - input stream already at EOF\r\n");
//                fw.flush();
            }
            if (!tls) {
                activity = "Shutting down input";
                forwardingSocket.shutdownInput();
            }
            activity = "Closing";
            forwardingSocket.close();
            timer.stopTimer();
            // System.out.println("Forward.flush@" + new Date());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            incomingRequest.setHandled(true);

            // log the request body and response 
            incomingRequest.log(buffer, sbResponse.toString().getBytes());
        } catch (SocketException se) {
            try {
                forwardingSocket.close();
            } catch (IOException ex) {
                logger.log(SEVERE, HttpForwarder.class.getName(), "Failed to close forwardingSocket " + ex.getMessage());
            }
            logger.log(WARNING, HttpForwarder.class.getName(), "Response from forwarding endpoint timed out " + forwardingURL.getHost() + " : " + se + " " + activity);
        } catch (Exception e) {
            logger.log(SEVERE, HttpForwarder.class.getName(), "Failed to send message to address " + forwardingURL.getHost() + " : " + e.getMessage() + " : " + activity);
        }
        //long duration = System.currentTimeMillis() - start;
        //cumulative += duration;
        //System.out.println("HttpForwarder.run terminated with request " + response + " after " + duration + " ms total = " + (cumulative / 1000) + " s");

        // indicate to the EvidecnMetatDataHandler that this subthread has ended and the evidecnce can be attempted to be written
        if (httpInterceptWorker.evidenceMetaDataHandler != null) {
            httpInterceptWorker.evidenceMetaDataHandler.subThreadEnded(metaDataThreadId);
        }
    }

    /**
     * construct a url from the content of the http host header field
     *
     * @param hostHeader
     * @return constructed URL object
     */
    private URL getURL(String hostHeader) {
        URL url = null;
        try {
            // NB if you don't use this single parameter signature you may get the address parsed as ipv6
            // in which case the protocol suffix just looks like the last part of the ip address and the
            // host string is returned surrounded by [ and ] beware!
            // we can add back the http because we know it is (or at worst https which wont affect the bits we are parsing anyway).
            url = new URL("http://" + hostHeader);
        } catch (MalformedURLException ex) {
            logger.log(SEVERE, HttpForwarder.class.getName(), "Malformed URL in http request host header " + hostHeader + " : " + ex.getMessage());
        }
        return url;
    }

    /**
     * checks all this machine's ip addresses to see if there's a match with the
     * host header URL if there is one then we assume the sending client is not
     * proxy aware so we return the forwarding url from the config otherwise we
     * honour the address in the host header and forward to that. NB Proxy aware
     * means eg a browser configured to talk to a proxy. Normally a browser
     * splits the url into the host address and the context path. It connects to
     * the host then passes just the context path to the endpoint. A proxy aware
     * client passes connects to the proxy and passes the whole url to the
     * proxy.
     *
     * @param hostHeaderURL
     * @return forwardingURL
     * @throws NumberFormatException
     */
    private URL getForwardingURL(URL hostHeaderURL) throws NumberFormatException {
        // default to the url in the host header
        URL forwardingURL = hostHeaderURL;
        try {
            /*
             check there's no match with the address in host header and this endpoint
             this only checks for a match on ip address not port number so if the host header ip adress matches
             the configured forwarding address will be used.
             if the host ip address does not match then the message will be forwarded to the header address
             this means that currently we can't use a proxy aware client to redirect to different *local* ports.
             This may need refinement at a later date
             */
            boolean match = false;
            if (hostHeaderURL.getHost().equals("localhost")) {
                match = true;
            } else {
                String hostHeaderAddress = null;
                try {
                    hostHeaderAddress = InetAddress.getByName(hostHeaderURL.getHost()).getHostAddress();
                } catch (UnknownHostException ex) {
                }
                if (hostHeaderAddress != null) {
                    Enumeration<NetworkInterface> eNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (!match && eNetworkInterfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = eNetworkInterfaces.nextElement();
                        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                if (interfaceAddress.getAddress().getHostAddress().equals(hostHeaderAddress)) {
                                    //   if (configuredListenPort == hostPort) {
                                    match = true;
                                    break;
                                    //   }
                                }
                            }
                        }
                    }
                } else {
                    // lookup failed failover to the configured forwarding adress
                    match = true;
                }
            }
            try {
                if (match) {
                    /*
                    if we get here the host endpoint ip address in the request header matches this endpoint ip address
                    this implies that the sending system is *not* proxy aware
                    in which case forward to the configured forwarding address
                     */
                    forwardingURL = new URL(tls ? "https" : "http", httpInterceptWorker.getForwardingAddress(), httpInterceptWorker.getForwardingPort(), "");
                }
            } catch (MalformedURLException e) {
                logger.log(SEVERE, HttpForwarder.class.getName(), "Cannot parse forwarding address URL " + httpInterceptWorker.getForwardingAddress() + ":" + httpInterceptWorker.getForwardingPort() + " " + e.getMessage());
            }
        } catch (SocketException ex) {
            logger.log(SEVERE, HttpForwarder.class.getName(), "Socket Exception getting network interfaces " + ex.getMessage());
        }
        return forwardingURL;
    }
}
