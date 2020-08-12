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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.IOException;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
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
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptorValidator;
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpLogFileGenerator;
import uk.nhs.digital.mait.tkwx.itklogverifier.LogVerifier;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.WrapperHelper;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
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
    private static final SimpleDateFormat FILEDATE = new SimpleDateFormat(HL7FORMATDATEMASK);
    private static final String EBXMLERRORACTION = "urn:urn:oasis:names:tc:ebxml-msg:service/MessageError";
    private long asyncResponseDelay = 0;

    protected String toPartyID = null;
    protected String fromPartyID = null;
    protected String conversationID = null;
    protected String CPAId = null;
    private WrapperHelper wh = null;
    private boolean unreliable = false;
    private boolean fwdReliable = false;

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
    public void handle(HttpRequest req, HttpResponse resp)
            throws HttpException, InterruptedException, IOException {
        ServiceResponse r = null;
        try {
            if (!doChecks(req, resp)) {
                return;
            }
            req.setLoggingFileOutputStream(logfile);

            if (synchronousResponseDelay != 0) {
                try {
                    Thread.sleep(synchronousResponseDelay);
                } catch (InterruptedException e) {
                    Logger.getInstance().log(WARNING, SpineAsynchronousWorker.class.getName(), "SynchronousResponseDelay sleep() interrupted.");
                }
            }

            ToolkitService svc = ServiceManager.getInstance().getService("RulesEngine");
            if (svc != null) {
                r = svc.execute(new String[]{soapaction, sm.getHL7Part()});
                if (r != null) {
                    if (r.getAction() != null) {
                        if (r.getAction().equals("ebXMLerror")) {
                            synchronousEbXMLError(req, resp, r);
                            return;
                        }
                    }
                    if (r.getCode() < 300) {
                        // response code 0 means hand off to Processor Service
                        if (r.getCode() == 0) {
                            doProcess(req, resp);
                            return;
                        }
                        synchronousAck(req, resp, r);
                        if (asyncResponseDelay != 0) {
                            try {
                                Thread.sleep(asyncResponseDelay);
                            } catch (InterruptedException e) {
                                Logger.getInstance().log(WARNING, SpineAsynchronousWorker.class.getName(), "AsynchronousResponseDelay sleep() interrupted.");
                            }
                        }
                        // Check to see if a NONE response has been asked for 
                        if (r.getResponse() != "") {
                            asynchronousResponse(r);
                        }

                    } else {
                        synchronousResponse(r.getCode(), "Internal server error", r.getResponse(), req, resp, r.getAction());
                    }
                } else {
//Assumes that an ebXML no response is required
                    synchronousResponse(202, "OK", "", req, resp, null);
                    req.setHandled(true);
//                    resp.forceClose();
                }
            } else {
                doProcess(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                synchronousResponse(500, "Internal server error", "Error reading message: " + e.getMessage(), req, resp, null);
            } catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());

        } finally {

            // Validate the Request if its not inhibited
            if (!inhibitValidation) {
                HttpInterceptorValidator hiv = new HttpInterceptorValidator(config, soapaction, null);

                // Send the simulator evidence and validation report (if available) to the Scheduler
//                if (scenarioInstantiationTrigger) {
//                    hiv.registerForReport((EvidenceInterface) this);
//                }
                hiv.validateRequest(req, subDir);
//                if (scenarioInstantiationTrigger) {
//                    EvidenceHandler eh = new EvidenceHandler();
//                    // add the request/response to the handler
//                    eh.addEvidence(req);
//                    // add the response payload to the handler
//                    eh.addEvidence(r);
//                    // add the validation report 
//                    eh.addEvidence(validationReport);
//                    eh.sendEvidence();
//                }

            }
            // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
            evidenceMetaDataHandler.mainThreadEnded();
        }
    }

    @Override
    protected boolean doChecks(HttpRequest req, HttpResponse resp)
            throws HttpException {
        ServiceResponse r = null;
        try {
            readMessage(req);
            // By now we've read the message - first thing to do is to see if
            // we're going to try to verify the signature, and do so.
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String m = this.makeSoapFault("soap:Client", "The service/interaction is not supported for the requested URI", "urn:nhs:names:errors:tms", "101", "Error", "HTTP Header - SOAPAction", "Error reading message: " + e.getMessage());
                synchronousResponse(500, "Internal server error", m, req, resp, null);
            } catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());
        }
        return true;
    }

    /**
     * This is a stub in TKW-x since the Process service is not supported
     *
     * @param req
     * @param resp
     * @throws Exception
     */
    protected void doProcess(HttpRequest req, HttpResponse resp)
            throws Exception {
//        ServiceResponse r = null;
//        ToolkitService svc = ServiceManager.getInstance().getService("Processor");
//        if (svc != null) {
//            ProcessData p = new ProcessData(soapaction, sm.getHL7Part());
//            p.setRequestContext(req.getContext());
//            p.setAsynchronous();
//            r = svc.execute(p);
//            if (r != null) {
//                if (r.getCode() < 300) {
//                    synchronousAck(req, resp, r);
//                    asynchronousResponse(r);
//                } else {
//                    synchronousResponse(r.getCode(), "Internal server error", r.getResponse(), req, resp, null);
//    }
//            } else {
//                req.setHandled(true);
////                resp.forceClose();
//            }
//        } else {
        throw new Exception("No rules r process defined for action: " + soapaction);
//        }
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
        evidenceMetaDataHandler = new EvidenceMetaDataHandler(sndAsid, "ASID");

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

        String smd = handler.getSavedMessagesDirectory();
        if (smd != null) {
            subDir = HttpLogFileGenerator.generateSubFolderName(req, soapaction);
            String rmlog = HttpLogFileGenerator.createLogFile(req, smd, subDir);
            logfile = new LoggingFileOutputStream(rmlog);
            logfile.setEvidenceMetaDataHandler(evidenceMetaDataHandler);
            logfile.setMetaDataDescription("interaction-log","Async Request Log");
            logfile.write(req.getRequestType());
            logfile.write(" ");
            logfile.write(req.getContext());
            logfile.write(" HTTP/1.1\r\n");
            for (String s : req.getFieldNames()) {
                String v = req.getField(s);
                logfile.write(s);
                logfile.write(": ");
                logfile.write(v);
                logfile.write("\r\n");
            }
            logfile.write("\r\n");
            logfile.write(sm.getPresentedFile());
            logfile.flush();
            logfile.write("\r\n" + LogMarkers.END_INBOUND_MARKER + "\r\n\r\n");
            logfile.flush();
            logfile.logComplete();
            logfile.close();

            // requires an explcit Y to inhibit
            if (!isY(System.getProperty(DONTSIGNLOGS_PROPERTY, "N"))) {
                LogVerifier l = LogVerifier.getInstance();
                l.makeSignature(rmlog);
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

    protected void synchronousAck(HttpRequest req, HttpResponse resp, ServiceResponse r)
            throws Exception {
        if (asyncHandler.getAckLoadException() != null) {
            throw asyncHandler.getAckLoadException();
        }
        if (asyncHandler.getSyncAckTemplate() == null) {
            synchronousResponse(202, "Accepted", "", req, resp, null);
            return;
        }
        if (unreliable || fwdReliable) {
            synchronousResponse(202, "Accepted", "", req, resp, null);
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
        synchronousResponse(202, "Accepted", ack.toString(), req, resp, r.getAction());
    }

    protected void synchronousEbXMLError(HttpRequest req, HttpResponse resp, ServiceResponse r)
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
        synchronousResponse(-1, "OK", ack.toString(), req, resp, EBXMLERRORACTION);
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
            sb.append(ruleresponse.getResponse());
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

    protected void asynchronousResponse(ServiceResponse ruleresponse)
            throws Exception {
        ToolkitService svc = null;
        String wrapper = null;
        String payload = null;
        //if the response required is not multipart mime
        if (fwdReliable) {
            payload = makeAsyncMessage(ruleresponse);
            //svc.setMultipart(false);
        } else {
            payload = ruleresponse.getResponse();
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
