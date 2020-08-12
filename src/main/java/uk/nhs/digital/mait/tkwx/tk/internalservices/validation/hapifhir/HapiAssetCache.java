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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.ConceptMap;
import org.hl7.fhir.dstu3.model.NamingSystem;
import org.hl7.fhir.dstu3.model.OperationDefinition;
import org.hl7.fhir.dstu3.model.SearchParameter;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.nhs.digital.mait.tkwx.util.Utils;

/**
 * Class to load from disk and store in memory bespoke assets which can then be
 * used by the CachingIValidationSupport class
 *
 * @author Richard Robinson
 */
public class HapiAssetCache {

    private static final int UNKNOWN_FORMAT = 0;
    private static final int JSON_FORMAT = 1;
    private static final int XML_FORMAT = 2;
    private static final int UNKNOWN_TYPE = 0;
//    private static final int STRUCTUREDEFINITION_TYPE = 1;
//    private static final int VALUESET_TYPE = 2;
//    private static final int CODESYSTEM_TYPE = 3;
//    private static final int CAPABILITYSTATEMENT_TYPE = 4;
//    private static final int CONCEPTMAP_TYPE = 5;
//    private static final int OPERATIONDEFINITION_TYPE = 6;
//    private static final int NAMINGSYSTEM_TYPE = 7;
//    private static final int SEARCHPARAMETER_TYPE = 8;

    private final HashMap<String, IBaseResource> resourceCache = new HashMap<>();
    private final HashMap<String, StructureDefinition> StructureDefinitionCache = new HashMap<>();
    private final HashMap<String, ValueSet> valueSetCache = new HashMap<>();
    private final HashMap<String, CodeSystem> codeSystemCache = new HashMap<>();
    private final HashMap<String, CapabilityStatement> capabilityStatementCache = new HashMap<>();
    private final HashMap<String, ConceptMap> conceptMapCache = new HashMap<>();
    private final HashMap<String, OperationDefinition> operationDefinitionCache = new HashMap<>();
    private final HashMap<String, NamingSystem> namingSystemCache = new HashMap<>();
    private final HashMap<String, SearchParameter> searchParameterCache = new HashMap<>();
    private FhirContext context = null;
    private int fileCount = 0;
    private String profileVersionFileName = null;
    private ArrayList<File> ignoreList;

    public HapiAssetCache(ArrayList<File> ignoreList) {
        super();
        context = FhirContext.forDstu3();
        this.ignoreList = ignoreList;
    }

    public HapiAssetCache(FhirContext c, ArrayList<File> ignoreList) {
        super();
        context = c;
        this.ignoreList = ignoreList;
    }

    public IBaseResource getResource(String uri) {
        return resourceCache.get(uri);
    }

    public HashMap<String, StructureDefinition> getStructureDefinitions() {
        return StructureDefinitionCache;
    }

    public HashMap<String, IBaseResource> getResourceCache() {
        return resourceCache;
    }

    public HashMap<String, ValueSet> getValueSetCache() {
        return valueSetCache;
    }

    public HashMap<String, CodeSystem> getCodeSystemCache() {
        return codeSystemCache;
    }

    public HashMap<String, CapabilityStatement> getCapabilityStatementCache() {
        return capabilityStatementCache;
    }

    public HashMap<String, ConceptMap> getConceptMapCache() {
        return conceptMapCache;
    }

    public HashMap<String, OperationDefinition> getOperationDefinitionCache() {
        return operationDefinitionCache;
    }

    public HashMap<String, NamingSystem> getNamingSystemCache() {
        return namingSystemCache;
    }

    public HashMap<String, SearchParameter> getSearchParameterCache() {
        return searchParameterCache;
    }

    public ValueSet getValueSet(String system) {
        return valueSetCache.get(system);
    }

    public CodeSystem getCodeSystem(String system) {
        return codeSystemCache.get(system);
    }

    public CapabilityStatement getCapabilityStatement(String system) {
        return capabilityStatementCache.get(system);
    }

    public ConceptMap getConceptMap(String system) {
        return conceptMapCache.get(system);
    }

    public NamingSystem getNamingSystem(String system) {
        return namingSystemCache.get(system);
    }

    public SearchParameter getSearchParameter(String system) {
        return searchParameterCache.get(system);
    }

    public FhirContext getFhirContext() {
        return context;
    }

    public void addFile(String f)
            throws Exception {
        if (!ignore(f)) {
            String s = Utils.readFile2String(f);
            addResource(s);
            fileCount++;
        }
    }

    public void addAll(String d)
            throws Exception {
        File dir = new File(d);
        if (!dir.isDirectory()) {
//            if (profileVersionFileName != null && d.equals(profileVersionFileName)) {
//                return;
//            }
            addFile(d);
            return;
        }
        if (!ignore(d)) {
            File[] files = dir.listFiles();
            for (File f : files) {
//            System.out.println("Read in file= " + f.getAbsolutePath());
                addAll(f.getAbsolutePath());
            }
        }
//        System.out.println("files read: " + fileCount);
    }

