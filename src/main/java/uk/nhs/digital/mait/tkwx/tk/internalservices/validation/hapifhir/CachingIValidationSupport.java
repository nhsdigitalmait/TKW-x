/*
 Copyright 2018 Richard Robinon rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import org.hl7.fhir.instance.model.api.IBaseResource;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.Constants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.dstu3.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.CodeType;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.dstu3.model.ValueSet.ValueSetExpansionComponent;
import org.hl7.fhir.dstu3.terminologies.ValueSetExpander;
import org.hl7.fhir.dstu3.terminologies.ValueSetExpanderSimple;
import org.hl7.fhir.utilities.validation.ValidationMessage;

/**
 * Class used to intercept calls to validate from Hapi FHIR validator which
 * allows bespoke assets to be inserted and used for validation
 *
 * The asset types which are cached and inserted are:
 *
 * Structure Definitions, Code Systems, Value Sets
 *
 * This code borrows from/is based upon the DefaultProfileValidationSupport
 * (@/org/hl7/fhir/dstu3/hapi/validation/DefaultProfileValidationSupport.java)
 * and from the Hapi FHIR validation documentation
 *
 * @http://hapifhir.io/doc_validation.html
 *
 * @author Richard Robinson
 */
public class CachingIValidationSupport implements IValidationSupport {

    private HapiAssetCache profileCache = null;
    private static final String URL_PREFIX_VALUE_SET = "http://hl7.org/fhir/STU3/ValueSet/";

    public CachingIValidationSupport() {
        super();
    }

    public void setProfileCache(HapiAssetCache p) {
        profileCache = p;
    }

    /**
     * Loads a resource needed by the validation (a StructureDefinition, or a
     * ValueSet)
     *
     * @param <T>
     * @param theContext The HAPI FHIR Context object current in use by the
     * validator
     * @param theClass The type of the resource to load
     * @param theUri The resource URI
     * @return Returns the resource, or <code>null</code> if no resource with
     * the given URI can be found
     */
    @Override
    public <T extends IBaseResource> T fetchResource(FhirContext theContext, Class<T> theClass, String theUri) {
        Validate.notBlank(theUri, "theUri must not be null or blank");

        if (theClass.equals(StructureDefinition.class)) {
            System.out.println("Fetch resource (StructureDefinition) = " + theUri);
            return (T) fetchStructureDefinition(theContext, theUri);
        }

        if (theClass.equals(ValueSet.class) || theUri.startsWith(URL_PREFIX_VALUE_SET)) {
            System.out.println("Fetch resource (ValueSet) = " + theUri);
            return (T) profileCache.getValueSet(theUri);
        }

        System.out.println("Fetch resource failed = " + theUri);
        return null;
    }

    /**
     * Validates that the given code exists and if possible returns a display
     * name. This method is called to check codes which are found in "example"
     * binding fields (e.g. <code>Observation.code</code> in the default
     * profile.
     *
     * @param theContext
     * @param theCodeSystem The code system, e.g.
     * "<code>http://loinc.org</code>"
     * @param theCode The code, e.g. "<code>1234-5</code>"
     * @param theDisplay The display name, if it should also be validated
     * @return Returns a validation result object
     */
    @Override
    public CodeValidationResult validateCode(FhirContext theContext, String theCodeSystem, String theCode, String theDisplay, String theValueSetUrl) {
        System.out.print("validateCode " + theCodeSystem + ", " + theCode + ", " + theDisplay + ", " + theValueSetUrl);
        if (theCodeSystem != null) {
            CodeSystem cs = fetchCodeSystem(theContext, theCodeSystem);
            if (cs != null) {
                boolean caseSensitive = true;
                if (cs.hasCaseSensitive()) {
                    caseSensitive = cs.getCaseSensitive();
                }

                CodeValidationResult retVal = testIfConceptIsInList(cs, theCode, cs.getConcept(), caseSensitive);

                if (retVal != null) {
                    System.out.println(", found code");
                    return retVal;
                }
            }
        }

        System.out.println(", cant find code");
        return new CodeValidationResult(ValidationMessage.IssueSeverity.WARNING, "Unknown code: " + theCodeSystem + " / " + theCode);
    }
//
//        CodeSystem cs = fetchCodeSystem(theContext, theCodeSystem);
//        if (cs != null) {
//            boolean caseSensitive = true;
//            if (cs.hasCaseSensitive()) {
//                caseSensitive = cs.getCaseSensitive();
//            }
//
//            CodeValidationResult retVal = testIfConceptIsInList(cs, theCode, cs.getConcept(), caseSensitive);
//
//            if (retVal != null) {
//                System.out.println(", found code");
//                return retVal;
//            }
//        }
//
//        System.out.println(", cant find code");
//        return new CodeValidationResult(ValidationMessage.IssueSeverity.WARNING, "Unknown code: " + theCodeSystem + " / " + theCode);
//    }

    /**
     * Fetch a code system by ID
     *
     * @param theContext
     * @param theSystem The code system
     * @return The valueset (must not be null, but can be an empty ValueSet)
     */
    @Override
    public CodeSystem fetchCodeSystem(FhirContext theContext, String theSystem) {
        System.out.println("CodeSystem = " + theSystem);
        CodeSystem cs = profileCache.getCodeSystem(theSystem);
        if (cs == null) {
//            System.out.println("CodeSystem NULL = " + theSystem);
        } else {
//            System.out.println("CodeSystem FOUND = " + theSystem);
        }
        return cs;
    }

