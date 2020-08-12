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

import java.net.InetAddress;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import javax.net.ssl.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers.END_INBOUND_MARKER;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Implementation of Listener which listens for SSL connections.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SSLSocketListener
        extends Thread
        implements Listener {

    private static final String SERVER_SECURE_CONNECTION_FAILED = "Server - Secure Connection Failed";
    private static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyyMMddHH:mm:ss");

    private HttpServer server = null;
    private int port;
    private String host;
    private String localId;
    private Exception exception;
    private boolean keepGoing;

    private boolean mutualAuthentication = false;
    private String subjectDNfilter = null;
    private int ssbacklog;

    private static boolean notUsingSslContext = false;

    private SSLContext sslContext = null;
    private SSLServerSocket serverSocket = null;
    private boolean propsSetInternally = false;

    /**
     * Creates a new instance of SocketListener
     *
     * @throws java.lang.Exception
     */
    public SSLSocketListener()
            throws Exception {
        port = DEFAULTTLSLISTENPORT;
        host = DEFAULTHOST;
        exception = null;
        keepGoing = true;
        localId = host + ":" + Integer.toString(port);
        mutualAuthentication = isY(System.getProperty(ORG_DOMUTUALAUTH_PROPERTY));
        String p = System.getProperty(ORG_MUTUALAUTHFILTER_PROPERTY);
        if ((p != null) && (p.trim().length() != 0)) {
            subjectDNfilter = p;
        }
        p = System.getProperty(ORG_SSBACKLOG_PROPERTY);
        if ((p != null) && (p.trim().length() != 0)) {
            ssbacklog = Integer.parseInt(p);
            if (ssbacklog <= 0) {
                throw new Exception("Server Socket backlog is not a positive integer: " + ssbacklog);
            }
        }

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
            Logger.getInstance().log(WARNING, SSLSocketListener.class.getName(), "invoked using externally set org security properties");
        }
        initSSLContext();
    }

    /**
     * If the system properties are set correctly this will explictly open a
     * keystore failing that the default behaviour will be used which does not
     * support changing keystores
     *
     * @throws Exception
     */
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
    public void setPort(int p) {
        if ((p > 0) && (p < 65535)) {
            port = p;
        }
        localId = host + ":" + Integer.toString(port);
    }

    @Override
    public void setHost(String h) {
        if ((h != null) && (h.trim().length() > 0)) {
            host = h.trim();
        }
        localId = host + ":" + Integer.toString(port);
    }

    @Override
    public void stopListening() {
        keepGoing = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, SSLSocketListener.class.getName(), "Exception closing listener socket: " + e.toString());
        }
    }

    @Override
    public void startListening(HttpServer s)
            throws HttpServerException {
        server = s;
        start();
    }

    Exception getException() {
        return exception;
    }

    @Override
    public void run() {
        this.setName("SSLSocketListenerThread");
        try {
            SSLServerSocketFactory ssf = null;
            if (sslContext == null) {
                ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            } else {
                ssf = sslContext.getServerSocketFactory();
            }
            serverSocket = null;
            if (host.contentEquals("0.0.0.0")) {
                serverSocket = (SSLServerSocket) ssf.createServerSocket(port, ssbacklog, (InetAddress) null);
            } else {
                serverSocket = (SSLServerSocket) ssf.createServerSocket(port, ssbacklog, InetAddress.getByName(host));
            }
            if (mutualAuthentication) {
                serverSocket.setNeedClientAuth(true);
            }
            System.out.println("SSLSocketListener listening on " + host + ":" + port);
            while (keepGoing) {
                SSLSocket requestSocket = (SSLSocket) serverSocket.accept();
                SSLSession ssls = (SSLSession) requestSocket.getSession();

                String savedMessagesFolder = System.getProperty(ORG_SSL_SOCKET_LISTENER_SAVED_MESSAGES_FOLDER_PROPERTY);
                // if this is set in the system properties we have been invoked by Test with a TLSMA client test
                // we must drop out rather than retry after tlsma errors are detected
                boolean quitOnTLSMAFail = savedMessagesFolder != null;
                if (ssls.isValid()) {
                    String msg = "Server - Secure Connection Established Mutual Authentication = " + serverSocket.getNeedClientAuth();
                    Logger.getInstance().log(msg);
                    System.out.println(msg);

                    if (mutualAuthentication && (subjectDNfilter != null)) {
                        String s = acceptSubjectDN(requestSocket);
                        // check that the dns name is in the correct form
                        if (s != null) {
                            requestSocket.close();
                            System.err.println(s);
                            Logger.getInstance().log(s);
                            if (quitOnTLSMAFail) {
                                handleTlsmaFailure(savedMessagesFolder, s);
                            } else {
                                continue;
                            }
                        }
                    }
                } else {
                    Logger.getInstance().log(SEVERE, SSLSocketListener.class.getName(), SERVER_SECURE_CONNECTION_FAILED);
                    System.out.println(SERVER_SECURE_CONNECTION_FAILED);
                    if (quitOnTLSMAFail) {
                        handleTlsmaFailure(savedMessagesFolder, SERVER_SECURE_CONNECTION_FAILED);
                    }
                }  // valid ssl connection

                if (keepGoing) {
                    StringBuilder sb = new StringBuilder(localId);
                    sb.append(" from ");
                    sb.append(requestSocket.getInetAddress().getHostAddress());
                    new RequestReader((Socket) requestSocket, server, sb.toString());
                }
            } // while keepGoing

        } catch (java.net.SocketException se) {
        } catch (Exception e) {
            exception = e;
            Logger.getInstance().log(SEVERE, SSLSocketListener.class.getName(),
                    "Exception " + host + ":" + port + ", listener exiting: " + e.getMessage());
        }

        if (propsSetInternally) {
            System.getProperties().remove(ORG_USESSLCONTEXT_PROPERTY);
            System.getProperties().remove(ORG_SSLPASS_PROPERTY);
        }
    }

    /**
     * put a log file into the messages folder showing details of tlsma failure
     *
     * @param savedMessagesFolder
     * @param s failure message
     * @throws IOException
     */
    private void handleTlsmaFailure(String savedMessagesFolder, String s) throws IOException {
        Utils.writeString2File(savedMessagesFolder + "/" + TIMESTAMP.format(new Date()) + "_tlsmafailed.log",
                "Failed " + s + "\r\n"
                + END_INBOUND_MARKER + "\r\n");
        keepGoing = false;
    }

    private String acceptSubjectDN(SSLSocket s) {
        String r = null;
        try {
            javax.security.auth.x500.X500Principal p = (javax.security.auth.x500.X500Principal) s.getSession().getPeerPrincipal();
            String n = p.getName();
            if (n == null) {
                r = "Null peer subject name";
            } else if (!n.contains(subjectDNfilter)) {
                r = "Peer subject DN: " + n + ", expected to contain: " + subjectDNfilter;
            }
        } catch (Exception e) {
            r = "Exception checking peer subject DN: " + e.toString();
        }
        return r;
    }
}
