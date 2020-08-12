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
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.AckDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.NackDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.util.ITKException;
import uk.nhs.digital.mait.spinetools.spine.messaging.DistributionEnvelopeHandler;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * A DistributionEnvelope handler that : 1. writes the envelope and its
 * content,to a file on disk. This is the default behaviour of the MHS' ITK
 * Trunk handler 2. responds to the requestor with a infrastructure
 * acknowledgement (+ve or negative - configurable) 3.then business ack is also
 * sent (+ve or negative - configurable)
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class SendDistEnvelopeHandler extends AbstractDistEnvelopeHandler
        implements DistributionEnvelopeHandler {



    private static final String ITK_INFRASTRUCTURE_ACK_INTERACTION = "urn:nhs-itk:interaction:ITKInfrastructureAcknowledgement-v1-0";
    private static final String INTERACTION = "urn:nhs-itk:ns:201005:interaction";

    /**
     * Constructor
     * @throws FileNotFoundException
     * @throws IllegalArgumentException 
     */
    public SendDistEnvelopeHandler()
            throws FileNotFoundException, IllegalArgumentException {
        super();
    }

    /**
     * Parses the distribution envelope payloads, and writes the envelope plus
     * contents to disk. It will then respond to the requestor with the
     * appropriate ack(s) dynamically depending on the content of the requesting
     * Handling Spec
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

        String interaction = d.getHandlingSpecification(INTERACTION);

        if (interaction == null || interaction.equals("")) {
            //Assume it's a infack and do nothing   
        } else if (interaction.equals(ITK_BUSINESS_ACK_INTERACTION)) {
            //Send Infrastructure Acknowledgement
            String infackReq = d.getHandlingSpecification(INFACKREQUESTED_HANDLINGSPEC);

            if (infackReq != null && infackReq.toLowerCase().equals("true")) {
                sendInfAck(d);
            }
        } else if (interaction.equals(ITK_INFRASTRUCTURE_ACK_INTERACTION)) {
            //it's a infack and do nothing   
        } else {
            //assume anything not busack or null or infack is a requesting message
            //Send Infrastructure Acknowledgement
            String infackReq = d.getHandlingSpecification(INFACKREQUESTED_HANDLINGSPEC);

            if (infackReq != null && infackReq.toLowerCase().equals("true")) {
                sendInfAck(d);
                //if a negative ebxml is configured do no further processing
                if (isY(System.getProperty(ORG_NEGEBXMLACK))) {
                    return;
                }
                SendDistEnvelopeHandler.sleep(3000);
            }

            //Send Business Acknowledgement
            String busackReq = d.getHandlingSpecification(ACKREQUESTED_HANDLINGSPEC);

            if (busackReq != null && busackReq.toLowerCase().equals("true")) {
                ApplicationAcknowledgment aa = new ApplicationAcknowledgment(d);
                aa.addHandlingSpecification(INFACKREQUESTED_HANDLINGSPEC, "true");
                aa.setService(SEND_DIST_ENVELOPE_SERVICE);
                send(d, aa);
            }
        }

    }

    private void sendInfAck(DistributionEnvelope d) throws Exception {
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
            nde.setService(SEND_DIST_ENVELOPE_SERVICE);
            /**
             * This is to support ITKv3 behaviour whereby the inf ack contains a
             * handling spec which is populated with an interaction id - this is
             * a consequence of the discredited decision to have generic
             * services.
             */
            nde.setAckTemplate(NACK_TEMPLATE_PATH);
            nde.makeMessage();
            send(d, nde);
        } else {
            AckDistributionEnvelope ade = new AckDistributionEnvelope(d);
            ade.setService(SEND_DIST_ENVELOPE_SERVICE);
            /**
             * This is to support ITKv3 behaviour whereby the inf ack contains a
             * handling spec which is populated with an interaction id - this is
             * a consequence of the discredited decision to have generic
             * services.
             */
            ade.setAckTemplate(ACK_TEMPLATE_PATH);
            ade.makeMessage();
            send(d, ade);
        }
    }

}
