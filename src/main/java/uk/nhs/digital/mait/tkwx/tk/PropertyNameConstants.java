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
package uk.nhs.digital.mait.tkwx.tk;

/**
 * TKW Property names
 *
 * @author simonfarrow
 */
public class PropertyNameConstants {

    private PropertyNameConstants() {
    }

    // tks property names
    public static final String ASYNCACKTEMPLATE_PROPERTY = "tks.soap.async.ack.template";
    public static final String ASYNCWRAPPER_PROPERTY = "tks.asynchronousreply.wrapper";
    public static final String CLIENT_TLSMA_PROPERTY = "tks.tls.clientmutualauthentication";
    public static final String SERVER_TLSMA_PROPERTY = "tks.tls.servermutualauthentication";
    public static final String ASYNCRESPONSEDELAY_PROPERTY = "tks.asynchreply.delay";
    public static final String TIMESTAMP_OFFSET_PROPERTY = "tks.asynchreply.timestampoffset";
    public static final String SENDTLSMA_PROPERTY = "tks.system.internal.clientmutualauthentication";

    public static final String KEYSTORE_PROPERTY = "tks.tls.keystore";
    public static final String KEYPASS_PROPERTY = "tks.tls.keystorepassword";
    public static final String TRUSTSTORE_PROPERTY = "tks.tls.truststore";
    public static final String TRUSTPASS_PROPERTY = "tks.tls.trustpassword";

    public static final String TXTTL_PROPERTY = "tks.transmitter.timetolive";

    public static final String SAVEDMESSAGES_PROPERTY = "tks.savedmessages";

    public static final String RECEIVE_USETLS_PROPERTY = "tks.receivetls";
    public static final String SEND_USETLS_PROPERTY = "tks.sendtls";

    public static final String INHIBITVALIDATION_PROPERTY = "tks.httpinterceptor.inhibit.validation";

    public static final String SYNCHRONOUSRESPONSEDELAY_PROPERTY = "tks.synchronousreply.delay";

    public static final String LOGFILELOCKER_PROPERTY = "tks.Toolkit.logfilelocker";

    public static final String TRANSMITDIR_PROPERTY = "tks.transmitter.source";
    public static final String TXNOSEND_PROPERTY = "tks.transmitter.nosend";
    public static final String ADDRESS_PROPERTY = "tks.transmitter.send.url";
    public static final String CHUNKXMIT_PROPERTY = "tks.transmitter.send.chunksize";

    public static final String TLSMAFILTER_PROPERTY = "tks.tls.ma.filterclientsubjectdn";
    public static final String SSBACKLOG_PROPERTY = "tks.HttpTransport.serversocketbacklog";

    public static final String VALIDATOR_CONFIG_PROPERTY = "tks.validator.config";
    public static final String VALIDATOR_SOURCE_PROPERTY = "tks.validator.source";
    public static final String VALIDATOR_REPORT_PROPERTY = "tks.validator.reports";

    public static final String RULEFILE_PROPERTY = "tks.rules.configuration.file";

    public static final String TRANSPORTLIST_PROPERTY = "tks.transportlist";
    public static final String REPLYTO_PROPERTY = "tks.transmitter.replyto.url";
    public static final String FAULTTO_PROPERTY = "tks.transmitter.faultto.url";

    public static final String SERVICELISTPROPERTY = "tks.servicenames";
    public static final String TRANSMITTERMODE_PROPERTY = "tks.transmitter.mode";

    public static final String STOP_ON_FAIL_PROPERTY = "tks.autotest.stoponfail";
    public static final String RUNROOT_PROPERTY = "tks.autotest.root";

    public static final String TRANSMITLOG_PROPERTY = "tks.sender.destination";
    public static final String FAULTPAYLOAD_PROPERTY = "tks.synchronousreply.fault";
    public static final String SYNCWRAPPER_PROPERTY = "tks.synchronousreply.wrapper";

    public static final String NOORIGINATE_PROPERTY = "tks.system.internal.donotoriginate";

    public static final String FHIR_VERSION_PROPERTY = "tks.fhir.version";

    public static final String DONTSIGNLOGS_PROPERTY = "tks.skipsignlogs";
    
    public static final String FORWARDINGADDRESSPROPERTY = "tks.httpinterceptor.forwardingaddress";
    public static final String FORWARDINGPORTPROPERTY = "tks.httpinterceptor.forwardingport";

    public static final String SPINESOAPSYNCREQUEST = "tks.HttpTransport.SpineSyncSoapRequest";
    public static final String SPINESOAPASYNCREQUEST = "tks.HttpTransport.SpineAsyncSoapRequest";

    public static final String GENERATE_EVIDENCE_METADATA_PROPERTY = "tks.evidencemetadata.generate";
    public static final String EVIDENCE_METADATA_LOCATION_PROPERTY = "tks.evidencemetadata.location";
    public static final String EVIDENCE_METADATA_URLBASE_PROPERTY = "tks.evidencemetadata.urlbase";
    public static final String EVIDENCE_METADATA_ENVRONMENT_PROPERTY = "tks.evidencemetadata.environment";
    
