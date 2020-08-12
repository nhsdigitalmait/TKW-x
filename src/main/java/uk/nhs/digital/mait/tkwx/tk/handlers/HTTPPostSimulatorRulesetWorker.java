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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.InputStreamReader;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Worker for HTTP POST requests for dynamically reconfiguring the simulator
 * ruleset. Reads the request and passes the fully qualified path plus name of
 * the ruleset to Ruleset class to be reloaded
 *
 * Returns an HTTP 400 if the request is not readable
 *
 * @author Richard Robinson
 */
public class HTTPPostSimulatorRulesetWorker {

    private HTTPPostSimulatorRulesetHandler handler = null;

    protected String request = null;

    HTTPPostSimulatorRulesetWorker(HTTPPostSimulatorRulesetHandler hpsrh) {
        handler = hpsrh;
    }

    /**
     *
     * @param path
     * @param paramsIgnored
     * @param req
     * @param resp
     * @throws HttpException
     */
    public void handle(String path, String paramsIgnored, HttpRequest req, HttpResponse resp)
            throws HttpException {
        if (path == null) {
            resp.setStatus(500);
            req.setHandled(true);
            return;
        }
        InputStreamReader isr = new InputStreamReader(req.getInputStream());
        char[] buf = new char[req.getContentLength()];
        int totalRead = 0;
        int dataSize = 0;
        try {
            while (totalRead < req.getContentLength()) {
                dataSize = isr.read(buf, totalRead, req.getContentLength() - totalRead);
                if (dataSize == -1) {
                    break;
                }
                totalRead += dataSize;
            }
            if (totalRead != req.getContentLength()) {
                throw new Exception("Given data is not the same size as content length: " + totalRead + "/" + req.getContentLength());
            }
            request = new String(buf);
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, HTTPPostSimulatorRulesetWorker.class.getName(), "Error reading incoming request" + e);
            resp.setStatus(400, "Request cannot be read");
            req.setHandled(true);
            return;
        }

        resp.setStatus(200, "OK");
        req.setHandled(true);
//        resp.forceClose();

        Reconfigurable svc = (Reconfigurable) ServiceManager.getInstance().getService("RulesEngine");
        try {
            svc.reconfigure(path, request);
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, HTTPPostSimulatorRulesetWorker.class.getName(), "Reconfiguration Failed" + e);
        }

    }
}
