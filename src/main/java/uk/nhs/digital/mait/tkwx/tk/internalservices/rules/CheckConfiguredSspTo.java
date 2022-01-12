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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import java.io.File;
import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;
import uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.ValidationGrammarCompilerVisiter.substTKWRootPath;
import static uk.nhs.digital.mait.tkwx.util.Utils.fileExists;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;
import static uk.nhs.digital.mait.tkwx.util.Utils.readPropertiesFile;

/**
 * Java extension class for use as a Class expression in a simulator rules file
 * compares configured local asid in routing file to incoming to Asid value[0]
 * contains the incoming sspto value args has paths to the two
 * gpconnect-demonstrator-api props files and the name of the property pointing
 * at the json routing file.
 *
 * @author simonfarrow
 */
public class CheckConfiguredSspTo implements ExpressionValue {

    /**
     *
     * @param values single element string array containing the incoming to asid
     * @param args[0..n-1] paths to gpconnect-demonstrator-api.properties
     * @param args[n] property name pointing to json routing file.
     * @return boolean result returns true for a mismatch
     * @throws Exception
     */
    @Override
    public boolean getValue(String[] values, String[] args) throws Exception {
        if (values.length != 1) {
            String msg = "Values array has invalid length " + values.length;
            Logger.getInstance().log(SEVERE, CheckConfiguredSspTo.class.getName(), msg);
            throw new IllegalArgumentException(msg);
        }

        if (args.length < 2) {
            String msg = "Args array has invalid length " + args.length;
            Logger.getInstance().log(SEVERE, CheckConfiguredSspTo.class.getName(), msg);
            throw new IllegalArgumentException(msg);
        }

        // get the default value from the first file
        // check for an override in the local environment file
        String routingFilePath = null;
        String propertyName = args[args.length - 1];
        File firstPropsFile = new File(args[0]);
        String localAsid = null;
        for (int i = 0; i < args.length - 1; i++) {
            String propertyFileName = args[i];
            System.out.println("Looking for property file named " + propertyFileName);
            if (fileExists(propertyFileName)) {
                Properties properties = new Properties();
                readPropertiesFile(propertyFileName, properties);
                if (properties.getProperty(propertyName) != null) {
                    routingFilePath = properties.getProperty(propertyName);
                    System.out.println("Path to routing file " + routingFilePath);

                    // assumes the routing file which is relative is in the same folder as the first props file.
                    // chop off the trailing path to the config folder only when the relative path is external
                    String jsonFile = routingFilePath.startsWith("external") ? firstPropsFile.getParent().replaceFirst("/[^/]+$", "/") + routingFilePath : 
                            firstPropsFile.getParent() + "/" + routingFilePath;
                    System.out.println("Looking for json file at " + jsonFile);
                    if (fileExists(jsonFile)) {
                        String json = readFile2String(substTKWRootPath(jsonFile));
                        String xml = JsonXmlConverter.jsonToXmlString(json.toCharArray());
                        localAsid = xpathExtractor("json:json/@ASID", xml);
                        System.out.println("Setting system asid = " + localAsid);
                    } else {
                        System.err.println("json File does not exist");
                    }
                } else {
                    System.err.println("Property " + propertyName + " not found in file " + propertyFileName);
                }
            }
        }
        
        if (localAsid == null) {
            return true;
        } else {
            return !localAsid.equals(values[0]);
        }
    }
}
