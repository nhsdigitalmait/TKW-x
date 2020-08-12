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
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;

/**
 * Specifically for FHIR Messaging over ITK returns synchronous empty messsage
 * followed by asynchronous Inf Ack/Nack but no Bus Ack. This Routing actor uses
 * HTTP transport for sending its response
 *
 * @author Simon Farrow simon.farrow1@nhs.net
 */
public class FHIRITKRoutingActorAckOnly
        extends AbstractFHIRITKRoutingActor {
    private final HttpRoutingHelper hra = new HttpRoutingHelper();

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public FHIRITKRoutingActorAckOnly()
            throws Exception {
        super();
        init();
    }

    @Override
    public String makeResponse(HashMap<String,Substitution> substitutions, Object obj)
            throws Exception {
        String o = getBody(obj);
        StringBuilder sbAppAckTemplate = new StringBuilder(appAckTemplate);
        ia.substituteDetails(sbAppAckTemplate);

        // apply the substitutions from the simulator config
        String appAckSubstituted = performSimulatorConfigSubstitutions(sbAppAckTemplate.toString(), substitutions, o);

        RoutingActorSender ras = new RoutingActorSender(appAckSubstituted, null, hra.infrastructureDeliveryUrl, null, hra.getAckDelay(), hra.getResponseDelay());
        ras.start();
        return "";
    }

}
