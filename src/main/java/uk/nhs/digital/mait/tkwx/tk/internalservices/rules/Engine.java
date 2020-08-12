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

package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
/**
 * Interface for rules engines. 
 * 
 * @author Damian Murphy murff@warlock.org
 */
public interface Engine {

    public void load(String filename)
            throws Exception;

    /**
     * 
     * @param typedata
     * @param input
     * @return ServiceResponse
     * @throws RulesException
     * @throws Exception 
     */
    public ServiceResponse execute(String typedata, String input)
            throws RulesException, Exception;

    /**
     *
     * @param req Request object
     * @return ServiceResponse
     * @throws RulesException
     * @throws Exception
     */
    public ServiceResponse execute(Request req)
            throws RulesException, Exception;

    /**
     * 
     * @param typedata
     * @param input org.w3c.dom.Node xml request as dom
     * @return ServiceResponse
     * @throws RulesException
     * @throws Exception 
     */
    public ServiceResponse execute(String typedata, Node input)
            throws RulesException, Exception;
    
    /**
     * 
     * @param serviceResponse
     * @param input String body of request
     * @return ServiceResponse
     * @throws RulesException
     * @throws Exception 
     */
    public ServiceResponse instantiateResponse(ServiceResponse serviceResponse, String input)
            throws RulesException, Exception;

    /**
     * 
     * @param serviceResponse
     * @param req HttpRequest
     * @return ServiceResponse
     * @throws RulesException
     * @throws Exception 
     */
    public ServiceResponse instantiateResponse(ServiceResponse serviceResponse, HttpRequest req)
            throws RulesException, Exception;

    public boolean isRestful();
}
