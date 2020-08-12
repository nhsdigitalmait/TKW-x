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
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.routingactor.CDARoutingActor;

/**
 * This class will respond back with a zero length content 
 * @author sifa2
 */
public class SendEmptyResponse extends CDARoutingActor {


    /**
     * public constructor
     * @throws Exception 
     */
    public SendEmptyResponse() throws Exception {
        super();
    }

    @Override
    public String makeResponse(HashMap<String,Substitution> substitutions, Object o)
            throws Exception {
        return "";
    }

}
