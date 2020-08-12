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

import ca.uhn.fhir.validation.ValidationResult;
import java.util.HashMap;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Resource;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir.HapiFhirValidatorEngine;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir.HapiFhirValidatorEngineOrchestrator;

/**
 * Specifically for FHIR Messaging over ITK returns synchronous empty message
 * followed by asynchronous Inf Ack and Bus Ack. This Routing actor uses HTTP
 * transport for sending its response
 *
 * @author Simon Farrow simon.farrow1@nhs.net
 */
public class FHIRITKInlineValidator
        extends RoutingActor {

    private HapiFhirValidatorEngine hfvEngine = null;
    private final HapiFhirValidatorEngineOrchestrator orchestrator = HapiFhirValidatorEngineOrchestrator.getInstance();

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public FHIRITKInlineValidator()
            throws Exception {
        super();
        init();
        initialise(null);
    }

    public FHIRITKInlineValidator(String hapiFhirValidatorInstanceName)
            throws Exception {
        super();
        init();
        initialise(hapiFhirValidatorInstanceName);
    }

    private void initialise(String hapiFhirValidatorInstanceName) {
        hfvEngine = orchestrator.getEngine(hapiFhirValidatorInstanceName);
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        HttpRequest httpRequest = (HttpRequest) obj;
        ValidationResult result = hfvEngine.validate(new String(httpRequest.getBody()));
        OperationOutcome oo = (OperationOutcome) result.toOperationOutcome();
        Meta meta = new Meta();
        Coding hapiVersion = new Coding();
        hapiVersion.setVersion(hfvEngine.getSoftwareVersion());
        hapiVersion.setSystem("urn:nhs:digital:fhir:validator:version");
        meta.addTag(hapiVersion);
        Coding profileVersion = new Coding();
        profileVersion.setVersion(hfvEngine.getProfileVersion());
        profileVersion.setSystem("urn:nhs:digital:fhir:profile:version");
        meta.addTag(profileVersion);
        Resource resource = (Resource) oo;
        resource.setMeta(meta);
        String ooResults = hfvEngine.getContext().newXmlParser().setPrettyPrint(true).encodeResourceToString(oo);
        return ooResults;
    }
}
