/*
 Copyright 2017  Richard Robinson rrobinson@nhs.net

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
import java.util.UUID;
import uk.nhs.digital.mait.tkwx.mesh.MeshMessage;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.mesh.OpenMeshMessageRegister;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import static uk.nhs.digital.mait.tkwx.util.Utils.substitute;

/**
 * Specifically for FHIR MESH ITK Messaging returns synchronous empty message
 * followed by asynchronous Inf Ack only. This Routing actor uses MESH transport
 * for sending its response
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class MESHFHIRITKRoutingActorInfAckOnly
        extends MESHFHIRITKRoutingActorSubs {

    protected static final String DESTINATIONBLOCK
            = "        <destination>\n"
            + "          <endpoint value=\"__REQUEST_SOURCE_ENDPOINT__\" />\n"
            + "        </destination>\r\n";
    protected static final String ERRORDETAILBLOCK
            = "          <details>\r\n"
            + "            <reference value=\"urn:uuid:__IA_OPERATION_OUTCOME_UUID__\" />\r\n"
            + "          </details>\r\n";
    protected static final String ERRORDETAILENTRYBLOCK
            = "  <entry>\r\n"
            + "    <fullUrl value=\"urn:uuid:__IA_OPERATION_OUTCOME_UUID__\" />\r\n"
            + "    <resource>\r\n"
            + "      <OperationOutcome>\r\n"
            + "        <id value=\"__IA_OPERATION_OUTCOME_UUID__\" />\r\n"
            + "        <meta>\r\n"
            + "          <profile value=\"https://fhir.nhs.uk/STU3/StructureDefinition/ITK-Infack-OperationOutcome-1\" />\r\n"
            + "        </meta>\r\n"
            + "        <issue>\r\n"
            + "          <!-- fatal | errror | warning | information -->\r\n"
            + "          <severity value=\"__ISSUE_SEVERITY__\" />\r\n"
            + "          <!-- level 1 : invalid | security | processing | transient | informational -->\r\n"
            + "          <code value=\"__ISSUE_CODE__\" />\r\n"
            + "          <details>\r\n"
            + "            <coding>\r\n"
            + "              <system value=\"https://fhir.nhs.uk/STU3/ITK-Acknowledgement-1\" />\r\n"
            + "              <code value=\"__ERRORCODE__\" />\r\n"
            + "              <display value=\"__ERRORTEXT__\" />\r\n"
            + "            </coding>\r\n"
            + "          </details>\r\n"
            + "        </issue>\r\n"
            + "      </OperationOutcome>\r\n"
            + "    </resource>\r\n"
            + "  </entry>\r\n";

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public MESHFHIRITKRoutingActorInfAckOnly()
            throws Exception {
        super();
        init();
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        MeshRequest meshReq = (MeshRequest) obj;
        MeshMessage mm = meshReq.getRequestMeshMessage();
        String meshData = null;
        if (mm.getDataFile() != null) {
            meshData = new String(mm.getDataFile().getContent());
        }
        StringBuilder sbAppAckTemplate = new StringBuilder(appAckTemplate);
        setPriorityBlock(meshReq, sbAppAckTemplate);
        if (ia.responseCode.toLowerCase().equals("ok")) {
            substitute(sbAppAckTemplate, "__RESPONSE_DETAIL__", "");
            substitute(sbAppAckTemplate, "__RESPONSE_DETAIL_ENTRY__", "");
        } else {
            substitute(sbAppAckTemplate, "__RESPONSE_DETAIL__", ERRORDETAILBLOCK);
            substitute(sbAppAckTemplate, "__RESPONSE_DETAIL_ENTRY__", ERRORDETAILENTRYBLOCK);
        }
        boolean found = false;
        for (Substitution s : substitutions.values()) {
            if (s.getTag().equals("__REQUEST_SOURCE_ENDPOINT__")) {
                found = true;
            }
        }
        if (found) {
            substitute(sbAppAckTemplate, "__DESTINATION_BLOCK__", DESTINATIONBLOCK);
        } else {
            substitute(sbAppAckTemplate, "__DESTINATION_BLOCK__", "");
        }
        ia.substituteDetails(sbAppAckTemplate);

        // apply the substitutions from the simulator config
        String appAckSubstituted = performSimulatorConfigSubstitutions(sbAppAckTemplate.toString(), substitutions, meshData);
        MeshMessage meshMessage = new MeshMessage(mm.getMeshMailboxId());
        meshMessage.setLoggingStream(meshReq.getLoggingStream());
        String id = "";
        if (mm.getOriginatingRequestId() == null) {
            id = UUID.randomUUID().toString();
        } else {
            id = mm.getOriginatingRequestId();
        }
        String localId = "infAck-" + id;
        meshMessage.createResponse(mm.getControlFile().getFromDTS(), appAckSubstituted, mm.getControlFile().getWorkflowId(), localId);
        OpenMeshMessageRegister.getInstance().registerMeshRequest(meshMessage, localId);
        return "";
    }

}
