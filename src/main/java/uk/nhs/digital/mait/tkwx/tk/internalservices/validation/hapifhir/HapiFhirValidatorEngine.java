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
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import static uk.nhs.digital.mait.tkwx.util.Utils.isYDefaultY;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import java.net.MalformedURLException;
import java.net.URL;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.RemoteTerminologyServiceValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;

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
    private final String INCLUDEVALIDATIONSUPPORTMODULE = ".includevalidationsupportmodule";
    private final String PREPOPULATED = ".prepopulated";
    private final String INMEMORYTERMINOLOGYSERVER = ".inmemoryterminologyserver";
    private final String COMMONCODESYSTEMSTERMINOLOGYSERVICE = ".commoncodesystemsterminologyservice";
    private final String REMOTETERMINOLOGYSERVICEURL = ".remoteterminologyserviceurl";
    private final String FHIRVERSION = ".fhir.version";
    public final int INFORMATION = 0;
    public final int WARNING = 1;
    public final int ERROR = 2;
    public final int FATAL = 3;
    private boolean strictParserValidation = false;
    private boolean schemaValidate = false;
    private boolean schematronValidate = false;
    private boolean noTerminologyChecks = false;
    private boolean prepopulatedValidationSupport = true;
    private boolean inMemoryTerminologyServerValidationSupport = true;
    private boolean commonCodeSystemTerminologyServiceValidationSupport = true;
    private String remoteTerminologyServiceUrl = null;
    private int minimumReportLevel = 0;
    private FhirContext context;
    private String softwareVersion;
    private String profileVersion;
    private Configurator config;
    private CustomHapiFhirErrorHandler customHapiFhirErrorHandler = new CustomHapiFhirErrorHandler();
    private HapiAssetCacheInterface hapiAssetCacheInterface = null;
    private FhirVersionEnum fhirVersionEnum = null;

    public String getRebuildBusyOOMessage() {
        return hapiAssetCacheInterface.getRebuildBusyOOMessage();
    }

    public String getRebuildSuccessOOMessage() {
        return hapiAssetCacheInterface.getRebuildSuccessOOMessage();
    }



    enum FhirVersionEnum {
        STU3,
        R4,
        R5
    }

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
            prepopulatedValidationSupport = isYDefaultY(config.getConfiguration(HapiFhirInstancePath + INCLUDEVALIDATIONSUPPORTMODULE + PREPOPULATED));
            inMemoryTerminologyServerValidationSupport = isYDefaultY(config.getConfiguration(HapiFhirInstancePath + INCLUDEVALIDATIONSUPPORTMODULE + INMEMORYTERMINOLOGYSERVER));
            commonCodeSystemTerminologyServiceValidationSupport = isYDefaultY(config.getConfiguration(HapiFhirInstancePath + INCLUDEVALIDATIONSUPPORTMODULE + COMMONCODESYSTEMSTERMINOLOGYSERVICE));

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
            remoteTerminologyServiceUrl = config.getConfiguration(HapiFhirInstancePath + INCLUDEVALIDATIONSUPPORTMODULE + REMOTETERMINOLOGYSERVICEURL);
            if (remoteTerminologyServiceUrl == null || remoteTerminologyServiceUrl.trim().equals("")) {
                remoteTerminologyServiceUrl = null;
            } else {
                try {
                    URL url = new URL(remoteTerminologyServiceUrl);
                } catch (MalformedURLException m) {
                    remoteTerminologyServiceUrl = null;
                    Logger.getInstance().log(java.util.logging.Level.WARNING, HapiFhirValidatorEngine.class.getName(), "Terminology Server URL is Malformed: " + m.getMessage());
                }
            }
            //Find out which FHIR version the validator is expecting
            String fhirVersion = config.getConfiguration(HapiFhirInstancePath + FHIRVERSION);

            if (!isNullOrEmpty(fhirVersion) && fhirVersion.toLowerCase().equals("r5")) {
                fhirVersionEnum = FhirVersionEnum.R5;
                context = FhirContext.forR5();
                System.out.println("HAPI FHIR Validator R5 selected");
            } else if (!isNullOrEmpty(fhirVersion) && fhirVersion.toLowerCase().equals("r4")) {
                 fhirVersionEnum = FhirVersionEnum.R4;
               context = FhirContext.forR4();
                System.out.println("HAPI FHIR Validator R4 selected");
            } else if (isNullOrEmpty(fhirVersion)||fhirVersion.toLowerCase().equals("stu3")) {
                fhirVersionEnum = FhirVersionEnum.STU3;
                context = FhirContext.forDstu3();
                System.out.println("HAPI FHIR Validator STU3 selected");
            }

            context.setParserErrorHandler(customHapiFhirErrorHandler);

            ValidationSupportChain supportChain = new ValidationSupportChain();

            DefaultProfileValidationSupport defaultProfileValidationSupport = new DefaultProfileValidationSupport(context);
            supportChain.addValidationSupport(defaultProfileValidationSupport);

            if (prepopulatedValidationSupport) {
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
                            profileVersion = jsonObject.get("name").getAsString() + " " + jsonObject.get("version").getAsString();
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
             hapiAssetCacheInterface = null;
            if( null!=fhirVersionEnum)switch (fhirVersionEnum) {
                    case R5:
                        hapiAssetCacheInterface = new HapiAssetCacheR5(context, ignoreList);
                        break;
                    case R4:
                        hapiAssetCacheInterface = new HapiAssetCacheR4(context, ignoreList);
                        break; 
                    case STU3:
                        hapiAssetCacheInterface = new HapiAssetCacheStu3 (context, ignoreList);
                        break;
                    default:
                        hapiAssetCacheInterface = new HapiAssetCacheStu3 (context, ignoreList);
                        break;
                }
                            
                //set the profile version so that it can be ignored in the loading of the profiles
                hapiAssetCacheInterface.setProfileVersionFileName(pVersion);
                //find what sort of config has been used to define the asset directory and load accordingly
                String sp = config.getConfiguration(HapiFhirInstancePath + ASSETDIR);
                if (sp != null && sp.trim().length() > 0) {
                    hapiAssetCacheInterface.addAll(sp);
                } else {
                    int i = 0;
                    while (true) {
                        sp = config.getConfiguration(HapiFhirInstancePath + ASSETDIR + "." + i);
                        if ((sp == null) || (sp.trim().length() == 0)) {
                            break;
                        } else {
                            hapiAssetCacheInterface.addAll(sp);
                        }
                        i++;
                    }
                    if (i == 0) {
                        Logger log = Logger.getInstance();
                        log.log("No HAPI FHIR assets read in");
                        System.out.println("No HAPI FHIR assets read in");
                    }
                }
                PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport(context, hapiAssetCacheInterface.getStructureDefinitionIBaseResourceCache(), hapiAssetCacheInterface.getValueSetIBaseResourceCache(), hapiAssetCacheInterface.getCodeSystemIBaseResourceCache());
                supportChain.addValidationSupport(prePopulatedSupport);
            }

            if (inMemoryTerminologyServerValidationSupport) {
                InMemoryTerminologyServerValidationSupport inMemoryTerminologyServer = new InMemoryTerminologyServerValidationSupport(context);
                supportChain.addValidationSupport(inMemoryTerminologyServer);
            }
            if (commonCodeSystemTerminologyServiceValidationSupport) {
                CommonCodeSystemsTerminologyService codeSystemsTerminologyService = new CommonCodeSystemsTerminologyService(context);
                supportChain.addValidationSupport(codeSystemsTerminologyService);
            }
            SnapshotGeneratingValidationSupport generatingValidationSupport = new SnapshotGeneratingValidationSupport(context);
            supportChain.addValidationSupport(generatingValidationSupport);

