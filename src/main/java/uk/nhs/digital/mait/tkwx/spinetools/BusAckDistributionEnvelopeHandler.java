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
package uk.nhs.digital.mait.tkwx.spinetools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.AckDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.NackDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.util.ITKException;
import uk.nhs.digital.mait.spinetools.spine.connection.ConnectionManager;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSSpineEndpointResolver;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSconnection;
import uk.nhs.digital.mait.spinetools.spine.connection.SdsTransmissionDetails;
import uk.nhs.digital.mait.spinetools.spine.messaging.DistributionEnvelopeHandler;
import uk.nhs.digital.mait.spinetools.spine.messaging.EbXmlMessage;
import uk.nhs.digital.mait.spinetools.spine.messaging.ITKDistributionEnvelopeAttachment;
import uk.nhs.digital.mait.spinetools.spine.messaging.SpineHL7Message;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * A DistributionEnvelope handler that : 1. writes the envelope and its content,
 * to a file on disk. This is the default behaviour of the MHS' ITK Trunk
 * handler 2. responds to the requestor with a infrastructure acknowledgement 3.
 * then busniness ack is also sent (+ve or negative - configuarble)
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
  */
public class BusAckDistributionEnvelopeHandler
        implements DistributionEnvelopeHandler {

    private File fileSaveDirectory = null;
    private boolean reportFilename = false;

    public BusAckDistributionEnvelopeHandler()
            throws FileNotFoundException, IllegalArgumentException {
        String fsd = System.getProperty(ORG_SAVE_DIRECTORY,System.getProperty("user.dir"));
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

    /**
     * Parses the distribution envelope payloads, and writes the envelope plus
     * contents to disk.
     *
     * @param d the distribution envelope object
     * @throws Exception
     */
    @Override
    public void handle(DistributionEnvelope d)
            throws Exception {
        // The initial parsing for the DistributionEnvlope doesn't extract
        // the payloads (because it is intended to be called by routers that
        // don't need to know about payloads). That will cause an exception
        // to be thrown when we try to write the DE to a string, so call
        // "parsePayloads()" here..
        //
        d.parsePayloads();
        String s = d.getService();
        s = s.replaceAll(":", "_");
        StringBuilder sb = new StringBuilder(s);
        sb.append("_");
        sb.append(getFileSafeMessageID(d.getTrackingId()));
        sb.append(".message");
        File outfile = new File(fileSaveDirectory, sb.toString());
        Utils.writeString2File(outfile,d.toString());

        if (reportFilename) {
            System.out.println(outfile.getCanonicalPath());
        }
        //Send Infrastructure Acknowledgement
        String infackReq = d.getHandlingSpecification(INFACKREQUESTED_HANDLINGSPEC);

        if (infackReq != null && infackReq.toLowerCase().equals("true")) {
            if (System.getProperty(ORG_NEGINFACK).trim().toLowerCase().equals("n")) {
                String errorCode = System.getProperty(ORG_INFACKNACKERRORCODE);
                String errorText = System.getProperty(ORG_INFACKNACKERRORTEXT);
                String diagText = System.getProperty(ORG_INFACKNACKDIAGTEXT);
                if (errorCode == null) {
                    errorCode = "1000";
                }
                if (errorText == null) {
                    errorText = "Example error";
                }
                if (diagText == null) {
                    diagText = "Example diagnostic text";
                }
                ITKException ie = new ITKException(errorCode, errorText, diagText);
                NackDistributionEnvelope nde = new NackDistributionEnvelope(d, ie);
                nde.makeMessage();
                send(d, nde);
            } else {
                AckDistributionEnvelope ade = new AckDistributionEnvelope(d);
                ade.makeMessage();
                send(d, ade);
            }
        }
    }

    private void send(DistributionEnvelope d, DistributionEnvelope de) throws Exception {
        //Extract ODS code from sending Entity - assume it's the first entity
        String ods = d.getSender().getParts().get(0);
        //Extract ODS code from sending Entity - assume it's the first entity after "ods:"
        int i = 0;
        String odsCode = null;

        for (String s : d.getSender().getParts()) {
            if (s.toLowerCase().equals("ods")) {
                odsCode = d.getSender().getParts().get(i + 1);
                break;
            }
            i++;
        }
        if (odsCode==null){
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
        deattachment = new ITKDistributionEnvelopeAttachment(de);

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


    private String getFileSafeMessageID(String s) {
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
}
