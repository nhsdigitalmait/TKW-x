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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.ackextension;

import java.util.HashMap;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.AckDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.CDARouterInfrastructureAckOnly;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.RoutingActorSender;

/**
 * send an infrastructure ack using the sendDistEnvelope service
 * This class will respond back with a zero content HTTP 200 with the new ack structure – 
 * handlingSpec with added element named after the ack, service and SoapAction named after SendDist
 * referenced in simulator rules files
 * @author Damian Murphy murff@warlock.org
 */
public class SendDistInfrastructureAckOnlyWithService
        extends CDARouterInfrastructureAckOnly {

    protected String service;

    /**
     * public constructor
     * @throws Exception 
     */
    public SendDistInfrastructureAckOnlyWithService()
            throws Exception {
        super();
        service = SEND_DIST_ENVELOPE_SERVICE;
    }

    /**
     * uses the ack template
     * @param substitutions
     * @param o
     * @return empty string
     * @throws Exception 
     */
    @Override
    public String makeResponse(HashMap<String,Substitution> substitutions, Object obj)
            throws Exception {
        String o = getBody(obj);
        DistributionEnvelopeHelper deh = DistributionEnvelopeHelper.getInstance();
        DistributionEnvelope d = deh.getDistributionEnvelope(o);
        AckDistributionEnvelope ade = new AckDistributionEnvelope(d);
        ade.setService(service);
        /**
         * This is to support ITKv2.2 behaviour whereby the inf ack contains a
         * handling spec which is populated with an interaction id - this is a
         * consequence of the discredited decision to have generic services.
         */
        ade.setAckTemplate(ACK_TEMPLATE_PATH);
        ade.makeMessage();
        RoutingActorSender ras = new RoutingActorSender(ade.getEnvelope(), null, hra.infrastructureDeliveryUrl, null, hra.getAckDelay(), 0);
// Two lines commented out - These set the service of the responses from a default of busack and infack to sendDE
            ras.setBizackservice(service);
            ras.setInfackservice(service);
        ras.start();
        return "";
    }

}
