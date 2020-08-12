/*
 Copyright 2014 Richard Robinson rrobinson@hscic.gov.uk  

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import java.net.URL;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import java.util.ArrayList;
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;
import uk.nhs.digital.mait.spinetools.spine.connection.ConnectionManager;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSSpineEndpointResolver;
import uk.nhs.digital.mait.spinetools.spine.connection.SDSconnection;
import uk.nhs.digital.mait.spinetools.spine.connection.SdsTransmissionDetails;
import uk.nhs.digital.mait.spinetools.spine.logging.SpineToolsLogger;
import uk.nhs.digital.mait.spinetools.spine.messaging.EbXmlMessage;
import uk.nhs.digital.mait.spinetools.spine.messaging.ITKDistributionEnvelopeAttachment;
import uk.nhs.digital.mait.spinetools.spine.messaging.ITKTrunkHandler;
import uk.nhs.digital.mait.spinetools.spine.messaging.Sendable;
import uk.nhs.digital.mait.spinetools.spine.messaging.SpineHL7Message;
import uk.nhs.digital.mait.spinetools.spine.messaging.SpineSOAPRequest;
import uk.nhs.digital.mait.tkwx.spinetools.BusAckDistributionEnvelopeHandler;
import uk.nhs.digital.mait.tkwx.spinetools.ITKTrunkDistributionEnvelopeHandler;
import uk.nhs.digital.mait.tkwx.spinetools.SendDistEnvelopeHandler;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.boot.SpineToolsTransport.*;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;

/**
 * Thread for transmitting Spine compliant SOAP messages using the SpineTools
 * library. Sets SpineTools library parameters by adding system properties of
 * the form  <code>uk.nhs.digital.mait.tkwx.spine.*</code><BR>
 * These are typically derived from corresponding tks property names. eg for the
 * SDS cache the value in <code>tks.spinetools.sds.cachedir</code> is set into
 * the system property named
 * <code>uk.nhs.digital.mait.tkwx.spine.sds.cachedir</code>
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class SpineToolsSender
        extends java.lang.Thread implements Sender {

    private static final String PAYLOADATTACH = "tks.spinetools.messaging.payloadasattachment";
    private static final String SESSION_CAPTURE_CLASS = "tks.spinetools.messaging.sessioncaptureclass";

    private String action = null;
    private String partyKey = null;
    private String ods = null;
    private String asid = null;
    private String payload = null;
    private String authorRole = null;
    private String authorUid = null;
    private String authorUrp = null;
    private long asyncWait = 0;
    private boolean payloadAttach = false;
    private int syncPort = 0;
    private Configurator config;
    private String correspondenceClientSt;
    private String setValue;
    private boolean correspondentClient = false;
    private boolean itkTrunk = false;
    private EvidenceMetaDataHandler evidenceMetaDataHandler = null;
    private String metaDataThreadId = null;

    public SpineToolsSender() {
    }

    /**
     * Sender override
     *
     * @param tk the ToolkitSimulator
     * @param what the Sender Request
     * @param useTls boolean indicating whether to use
     * @param logdir
     */
    @Override

    public void init(ToolkitSimulator tk, SenderRequest what, boolean useTls, String logdir) {
        payload = what.getPayload();
        action = what.getAction();
        ods = what.getODSCode();
        partyKey = what.getPartyKey();
        asid = what.getAsid();
        evidenceMetaDataHandler = what.getEvidenceMetaDataHandler();

        SpineToolsLogger stl = SpineToolsLogger.getInstance();
        stl.setAppName("TKW", logdir);
        try {
            config = Configurator.getConfigurator();

            // set a default session capture class for SpineTools called LoggingSessionCaptor (This is defined in tkw.jar)
            System.setProperty("uk.nhs.digital.mait.spinetools.spine.messaging.sessioncaptureclass", "uk.nhs.digital.mait.tkwx.spinetools.LoggingSessionCaptor");

            SPSetter.executeSettings(config, new String[][]{
                {CLEAR, "tkw.spine-test.cleartext"},
                {KEYSTORE_PROPERTY, "uk.nhs.digital.mait.spinetools.http.spine.certs"},
                {KEYPASS_PROPERTY, "uk.nhs.digital.mait.spinetools.http.spine.sslcontextpass"},
                {TRUSTSTORE_PROPERTY, "uk.nhs.digital.mait.spinetools.http.spine.trust"},
                {TRUSTPASS_PROPERTY, "uk.nhs.digital.mait.spinetools.http.spine.trustpass"},
                {CACHEDIR, "uk.nhs.digital.mait.spinetools.spine.sds.cachedir", "SDS cache directory not set"},
                {MYASID, "uk.nhs.digital.mait.spinetools.spine.sds.myasid", "SDS sending ASID not set"},
                {MYPARTYKEY, "uk.nhs.digital.mait.spinetools.spine.sds.mypartykey", "SDS sending Party Key not set"},
                {URLRESOLVERDIR, "uk.nhs.digital.mait.spinetools.spine.sds.urlresolver"},
                {URL, "uk.nhs.digital.mait.spinetools.spine.sds.url", "SDS URL not set"},
                {RETRY, "uk.nhs.digital.mait.spinetools.spine.messaging.retrytimerperiod"},
                {MYIP, "uk.nhs.digital.mait.spinetools.spine.connection.myip", "Connection sending IP not set"},
                {MESSAGESPOOLDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.messagedirectory", "Message Pool Directory not set"},
                {EXPIREDMESSAGEDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.expireddirectory", "Expired message directory not set"},
                {PERSISTDURATIONDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.persistdurations"},
                {EBXMLDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.defaultebxmlhandler.filesavedirectory", "EbXML saved directory not set"},
                // {DEDIR, SAVE_DIRECTORY, "Distribution Envelope Saved directory not set"},
                {SYNCDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.defaultsynchronousresponsehandler.filesavedirectory", "Synchronous message saved directory not set"},
                {NEGEBXMLACK, ORG_NEGEBXMLACK},
                {NEGEBXMLACKECODE, "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride.ecode"},
                {NEGEBXMLACKECODECONTEXT, "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride.ecodecontext"},
                {NEGEBXMLACKEDESC, "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride.edesc"},
                {SOAPFAULT, "uk.nhs.digital.mait.spinetools.spine.connection.soapfault"},
                {SYNCRESPONSECOUNTDOWN, "uk.nhs.digital.mait.spinetools.spine.syncresponsecountdown"},
                {ASYNCRESPONSEDELAYCOUNTDOWN, "uk.nhs.digital.mait.spinetools.spine.asyncresponsedelaycountdown"},
                {ASYNCHRONOUSRESPONSEDELAY, "uk.nhs.digital.mait.spinetools.spine.asynchronousebxmlreply.delay"},
                {INFACKRESPONSETYPE, ORG_NEGINFACK},
                {INFACKNACKERRORCODE, ORG_INFACKNACKERRORCODE},
                {INFACKNACKERRORTEXT, ORG_INFACKNACKERRORTEXT},
                {INFACKNACKDIAGTEXT, ORG_INFACKNACKDIAGTEXT},
                {SESSION_CAPTURE_CLASS, "uk.nhs.digital.mait.spinetools.spine.messaging.sessioncaptureclass"},});

            // workaround specific handling for DEDIR if there's no value in system props then set it from the config 
            // otherwise dont because the value may have been set by TKWATM dynamically
            if (System.getProperty(ORG_SAVE_DIRECTORY) == null) {
                System.setProperty(ORG_SAVE_DIRECTORY, config.getConfiguration(DEDIR));
            }

            new SPSetter(ITKTRUNK, s -> {
                itkTrunk = isY(s);
            }).execute(config);

            SPSetter.executeSettings(config, new SPSetter[]{
                new SPSetter(AUTHROLE, SPINE_MESSAGING_AUTHOR_ROLE, s -> authorRole = s),
                new SPSetter(AUTHUID, SPINE_MESSAGING_AUTHOR_UID, s -> authorUid = s),
                new SPSetter(AUTHURP, SPINE_MESSAGING_AUTHOR_URP, s -> authorUrp = s),
                new SPSetter(ASYNCWAIT, null, "Asynchronous wait period not set", s -> {
                    try {
                        asyncWait = Long.parseLong(s) * 1000;
                    } catch (NumberFormatException e) {
                        Logger.getInstance().log(SEVERE, SpineToolsSender.class.getName(),
                                "Asynchronous wait period not a valid integer - " + e.toString());
                    }
                }),
                new SPSetter(PAYLOADATTACH, s -> {
                    payloadAttach = isY(s);
                    try {
                        syncPort = Integer.parseInt(config.getConfiguration(SYNCLISTENPORT));
                    } catch (NumberFormatException numberFormatException) {
                        syncPort = DEFAULTLISTENPORT;
                    }
                })
            });

            // lambda to capture the value set
            new SPSetter(CORRESPONDENCE_CLIENT, "uk.nhs.digital.mait.tkwx.spine.correspondenceclient", st -> {
                correspondenceClientSt = st;
            }).execute(config);

            if (isY(correspondenceClientSt)) {
                correspondentClient = true;
                setConfiguratorOnSuccess(TKS_SENDER_PROPERTY, ORG_SENDER_PROPERTY, new Exception("Distribution Envelope Sender Address not set for ack response"));
                setConfiguratorOnSuccess(TKS_AUDIT_ID_PROPERTY, ORG_AUDIT_ID_PROPERTY, new Exception("Distribution Envelope Audit ID not set for ack response"));

                SPSetter.executeSettings(config, new String[][]{
                    {BUSACKRESPONSETYPE, ORG_RESPONSETYPE},
                    {BUSACKNACKERRORCODE, ORG_NACKERRORCODE},
                    {BUSACKNACKERRORTEXT, ORG_NACKERRORTEXT},
                    {BUSACKNACKDIAGTEXT, "uk.nhs.digital.mait.spinetools.spine.messaging.acknowledgements.nack.diagnostictext"}});

                System.setProperty("uk.nhs.digital.mait.tkwx.spine.correspondence.client", correspondenceClientSt);
            }

            //set the appropriate Handlers
            ConnectionManager cm = ConnectionManager.getInstance();
            //add the ITK Trunk handlers
            if (itkTrunk) {
                ITKTrunkHandler ith = new ITKTrunkHandler();
                cm.addHandler(ITK_TRUNK_SERVICE, ith);
                cm.addHandler("\"" + ITK_TRUNK_SERVICE + "\"", ith);
                if (correspondentClient) {
                    ith.addHandler(BIZACKSERVICE, new BusAckDistributionEnvelopeHandler());
                } else {
                    ith.addHandler(SEND_CDA_V2_SERVICE, new ITKTrunkDistributionEnvelopeHandler());
                }
                //For ITK3 ITK Trunk messages
                ith.addHandler(SEND_DIST_ENVELOPE_SERVICE, new SendDistEnvelopeHandler());
            }
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, SpineToolsSender.class.getName(), "A problem occurred whilst loading Spine Tools properties");
        }
        // register that a subthread has started which will require the EvidenceMetaDataHandler to remain open 
        if (evidenceMetaDataHandler != null) {
            metaDataThreadId = UUID.randomUUID().toString();
            evidenceMetaDataHandler.subThreadStarted(metaDataThreadId);
        }
        start();
    }

    /**
     * helper function uses a temporary attribute setValue to determine the
     * value of the property
     *
     * @param tksProperty
     * @param systemProperty
     * @param exToThrow
     * @throws Exception
     */
    private void setConfiguratorOnSuccess(String tksProperty, String systemProperty, Exception exToThrow) throws Exception {
        new SPSetter(tksProperty, systemProperty, exToThrow, st -> {
            setValue = st;
        }).execute(config);
        // can't throw an exception from a lambda but if we get here its ok to set this and we can let this block handle the exception
        config.setConfiguration(systemProperty, setValue);
    }

    /**
     * Thread override invoked on Thread.start()
     */
    @Override
    public void run() {
        this.setName("SpineToolsSenderThread");
        Logger l = Logger.getInstance();
        setName("SpineToolsSender");
        l.log("Sender", "Using SpineTools: svcIA - " + action + " ODS - " + ods + " ASID - " + asid + " Party Key - " + partyKey);
        try {

            ConnectionManager cm = ConnectionManager.getInstance();
            cm.listen(syncPort);
            SDSconnection sds = cm.getSdsConnection();
            SDSSpineEndpointResolver resolver = new SDSSpineEndpointResolver(sds);
            ArrayList<SdsTransmissionDetails> details;
            details = resolver.getTransmissionDetails(action, ods, asid, partyKey);
            SdsTransmissionDetails pds = details.get(0);
            SpineHL7Message msg;
            ITKDistributionEnvelopeAttachment deattachment = null;
            // Is the payload a Distribution Envelope requiring attachment to a separate ebXML MIME part?
            if (!payloadAttach) {
                msg = new SpineHL7Message(pds.getInteractionId(), payload);
            } else {
                msg = new SpineHL7Message(pds.getInteractionId(), "");
                DistributionEnvelope d = DistributionEnvelopeHelper.getInstance().getDistributionEnvelope(payload);
                deattachment = new ITKDistributionEnvelopeAttachment(d);

            }
            msg.setMyAsid(cm.getMyAsid());
            msg.setToAsid(pds.getAsids().get(0));

            // Set author details in msg
            msg.setAuthorRole(authorRole);
            msg.setAuthorUid(authorUid);
            msg.setAuthorUrp(authorUrp);
            Sendable sendable;
            if (pds.isSynchronous()) {
                sendable = new SpineSOAPRequest(pds, msg);
                cm.send(sendable, pds);
                while (sendable.getSynchronousResponse() == null) {
                    Thread.sleep(10);
                }
                // This thread was not terminating because cm hadnt been closed down.
                cm.stopListening();
            } else {
                EbXmlMessage e = new EbXmlMessage(pds, msg);
                sendable = e;
                if (payloadAttach) {
                    e.addAttachment(deattachment);
                }
                cm.send(sendable, pds);

                try {
                    Thread.sleep(asyncWait);
//                    java.util.logging.Logger.getLogger(SpineToolsSender.class.getName()).log(Level.SEVERE, null, ie);
//                    System.err.println("Asynchronous wait period interrupted" + ie.getMessage());
                } catch (InterruptedException ie) {
                }

                cm.stopListening();
                cm.stopRetryProcessor();
            }
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, SpineToolsSender.class.getName(), ex.getMessage());
        }
        // indicate to the EvidecnMetatDataHandler that this subthread has ended and the evidecnce can be attempted to be written
        if (evidenceMetaDataHandler != null) {
            evidenceMetaDataHandler.subThreadEnded(metaDataThreadId);
        }
    }
}
