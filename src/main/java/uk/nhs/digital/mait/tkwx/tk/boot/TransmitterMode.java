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
import uk.nhs.digital.mait.commonutils.util.ConfigurationStringTokeniser;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
/**
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class TransmitterMode 
    extends Mode
{
    private static final String SERVICELIST = "Sender";
    private static final String TXDOSIGNPROPERTY = "tks.transmitter.includesigner";
    
    public TransmitterMode() {
        serviceList = SERVICELIST;
    }
            
    @Override
    public void init(ToolkitSimulator t)
            throws Exception
    {
        super.init(t);
        Properties p = t.getProperties();
        p.setProperty(SERVICELISTPROPERTY, serviceList);       
        String tr = p.getProperty(TRANSMITTERMODE_PROPERTY);
        if (tr == null) {
            Logger.getInstance().log(SEVERE,
                    TransmitterMode.class.getName(),
                    "There is no transport defined for transmitter: "
                            + TRANSMITTERMODE_PROPERTY
                            + " not defined");
            return;
        }
        String sn = p.getProperty(ToolkitSimulator.SERVICES);
        if (Utils.isNullOrEmpty(sn)) {
            Logger.getInstance().log(SEVERE,
                    TransmitterMode.class.getName(),
                    "property "
                            + ToolkitSimulator.SERVICES
                            + " not defined");
            return;
        }
        ConfigurationStringTokeniser cst = new ConfigurationStringTokeniser(tr);
        
        if (cst.countTokens()!=1) {
            Logger.getInstance().log(SEVERE,
                    TransmitterMode.class.getName(),
                    "property "
                            + ToolkitSimulator.SERVICES
                            + " not defined");
            return;
        }
        toolkitServiceName = tr.trim()+ "Transmitter";
        p.setProperty(SERVICELISTPROPERTY, toolkitServiceName + " " + sn); 
        t.boot();
        rootService = ServiceManager.getInstance().getService(toolkitServiceName);        
    }
    
    @Override
    public void executeService(String arg)
            throws Exception
    {
        rootService.execute(null);
    }
    
}
