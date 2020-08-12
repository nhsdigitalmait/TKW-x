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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 * The TKW simulator rules engine is "pluggable" and can be provided by any
 * implementation of the "Engine" interface. The DefaultRulesEngine is the
 * default implementation, the one whose configuration files are provided for
 * use in the ITK Accreditation process, and described in the configuration
 * manual.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class DefaultRulesEngine
        implements Engine {

    protected HashMap<String, Substitution> substitutions = new HashMap<>();
    private Substitution sessionSubstitution = null;
    protected HashMap<String, RuleSet> rules = new HashMap<>();

    private static final String SESSION_ID_TAG = "SESSION_ID";

    public DefaultRulesEngine() {
    }

    /**
     * Loads the simulator rules configuration at the given file name.
     *
     * @param filename
     * @throws Exception
     */
    @Override
    public void load(String filename)
            throws Exception {
//      loadRules(filename, NOTHING, 0, null);

        SimulatorRulesGrammarCompilerVisiter parser = new SimulatorRulesGrammarCompilerVisiter();
        parser.parse(filename);

        substitutions = parser.getSubstitutions();
        rules = parser.getRules();

        // look for a session id substitution tag
        sessionSubstitution = substitutions.get(SESSION_ID_TAG);
    }

    /**
     * Do the rules lookup for a message of the given SOAPaction.
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
        HttpRequest httpRequest = new HttpRequest("");
        httpRequest.setHeader(SOAP_ACTION_HEADER, soapAction);
        httpRequest.setInputStream(new ByteArrayInputStream(input.getBytes()));
        httpRequest.setContentLength(input.length());
        return execute(httpRequest);
    }

    /**
     * Do the rules lookup for a request presented as a DOM.
     *
     * @param type SOAPaction
     * @param input Root element of DOM form of request
     * @return ServiceResponse containing HTTP result code and body.
     * @throws RulesException
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(String type, Node input)
            throws RulesException, Exception {
        RuleSet r = rules.get(type);
        if (r == null) {
            throw new RulesException("No rules found for type " + type);
        }
        DOMImplementationLS dls = (DOMImplementationLS) input.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lss = dls.createLSSerializer();
        String s = lss.writeToString(input);

        HttpRequest req = new HttpRequest("");
        req.setInputStream(new ByteArrayInputStream(s.getBytes()));
        req.setContentLength(s.length());
        List<Response> responses = r.execute(req);
        return processResponses(responses, s);
    }

    /**
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ServiceResponse instantiateResponse(ServiceResponse s, HttpRequest req)
            throws Exception {
        return new ServiceResponse(0, "Toolkit Simulator Rules Service");
    }

    /**
     *
     * @param s
     * @param body
     * @return
     * @throws Exception
     */
    @Override
    public ServiceResponse instantiateResponse(ServiceResponse s, String body)
            throws Exception {
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

        String action = null;
        if (req instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) req;
            action = httpRequest.getField(SOAP_ACTION_HEADER);
        }

        setSessionID(req);

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
        return processResponses(responses, req);
    }

    @Override
    public boolean isRestful() {
        return false;
    }

    /**
     * returns the service response form the first response but also processes
     * any subsequent ones
     *
     * @param responses List of responses from then or else clauses
     * @param o object to process could be a String, an HttpRequest or a
     * MeshRequest
     * @return ServiceResponse
     * @throws Exception
     */
    protected ServiceResponse processResponses(List<Response> responses, Object o) throws Exception {
        ServiceResponse sr = null;
        for (Response response : responses) {
            if (sr == null) {
                // where there are > 1 responses the service response comes from the first respsonse
                sr = response.instantiate(substitutions, o);
            } else {
                response.instantiate(substitutions, o);
            }
        }
        return sr;
    }

    /**
     * if there is a sessionid substitution available use it to get the session
     * id and set it into the Manager singleton
     *
     * @param req
     */
    protected void setSessionID(Request req) {
        if (sessionSubstitution != null) {
            StringBuffer sb = new StringBuffer(SESSION_ID_TAG);
            try {
                sessionSubstitution.substitute(sb, req);
                String sessionID = sb.toString();
                if (!SESSION_ID_TAG.equals(sessionID)) {
                    SessionStateManager.getInstance().setCurrentSessionID(sessionID);
                }
            } catch (Exception ex) {
            }
        }
    }
}
