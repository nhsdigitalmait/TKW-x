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

import java.io.IOException;
import java.io.StringReader;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.xml.sax.InputSource;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SpineSynchronousSoapRequestHandler
        extends AbstractSoapRequestHandler {

    private static final String SYNCWRAPPER = "tks.spine.webservice.headertemplate";
    private static final String FAULTPAYLOAD = "tks.spine.soapfault";

    public static final String SOAPHEADER = "/SOAP:Envelope/SOAP:Header";
    public static final String SOAPBODY = "/SOAP:Envelope/SOAP:Body";
    private static final String MSGID = "/SOAP:Envelope/SOAP:Header/wsaspine:MessageID";
    protected static final String REPLY = "/SOAP:Envelope/SOAP:Header/wsaspine:ReplyTo/wsaspine:Address";
    protected static final String FAULT = "/SOAP:Envelope/SOAP:Header/wsaspine:FaultTo/wsaspine:Address";
    protected static final String TOADDR = "/SOAP:Envelope/SOAP:Header/wsaspine:From/wsaspine:Address";
    protected static final String FROMADDR = "/SOAP:Envelope/SOAP:Header/wsaspine:To";
    private static final String RCVASID = "/SOAP:Envelope/SOAP:Header/hl7:communicationFunctionSnd/hl7:device/hl7:id/@extension";
    private static final String SNDASID = "/SOAP:Envelope/SOAP:Header/hl7:communicationFunctionRcv/hl7:device/hl7:id/@extension";

    protected XPathExpression getToAddress = null;
    protected XPathExpression getFromAddress = null;
    protected XPathExpression getRcvAsid = null;
    protected XPathExpression getSndAsid = null;

    /**
     * public constructor
     *
     * @throws Exception
     */
    public SpineSynchronousSoapRequestHandler()
            throws Exception {

        setXpathExpressions();
        Configurator config = Configurator.getConfigurator();
        synchronousWrapper = Utils.readFile2String(config.getConfiguration(SYNCWRAPPER));
        soapfault = Utils.readFile2String(config.getConfiguration(FAULTPAYLOAD));
        savedMessagesDirectory = config.getConfiguration(SAVEDMESSAGES_PROPERTY);
        if (config.getConfiguration(SYNCHRONOUSRESPONSEDELAY_PROPERTY) != null) {
            System.setProperty(SYNCHRONOUSRESPONSEDELAY_PROPERTY, config.getConfiguration(SYNCHRONOUSRESPONSEDELAY_PROPERTY));
        }
    }

    private void setXpathExpressions() throws XPathExpressionException, XPathFactoryConfigurationException {
        getMessageID = getXpathExtractor(MSGID);
        getReplyTo = getXpathExtractor(REPLY);
        getFaultTo = getXpathExtractor(FAULT);
        getHeader = getXpathExtractor(SOAPHEADER);
        getBody = getXpathExtractor(SOAPBODY);
        getSoapRequest = getXpathExtractor(SOAPREQUEST);
        getToAddress = getXpathExtractor(TOADDR);
        getFromAddress = getXpathExtractor(FROMADDR);
        getRcvAsid = getXpathExtractor(RCVASID);
        getSndAsid = getXpathExtractor(SNDASID);
    }

    String extractToAddress(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getToAddress, s);
    }

    String extractFromAddress(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getFromAddress, s);
    }

    String extractRcvAsid(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getRcvAsid, s);
    }

    String extractSndAsid(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getSndAsid, s);
    }

    @Override // nb this is lexically but not sematically identical to its sibling
    public void setToolkit(HttpTransport t)
            throws Exception {
//        super.setToolkit(t);
//        Configurator config = Configurator.getConfigurator();
//        synchronousWrapper = Utils.readFile2String(config.getConfiguration(SYNCWRAPPER));
//        soapfault = Utils.readFile2String(config.getConfiguration(FAULTPAYLOAD));
//        savedMessagesDirectory = config.getConfiguration(SAVEDMESSAGES_PROPERTY);
//        if (config.getConfiguration(SYNCHRONOUSRESPONSEDELAY_PROPERTY) != null) {
//            System.setProperty(SYNCHRONOUSRESPONSEDELAY_PROPERTY, config.getConfiguration(SYNCHRONOUSRESPONSEDELAY_PROPERTY));
//        }
    }

    @Override
    public void handle(String pathIgnored, String paramsIgnored, HttpRequest req, HttpResponse resp)
            throws HttpException {

    }

    public ServiceResponse invoke(HttpRequest req) throws HttpException {
        SpineSynchronousWorker s = new SpineSynchronousWorker(this);
        try {
            return s.handle(req);
        } catch (InterruptedException | IOException ex) {
            uk.nhs.digital.mait.commonutils.util.Logger.getInstance().log(SEVERE, SpineSynchronousSoapRequestHandler.class.getName(), null);
        }
        return null;
    }

}
