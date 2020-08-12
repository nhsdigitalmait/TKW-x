/*
 Copyright 2018 Richard Robinson rrobinson@nhs.net

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
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.mesh.MeshControlFile;
import uk.nhs.digital.mait.tkwx.mesh.MeshControlStatus;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.mesh.OpenMeshMessageRegister;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.MeshTransport;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 * Class to "intercept" a Mesh transaction.
 *
 * The interceptor is designed to act upon new files arriving in the specified
 * mailbox. The nature of Mesh using an mailbox directory structure means that a
 * "forward" function as used in a streamed messaging service is not necessary
 * as the interceptor is not diverting the normal transfer. The control file is
 * the driving entity the contents of which will then be used to identify and
 * processIncomingRequest the data file
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class MeshInterceptHandler
        extends AbstractMeshRequestHandler {


    private static final String REQUESTUUID = "//fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:id/@value";
    private static final String RESPONSEUUID = "//fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:response/fhir:identifier/@value";
    private static final String EVENTCODE = "//fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:event/fhir:code/@value";
    private static final String PRIORITYCODE = "//fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:extension/fhir:extension[@url=\"Priority\"]/fhir:valueCoding/fhir:code/@value";
    private static final String PRIORITYDISPLAY = "//fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:extension/fhir:extension[@url=\"Priority\"]/fhir:valueCoding/fhir:display/@value";
    private static final SimpleDateFormat FILEDATE = new SimpleDateFormat(FILEDATEMASK);
    private Configurator config;

    /**
     * Constructor
     *
     * @throws java.lang.Exception
     */
    public MeshInterceptHandler() throws Exception {
        setXpathExpressions();
    }

    private void setXpathExpressions() throws XPathExpressionException, XPathFactoryConfigurationException {
        getRequestId = getXpathExtractor(REQUESTUUID);
        getResponseId = getXpathExtractor(RESPONSEUUID);
        getEventCode = getXpathExtractor(EVENTCODE);
        getPriorityCode = getXpathExtractor(PRIORITYCODE);
        getPriorityDisplay = getXpathExtractor(PRIORITYDISPLAY);
    }

    /**
     *
     * @param t
     * @throws Exception
     */
    @Override
    public void setToolkit(MeshTransport t)
            throws Exception {
        super.setToolkit(t);
        config = Configurator.getConfigurator();
        savedMessagesDirectory = config.getConfiguration(SAVEDMESSAGES_PROPERTY);
    }

    /**
     *
     * @param mailboxType
     * @param ctlFile
     *
     * @throws uk.nhs.digital.mait.tkwx.mesh.MeshDataException
     */
    @Override
    public void handle(String mailboxType, Path ctlFile) throws Exception {

        System.out.println("message arrived in " + mailboxType + " at " + ctlFile.getFileName());

        MeshRequest meshRequest = new MeshRequest();
        MeshMessage meshMessage = new MeshMessage(meshMailboxId);
        OpenMeshMessageRegister register = OpenMeshMessageRegister.getInstance();
        try {
            meshRequest.setRequestMessage(meshMessage);
            // Give the control file an opportunity to be completely written by 
            // the underlying OS to the directory by MESH Client
            Thread.sleep(1000);
            meshMessage.parseControl(ctlFile);

        } catch (Exception e) {
            // These errors require logging. They are the sort which should never 
            // happen when used with the MESH Client software, because they should 
            // not ever get received with many of the errors but here for completion 
            // and debugging
            // There is not enough information as the control file could not be reliably
            // understood to be able to respond or log per meesage at all
            String s = "Failure to open Mesh mailbox file:" + e.getMessage();
            //log to Global TKW logger
            Logger l = Logger.getInstance();
            l.log("Error in the processing of the control file(" + ctlFile + "): " + s);
            return;
        }

        // Control file has been successfully read so logging can happen to the correct directory
        if (mailboxType.endsWith(MeshTransport.NAMEIN)) {
            //Messages arriving to the inbox can be one of 4 possibilities:
            // 1. Incoming request (MessageType = Data) CLIENT MODE (GP SYSTEM)
            // 2. Transfer Report relating to messages posted to the OUTbox (MessageType = Report) CLIENT MODE (GP SYSTEM) & SERVER MODE (HOSPITAL SYSTEM)
            // 3. Error Report which is unrelated to any messages sent/received e.g. polling errors/authentication errors (MessageType = Report)
            // 4. Incoming Acknowledement (MessageType = Data) CLIENT MODE (GP SYSTEM) & SERVER MODE (HOSPITAL SYSTEM)
            switch (meshMessage.getControlFile().getMessageType()) {
                case "Data":
                    MeshInterceptWorker miw = new MeshInterceptWorker(this);
                    try {
                        //First check to see if we can processIncomingRequest this type of file from the workflow Id
                        MeshControlFile mcf = meshMessage.getControlFile();
                        mcf.getWorkflowIdFromXref(mcf.getWorkflowId());

                        miw.processIncomingRequest(meshRequest);
                    } catch (Exception e) {
                        // Catch ALL to handle any errors in processing request
                        String s = "Error in the processing of the response to the request: ";
                        Logger l = Logger.getInstance();
                        l.log(s + e.toString());
                    }
                    break;
                case "Report":
                    //Check to see if the incoming is an error report or related to a sent message
                    MeshControlStatus mcs = meshMessage.getControlFile().getControlStatus();
                    StringBuilder logbuilder = new StringBuilder();
                    if (mcs.getEvent().equals("TRANSFER")) {
                        String localId = meshRequest.getRequestMeshMessage().getControlFile().getLocalId();
                        MeshMessage mm = register.getOpenMeshMessage(localId);
                        //If the incoming message cant be tied up to an existing open message create a new log
                        if (mm != null) {
                            logbuilder.append("************ START INBOX TRANSFER REPORT **************" + "\n");
                            logbuilder.append("MESH transfer file name: ").append(mm.getMeshFileName()).append("\n");
                            logbuilder.append("MESH local id: ").append(localId).append("\n");
                            logbuilder.append(mcs.logStatusReport()).append("\n");
                            if (mcs.getStatus().equals("SUCCESS")) {
                                // as all is done and dusted with this message the message can be closed/unregistered
                                //However a short wait beore deregistering ensures that the SENTbox transfer report is read and complete

                            } else {
                                //TO DO insert resend code
                                logbuilder.append("Send unsuccessful - Resend code not yet implemented" + "\n");
                            }
                            logbuilder.append("************ END INBOX TRANSFER REPORT **************");
                            mm.log(logbuilder.toString(), 2, true);
                            Thread.sleep(1000);
                            register.deRegisterMeshRequest(localId);
                        } else {
                            //create a new log
                            LoggingFileOutputStream lfos = new LoggingFileOutputStream(makeLogFile(getSavedMessagesDirectory(), meshMessage.getControlFile().getToDTS(), null));
                            EvidenceMetaDataHandler emdh = new EvidenceMetaDataHandler(meshMailboxId,"MESH Server");
                            lfos.setEvidenceMetaDataHandler(emdh);
                            lfos.setMetaDataDescription("interaction-log","TRANSFER Report arrived in INbox that cannot be related back to a request sent from this mailbox");
                            meshMessage.setLoggingStream(lfos);
                            logbuilder.append("************ START INBOX TRANSFER REPORT **************" + "\n");
                            logbuilder.append("MESH transfer file name: ").append(meshMessage.getMeshFileName()).append("\n");
                            logbuilder.append("MESH local id: ").append(localId).append("\n");
                            logbuilder.append("TRANSFER Report arrived in INbox that cannot be related back to "
                                    + "a request sent from this mailbox. This may be because the original "
                                    + "message report timeout period expired" + "\n");
                            meshMessage.getControlFile().logControlFile("MESH");
                            logbuilder.append(mcs.logStatusReport()).append("\n");
                            logbuilder.append("************ END INBOX TRANSFER REPORT**************");
                            meshMessage.log(logbuilder.toString(), 2, true);
                            lfos.logComplete();
                            lfos.close();
                           // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
                            emdh.mainThreadEnded();
                            // Cant resend as original message has expired
                        }
                        System.out.println("INbox transfer report read for " + localId);

                    } else {
                        Logger l = Logger.getInstance();
                        l.log("Event: " + mcs.getEvent() + " has arrived in INbox \n" + mcs.logStatusReport());
                    }
                    break;
                default:
                    //log to Global TKW logger
                    Logger l = Logger.getInstance();
                    l.log("Message Type of incoming control message is not Data or Report: " + meshMessage.getMeshFileName());
                    return;
            }

        } else if (mailboxType.endsWith(MeshTransport.NAMEFORWARDER)) {
            // The MESH forwarder is so that TKW can forward MESH request messages using the MESH client software, but take control 
            // of the acknowledgement responses so that logging and report generation are coordinated
            switch (meshMessage.getControlFile().getMessageType()) {
                case "Data":
                    MeshInterceptWorker miw = new MeshInterceptWorker(this);
                    try {
                         //Check to see if we can process this type of file from the workflow Id
                        MeshControlFile mcf = meshMessage.getControlFile();
                        mcf.getWorkflowIdFromXref(mcf.getWorkflowId());

                        miw.processForwarder(meshRequest);
                    } catch (Exception e) {
                        // Catch ALL to handle any errors in processing request
                        String s = "Error in the processing of the response to the request: ";
                        Logger l = Logger.getInstance();
                        l.log(s + e.toString());
                    }
                    break;
            }

        } else if (mailboxType.endsWith(MeshTransport.NAMESENT)) {
            // Messages arriving in the SENTbox are copies of those placed in the 
            // OUTbox after they have been sent. However the Mesh client has added 
            // a status report to the control file 
            String localId = meshRequest.getRequestMeshMessage().getControlFile().getLocalId();
            MeshMessage mm = register.getOpenMeshMessage(localId);
            MeshControlStatus mcs = meshMessage.getControlFile().getControlStatus();
            //If the incoming message cant be tied up to an existing open message create a new log
            StringBuilder logbuilder = new StringBuilder();
            if (mcs.getEvent().equals("TRANSFER")) {
                if (mm != null) {
                    logbuilder.append("************ START SENTBOX TRANSFER REPORT **************\n");
                    logbuilder.append("MESH transfer file name: ").append(mm.getMeshFileName()).append("\n");
                    logbuilder.append("MESH local id: ").append(localId).append("\n");
                    logbuilder.append(mcs.logStatusReport()).append("\n");
                    if (mcs.getStatus().equals("SUCCESS")) {
                    } else {
                        //TO DO insert resend code
                        logbuilder.append("Send unsuccessful - Resend code not yet implemented");
                    }
                    logbuilder.append("************ END SENTBOX TRANSFER REPORT **************");
                    mm.log(logbuilder.toString(), 2, true);

                } else {
                    //create a new log
                    LoggingFileOutputStream lfos = new LoggingFileOutputStream(makeLogFile(getSavedMessagesDirectory(), meshMessage.getControlFile().getToDTS(), null));
                    EvidenceMetaDataHandler emdh = new EvidenceMetaDataHandler(meshMailboxId,"MESH Server");
                    lfos.setEvidenceMetaDataHandler(emdh);
                    lfos.setMetaDataDescription("interaction-log","TRANSFER Report arrived in SENTbox that cannot be related back to a request sent from the OUTbox.");
                    meshMessage.setLoggingStream(lfos);
                    logbuilder.append("************ START SENTBOX TRANSFER REPORT **************" + "\n");
                    logbuilder.append("MESH transfer file name: ").append(meshMessage.getMeshFileName()).append("\n");
                    logbuilder.append("MESH local id: ").append(localId).append("\n");
                    logbuilder.append("TRANSFER Report arrived in SENTbox that cannot be related back to "
                            + "a request sent from the OUTbox. This may be because the original "
                            + "message report timeout period expired" + "\n");
                    logbuilder.append(meshMessage.getControlFile().getControlStatus().logStatusReport() + "\n");
                    logbuilder.append("************ END SENTBOX TRANSFER REPORT**************");
                    meshMessage.log(logbuilder.toString(), 2, true);
                    lfos.logComplete();
                    lfos.close();
                    // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
                    emdh.mainThreadEnded();
               }
                System.out.println("SENTbox transfer report read for " + localId);

            } else {
                Logger l = Logger.getInstance();
                l.log("Event: " + mcs.getEvent() + " has arrived in SENTbox \n" + mcs.logStatusReport());
            }
        }
    }

    protected File makeLogFile(String logDirectory, String fromDTS, String requestUuid) throws IOException {
        String logDirString = getLogDirString(logDirectory, fromDTS, requestUuid);
        File file = new File(logDirString);
        file.getParentFile().mkdirs();
     
        return file;
    }

    protected String getLogDirString(String logDirectory, String fromDTS, String requestUuid) throws IOException {
        StringBuilder sb = new StringBuilder(logDirectory);
        sb.append(File.separator);
        if (fromDTS != null) {
            sb.append(fromDTS);
            sb.append(File.separator);
        }
        sb.append("transactional_logs");
        sb.append(File.separator);
        if (requestUuid != null) {
            sb.append(requestUuid);
            sb.append("_");
        }
        sb.append(FILEDATE.format(new Date()));
        sb.append(".log");
        return sb.toString();
    }

}