    public boolean ignore(String r) {
        boolean result = false;
        File f = new File(r);
        for (File ignore : ignoreList) {
            if (f.equals(ignore)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void addResource(String r)
            throws Exception {
        int f = getFormat(r);
//        int t = getType(r);
        IParser parser = null;

//        if (t == UNKNOWN_TYPE) {
//            throw new IllegalArgumentException("Cannot recognise resource type");
//        }
        switch (f) {

            case XML_FORMAT:
                parser = context.newXmlParser();
                break;

            case JSON_FORMAT:
                parser = context.newJsonParser();
                break;

            case UNKNOWN_FORMAT:
            default:
                throw new IllegalArgumentException("Cannot recognise resource format");
        }
        try {
            IBaseResource resource = parser.parseResource(r);

//            IIdType iIdType = resource.getIdElement();
//            String type = iIdType.getResourceType();
//***********************THIS IS A VARIABLE WHICH CONFERS WHICH VERSION OF FHIR THSI RESOURCE IS FOR: USEFUL!!!!******************************         
//            FhirVersionEnum enumer = resource.getStructureFhirVersionEnum();
            String url = null;

            if (resource instanceof StructureDefinition) {
                url = ((StructureDefinition) resource).getUrl();
                StructureDefinitionCache.put(url, (StructureDefinition) resource);
//                System.out.println("create StructureDefinition url= " + url);
            } else if (resource instanceof ValueSet) {
                url = ((ValueSet) resource).getUrl();
//changed for dstuv3                String id = ((ValueSet)resource).getCodeSystem().getSystem();
//                String id = ((ValueSet) resource).getCompose().getInclude().get(0).getSystem();
//                if ((id != null) && (id.trim().length() != 0) && !id.toLowerCase().contentEquals("null")) {
//                    valueSetCache.put(url, ((ValueSet) resource));
////                    valueSetCache.put(id, ((ValueSet) resource));
//                }
//                System.out.println("create ValueSet url= " + url);
                valueSetCache.put(url, ((ValueSet) resource));
            } else if (resource instanceof CodeSystem) {
                url = ((CodeSystem) resource).getUrl();
//                System.out.println("create CodeSystem url= " + url);
                codeSystemCache.put(url, (CodeSystem) resource);
            } else if (resource instanceof ConceptMap) {
                url = ((ConceptMap) resource).getUrl();
//                System.out.println("create ConceptType url= " + url);
                conceptMapCache.put(url, (ConceptMap) resource);
            } else if (resource instanceof OperationDefinition) {
                url = ((OperationDefinition) resource).getUrl();
//                System.out.println("create OperationDefinition url= " + url);
                operationDefinitionCache.put(url, (OperationDefinition) resource);
            } else if (resource instanceof CapabilityStatement) {
                url = ((CapabilityStatement) resource).getUrl();
//                System.out.println("create CapabilityStatement url= " + url);
                capabilityStatementCache.put(url, (CapabilityStatement) resource);
            } else if (resource instanceof NamingSystem) {
                // The NamingSystem doesn't have a URL. Using the name as this is described as machine processable in the spec https://www.hl7.org/fhir/namingsystem.html
                // Also I dont think that this field is used by HAPI FHIR
                String name = ((NamingSystem) resource).getName();
                namingSystemCache.put(name, (NamingSystem) resource);
            } else if (resource instanceof SearchParameter) {
                url = ((SearchParameter) resource).getUrl();
                searchParameterCache.put(url, (SearchParameter) resource);
            } else {
                throw new IllegalArgumentException("Cannot recognise resource type" + r);
            }

            resourceCache.put(url, resource);
        } catch (DataFormatException npe) {
            throw new IllegalArgumentException("Cannot recognise resource type " + npe.getMessage() + r);
        } catch (Exception npe) {
            throw new IllegalArgumentException("Cannot recognise resource type " + npe.getMessage() + r);
        }
    }

//    private int getType(String r) {
//        if ((r == null) || (r.trim().length() == 0)) {
//            return UNKNOWN_TYPE;
//        }
//
//        // TODO: This could probably do with a more robust way of determining whether what we'er about to
//        // read is a StructureDefinition or a ValueSet. But for now it'll do...
//        //
//        if (r.contains("<StructureDefinition") || r.contains("\"resourceType\":\"StructureDefinition\"")) {
//            return STRUCTUREDEFINITION_TYPE;
//        }
//        if (r.contains("<ValueSet") || r.contains("\"resourceType\":\"ValueSet\"")) {
//            return VALUESET_TYPE;
//        }
//        if (r.contains("<CodeSystem") || r.contains("\"resourceType\":\"CodeSystem\"")) {
//            return CODESYSTEM_TYPE;
//        }
//        if (r.contains("<CapabilityStatement") || r.contains("\"resourceType\":\"CapabilityStatement\"")) {
//            return CAPABILITYSTATEMENT_TYPE;
//        }
//        if (r.contains("<ConceptMap") || r.contains("\"resourceType\":\"ConceptMap\"")) {
//            return CONCEPTMAP_TYPE;
//        }
//        if (r.contains("<OperationDefinition") || r.contains("\"resourceType\":\"OperationDefinition\"")) {
//            return OPERATIONDEFINITION_TYPE;
//        }
//        if (r.contains("<NamingSystem") || r.contains("\"resourceType\":\"NamingSystem\"")) {
//            return NAMINGSYSTEM_TYPE;
//        }
//        if (r.contains("<SearchParameter") || r.contains("\"resourceType\":\"SearchParameter\"")) {
//            return SEARCHPARAMETER_TYPE;
//        }
//        return UNKNOWN_TYPE;
//    }
//
    private int getFormat(String r) {
        if ((r == null) || (r.trim().length() == 0)) {
            return UNKNOWN_FORMAT;
        }
        for (char c : r.toCharArray()) {
            if (c == '<') {
                return XML_FORMAT;
            }
            if ((c == '{') || (c == '[')) {
                return JSON_FORMAT;
            }
        }
        return UNKNOWN_FORMAT;
    }

    void setProfileVersionFileName(String name) {
        profileVersionFileName = name;
    }

}
