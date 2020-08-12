/*
 Copyright 2014  Damian Murphy <damian.murphy@hscic.gov.uk>

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

import java.util.List;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 * The TKW simulator rules engine is "pluggable" and can be provided by any
 * implementation of the "Engine" interface. The RESTFulRulesEngine is called to
 * process RESTful URI context paths. Implementation is as-for
 * DefaultRulesEngine except that the execute() methods do not call
 * Response.instantiate(), rather the caller is expected to call
 * Response.instantiate() itself after any processing it needs.
 *
 *
 * @author Damian Murphy damian.murphy@hscic.gov.uk
 */
public class RESTfulRulesEngine
        extends DefaultRulesEngine {

    /**
     * Call to instantiate on a passed ServiceResponse that is supposed to
     * contain a Response object
     *
     * @param s ServiceResponse
     * @param body
     * @return ServiceResponse
     * @throws Exception
     */
    @Override
    public ServiceResponse instantiateResponse(ServiceResponse s, String body)
            throws Exception {
        List<Response> responses = (List<Response>) s.getScalar();
        return processResponses(responses, body);
    }

    /**
     * Call to instantiate on a passed ServiceResponse that is supposed to
     * contain a List of Response objects
     *
     * @param s ServiceResponse
     * @param req HttpRequest object
     * @return ServiceResponse
     * @throws Exception
     */
    @Override
    public ServiceResponse instantiateResponse(ServiceResponse s, HttpRequest req)
            throws Exception {
        List<Response> responses = (List<Response>) s.getScalar();
        return processResponses(responses, req);
    }

    /**
     * Do the rules lookup for a message of the given RESTful context path.
     *
     * @param httpMethod
     * @param contextPath
     * @return ServiceResponse containing 1 plus a scalar referencing the
     * response, or null for no match.
     * @throws RulesException
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(String httpMethod, String contextPath)
            throws RulesException, Exception {
        HttpRequest httpRequest = new HttpRequest("");
        httpRequest.setRequestType(httpMethod);
        httpRequest.setRequestContext(contextPath);
        return execute(httpRequest);
    }

    /**
     *
     * @param req
     * @return
     * @throws RulesException
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Request req) throws RulesException, Exception {

        String action = null;
        String httpMethod = null;
        String contextPath = null;

        if (req instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) req;
            action = httpRequest.getField(SOAP_ACTION_HEADER);
            httpMethod = httpRequest.getRequestType();
            contextPath = httpRequest.getContext();
        }

        // for a restful model we can check whether there is an action available from the Ssp-InteractionID http header
        // this would typically be for a FHIR rest interaction
        if (action == null && req != null) {
            action = req.getField(SSP_INTERACTION_ID_HEADER);
            if (action != null && action.isEmpty()) {
                action = null;
            }
        }
        
        setSessionID(req);

        RuleSet ruleSet = null;
        //ANY functionality included so that any request is responded to in accordance with one ruleset
        if (rules.containsKey("ANY")) {
            ruleSet = rules.get("ANY");
        } else if (action != null) {
            // is there a "method qualified by action" ruleset?
            ruleSet = rules.get(httpMethod + "+" + action);
        }

        if (ruleSet == null) {
            // is there a non action qualified ruleset
            ruleSet = rules.get(httpMethod);
        }

        if (ruleSet == null) {
            // TODO the calling code should check the Service Response
            System.err.println("Rules error: no rules found for method " + httpMethod);
            return new ServiceResponse(-1, "Rules error: no rules found for method " + httpMethod);
        }
        List<Response> resp = null;
        try {
            if (req != null) {
                resp = ruleSet.execute(req);
            } else {
                // this for ers
                // TODO test this
                req = new HttpRequest("");
                if (contextPath != null && !contextPath.isEmpty()) {
                    req.setRequestContext(contextPath);
                }
                if (httpMethod != null && !httpMethod.isEmpty()) {
                    req.setRequestType(httpMethod);
                }
                resp = ruleSet.execute(req);
            }
        } catch (Exception e) {
            return null;
        }
        if (resp == null || resp.isEmpty()) {
            return null;
        }
        ServiceResponse sr = new ServiceResponse();
        sr.setScalar(resp);
        sr.setCode(1);
        return sr;
    }

    /**
     *
     * @param type
     * @param input
     * @return
     * @throws RulesException
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(String type, Node input)
            throws RulesException, Exception {
        throw new Exception("RESTfulRulesEngine.execute(String, Node) not implemented. Use RESTfulRulesEngine.execute(String, String).");
    }

    @Override
    public boolean isRestful() {
        return true;
    }
}
