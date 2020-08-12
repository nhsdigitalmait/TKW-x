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

import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;

/**
 * This class allows particular elements to be added to MESH ITK3 FHIR responses
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public abstract class MESHFHIRITKRoutingActorSubs
        extends AbstractFHIRITKRoutingActor {

    protected static final String PRIORITYBLOCK
            = "		  <extension url=\"Priority\">\n"
            + "		   <valueCoding>\n"
            + "		    <system value=\"https://fhir.nhs.uk/STU3/CodeSystem/ITK-Priority-1\"/>\n"
            + "		    <code value=\"__PRIORITYCODE__\"/>\n"
            + "		    <display value=\"__PRIORITYDISPLAY__\"/>\n"
            + "		   </valueCoding>\n"
            + "		  </extension>\r\n";

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public MESHFHIRITKRoutingActorSubs()
            throws Exception {
        super();
    }

    /**
     * This method allows responses to request the addition of a Priority block
     *
     * @author Richard Robinson rrobinson@nhs.net
     * @param meshReq
     * @param template
     * @throws java.lang.Exception
     */
    public void setPriorityBlock(MeshRequest meshReq, StringBuilder template)
            throws Exception {
        if (meshReq.getPriorityCode() != null && meshReq.getPriorityCode().trim().length() != 0
                && meshReq.getPriorityDisplay() != null && meshReq.getPriorityDisplay().trim().length() != 0) {
            substitute(template, "__PRIORITY_BLOCK__", PRIORITYBLOCK);
            substitute(template, "__PRIORITYCODE__", meshReq.getPriorityCode());
            substitute(template, "__PRIORITYDISPLAY__", meshReq.getPriorityDisplay());

        } else {
            substitute(template, "__PRIORITY_BLOCK__", "");
        }
    }
}
