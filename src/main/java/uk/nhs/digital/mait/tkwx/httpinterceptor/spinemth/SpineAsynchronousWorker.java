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
package uk.nhs.digital.mait.tkwx.httpinterceptor.spinemth;

import java.io.IOException;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.commonutils.util.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpLogFileGenerator;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.WrapperHelper;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.tkwx.util.Utils.substituteHandleNull;

/**
 * Handler implementation to receive an asynchronous ITK SOAP message, check it,
 * to call either the rules engine or a process as directed by the simulator
 * configuration file, and to return a response asynchronously.
 *
 * @author Damian Murphy murff@warlock.org
 */
class SpineAsynchronousWorker extends SpineSynchronousWorker {

    protected SpineAsynchronousSoapRequestHandler asyncHandler = null;

    private static final String FROMPARTYID = "__ORIGINATING_FROM_PARTY_ID__";
    private static final String TOPARTYID = "__ORIGINATING_TO_PARTY_ID__";
    private static final String CONVERSATIONID = "__CONVERSATION_ID__";
    private static final String CPAID = "__MY_ACK_CPAID_LITERAL__";

    private static final String POSTHL7 = "\r\n----=_MIME-Boundary--\r\n\r\n";
    private static final String PREHL71 = "----=_MIME-Boundary\r\nContent-Id: <ebXMLHeader@spine.nhs.uk>\r\nContent-Type: text/xml; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\n\r\n";
    private static final String PREHL72 = "\r\n----=_MIME-Boundary\r\nContent-Id: <";
    private static final String PREHL73 = ">\r\nContent-Type: application/xml; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\n\r\n";

