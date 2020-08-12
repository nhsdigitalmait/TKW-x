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
import uk.nhs.digital.mait.tkwx.tk.internalservices.ValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.SpineValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
/**
 * The TKW validate mode keys validation scripts on service name. Each script is
 * a collection of validation checks supporting conditional validation, "subroutine"
 * calls and so on. A validation script for a service is a ValidationSet, this object
 * is made by the validation configuration file parser and stored in a
 * ValidatorFactory.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class ValidationSet implements VariableProvider, VariableConsumer{
    private final ArrayList<Validation> validations = new ArrayList<>();
    private String serviceName = null;
    private final VariableContext var = new VariableContext();
    private VariableProvider vProvider = null;
    
    ValidationSet() {}

    void setServiceName(String n) {
        serviceName = n;
    }

    public String getServiceName() { return serviceName; }

    public void addValidation(Validation v) { 
        validations.add(v); 
    }

    public ArrayList<ValidationReport> doSpineValidations(SpineMessage sm, SpineValidatorService vs) {
        ArrayList<ValidationReport> ve = new ArrayList<>();
        if (validations == null) {
            return ve;
        }
        for (Validation v : validations) {
            ValidationReport[] e = null;
            try {
                e = v.validate(sm, vs);
            }
            catch (Exception ex) {
                ValidationReport vr = new ValidationReport("Error running check");
                vr.setTest("Validation type " + v.getType() + " using " + v.getResource() + " : " + ex.getMessage());
                e = new ValidationReport[1];
                e[0] = vr;
            }
            for (ValidationReport ex : e) {
                ex.setServiceName(serviceName);
                ve.add(ex);
            }
        }
        return ve;
    }

    /**
     * 
     * @param o The message content
     * @param extraMessageInfo hashmap of extra optional data
     * @param stripHeader boolean indicating whether to strip the header from the message
     * @param vs a ValidationService object
     * @return ArrayList of ValidationReport
     */
    public ArrayList<ValidationReport> doValidations(String o, HashMap<String,Object> extraMessageInfo, boolean stripHeader, ValidatorService vs)
    {
        ArrayList<ValidationReport> ve = new ArrayList<>();
        if (validations == null) {
            return ve;
        }
        for (Validation v : validations) {
            ValidationReport[] e = null;
            try {
                e = v.validate(o, extraMessageInfo, stripHeader, vs);                
            }
            catch (Exception ex) {
                ValidationReport vr = new ValidationReport("Error running check");
                vr.setTest("Validation type " + v.getType() + " using " + v.getResource() + " : " + ex.getMessage());
                e = new ValidationReport[1];
                e[0] = vr;
            }
            for (ValidationReport ex : e) {
                if (ex != null) {
                    ex.setServiceName(serviceName);
                    ve.add(ex);
                }
            }
        }
        //Remove all the variables in this ValidationSet context
        var.removeAllVariables();
        return ve;
    }

    @Override
    public Object getVariable(String s) {
        if(var.getVariable(s) == null){
            if(vProvider == null){
                // This means that the VALIDATE has been reached and no variable found
                return "";
            }
            else
            {
                return vProvider.getVariable(s);
            }
        }
        else
        {
            return var.getVariable(s);
        }
    }

    @Override
    public void setVariable(String s, Object o) {
        var.setVariable(s, o);
    }

    @Override
    public void setVariableProvider(VariableProvider vp) {
        vProvider = vp;
    }

    /**
     * @return the validations used for debugging testing
     */
    ArrayList<Validation> getValidations() {
        return validations;
    }

}
