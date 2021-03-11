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

import ca.uhn.fhir.context.FhirContext;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptorValidator;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.SOAP_ACTION_HEADER;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceInterface;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Substitution;

/**
 * Specifically for FHIR Messaging over ITK returns synchronous empty message
 * followed by asynchronous Inf Ack and Bus Ack. This Routing actor uses HTTP
 * transport for sending its response
 * 
 * ***********
 * Not sure whether this is still current so not made it FHIR version agnostic
 * ***********
 * 
 * @author Simon Farrow simon.farrow1@nhs.net
 */
public class GenericInlineFHIRValidator
        extends RoutingActor implements EvidenceInterface {

    private static Configurator config;
    private String validationReport = null;
    private final FhirContext context = FhirContext.forDstu3();

    static {
        try {
            config = Configurator.getConfigurator();
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, GenericInlineFHIRValidator.class.getName(), "Configurator error - " + e.toString());
        }
    }

    /**
     * Public constructor
     *
     * @throws Exception
     */
    public GenericInlineFHIRValidator()
            throws Exception {
        super();
        init();
    }

    @Override
    public String makeResponse(HashMap<String, Substitution> substitutions, Object obj)
            throws Exception {
        HttpRequest httpRequest = (HttpRequest) obj;
        String soapAction = httpRequest.getField(SOAP_ACTION_HEADER);
        HttpInterceptorValidator hiv = new HttpInterceptorValidator(config, soapAction, null);

        // Send the simulator evidence and validation report (if available) to the Scheduler
        hiv.registerForReport((EvidenceInterface) this);

        hiv.validateRequest(httpRequest, null);

        OperationOutcome oo = new OperationOutcome();
        oo.addIssue()
                .setSeverity(OperationOutcome.IssueSeverity.INFORMATION)
                .setCode(OperationOutcome.IssueType.INFORMATIONAL)
                .setDiagnostics(validationReport);

        /*
            oo.Meta meta = new Meta();
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
         */
        String ooResults = context.newXmlParser().setPrettyPrint(true).encodeResourceToString(oo);
        return ooResults;

    }

    @Override
    public void setValidationReport(String s) {
        validationReport = s;
    }

}
