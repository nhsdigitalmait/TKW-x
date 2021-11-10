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

import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import java.util.Properties;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isBinarySourceFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import static uk.nhs.digital.mait.tkwx.util.Utils.wrapBinaryPayload;

/**
 * Service to implement the TKW "transmit" mode.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpTransmitter
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService, Reconfigurable {

    private static final String REPLYTO = "__REPLYTO__";
    private static final String FAULTTO = "__FAULTTO__";
    private static final String SENDTO = "__SENDTO__";
    private static final String TRACKINGID = "__INTERNAL_TRACKING_ID__";
    private static final String SYSTEMPROPSUBS = "__SYSTEM_PROPERTY_SUBSTITUTION__";

    private static final String OFFSET = "tks.transmitter.timestampoffset";

    private static final String SYSTEMPROPNAME = "tks.transmitter.systempropertyname";

    private static final String HTTP_METHOD = "tks.transmitter.http.method";

    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);
    private static final SimpleDateFormat LOGFILEDATE = new SimpleDateFormat(HL7FORMATDATEMASK);

    private File sourceDirectory = null;
    private String address = null;
    private String replyto = null;
    private String faultto = null;
    private boolean nosend = false;
    private int chunkSize = 0;
    private int ttl = -1;
    private int offsetSeconds = 0;
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private String systemvariablename = null;
    private String httpMethod = null;

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void reconfigure(Properties p)
            throws Exception {
//        ToolkitService tksnd = ServiceManager.getInstance().getService("Sender");
//        ((Reconfigurable)tksnd).reconfigure(p);
        boot(simulator, p, serviceName);
    }

    @Override
    public String reconfigure(String what, String value)
            throws Exception {
        String oldValue = null;
        if (what.contentEquals(ReconfigureTags.SOURCE_DIRECTORY)) {
            oldValue = sourceDirectory.getAbsolutePath();
            sourceDirectory = new File(value);
            return oldValue;
        } else if (what.contentEquals(ReconfigureTags.ADDRESS)) {
            oldValue = address;
            address = value;
            return oldValue;
        } else if (what.contentEquals(ReconfigureTags.TIMESTAMP_OFFSET)) {
            oldValue = Integer.toString(offsetSeconds);
            offsetSeconds = Integer.parseInt(value);
            return oldValue;
        }
        throw new Exception("Configuration not supported");
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
            throw new Exception("Transmitter: null or empty source directory "
                    + TRANSMITDIR_PROPERTY);
        }
        Utils.createFolderIfMissing(prop);
        sourceDirectory = new File(prop);
        if (!sourceDirectory.canRead()) {
            throw new Exception("Transmitter: Unable to read source directory "
                    + prop);
        }

        prop = bootProperties.getProperty(TXTTL_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("Transmitter: null or empty TTL given "
                    + TXTTL_PROPERTY);
        }
        try {
            ttl = Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            throw new Exception("Transmitter: Invalid TTL: "
                    + prop);
        }

        prop = bootProperties.getProperty(ADDRESS_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("Transmitter: null or empty address given "
                    + ADDRESS_PROPERTY);
        }
        address = prop;

        nosend = isY(bootProperties.getProperty(TXNOSEND_PROPERTY));

        prop = bootProperties.getProperty(CHUNKXMIT_PROPERTY);
        if (!Utils.isNullOrEmpty(prop)) {
            chunkSize = Integer.parseInt(prop);
        }

        prop = bootProperties.getProperty(REPLYTO_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("ReplyTo: null or empty address given "
                    + REPLYTO_PROPERTY);
        }
        replyto = prop;

        prop = bootProperties.getProperty(FAULTTO_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("FaultTo: null or empty address given "
                    + FAULTTO_PROPERTY);
        }
        faultto = prop;

        prop = bootProperties.getProperty(OFFSET);
        if (!Utils.isNullOrEmpty(prop)) {
            try {
                offsetSeconds = Integer.parseInt(prop);
            } catch (NumberFormatException e) {
                Logger.getInstance().log(WARNING,
                        HttpTransmitter.class.getName(),
                        "timestamp offset parse error, should be integer seconds, setting to 0: "
                                + prop);
            }
        }
        systemvariablename = bootProperties.getProperty(SYSTEMPROPNAME);

        prop = bootProperties.getProperty(HTTP_METHOD);
        if ((Utils.isNullOrEmpty(prop))) {
            httpMethod = "POST";
        } else {
            httpMethod = prop;
        }

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
            return transmitAllFiles();
        }
    }

    /**
     * Transmit all files in the transmitter source directory as configured in
     * the TKW properties file. Performs substitutions of timestamps, and SOAP
     * header signing, as required.
     *
     * @param param Not used.
     * @return Empty ServiceResponse.
     * @throws Exception
     */
    private ServiceResponse transmitAllFiles() throws Exception {
        int sent = 0;
        String files[] = sourceDirectory.list();
        for (String f : files) {
            if (sendMessage(sourceDirectory.getPath(), f)) {
                sent++;
            }
        }
        return new ServiceResponse(sent, null);
    }

    /**
     * Transmit the given file in the given directory.
     *
     * @param dir Directory
     * @param filename File to send
     * @return
     * @throws Exception
     */
    private boolean sendMessage(String dir, String filename)
            throws Exception {
        StringBuilder msg = null;
        if (isBinarySourceFile(filename)) {
            byte[] bytes = Files.readAllBytes(Paths.get(dir + "/" + filename));
            msg = wrapBinaryPayload(bytes);
        } else {
            msg = new StringBuilder(Utils.readFile2String(dir, filename));
        }
        Date now = new Date();
        String ts = ISO8601FORMATDATE.format(now);
        Calendar cal = Calendar.getInstance();
        Calendar offsetcal = null;
        String osts = null;
        String osexp = null;
        cal.setTime(now);
        if (offsetSeconds != 0) {
            offsetcal = Calendar.getInstance();
            offsetcal.add(Calendar.SECOND, offsetSeconds);
            osts = ISO8601FORMATDATE.format(offsetcal.getTime());
            offsetcal.add(Calendar.SECOND, ttl);
            osexp = ISO8601FORMATDATE.format(offsetcal.getTime());
        }
        cal.add(Calendar.SECOND, ttl);
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        String msgid = randomUUID().toString().toUpperCase();
        String trkid = randomUUID().toString().toUpperCase();
        String ft = LOGFILEDATE.format(now);
        boolean timeStampDone = false;
        if (offsetcal != null) {
            // Just substitute the "normal" tags, not offset-test-specific ones.
            //
//            timeStampDone = substitute(msg, OFFSETTIMESTAMP, osts);
//            substitute(msg, OFFSETEXPIRES, osexp);            
            timeStampDone = substitute(msg, TIMESTAMP_TAG, osts);
            substitute(msg, EXPIRES_TAG, osexp);
        } else {
            timeStampDone = substitute(msg, TIMESTAMP_TAG, ts);
            substitute(msg, EXPIRES_TAG, exp);
        }
        //substitute(msg, TIMESTAMP, ts);
        //substitute(msg, EXPIRES, exp);
        substitute(msg, MESSAGEID_TAG, msgid);
        substitute(msg, TRACKINGID, trkid);
        substitute(msg, REPLYTO, replyto);
        substitute(msg, FAULTTO, faultto);
        substitute(msg, SENDTO, address);
        // substitute at execute time the system property associated with tkw config if present
        if (systemvariablename != null && systemvariablename.trim().length() != 0) {
            String sysvar = System.getProperty(systemvariablename);
            if (sysvar != null && systemvariablename.trim().length() != 0) {
                substitute(msg, SYSTEMPROPSUBS, sysvar);
            }
        }

        String m = msg.toString();

        ServiceManager smr = ServiceManager.getInstance();
        if (nosend) {
            System.setProperty(NOORIGINATE_PROPERTY, "Transmitter instructed not to send");
        }
        ToolkitService tksnd = smr.getService("Sender");
        if (tksnd != null) {
            EvidenceMetaDataHandler evidenceMetaDataHandler = new EvidenceMetaDataHandler(address, "Address");
            SenderRequest sr = new SenderRequest(m, null, address, httpMethod);
            if (System.getProperty("tkw.internal.runningautotest") != null) {
                sr.setOriginalFileName(filename);
            }
            if (chunkSize != 0) {
                sr.setChunkSize(chunkSize);
            }
            sr.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
            tksnd.execute(sr);
            evidenceMetaDataHandler.mainThreadEnded();
        } else {
            return false;
        }
        return true;
    }
}
