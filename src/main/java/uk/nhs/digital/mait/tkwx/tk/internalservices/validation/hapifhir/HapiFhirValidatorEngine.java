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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.SchemaBaseValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import ca.uhn.fhir.validation.schematron.SchematronBaseValidator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.hl7.fhir.dstu3.hapi.ctx.DefaultProfileValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.dstu3.hapi.validation.PrePopulatedValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.ValidationSupportChain;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 *
 * @author riro
 */
public class HapiFhirValidatorEngine {

    private IParser xmlParser = null;
    private IParser jsonParser = null;
    private FhirValidator fhirValidator = null;
    private final String HAPIFHIR = "tks.validator.hapifhirvalidator";
    private final String ASSETDIR = ".assetdir";
    private final String IGNORELIST = ".ignore";
    private final String SOFTWAREVERSION = ".softwareversion";
    private final String PROFILEVERSION = ".profileversion";
    private final String PROFILEVERSIONFILELOCATION = ".profileversionfilelocation";
    private final String STRICTPARSERVALIDATION = ".strictparservalidation";
    private final String SCHEMAVALIDATE = ".schemavalidate";
    private final String SCHEMATRONVALIDATE = ".schematronvalidate";
    private final String NOTERMINOLOGYCHECKS = ".noterminologychecks";
    private final String MINIMUMREPORTLEVEL = ".minimumreportlevel";
    public final int INFORMATION = 0;
    public final int WARNING = 1;
    public final int ERROR = 2;
    public final int FATAL = 3;
    private boolean strictParserValidation = false;
    private boolean schemaValidate = false;
    private boolean schematronValidate = false;
    private boolean noTerminologyChecks = false;
    private int minimumReportLevel = 0;
    private final FhirContext context = FhirContext.forDstu3();
    private String softwareVersion;
    private String profileVersion;
    private Configurator config;
    private CustomHapiFhirErrorHandler customHapiFhirErrorHandler = new CustomHapiFhirErrorHandler();

    public HapiFhirValidatorEngine(String hapiFhirInstanceName) {
        initialise(hapiFhirInstanceName);
    }

