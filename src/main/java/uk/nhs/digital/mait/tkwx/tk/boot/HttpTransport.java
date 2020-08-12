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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.http.HttpContext;
import uk.nhs.digital.mait.tkwx.http.HttpServer;
import uk.nhs.digital.mait.tkwx.http.Listener;
import uk.nhs.digital.mait.tkwx.http.SocketListener;
import uk.nhs.digital.mait.tkwx.http.SSLSocketListener;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ReconfigureTags;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SPSetter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Core TKW service.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpTransport
        implements ToolkitService,
        uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable, 
        uk.nhs.digital.mait.tkwx.tk.internalservices.Stoppable {

    private static final String SDSREFERENCE = "tks.spine.sds.reference";
    private static final String INTERACTIONMAP = "tks.spine.interaction.map";
    private static final String MESSAGEHEADERTEMPLATE = "tks.spine.ebxml.headertemplate";
    private static final String WSHEADERTEMPLATE = "tks.spine.webservice.headertemplate";
    private static final String MYASID = "tks.spine.my.asid";
    private static final String MYACKCPAID = "tks.spine.my.ack.cpaid";

    private static final String PROOT = "tks.";
    private static final String PORTNAMES = ".namelist";
    private static final String LISTENADDR = ".listenaddr";
    private static final String LISTENPORT = ".listenport";
    private static final String ADMINLISTENADDR = ".adminlistenaddr";
    private static final String ADMINLISTENPORT = ".adminlistenport";

    private static final String SMSP = "tks.SMSP";
    private static final String SPINECERT = "tks.SMSP.spine.certs";
    private static final String SPINESSLCONTEXTPASS = "tks.SMSP.spine.sslcontextpass";
    private static final String SPINETRUST = "tks.SMSP.spine.trust";
    private static final String SPINETRUSTPASS = "tks.SMSP.spine.trustpass";

    private static final String NAMEPATH = ".path";
    private static final String NAMECLASS = ".class";
    private static final String NAMETTL = ".ttl";

    private static final String DEFAULTASYNCTTLPROPERTY = "tks.HttpTransport.default.asyncttl";
    private static final int DEFAULTASYNCTTL = 10;

    private boolean tls = false;
    private ToolkitSimulator simulator = null;
    private String serviceName = null;
    private HttpServer server = null;
    private Properties bootProperties = null;
    private ArrayList<ToolkitHttpHandler> handlers = null;

    public HttpTransport() {
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void stop()
            throws Exception {
        server.stop();
    }

    @Override
    public void reconfigure(Properties p)
            throws Exception {
//        throw new Exception("Cannot restart from properties in this version");
        server.stop();
        boot(simulator, p, serviceName);
    }

    @Override
    public String reconfigure(String what, String value)
            throws Exception {
        if (what.contentEquals(ReconfigureTags.SAVED_MESSAGES)) {
            for (ToolkitHttpHandler t : handlers) {
                t.setSavedMessagesDirectory(value);
            }
            return null;
        }
        if (what.contentEquals(ReconfigureTags.ASYNCHRONOUS_TIMESTAMP_OFFSET)) {
            for (ToolkitHttpHandler t : handlers) {
                t.setAsynchronousOffsetSeconds(Integer.parseInt(value));
            }
            return null;
        }
        throw new Exception("Cannot reconfigure " + what);
    }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        bootProperties = p;
        simulator = t;
        serviceName = s;
        String usetls = p.getProperty(RECEIVE_USETLS_PROPERTY);
        if (usetls != null) {
            if (usetls.toUpperCase().charAt(0) == 'Y') {
                tls = true;
            }
            String tlsma = p.getProperty(SERVER_TLSMA_PROPERTY);
            if (isY(tlsma)) {
                System.setProperty(ORG_DOMUTUALAUTH_PROPERTY, tlsma);
                String tlsmafilter = p.getProperty(TLSMAFILTER_PROPERTY);
                if (tlsmafilter != null) {
                    System.setProperty(ORG_MUTUALAUTHFILTER_PROPERTY, tlsmafilter);
                }
            }

        }
        String listenAddr = p.getProperty(PROOT + serviceName + LISTENADDR);
        int listenPort = Integer.parseInt(p.getProperty(PROOT + serviceName + LISTENPORT));
        if (listenPort == 0) {
            if (tls) {
                listenPort = DEFAULTTLSLISTENPORT;
            } else {
                listenPort = DEFAULTLISTENPORT;
            }
        }
        String adminListenAddr = p.getProperty(PROOT + serviceName + ADMINLISTENADDR);
        int adminListenPort = -1;
        if (p.getProperty(PROOT + serviceName + ADMINLISTENPORT) != null) {
            adminListenPort = Integer.parseInt(p.getProperty(PROOT + serviceName + ADMINLISTENPORT));
        }
        server = new HttpServer();

        String opnames = p.getProperty(PROOT + serviceName + PORTNAMES);
        if (opnames == null) {
            throw new Exception("ToolkitHandler booted with no service ports: " + PROOT + serviceName + PORTNAMES + " property not defined");
        }
        startHandlers(p, opnames);
        Listener l = null;
        String ssb = p.getProperty(SSBACKLOG_PROPERTY);
        if (ssb == null) {
            Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + SSBACKLOG_PROPERTY + " not set");
        } else {
            int b = Integer.parseInt(ssb);
            if (b <= 0) {
                throw new Exception("Backlog rate is not a positive integer: " + ssb);
            }
            System.setProperty(ORG_SSBACKLOG_PROPERTY, p.getProperty(SSBACKLOG_PROPERTY));
        }

        SPSetter.executeSettings(p, new String[][]{
            {SDSREFERENCE, "uk.nhs.digital.mait.tkwx.spine.sds.reference"},
            {INTERACTIONMAP, "uk.nhs.digital.mait.tkwx.spine.interaction.map"},
            {MESSAGEHEADERTEMPLATE, "uk.nhs.digital.mait.tkwx.spine.message.headertemplate"},
            {WSHEADERTEMPLATE, "uk.nhs.digital.mait.tkwx.spine.webservice.headertemplate"},
            {MYASID, "mth.my.asid"},
            {MYACKCPAID, "mth.my.ack.cpaid"},});

        if (p.getProperty(SMSP) != null) {
            if (p.getProperty(SMSP).toUpperCase().charAt(0) == 'Y') {
                SPSetter.executeSettings(p, new SPSetter[]{
                    new SPSetter(SPINECERT, "uk.nhs.digital.mait.tkwx.http.spine.certs", s1 -> {
                        Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + SPINECERT + " not set");
                    }, null),
                    new SPSetter(SPINESSLCONTEXTPASS, "uk.nhs.digital.mait.tkwx.http.spine.sslcontextpass", s1 -> {
                        Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + SPINESSLCONTEXTPASS + " not set");
                    }, null),
                    new SPSetter(SPINETRUST, "uk.nhs.digital.mait.tkwx.http.spine.trust", s1 -> {
                        Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + SPINETRUST + " not set");
                    }, null),
                    new SPSetter(SPINETRUSTPASS, "uk.nhs.digital.mait.tkwx.http.spine.trustpass", s1 -> {
                        Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + SPINETRUSTPASS + " not set");
                    }, null),});
            }
        }
        if (tls) {
            SPSetter.executeSettings(p, new SPSetter[]{
                new SPSetter(TRUSTSTORE_PROPERTY, "javax.net.ssl.trustStore", s1 -> {
                    Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + TRUSTSTORE_PROPERTY + " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail");
                }, null),
                new SPSetter(TRUSTPASS_PROPERTY, "javax.net.ssl.trustStorePassword", s1 -> {
                    Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + TRUSTPASS_PROPERTY + " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail");
                }, null),
                new SPSetter(KEYSTORE_PROPERTY, "javax.net.ssl.keyStore", s1 -> {
                    Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + KEYSTORE_PROPERTY + " not set, TLS initialisation may fail");
                }, null),
                new SPSetter(KEYPASS_PROPERTY, "javax.net.ssl.keyStorePassword", s1 -> {
                    Logger.getInstance().log(WARNING, HttpTransport.class.getName(), "Property " + KEYPASS_PROPERTY + " not set, TLS initialisation may fail");
                }, null),});
            l = new SSLSocketListener();
        } else {
            l = new SocketListener();   
        }
        l.setHost(listenAddr);
        l.setPort(listenPort);

        //Add admin listener for dynamic reconfiguaration of the simulator
        if (adminListenAddr != null && adminListenPort != -1) {
            Listener al = null;
            al = new SocketListener();
            al.setHost(adminListenAddr);
            al.setPort(adminListenPort);
            server.addListener(al);
        }

        server.addListener(l);
        server.start();
        System.out.println("HttpTransport service ready");
        Logger.getInstance().log("HttpTransport service ready");
    }

    public boolean isUsingTLS() {
        return tls;
    }

    private void startHandlers(Properties p, String n)
            throws Exception {
        // Modify this so that it keeps references to the ToolkitHttpHandler instances,
        // if it gets a request for the same one, just make the HttpContext and add the
        // same reference to it
        HashMap<String, ToolkitHttpHandler> cache = new HashMap<>();
        handlers = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(n);
        while (st.hasMoreTokens()) {
            String pname = st.nextToken();
            StringBuilder sbpath = new StringBuilder(PROOT);
            sbpath.append(serviceName);
            sbpath.append(".");
            sbpath.append(pname);
            StringBuilder sbclass = new StringBuilder(sbpath.toString());
            StringBuilder sbttl = new StringBuilder(sbpath.toString());
            sbpath.append(NAMEPATH);
            sbclass.append(NAMECLASS);
            sbttl.append(NAMETTL);
            String path = p.getProperty(sbpath.toString());
            String classname = p.getProperty(sbclass.toString());
            String attl = p.getProperty(sbttl.toString());
            int asyncttl = 0;
            if (attl == null) {
                attl = p.getProperty(DEFAULTASYNCTTLPROPERTY);
                if (attl == null) {
                    asyncttl = DEFAULTASYNCTTL;
                } else {
                    try {
                        asyncttl = Integer.parseInt(attl);
                    } catch (Exception e) {
                        asyncttl = DEFAULTASYNCTTL;
                    }
                }
            } else {
                try {
                    asyncttl = Integer.parseInt(attl);
                } catch (Exception e) {
                    asyncttl = DEFAULTASYNCTTL;
                }
            }
            try {
                ToolkitHttpHandler h = null;
                if (cache.containsKey(classname)) {
                    h = cache.get(classname);
                } else {
                    h = (ToolkitHttpHandler) Class.forName(classname).newInstance();
                    h.setToolkit(this);
                }
                h.setAsynchronousTTL(asyncttl);
                HttpContext c = new HttpContext();
                c.addHandler(h);
                handlers.add(h);
                c.setContextPath(path);
                server.addContext(c);
            } catch (Exception e) {
                throw new Exception("ToolkitHandler boot failed: Loading handler " + classname + " : " + e.getMessage());
            }
        }

    }

    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        return new ServiceResponse(0, TOOLKIT_SIMULATOR);
    }
}
