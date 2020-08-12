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

import uk.nhs.digital.mait.tkwx.http.HttpException;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.http.HttpResponse;

 /**
 * Handler implementation for HTTP PUT requests for resetting simulator state variables. TKW wants a single
 * class as the handler for a request, but this request will have been put in its
 * own thread by the HTTP server. So for thread safety this class' handle() method
 * creates a separate HTTPGet..Worker instance to handle this particular request,
 * and calls handle() on that.
 * 
 * @author Simon Farrow
 */
public class HTTPPutResetSimulatorStateVariablesHandler 
    extends uk.nhs.digital.mait.tkwx.tk.boot.ToolkitHttpHandler
{

    /**
     *
     * @param path
     * @param paramsIgnored
     * @param req
     * @param resp
     * @throws HttpException
     */
    @Override
    public void handle(String path, String paramsIgnored, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        HTTPPutResetSimulatorStateVariablesWorker s = new HTTPPutResetSimulatorStateVariablesWorker(this);
        s.handle(path, paramsIgnored, req, resp);
    }
    
}
