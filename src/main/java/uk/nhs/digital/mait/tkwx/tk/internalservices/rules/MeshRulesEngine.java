/*
 Copyright 2017  Richard Robinson rrobinson@nhs.net

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
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 * The TKW simulator rules engine is "pluggable" and can be provided by any
 * implementation of the "Engine" interface.
 *
 * @author Richard Robinson
 */
public class MeshRulesEngine extends DefaultRulesEngine {

    public MeshRulesEngine() {
    }

    /**
     * Do the rules lookup for a message of the given SOAPaction.
     *
     * For MESH this method is not required
     *
     * @param soapAction
     * @param input Message
     * @return ServiceResponse containing an HTTP response code and body.
     * @throws RulesException
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(String soapAction, String input)
            throws RulesException, Exception {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Do the rules lookup for a request presented as a DOM.
     *
     * @param type
     * @param input Root element of DOM form of request
     * @return ServiceResponse containing result code and body.
     * @throws RulesException
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(String type, Node input)
            throws RulesException, Exception {
        return new ServiceResponse(0, "Toolkit Simulator Rules Service");
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
        RuleSet r;
        MeshRequest meshRequest = (MeshRequest) req;
        
        setSessionID(req);

        // workaround for passing the action to the more generic interface
        String action = meshRequest.getField("eventCode");
        //ANY functionality included so that any request is responded to in accordance with one ruleset
        if (rules.containsKey("ANY")) {
            r = rules.get("ANY");
        } else {
            r = rules.get(action);
        }
        if (r == null) {
            // TODO the calling code should check the Service Response
            System.err.println("Rules error: no rules found for type " + action);
            return new ServiceResponse(-1, "Rules error: no rules found for type " + action);
        }

        List<Response> responses = r.execute(req);
        if (responses == null || responses.isEmpty()) {
            System.err.println("Rules error: null response returned for type " + action);
            return new ServiceResponse(500, "Rules error: null response returned for type " + action);
        }
        return processResponses(responses, meshRequest);
    }
}
