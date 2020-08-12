/*
 Copyright 2018 Richard Robinon rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.meshinterceptor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.mesh.MeshControlFile;
import uk.nhs.digital.mait.tkwx.mesh.MeshData;
import uk.nhs.digital.mait.tkwx.mesh.MeshDataException;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.mesh.OpenMeshMessageRegister;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution.SubstitutionType;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.MESHFHIRITKRoutingActorInfAckOnly;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.SettableErrorCode;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 * This worker class takes a MeshRequest, parses the associated data file and
 * handles the call to the rules engine
 *
 * Still a work in progress - requires the call to validation
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class MeshInterceptWorker {

    private static Configurator config;

    private MeshInterceptHandler handler = null;
    private MeshRequest meshRequest = null;
    private File logFile = null;

    private static boolean inhibitValidation = false;

    private String eventCode = null;
    private String logDirectoryMailboxId;
    private String id = null;
    private OpenMeshMessageRegister ommr = OpenMeshMessageRegister.getInstance();
    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    static {
        try {
            config = Configurator.getConfigurator();
            inhibitValidation = isY(config.getConfiguration(INHIBITVALIDATION_PROPERTY));
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, MeshInterceptWorker.class.getName(), "Failure to initialise configurator - " + e.toString());
        }
    }

    /**
     * public constructor
     *
     * @param h Interceptor hander instance
     */
    public MeshInterceptWorker(MeshInterceptHandler h) {
        handler = h;
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * To process an IncomingRequest and responding with a simulated response
     * based upon the context path of the request . All messages which have
     * content are validated.
     *
     * @param request containing a validated control file
     * @throws Exception
     */
    public void processIncomingRequest(MeshRequest request) throws Exception {

        meshRequest = request;
        //Set logDirectory Mailboxid as the TO address as this controls the directory location where it needs saving
        logDirectoryMailboxId = meshRequest.getRequestMeshMessage().getControlFile().getFromDTS();

        ServiceResponse r = null;
        boolean incomingRequest = true;
        if (!readDataFile(incomingRequest)) {
            return;
        }

        ToolkitService svc = ServiceManager.getInstance().getService("RulesEngine");
        if (svc != null) {
            r = svc.execute(new Object[]{eventCode, meshRequest});
        } else {
//                doProcessor(req, resp);
        }

//        // Validate the Request if it has content and its not inhibited
        if (!inhibitValidation && meshRequest.getRequestMeshMessage().getDataFile().getContentLength() > 0) {
            validateRequest(eventCode, meshRequest);
        }
    }

    /**
     * To forward a Request to the MESH client software for forwarding. All
     * messages which have content are validated.
     *
     * @param request containing a validated control file
     * @throws Exception
     */
    public void processForwarder(MeshRequest request) throws Exception {

        meshRequest = request;
        MeshMessage mm = meshRequest.getRequestMeshMessage();
        //Set logDirectory Mailboxid as the TO address as this controls the directory location where it needs saving
        logDirectoryMailboxId = mm.getControlFile().getToDTS();

        ServiceResponse r = null;
        boolean incomingRequest = false;
        if (!readDataFile(incomingRequest)) {
            return;
        }

        //This is a forwarder message
        //Code to register request in OpenMeshMessageRegister
        ommr.registerMeshRequest(mm, id);
        mm.createRequest(id);

        // Validate the Request if it has content and its not inhibited
        if (!inhibitValidation && meshRequest.getRequestMeshMessage().getDataFile().getContentLength() > 0) {
            validateRequest(eventCode, meshRequest);
        }
    }

    protected boolean readDataFile(boolean incomingRequest)
            throws Exception {
        boolean success = false;

        MeshMessage meshMessage = meshRequest.getRequestMeshMessage();
        if (meshMessage == null) {
            return false;
        }
        try {
            meshMessage.parseData();

        } catch (MeshDataException e) {
            // RETURN INFACK TO SENDER: failure to find .dat file

            String s = "Failure to parse Mesh data file:" + e.getMessage();
            logFile = handler.makeLogFile(handler.getSavedMessagesDirectory(), logDirectoryMailboxId, null);
            LoggingFileOutputStream lfos = new LoggingFileOutputStream(logFile);
            EvidenceMetaDataHandler emdh = new EvidenceMetaDataHandler(meshMessage.getMeshMailboxId(),"MESH Server");
            lfos.setEvidenceMetaDataHandler(emdh);
            lfos.setMetaDataDescription("interaction-log","Non Parseable MESH data file");
            meshRequest.setLoggingStream(lfos);
            meshMessage.log(s, 2, true);
            if (incomingRequest) {
                sendFailureResponse(logDirectoryMailboxId, "10009", meshRequest);
            }
            lfos.logComplete();
            lfos.close();
            // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
            emdh.mainThreadEnded();
            return false;
        }
        if (meshMessage.getDataFile() != null && meshMessage.getDataFile().getContentLength() <= 0) {
            //The data file is empty and therefore no processing can happen
            //TODO RETURN INFACK TO SENDER: .dat file is empty
            String s = "No content in the Mesh data file: " + meshMessage.getDataFile().getDataFilePath().toString();
            logFile = handler.makeLogFile(handler.getSavedMessagesDirectory(), logDirectoryMailboxId, null);
            LoggingFileOutputStream lfos = new LoggingFileOutputStream(logFile);
            EvidenceMetaDataHandler emdh = new EvidenceMetaDataHandler(meshMessage.getMeshMailboxId(),"MESH Server");
            lfos.setEvidenceMetaDataHandler(emdh);
            lfos.setMetaDataDescription("interaction-log","No content in the Mesh data file");
            meshRequest.setLoggingStream(lfos);
            meshMessage.log(s, 2, true);
            if (incomingRequest) {
                sendFailureResponse(logDirectoryMailboxId, "10009", meshRequest);
            }
            lfos.logComplete();
            lfos.close();
            // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
            emdh.mainThreadEnded();
            return false;
        }

        // Update any template substitutions if it's a forwarding message
        if (!incomingRequest) {
            MeshData meshData = meshMessage.getDataFile();
            String content = new String(meshData.getContent());
            content = content.replaceAll("__BUNDLEID__", UUID.randomUUID().toString());
            content = content.replaceAll("__BUNDLEIDENTIFIER__", UUID.randomUUID().toString());
            content = content.replaceAll("__MESSAGEID__", UUID.randomUUID().toString());
            content = content.replaceAll("__TIMESTAMP__", ISO8601FORMATDATE.format(new Date()));

            meshData.createMeshData(content);
        }

        String request = new String(meshMessage.getDataFile().getContent());

        // We now know that there is a well formed control and data file so a log can be written
        // The log file must be named after the initiating request uuid
        String responseId = handler.extractResponseId(request);
        if (responseId != null && responseId.length() > 0) {
            //This is not a request and most likely a inf ack to the bus ack response sent from an initial request
            id = responseId;
        } else {
            String requestId = handler.extractRequestId(request);
            id = requestId;
        }

        // create a log file if it doesnt already exist elsewhere
        MeshMessage originatingMeshMessage = null;
        LoggingFileOutputStream lfos = null;
        if ((originatingMeshMessage = ommr.getOpenMeshMessage(id)) == null) {
            logFile = handler.makeLogFile(handler.getSavedMessagesDirectory(), logDirectoryMailboxId, id);
            lfos = new LoggingFileOutputStream(logFile);
            lfos.setEvidenceMetaDataHandler(new EvidenceMetaDataHandler(meshMessage.getMeshMailboxId(),"MESH Server"));
            lfos.setMetaDataDescription("interaction-log","MESH Transaction Log");
            //Code to register request in OpenMeshMessageRegister
            ommr.registerMeshRequest(meshMessage, id);
        } else {
            lfos = originatingMeshMessage.getLoggingStream();
        }
        meshRequest.setLoggingStream(lfos);
        MeshControlFile mcf = meshMessage.getControlFile();

        if (incomingRequest) {
            mcf.logControlFile("Incoming request");
            meshMessage.getDataFile().logDataFile(LogMarkers.END_INBOUND_MARKER);
        }

        meshMessage.setOriginatingRequestId(id);

        eventCode = handler.extractEventCode(request);
        String priorityCode = handler.extractPriorityCode(request);
        String priorityDisplay = handler.extractPriorityDisplay(request);
        meshRequest.setPriorityCode(priorityCode);
        meshRequest.setPriorityDisplay(priorityDisplay);

        if (eventCode == null || eventCode.trim().length() == 0) {
            //there is not sufficient data regarding the type of message eventCode is missing or cannot be understood
            //TODO RETURN INFACK TO SENDER
            String s = "there is not sufficient data regarding the type of message eventCode is missing or cannot be understood";
            meshMessage.log(s, 2, true);
            throw new Exception(s);
        } else {
            success = true;
        }

        return success;
    }

    private void sendFailureResponse(String requestorDTS, String errorCode, MeshRequest req) throws Exception {
        HashMap<String, Substitution> substitutions = new HashMap<>();
        substitutions.put("__SENDER_UUID__", new Substitution("__SENDER_UUID__", SubstitutionType.UUID_LOWER, null, null, null));
        substitutions.put("__IAID_BUNDLE_UUID__", new Substitution("__IAID_BUNDLE_UUID__", SubstitutionType.UUID_LOWER, null, null, null));
        substitutions.put("__IA_BUNDLE_UUID__", new Substitution("__IA_BUNDLE_UUID__", SubstitutionType.UUID_LOWER, null, null, null));
        substitutions.put("__IA_MESSAGE_HEADER_UUID__", new Substitution("__IA_MESSAGE_HEADER_UUID__", SubstitutionType.UUID_LOWER, null, null, null));
        substitutions.put("__IA_OPERATION_OUTCOME_UUID__", new Substitution("__IA_OPERATION_OUTCOME_UUID__", SubstitutionType.UUID_LOWER, null, null, null));
        substitutions.put("__TIMESTAMP__", new Substitution("__TIMESTAMP__", SubstitutionType.ISO8601DATE, null, null, null));
        substitutions.put("__REQUEST_DEST_ENDPOINT__", new Substitution("__REQUEST_DEST_ENDPOINT__", SubstitutionType.LITERAL, null, requestorDTS, null));
        substitutions.put("__REQUEST_UUID__", new Substitution("__REQUEST_UUID__", SubstitutionType.LITERAL, null, "UNREADABLE-IN-ORIGINAL-MESSAGE", null));
        MESHFHIRITKRoutingActorInfAckOnly nack = new MESHFHIRITKRoutingActorInfAckOnly();
        SettableErrorCode sec = (SettableErrorCode) nack;
        sec.setErrorCode(errorCode);
        nack.makeResponse(substitutions, req);
    }

    /**
     * Method to call validation in a new thread
     *
     * @param eventCode
     * @param req
     */
    private void validateRequest(String eventCode, MeshRequest req) {
        MeshInterceptorValidator miv = new MeshInterceptorValidator(config, handler, logDirectoryMailboxId);
        miv.validateRequest(eventCode, req);
    }

}
