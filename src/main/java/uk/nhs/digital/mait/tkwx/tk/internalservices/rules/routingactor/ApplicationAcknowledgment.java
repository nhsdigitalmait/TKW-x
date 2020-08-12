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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Address;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Identity;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Payload;

import uk.nhs.digital.mait.distributionenvelopetools.itk.util.ITKException;
import static uk.nhs.digital.mait.tkwx.spinetools.ApplicationAcknowledgment.HL7TIMESTAMP;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;
/**
 *
 * @author DAMU2
 */
public class ApplicationAcknowledgment 
    extends DistributionEnvelope
{    
    private static final String BIZACKTEMPLATE = "business-ack-payload-template.txt";
    // private static final String RESPDETEMPLATE = "itk_business_ack_distribution_envelope_template.txt";
        
    private static final String SERVICE = BIZACKSERVICE; 
    
    public static final String RESPONSETYPE = "tks.routingactor.application.responsetype";

    private static final String NACKERRORCODE = "tks.routingactor.application.nack.errorcode";    
    private static final String NACKERRORTEXT = "tks.routingactor.application.nack.errortext";
    private static final String NACKDIAGTEXT = "tks.routingactor.application.nack.diagnostictext";
    
    private static String ackTemplate = null;
    private static String nackTemplate = null;
    //private static String responseEnvelope = null;
    
    protected static final SimpleDateFormat ISOTIMESTAMP = new SimpleDateFormat(ISOFORMATDATEMASK_NOZ);
    protected String serviceRef = null;
    
    protected DistributionEnvelope original = null;
    
    public ApplicationAcknowledgment(DistributionEnvelope d)
            throws Exception
    {         
        synchronized (BIZACKTEMPLATE) {
            if (ackTemplate == null) {
                ackTemplate = readFile2String(getClass().getResourceAsStream(BIZACKTEMPLATE));
                nackTemplate = readFile2String(getClass().getResourceAsStream(BIZNACKTEMPLATE));
      //          responseEnvelope = loadTemplate(getClass().getResourceAsStream(RESPDETEMPLATE));
            }
        }
        original = d;
        setTrackingId(UUID.randomUUID().toString().toUpperCase());
        Address[] a = new Address[1];
        a[0] = d.getSender();
        setTo(a);
        String id = null;
        String snd = null;
        try {
            id = Configurator.getConfigurator().getConfiguration(ORG_AUDIT_ID_PROPERTY);
            snd = Configurator.getConfigurator().getConfiguration(ORG_SENDER_PROPERTY);
        }
        catch (Exception e) {
            throw new ITKException("SYST-0000", "Configuration manager exception", e.getMessage());
        }
        Address sndr = new Address(snd);
        Identity[] auditId = new Identity[1];
        auditId[0] = new Identity(id);
        setAudit(auditId);
        setSender(sndr);
        setService(SERVICE);
        // Oops - actually set above.
        //
        // setTrackingId(d.getTrackingId());
        serviceRef = d.getService();
        addHandlingSpecification(INTERACTIONID, ITK_BUSINESS_ACK_INTERACTION);
        makeResponse();
        
    }
    
    protected final void makeResponse() 
            throws Exception
    {        
       String rtype = Configurator.getConfigurator().getConfiguration(RESPONSETYPE);
       StringBuilder ack = null;
       boolean isack = ((rtype != null) && rtype.toLowerCase().contentEquals("ack"));
       if (isack) {
           ack = new StringBuilder(ackTemplate);
           doAckSubstitutions(ack);
       } else {
           ack = new StringBuilder(nackTemplate);
           doAckSubstitutions(ack);
           doNackSubstitutions(ack);
       }
       
       Payload p = new Payload(XML_MIMETYPE);
       p.setBody(ack.toString(), false);
       p.setProfileId(PROFILEID);
       super.addPayload(p);
    }
    
    private void doAckSubstitutions(StringBuilder sb) 
            throws Exception
    {
        // Substitutions common to acks and nacks
        substitute(sb, "__MESSAGE_ID__", UUID.randomUUID().toString().toUpperCase());
        substitute(sb, "__HL7_CREATION_DATE__", HL7TIMESTAMP.format(new Date()));
        substitute(sb, "__INTERACTION_ID__", original.getInteractionId());
        substitute(sb, "__INBOUND_TRANSMISSIONID__", original.getTrackingId());
        substitute(sb, "__RESPONDER_RECEIVER_ADDRESS_OID__", original.getSender().getOID());
        substitute(sb, "__RESPONDER_RECEIVER_ADDRESS__", original.getSender().getUri());        
        substitute(sb, "__RESPONDER_SENDER_ADDRESS_OID__", sender.getOID());
        substitute(sb, "__RESPONDER_SENDER_ADDRESS__", sender.getUri());
        substitute(sb, "__INBOUND_PAYLOAD_ID__", original.getPayloadId(0));
    }
    
    private void doNackSubstitutions(StringBuilder sb) 
            throws Exception
    {
        // Substitutions for nacks only
        // __ERROR_CODE__ __ERROR_TEXT__ and __DIAGNOSTIC_TEXT__ need to be picked up from
        // somewhere... Properties for now... Needs review.
        
        String errorCode = Configurator.getConfigurator().getConfiguration(NACKERRORCODE);
        String errorText = Configurator.getConfigurator().getConfiguration(NACKERRORTEXT);
        String diagText = Configurator.getConfigurator().getConfiguration(NACKDIAGTEXT);
        if (errorCode == null) { errorCode = "1000"; }
        if (errorText == null) { errorText = "Example error"; }
        if (diagText == null) { diagText = "Example diagnostic text"; }
        substitute(sb, "__ERROR_CODE__", errorCode);
        substitute(sb, "__ERROR_TEXT__", errorText);
        substitute(sb, "__DIAGNOSTIC_TEXT__", diagText);
    }
}
