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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.util.Properties;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitSimulator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Engine;
import org.w3c.dom.Node;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;

/**
 * Service providing access to the TKW simulator rules.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class RuleService
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService, Reconfigurable {

    private static final String RULEENGINE = "tks.rules.engineclass";
    private static final String DEFAULTENGINE = "uk.nhs.digital.mait.tkwx.tk.internalservices.rules.DefaultRulesEngine";
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Engine rulesEngine = null;
    private Properties bootProperties = null;
    private boolean busy = false;
    private String busyMessage = null;

    public RuleService() {
    }

    @Override
    public Properties getBootProperties() {
        return bootProperties;
    }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String rulefile = p.getProperty(RULEFILE_PROPERTY);
        if (rulefile == null) {
            throw new Exception("No rule file defined in property " + RULEFILE_PROPERTY);
        }
        String engineClass = p.getProperty(RULEENGINE);
        if (engineClass == null) {
            engineClass = DEFAULTENGINE;
        }
        load(rulefile, engineClass);
    }

    private void load(String f, String c)
            throws Exception {
        rulesEngine = (Engine) Class.forName(c).newInstance();
        rulesEngine.load(f);
    }

    /**
     *
     * @param param Object
     * @return Empty ServiceResponse
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        // NB passing String[] to instanceof Object[] returns true so the order is important here
        if (param != null && param instanceof String[]) {
            String[] sa = (String[]) param;
            if (sa.length == 2) {
                // SOAPAction , xmlPayload for default/mesh
                // http method , context path for restful
                return rulesEngine.execute(sa[0], sa[1]);
            } else {
                throw new IllegalArgumentException("String[] signature is not valid array length is not 2");
            }
        } else if (param != null && param instanceof Object[]) {
            Object[] oa = (Object[]) param;
            if (oa.length == 2) {
                if (oa[0] instanceof String) {
                    return execute((String) oa[0], oa[1]);
                } else {
                    throw new IllegalArgumentException("Object[] signature is not valid oa[0] is not a String");
                }
            } else {
                throw new IllegalArgumentException("Object[] signature is not valid array length is not 2");
            }
        } else if (param != null && param instanceof HttpRequest) {
            return rulesEngine.execute((HttpRequest) param);
        } else {
            return new ServiceResponse(0, "Toolkit Simulator Rules Service");
        }
    }

    /**
     * Calls RulesEngine.execute()
     *
     * @param type SOAPAction of request message | mesh eventCode
     * @param param DOM Node representing the root of the request message |
     * MeshRequest
     * @return ServiceResponse output from the rules engine containing HTTP
     * response code and body
     * @throws Exception
     */
    private ServiceResponse execute(String type, Object param)
            throws Exception {
        if (param instanceof MeshRequest) {
            MeshRequest req = (MeshRequest) param;
            // TODO workaround adding the eventCode which is retrievable from an eponymous field
            req.setHeader("eventCode", type);
            return rulesEngine.execute(req);
        } else if (param instanceof Node) {
            Node n = (Node) param;
            return rulesEngine.execute(type, n);
        } else {
            throw new IllegalArgumentException("Object[] signature oa[1] is not MeshRequest or Node");
        }
    }

    /**
     *
     * @param p Properties object
     * @throws Exception
     */
    @Override
    public void reconfigure(Properties p)
            throws Exception {
        boot(simulator, p, serviceName);
    }

    /**
     *
     * @param what
     * @param value
     * @return null string
     * @throws Exception
     */
    @Override
    public String reconfigure(String what, String value)
            throws Exception {
        if (what.equals("/rulesetchange") || what.equals(RULEFILE_PROPERTY)) {
            String engineClass = bootProperties.getProperty(RULEENGINE);
            if (engineClass == null) {
                engineClass = DEFAULTENGINE;
            }
            load(value, engineClass);
        }
        return null;
    }

    /**
     *
     * @param s ServiceResponse object
     * @param body This may be context path or http content depending on the
     * class
     * @return ServiceResponse
     * @throws Exception
     */
    public ServiceResponse instantiateResponse(ServiceResponse s, String body)
            throws Exception {
        if (busy) {
            s.setCode(500);
            s.setCodePhrase("Internal Server Error");
            s.setResponse(busyMessage);
            s.setHttpHeaders(new HttpHeaderManager());
            return s;
        } else {
            return rulesEngine.instantiateResponse(s, body);
        }
    }

    /**
     *
     * @return boolean
     */
    public boolean isRestful() {
        return rulesEngine.isRestful();
    }

    /**
     *
     * @param simulatorServiceResponse
     * @param req HttpRequest object
     * @return ServiceResponse
     * @throws java.lang.Exception
     */
    public ServiceResponse instantiateResponse(ServiceResponse simulatorServiceResponse, HttpRequest req) throws Exception {
        if (busy) {
            simulatorServiceResponse.setCode(500);
            simulatorServiceResponse.setCodePhrase("Internal Server Error");
            simulatorServiceResponse.setResponse(busyMessage);
            simulatorServiceResponse.setHttpHeaders(new HttpHeaderManager());
            return simulatorServiceResponse;
        } else {
            return rulesEngine.instantiateResponse(simulatorServiceResponse, req);
        }
    }

    public synchronized void setBusy(boolean busy, String message) {
        this.busyMessage = message;
        this.busy = busy;
    }
}