    private void initialise(String hapiFhirInstanceName) {
        try {
            //Set HapiFhir instance
            String HapiFhirInstancePath = null;
            if (hapiFhirInstanceName != null && hapiFhirInstanceName.length() > 0) {
                HapiFhirInstancePath = HAPIFHIR + "." + hapiFhirInstanceName;
            } else {
                HapiFhirInstancePath = HAPIFHIR;
            }
            System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema", "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory");
            config = Configurator.getConfigurator();
            schemaValidate = isY(config.getConfiguration(HapiFhirInstancePath + SCHEMAVALIDATE));
            schematronValidate = isY(config.getConfiguration(HapiFhirInstancePath + SCHEMATRONVALIDATE));
            noTerminologyChecks = isY(config.getConfiguration(HapiFhirInstancePath + NOTERMINOLOGYCHECKS));
            strictParserValidation = isY(config.getConfiguration(HapiFhirInstancePath + STRICTPARSERVALIDATION));

            String c = config.getConfiguration(HapiFhirInstancePath + MINIMUMREPORTLEVEL);

            if ((c == null) || (c.trim().length() == 0)) {
                minimumReportLevel = INFORMATION;
            } else if (c.equals("INFORMATION")) {
                minimumReportLevel = INFORMATION;
            } else if (c.equals("WARNING")) {
                minimumReportLevel = WARNING;
            } else if (c.equals("ERROR")) {
                minimumReportLevel = ERROR;
            } else if (c.equals("FATAL")) {
                minimumReportLevel = FATAL;
            } else {
                minimumReportLevel = INFORMATION;
            }

            ArrayList<File> ignoreList = new ArrayList<>();
            softwareVersion = config.getConfiguration(HapiFhirInstancePath + SOFTWAREVERSION);
            if (softwareVersion == null || softwareVersion.trim().equals("")) {
                softwareVersion = "Version number not configured";
            }
            // Version number of additional FHIR profiles 
            String pVersion = config.getConfiguration(HapiFhirInstancePath + PROFILEVERSIONFILELOCATION);
            if (pVersion == null || pVersion.trim().equals("")) {
                profileVersion = config.getConfiguration(HapiFhirInstancePath + PROFILEVERSION);
                if (profileVersion == null || profileVersion.trim().equals("")) {
                    profileVersion = "Version number not configured. Either use " + PROFILEVERSION
                            + " in the properties file or " + HapiFhirInstancePath + PROFILEVERSIONFILELOCATION + " in the properties"
                            + " file referring to an external file with a colon separated profile_version in it e.g. profile_version:V1.1";
                }
            } else {
                try {
                    ignoreList.add(new File(pVersion));
                    if (pVersion.endsWith(".txt")) {
                        // assume it's a colon separated file
                        Map<String, String> profileVersionMap = Utils.readColonSeparatedFile2Map(pVersion);
                        profileVersion = profileVersionMap.get("profile_version");
                        if (profileVersion == null || profileVersion.trim().equals("")) {
                            profileVersion = "NHS FHIR Profile Version number not configured properly in " + pVersion
                                    + ". Make sure it contains a colon separated term e.g. profile_version:V1.1 ";
                        }

                    } else if (pVersion.endsWith("package.json")) {
                        //assume it's an npm package.json
                        JsonObject jsonObject = new JsonParser().parse(Utils.readFile2String(pVersion)).getAsJsonObject();
                        profileVersion = jsonObject.get("name").getAsString() + " " +jsonObject.get("version").getAsString();
                        if (profileVersion == null || profileVersion.trim().equals("")) {
                            profileVersion = "NHS FHIR Profile Version number not configured properly in " + pVersion
                                    + ". Make sure it is a valid package.json file ";
                        }
                    }

                } catch (java.nio.file.NoSuchFileException e) {
                    profileVersion = "NHS FHIR Profile Version number not configured properly in " + pVersion
                            + ". Make sure the colon separated file or package.json file exists ";
                }

            }
            // create an ignore list
            String ignore = config.getConfiguration(HapiFhirInstancePath + ASSETDIR + IGNORELIST);
            if (ignore != null) {
                try {
                    StringTokenizer st = new StringTokenizer(ignore);

                    while (st.hasMoreTokens()) {
                        File f = new File(st.nextToken());
                        ignoreList.add(f);
                    }
                } catch (Exception e) {
                    Logger.getInstance().log(java.util.logging.Level.WARNING, HapiFhirValidatorEngine.class.getName(), "One or more elements of the ignore list cannot be understood: " + e.getMessage());
                }
            }

//            context = FhirContext.forDstu3();
            context.setParserErrorHandler(customHapiFhirErrorHandler);

            HapiAssetCache p = new HapiAssetCache(context, ignoreList);
            //set the profile version so that it can be ignored in the loading of the profiles
            p.setProfileVersionFileName(pVersion);
            //find what sort of config has been used to define the asset directory and load accordingly
            String sp = config.getConfiguration(HapiFhirInstancePath + ASSETDIR);
            if (sp != null && sp.trim().length() > 0) {
                p.addAll(sp);
            } else {
                int i = 0;
                while (true) {
                    sp = config.getConfiguration(HapiFhirInstancePath + ASSETDIR + "." + i);
                    if ((sp == null) || (sp.trim().length() == 0)) {
                        break;
                    } else {
                        p.addAll(sp);
                    }
                    i++;
                }
                if (i == 0) {
                    Logger log = Logger.getInstance();
                    log.log("No HAPI FHIR assets read in");
                    System.out.println("No HAPI FHIR assets read in");
                }
            }

            DefaultProfileValidationSupport defaultProfileValidationSupport = new DefaultProfileValidationSupport();

// USING NEW PREPOPULATED            
//CHHOSE EITHER VANILLA HAPI which loads the reources in but returns null            
//            PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport();
////OR MINE WHICH ESSENTIALLY DOES THE SAME AS THE CACHING SUPPORT            
////            PrePopulatedValidationSupportWithCodedSystems prePopulatedSupport = new PrePopulatedValidationSupportWithCodedSystems();
//
//            
//            HashMap<String, StructureDefinition> sd = p.getStructureDefinitions();
//            for (StructureDefinition value : sd.values()) {
//                prePopulatedSupport.addStructureDefinition(value);
//            }
//
//            HashMap<String, CodeSystem> cs = p.getCodeSystemCache();
//            for (CodeSystem value : cs.values()) {
//                prePopulatedSupport.addCodeSystem(value);
//            }
//
//            HashMap<String, ValueSet> vs = p.getValueSetCache();
//            for (ValueSet value : vs.values()) {
//                prePopulatedSupport.addValueSet(value);
//            }
//            ValidationSupportChain supportChain = new ValidationSupportChain(defaultProfileValidationSupport, prePopulatedSupport);
// USING NEW PREPOPULATED END
// USING CACHINGIALIDATIONSUPPORT
            CachingIValidationSupport caching = new CachingIValidationSupport();
            caching.setProfileCache(p);
            ValidationSupportChain supportChain = new ValidationSupportChain(defaultProfileValidationSupport, caching);
// USING CACHINGIALIDATIONSUPPORT END



            FhirInstanceValidator fhirInstanceValidator = new FhirInstanceValidator(supportChain);
            fhirInstanceValidator.setNoTerminologyChecks(noTerminologyChecks);


            xmlParser = context.newXmlParser();
            jsonParser = context.newJsonParser();
            // set Parser Validation.
            /**
             * Parser Validation is validation at runtime during the parsing of
             * a resource. It can be used to catch input data that is impossible
             * to fit into the HAPI data model. For example, it can be used to
             * throw exceptions or display error messages if a resource being
             * parsed contains elements for which there are no appropriate
             * fields in a HAPI data structure. This is useful in order to
             * ensure that no data is being lost during parsing, but is less
             * comprehensive than resource validation against raw text data.
             */

            fhirValidator = context.newValidator();
            // Create the Schema/Schematron modules and register them. Note that
            // you might want to consider keeping these modules around as long-term
            // objects: they parse and then store schemas, which can be an expensive
            // operation.
            if (schemaValidate) {
                IValidatorModule module1 = new SchemaBaseValidator(context);
                fhirValidator.registerValidatorModule(module1);
            }
            if (schematronValidate) {
                IValidatorModule module2 = new SchematronBaseValidator(context);
                fhirValidator.registerValidatorModule(module2);
            }

            fhirValidator.registerValidatorModule(fhirInstanceValidator);
        } catch (Exception e) {
            Logger log = Logger.getInstance();
            log.log("Exception " + e.getMessage());
            System.out.println(e);
        }

        if (!customHapiFhirErrorHandler.getWarningMessages().isEmpty()) {
            Logger log = Logger.getInstance();
            for (SingleValidationMessage s : customHapiFhirErrorHandler.getWarningMessages()) {
                log.log("HapiFhirValidationEngine boot warning: " + s.toString());
            }
            customHapiFhirErrorHandler.resetWarningMessages();
        }
    }

    public synchronized ValidationResult validate(String o) throws Exception {
        IBaseResource resource = null;
        if (o.trim().startsWith("<")) {
            resource = xmlParser.parseResource(o);
        } else if (o.trim().startsWith("{")) {
            resource = jsonParser.parseResource(o);
        } else {
            throw new Exception("Message format of message to be validated cannot be determined as json or XML");
        }
        ValidationResult result = fhirValidator.validateWithResult(resource);
        List<SingleValidationMessage> svmList = new ArrayList<>();
        for (SingleValidationMessage svm : customHapiFhirErrorHandler.getWarningMessages()) {
            svmList.add(svm);
        }
        for (SingleValidationMessage svm : result.getMessages()) {
            svmList.add(svm);
        }
        customHapiFhirErrorHandler.resetWarningMessages();
        return new ValidationResult(context, svmList);
    }

    public void rebuild() {
        rebuild(null);
    }

    public void rebuild(String hapiFhirInstanceName) {
        initialise(hapiFhirInstanceName);
    }

    public int getMinimumReportLevel() {
        return minimumReportLevel;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public String getProfileVersion() {
        return profileVersion;
    }

    public FhirContext getContext() {
        return context;
    }

}
