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

 /**
 * Return type for ValidationCheck.validate() method, encapsulating an array
 * of ValidationReport and an output string. Contains boilerplate "get" methods.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class ValidatorOutput {
    ValidationReport[] report = null;
    String output = null;
    
    public ValidatorOutput(String o, ValidationReport[] v) {
        report = v;
        output = o;
    }
    
    public ValidationReport[] getReport() { return report; }
    public String getOutput() { return output; }
}
