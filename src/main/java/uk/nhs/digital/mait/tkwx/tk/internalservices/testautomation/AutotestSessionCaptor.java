/*

 Copyright 2014 Health and Social Care Information Centre
 Solution Assurance damian.murphy@hscic.gov.uk

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.spinetools.spine.connection.SessionCaptor;
import uk.nhs.digital.mait.spinetools.spine.messaging.Sendable;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.FileLocker;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Autotest implementation of a SpineTools SessionCaptor that saves the request
 * dialogue to a log file based on two parameters passed as system properties
 * (folder and filename prefix)
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
public class AutotestSessionCaptor
        implements SessionCaptor {

    public static AutotestSessionCaptor getInstance() {
        return me;
    }

    private String messagesFolder;
    private String messagesFilename;
    private static AutotestSessionCaptor me = null;
    private static final SimpleDateFormat FILEDATE = new SimpleDateFormat(FILEDATEMASK);

    /**
     * public constructor
     */
    public AutotestSessionCaptor() {
        init();
    }

    /**
     * avoid the leaking this warning
     */
    private void init() {
        me = this;
    }

    /**
     * log the request and synchronous response to the required log file
     *
     * @param s the sendable object
     */
    @Override
    public void capture(Sendable s) {

        if (messagesFolder != null && messagesFilename != null) {
            // extract the de
            SpineMessage sm = null;
            try {
                sm = new SpineMessage(new String(s.getOnTheWireRequest()));
                // extract the de
                StringBuilder sb = new StringBuilder(sm.getATTACHMENTPart(0));

                // add the required log file marker
                sb.append("\r\n" + LogMarkers.END_REQUEST_MARKER + "\r\n");

                // add the synchronous http response
                sb.append(new String(s.getOnTheWireResponse()));

                String filename = messagesFolder + "/" + messagesFilename + "_" + FILEDATE.format(new Date()) + ".log";
                // write the concatenated log file
                File f = new File(filename);
                try (FileLocker fl = new FileLocker(f.getAbsolutePath())) {
                    Utils.writeString2File(filename, sb.toString());
                }
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE,AutotestSessionCaptor.class.getName(),"capture failed to write file "+ ex.getMessage());
            }
        }
    }

    /**
     * @param messagesFolder the messagesFolder to set
     */
    public void setMessagesFolder(String messagesFolder) {
        this.messagesFolder = messagesFolder;
    }

    /**
     * @param messagesFilename the messagesFilename to set
     */
    public void setMessagesFilename(String messagesFilename) {
        this.messagesFilename = messagesFilename;
    }
}
