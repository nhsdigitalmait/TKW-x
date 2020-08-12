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

/**
 * Specifically for FHIR MESH ITK Messaging returns synchronous empty message
 * followed by asynchronous Inf Ack and Bus Ack. This Routing actor uses MESH
 * transport for sending its response
 *
 * @author Richard Robinson rrobinson@nhs.net
 */
public class MESHFHIRITKRoutingActor
        extends MESHFHIRITKRoutingActorInfAckOnly {

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public MESHFHIRITKRoutingActor()
            throws Exception {
        super();
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        super.makeResponse(substitutions, obj);
        MeshRequest meshReq = (MeshRequest) obj;
        MeshMessage mm = meshReq.getRequestMeshMessage();
        String meshData = new String(mm.getDataFile().getContent());
        StringBuilder sbBusAckTemplate = new StringBuilder(busAckTemplate);

        setPriorityBlock(meshReq, sbBusAckTemplate);
        ba.substituteDetails(sbBusAckTemplate);

        // apply the substitutions from the simulator config
        String busAckSubstituted = performSimulatorConfigSubstitutions(sbBusAckTemplate.toString(), substitutions, meshData);

        MeshMessage meshMessage = new MeshMessage(mm.getMeshMailboxId());
        meshMessage.setLoggingStream(meshReq.getLoggingStream());
        String id = "";
        if(mm.getOriginatingRequestId()==null){
            id = UUID.randomUUID().toString();
        } else{
            id = mm.getOriginatingRequestId();
        }
        String localId = "busAck-" + id;
        meshMessage.createResponse(mm.getControlFile().getFromDTS(), busAckSubstituted, mm.getControlFile().getWorkflowId(),localId);
        OpenMeshMessageRegister.getInstance().registerMeshRequest(meshMessage, localId);
        return "";
    }
}
