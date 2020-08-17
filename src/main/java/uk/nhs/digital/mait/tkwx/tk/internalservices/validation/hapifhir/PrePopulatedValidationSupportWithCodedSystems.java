/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import java.util.List;
import org.hl7.fhir.dstu3.hapi.validation.PrePopulatedValidationSupport;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.utilities.validation.ValidationMessage;

/**
 *
 * @author riro
 */
public class PrePopulatedValidationSupportWithCodedSystems extends PrePopulatedValidationSupport {

    @Override
    public <T extends IBaseResource> T fetchResource(FhirContext theContext, Class<T> theClass, String theUri) {
            System.out.println("Fetch resource = " + theUri);
        if (theClass.equals(StructureDefinition.class)) {
            return super.fetchResource(theContext, theClass, theUri);
        }
        if (theClass.equals(ValueSet.class)) {

            return super.fetchResource(theContext, theClass, theUri);
        }
        if (theClass.equals(CodeSystem.class)) {
            return super.fetchResource(theContext, theClass, theUri);
        }
        if (theUri.startsWith("http://hl7.org/fhir/STU3/ValueSet/")) {
            System.out.println("Fetch resource (ValueSet) = " + theUri);
        }
        System.out.println("Cant Fetch resource = " + theUri);
        return null;
    }

    @Override
    public boolean isCodeSystemSupported(FhirContext theContext, String theSystem) {
        CodeSystem cs = fetchCodeSystem(theContext, theSystem);
        if (cs != null && cs.getContent() != CodeSystem.CodeSystemContentMode.NOTPRESENT) {
            System.out.println("isCodeSystemSupported " + theSystem + ", returned true");
            return true;
        }
        System.out.println("isCodeSystemSupported " + theSystem + ", returned false");
        return false;
    }

    @Override
    public CodeValidationResult validateCode(FhirContext theContext, String theCodeSystem, String theCode, String theDisplay, String valueSetUrl) {
        System.out.print("validateCode " + theCodeSystem + ", " + theCode + ", " + theDisplay + ", " + valueSetUrl);

        CodeSystem cs = fetchCodeSystem(theContext, theCodeSystem);
        if (cs != null) {
            boolean caseSensitive = true;
            if (cs.hasCaseSensitive()) {
                caseSensitive = cs.getCaseSensitive();
            }

            CodeValidationResult retVal = testIfConceptIsInList(theCode, cs.getConcept(), caseSensitive);

            if (retVal != null) {
                System.out.println(", found code");
                return retVal;
            }
        }
        System.out.println(", cant find code");

        return new CodeValidationResult(ValidationMessage.IssueSeverity.WARNING, "Unknown code: " + theCodeSystem + " / " + theCode);
    }

    private CodeValidationResult testIfConceptIsInList(String theCode, List<CodeSystem.ConceptDefinitionComponent> conceptList, boolean theCaseSensitive) {
        String code = theCode;
        if (theCaseSensitive == false) {
            code = code.toUpperCase();
        }

        return testIfConceptIsInListInner(conceptList, theCaseSensitive, code);
    }

    private CodeValidationResult testIfConceptIsInListInner(List<CodeSystem.ConceptDefinitionComponent> conceptList, boolean theCaseSensitive, String code) {
        CodeValidationResult retVal = null;
        for (CodeSystem.ConceptDefinitionComponent next : conceptList) {
            String nextCandidate = next.getCode();
            if (theCaseSensitive == false) {
                nextCandidate = nextCandidate.toUpperCase();
            }
            if (nextCandidate.equals(code)) {
                retVal = new CodeValidationResult(next);
                break;
            }

            // recurse
            retVal = testIfConceptIsInList(code, next.getConcept(), theCaseSensitive);
            if (retVal != null) {
                break;
            }
        }

        return retVal;
    }

}