    /**
     * Expands the given portion of a ValueSet
     *
     * @param fc
     * @param csc The portion to include
     * @return The expansion
     */
    @Override
    public ValueSet.ValueSetExpansionComponent expandValueSet(FhirContext fc, ValueSet.ConceptSetComponent csc) {
        System.out.println("expandValueSet");
        ValueSetExpansionComponent retVal = new ValueSetExpansionComponent();

        Set<String> wantCodes = new HashSet<String>();
        for (ValueSet.ConceptReferenceComponent next : csc.getConcept()) {
            wantCodes.add(next.getCode());
        }

        CodeSystem system = fetchCodeSystem(fc, csc.getSystem());
        /* Previous Code
        for (ConceptDefinitionComponent next : system.getConcept()) {
            if (wantCodes.isEmpty() || wantCodes.contains(next.getCode())) {
                retVal
                        .addContains()
                        .setSystem(csc.getSystem())
                        .setCode(next.getCode())
                        .setDisplay(next.getDisplay());
            }
        }
         */
        if (system != null) {
            List<ConceptDefinitionComponent> concepts = system.getConcept();
            addConcepts(csc, retVal, wantCodes, concepts);
        }

        for (UriType next : csc.getValueSet()) {
            ValueSet vs = profileCache.getValueSet(defaultString(next.getValueAsString()));
            if (vs != null) {
                for (ValueSet.ConceptSetComponent nextInclude : vs.getCompose().getInclude()) {
                    ValueSetExpansionComponent contents = expandValueSet(fc, nextInclude);
                    retVal.getContains().addAll(contents.getContains());
                }
            }
        }
//        System.out.println("expand ValueSet "+ retVal.getIdentifier());
        return retVal;
    }

    private void addConcepts(ValueSet.ConceptSetComponent theInclude, ValueSetExpansionComponent theRetVal, Set<String> theWantCodes, List<ConceptDefinitionComponent> theConcepts) {
        for (ConceptDefinitionComponent next : theConcepts) {
            if (theWantCodes.isEmpty() || theWantCodes.contains(next.getCode())) {
                theRetVal
                        .addContains()
                        .setSystem(theInclude.getSystem())
                        .setCode(next.getCode())
                        .setDisplay(next.getDisplay());
            }
            addConcepts(theInclude, theRetVal, theWantCodes, next.getConcept());
        }
    }

    /**
     * Load and return all possible structure definitions
     *
     * @param fc
     * @return
     */
    @Override
    public List<StructureDefinition> fetchAllStructureDefinitions(FhirContext fc) {
//        System.out.println("fetchAllStructureDefinitions");
        return new ArrayList<>(profileCache.getStructureDefinitions().values());
    }

    @Override
    public StructureDefinition fetchStructureDefinition(FhirContext fc, String string) {
//        System.out.println("fetchStructureDefinition");
        return profileCache.getStructureDefinitions().get(string);
    }

    /**
     * Returns <code>true</code> if codes in the given code system can be
     * expanded or validated
     *
     * @param string The URI for the code system, e.g.
     * <code>"http://loinc.org"</code>
     * @return Returns <code>true</code> if codes in the given code system can
     * be validated
     */
    @Override
    public boolean isCodeSystemSupported(FhirContext fc, String string) {
        CodeSystem cs = fetchCodeSystem(fc, string);
        if (cs != null && cs.getContent() != CodeSystem.CodeSystemContentMode.NOTPRESENT) {
            System.out.println("isCodeSystemSupported " + string + ", returned true");
            return true;
        }
        System.out.println("isCodeSystemSupported " + string + ", returned false");
        return false;
    }

    /**
     * Load and return all conformance resources associated with this validation
     * support module. This method may return null if it doesn't make sense for
     * a given module.
     */
    @Override
    public List<IBaseResource> fetchAllConformanceResources(FhirContext fc) {
//        System.out.println("fetchAllConformanceResources");
        ArrayList<IBaseResource> retVal = new ArrayList<>();
        retVal.addAll(new ArrayList<>(profileCache.getResourceCache().values()));
        return retVal;
    }

    private CodeValidationResult testIfConceptIsInList(CodeSystem theCodeSystem, String theCode, List<CodeSystem.ConceptDefinitionComponent> conceptList, boolean theCaseSensitive) {
        String code = theCode;
        if (theCaseSensitive == false) {
            code = code.toUpperCase();
        }

        return testIfConceptIsInListInner(theCodeSystem, conceptList, theCaseSensitive, code);
    }

    private CodeValidationResult testIfConceptIsInListInner(CodeSystem theCodeSystem, List<CodeSystem.ConceptDefinitionComponent> conceptList, boolean theCaseSensitive, String code) {
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
            retVal = testIfConceptIsInList(theCodeSystem, code, next.getConcept(), theCaseSensitive);
            if (retVal != null) {
                break;
            }
        }
        if (retVal != null) {
            retVal.setCodeSystemName(theCodeSystem.getName());
            retVal.setCodeSystemVersion(theCodeSystem.getVersion());
        }

        return retVal;
    }

    @Override
    public ValueSet fetchValueSet(FhirContext fhirContext, String string) {
        return profileCache.getValueSet(string);
    }

    @Override
    public LookupCodeResult lookupCode(FhirContext theContext, String theSystem, String theCode) {
        return validateCode(theContext, theSystem, theCode, null, (String) null).asLookupCodeResult(theSystem, theCode);
    }

    @Override
    public StructureDefinition generateSnapshot(StructureDefinition theInput, String theUrl, String theName) {
        return null;
    }

}