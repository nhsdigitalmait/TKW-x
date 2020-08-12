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
import java.io.OutputStreamWriter;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;
import uk.nhs.digital.mait.tkwx.http.HttpException;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;


/**
 * Handler implementation to provide information about TKW internals, under an
 * HTTP 200 response.
 * 
 * @author Damian Murphy murff@warlock.org
 */ 
public class DiagnosticHandler
    extends uk.nhs.digital.mait.tkwx.tk.boot.ToolkitHttpHandler
{
    public DiagnosticHandler() {}

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
        throws HttpException
    {
        StringBuilder   sb = new StringBuilder("Input SOAPaction: ");
        sb.append(req.getField(SOAP_ACTION_HEADER));
        sb.append("\nSimulator services:\n");
        for (String s : ServiceManager.getInstance().getServiceNames()) {
            sb.append(s);
            sb.append("\n");
        }
        sb.append("\nMemory: ");
        sb.append(Runtime.getRuntime().freeMemory());
        sb.append(" free of ");
        sb.append(Runtime.getRuntime().maxMemory());
        resp.setContentType("text/plain");
        resp.setStatus(200, "OK");
        try {
            OutputStreamWriter osw = new OutputStreamWriter(resp.getOutputStream());
            osw.write(sb.toString());
            osw.flush();
        }
        catch (Exception e) {
            throw new HttpException("Failed to write output stream: " + e.getMessage());
        }
        req.setHandled(true);
    }

}
