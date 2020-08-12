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
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SpineSender;
import java.io.File;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SPSetter;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Service which acts as a launcher for
 * uk.nhs.digital.mait.tkwx.tk.internalservices.send.Sender threads to deliver messages
 * either to queues, or via SOAP web service calls. Used for asynchronous
 * responses, and for the TKW transmitter function.
 *
 * @author DAMU2
 *
 */
public class SpineSenderService
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService, Reconfigurable {

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private boolean useTls = false;
    private String destinationDirectory = null;

    public SpineSenderService() {
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void reconfigure(Properties p)
            throws Exception {
        boot(simulator, p, serviceName);
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
            throw new Exception("SpineSender: null or empty destination directory " + TRANSMITLOG_PROPERTY);
        }
        File f = new File(destinationDirectory);
        if (!f.canWrite()) {
            throw new Exception("SpineSender: Unable to write to destination directory " + destinationDirectory);
        }
        if (useTls) {
            SPSetter.executeSettings(p, new SPSetter[]{
                new SPSetter(TRUSTSTORE_PROPERTY, "javax.net.ssl.trustStore", s1 -> {
                    Logger.getInstance().log(WARNING, SpineSenderService.class.getName(), "Property " + TRUSTSTORE_PROPERTY + " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail");
                }, null),
                new SPSetter(TRUSTPASS_PROPERTY, "javax.net.ssl.trustStorePassword", s1 -> {
                    Logger.getInstance().log(WARNING, SpineSenderService.class.getName(), "Property " + TRUSTPASS_PROPERTY + " not set explicitly, if not set in JVM cacerts, TLS initialisation may fail");
                }, null),
                new SPSetter(KEYSTORE_PROPERTY, "javax.net.ssl.keyStore", s1 -> {
                    Logger.getInstance().log(WARNING, SpineSenderService.class.getName(), "Property " + KEYSTORE_PROPERTY + " not set, TLS initialisation may fail");
                }, null),
                new SPSetter(KEYPASS_PROPERTY, "javax.net.ssl.keyStorePassword", s1 -> {
                    Logger.getInstance().log(WARNING, SpineSenderService.class.getName(), "Property " + KEYPASS_PROPERTY + " not set, TLS initialisation may fail");
                }, null),});
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
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        if (param != null && param instanceof Object[]) {
            return new ServiceResponse(200, "");
        } else if (param != null && param instanceof SenderRequest) {
            SenderRequest sr = (SenderRequest) param;
            new SpineSender(simulator, sr, useTls, destinationDirectory);
            return new ServiceResponse(0, "Toolkit simulator sender service");
        } else {
            throw new IllegalArgumentException("Signature is not Object[] or SenderRequest");
        }
    }
}
