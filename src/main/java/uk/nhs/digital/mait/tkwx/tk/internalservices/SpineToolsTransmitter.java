/*
 Copyright 2014 Richard Robinson rrobinson@hscic.gov.uk

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
 /*
    Uses the Sender service
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import java.util.Properties;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import static uk.nhs.digital.mait.tkwx.tk.boot.SpineToolsTransport.TOPARTYKEY;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Service to implement the SpineTools TKW "transmit" mode.
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class SpineToolsTransmitter
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService, Reconfigurable {

    private static final String SVCIA = "tks.spinetools.transmit.svcia";
    private static final String ODS = "tks.spinetools.transmit.ods";
    private static final String ASID = "tks.spinetools.transmit.asid";

    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    private File sourceDirectory = null;
    private String action = null;
    private String ods = null;
    private String asid = null;
    private String partyKey = null;

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;

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
        switch (what) {
            case ReconfigureTags.SOURCE_DIRECTORY:
                oldValue = sourceDirectory.getAbsolutePath();
                sourceDirectory = new File(value);
                return oldValue;
            case ReconfigureTags.ADDRESS:
                // parse the "to" address and set properties for SpineTools transport
                if (value.matches("^spine://[^/]+/[^/]+$")) {
                    oldValue = "spine://" + partyKey + (asid != null ? ("/" + asid) : "");
                    partyKey = value.replaceFirst("^spine://([^/]+).*$", "$1");
                    asid = value.replaceFirst("^spine://.[^/]+/([^/]+).*$", "$1");
                    return oldValue;
                } else {
                    throw new Exception("address property value is malformed:" + value);
                }
        }
        throw new Exception("Cannot reconfigure " + what);
    }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
        String prop = null;
        prop = bootProperties.getProperty(TRANSMITDIR_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("SpineTools Transmitter: null or empty source directory "
                    + TRANSMITDIR_PROPERTY);
        }
        Utils.createFolderIfMissing(prop);
        sourceDirectory = new File(prop);
        if (!sourceDirectory.canRead()) {
            throw new Exception("SpineTools Transmitter: Unable to read source directory "
                    + prop);
        }
        prop = bootProperties.getProperty(SVCIA);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("SpineTools Transmitter: SvcIA - service-qualified interaction id cannot be null: "
                    + SVCIA);
        }
        action = prop;
        prop = bootProperties.getProperty(ODS);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("SpineTools Transmitter: ODS code, cannot be null: "
                    + ODS);
        }
        ods = prop;
        prop = bootProperties.getProperty(ASID);
        if (Utils.isNullOrEmpty(prop) || prop.equals("null")) {
            prop = null;
        }
        asid = prop;
        prop = bootProperties.getProperty(TOPARTYKEY);
        if (Utils.isNullOrEmpty(prop) || prop.equals("null")) {
            prop = null;
        }
        partyKey = prop;

        System.out.println(serviceName
                + " started, class: "
                + this.getClass().getCanonicalName());
    }

    /**
     *
     * @param param Not used.
     * @return Empty ServiceResponse.
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        if (param != null && param instanceof Object[]) {
            return new ServiceResponse(0, null);
        } else {
            return sendAllFiles();
        }
    }

    /**
     * Transmit one file in the transmitter source directory as configured in
     * the TKW properties file.
     *
     * @return
     * @throws Exception
     */
    private ServiceResponse sendAllFiles() throws Exception {
        int sent = 0;
        File files[] = sourceDirectory.listFiles();
        for (File f : files) {
            if ((f.isDirectory() == false) && sendMessage(sourceDirectory.getPath(), f.getName())) {
                sent++;
            }
        }
        return new ServiceResponse(sent, null);
    }

    private boolean sendMessage(String dir, String filename)
            throws Exception {

        ServiceManager smr = ServiceManager.getInstance();
        ToolkitService tksnd = smr.getService("Sender");

        String msg = Utils.readFile2String(dir, filename);

        if (tksnd != null) {
            EvidenceMetaDataHandler evidenceMetaDataHandler = new EvidenceMetaDataHandler(asid, "ASID");
            SenderRequest sr = new SenderRequest(action, msg);
            sr.setODSCode(ods);
            sr.setAsid(asid);
            sr.setPartyKey(partyKey);
            if (System.getProperty("tkw.internal.runningautotest") != null) {
                sr.setOriginalFileName(filename);
            }
            sr.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
            tksnd.execute(sr);
            evidenceMetaDataHandler.mainThreadEnded();
            return false;
        } else {
            return true;
        }
    }
}
