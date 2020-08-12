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
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.NackDistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelope;
import uk.nhs.digital.mait.distributionenvelopetools.itk.distributionenvelope.DistributionEnvelopeHelper;
import uk.nhs.digital.mait.distributionenvelopetools.itk.util.ITKException;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SIMPLE_ITK_OK_RESPONSE;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Synchronous SimpleMessageResponse OK followed by Asynchronous Inf Nack
 *
 * @author Damian Murphy murff@warlock.org
 */
public class CDARouterInfrastructureNackOnly
        extends RoutingActor implements SettableErrorCode {

    protected String errorCode = "1000";
    protected String errorText = "Default example error";
    protected String diagnosticText = "Simulated routing infrastructure rejection: default diagnostic information";

    public static final String ERRORCODE = "tks.routingactor.cda.appnackerrorcode";
    public static final String ERRORTEXT = "tks.routingactor.cda.appnackerrortext";
    public static final String DIAGTEXT = "tks.routingactor.cda.appnackdiagnostictext";
    protected final HttpRoutingHelper hra = new HttpRoutingHelper();
    private final Configurator c;

    public CDARouterInfrastructureNackOnly()
            throws Exception {
        super();
        init();
        String s = null;
        c = Configurator.getConfigurator();
        s = c.getConfiguration(ERRORCODE);
        if ((s != null) && (s.trim().length() > 0)) {
            errorCode = s.contentEquals("NONE") ? "" : s;
        }
        s = c.getConfiguration(ERRORTEXT);
        if ((s != null) && (s.trim().length() > 0)) {
            errorText = s.contentEquals("NONE") ? "" : s;
        }
        s = c.getConfiguration(DIAGTEXT);
        if ((s != null) && (s.trim().length() > 0)) {
            diagnosticText = s.contentEquals("NONE") ? "" : s;
        }
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        String o = getBody(obj);
        DistributionEnvelopeHelper deh = DistributionEnvelopeHelper.getInstance();
        DistributionEnvelope d = deh.getDistributionEnvelope(o);
        ITKException e = new ITKException(errorCode, errorText, diagnosticText);
        NackDistributionEnvelope nde = new NackDistributionEnvelope(d, e);
        nde.makeMessage();
        RoutingActorSender ras = new RoutingActorSender(nde.getEnvelope(), null, hra.infrastructureDeliveryUrl, null, hra.getAckDelay(), 0);
        ras.start();
        return SIMPLE_ITK_OK_RESPONSE;
    }

    /**
     * allows us to set different DE error codes from simulator rules looks up
     * and applied associated attributes
     *
     * @param deErrorCode
     */
    @Override
    public void setErrorCode(String deErrorCode) {
        if (!Utils.isNullOrEmpty(deErrorCode)) {
            errorCode = deErrorCode;
            if (!Utils.isNullOrEmpty(c.getConfiguration(ERRORTEXT + "." + deErrorCode))) {
                errorText = c.getConfiguration(ERRORTEXT + "." + deErrorCode);
            }
            if (!Utils.isNullOrEmpty(c.getConfiguration(DIAGTEXT + "." + deErrorCode))) {
                diagnosticText = c.getConfiguration(DIAGTEXT + "." + deErrorCode);
            }
        }
    }

}
