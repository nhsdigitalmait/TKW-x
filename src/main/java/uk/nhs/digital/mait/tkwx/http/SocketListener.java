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

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;

import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Implementation of the listener interface to listen for clear-text
 * connections.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SocketListener
        extends Thread
        implements Listener {

    private HttpServer server = null;
    private ServerSocket serverSocket = null;
    private int port;
    private String host;
    private String localId;
    private Exception exception;
    private boolean keepGoing;
    private int ssbacklog;

    private static final int DEFAULTPORT = 80;

    /**
     * Creates a new instance of SocketListener
     */
    public SocketListener()
            throws Exception {
        port = DEFAULTPORT;
        host = DEFAULTHOST;
        exception = null;
        keepGoing = true;
        localId = host + ":" + Integer.toString(port);
        String p = System.getProperty(ORG_SSBACKLOG_PROPERTY);
        if ((p != null) && (p.trim().length() != 0)) {
            ssbacklog = Integer.parseInt(p);
            if (ssbacklog <= 0) {
                throw new Exception("Server Socket backlog is not a positive integer: " + ssbacklog);
            }
        }

    }

    /**
     * Set the port for listening if not the default 80
     *
     * @param p
     */
    @Override
    public void setPort(int p) {
        if ((p > 0) && (p < 65535)) {
            port = p;
        }
        localId = host + ":" + Integer.toString(port);
    }

    /**
     * Set the host to listen on if not the default localhost. May be 0.0.0.0 to
     * listen on all available network interfaces.
     *
     * @param h
     */
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
            Logger.getInstance().log(WARNING, SocketListener.class.getName(), "Exception closing listener socket: " + e.toString());
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
        this.setName("SocketListenerThread:"+port);
        try {
            serverSocket = new ServerSocket();
            if (host.contentEquals("0.0.0.0")) {
                serverSocket.bind(new InetSocketAddress((java.net.Inet4Address) null, port), ssbacklog);
            } else {
                serverSocket.bind(new InetSocketAddress(host, port), ssbacklog);
            }
            System.out.println("SocketListener listening on " + host + ":" + port);
            while (keepGoing) {
                Socket requestSocket = serverSocket.accept();
                if (keepGoing) {
                    StringBuilder sb = new StringBuilder(localId);
                    sb.append(" from ");
                    sb.append(requestSocket.getInetAddress().getHostAddress());
                    new RequestReader(requestSocket, server, sb.toString());
                }
            }
        } catch (java.net.SocketException se) {
        } catch (Exception e) {
            exception = e;
            Logger.getInstance().log(WARNING, SocketListener.class.getName(),
                    "Exception " + host + ":" + port + ", listener exiting: " + e.getMessage());
        }

    }
}
