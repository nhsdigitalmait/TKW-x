/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.util.ClasspathUtil;
import java.io.File;
import java.io.FileInputStream;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.utilities.npm.NpmPackage;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;

/**
 * This interceptor loads and parses FHIR NPM Conformance Packages, and makes
 * the artifacts foudn within them available to the FHIR validator.
 *
 * @since 5.5.0
 */
public class CustomNpmPackageValidationSupport extends PrePopulatedValidationSupport {

    /**
     * Constructor
     */
    public CustomNpmPackageValidationSupport(@Nonnull FhirContext theFhirContext) {
        super(theFhirContext);
    }

    /**
     * Load an NPM package using a fileName specification, e.g.
     * <code>/disk/path/to/resource/my_package.tgz</code>. 
     *
     * @throws InternalErrorException If the fileName file can't be found
     */
    public void loadPackageFromDisk(String fileName) throws IOException {
        try ( InputStream is = new FileInputStream(new File(fileName))) {
            NpmPackage pkg = NpmPackage.fromPackage(is);
            if (pkg.getFolders().containsKey("package")) {
                NpmPackage.NpmPackageFolder packageFolder = pkg.getFolders().get("package");

                for (String nextFile : packageFolder.listFiles()) {
                    if (nextFile.toLowerCase(Locale.US).endsWith(".json")) {
                        String input = new String(packageFolder.getContent().get(nextFile), StandardCharsets.UTF_8);
                        IBaseResource resource = getFhirContext().newJsonParser().parseResource(input);
                        super.addResource(resource);
                    }
                }

            }
        }
    }


}
