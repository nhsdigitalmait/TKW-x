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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.util.Properties;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import java.io.File;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.TransmitterMode;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SPSetter;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.Sender;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Schedule.SPINETOOLS_TRANSMITTER_MODE;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Service which acts as a launcher for
 * uk.nhs.digital.mait.tkwx.tk.internalservices.send.Sender threads to deliver messages
 * either to queues, or via SOAP web service calls. Used for asynchronous
 * responses, and for the TKW transmitter function.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SenderService
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService, Reconfigurable {

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private boolean useTls = false;
    private String destinationDirectory = null;
    private String transmitterMode = null;
    private Properties reconfiguredProperties = null;

    public SenderService() {
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void reconfigure(Properties p)
            throws Exception {
        boot(simulator, p, serviceName);
        reconfiguredProperties = p;
    }

    @Override
    public String reconfigure(String what, String value)
            throws Exception {
        String oldValue = null;
        if (what.contentEquals(ReconfigureTags.DESTINATION_DIRECTORY)) {
            oldValue = destinationDirectory;
            destinationDirectory = value;
            return oldValue;
        } else {
            throw new Exception("Configuration not supported");
        }
    }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        bootProperties = p;
        serviceName = s;
        simulator = t;
        transmitterMode = p.getProperty(TRANSMITTERMODE_PROPERTY);
        if (transmitterMode == null || transmitterMode.trim().length() == 0) {
            throw new Exception("Sender: null or empty tranmitter mode " + TRANSMITTERMODE_PROPERTY);
        }
        String tls = p.getProperty(SEND_USETLS_PROPERTY);
        if (isY(tls)) {
            useTls = true;
            String ma = p.getProperty(CLIENT_TLSMA_PROPERTY);
            if (ma != null) {
                System.setProperty(SENDTLSMA_PROPERTY, ma);
            }
        }
        destinationDirectory = bootProperties.getProperty(TRANSMITLOG_PROPERTY);
        if ((destinationDirectory == null) || (destinationDirectory.trim().length() == 0)) {
            throw new Exception("Sender: null or empty destination directory " + TRANSMITLOG_PROPERTY);
        }
        File f = new File(destinationDirectory);
        if (!f.canWrite()) {
            throw new Exception("Sender: Unable to write to destination directory " + destinationDirectory);
        }
        if (useTls) {
            if (transmitterMode.equals(SPINETOOLS_TRANSMITTER_MODE)) {
                SPSetter.executeSettings(p, new String[][]{
                    {TRUSTSTORE_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.trust", "SpineTools requires property " + TRUSTSTORE_PROPERTY + " which is not set"},
                    {TRUSTPASS_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.trustpass", "SpineTools requires property " + TRUSTPASS_PROPERTY + " which is not set"},
                    {KEYSTORE_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.certs", "SpineTools requires property " + KEYSTORE_PROPERTY + " which is not set"},
                    {KEYPASS_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.sslcontextpass", "SpineTools requires property " + KEYPASS_PROPERTY + " which is not set"}});
            } else {
                SPSetter.executeSettings(p, new SPSetter[]{
                    new SPSetter(TRUSTSTORE_PROPERTY, "javax.net.ssl.trustStore", s1 -> {
                        Logger.getInstance().log(WARNING, SenderService.class.getName(), "Property " + TRUSTSTORE_PROPERTY + " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail");
                    }, null),
                    new SPSetter(TRUSTPASS_PROPERTY, "javax.net.ssl.trustStorePassword", s1 -> {
                        Logger.getInstance().log(WARNING, SenderService.class.getName(), "Property " + TRUSTPASS_PROPERTY + " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail");
                    }, null),
                    new SPSetter(KEYSTORE_PROPERTY, "javax.net.ssl.keyStore", s1 -> {
                        Logger.getInstance().log(WARNING, SenderService.class.getName(), "Property " + KEYSTORE_PROPERTY + " not set, TLS initialisation may fail");
                    }, null),
                    new SPSetter(KEYPASS_PROPERTY, "javax.net.ssl.keyStorePassword", s1 -> {
                        Logger.getInstance().log(WARNING, SenderService.class.getName(), "Property " + KEYPASS_PROPERTY + " not set, TLS initialisation may fail");
                    }, null),});
            }
        }
    }
    
    /**
     * @param param Object
     * @return ServiceResponse
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        if (param != null && param instanceof Object[]) {
            return new ServiceResponse(200, "");
        } else if (param != null && param instanceof SenderRequest) {
            return sendRequest((SenderRequest)param);
        } else {
            throw new IllegalArgumentException("Signature is not Object[] or SenderRequest");
        }
    }

    /**
     * Send a message.
     *
     * @param param uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest instance
     * containing message to be sent
     * @return ServiceResponse with zero response code on success.
     * @throws Exception
     */
    private ServiceResponse sendRequest(SenderRequest sr) throws ClassNotFoundException, Exception, IllegalAccessException, InstantiationException {
        Sender s = ((Sender) Class.forName("uk.nhs.digital.mait.tkwx.tk.internalservices.send." + transmitterMode + "Sender").newInstance());
        if (reconfiguredProperties != null && s instanceof Reconfigurable) {
            ((Reconfigurable) s).reconfigure(reconfiguredProperties);
        }
        s.init(simulator, sr, useTls, destinationDirectory);
        return new ServiceResponse(0, "Toolkit simulator sender service");
    }
}
