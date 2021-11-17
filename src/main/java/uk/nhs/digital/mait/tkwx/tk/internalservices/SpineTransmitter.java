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
 /*
    Uses the SpineSender service
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import java.util.Properties;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;

/**
 *
 * @author DAMU2
 */
public class SpineTransmitter
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService {

    private final static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);
    private final static SimpleDateFormat LOGFILEDATE = new SimpleDateFormat(HL7FORMATDATEMASK);

    private File sourceDirectory = null;
    private String address = null;
    private boolean nosend = false;
    private int chunkSize = 0;
    private int ttl = -1;
    private String serviceName = null;
    private Properties bootProperties = null;

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));

        bootProperties = p;
        serviceName = s;

        String prop = null;
        prop = bootProperties.getProperty(TRANSMITDIR_PROPERTY);
        if (Utils.isNullOrEmpty(prop)) {
            throw new Exception("Transmitter: null or empty source directory "
                    + TRANSMITDIR_PROPERTY);
        }
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
        address = new String(prop);
        nosend = isY(bootProperties.getProperty(TXNOSEND_PROPERTY));
        prop = bootProperties.getProperty(CHUNKXMIT_PROPERTY);
        if (!Utils.isNullOrEmpty(prop)) {
            chunkSize = Integer.parseInt(prop);
        }
        System.out.println(serviceName
                + " started, class: "
                + this.getClass().getCanonicalName());
    }

    /**
     *
     * @param param Object
     * @return ServiceResponse
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

    private ServiceResponse sendAllFiles() throws Exception {
        int sent = 0;
        String files[] = sourceDirectory.list();
        for (String f : files) {
            if (sendMessage(sourceDirectory.getPath(), f)) {
                sent++;
            }
        }
        return new ServiceResponse(sent, null);
    }

    private boolean sendMessage(String dir, String filename)
            throws Exception {
        StringBuilder msg = new StringBuilder(Utils.readFile2String(dir, filename));
        Date now = new Date();
        String ts = ISO8601FORMATDATE.format(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, ttl);
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        String msgid = randomUUID().toString().toUpperCase();
        String ft = LOGFILEDATE.format(now);
        boolean timeStampDone = substitute(msg, TIMESTAMP_TAG, ts);
        substitute(msg, TIMESTAMP_TAG, ts);
        substitute(msg, EXPIRES_TAG, exp);
        substitute(msg, MESSAGEID_TAG, msgid);
        String m = msg.toString();

        if (nosend) {
            System.setProperty(NOORIGINATE_PROPERTY, "Transmitter instructed not to send");
        }
        ToolkitService tksnd = ServiceManager.getInstance().getService("SpineSender");
        if (tksnd != null) {
            SenderRequest sr = new SenderRequest(m.toString(), null, address);
            if (chunkSize != 0) {
                sr.setChunkSize(chunkSize);
            }
            tksnd.execute(sr);
        } else {
            return false;
        }

        return true;
    }
}
