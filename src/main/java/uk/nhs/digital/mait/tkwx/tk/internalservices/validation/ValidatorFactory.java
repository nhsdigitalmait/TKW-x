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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import uk.nhs.digital.mait.commonutils.util.ConfigurationStringTokeniser;
import java.util.HashMap;
import java.util.Properties;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.util.logging.Level.SEVERE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Class to implement the parser for validation configuration files, and to
 * provide the parsed validation sets to the TKW validate mode, keyed on
 * service.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class ValidatorFactory {

    private static final String EXTRACTFULLRULESET = "tks.validator.extractfullruleset";
    private static final SimpleDateFormat FILEDATE = new SimpleDateFormat("yyyyMMddHHmmssss");
    private static final ValidatorFactory FACTORY = new ValidatorFactory();
    private static Exception bootException = null;
    private HashMap<String, ValidationSet> validationSets = null;
    private HashMap<String, ValidationSet> subroutines = null;
    private Properties bootProperties = null;
    private List<RulesetMetadata> metadata = null;
    private boolean extractFullRuleset = false;
    private final StringBuilder fullExtractsb = new StringBuilder();
    private final static boolean USE_ANTLR_PARSER = true;

    public static ValidatorFactory getInstance()
            throws Exception {
        if (bootException != null) {
            throw new Exception("Exception was thrown initialising ValidatorFactory", bootException);
        }
        return FACTORY;
    }
    private ValidationGrammarCompilerVisiter visitor;

    public ValidationSet getSubroutine(String r) {
        return subroutines.get(r);
    }

    public List<RulesetMetadata> getMetadata() {
        return metadata;
    }

    public void clear() {
        validationSets = null;
        subroutines = null;
        bootProperties = null;
        bootException = null;
    }

    public void init(Properties p)
            throws Exception {
        bootProperties = p;
        String conf = p.getProperty(VALIDATOR_CONFIG_PROPERTY);
        if (conf == null) {
            throw new Exception("Validator configuration file property " + VALIDATOR_CONFIG_PROPERTY + " not set");
        }
        extractFullRuleset = isY(p.getProperty(EXTRACTFULLRULESET));
        String reportDir = p.getProperty(VALIDATOR_REPORT_PROPERTY);
        if (reportDir == null) {
            throw new Exception("Validator Report configuration file property " + VALIDATOR_REPORT_PROPERTY + " not set");
        }
        if (USE_ANTLR_PARSER) {
            visitor = new ValidationGrammarCompilerVisiter(bootProperties);
            visitor.parse(conf);
            validationSets = visitor.getValidationSets();
            subroutines = visitor.getSubroutines();
            metadata = visitor.getMetadata();
        } else {
            validationSets = new HashMap<>();
            subroutines = new HashMap<>();
            metadata = new ArrayList<>();
            processRuleset(conf, null, null, null);
        }
        if (extractFullRuleset) {
            writeFullRuleset(reportDir);
        }
    }

    private void processRuleset(String conf, ValidationSet currentValidationSet, Validation currentValidation, Validation lastValidation) throws FileNotFoundException, Exception {
        FileReader fr = new FileReader(conf);
        BufferedReader br = new BufferedReader(fr);

        String line = null;
        int lineNumber = 0;
        RulesetMetadata r = new RulesetMetadata();
        boolean metadataAdded = false;
        while ((line = br.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            if (line.startsWith("#")) {
                if (extractFullRuleset) {
                    extractFullRuleset(line);
                }
                continue;
            }
            if (line.length() == 0) {
                continue;
            }
            ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
            String elem = st.nextToken();

            if (elem.contentEquals("VALIDATION-RULESET-NAME")) {
                metadataAdded = true;
                StringBuilder sb = restOfLine(st);
                r.setName(sb.toString());
                continue;
            }
            if (elem.contentEquals("VALIDATION-RULESET-VERSION")) {
                metadataAdded = true;
                StringBuilder sb = restOfLine(st);
                r.setVersion(sb.toString());
                continue;
            }
            if (elem.contentEquals("VALIDATION-RULESET-TIMESTAMP")) {
                metadataAdded = true;
                StringBuilder sb = restOfLine(st);
                r.setTimestamp(sb.toString());
                continue;
            }
            if (elem.contentEquals("VALIDATION-RULESET-AUTHOR")) {
                metadataAdded = true;
                StringBuilder sb = restOfLine(st);
                r.setAuthor(sb.toString());
                continue;
            }
            if (extractFullRuleset && !elem.contentEquals("INCLUDE")) {
                extractFullRuleset(line);
            }
            if (elem.contentEquals("VALIDATE")) {
                if (currentValidation != null) {
                    throw new Exception("Syntax error in validation configuration: VALIDATE after unterminated condition");
                }
                if (!st.hasMoreTokens()) {
                    throw new Exception("Syntax error in validation configuration " + conf + " : bare VALIDATE");
                }
                elem = st.nextToken();
                if ((currentValidationSet = validationSets.get(elem)) == null) {
                    currentValidationSet = new ValidationSet();
                    currentValidationSet.setServiceName(elem);
                }
                validationSets.put(elem, currentValidationSet);
                continue;
            }
            if (elem.contentEquals("SUBSET")) {
                if (currentValidation != null) {
                    throw new Exception("Syntax error in validation configuration: SUBSET after unterminated condition");
                }
                currentValidationSet = new ValidationSet();
                if (!st.hasMoreTokens()) {
                    throw new Exception("Syntax error in validation configuration " + conf + " : bare SUBSET with no name");
                }
                elem = st.nextToken();
                currentValidationSet.setServiceName(elem);
                subroutines.put(elem, currentValidationSet);
                continue;
            }
            if (elem.contentEquals("INCLUDE")) {
                if (currentValidation != null) {
                    throw new Exception("Syntax error in validation configuration: INCLUDE after unterminated condition");
                }
                if (!st.hasMoreTokens()) {
                    throw new Exception("Syntax error in validation configuration " + conf + " : bare INCLUDE");
                }
                elem = st.nextToken();
                processRuleset(elem, currentValidationSet, currentValidation, lastValidation);
                continue;
            }
            String annotation = null;
            if (elem.contentEquals("ANNOTATION")) {
                if (lastValidation != null) {
                    annotation = line.substring("ANNOTATION".length()).trim();
                    lastValidation.setAnnotation(annotation);
                    annotation = null;
                }
                continue;
            }
            Validation v = null;
            if (st.hasMoreTokens()) {
                // We're reading a real validation
                v = new Validation(elem);
                lastValidation = v;
                v.setService(currentValidationSet.getServiceName());
                if (currentValidation != null) {
                    currentValidation.appendValidation(v);
                    v.setVariableProvider(currentValidation);
                } else {
                    currentValidationSet.addValidation(v);
                    v.setVariableProvider(currentValidationSet);
                }
                elem = st.nextToken();
                v.setType(elem);
                if (st.hasMoreTokens()) {
                    elem = st.nextToken();
                    v.setResource(elem);
                    if (st.hasMoreTokens()) {
                        StringBuilder sb = restOfLine(st);
                        v.setData(sb.toString());
                    }
                }
//                System.err.println(line);
                currentValidation = v.initialise(bootProperties);
            } else // We're doing a "THEN" or and "ELSE" or an "ENDIF" from an IF
            {
                if (currentValidation == null) {
                    Logger.getInstance().log(SEVERE, ValidatorFactory.class.getName(),
                            "Configuration error - expected to be in a condition but no current validation found: Line " + lineNumber + " : " + line);
                } else {
                    currentValidation.setType(elem);
                    currentValidation = currentValidation.initialise();
                }
            }

        }
        if (metadataAdded) {
            metadata.add(r);
            metadataAdded = false;
        }

    }

    private StringBuilder restOfLine(ConfigurationStringTokeniser st) throws Exception {
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            if (st.hasMoreTokens()) {
                sb.append(" ");
            }
        }
        return sb;
    }

    private ValidatorFactory() {
    }

    public ValidationSet getValidationSet(String s)
            throws Exception {
        if (validationSets == null) {
            return null;
        }
        return validationSets.get(s);
    }

    private void extractFullRuleset(String line) {
        fullExtractsb.append(line);
    }

    private void writeFullRuleset(String dir)
            throws Exception {
        try {
            File f = new File(dir, "FullExtractedRuleset_" + FILEDATE.format(new Date()) + ".txt");
            Utils.writeString2File(f, fullExtractsb.toString());
        } catch (IOException e) {
            throw new Exception("Problem writing full extracted Ruleset to " + dir + e);
        }

    }
}