// Create a module that uses a remote terminology service
            if (remoteTerminologyServiceUrl != null) {
                RemoteTerminologyServiceValidationSupport remoteTermSvc = new RemoteTerminologyServiceValidationSupport(context);
                remoteTermSvc.setBaseUrl(remoteTerminologyServiceUrl);
                supportChain.addValidationSupport(remoteTermSvc);
            }
            CachingValidationSupport cachingValidationSupport = new CachingValidationSupport(supportChain);

            FhirInstanceValidator fhirInstanceValidator = new FhirInstanceValidator(cachingValidationSupport);
//            fhirInstanceValidator.setNoTerminologyChecks(noTerminologyChecks);

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
//            if (schemaValidate) {
//                IValidatorModule module1 = new SchemaBaseValidator(context);
//                fhirValidator.registerValidatorModule(module1);
//            }
//            if (schematronValidate) {
//                IValidatorModule module2 = new SchematronBaseValidator(context);
//                fhirValidator.registerValidatorModule(module2);
//            }

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

    public String validateWithStringOOResult(String o) throws Exception {
        ValidationResult result = validate(o);
        return hapiAssetCacheInterface.convertValidationResultToOOString(result, this);
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

    public boolean isPrepopulatedValidationSupport() {
        return prepopulatedValidationSupport;
    }

    public boolean isInMemoryTerminologyServerValidationSupport() {
        return inMemoryTerminologyServerValidationSupport;
    }

    public boolean isCommonCodeSystemTerminologyServiceValidationSupport() {
        return commonCodeSystemTerminologyServiceValidationSupport;
    }

    public String getRemoteTerminologyServiceUrl() {
        return remoteTerminologyServiceUrl;
    }
    String getFhirVersion() {
        return fhirVersionEnum.toString();
    }
}
