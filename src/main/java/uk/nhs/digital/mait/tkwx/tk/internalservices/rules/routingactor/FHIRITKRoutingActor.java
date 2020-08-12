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
 * Specifically for FHIR Messaging over ITK returns synchronous empty message
 * followed by asynchronous Inf Ack and Bus Ack. This Routing actor uses HTTP
 * transport for sending its response
 *
 * @author Simon Farrow simon.farrow1@nhs.net
 */
public class FHIRITKRoutingActor
        extends AbstractFHIRITKRoutingActor {

    private final HttpRoutingHelper hra = new HttpRoutingHelper();

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public FHIRITKRoutingActor()
            throws Exception {
        super();
        init();
    }

    /**
     * Constructs templated async bus ack and inf ack ITK responses
     * which are then scheduled for transmission at the configured delay times
     * using RoutingActorSender in a separate thread
     * 
     * @param substitutions
     * @param obj message object could be String or HttpRequest
     * @return empty string since responses are async
     * @throws Exception 
     */
    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {

        String o = getBody(obj);

        StringBuilder sbAppAckTemplate = new StringBuilder(appAckTemplate);
        StringBuilder sbBusAckTemplate = new StringBuilder(busAckTemplate);

        ia.substituteDetails(sbAppAckTemplate);
        ba.substituteDetails(sbBusAckTemplate);

        // apply the substitutions from the simulator config
        String appAckSubstituted = performSimulatorConfigSubstitutions(sbAppAckTemplate.toString(), substitutions, o);
        String busAckSubstituted = performSimulatorConfigSubstitutions(sbBusAckTemplate.toString(), substitutions, o);
        appResponseDeliveryUrl = configurator.getConfiguration(CDARoutingActor.CDAACKDELIVERYURL);

        RoutingActorSender ras = new RoutingActorSender(appAckSubstituted, busAckSubstituted, hra.infrastructureDeliveryUrl, appResponseDeliveryUrl, hra.getAckDelay(), hra.getResponseDelay());
        ras.start();
        return "";
    }
}
