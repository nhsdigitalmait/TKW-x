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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Class to encapsulate a list of Rule instances.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class RuleSet {

    private ArrayList<Rule> rules = null;
    private String requestType = null;
    private String sdm = null;
    private static final String SIMULATORDEBUGMODE = "tks.debug.simulatordebugmode";

    RuleSet(String rq)
            throws Exception {
        requestType = rq;
        rules = new ArrayList<>();
        Configurator config = Configurator.getConfigurator();
        sdm = config.getConfiguration(SIMULATORDEBUGMODE);
    }

    void addRule(Rule r) {
        rules.add(r);
    }

    /**
     * Evaluate each Rule in the list until a Response is identified.
     *
     * @param req HttpRequest object
     * @return Response instance
     * @throws Exception If processing runs off the end of the list without
     * identifying a response.
     */
    List<Response> execute(Request req)
            throws Exception {
        List<Response> result = null;
        for (Rule r : rules) {
            List<Response> resp = r.evaluate(req);
            if (resp != null && !resp.isEmpty()) {
                result = resp;
                logRuleEvent(SIMULATOR_RULE_TRIGGERED + r.getName());
                break;
            }
        }
        if (result == null || result.isEmpty()) {
            logRuleEvent(SIMULATOR_RULE_TRIGGERED_RULES_FOR_REQUEST + requestType + RAN_OFF_THE_END_OF_PROCESSING_WITHOUT_ID);
            throw new Exception(new Date() + " " + "Rules for request type " + requestType + RAN_OFF_THE_END_OF_PROCESSING_WITHOUT_ID);
        }
        return result;
    }

    private void logRuleEvent(String message) {
        if (sdm != null) {
            if (sdm.toUpperCase().contains("Y")) {
                Logger.getInstance().log(message);
            }
            // for console
            if (sdm.toUpperCase().contains("C")) {
                System.out.println(new Date() + " " + message);
            }
        }
    }
    private static final String RAN_OFF_THE_END_OF_PROCESSING_WITHOUT_ID = " ran off the end of processing without identifying a response";
    private static final String SIMULATOR_RULE_TRIGGERED_RULES_FOR_REQUEST = "Simulator rule triggered: Rules for request type ";
    private static final String SIMULATOR_RULE_TRIGGERED = "Simulator rule triggered: ";
}
