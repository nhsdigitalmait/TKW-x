/*
 Copyright 2014  Richard Robinson rrobinson@hscic.gov.uk

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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.util.Properties;
import uk.nhs.digital.mait.spinetools.spine.connection.ConnectionManager;
import uk.nhs.digital.mait.spinetools.spine.logging.SpineToolsLogger;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ReconfigureTags;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SPSetter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.spinetools.spine.messaging.ITKTrunkHandler;
import uk.nhs.digital.mait.tkwx.spinetools.BusAckDistributionEnvelopeHandler;
import uk.nhs.digital.mait.tkwx.spinetools.ITKTrunkDistributionEnvelopeHandler;
import uk.nhs.digital.mait.tkwx.spinetools.SendDistEnvelopeHandler;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Core TKW service.
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class SpineToolsTransport
        implements ToolkitService,
        uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable, 
        uk.nhs.digital.mait.tkwx.tk.internalservices.Stoppable {

    public static final String CACHEDIR = "tks.spinetools.sds.cachedir";
    public static final String MYASID = "tks.spinetools.sds.myasid";
    public static final String MYPARTYKEY = "tks.spinetools.sds.mypartykey";

    public static final String URLRESOLVERDIR = "tks.spinetools.sds.urlresolver";
    public static final String URL = "tks.spinetools.sds.url";
    public static final String RETRY = "tks.spinetools.messaging.retrytimerperiod";
    public static final String MYIP = "tks.spinetools.connection.myip";
    public static final String MESSAGESPOOLDIR = "tks.spinetools.messaging.messagedirectory";
    public static final String EXPIREDMESSAGEDIR = "tks.spinetools.messaging.expireddirectory";
    public static final String PERSISTDURATIONDIR = "tks.spinetools.messaging.persistdurations";
    public static final String EBXMLDIR = "tks.spinetools.messaging.defaultebxmlhandler.filesavedirectory";
    public static final String DEDIR = "tks.spinetools.messaging.defaultdistributionenvelopehandler.filesavedirectory";
    public static final String SYNCDIR = "tks.spinetools.messaging.defaultsynchronousresponsehandler.filesavedirectory";
    public static final String SESSION_CAPTURE_CLASS = "tks.spinetools.messaging.sessioncaptureclass";
    public static final String CLEAR = "tkw.spine-test.cleartext";
    public static final String TKS_AUDIT_ID_PROPERTY = "tks.spinetools.desender.auditidentity";
    public static final String TKS_SENDER_PROPERTY = "tks.spinetools.desender.senderaddress";
    public static final String BUSACKNACKERRORCODE = "tks.spinetools.busack.nack.errorcode";
    public static final String BUSACKNACKERRORTEXT = "tks.spinetools.busack.nack.errortext";
    public static final String BUSACKNACKDIAGTEXT = "tks.spinetools.busack.nack.diagnostictext";
    public static final String CORRESPONDENCE_HOST = "tks.spinetools.correspondence.host";
    public static final String CORRESPONDENCE_CLIENT = "tks.spinetools.correspondence.client";
    public static final String RESPONSETYPE = "tks.spinetools.correspondenceresponder.positiveresponsetype";
    public static final String ITKTRUNK = "tks.spinetools.itktrunk";
    public static final String BUSACKRESPONSETYPE = "tks.spinetools.busack.positiveresponsetype";
    public static final String INFACKRESPONSETYPE = "tks.spinetools.infack.positiveresponsetype";
    public static final String INFACKNACKERRORCODE = "tks.spinetools.infack.nack.errorcode";
    public static final String INFACKNACKERRORTEXT = "tks.spinetools.infack.nack.errortext";
    public static final String INFACKNACKDIAGTEXT = "tks.spinetools.infack.nack.diagnostictext";
    public static final String AUTHROLE = "tks.spinetools.messaging.authorrole";
    public static final String AUTHUID = "tks.spinetools.messaging.authoruid";
    public static final String AUTHURP = "tks.spinetools.messaging.authorurp";
    public static final String NEGEBXMLACK = "tks.spinetools.connection.negativeebxmloverride";
    public static final String NEGEBXMLACKECODE = "tks.spinetools.connection.negativeebxmloverride.ecode";
    public static final String NEGEBXMLACKECODECONTEXT = "tks.spinetools.connection.negativeebxmloverride.ecodecontext";
    public static final String NEGEBXMLACKEDESC = "tks.spinetools.connection.negativeebxmloverride.edesc";
    public static final String SOAPFAULT = "tks.spinetools.connection.soapfault";
    public static final String ASYNCHRONOUSRESPONSEDELAY = "tks.spinetools.asynchronousebxmlreply.delay";
    public static final String ASYNCRESPONSEDELAYCOUNTDOWN = "tks.spinetools.asyncresponsedelaycountdown";
    public static final String SYNCRESPONSECOUNTDOWN = "tks.spinetools.syncresponsecountdown";
    public static final String SYNCLISTENPORT = "tks.SpineToolsTransport.listenport";

    // moved after property name refactoring
    public static final String TOPARTYKEY = "tks.spinetools.transmit.partykey";
    public static final String ASYNCWAIT = "tks.spinetools.messaging.asynchronouswaitperiod";

    private static final String SPINE_TOOLS_SIMULATOR = "SpineTools simulator";

    private final ToolkitSimulator simulator = null;
    private final String serviceName = null;
    private Properties bootProperties = null;
    private boolean correspondenceHost = false;
    private String correspondenceHostSt;

    private boolean itkTrunk = false;
    private ConnectionManager cm = null;
    private String setValue;

    public SpineToolsTransport() {
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void stop()
            throws Exception {
        cm.stopListening();
    }

    /**
     *
     * @param p
     * @throws Exception
     */
    @Override
    public void reconfigure(Properties p)
            throws Exception {
        cm.stopListening();
        boot(simulator, p, serviceName);
    }

    /**
     * reconfigure tag what with value
     *
     * @param what
     * @param value
     * @return previous value if possible
     * @throws Exception
     */
    @Override
    public String reconfigure(String what, String value)
            throws Exception {
        if (what.contentEquals(ReconfigureTags.SAVED_MESSAGES)) {
            System.setProperty(ORG_SAVE_DIRECTORY, value);
            bootProperties.setProperty(DEDIR, value);

            return value;
        }
        throw new Exception("Cannot reconfigure " + what);
    }

    /**
     *
     * @param t toolkit simulator object
     * @param p boot properties object
     * @param s string not used in this overrride
     * @throws Exception
     */
    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        // Mod SCF for consistency with httptransport
        bootProperties = p;
        SpineToolsLogger stl = SpineToolsLogger.getInstance();
        stl.setAppName("TKW", p.getProperty(SAVEDMESSAGES_PROPERTY));

        //set a default sessionCaptor class
        System.setProperty("uk.nhs.digital.mait.spinetools.spine.messaging.sessioncaptureclass", "uk.nhs.digital.mait.tkwx.spinetools.LoggingSessionCaptor");

        SPSetter.executeSettings(p, new String[][]{
            {CLEAR, "tkw.spine-test.cleartext"},
            {KEYSTORE_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.certs"},
            {KEYPASS_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.sslcontextpass"},
            {TRUSTSTORE_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.trust"},
            {TRUSTPASS_PROPERTY, "uk.nhs.digital.mait.tkwx.http.spine.trustpass"},
            {AUTHROLE, SPINE_MESSAGING_AUTHOR_ROLE},
            {AUTHUID, SPINE_MESSAGING_AUTHOR_UID},
            {AUTHURP, SPINE_MESSAGING_AUTHOR_URP},
            {NEGEBXMLACK, ORG_NEGEBXMLACK},
            {NEGEBXMLACKECODE, "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride.ecode"},
            {NEGEBXMLACKECODECONTEXT, "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride.ecodecontext"},
            {NEGEBXMLACKEDESC, "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride.edesc"},
            {SESSION_CAPTURE_CLASS, "uk.nhs.digital.mait.spinetools.spine.messaging.sessioncaptureclass"},});

        SPSetter.executeSettings(p, new SPSetter[]{
            new SPSetter(CACHEDIR, "uk.nhs.digital.mait.spinetools.spine.sds.cachedir", new Exception("SDS cache directory not set")),
            new SPSetter(MYASID, "uk.nhs.digital.mait.spinetools.spine.sds.myasid", new Exception("SDS sending ASID not set")),
            new SPSetter(MYPARTYKEY, "uk.nhs.digital.mait.spinetools.spine.sds.mypartykey", new Exception("SDS sending Party Key not set")),
            new SPSetter(URLRESOLVERDIR, "uk.nhs.digital.mait.spinetools.spine.sds.urlresolver", new Exception("SDS URL resolver directory sending not set")),
            new SPSetter(URL, "uk.nhs.digital.mait.spinetools.spine.sds.url", new Exception("SDS URL not set")),
            new SPSetter(RETRY, "uk.nhs.digital.mait.spinetools.spine.messaging.retrytimerperiod", new Exception("Messaging Retry Period not set")),
            new SPSetter(MYIP, "uk.nhs.digital.mait.spinetools.spine.connection.myip", new Exception("Connection sending IP not set")),
            new SPSetter(MESSAGESPOOLDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.messagedirectory", new Exception("Message Pool Directory not set")),
            new SPSetter(EXPIREDMESSAGEDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.expireddirectory", new Exception("Expired message directory not set")),
            new SPSetter(PERSISTDURATIONDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.persistdurations", new Exception("Persist Duration directory not set")),
            new SPSetter(EBXMLDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.defaultebxmlhandler.filesavedirectory", new Exception("EbXML saved directory not set")),
            new SPSetter(DEDIR, ORG_SAVE_DIRECTORY, new Exception("Distribution Envelope Saved directory not set"),
            st -> {
                //System.out.println("SpineToolsTransport setting " + DEDIR + " to " + st);
            }),
            new SPSetter(SYNCDIR, "uk.nhs.digital.mait.spinetools.spine.messaging.defaultsynchronousresponsehandler.filesavedirectory", new Exception("Synchronous message saved directory not set")),});

        int listenPort = Integer.parseInt(p.getProperty(SYNCLISTENPORT));
        if (listenPort == 0) {
            listenPort = DEFAULTLISTENPORT;
        }

        // lambda to capture the value set
        new SPSetter(CORRESPONDENCE_HOST, "uk.nhs.digital.mait.tkwx.spine.correspondence.host", st -> {
            correspondenceHostSt = st;
        }).execute(p);

        if (isY(correspondenceHostSt)) {
            correspondenceHost = true;

            setConfiguratorOnSuccess(p, TKS_SENDER_PROPERTY, ORG_SENDER_PROPERTY, new Exception("Distribution Envelope Sender Address not set for ack response"));
            setConfiguratorOnSuccess(p, TKS_AUDIT_ID_PROPERTY, ORG_AUDIT_ID_PROPERTY, new Exception("Distribution Envelope Audit ID not set for ack response"));

            SPSetter.executeSettings(p, new String[][]{
                {BUSACKRESPONSETYPE, ORG_RESPONSETYPE},
                {BUSACKNACKERRORCODE, ORG_NACKERRORCODE},
                {BUSACKNACKERRORTEXT, ORG_NACKERRORTEXT},
                {BUSACKNACKDIAGTEXT, "uk.nhs.digital.mait.spinetools.spine.messaging.acknowledgements.nack.diagnostictext"},
                {SOAPFAULT, "uk.nhs.digital.mait.spinetools.spine.connection.soapfault"},
                {ASYNCHRONOUSRESPONSEDELAY, "uk.nhs.digital.mait.tkwx.spine.asynchronousebxmlreply.delay"},
                {SYNCRESPONSECOUNTDOWN, "uk.nhs.digital.mait.tkwx.spine.syncresponsecountdown"},
                {ASYNCRESPONSEDELAYCOUNTDOWN, "uk.nhs.digital.mait.tkwx.spine.asyncresponsedelaycountdown"},
                {INFACKRESPONSETYPE, ORG_NEGINFACK},
                {INFACKNACKERRORCODE, ORG_INFACKNACKERRORCODE},
                {INFACKNACKERRORTEXT, ORG_INFACKNACKERRORTEXT},
                {INFACKNACKDIAGTEXT, ORG_INFACKNACKDIAGTEXT},});

            new SPSetter(ITKTRUNK, st -> {
                itkTrunk = isY(st);
            }).execute(p);
        }
        //set the appropriate Handlers
        cm = ConnectionManager.getInstance();
        //add the ITK Trunk handlers
        if (itkTrunk) {
            ITKTrunkHandler ith = new ITKTrunkHandler();
            cm.addHandler(ITK_TRUNK_SERVICE, ith);
            cm.addHandler("\"" + ITK_TRUNK_SERVICE + "\"", ith);
            if (correspondenceHost) {
                ith.addHandler(SEND_CDA_V2_SERVICE, new ITKTrunkDistributionEnvelopeHandler());
            } else {
                ith.addHandler(BIZACKSERVICE, new BusAckDistributionEnvelopeHandler());
            }
            //For ITK3 ITK Trunk messages
            ith.addHandler(SEND_DIST_ENVELOPE_SERVICE, new SendDistEnvelopeHandler());
        }

        cm.listen(listenPort);

        System.out.println("SpineToolsTransport service ready listening on port " + listenPort);
        Logger.getInstance().log("SpineToolsTransport service ready listening on port " + listenPort);
    }

    /**
     * helper function uses a temporary attribute setValue to determine the
     * value of the property
     *
     * @param p
     * @param tksProperty
     * @param systemProperty
     * @param exToThrow
     * @throws Exception
     */
    private void setConfiguratorOnSuccess(Properties p, String tksProperty, String systemProperty, Exception exToThrow) throws Exception {
        new SPSetter(tksProperty, systemProperty, exToThrow, s -> {
            setValue = s;
        }).execute(p);
        // cant throw an exception from a lambda but if we get here its ok to set this
        Configurator.getConfigurator().setConfiguration(systemProperty, setValue);
    }

    /**
     *
     * @param param - ignored
     * @return service response object
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        return new ServiceResponse(0, SPINE_TOOLS_SIMULATOR);
    }
}
