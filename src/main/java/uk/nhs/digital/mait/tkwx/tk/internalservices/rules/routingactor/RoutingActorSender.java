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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import static java.util.logging.Level.WARNING;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFile.FHIR_NAMESPACE;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.SenderRequest;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 * Thread class for returning asynchronous ITK simulator responses ie ITK inf /
 * bus acks
 *
 * @author DAMU2
 */
public class RoutingActorSender
        extends Thread {

    public static final String ROUTINGACTORUSER = "tks.routingactor.username";

    private static final String WRAPPERTEMPLATE = "soap_response_wrapper_template.txt";
    private static String soapWrapperTemplate = null;

    private final static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    private String infrastructureBody = null;
    private String businessBody = null;
    private URL infrastructureTo = null;
    private URL businessTo = null;
    private int infrastructureDelay = 0;  // seconds after invocation
    private int businessDelay = 0; // seconds after inf ack sent
    private int ttl = 0; // time to live
    private String infackservice = INFACKSERVICE;
    private String bizackservice = BIZACKSERVICE;

    private ServiceResponse infrastructureResponse = null;
    private ServiceResponse businessResponse = null;

    /**
     * Public constructor
     *
     * @param inf String containing infrastructure ack body
     * @param app String containing business ack body
     * @param infto String containing URL to which infrastructure ack to be sent
     * @param appto String containing URL to which business ack to be sent
     * @param infdelay delay (in seconds) before sending infrastructure ack
     * @param appdelay delay (in seconds) before sending business ack
     * @throws Exception
     */
    public RoutingActorSender(String inf, String app, String infto, String appto, int infdelay, int appdelay)
            throws Exception {
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));

        synchronized (this) {
            if (soapWrapperTemplate == null) {
                soapWrapperTemplate = readFile2String(getClass().getResourceAsStream(WRAPPERTEMPLATE));
            }
        }
        ttl = Integer.parseInt(Configurator.getConfigurator().getConfiguration(TXTTL_PROPERTY));

        infrastructureBody = inf;
        businessBody = app;
        if (infto != null) {
            infrastructureTo = new URL(infto);
        }
        if (appto != null) {
            businessTo = new URL(appto);
        }
        infrastructureDelay = infdelay;
        businessDelay = appdelay;
    }

    String getInfrastructureTransmissionResult() {
        if (infrastructureResponse != null) {
            return infrastructureResponse.getResponse();
        }
        return null;
    }

    String getAppTransmissionResult() {
        if (businessResponse != null) {
            return businessResponse.getResponse();
        }
        return null;
    }

    /**
     * Thread override
     */
    @Override
    public void run() {
        this.setName("RoutingActorSenderThread");

        try {
            if (infrastructureTo != null) {
                if (infrastructureDelay != 0) {
                    synchronized (this) {
                        this.wait((long) (infrastructureDelay * 1000));
                    }
                }
                infrastructureResponse = send(infrastructureBody, infackservice, infrastructureTo);
            }
            if (businessTo != null) {
                if (businessDelay != 0) {
                    synchronized (this) {
                        this.wait((long) (businessDelay * 1000));
                    }
                }
                businessResponse = send(businessBody, bizackservice, businessTo);
            }
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, RoutingActorSender.class.getName(),
                    "Error sending responses:  " + e.getMessage());
        }
    }

    /**
     *
     * @param msg String containing the message payload
     * @param svc interaction id for service
     * @param to URL
     * @return ServiceResponse
     * @throws Exception
     */
    private ServiceResponse send(String msg, String svc, URL to)
            throws Exception {
        // FOR NOW: Use the SenderService. Make a SOAP wrapper and a SenderRequest.
        // FUTURE: Call the router.

        String w = null;
        // for any fhir soap wrapper not required
        // TODO this is a bit of a hack
        if (msg.contains(FHIR_NAMESPACE)) {
            w = "__PAYLOAD_BODY__";
        } else {
            w = makeSOAPWrapper(svc, to);
        }
        SenderRequest sr = new SenderRequest(msg, w, to.toString());
        // don't require a soap action in the header if its fhir messaging
        if (!msg.contains(FHIR_NAMESPACE)) {
            sr.setAction(svc);
        }

        ServiceManager sm = ServiceManager.getInstance();
        ToolkitService sender = sm.getService("Sender");
        ServiceResponse resp = sender.execute(sr);
        return resp;
    }

    /**
     * build a populated soap wrapper from the soap template
     *
     * @param service service name
     * @param to URL
     * @return wrapper string
     * @throws Exception
     */
    private String makeSOAPWrapper(String service, URL to)
            throws Exception {
        StringBuilder sb = new StringBuilder(soapWrapperTemplate);
        String msgId = randomUUID().toString().toUpperCase();
        substitute(sb, "__MESSAGEID__", msgId);
        substitute(sb, "__ACTION__", service);
        substitute(sb, "__TO_ADDRESS__", to.toExternalForm());
        String uname = Configurator.getConfigurator().getConfiguration(ROUTINGACTORUSER);
        substitute(sb, "__USER_NAME__", uname);
        Date now = new Date();
        String ts = ISO8601FORMATDATE.format(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, ttl);
        String exp = ISO8601FORMATDATE.format(cal.getTime());
        substitute(sb, "__TIMESTAMP__", ts);
        substitute(sb, "__EXPIRES__", exp);
        return sb.toString();
    }

    /**
     *
     * @param infackservice
     */
    public void setInfackservice(String infackservice) {
        this.infackservice = infackservice;
    }

    /**
     *
     * @param bizackservice
     */
    public void setBizackservice(String bizackservice) {
        this.bizackservice = bizackservice;
    }

}
