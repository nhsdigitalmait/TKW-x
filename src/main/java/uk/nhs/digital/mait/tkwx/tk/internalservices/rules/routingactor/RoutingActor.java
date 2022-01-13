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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.replaceTkwroot;


/**
 * Abstract superclass for simulator processes which perform potentially
 * multiple asynchronous operations on receipt of a message. Subclasses of this
 * will parse out a routed message and are responsible for application specific
 * subsequent responses. This class provides the infrastructure ack/nack
 * templates but it is the responsibility of the concrete class actually to use
 * them.
 *
 * @author Damian Murphy murff@warlock.org
 */
public abstract class RoutingActor
        implements uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Responder {


    protected static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);

    protected void init()
            throws Exception {
        ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));

    }

    /**
     * Overload with property name
     * @param pname
     * @return template string
     * @throws Exception 
     */
    protected String loadTemplate(String pname)
            throws Exception {
        String fname = Configurator.getConfigurator().getConfiguration(pname);
        if ((fname == null) || (fname.trim().length() == 0)) {
            throw new Exception("RoutingActor.init() template filename " + pname + " not set");
        }
        // import doesn't work because there is ambiguity due to overrides
        return Utils.readFile2String(replaceTkwroot(fname));
    }


    /**
     * abstract method
     * @param substitutions ArrayList of Substitution objects
     * @param o message string
     * @return populates response string
     * @throws Exception
     */
    @Override
    public abstract String makeResponse(HashMap<String, Substitution> substitutions, Object o)
            throws Exception;

    /**
     *
     * @return
     */
    @Override
    public Boolean forceClose() {
        return false;
    }

    /**
     * apply the substitutions to the template
     * @param template string
     * @param substitutions ArrayList of Substitution objects
     * @param o request message to be used for substitutions
     * @return substituted template string
     * @throws Exception 
     */
    protected String performSimulatorConfigSubstitutions(String template, HashMap<String,Substitution> substitutions, String o) throws Exception {
        StringBuffer sb = new StringBuffer(template);
        for (Substitution s : substitutions.values()) {
            s.substitute(sb, o);
        }
        return sb.toString();
    }
    
    /**
     * performs typesafe casting to get the body of the request
     * @param obj String or HttpRequest
     * @return String containing body of request
     * @throws Exception 
     */
    protected String getBody(Object obj) throws Exception {
        String body = null;
        if (obj instanceof String ) {
            body = (String)obj;
        } else if (obj instanceof HttpRequest ) {
            body = new String(((HttpRequest)obj).getBody());
        }
        return body;
    }

}
