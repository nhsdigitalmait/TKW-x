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
package uk.nhs.digital.mait.tkwx.httpinterceptor.interceptor;

import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.HttpTransport;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Class to "intercept + forward" an HTTP stream. Uses the handler paths
 * assigned by the properties file for the context path
 *
 * @author Richard Robinson rrobinson@hscic.gov.uk
 */
public class HttpInterceptHandler
        extends uk.nhs.digital.mait.tkwx.tk.boot.ToolkitHttpHandler {


    /**
     * Constructor
     */
    public HttpInterceptHandler() {
    }

    /**
     * Method to return saved messages directory
     *
     * @return saved messages directory
     */
    public String getSavedMessagesDirectory() {
        return savedMessagesDirectory;
    }

    /**
     *
     * @param t
     * @throws Exception
     */
    @Override
    public void setToolkit(HttpTransport t)
            throws Exception {
        super.setToolkit(t);
        Configurator config = Configurator.getConfigurator();
        savedMessagesDirectory = config.getConfiguration(SAVEDMESSAGES_PROPERTY);
        if (config.getConfiguration(SYNCHRONOUSRESPONSEDELAY_PROPERTY) != null) {
            System.setProperty(SYNCHRONOUSRESPONSEDELAY_PROPERTY, config.getConfiguration(SYNCHRONOUSRESPONSEDELAY_PROPERTY));
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
    @Override
    public void handle(String pathIgnored, String paramsIgnored, HttpRequest req, HttpResponse resp)
            throws HttpException {
        HttpInterceptWorker hiw = new HttpInterceptWorker(req, this);
        try {
            hiw.process(req, resp);
        } catch (Exception e) {
            String s = "Error in the processing of the response to the request: ";
            Logger.getInstance().log(SEVERE,HttpInterceptHandler.class.getName(),s + e.toString());
            throw new HttpException(s + e.toString());
        }
    }
}
