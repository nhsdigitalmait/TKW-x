/*
  Copyright 2020  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import java.util.HashMap;

/**
 * This class is used to orchestrate the creation and serving of HAPI FHIR
 * Validation engines.
 *
 * Each engine can be configured independently and are identified by a string
 * name.
 *
 * @author riro
 */
public class HapiFhirValidatorEngineOrchestrator {

    private final HashMap<String, HapiFhirValidatorEngine> engines = new HashMap<>();
    private static final String UNNAMED = "UNNAMED";

    private HapiFhirValidatorEngineOrchestrator() {
    }

    public static HapiFhirValidatorEngineOrchestrator getInstance() {
        return HapiFhirValidatorEngineOrchestratorHolder.INSTANCE;
    }

    public HapiFhirValidatorEngine getEngine(String hapiFhirValidatorInstanceName) {
        String name = null;
        if (hapiFhirValidatorInstanceName == null) {
            name = UNNAMED;
        } else {
            name = hapiFhirValidatorInstanceName;
        }
        HapiFhirValidatorEngine hfve = engines.get(name);
        if (hfve == null) {
            if (name.equals(UNNAMED)) {
                hfve = new HapiFhirValidatorEngine(null);
            } else {
                hfve = new HapiFhirValidatorEngine(name);
            }
            engines.put(name, hfve);
        }
        return hfve;
    }

    private static class HapiFhirValidatorEngineOrchestratorHolder {

        private static final HapiFhirValidatorEngineOrchestrator INSTANCE = new HapiFhirValidatorEngineOrchestrator();
    }
}
