/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.validation.ValidationResult;
import java.util.HashMap;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 *
 * @author riro
 */
public interface HapiAssetCacheInterface {

    void addAll(String d) throws Exception;

    public void setProfileVersionFileName(String name);

    HashMap<String, IBaseResource> getCodeSystemIBaseResourceCache();

    HashMap<String, IBaseResource> getStructureDefinitionIBaseResourceCache();

    HashMap<String, IBaseResource> getValueSetIBaseResourceCache();

    String convertValidationResultToOOString(ValidationResult vr, HapiFhirValidatorEngine hfve);

    public String getRebuildBusyOOMessage();

    public String getRebuildSuccessOOMessage();

}