    // Org property names
    public static final String ORG_SSL_SOCKET_LISTENER_SAVED_MESSAGES_FOLDER_PROPERTY = "uk.nhs.digital.mait.tkwx.tlsma.failure.log.folder";
    public static final String ORG_SSLPASS_PROPERTY = "uk.nhs.digital.mait.tkwx.http.sslcontextpass";
    /**
     * Property name only required if the platform default algorithm is not an
     * X509 implementation.
     */
    public static final String ORG_SSLALGORITHM_PROPERTY = "uk.nhs.digital.mait.tkwx.http.sslalgorithm";
    public static final String ORG_DOMUTUALAUTH_PROPERTY = "uk.nhs.digital.mait.tkwx.http.servermutualauthentication";
    public static final String ORG_USESSLCONTEXT_PROPERTY = "uk.nhs.digital.mait.tkwx.http.usesslcontext";
    public static final String ORG_MUTUALAUTHFILTER_PROPERTY = "uk.nhs.digital.mait.tkwx.http.filterclientsubjectdn";

    public static final String ORG_AUDIT_ID_PROPERTY = "uk.nhs.digital.mait.distributionenvelopetools.itk.router.auditidentity";
    public static final String ORG_SENDER_PROPERTY = "uk.nhs.digital.mait.distributionenvelopetools.itk.router.senderaddress";

    public static final String ORG_SSBACKLOG_PROPERTY = "uk.nhs.digital.mait.tkwx.http.serversocketbacklog";

    public static final String SPINE_MESSAGING_AUTHOR_ROLE = "uk.nhs.digital.mait.spinetools.spine.messaging.authorrole";
    public static final String SPINE_MESSAGING_AUTHOR_UID = "uk.nhs.digital.mait.spinetools.spine.messaging.authoruid";
    public static final String SPINE_MESSAGING_AUTHOR_URP = "uk.nhs.digital.mait.spinetools.spine.messaging.authorurp";

    public static final String ORG_INFACKNACKERRORCODE = "uk.nhs.digital.mait.tkwx.spine.infack.nack.errorcode";
    public static final String ORG_INFACKNACKERRORTEXT = "uk.nhs.digital.mait.tkwx.spine.infack.nack.errortext";
    public static final String ORG_INFACKNACKDIAGTEXT = "uk.nhs.digital.mait.tkwx.spine.infack.nack.diagnostictext";
    public static final String ORG_NEGEBXMLACK = "uk.nhs.digital.mait.spinetools.spine.connection.negativeebxmloverride";

    // TODO Please note that this constant is poorly named 
    // the code works it just doesn't read like it should baecause the test is whether its set to n or not
    public static final String ORG_NEGINFACK = "uk.nhs.digital.mait.tkwx.spine.infack.positiveresponsetype";

    public static final String ORG_RESPONSETYPE = "uk.nhs.digital.mait.spinetools.spine.messaging.acknowledgements.positiveresponsetype";
    public static final String ORG_NACKERRORCODE = "uk.nhs.digital.mait.spinetools.spine.messaging.acknowledgements.nack.errorcode";
    public static final String ORG_NACKERRORTEXT = "uk.nhs.digital.mait.spinetools.spine.messaging.acknowledgements.nack.errortext";
    public static final String ORG_NACKDIAGTEXT = "uk.nhs.digital.mait.spinetools.spine.messaging.acknowledgements.nack.diagnostictext";
    public static final String ORG_CONFIGURATOR = "uk.nhs.digital.mait.commonutils.util.configurator.class";
    public static final String ORG_RESETTABLE_PROPERTIES_CONFIGURATOR = "uk.nhs.digital.mait.commonutils.util.configurator.ResettablePropertiesConfigurator";
    public static final String ORG_DEFAULT_SYSTEM_PROPERTIES_CONFIGURATOR = "uk.nhs.digital.mait.commonutils.util.configurator.DefaultSystemPropertiesConfigurator";
    
    
    /**
     * System property. Holds the path where received ITK Distribution Envelopes
     * are written. Note that if this property is not set, the system "user.dir"
     * property is used.
     */
    public static final String ORG_SAVE_DIRECTORY = "uk.nhs.digital.mait.spinetools.spine.messaging.defaultdistributionenvelopehandler.filesavedirectory";

    /**
     * System property. Set to something beginning with "y" or "Y" to cause the
     * fully-qualified path name of the file, to be written to System.out.
     */
    public static final String ORG_REPORT_FILENAME = "uk.nhs.digital.mait.spinetools.spine.messaging.defaultdistributionenvelopehandler.reportfilename";

    public static final String DEBUG_DISPLAY_DIGEST_PROPERTY = "tks.debug.displaydigestvalues";
    
    // Currently this only applies to the autotest extractor SingleRecordXpathResponseExtractor class
    // where a supplier was returning a urlencoded Location header
    public static final String URL_DECODE_HTTP_HEADERS_PROPERTY = "tks.url_decode.httpheaders";
    
    public static final String FHIR_SERVICE_LOCATION_PROPERTY = "tks.fhir.service.location";

}
