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

import java.util.HashMap;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.RuleService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir.HapiFhirValidatorEngine;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir.HapiFhirValidatorEngineOrchestrator;

/**
 * Specifically for FHIR Messaging over ITK returns synchronous empty message
 * followed by asynchronous Inf Ack and Bus Ack. This Routing actor uses HTTP
 * transport for sending its response
 *
 * @author Simon Farrow simon.farrow1@nhs.net
 */
public class HAPIFHIRRebuild
        extends RoutingActor {

    private HapiFhirValidatorEngine hfvEngine = null;
    private String busyMessage = null;
    private String successMessage = null;
    private final HapiFhirValidatorEngineOrchestrator orchestrator = HapiFhirValidatorEngineOrchestrator.getInstance();
    String hapiFhirValidatorInstanceName = null;

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public HAPIFHIRRebuild()
            throws Exception {
        super();
        init();
        initialise(null);
    }

    public HAPIFHIRRebuild(String hapiFhirValidatorInstanceName)
            throws Exception {
        super();
        init();
        initialise(hapiFhirValidatorInstanceName);
    }

    private void initialise(String instanceName) {
        this.hapiFhirValidatorInstanceName = instanceName;
        hfvEngine = orchestrator.getEngine(hapiFhirValidatorInstanceName);
        if (hfvEngine.isPrepopulatedValidationSupport()) {
            busyMessage = hfvEngine.getRebuildBusyOOMessage();
            successMessage = hfvEngine.getRebuildSuccessOOMessage();
        }
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {

        if (hfvEngine.isPrepopulatedValidationSupport()) {
            RuleService rulesService = (RuleService) ServiceManager.getInstance().getService("RulesEngine");
            rulesService.setBusy(true, busyMessage);

            hfvEngine.rebuild(hapiFhirValidatorInstanceName);

            rulesService.setBusy(false, "");
            return successMessage;
        }
        return "";
    }
}
