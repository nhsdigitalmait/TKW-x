/*
 Copyright 2018  Richard Robinson rrobinson@nhs.net

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

import java.util.Random;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;

/**
 * Class to provide details on configured delays for response messages 
 *
 * @author RRobinson
 */
public class HttpRoutingHelper{

    public static final String MINACKDELAY = "tks.routingactor.minackdelay";
    public static final String MAXACKDELAY = "tks.routingactor.maxackdelay";
    public static final String MINRESPDELAY = "tks.routingactor.minappresponsedelay";
    public static final String MAXRESPDELAY = "tks.routingactor.maxappresponsedelay";

    public static final String INFRACKDELIVERYURL = "tks.routingactor.infrastructure.forcedeliveryurl";


    protected Random rng = null;
    protected int minAckDelay = 0;
    protected int maxAckDelay = 0;
    protected int minResponseDelay = 0;
    protected int maxResponseDelay = 0;

    public String infrastructureDeliveryUrl = null;

    public HttpRoutingHelper() throws Exception{
        rng = new Random();
        minAckDelay = getIntProperty(MINACKDELAY);
        maxAckDelay = getIntProperty(MAXACKDELAY);
        if (minAckDelay > maxAckDelay) {
            int i = minAckDelay;
            minAckDelay = maxAckDelay;
            maxAckDelay = i;
        }
        minResponseDelay = getIntProperty(MINRESPDELAY);
        maxResponseDelay = getIntProperty(MAXRESPDELAY);
        if (minResponseDelay > maxResponseDelay) {
            int i = minResponseDelay;
            minResponseDelay = maxResponseDelay;
            maxResponseDelay = i;
        }
        infrastructureDeliveryUrl = Configurator.getConfigurator().getConfiguration(INFRACKDELIVERYURL);
    }


    private int getIntProperty(String pname)
            throws Exception {
        String p = Configurator.getConfigurator().getConfiguration(pname);
        if ((p == null) || (p.trim().length() == 0)) {
            return 0;
        }
        return Integer.parseInt(p.trim());
    }

    public int getDelay(int min, int max) {
        if (min == max) {
            return min;
        }
        return rng.nextInt(max - min) + min;
    }

    public int getAckDelay() {
        return getDelay(minAckDelay, maxAckDelay);
    }

    public int getResponseDelay() {
        return getDelay(minResponseDelay, maxResponseDelay);
    }

}
