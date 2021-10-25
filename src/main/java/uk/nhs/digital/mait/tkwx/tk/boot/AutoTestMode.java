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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 *
 * @author Damian Murphy murff@warlock.org
 */
public class AutoTestMode 
    extends TransmitterMode
{
    private static final String SERVICELIST
            = "AutoTest RulesEngine Sender Validator";    
    
    public AutoTestMode() {
    }
    @Override
    public void init(ToolkitSimulator t)
            throws Exception
    {
        Properties p = t.getProperties();
        String tr = p.getProperty(TRANSPORTLIST_PROPERTY);
        if (tr == null) {
            Logger.getInstance().log(SEVERE,
                    AutoTestMode.class.getName(),
                    "No transports defined for simulator: "
                            + TRANSPORTLIST_PROPERTY
                            + " not defined");
            return;
        }
        serviceList = tr.trim() + " " + SERVICELIST;
        super.init(t);   
        toolkitServiceName = "AutoTest";    
        rootService = ServiceManager.getInstance().getService(toolkitServiceName);        
    }

    @Override
    public void executeService(String arg)
            throws Exception
    {
        rootService.execute(arg);
    }    
}
