/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import static java.util.logging.Level.SEVERE;
import org.hl7.fhir.instance.model.api.IBaseResource;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.util.Utils.doEscape;

/**
 * Singleton performing standard fhir json <--> xml conversions
 *
 * @author simonfarrow
 */
public class FHIRJsonXmlAdapter {

    private static FHIRJsonXmlAdapter me = null;

    private IParser xmlParser = null;
    private IParser jsonParser = null;

    private static FhirVersionEnum fhirVersion;
    private static final boolean DEBUG = false;

    public static final String FHIRCONVERSIONFAILURE = "fhirconversionfailure";

    /**
     * private constructor
     */
    private FHIRJsonXmlAdapter() {
        try {
            Configurator configurator = Configurator.getConfigurator();
            fhirVersion = configurator.getConfiguration(FHIR_VERSION_PROPERTY) != null ? 
                    FhirVersionEnum.valueOf(configurator.getConfiguration(FHIR_VERSION_PROPERTY).toUpperCase()) : FhirVersionEnum.DSTU3;
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, FHIRJsonXmlAdapter.class.getName(),
                    "Error getting fhir version : " + ex.getMessage());
        }

        init();
    }

    private void init() {
        try {
            FhirContext ctx = null;
            switch (fhirVersion) {
                case DSTU2:
                    ctx = FhirContext.forDstu2();
                    break;
                case DSTU3:
                    ctx = FhirContext.forDstu3();
                    break;
                case R4:
                    ctx = FhirContext.forR4();
                    break;
                case R5:
                    ctx = FhirContext.forR5();
                    break;
                default:
                    Logger.getInstance().log(SEVERE, FHIRJsonXmlAdapter.class.getName(),
                            "Unrecognised fhir version : " + fhirVersion);
            }

            // version required on capability statement operation definition
            // see https://hapifhir.io/doc_resource_references.html
            ctx.getParserOptions().setStripVersionsFromReferences(false);
            
            xmlParser = ctx.newXmlParser();
            jsonParser = ctx.newJsonParser();
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, FHIRJsonXmlAdapter.class.getName(),
                    "Fhir converter initialisation failed for " + fhirVersion + " " + ex);
        }
    }

    private static void getInstance() {
        // lazy evaluation
        if (me == null) {
            me = new FHIRJsonXmlAdapter();
        }
    }

    public static void destroyInstance() {
        me = null;
    }

    /**
     * convert fhir xml content into canonical fhir json format Parsers are not
     * thread safe
     *
     * @param xml fhir String in xml
     * @return message fhir String in json
     */
    public static synchronized String fhirConvertXml2Json(String xml) {
        getInstance();
        String retval = "";
        if (DEBUG) {
            System.out.println("xml in = \r\n" + xml);
        }
        if (!Utils.isNullOrEmpty(xml)) {
            try {
                retval = me.xml2Json(xml);
            } catch (DataFormatException ex) {
                // escape quotes in returned fhir conversion failure message
                retval = "{ \"" + FHIRCONVERSIONFAILURE + "\" : \"" + ex.getMessage().replaceAll("\"", "\\\\\"") + "\" }";
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE, FHIRJsonXmlAdapter.class.getName(), "fhirConvertXml2Json: " + (ex.getMessage() != null ? ex.getMessage() : ex));
            }
        }
        if (DEBUG) {
            System.out.println("json out = \r\n" + retval);
        }
        return retval;
    }

    /**
     * convert fhir json content into canonical fhir xml format Parsers are not
     * thread safe In the event of a failure returns some valid xml indicating
     * the nature of the failure
     *
     * @param json
     * @return message fhir String in xml
     */
    public static synchronized String fhirConvertJson2Xml(String json) {
        getInstance();
        String retval = "";
        if (DEBUG) {
            System.out.println("json in = \r\n" + json);
        }
        if (!Utils.isNullOrEmpty(json)) {
            try {
                retval = me.json2Xml(json);
            } catch (DataFormatException ex) {
                // escape quotes etc in returned fhir conversion failure message
                retval = "<" + FHIRCONVERSIONFAILURE + ">" + doEscape(ex.getMessage()) + "</" + FHIRCONVERSIONFAILURE + ">";
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE, FHIRJsonXmlAdapter.class.getName(), "fhirConvertJson2Xml: " + (ex.getMessage() != null ? ex.getMessage() : ex));
            }
        }
        if (DEBUG) {
            System.out.println("xml out = \r\n" + retval);
        }
        return retval;
    }

    /**
     * do the low level conversion to json
     *
     * @param xml
     * @return json
     */
    private String xml2Json(String xml) {
        return encodeObject(parseEncoded(xml, xmlParser), jsonParser);
    }

    /**
     * do the low level conversion to xml:
     *
     * @param json
     * @return xml
     */
    private String json2Xml(String json) {
        return encodeObject(parseEncoded(json, jsonParser), xmlParser);
    }

    /**
     * populate a fhir resource from the string using the appropriate parser
     *
     * @param string to parse
     * @param parser parser to use xml or json
     * @return populated fhir resource
     */
    private IBaseResource parseEncoded(String s, IParser parser) {
        return parser.parseResource(s);
    }

    /**
     * construct xml or json encoded object depending on parser
     *
     * @param resource fhir resource to encode
     * @param parser parser to use xml or json
     * @return Encoded string xml or json
     */
    private String encodeObject(IBaseResource resource, IParser parser) {
        return parser.setPrettyPrint(true).encodeResourceToString(resource);
    }

    /**
     * @return the fhirVersion
     */
    public static FhirVersionEnum getFhirVersion() {
        return fhirVersion;
    }

    /**
     * @param aFhirVersion the fhirVersion to set
     */
    public static void setFhirVersion(FhirVersionEnum aFhirVersion) {
        if (fhirVersion == null) {
            getInstance();
        }
        if (aFhirVersion != fhirVersion) {
            fhirVersion = aFhirVersion;
            me.init();
        }
    }
}
