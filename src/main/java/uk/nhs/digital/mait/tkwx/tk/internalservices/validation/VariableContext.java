/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.util.HashMap;

/**
 *
 * @author riro
 */
public class VariableContext {

        private HashMap<String, Object> variableSet = new HashMap<String, Object>();;

        public void removeAllVariables(){
            variableSet.clear();
        }

        public void setVariable(String s, Object o){
            variableSet.put(s, o);
        }   
        
        public Object getVariable(String s){
            return variableSet.get(s);
        }

}
