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
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.CDARouterInfrastructureAckOnly;

/**
 * This class will respond back with a zero content HTTP 200 with the ack structure unchanged from the original – Service and SoapAction named after the ack
 * referenced in simulator rules files
 * @author Damian Murphy murff@warlock.org
 */
public class SendDistInfrastructureAckOnly
        extends CDARouterInfrastructureAckOnly {

    /**
     * public constructor
     * @throws Exception
     */
    public SendDistInfrastructureAckOnly()
            throws Exception {
        super();
    }

    @Override
    public String makeResponse(HashMap<String,Substitution> substitutions, Object obj)
            throws Exception {
        super.makeResponse(substitutions, obj);
        return "";
    }

}
