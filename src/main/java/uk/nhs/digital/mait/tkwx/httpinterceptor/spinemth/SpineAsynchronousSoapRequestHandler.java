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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Handler implementation for asynchronous ITK SOAP requests. TKW wants a single
 * class as the handler for a request, but this request will have been put in
 * its own thread by the HTTP server. So for thread safety this class' handle()
 * method creates a separate AsynchronousWorker instance to handle this
 * particular request, and calls handle() on that.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class SpineAsynchronousSoapRequestHandler
        extends SpineSynchronousSoapRequestHandler {
    
    private static final String RELIABILITYMAP = "tks.soap.async.ack.reliabilitymap";
    private static String syncAckTemplate = null;
    private static String asyncWrapper = null;

    // differ from sync versions
    private static final String MSGID = "/SOAP:Envelope/SOAP:Header/eb:MessageHeader/eb:MessageData/eb:MessageId";
    private static final String RCVASID = "/*[1]/hl7:communicationFunctionRcv/hl7:device/hl7:id/@extension";
    private static final String SNDASID = "/*[1]/hl7:communicationFunctionSnd/hl7:device/hl7:id/@extension";

    // async specific
    private static final String FRMPARTYID = "/SOAP:Envelope/SOAP:Header/eb:MessageHeader/eb:From/eb:PartyId";
    private static final String TOPARTYID = "/SOAP:Envelope/SOAP:Header/eb:MessageHeader/eb:To/eb:PartyId";
    private static final String CONVERSATIONID = "/SOAP:Envelope/SOAP:Header/eb:MessageHeader/eb:ConversationId";
    private static final String CPAID = "/SOAP:Envelope/SOAP:Header/eb:MessageHeader/eb:CPAId";

    // async specific
    protected XPathExpression getFromPartyID = null;
    protected XPathExpression getToPartyID = null;
    protected XPathExpression getConversationID = null;
    protected XPathExpression getCPAID = null;
    
    protected HashMap<String, String> reliabilityMap = null;
    
    private Exception ackLoadException = null;
    private SpineAsynchronousWorker asynchronousWorker = null;
    protected boolean hasAsyncResponse = false;
    
    public SpineAsynchronousSoapRequestHandler()
            throws Exception {
        super();
        setXpathExpressions();
        Configurator config = Configurator.getConfigurator();
        synchronized (this) {
            try {
                String ackFile = config.getConfiguration(ASYNCACKTEMPLATE_PROPERTY);
                if ((ackFile != null) && (!ackFile.toUpperCase().contentEquals("NONE"))) {
                    syncAckTemplate = Utils.readFile2String(ackFile);
                }
                asyncWrapper = Utils.readFile2String(config.getConfiguration(ASYNCWRAPPER_PROPERTY));
            } catch (Exception e) {
                ackLoadException = e;
                throw e;
            }
            if (config.getConfiguration(ASYNCRESPONSEDELAY_PROPERTY) != null) {
                System.setProperty(ASYNCRESPONSEDELAY_PROPERTY, config.getConfiguration(ASYNCRESPONSEDELAY_PROPERTY));
            }
            String offset = config.getConfiguration(TIMESTAMP_OFFSET_PROPERTY);
            if ((offset != null) && (offset.trim().length() > 0)) {
                try {
                    offsetSeconds = Integer.parseInt(offset);
                } catch (Exception e) {
                    Logger.getInstance().log(WARNING, SpineAsynchronousSoapRequestHandler.class.getName(), "timestamp offset parse error, should be integer seconds, setting to 0: " + offset);
                }
            }
            
            try {
                String reliabilityFile = config.getConfiguration(RELIABILITYMAP);
                if ((reliabilityFile != null) && (!reliabilityFile.toUpperCase().contentEquals("NONE"))) {
                    loadReliabilityMap(reliabilityFile);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    private void setXpathExpressions() throws XPathFactoryConfigurationException, XPathExpressionException {
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

        // async specific
        getFromPartyID = getXpathExtractor(FRMPARTYID);
        getToPartyID = getXpathExtractor(TOPARTYID);
        getConversationID = getXpathExtractor(CONVERSATIONID);
        getCPAID = getXpathExtractor(CPAID);
    }

    // package protection level methods
    String extractFromPartyID(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getFromPartyID, s);
    }
    
    String extractToPartyID(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getToPartyID, s);
    }
    
    String extractConversationID(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getConversationID, s);
    }
    
    String extractCPAID(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getCPAID, s);
    }
    
    @Override
    protected String extractMessageId(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getMessageID, s);
    }
    
    @Override
    String extractRcvAsid(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getRcvAsid, s);
    }
    
    @Override
    String extractSndAsid(String m)
            throws Exception {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getSndAsid, s);
    }
    
    private void loadReliabilityMap(String fName)
            throws Exception {
        try {
            HashMap<String, String> h = new HashMap<>();
            String line = null;
            FileReader fr = new FileReader(fName);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                int delimiter = line.indexOf(':');
                if (delimiter != -1) {
                    h.put(line.substring(0, delimiter), line.substring(delimiter + 1));
                }
            }
            fr.close();
            reliabilityMap = h;
        } catch (Exception e) {
            throw new Exception("Failed to read interaction map: " + fName + ": " + e.getMessage());
        }
    }
    
    @Override
    public void setToolkit(HttpTransport t)
            throws Exception {
        super.setToolkit(t);
        Configurator config = Configurator.getConfigurator();
        synchronized (this) {
            try {
                String ackFile = config.getConfiguration(ASYNCACKTEMPLATE_PROPERTY);
                if ((ackFile != null) && (!ackFile.toUpperCase().contentEquals("NONE"))) {
                    syncAckTemplate = Utils.readFile2String(ackFile);
                }
                asyncWrapper = Utils.readFile2String(config.getConfiguration(ASYNCWRAPPER_PROPERTY));
            } catch (Exception e) {
                ackLoadException = e;
                throw e;
            }
            if (config.getConfiguration(ASYNCRESPONSEDELAY_PROPERTY) != null) {
                System.setProperty(ASYNCRESPONSEDELAY_PROPERTY, config.getConfiguration(ASYNCRESPONSEDELAY_PROPERTY));
            }
            String offset = config.getConfiguration(TIMESTAMP_OFFSET_PROPERTY);
            if ((offset != null) && (offset.trim().length() > 0)) {
                try {
                    offsetSeconds = Integer.parseInt(offset);
                } catch (Exception e) {
                    Logger.getInstance().log(WARNING, SpineAsynchronousSoapRequestHandler.class.getName(), "timestamp offset parse error, should be integer seconds, setting to 0: " + offset);
                }
            }
            
            try {
                String reliabilityFile = config.getConfiguration(RELIABILITYMAP);
                if ((reliabilityFile != null) && (!reliabilityFile.toUpperCase().contentEquals("NONE"))) {
                    loadReliabilityMap(reliabilityFile);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    @Override
    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
            throws HttpException {
//        SpineAsynchronousWorker a = new SpineAsynchronousWorker(this);
//        try {
//            a.handle(req, resp);
//        } catch (InterruptedException | IOException ex) {
//            Logger.getInstance().log(SEVERE, SpineSynchronousSoapRequestHandler.class.getName(), null);
//        }
    }
    
    @Override
    public ServiceResponse invoke(HttpRequest req)
            throws HttpException {
        asynchronousWorker = new SpineAsynchronousWorker(this);
        try {
            return asynchronousWorker.handle(req);
        } catch (InterruptedException | IOException ex) {
            Logger.getInstance().log(SEVERE, SpineAsynchronousSoapRequestHandler.class.getName(), null);
        }
        return null;
    }
    
    public void asynchronousResponse(ServiceResponse ruleresponse, EvidenceMetaDataHandler evidenceMetaDataHandler) throws Exception{
        asynchronousWorker.asynchronousResponse(ruleresponse, evidenceMetaDataHandler);
    }
     
    public boolean hasAsyncResponse(){
        return hasAsyncResponse;
    }
    Exception getAckLoadException() {
        return ackLoadException;
    }
    
    String getSyncAckTemplate() {
        return syncAckTemplate;
    }
    
    String getAsyncWrapper() {
        return asyncWrapper;
    }
    
    int getTimestampOffset() {
        return offsetSeconds;
    }
}
