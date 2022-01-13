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

import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.SettableErrorCode;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isBinarySourceFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.replaceTkwroot;
import static uk.nhs.digital.mait.tkwx.util.Utils.wrapBinaryPayload;

/**
 * Class for making simulated responses to requests. Can be based on a template
 * file, or on a class that implements the Responder interface.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Response {

    // Currently two types of response supported: "class" and "file"
    private String name = null;
    private String url = null;

    private Responder responseClass = null;
    private String template = null;
    private String action = null;
    private int responseCode = 0;
    private String responsePhrase = "";
    private HttpHeaderManager httpHeaders = new HttpHeaderManager();
    private boolean noResponse = false;
    private String variableName = null;
    private String variableValue = null;

    /**
     * public constructor
     *
     * @param name Response name String
     * @param url Response url for source of message
     * @throws Exception
     */
    public Response(String name, String url)
            throws Exception {
        this.name = name;
        this.url = url;
        init();
    }

    /**
     * mutator
     *
     * @param i http response code
     */
    public void setCode(int i) {
        responseCode = i;
    }

    /**
     * mutator
     *
     * @param s http response phrase eg "200 OK"
     */
    public void setResponsePhrase(String s) {
        responsePhrase = s;
    }

    /**
     * mutator
     *
     * @param a http response asynchronous action string
     */
    public void setAction(String a) {
        action = a;
    }

    private void init()
            throws Exception {
        if (url.contentEquals("NONE")) {
            noResponse = true;
            return;
        }
        String resource = null;
        if (url.startsWith("class:")) {
            resource = url.substring(6);
            // are there any query parameters?
            if (resource.contains("?")) {
                // take uri without the scheme as class: is not officially recognised
                URI classUri = new URI(resource);
                String query = classUri.getQuery();
                String path = classUri.getPath();
                // if the passed in URI has query string parse the queries
                Map<String, String> query_pairs = new LinkedHashMap<>();
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
                // This implementation will only pass one parameter called "param" but could be expanded here
                if (pairs.length > 0) {
                    if (query_pairs.get("param") != null) {
                        Constructor c = Class.forName(path).getConstructor(String.class);
                        responseClass = (Responder) c.newInstance(query.substring(query.indexOf("=") + 1));
                    } else {
                        throw new Exception("Query parameters passed via simulator ruleset response URIs must be a key value pair where the key=param");
                    }
                }
            } else {
                responseClass = (Responder) Class.forName(resource).newInstance();
            }
        } else {
            if (isBinarySourceFile(url)) {
                byte[] bytes = Files.readAllBytes(Paths.get(replaceTkwroot(url)));
                template = wrapBinaryPayload(bytes).toString();
            } else {
                template = Utils.readFile2String(replaceTkwroot(url));
            }
        }
    }

    /**
     * add an http header pair to be returned in the response
     *
     * @param headerName
     * @param headerValue
     */
    public void addHttpHeader(String headerName, String headerValue) {
        httpHeaders.addHttpHeader(headerName, headerValue);
    }

    /**
     * initialiser
     *
     * @param substitutions ArrayList of substitutions
     * @param obj initialising object typically a String but MeshRequest for
     * Mesh Interactions
     * @return ServiceResponse Object
     * @throws Exception
     */
    public ServiceResponse instantiate(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        ServiceResponse sr = null;
        String o = null;
        Request req = null;
        if (obj instanceof String) {
            o = (String) obj;
        } else if (obj instanceof Request) {
            req = (Request) obj;
        }

        if (variableName != null) {
            SessionStateManager.getInstance().setValue(variableName, variableValue);
        }

        if (noResponse) {
            sr = new ServiceResponse(responseCode, "");
            sr.setCodePhrase(responsePhrase);
            return sr;
        }
        if (responseClass != null) {
            if (responseClass instanceof SettableErrorCode) {
                // For CDA responder classes the action field contains the DE error code
                SettableErrorCode nacker = (SettableErrorCode) responseClass;
                if (!Utils.isNullOrEmpty(action)) {
                    nacker.setErrorCode(action);
                }
            }
            if (!responseClass.forceClose()) {
                StringBuffer rcResponse = null;
                if (obj instanceof String) {
                    rcResponse = new StringBuffer(responseClass.makeResponse(substitutions, o));
                    for (Substitution s : substitutions.values()) {
                        s.substitute(rcResponse, o);
                    }
                } else if (obj instanceof Request) {
                    rcResponse = new StringBuffer(responseClass.makeResponse(substitutions, req));
                    for (Substitution s : substitutions.values()) {
                        s.substitute(rcResponse, req);
                    }
                }

                sr = new ServiceResponse(responseCode, rcResponse.toString());
            } else {
                return null;
            }
        } else // non null response class
        {
            sr = new ServiceResponse(responseCode, template);
        }

        sr.setAction(action);
        sr.setCodePhrase(responsePhrase);

        // create a new HttpHeaderManager object into which the original httpHeaders object is copied. 
        //This is done as the headers may contain substitutable content and we dont want the original version 
        //updated in this process as otherwise the substitutions will be preminant from that point on 
        HttpHeaderManager hm = new HttpHeaderManager();
        ArrayList<String> fnames = httpHeaders.getFieldNames();
        for (String name : fnames) {
            String value = httpHeaders.getHttpHeaderValue(name);
            hm.addHttpHeader(name, value);
        }
        hm.setFirstLine(httpHeaders.getFirstLine());
        sr.setHttpHeaders(hm);
        serviceResponseSubstitute(sr, substitutions, req, o);
        return sr;

    }

    /**
     *
     * @param substitutions
     * @param o String request body
     * @return substituted string response
     * @throws Exception
     */
    private void serviceResponseSubstitute(ServiceResponse sr, HashMap<String, Substitution> substitutions, Request req, String o) throws Exception {
        for (Substitution s : substitutions.values()) {
            if (req != null && o == null) {
                s.substitute(sr, req);
            } else if (req == null && o != null) {
                s.substitute(sr, o);
            } else {
                throw new Exception("Can't substitute using more than one substitution source");
            }
        }
    }

    /**
     *
     * @param substitutions
     * @param o String request body
     * @return substituted string response
     * @throws Exception
     */
    private String substituteTemplate(HashMap<String, Substitution> substitutions, String strTemplate, Request req, String o) throws Exception {
        StringBuffer sb = new StringBuffer(strTemplate);
        for (Substitution s : substitutions.values()) {
            if (req != null && o == null) {
                s.substitute(sb, req);
            } else if (req == null && o != null) {
                s.substitute(sb, o);
            } else {
                throw new Exception("Can't substitute using more than one substitution source");
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param variableName
     * @param value
     */
    public void setVariableAssignment(String variableName, String value) {
        this.variableName = variableName;
        this.variableValue = value;
    }
}
