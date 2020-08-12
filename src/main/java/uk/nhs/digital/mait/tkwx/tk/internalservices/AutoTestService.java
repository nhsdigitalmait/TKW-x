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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.ScriptParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;

/**
 * INCOMPLETE. Do not use.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class AutoTestService
        implements uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService {

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;

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
        if (bootProperties.getProperty("tks.HttpTransport.default.asyncttl") != null) {
            System.setProperty("tks.HttpTransport.default.asyncttl", bootProperties.getProperty("tks.HttpTransport.default.asyncttl"));
        }

    }

    /**
     * @param param Object
     * @return ServiceResponse
     * @throws Exception
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception {
        if (param != null && param instanceof Object[]) {
            return new ServiceResponse(200, "");
        } else {
            if (param != null && param instanceof String) {
                return runTestScript( (String) param);
            } else {
                throw new IllegalArgumentException("Object signature is not valid. Not a String");
            }
        }
    }

    /**
     * 
     * @param testscript String
     * @return ServiceResponse
     * @throws Exception 
     */
    private ServiceResponse runTestScript(String testscript) throws Exception {
        ScriptParser p = new ScriptParser(bootProperties);
        Script script = p.parse(testscript);
        script.execute(simulator);
        return null;
    }
}
