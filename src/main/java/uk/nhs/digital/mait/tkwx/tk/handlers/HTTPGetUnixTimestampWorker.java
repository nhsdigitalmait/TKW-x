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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.time.Instant;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;

/**
 * Worker for HTTP GET requests for Unix Timestamp.
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class HTTPGetUnixTimestampWorker {

    private HTTPGetUnixTimestampHandler handler = null;

    private static final String GETUNIXTIMESTAMPPATH = "getepoch";

    HTTPGetUnixTimestampWorker(HTTPGetUnixTimestampHandler hgts) {
        handler = hgts;
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
        String p = path.toLowerCase();
        String b = null;
        if (p.contains(GETUNIXTIMESTAMPPATH)) {
            b = getUnixTimestamp();
        }
        if (b == null) {
            resp.setStatus(404, "Unix timestamp cannot be returned");
            req.setHandled(true);
            return;
        }

        resp.setStatus(200, "OK");
        req.setHandled(true);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
        try {
            bw.write(b);
            bw.flush();
        } catch (java.io.IOException e) {
            throw new HttpException("Error writing Unix timestamp");
        }

    }

    private String getUnixTimestamp() {
        String epoch = Long.toUnsignedString(Instant.now().getEpochSecond());
        String response = "<epoch>" + epoch + "</epoch>";
        return response;
    }

}
