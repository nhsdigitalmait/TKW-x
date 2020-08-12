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
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.Payload;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SIMPLE_ITK_OK_RESPONSE;

/**
 * returns synchronous SimpleMessage Ok followd by asynchronous Inf Ack and Bus ACk
 * @author Damian Murphy murff@warlock.org
 */
public class CDARoutingActor 
    extends RoutingActor
{
//    public static final String APPACKTEMPLATE = "tks.routingactor.cda.appacktemplate";
//    public static final String APPNACKTEMPLATE = "tks.routingactor.cda.appnacktemplate";
    public static final String CDAACKDELIVERYURL = "tks.routingactor.cda.forcedeliveryurl";
    
//    protected String appAckTemplate = null;
//    protected String appNackTemplate = null;
    protected String appResponseDeliveryUrl = null;
    protected final HttpRoutingHelper hra = new HttpRoutingHelper();
    
    public CDARoutingActor() 
            throws Exception
    {
        init();
    }
    
    @Override
    public final void init()
            throws Exception
    {
        super.init();
//        appAckTemplate = loadTemplate(APPACKTEMPLATE);
//        appNackTemplate = loadTemplate(APPNACKTEMPLATE);
        appResponseDeliveryUrl = Configurator.getConfigurator().getConfiguration(CDAACKDELIVERYURL);
    }
    
    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        String o = getBody(obj);
        DistributionEnvelopeHelper deh = DistributionEnvelopeHelper.getInstance();
        DistributionEnvelope d = deh.getDistributionEnvelope(o);
        Payload[] pl = deh.getPayloads(d);
        for (Payload p : pl) {
            d.addPayload(p);
        }
        AckDistributionEnvelope ade = new AckDistributionEnvelope(d);
        ade.makeMessage();      

        ApplicationAcknowledgment aa = new ApplicationAcknowledgment(d);
        String aade = aa.toString();
        RoutingActorSender ras = new RoutingActorSender(ade.getEnvelope(), aade, hra.infrastructureDeliveryUrl, appResponseDeliveryUrl, hra.getAckDelay(), hra.getResponseDelay());
        ras.start();
        // TODO: This needs SOAP wrapping
        //
        return SIMPLE_ITK_OK_RESPONSE;                
    }    
 
}