    protected static final String AMPPATTERN = "\\&";
    protected Pattern ampPattern = null;

    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);
    private static final String EBXMLERRORACTION = "urn:urn:oasis:names:tc:ebxml-msg:service/MessageError";
    private static final String EBXMLACKACTION = "urn:urn:oasis:names:tc:ebxml-msg:service/Acknowledgment";
    private long asyncResponseDelay = 0;

    protected String toPartyID = null;
    protected String fromPartyID = null;
    protected String conversationID = null;
    protected String CPAId = null;
    private WrapperHelper wh = null;
    private boolean unreliable = false;
    private boolean fwdReliable = false;
    private String asyncResponsePayload = null;

    SpineAsynchronousWorker(SpineAsynchronousSoapRequestHandler asrh) {
        super(asrh);
        ampPattern = Pattern.compile(AMPPATTERN);
        asyncHandler = asrh;
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
        String asrd = System.getProperty(ASYNCRESPONSEDELAY_PROPERTY);
        if (asrd != null) {
            try {
                asyncResponseDelay = Long.parseLong(asrd) * 1000;
            } catch (NumberFormatException e) {
                Logger.getInstance().log(WARNING, SpineAsynchronousWorker.class.getName(), "Error setting asynchronous response delay: " + asrd);
            }
        }
        wh = WrapperHelper.getInstance();

    }

    @Override
    public ServiceResponse handle(HttpRequest req)
            throws HttpException, InterruptedException, IOException {
        try {
            if (!doChecks(req)) {
                return serviceResponse;
            }

            if (synchronousResponseDelay != 0) {
                try {
                    Thread.sleep(synchronousResponseDelay);
                } catch (InterruptedException e) {
                    Logger.getInstance().log(WARNING, SpineAsynchronousWorker.class.getName(), "SynchronousResponseDelay sleep() interrupted.");
                }
            }

            ToolkitService svc = ServiceManager.getInstance().getService("RulesEngine");
            if (svc != null) {
                serviceResponse = svc.execute(new String[]{soapaction, sm.getHL7Part()});
                if (serviceResponse != null) {
                    if (serviceResponse.getAction() != null) {
                        if (serviceResponse.getAction().equals("ebXMLerror")) {
                            synchronousEbXMLError(serviceResponse);
                            return serviceResponse;
                        }
                    }
                    if (serviceResponse.getCode() < 300) {
                        // response code 0 means hand off to Processor Service
                        if (serviceResponse.getCode() == 0) {
                            throw new Exception("No rules r process defined for action: " + soapaction);
                        }
                        // Put the Async payload into asyncResponse for use later because the synchronousAck will overwrite it
                        asyncResponsePayload = serviceResponse.getResponse();

                        synchronousAck();
                        if (asyncResponseDelay != 0) {
                            try {
                                Thread.sleep(asyncResponseDelay);
                            } catch (InterruptedException e) {
                                Logger.getInstance().log(WARNING, SpineAsynchronousWorker.class.getName(), "AsynchronousResponseDelay sleep() interrupted.");
                            }
                        }
                        // Check to see if a NONE response has been asked for 
                        if (!asyncResponsePayload.equals("")) {
                            asyncHandler.hasAsyncResponse = true;
                            subDir = HttpLogFileGenerator.generateSubFolderName(req, soapaction);
                        }
                        return serviceResponse;
                    } else {
                        synchronousResponse(serviceResponse.getCode(), "Internal server error", serviceResponse.getResponse(), serviceResponse.getAction());
                        return serviceResponse;
                    }
                } else {
//Assumes that an ebXML no response is required
                    synchronousResponse(202, "OK", "", null);
                    return serviceResponse;
//                      req.setHandled(true);
//                    resp.forceClose();
                }
            } else {
                throw new Exception("No rules r process defined for action: " + soapaction);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                synchronousResponse(500, "Internal server error", "Error reading message: " + e.getMessage(), null);
            } catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());

        }
    }

    @Override
    protected boolean doChecks(HttpRequest req)
            throws HttpException {
        try {
            readMessage(req);
            // By now we've read the message - first thing to do is to see if
            // we're going to try to verify the signature, and do so.
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String m = this.makeSoapFault("soap:Client", "The service/interaction is not supported for the requested URI", "urn:nhs:names:errors:tms", "101", "Error", "HTTP Header - SOAPAction", "Error reading message: " + e.getMessage());
                synchronousResponse(500, "Internal server error", m, null);
            } catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());
        }
        return true;
    }

    @Override
    protected void readMessage(HttpRequest req)
            throws Exception {
        soapaction = req.getField(SOAP_ACTION_HEADER);
        if (soapaction == null) {
            throw new Exception("No SOAPaction HTTP header found in request");
        }
        StringBuilder qcheck = new StringBuilder(soapaction);
        if (qcheck.length() > 0) {
            if (qcheck.charAt(0) == '"') {
                qcheck = qcheck.deleteCharAt(0);
                if (qcheck.charAt(qcheck.length() - 1) == '"') {
                    qcheck = qcheck.deleteCharAt(qcheck.length() - 1);
                }
                soapaction = qcheck.toString();
            }
        } else {
            throw new Exception("No SOAPaction");
        }
        sm = new SpineMessage(req);

        resolveSndAsid();

        // initialise the evidence Metatdata handler
//        evidenceMetaDataHandler = new EvidenceMetaDataHandler(sndAsid, "ASID");
        resolveMessageId();

        // Reliability check
        String interactionId = soapaction.substring(soapaction.lastIndexOf("/") + 1, soapaction.length());
        String mapping = asyncHandler.reliabilityMap.get(interactionId);
        if (mapping != null) {
            switch (mapping.trim()) {
                case "UNRELIABLE":
                    unreliable = true;
                    break;
                case "FORWARD-EXPRESS":
                    fwdReliable = true;
                    break;
            }
        }


        resolveToAddress();
        resolveFromAddress();
        resolveToPartyID();
        resolveFromPartyID();
        resolveConversationID();
        resolveCPAID();
        resolveRcvAsid();

    }

    protected void synchronousAck()
            throws Exception {
        if (asyncHandler.getAckLoadException() != null) {
            throw asyncHandler.getAckLoadException();
        }
        if (asyncHandler.getSyncAckTemplate() == null) {
            synchronousResponse(202, "Accepted", "", null);
            return;
        }
        if (unreliable || fwdReliable) {
            synchronousResponse(202, "Accepted", "", null);
            return;
        }
        String ackTime = ISO8601FORMATDATE.format(new Date());
        String ackID = randomUUID().toString().toUpperCase();

        StringBuilder ack = new StringBuilder(asyncHandler.getSyncAckTemplate());

        substituteHandleNull(ack, MESSAGEID_TAG, ackID);
        substituteHandleNull(ack, FROMPARTYID, fromPartyID);
        substituteHandleNull(ack, TOPARTYID, toPartyID);
        substituteHandleNull(ack, CONVERSATIONID, conversationID);
        substituteHandleNull(ack, TIMESTAMP_TAG, ackTime);
        substituteHandleNull(ack, ORIGINALMESSAGEID_TAG, messageId);
        substituteHandleNull(ack, CPAID, CPAId);
//        synchronousResponse(202, "Accepted", ack.toString(), r.getAction()); // this returned the response soapaction c.f. the acknowledgement soapaction
        synchronousResponse(202, "Accepted", ack.toString(), EBXMLACKACTION);

        }

    protected void synchronousEbXMLError(ServiceResponse r)
            throws Exception {
        String ackTime = ISO8601FORMATDATE.format(new Date());
        String ackID = randomUUID().toString().toUpperCase();

        StringBuilder ack = new StringBuilder(r.getResponse());

        substituteHandleNull(ack, MESSAGEID_TAG, ackID);
        substituteHandleNull(ack, FROMPARTYID, fromPartyID);
        substituteHandleNull(ack, TOPARTYID, toPartyID);
        substituteHandleNull(ack, CONVERSATIONID, conversationID);
        substituteHandleNull(ack, TIMESTAMP_TAG, ackTime);
        substituteHandleNull(ack, ORIGINALMESSAGEID_TAG, messageId);
        substituteHandleNull(ack, CPAID, CPAId);
        synchronousResponse(-1, "OK", ack.toString(), EBXMLERRORACTION);
    }

    protected String makeAsyncMessage(ServiceResponse ruleresponse)
            throws Exception {

        String hl7AttachmentId = randomUUID().toString().toUpperCase() + "@spine.nhs.uk";

        HashMap<String, String> to = null;
        HashMap<String, String> from = null;
        String fromInteractionId = sm.getService();
        String toService = ruleresponse.getAction().substring(0, ruleresponse.getAction().lastIndexOf("/"));
        String toInteractionId = ruleresponse.getAction().substring(ruleresponse.getAction().lastIndexOf("/") + 1);
        to = wh.getSDSInteractionEntry(rcvAsid, fromInteractionId);
        if (to == null) {
            throw new Exception("Cannot resolve SDS data for to ASID " + rcvAsid);
        }
        from = wh.getSDSInteractionEntry(sndAsid, toInteractionId);
        if (from == null) {
            throw new Exception("Cannot resolve SDS data for from ASID " + sndAsid);
        }
        String fromPartyId = from.get("partykey");
        replyTo = from.get("endpoint");
        String cpaId = from.get("cpaid");
        from = null;
        String toPartyId = to.get("partykey");
        String mhsActor = to.get("mhsactor");
        String syncReply = to.get("syncReply");
        String service = to.get("service");
        String action = to.get("interaction");
        String serviceAction = to.get("soapaction");
        String duplicateElimination = to.get("dupElim");
        String ackRequested = to.get("ackRq");
        to = null;

// Make the Soap Header
        StringBuilder sb = new StringBuilder();
        if (fwdReliable) {
            sb.append(asyncResponsePayload);
            substituteHandleNull(sb, "__ORIGINATING_TO_PARTY_ID__", toPartyId);
            substituteHandleNull(sb, "__ORIGINATING_FROM_PARTY_ID__", fromPartyId);
            substituteHandleNull(sb, "__MY_ACK_CPAID_LITERAL__", cpaId);
            if (conversationID == null) {
                substituteHandleNull(sb, "__CONVERSATION_ID__", randomUUID().toString().toUpperCase());
                // NOT SURE WHY SUBS IF NULL??
            } else {
                substituteHandleNull(sb, "__CONVERSATION_ID__", conversationID);
            }
            substituteHandleNull(sb, "__MESSAGEID__", randomUUID().toString().toUpperCase());
            Date now = new Date();
            substituteHandleNull(sb, "__TIMESTAMP__", ISO8601FORMATDATE.format(now));
            substituteHandleNull(sb, "__ORIGINAL_MESSAGEID__", messageId);

            return sb.toString();

        } else {
            sb = wh.getMessageHeaderTemplate();
            substituteHandleNull(sb, "__TOPARTYID__", toPartyId);
            substituteHandleNull(sb, "__FROMPARTYID__", fromPartyId);
            substituteHandleNull(sb, "__CPAID__", cpaId);
            if (conversationID == null) {
                substituteHandleNull(sb, "__CONVERSATIONID__", randomUUID().toString().toUpperCase());
                // NOT SURE WHY SUBS IF NULL??
            } else {
                substituteHandleNull(sb, "__CONVERSATIONID__", conversationID);
            }
            substituteHandleNull(sb, "__SERVICENAME__", toService);
            substituteHandleNull(sb, "__ACTIONNAME__", toInteractionId);
            substituteHandleNull(sb, "__MESSAGEID__", randomUUID().toString().toUpperCase());
            Date now = new Date();
            substituteHandleNull(sb, "__TIMESTAMP__", ISO8601FORMATDATE.format(now));
            substituteHandleNull(sb, "__INBOUND_MESSAGE_ID__", messageId);
            if (duplicateElimination.equals("always")) {
                substituteHandleNull(sb, "__DUPLICATEELIMINATION__", "<eb:DuplicateElimination/>");
            } else {
                substituteHandleNull(sb, "__DUPLICATEELIMINATION__", "");
            }
            if (ackRequested.equals("always")) {
                StringBuilder ar = new StringBuilder("<eb:AckRequested SOAP:mustUnderstand=\"1\" eb:version=\"2.0\" ");
                ar.append("eb:signed=\"false\" SOAP:actor=\"");
                ar.append(mhsActor);
                ar.append("\"/>");
                substituteHandleNull(sb, "__ACKREQUESTED__", ar.toString());
            } else {
                substituteHandleNull(sb, "__ACKREQUESTED__", "");
            }
            if (!syncReply.equalsIgnoreCase("none")) {
                substituteHandleNull(sb, "__SYNCREPLY__", "<eb:SyncReply SOAP:mustUnderstand=\"1\" eb:version=\"2.0\" SOAP:actor=\"http://schemas.xmlsoap.org/soap/actor/next\"/>");
            } else {
                substituteHandleNull(sb, "__SYNCREPLY__", "");
            }
            substituteHandleNull(sb, "__HL7XLINKREF__", "cid:" + hl7AttachmentId);
            String soapHeader = sb.toString();

            StringBuilder s = new StringBuilder();

            s.append(PREHL71);
            s.append(soapHeader);
            s.append(PREHL72);
            s.append(hl7AttachmentId);
            s.append(PREHL73);

            s.append("__PAYLOAD_BODY__");

            s.append(POSTHL7);

            return s.toString();
        }
    }

    protected String makeSafeReturnUrl(String u)
            throws Exception {
        // Find any ampersands and replace with &amp;
        Matcher m = ampPattern.matcher(u);
        String s = m.replaceAll("&amp;");
        return s;
    }

    public void asynchronousResponse(ServiceResponse ruleresponse, EvidenceMetaDataHandler evidenceMetaDataHandler)
            throws Exception {
        ToolkitService svc = null;
        String wrapper = null;
        String payload = null;
        //if the response required is not multipart mime
        if (fwdReliable) {
            payload = makeAsyncMessage(ruleresponse);
            //svc.setMultipart(false);
        } else {
            payload = asyncResponsePayload;
            wrapper = makeAsyncMessage(ruleresponse);
        }
        svc = ServiceManager.getInstance().getService("SpineSender");
        if (svc == null) {
            throw new Exception("Asked to send asynchronous response but no Sender service included");
        }
        SenderRequest sreq = null;
        if ((replyTo == null) || (replyTo.trim().length() == 0)) {
            Logger.getInstance().log(SEVERE, SpineAsynchronousWorker.class.getName(),
                    "Unable to resolve endpoint address for " + ruleresponse.getAction() + " to " + toPartyID);
            return;
        }

        if (ruleresponse.getCode() < 300) {
            sreq = new SenderRequest(payload, wrapper, replyTo);
            sreq.setRelatesTo(messageId);
            sreq.setAction(ruleresponse.getAction());
            sreq.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
            sreq.setLoggingSubDir(subDir);
            svc.execute(sreq);
        } else {
// What should be done if a >300 is got?????
            sreq = new SenderRequest(payload, wrapper, faultTo);
            sreq.setAction(ruleresponse.getAction());
            sreq.setRelatesTo(messageId);
            sreq.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
            sreq.setLoggingSubDir(subDir);
            svc.execute(sreq);
        }
    }

    private void resolveMessageId()
            throws Exception {
        try {
            messageId = asyncHandler.extractMessageId(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading message id: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveToAddress()
            throws Exception {
        try {
            toAddress = asyncHandler.extractToAddress(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading From Address: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveFromAddress()
            throws Exception {
        try {
            fromAddress = asyncHandler.extractFromAddress(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading To Address: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveFromPartyID()
            throws Exception {
        try {
            fromPartyID = asyncHandler.extractFromPartyID(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading From PartyID: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveToPartyID()
            throws Exception {
        try {
            toPartyID = asyncHandler.extractToPartyID(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading To PartyID: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveConversationID()
            throws Exception {
        try {
            conversationID = asyncHandler.extractConversationID(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading ConverdationId: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveCPAID()
            throws Exception {
        try {
            CPAId = asyncHandler.extractCPAID(sm.getEbXmlPart());
        } catch (Exception e) {
            throw new Exception("Error reading ConverdationId: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveRcvAsid()
            throws Exception {
        try {
            rcvAsid = handler.extractRcvAsid(sm.getHL7Part());
        } catch (Exception e) {
            throw new Exception("Error reading communicationFunctionSnd: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveSndAsid()
            throws Exception {
        try {
            sndAsid = handler.extractSndAsid(sm.getHL7Part());
        } catch (Exception e) {
            throw new Exception("Error reading communicationFunctionRcv: Message is not well formed : " + e.getMessage());
        }
    }

}
