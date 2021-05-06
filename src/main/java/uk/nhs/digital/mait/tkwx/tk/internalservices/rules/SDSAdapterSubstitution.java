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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.logging.Level.SEVERE;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.httpinterceptor.HttpInterceptWorker.getRequestParametersFromCP;

/**
 * Returns a fhirformatted SDS adapter query response
 * parameters on setup are sdsdumpfile device_transform endpoint_transform 
 * Applies one of two parameterised transforms to an xml version of an SDS extract
 *
 * @author simonfarrow
 */
public class SDSAdapterSubstitution implements SubstitutionValue {

    private String[] parameters = null;
    private String sdsfile;
    private final HashMap<String, String> hmTransforms = new HashMap<>();

    /**
     * public constructor
     */
    public SDSAdapterSubstitution() {
    }

    /**
     * evaluated at test time
     *
     * @param o the context path of the received message
     * @return the String containing the xml format fhir response
     * @throws Exception
     */
    @Override
    public String getValue(String o) throws Exception {
        // takes the context path extracts parameters and invokes the correct transform
        // on the sds extract file
        
        // format (^/Device|Endpoint)?(param=value(&|$))+
        String resourceName = o.replaceFirst("^/(.*)\\?.*$", "$1");
        HashMap<String, ArrayList<String>> requestParameters = getRequestParametersFromCP(o);

        ArrayList<String> organizationParameters = null;
        ArrayList<String> identifiers = null;

        String odsCode = null;
        String interaction = null;
        String partyKey = null;
        switch (resourceName) {
            case "Device":
            case "Endpoint":
                organizationParameters = requestParameters.get("organization");
                String[] parts = organizationParameters.get(0).split("\\|");
                odsCode = parts[1];

                identifiers = requestParameters.get("identifier");
                parts = identifiers.get(0).split("\\|");
                
                // switch on system url
                switch (parts[0]) {
                    case "https://fhir.nhs.uk/Id/nhsServiceInteractionId":
                        interaction = parts[1];
                        break;
                    case "https://fhir.nhs.uk/Id/nhsMhsPartyKey|":
                        partyKey = parts[1];
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid identifier system name " + parts[0]);
                }

                break;

            default:
                throw new IllegalArgumentException("Invalid Resource Name " + resourceName);
        }

        // pass parameters to the appropriate transform and return the result
        File stylesheet = new File(hmTransforms.get(resourceName));
        if (!stylesheet.exists()) {
            throw new IllegalArgumentException("SDS transform stylesheet does not exist " + stylesheet.getPath());
        }
        File datafile = new File(sdsfile);
        if (!datafile.exists()) {
            throw new IllegalArgumentException("SDS dump extract file does not exist " + datafile.getPath());
        }

        StreamSource input = new StreamSource(datafile);

        StreamSource stylesource = new StreamSource(stylesheet);
        try {
            Transformer transformer = new net.sf.saxon.TransformerFactoryImpl().newTransformer(stylesource);
            
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            
            transformer.setParameter("org", odsCode);
            transformer.setParameter("int", interaction);
            
            StringWriter sw = new StringWriter();
            StreamResult r = new StreamResult(sw);

            transformer.transform(input, r);
            return sw.getBuffer().toString();
        } catch (TransformerConfigurationException ex) {
            Logger.getInstance().log(SEVERE, SDSAdapterSubstitution.class.getName(), "Transform error " + ex.getMessage());
        }
        return null;
    }

    /**
     * One time setup for substitution
     * Quoted string containing three paths
     * sdsdumpfile device_transform endpoint_transform 
     * @param s
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
        parameters = s.split("\\s");
        if (parameters.length != 3) {
            throw new IllegalArgumentException("Incorrect parameter count "+parameters.length);
        }
        sdsfile = parameters[0];
        hmTransforms.put("Device", parameters[1]);
        hmTransforms.put("Endpoint", parameters[2]);
    }
}
