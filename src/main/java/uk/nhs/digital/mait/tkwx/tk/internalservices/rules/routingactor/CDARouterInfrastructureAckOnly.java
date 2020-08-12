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
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.AckDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SIMPLE_ITK_OK_RESPONSE;

/**
 * Synchronous SimpleMessageResponse OK followed by Asynchronous Inf Ack
 *
 * @author Damian Murphy murff@warlock.org
 */
public class CDARouterInfrastructureAckOnly
        extends RoutingActor {

    protected final HttpRoutingHelper hra = new HttpRoutingHelper();

    public CDARouterInfrastructureAckOnly()
            throws Exception {
        super();
        init();
    }

    @Override
    public String makeResponse(HashMap<String,Substitution> substitutions, Object obj)
            throws Exception {
        String o = getBody(obj);
        DistributionEnvelopeHelper deh = DistributionEnvelopeHelper.getInstance();
        DistributionEnvelope d = deh.getDistributionEnvelope(o);
        AckDistributionEnvelope ade = new AckDistributionEnvelope(d);
        ade.makeMessage();
        RoutingActorSender ras = new RoutingActorSender(ade.getEnvelope(), null, hra.infrastructureDeliveryUrl, null, hra.getAckDelay(), 0);
        ras.start();
        return SIMPLE_ITK_OK_RESPONSE;
    }
}
