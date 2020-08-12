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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.UUID.randomUUID;
import static java.util.logging.Level.SEVERE;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Test.RegexpSubstitution;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.MessageContext;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.writeString2File;

/**
 * Class representing a "Message" from the test automation script. A "message"
 * specifies a template and a data source, and is responsible for instantiating
 * a populated, wrapped and ready-to-go message instance for transmission. As
 * such it can handle compression and base64 encoding of content as directed by
 * the script.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Message
        implements Linkable {

    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    private String name = null;

    private String templatename = null;
    private Template template = null;

    private boolean base64 = false;
    private boolean compress = false;
    private boolean soapwrap = false;
    private boolean envwrap = false;
    private String soapaction = null;
    private String auditIdentity = null;
    private String mimetype = null;

    private String messageId = null;
    private String trackingId = null;

    private String datasourcename = null;
    private DataSource datasource = null;

    private String recordid = null;
    private int iteration = 0;

    private static String distributionEnvelopeTemplate = null;
    private static String soapEnvelopeTemplate = null;
    private static String wsSecurityHeaderTemplate = null;

    private static int instantiationCount = 0;

    /**
     * antlr parser constructor
     *
     * @param messageCtx
     */
    public Message(MessageContext messageCtx) {
        loadResources();
        name = messageCtx.messageName().getText();
        for (AutotestParser.MessageArgContext messageArgCtx : messageCtx.messageArg()) {
            AutotestParser.MessageArgSingleContext messageArgSingleCtx = messageArgCtx.messageArgSingle();
            if (messageArgSingleCtx != null) {
                switch (messageArgSingleCtx.getText()) {
                    case "BASE64":
                        base64 = true;
                        break;
                    case "COMPRESS":
                        compress = true;
                        break;
                    case "SOAPWRAP":
                        soapwrap = true;
                        break;
                    case "DISTRIBUTIONENVELOPEWRAP":
                        envwrap = true;
                        break;
                }
                continue;
            }

            AutotestParser.UsingTemplateContext usingTemplateCtx = messageArgCtx.usingTemplate();
            if (usingTemplateCtx != null) {
                templatename = usingTemplateCtx.templateName().getText();
                continue;
            }

            AutotestParser.WithDatasourceContext withDataSourceCtx = messageArgCtx.withDatasource();
            if (withDataSourceCtx != null) {
                datasourcename = withDataSourceCtx.datasourceName().getText();
                continue;
            }

            AutotestParser.MessageArgPairContext messageArgPairCtx = messageArgCtx.messageArgPair();
            if (messageArgPairCtx != null) {
                switch (messageArgPairCtx.messageStringArg().getText()) {
                    case "SOAPACTION":
                        soapaction = messageArgPairCtx.messageString().getText();
                        break;
                    case "MIMETYPE":
                        mimetype = messageArgPairCtx.messageString().getText();
                        break;
                    case "AUDITIDENTITY":
                        auditIdentity = messageArgPairCtx.messageString().getText();
                        break;
                    case "ID":
                        recordid = messageArgPairCtx.messageString().getText();
                        break;
                }
                continue;
            }
        }
    }

    private String readResource(String rname) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((getClass().getResourceAsStream(rname))));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, Message.class.getName(), "Error reading messaging template: " + rname);
            return null;
        }
        return sb.toString();
    }

    private synchronized void loadResources() {
        if (distributionEnvelopeTemplate == null) {
            distributionEnvelopeTemplate = readResource("DistributionEnvelope.template");
        }
        if (soapEnvelopeTemplate == null) {
            soapEnvelopeTemplate = readResource("SOAPEnvelope.template");
        }
        if (wsSecurityHeaderTemplate == null) {
            wsSecurityHeaderTemplate = readResource("WSSecurityHeaderElement.template");
        }
    }

    /**
     * @return Message name from the script.
     */
    public String getName() {
        return name;
    }

    /**
     * Only available post-instantiation.
     *
     * @return
     */
    String getMessageId() {
        return messageId;
    }

    String getTrackingId() {
        return trackingId;
    }

    /**
     * Initialises and checks the integrity of the Message instance, including
     * references to template and datasource.
     *
     * @param p
     * @throws Exception In case of script errors.
     */
    @Override
    public void link(ScriptParser p)
            throws Exception {
        if (base64) {
            if (soapaction == null) {
                throw new Exception("Base64 requires that the SOAPACTION be specified");
            }
            if (!envwrap) {
                throw new Exception("Base64 requires that the message be distribution envelope wrapped");
            }
        }
        if (compress) {
            if (soapaction == null) {
                throw new Exception("Compression requires that the SOAPACTION be specified");
            }
            if (!envwrap) {
                throw new Exception("Compression requires that the message be distribution envelope wrapped");
            }
        }
        if (envwrap || soapwrap) {
            if (soapaction == null) {
                throw new Exception("SOAP and distribution envelope wrapping requires that the SOAPACTION be specified");
            }
        }
        // Get anything we need out of the parser (and do the same for the other classes)
        if (templatename == null) {
            throw new Exception("Message " + name + " doesn't specify a template");
        }
        template = p.getTemplate(templatename);
        if (template == null) {
            throw new Exception("Message " + name + " : template " + templatename + " not found");
        }
        if (datasourcename == null) {
            throw new Exception("Message " + name + " doesn't specify a data source");
        }
        if (!datasourcename.contentEquals("NULL")) {
            datasource = p.getDataSource(datasourcename);
            if (datasource == null) {
                throw new Exception("Message " + name + " : data source " + datasourcename + " not found");
            }
        }
    }

    /**
     * 
     * @param msg String containing message for transform
     * @param where transform point
     * @param xslTransforms HashMap of ArrayList of transform paths
     * @param reSubstitutions HashMap of ArrayList of Regular EXpression Transform objects
     * @return String containing the transformed message
     * @throws Exception 
     */
    private String doPreTransform(String msg, String where, HashMap<String, ArrayList<String>> xslTransforms, HashMap<String, ArrayList<RegexpSubstitution>> reSubstitutions)
            throws Exception {
        if (xslTransforms != null && !xslTransforms.isEmpty()) {
            ArrayList<String> a = xslTransforms.get(where);
            if (a != null) {
                TransformManager tm = TransformManager.getInstance();
                for (String t : a) {
                    msg = tm.doTransform(t, msg);
                }
            }
        }
        if (reSubstitutions != null && !reSubstitutions.isEmpty()) {
            ArrayList<RegexpSubstitution> a = reSubstitutions.get(where);
            if (a != null) {
                for (RegexpSubstitution subst : a) {
                    msg = subst.substitute(msg);
                }
            }
        }
        return msg;
    }

    /**
     * Creates a transmittable form of the message in a string.
     *
     * @param ts Timestamp (for log file naming)
     * @param to "To" address or URL
     * @param from "From" address (URL in SOAP usage)
     * @param replyto "ReplyTo" URL
     * @param xslTransforms
     * @param reSubstitutions
     * @param profileId
     * @return
     * @throws Exception
     */
    public String instantiate(String ts, String to, String from, String replyto, HashMap<String, ArrayList<String>> xslTransforms, HashMap<String, ArrayList<RegexpSubstitution>> reSubstitutions, String profileId)
            throws Exception {
        return instantiate(ts, to, from, replyto, xslTransforms, reSubstitutions, profileId, 0);
    }

    /**
     * Creates a transmittable form of the message in a string.
     *
     * @param ts Timestamp (for log file naming)
     * @param to "To" address or URL
     * @param from "From" address (URL in SOAP usage)
     * @param replyto "ReplyTo" URL
     * @param xslTransforms hashmap of arrays of transforms keyed on transform
     * point name
     * @param reSubstitutions
     * @param profileId
     * @param iterationp for a LOOP number of times we are through the iteration
     * @return
     * @throws Exception
     */
    public String instantiate(String ts, String to, String from, String replyto, HashMap<String, ArrayList<String>> xslTransforms, HashMap<String, ArrayList<RegexpSubstitution>> reSubstitutions, String profileId, int iterationp)
            throws Exception {
        this.iteration = iterationp;
        instantiationCount++;
        String id = null;
        trackingId = Template.resolveDataValue("__UCASE_UUID__");
        if (datasource != null) {
            if (recordid == null) {
                id = datasource.getNextId();
            } else {
                id = getRecordid();
            }
        }
        String msg = template.makeMessage(id, datasource);
        msg = doPreTransform(msg, "data", xslTransforms, reSubstitutions);
        if (base64) {
            msg = doPreTransform(msg, "prebase64", xslTransforms, reSubstitutions);
            msg = base64(msg.getBytes());
            msg = doPreTransform(msg, "postbase64", xslTransforms, reSubstitutions);
        }
        if (compress) {
            msg = doPreTransform(msg, "precompress", xslTransforms, reSubstitutions);
            msg = compress(msg);
            msg = doPreTransform(msg, "postcompress", xslTransforms, reSubstitutions);
        }
        // Note: If the Message does the DE wrapping, it will apply the
        // tracking id as part of the process, but as of 20121105 that wrapping
        // method doesn't know how to add addresses. So we need a pre-built
        // DE if we want addresses... and so that pre-built DE in the template
        // needs to have a __TRACKING_ID__ substitution tag supported so we
        // can apply it here.
        //
        if (envwrap) {
            msg = doPreTransform(msg, "predistributionenvelope", xslTransforms, reSubstitutions);
            msg = distributionEnvelopeWrap(msg, id, profileId);
            msg = doPreTransform(msg, "postdistributionenvelope", xslTransforms, reSubstitutions);
        } else {
            StringBuilder sb = new StringBuilder(msg);
            substitute(sb, "__TRACKING_ID__", trackingId);
            msg = sb.toString();
        }
        if (soapwrap) {
            msg = doPreTransform(msg, "presoap", xslTransforms, reSubstitutions);
            msg = soapWrap(msg, to, from, replyto);
            msg = doPreTransform(msg, "postsoap", xslTransforms, reSubstitutions);
        }

        msg = doPreTransform(msg, "final", xslTransforms, reSubstitutions);

        StringBuilder sb = new StringBuilder(name);
        sb.append("_");
        sb.append(instantiationCount);
//        sb.append((id == null) ? "NO_ID" : id);
        sb.append(".msg");
        String fn = sb.toString();
        File f = new File(ts, fn);
        writeString2File(f, msg);
        return fn;
    }

    /**
     * get the correct dataset recordid given the iteration number and the
     * starting id
     *
     * @return offset record id
     * @throws java.lang.Exception
     */
    private String getOffsetRecordId() throws Exception {
        String id = null;
        Iterable<String> rids = datasource.getRecordids();
        int itCount = 0;
        boolean startFound = false;
        for (String rid : rids) {
            if (rid.equals(recordid)) {
                startFound = true;
            }
            if (startFound) {
                itCount++;
                if (itCount >= iteration) {
                    id = rid;
                    break;
                }
            }
        }
        if (id == null) {
            throw new Exception("No offset record id found");
        }
        return id;
    }

    private String base64(byte[] m) {
        Base64 b64 = new Base64();
        byte[] b = b64.encode(m);
        return new String(b);
    }

    private String compress(String m) {
        Deflater d = new Deflater();
        d.setInput(m.getBytes());
        d.finish();
        // WARNING; DANGEROUS ASSUMPTION that the output is not going to be
        // larger than the input... If it is the byte array will blow and we
        // should catch the exception. If that happens, assume we're better off
        // not compressing, so turn "compress" off and just return what we were
        // given
        try {
            byte[] b = new byte[m.length()];
            int l = d.deflate(b);
            return base64(b);
        } catch (Exception e) {
            compress = false;
            return m;
        }
    }

    private String makeEndpointReference(String t, String a) {
        StringBuilder sb = new StringBuilder("<wsa:");
        sb.append(t);
        sb.append("><wsa:Address>");
        sb.append(a);
        sb.append("</wsa:Address></wsa:");
        sb.append(t);
        sb.append(">");
        return sb.toString();
    }

    private String soapWrap(String m, String to, String from, String replyto)
            throws Exception {
        StringBuilder sb = new StringBuilder(soapEnvelopeTemplate);
        messageId = Template.resolveDataValue("__UCASE_UUID__");
        substitute(sb, "__MESSAGE_ID__", messageId);
        substitute(sb, "__SOAP_ACTION__", soapaction);
        substitute(sb, "__TO_URL__", to);
        substitute(sb, "__FROM_URL__", (from != null) ? makeEndpointReference("From", from) : "");
        substitute(sb, "__REPLY_TO__", (replyto != null) ? makeEndpointReference("ReplyTo", replyto) : "");
        if (System.getProperty("tks.internal.autotest.dosigntureheader") != null) {
//            substitute(sb, "__SECURITY__", wsSecurityHeaderTemplate);
            substitute(sb, "__SECURITY__", makeSecurity());
        } else {
            substitute(sb, "__SECURITY__", "");
        }
        substitute(sb, "__SOAP_BODY__", m);
        return sb.toString();
    }

    private String makeSecurity()
            throws Exception {
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
        StringBuilder sb = new StringBuilder(wsSecurityHeaderTemplate);
        Date d = new Date();

        substitute(sb, "__TIMESTAMP__", ISO8601FORMATDATE.format(d));
        int ttl = 0;
        if (System.getProperty("tks.HttpTransport.default.asyncttl") == null) {
            ttl = 30;
        } else {
            ttl = Integer.parseInt(System.getProperty("tks.HttpTransport.default.asyncttl"));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.SECOND, ttl);
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        substitute(sb, "__EXPIRES__", exp);
        substitute(sb, "__SEC_ID__", "uuid_" + randomUUID().toString().toUpperCase());
        return sb.toString();
    }

    private String makeAddress(String id, String addrtag, String oidtag) {
        StringBuilder sb = new StringBuilder("<itk:address");
        try {
            if (datasource.hasValue(id, addrtag)) {
                sb.append(" uri=\"");
                sb.append(datasource.getValue(id, addrtag));
                sb.append("\"");
                if (datasource.hasValue(id, oidtag)) {
                    sb.append(" type=\"");
                    sb.append(datasource.getValue(id, oidtag));
                    sb.append("\"");
                }
            }
        } catch (Exception e) {
            return "";
        }
        sb.append("/>\n");
        return sb.toString();
    }

    private String makeHandlingSpecification(String id, String hstag, String hstypetag) {
        StringBuilder sb = new StringBuilder("<itk:spec");
        try {
            if (datasource.hasValue(id, hstag)) {
                sb.append(" value=\"");
                sb.append(datasource.getValue(id, hstag));
                sb.append("\"");
            }
            sb.append(" key=\"");
            sb.append(datasource.getValue(id, hstypetag));
            sb.append("\"");
        } catch (Exception e) {
            return "";
        }
        sb.append("/>\n");
        return sb.toString();
    }

    private String distributionEnvelopeWrap(String m, String id, String profileId)
            throws Exception {
        // If the "reserved" data source tagnames are present, build addressList,
        // senderAddress and handlingSpecifications elements. Use the argument "String id"
        // for the datasource id to get the "special" tagnames:
        // __RTR_ADDR_1__ __RTR_ADDR_OID_1__  First router address and oid (oid may be absent)
        // __RTR_ADDR_2__ __RTR_ADDR_OID_2__  Second
        // __RTR_ADDR_3__ __RTR_ADDR_OID_3__  Third
        // __SNDR_ADDR__ __SNDR_ADDR_OID__ Sender address and OID
        // __HND_SPEC_1__   __HND_SPEC_TYPE_1__ First handling specification value and OID
        // __HND_SPEC_2__   __HND_SPEC_TYPE_2__ Second handling specification value and OID
        // __HND_SPEC_3__   __HND_SPEC_TYPE_3__ Third handling specification value and OID

        StringBuilder sb = new StringBuilder(distributionEnvelopeTemplate);
        substitute(sb, "__SOAP_ACTION__", soapaction);
        substitute(sb, "__TRACKING_ID__", trackingId);
        if (profileId == null) {
            substitute(sb, "__PROFILEID__", "");
        } else {
            StringBuilder sbProfile = new StringBuilder(" profileid=\"");
            sbProfile.append(profileId);
            sbProfile.append("\"");
            substitute(sb, "__PROFILEID__", sbProfile.toString());
        }
        // Initial version: Address list and Sender address are always empty
        if (datasource.hasValue(id, "__RTR_ADDR_1__")) {
            StringBuilder sbaddr = new StringBuilder();
            sbaddr.append("<itk:addresslist>");
            sbaddr.append(makeAddress(id, "__RTR_ADDR_1__", "__RTR_ADDR_OID_1__"));
            sbaddr.append(makeAddress(id, "__RTR_ADDR_2__", "__RTR_ADDR_OID_2__"));
            sbaddr.append(makeAddress(id, "__RTR_ADDR_3__", "__RTR_ADDR_OID_3__"));
            sbaddr.append("</itk:addresslist>");
            substitute(sb, "__ADDRESS_LIST__", sbaddr.toString());
        } else {
            substitute(sb, "__ADDRESS_LIST__", "");
        }
        if (datasource.hasValue(id, "__SNDR_ADDR__")) {
            StringBuilder sndr = new StringBuilder("<itk:senderAddress uri=\"");
            sndr.append(datasource.getValue(id, "__SNDR_ADDR__"));
            sndr.append("\"");
            if (datasource.hasValue(id, "__SNDR_ADDR_OID__")) {
                sndr.append(" type=\"");
                sndr.append(datasource.getValue(id, "__SNDR_ADDR_OID__"));
                sndr.append("\"");
            }
            sndr.append("/>\n");
            substitute(sb, "__SENDER_ADDRESS__", sndr.toString());
        } else {
            substitute(sb, "__SENDER_ADDRESS__", "");
        }
        if (auditIdentity == null) {
            substitute(sb, "__OID__", "");
            substitute(sb, "__AUDIT_IDENTITY__", "");
        } else if (auditIdentity.contains("/")) {
            substitute(sb, "__OID__", auditIdentity.substring(0, auditIdentity.indexOf("/")));
            substitute(sb, "__AUDIT_IDENTITY__", auditIdentity.substring(auditIdentity.indexOf("/")));
        } else {
            substitute(sb, "__OID__", "");
            substitute(sb, "__AUDIT_IDENTITY__", auditIdentity);
        }

        StringBuilder handlingSpec = new StringBuilder("<itk:handlingSpecification>");
        boolean handlingSpecPopulated = false;
        for (int i = 1; i <= 3; i++) {
            if (datasource.hasValue(id, "__HND_SPEC_" + i + "__")) {
                if (!datasource.getValue(id, "__HND_SPEC_" + i + "__").isEmpty()) {
                    handlingSpec.append(makeHandlingSpecification(id, "__HND_SPEC_" + i + "__", "__HND_SPEC_TYPE_" + i + "__"));
                    handlingSpecPopulated = true;
                }
            }
        }
        handlingSpec.append("</itk:handlingSpecification>");

        if (handlingSpecPopulated) {
            substitute(sb, "__HANDLING_SPECIFICATION__", handlingSpec.toString());
        } else {
            substitute(sb, "__HANDLING_SPECIFICATION__", "");
        }
        String payloadId = Template.resolveDataValue("__UCASE_UUID__");
        substitute(sb, "__PAYLOAD_ID__", payloadId);
        if (mimetype == null) {
            substitute(sb, "__MIME_TYPE__", XML_MIMETYPE);
        } else {
            substitute(sb, "__MIME_TYPE__", mimetype);
        }
        substitute(sb, "__BASE64__", base64 ? "base64=\"true\"" : "");
        substitute(sb, "__COMPRESSED__", compress ? "compressed=\"true\"" : "");
        substitute(sb, "__PAYLOAD__", m);
        return sb.toString();
    }

    /**
     * @return the datasource
     */
    public DataSource getDatasource() {
        return datasource;
    }

    /**
     * @return the recordid
     * @throws java.lang.Exception
     */
    public String getRecordid() throws Exception {
        if (iteration <= 1) {
            return recordid;
        } else {
            return getOffsetRecordId();
        }
    }

    /**
     *
     * @return Name of the datasource
     */
    public String getDatasourceName() {
        return datasourcename;
    }
}
