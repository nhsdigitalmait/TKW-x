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

import java.io.BufferedOutputStream;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;

/**
 * Worker for HTTP GET requests for a CRL. The CRL itself is assumed to be a
 * file, and three are supported using the properties "tks.tls.crl",
 * "tks.signing.crl" and "tks.crl" for a TLS, a message signing and a "general"
 * CA CRL.
 *
 * The full context path is configured via properties in the usual TKW way, but
 * to distinguish the three cases, context paths must contain the substrings
 * "gettlscrl", "getsigningcrl" and "getcrl" for each, respectively.
 *
 * Returns an HTTP 404 if a request is made for a CRL which has not been
 * configured.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HTTPGetCRLWorker {

    private HTTPGetCRLHandler handler = null;

    private static final String GETTLSCRLPATH = "gettlscrl";
    private static final String GETSIGNINGCRLPATH = "getsigningcrl";
    private static final String GETCOMMONCRLPATH = "getcrl";

    HTTPGetCRLWorker(HTTPGetCRLHandler hgch) {
        handler = hgch;
    }

    /**
     *
     * @param path context path
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
        String p = path.toLowerCase();
        byte[] b = null;
        if (p.contains(GETTLSCRLPATH)) {
            b = handler.getTlsCrl();
        } else {
            if (p.contains(GETSIGNINGCRLPATH)) {
                b = handler.getSigningCrl();
            } else {
                if (p.contains(GETCOMMONCRLPATH)) {
                    b = handler.getCommonCrl();
                }
            }
        }
        if (b == null) {
            resp.setStatus(404, "Requested CRL not found");
            req.setHandled(true);
            return;
        }
        resp.setStatus(200, "OK");
        req.setHandled(true);

        BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
        try {
            bos.write(b);
            bos.flush();
        } catch (java.io.IOException e) {
            throw new HttpException("Error writing CRL");
        }
    }
}
