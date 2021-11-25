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
 *
 * @author simonfarrow
 */
public class GeneralConstants {

    private GeneralConstants() {
    }

    public static final String TOOLKIT_SIMULATOR = "Toolkit simulator";

    public static final String DEFAULTHOST = "localhost";

    public static final String PROFILEID = "urn:nhs-en:profile:ITKBusinessAcknowledgement-v2-0";

    // ITK DE Handling specs
    public static final String INFACKREQUESTED_HANDLINGSPEC = "urn:nhs-itk:ns:201005:infackrequested";
    public static final String ACKREQUESTED_HANDLINGSPEC = "urn:nhs-itk:ns:201005:ackrequested";

    public static final String ITK_BUSINESS_ACK_INTERACTION = "urn:nhs-itk:interaction:ITKBusinessAcknowledgement-v1-0";

    // ITK Services
    public static final String ITK_TRUNK_SERVICE = "urn:nhs:names:services:itk/COPC_IN000001GB01";
    public final static String SEND_CDA_V2_SERVICE = "urn:nhs-itk:services:201005:SendCDADocument-v2-0";
    public static final String SEND_DIST_ENVELOPE_SERVICE = "urn:nhs-itk:services:201005:sendDistEnvelope";
    public static final String BIZACKSERVICE = "urn:nhs-itk:services:201005:SendBusinessAck-v1-0";
    public static final String INFACKSERVICE = "urn:nhs-itk:services:201005:SendInfrastructureAck-v1-0";
    public static final String SIMPLE_ITK_OK_RESPONSE = "<itk:SimpleMessageResponse xmlns:itk=\"urn:nhs-itk:ns:201005\">OK</itk:SimpleMessageResponse>";

    public static final String SSP_INTERACTION_ID_HEADER = "SSP-InteractionID";

    public static final String XML_MIMETYPE = "text/xml";

    // xpaths
    public static final String CDAEXTRACTORXPATH = "//hl7:ClinicalDocument";
    public static final String FHIR_SERVICE_LOCATION = "/fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:event/fhir:code/@value";

    // NB These are two different names spaces for wsa
    public static final String SOAP_HEADER_WSASPINE_ACTION = "/soap:Envelope/soap:Header/wsaspine:Action";
    public static final String SOAP_HEADER_WSA_ACTION = "/soap:Envelope/soap:Header/wsa:Action";
    public static final String SOAP_HEADER_MESSAGEID = "/SOAP:Envelope/SOAP:Header/wsa:MessageID";

    // standard http header names
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    public static final String CONTENT_TRANSFER_ENCODING_HEADER = "Content-Transfer-Encoding";
    public static final String TRANSFER_ENCODING_HEADER = "Transfer-Encoding";
    public static final String TRANSFER_ENCODING_CHUNKED = "chunked";


    // timestamp masks
    public static final String ISO8601FORMATDATEMASK = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String RFC822FORMATDATEMASK = "EEE, d MMM yyyy HH:mm:ss Z";
    public static final String HL7FORMATDATEMASK = "yyyyMMddHHmmss";
    // NB no trailing Z
    public static final String ISOFORMATDATEMASK_NOZ = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static final String FILEDATEMASK = "yyyyMMddHHmmssSSS";
    
    public static final String BIZNACKTEMPLATE = "business-nack-payload-template.txt";

    // default http port numbers
    public static final int DEFAULTTLSLISTENPORT = 443;
    public static final int DEFAULTLISTENPORT = 4848;

    // substitution tags
    public static final String TOADDRESS_TAG = "__TO_ADDRESS__";
    public static final String EXPIRES_TAG = "__EXPIRES__";
    public static final String RESPONSETO_TAG = "__RESPONSETO__";
    public static final String TO_TAG = "__TO__";
    public static final String PAYLOAD_TAG = "__PAYLOAD_BODY__";
    public static final String MESSAGEID_TAG = "__MESSAGEID__";
    public static final String ACTION_TAG = "__ACTION__";
    public static final String ORIGINALMESSAGEID_TAG = "__ORIGINAL_MESSAGEID__";
    public static final String FROMADDRESS_TAG = "__FROM_ADDRESS__";
    public static final String RCVASID_TAG = "__RCV_ASID__";
    public static final String SNDASID_TAG = "__SND_ASID__";
    public static final String OFFSETEXPIRES_TAG = "__OFFSETEXPIRES__";
    public static final String OFFSETTIMESTAMP_TAG = "__OFFSETTIMESTAMP__";
    public static final String TIMESTAMP_TAG = "__TIMESTAMP__";

    public static final String SUBSCRIBER_PREFIX = "urn:nhs-itk:subscriber:";

    // SOAP Constants
    public static final String HEADERSTRIPPER = "/soap:Envelope/soap:Body/*[1]";
    public static final String ANONYMOUS_FROM = "http://www.w3.org/2005/08/addressing/anonymous";
    public static final String ADDRESSING_NONE = "http://www.w3.org/2005/08/addressing/none";
    public static final String SOAP_ACTION_HEADER = "SOAPAction";


    public static final String ITK_TRUNK_INTERACTION = "urn:nhs:names:services:itk:COPC_IN000001GB01";

    
    public static final String ACK_TEMPLATE_PATH = "infrastructure_ack_template.xml.txt";
    public static final String NACK_TEMPLATE_PATH = "infrastructure_nack_template.xml.txt";
    
    // FHIR
    // this is the DSTU2 order 
    public static final String FHIR_JSON_MIMETYPE_DSTU2 = "application/json+fhir;charset=UTF-8";
    public static final String FHIR_XML_MIMETYPE_DSTU2 = "application/xml+fhir;charset=UTF-8";
    // this is the STU3 order: application/fhir+json
    public static final String FHIR_JSON_MIMETYPE_STU3 = "application/fhir+json;charset=UTF-8";
    public static final String FHIR_XML_MIMETYPE_STU3 = "application/fhir+xml;charset=UTF-8";
}
