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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.SpineValidatorService;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.ChainType.FALSE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.ChainType.NULL;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.ChainType.SINGLE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.ChainType.TRUE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType.CHECK;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType.ELSE;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType.IF;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType.SET;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType.THEN;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.Validation.SequenceType.UNDEFINED;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Validation is a wrapper round the implementations of ValidationCheck that
 * actually do the validations. This allows the loader to make something
 * concrete before it knows what sort of thing it is going to make (because the
 * configurations are loaded generically from XML and it doesn't know what order
 * it will get the data in). The Validation class itself implements
 * ValidationCheck for the convenience of the loader.
 *
 * Initially-supported validations are schema and xslt. You can extend this by
 * providing your own implementation of ValidationCheck, and putting the mapping
 * between the type name and the implementation class in the system properties.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Validation implements VariableConsumer, VariableProvider {

    private static final boolean DEBUG = false;

    enum SequenceType {
        UNDEFINED,
        CHECK,
        IF,
        THEN,
        ELSE,
        SET
    }

    enum ChainType {
        NULL,
        SINGLE,
        TRUE,
        FALSE
    }

    private static final String PROPERTYROOT = "tks.validator.check.";

    private ArrayList<Validation> trueChain = null;
    private ArrayList<Validation> falseChain = null;

    private ChainType chain = NULL;

    private Validation parent = null;

    private final VariableContext var = new VariableContext();
    private VariableProvider vProvider = null;

    private SequenceType sequenceType = UNDEFINED;
    private String type = null;
    private String resource = null;
    private String data = null;
    private String serviceName = null;

    private String annotation = null;

    private ValidationCheck validation = null;

    /**
     * public constructor 
     * 
     * param is not a type.  The type is set using setType
     * sets the sequence type
     *
     * @param sequenceTypeStr CHECK|IF|THEN|ELSE|END IF|SET
     * Not to be confused with type which is set later
     */
    public Validation(String sequenceTypeStr) {
        if (DEBUG) {
            System.out.println("Instantiating new Validation " + this + " sequenceTypeStr = " + sequenceTypeStr);
        }
        switch (sequenceTypeStr) {
            case "CHECK":
                sequenceType = CHECK;
                chain = SINGLE;
                break;
            case "IF":
                sequenceType = IF;
                chain = SINGLE;
                break;
            case "THEN":
                sequenceType = THEN;
                chain = NULL;
                break;
            case "ELSE":
                sequenceType = ELSE;
                chain = NULL;
                break;
            case "END IF":
                sequenceType = UNDEFINED;
                chain = NULL;
                break;
            case "SET":
                sequenceType = SET;
                chain = SINGLE;
                break;
            default:
                break;
        }
    }

    public void setAnnotation(String a) {
        if (annotation == null) {
            annotation = a;
        }
    }

    SequenceType getSequenceType() {
        return sequenceType;
    }

    /**
     * allows appending of validations to CHECK, SET or IF validations
     * for IF either to the true or false chain depending on the status (type)
     * @param v Validation to append
     * @throws Exception 
     */
    void appendValidation(Validation v)
            throws Exception {
        if (DEBUG) {
            System.out.println("Appending validation " + v + " to " + " validation " + this);
            System.out.println("          validation type " + v.type + " to " + " validation type " + this.type);
            System.out.println("          validation resource " + v.resource + " to " + " validation resource " + this.resource);
        }
        switch (v.getSequenceType()) {
            case CHECK:
            case SET:
            case IF:
                v.setParent(this);
                if (chain.ordinal() < TRUE.ordinal()) {
                    throw new Exception("Syntax error in config");
                }
                if (chain == TRUE) {
                    trueChain.add(v);
                } else {
                    falseChain.add(v);
                }
                break;

            case THEN:
                chain = TRUE;
                trueChain = new ArrayList<>();
                break;

            case ELSE:
                chain = FALSE;
                falseChain = new ArrayList<>();
                break;

            case UNDEFINED:
                break;
        }
    }

    void setParent(Validation v) {
        parent = v;
    }

    /**
     * type xpathequals...|THEN|ELSE|ENDIF
     * Not to be confused with constructor param sequence type
     * For an IF this amounts to a status flag
     * @param t validation type
     */
    void setType(String t) {
        if (DEBUG) {
            System.out.println("Setting type of " + this + " to " + t);
        }
        if (sequenceType != IF) {
            type = t;
            return;
        }

        // its an IF which behaves dfferently
        // type is not static for IF and indicates a stage as the Validation is built up
        if (t.contentEquals("THEN")) {
            // we are now in the THEN part and will append any further Validations to the true chain
            chain = TRUE;
            trueChain = new ArrayList<>();
            return;
        }
        if (t.contentEquals("ELSE")) {
            // we are now in the ELSE part and we will append any further Validations to the false chain
            chain = FALSE;
            falseChain = new ArrayList<>();
            return;
        }
        if (t.contentEquals("ENDIF")) {
            // we have finished appending to the true or false chains
            chain = NULL;
            return;
        }
        type = t;
    }

    String getType() {
        return type;
    }

    String getResource() {
        return resource;
    }

    /**
     * second parameter
     * @param r 
     */
    void setResource(String r) {
        resource = r;
    }

    /**
     * third and subsequent params appended
     * @param d 
     */
    void setData(String d) {
        data = d;
    }

    void setService(String s) {
        serviceName = s;
    }

    public String getServiceName() {
        return serviceName;
    }

    /**
     * This does not change the state of the object
     * @return 
     */
    Validation initialise() {
        if (chain == NULL) {
            return parent;
        }
        return this;
    }

    /**
     * handles instantiation of contained ValidationCheck objects
     * @param p
     * @return
     * @throws Exception 
     */
    Validation initialise(Properties p)
            throws Exception {
        if (type == null) {
            return parent;
//            throw new Exception("Validation().initialise() called and type not set");
        }
        if (null != sequenceType) {
            switch (sequenceType) {
                case CHECK:
                    initCheck(p);
                    return parent;
                case UNDEFINED:
                    return null;
                case IF:
                    initCheck(p);
//            var = new VariableContext();
                    break;
                case SET:
                    //            if(type.equals("literal")){
//                parent.var.initialiseVariable(resource);
//            }
                    return parent;
                default:
                    break;
            }
        }
        return this;
    }

    /**
     * instantiation a new ValidationCheck using reflection
     * @param p
     * @throws Exception 
     */
    void initCheck(Properties p)
            throws Exception {
        StringBuilder sb = new StringBuilder(PROPERTYROOT);
        // type can now carry an optional appended xml source for xpath validation so strip that when instantiating
        sb.append(type.replaceFirst("^([^\\s]*)\\s+.*$", "$1"));
        String cname = p.getProperty(sb.toString());
        if (cname == null) {
            throw new Exception("Class definition property " + sb.toString() + " not found");
        }
        try {
            validation = (ValidationCheck) Class.forName(cname).newInstance();
            if (DEBUG) {
                System.out.println("Creating a new check of type " + cname + " " + validation);
            }
            validation.setVariableProvider((VariableProvider) this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new Exception("Exception instantiating ValidatorCheck " + cname + " : " + e.getClass().getName() + " : " + e.getMessage());
        }
        validation.setType(type);
        validation.setResource(resource);
        validation.setData(data);
        validation.initialise();
    }

    /**
     * Spine overload of validate
     *
     * @param o SpineMessage
     * @param vs ValidatorService
     * @return Array of ValidationReport
     * @throws Exception
     */
    public ValidationReport[] validate(SpineMessage o, SpineValidatorService vs)
            throws Exception {
        if (sequenceType == UNDEFINED) {
            return null;
        }
        // For an IF, we need to execute the decision check, then identify which
        // chain we're going down. Then iterate (and recurse as needed) down the
        // appropriate chain, and assemble all the reports.
        //
        if (sequenceType == SET) {
            vProvider.setVariable(resource, data);
            ValidationReport ve = new ValidationReport("Info");
            ve.setPassed();
            ve.setTest("Variable " + resource + " set to " + type + ": " + data);
            return new ValidationReport[]{ve};
        }
        if (sequenceType == IF) {
            ValidationReport[] vrep = validation.validate(o);
            if (vrep == null) {
                return null;
            }
            boolean allpassed = true;
            for (ValidationReport r : vrep) {
                if (!r.getPassed()) {
                    allpassed = false;
                    break;
                }
            }
            ArrayList<ValidationReport> output = new ArrayList<>();
            ArrayList<Validation> branch = allpassed ? trueChain : falseChain;
            if (branch != null) { // Empty branch
                for (Validation v : branch) {
                    ValidationReport[] vbranch = v.validate(o, vs);
                    for (ValidationReport vx : vbranch) {
                        output.add(vx);
                    }
                }
            } else {
                return new ValidationReport[0];
            }
            return output.toArray(new ValidationReport[output.size()]);
        }
        //
        // Normal check, just execute it and return the result. Put any annotation
        // into the first report element.
        //
        ValidationReport[] voutput = validation.validate(o);
        String sd = validation.getSupportingData();
        if (sd != null) {
            vs.writeSupportingData(sd);
        }
        if (annotation != null) {
            if ((voutput != null) && (voutput[0] != null)) {
                voutput[0].setAnnotation(annotation);
            }
        }
        return voutput;
    }

    /**
     * Non Spine overload of validate
     *
     * @param o Non spine message to validate
     * @param extraMessageInfo
     * @param stripHeader
     * @param vs ValidatorService
     * @return Array of Validation Report
     * @throws Exception
     */
    public ValidationReport[] validate(String o, HashMap<String,Object> extraMessageInfo, boolean stripHeader, ValidatorService vs)
            throws Exception {
        if (sequenceType == UNDEFINED) {
            return null;
        }
        // For an IF, we need to execute the decision check, then identify which
        // chain we're going down. Then iterate (and recurse as needed) down the
        // appropriate chain, and assemble all the reports.
        //
        if (sequenceType == SET) {
            vProvider.setVariable(resource, data);
            ValidationReport ve = new ValidationReport("Info");
            ve.setPassed();
            ve.setTest("Variable " + resource + " set to " + type + ": " + data);
            return new ValidationReport[]{ve};
        }
        if (sequenceType == IF) {
            ValidationReport[] vrep = null;
            ValidatorOutput vout = validation.validate(o, extraMessageInfo, stripHeader);
            vrep = vout.getReport();
            if (vrep == null) {
                return null;
            }
            boolean allpassed = true;
            for (ValidationReport r : vrep) {
                if (!r.getPassed()) {
                    allpassed = false;
                    break;
                }
            }
            vrep = null;
            ArrayList<Validation> branch = null;
            ArrayList<ValidationReport> output = new ArrayList<>();
            branch = allpassed ? trueChain : falseChain;
            if (branch != null) { // Empty branch
                for (Validation v : branch) {
                    ValidationReport[] vbranch = v.validate(o, extraMessageInfo, stripHeader, vs);
                    for (ValidationReport vx : vbranch) {
                        output.add(vx);
                    }
                }
            } else {
                return new ValidationReport[0];
            }
            vrep = output.toArray(new ValidationReport[output.size()]);
            //Remove all the variables in this IF context
            var.removeAllVariables();
            return vrep;
        }
        //
        // Normal check, just execute it and return the result. Put any annotation
        // into the first report element.
        //
        ValidatorOutput vout = validation.validate(o, extraMessageInfo, stripHeader);
        ValidationReport[] voutput = vout.getReport();
        if (vs != null) {
            String sd = validation.getSupportingData();
            if (sd != null) {
                vs.writeSupportingData(sd);
            }
        }
        if (annotation != null) {
            if ((voutput != null) && (voutput[0] != null)) {
                voutput[0].setAnnotation(annotation);
            }
        }
        return voutput;
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        vProvider = vp;
    }

    @Override
    public Object getVariable(String s) {
        if (var.getVariable(s) == null) {
            return vProvider.getVariable(s);
        } else {
            return var.getVariable(s);
        }
    }

    @Override
    public void setVariable(String s, Object o) {
        var.setVariable(s, o);
    }

    private int indent = -1;
    
    public void walk() {
        walk(this);
    }
    
    /**
     * recursively dump validations
     * @param v 
     */
    public void walk(Validation v) {
        indent++;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        String indentStr = sb.toString();
        System.out.print(indentStr + v.sequenceType);
        switch (v.sequenceType) {
            case SET:
            case CHECK:
            case IF:
                System.out.println(" " + v.type + (v.resource != null ? " "+ v.resource : "") + (v.data != null ? " "+ v.data : ""));
                if (v.trueChain != null && v.trueChain.size() > 0) {
                    System.out.println(indentStr + "True chain");
                    for (Validation t : v.trueChain) {
                        walk(t);
                    }
                }
                if (v.falseChain != null && v.falseChain.size() > 0) {
                    System.out.println(indentStr + "False chain");
                    for (Validation f : v.falseChain) {
                        walk(f);
                    }
                }
                break;
            case THEN:
            case ELSE:
                break;
            default:
                Logger.getInstance().log(SEVERE,Validation.class.getName(),"Unrecognised sequence type "+v.sequenceType);
        }
        indent--;
    }
}
