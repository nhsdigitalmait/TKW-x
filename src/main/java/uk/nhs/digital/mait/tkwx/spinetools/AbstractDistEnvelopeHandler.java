/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.spinetools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.spinetools.spine.connection.ConnectionManager;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSSpineEndpointResolver;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSconnection;
import uk.nhs.digital.mait.spinetools.spine.connection.SdsTransmissionDetails;
import uk.nhs.digital.mait.spinetools.spine.messaging.EbXmlMessage;
import uk.nhs.digital.mait.spinetools.spine.messaging.ITKDistributionEnvelopeAttachment;
import uk.nhs.digital.mait.spinetools.spine.messaging.SpineHL7Message;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 *
 * @author simonfarrow
 */
public abstract class AbstractDistEnvelopeHandler extends Thread {

    protected File fileSaveDirectory = null;
    protected boolean reportFilename = false;



    /**
     * common constructor
     * @throws FileNotFoundException
     * @throws IllegalArgumentException 
     */
    protected AbstractDistEnvelopeHandler()
            throws FileNotFoundException, IllegalArgumentException {
        String fsd = System.getProperty(ORG_SAVE_DIRECTORY);
        if (fsd == null) {
            fsd = System.getProperty("user.dir");
        }
        fileSaveDirectory = new File(fsd);
        if (!fileSaveDirectory.exists()) {
            throw new FileNotFoundException("Default ITK distribution envelope handler save location does not exist");
        }
        if (!fileSaveDirectory.isDirectory()) {
            throw new IllegalArgumentException("Default ITK distribution envelope handler save location must be a directory");
        }
        if (!fileSaveDirectory.canWrite()) {
            throw new IllegalArgumentException("Default ITK distribution envelope handler save location must be writable");
        }
        reportFilename = isY(System.getProperty(ORG_REPORT_FILENAME));
    }

    protected void send(DistributionEnvelope incoming, DistributionEnvelope outgoingDE) throws Exception {
        //Extract ODS code from sending Entity - assume it's the first entity after "ods:"
        int i = 0;
        String odsCode = null;

        for (String s : incoming.getSender().getParts()) {
            if (s.toLowerCase().equals("ods")) {
                odsCode = incoming.getSender().getParts().get(i + 1);
                break;
            }
            i++;
        }
        if (odsCode == null) {
            throw new Exception("ODS code cannot be derived from the sender address");
        }
        // call SDS to find where/how to send response bus/inf acks
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.loadPersistedMessages();
        SDSconnection sds = cm.getSdsConnection();
        SDSSpineEndpointResolver resolver = new SDSSpineEndpointResolver(sds);
        ArrayList<SdsTransmissionDetails> details = null;
        // The assumption is that there is only instance of an end point in the target ODS.
        // Therefore The SDS resolver will take the only response or first entry in cache
        details = resolver.getTransmissionDetails(ITK_TRUNK_INTERACTION, odsCode, null, null);
        SdsTransmissionDetails std = details.get(0);

        SpineHL7Message msg = new SpineHL7Message(std.getInteractionId(), "");

        ITKDistributionEnvelopeAttachment deattachment = null;
        deattachment = new ITKDistributionEnvelopeAttachment(outgoingDE);

        msg.setMyAsid(std.getAsids().get(0));
        msg.setToAsid(cm.getMyAsid());

// Set author details in msg
        msg.setAuthorRole(System.getProperty(SPINE_MESSAGING_AUTHOR_ROLE));
        msg.setAuthorUid(System.getProperty(SPINE_MESSAGING_AUTHOR_UID));
        msg.setAuthorUrp(System.getProperty(SPINE_MESSAGING_AUTHOR_URP));
        EbXmlMessage e = new EbXmlMessage(std, msg);
        e.addAttachment(deattachment);
        cm.send(e, std);
    }

    protected void substitute(StringBuilder sb, String tag, String value)
            throws Exception {
        int tagPoint = -1;
        int tagLength = tag.length();
        while ((tagPoint = sb.indexOf(tag)) != -1) {
            sb.replace(tagPoint, tagPoint + tagLength, value);
        }
    }

    protected String getFileSafeMessageID(String s) {
        if (s == null) {
            return s;
        }
        if (!s.contains(":")) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        int c = -1;
        while ((c = sb.indexOf(":")) != -1) {
            sb.setCharAt(c, '_');
        }
        return sb.toString();
    }

    protected StringBuilder readTemplate(String tname)
            throws Exception {
        StringBuilder sb;
        try (InputStream is = getClass().getResourceAsStream(tname)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
        }
        return sb;
    }
}
