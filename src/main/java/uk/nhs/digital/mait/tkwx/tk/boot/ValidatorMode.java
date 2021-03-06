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

/**
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class ValidatorMode 
    extends Mode
{
    private static final String SERVICELIST = "Validator";
    
    public ValidatorMode() {
        serviceList = SERVICELIST;
        toolkitServiceName = "Validator";
    }
            
    @Override
    public void init(ToolkitSimulator t)
            throws Exception
    {
        super.init(t);
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
