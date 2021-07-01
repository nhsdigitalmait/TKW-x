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

import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import org.w3c.dom.Node;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.io.IOException;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.substituteHandleNull;

/**
 * Class to handle the work of the SpineSynchronousSoapRequestHandler in a
 * thread-safe way.
 *
 * @author DAMU2
 */
class SpineSynchronousWorker {

    protected SpineSynchronousSoapRequestHandler handler = null;
    protected static Configurator config;
    protected static boolean inhibitValidation = true;
    protected static boolean scenarioInstantiationTrigger = false;

    private final static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    protected String soapaction = null;

    protected Node soapRequest = null;
    protected SpineMessage sm = null;

    protected String messageId = null;
    protected String replyTo = null;
    protected String faultTo = null;
    protected String to = null;
    protected String toAddress = null;
    protected String fromAddress = null;
    protected String rcvAsid = null;
    protected String sndAsid = null;

    protected long synchronousResponseDelay = 0;
    protected String subDir = null;
    protected String validationReport = null;
    ServiceResponse serviceResponse = new ServiceResponse();

    static {
        try {
            config = Configurator.getConfigurator();
            inhibitValidation = isY(config.getConfiguration(INHIBITVALIDATION_PROPERTY));
            scenarioInstantiationTrigger = isY(config.getConfiguration(SCENARIO_INSTANTIATION_TRIGGER_PROPERTY));
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, SpineSynchronousWorker.class.getName(), "Configurator error - " + e.toString());
        }
    }

    SpineSynchronousWorker(SpineSynchronousSoapRequestHandler ssrh) {
        handler = ssrh;
        String srd = System.getProperty(SYNCHRONOUSRESPONSEDELAY_PROPERTY);
        if (srd != null) {
            try {
                synchronousResponseDelay = Long.parseLong(srd) * 1000;
            } catch (NumberFormatException e) {
                Logger.getInstance().log(WARNING, SpineSynchronousWorker.class.getName(), "Error setting synchronous response delay: " + srd);
            }
        }
    }

    /**
     *
     * @param pathIgnored
     * @param paramsIgnored
     * @param req
     * @param resp
     * @throws HttpException
     */
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
                    Logger.getInstance().log(WARNING, SpineSynchronousWorker.class.getName(), "SynchronousResponseDelay sleep() interrupted.");
                }
            }
            ToolkitService svc = ServiceManager.getInstance().getService("RulesEngine");
            serviceResponse = svc.execute(new String[]{soapaction, sm.getHL7Part()});
            if (serviceResponse != null) {
                if (serviceResponse.getCode() == 0) {
                    throw new Exception("No rules or process defined for action: " + soapaction);
                }
                if (serviceResponse.getCode() == -1) {
                    String m = makeSoapFault("soap:Client", "The service/interaction is not supported for the requested URI", "urn:nhs:names:errors:tms", "101", "Error", "HTTP Header - SOAPAction", "The message is not well formed");
                    synchronousResponse(500, "Internal server error", m, "http://www.w3.org/2005/08/addressing/soap/fault");
                    return serviceResponse;
                }
                String hresp = null;
                if (serviceResponse.getCode() < 300) {
                    hresp = "OK";
                } else {
                    hresp = "Internal server error";
                }
                synchronousResponse(serviceResponse.getCode(), hresp, serviceResponse.getResponse(), serviceResponse.getAction());
                return serviceResponse;
            } else {
                //Removed req.setHandled(true) and assume that this will be don in the process part of httpinterceptor
//                    req.setHandled(true);
                return serviceResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                synchronousResponse(500, "Internal server error", "Error reading message: " + e.getMessage(), "http://www.w3.org/2005/08/addressing/soap/fault");
                return serviceResponse;
            } catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }

        }
    }

    protected String makeSoapFault(String faultCode, String faultString, String codeContext, String errorCode, String severity, String location, String description)
            throws Exception {
        StringBuilder sb = new StringBuilder(handler.getSoapFault());
        substituteHandleNull(sb, "__SOAP_ERROR_FAULTCODE_REQUIRED__", faultCode);
        substituteHandleNull(sb, "__SOAP_ERROR_FAULTSTRING_REQUIRED__", faultString);
        substituteHandleNull(sb, "__SOAP_ERROR_CODECONTEXT__", codeContext);
        substituteHandleNull(sb, "__SOAP_ERROR_ERRORCODE__", errorCode);
        substituteHandleNull(sb, "__SOAP_ERROR_SEVERITY__", severity);
        substituteHandleNull(sb, "__SOAP_ERROR_LOCATION__", location);
        substituteHandleNull(sb, "__SOAP_ERROR_DESCRIPTION__", description);
        return sb.toString();
    }

    protected boolean doChecks(HttpRequest req)
            throws HttpException {
        try {
            readMessage(req);
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

    protected StringBuilder makeWrapper(String action)
            throws Exception {
        StringBuilder wrapped = new StringBuilder(handler.getSynchronousWrapper());
        substituteHandleNull(wrapped, MESSAGEID_TAG, randomUUID().toString().toUpperCase());
        substituteHandleNull(wrapped, ORIGINALMESSAGEID_TAG, messageId);
        if (action != null) {
            substituteHandleNull(wrapped, ACTION_TAG, action);
        } else {
            substituteHandleNull(wrapped, ACTION_TAG, soapaction + "Response");
        }
        substituteHandleNull(wrapped, TOADDRESS_TAG, toAddress);
        substituteHandleNull(wrapped, FROMADDRESS_TAG, fromAddress);
        substituteHandleNull(wrapped, RCVASID_TAG, rcvAsid);
        substituteHandleNull(wrapped, SNDASID_TAG, sndAsid);

        return wrapped;
    }

    protected void synchronousResponse(int i, String s, String m, String action)
            throws Exception {
        HttpHeaderManager hhm = serviceResponse.getHttpHeaders();
//        if (m.trim().length() != 0) {
//            //resp.setContentType(XML_MIMETYPE);
//            hhm.addHttpHeader(CONTENT_TYPE_HEADER, XML_MIMETYPE);
//        }
        if (action != null) {
//            resp.setField(SOAP_ACTION_HEADER, action);
            hhm.addHttpHeader(SOAP_ACTION_HEADER, action);
        }

        if (i == -1) {
//            resp.setStatus(200, "OK");
            serviceResponse.setCode(200);
            serviceResponse.setCodePhrase("OK");
            serviceResponse.setResponse(m);
//            req.setHandled(true);
            return;
        }
        if (i == 202) {
//            resp.setStatus(i, s);
            serviceResponse.setCode(i);
            serviceResponse.setCodePhrase(s);
            serviceResponse.setResponse(m);
//            req.setHandled(true);
            return;
        }
        String tosend = null;
        if (i != 500) {
            StringBuilder wrapped = makeWrapper(action);
            Date now = new Date();
            ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
            String ts = ISO8601FORMATDATE.format(now);
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.SECOND, handler.getAsyncTTL());
            Date expires = cal.getTime();
            String exp = ISO8601FORMATDATE.format(expires);
            substituteHandleNull(wrapped, PAYLOAD_TAG, m);
            tosend = wrapped.toString();
        } else {
            tosend = m;
        }
//        resp.setStatus(i, s);
        serviceResponse.setCode(i);
        serviceResponse.setCodePhrase(s);
        serviceResponse.setResponse(tosend);
//        req.setHandled(true);

    }

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
        resolveMessageId();

        resolveToAddress();
        resolveFromAddress();
        resolveRcvAsid();
    }

    private void resolveMessageId()
            throws Exception {
        try {
            messageId = handler.extractMessageId(sm.getHL7Part());
        } catch (Exception e) {
            throw new Exception("Error reading message id: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveToAddress()
            throws Exception {
        try {
            toAddress = handler.extractToAddress(sm.getHL7Part());
        } catch (Exception e) {
            throw new Exception("Error reading From Address: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveFromAddress()
            throws Exception {
        try {
            fromAddress = handler.extractFromAddress(sm.getHL7Part());
        } catch (Exception e) {
            throw new Exception("Error reading To Address: Message is not well formed : " + e.getMessage());
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
