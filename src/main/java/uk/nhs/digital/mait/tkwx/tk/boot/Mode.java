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

import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;

/**
 * Abstract class for extension to carry definitions for TKW operating modes,
 * and mode-specific initialisations.
 *
 * @author Damian Murphy murff@warlock.org
 */
public abstract class Mode {

    protected String serviceList = null;
    protected String toolkitServiceName = null;
    protected ToolkitService rootService = null;

    public void init(ToolkitSimulator t)
            throws Exception {
        Configurator config = Configurator.getConfigurator();
        // if there's any servicenames defined in the .properties config dont throw them away
        String sn = config.getConfiguration(ToolkitSimulator.SERVICES);

        if (sn != null) {
            serviceList = serviceList.concat(" ").concat(sn);
        }
        config.setConfiguration(SERVICELISTPROPERTY, serviceList);

    }

    public ToolkitService getService() {
        return rootService;
    }

    public abstract void executeService(String arg) throws Exception;
}
