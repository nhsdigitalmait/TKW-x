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

/**
 * Interface implemented by classes providing substitutions for response templates.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public interface SubstitutionValue {

    /**
     * Called when the request is received (NB not when the tag is evaluated)
     * @param o String containing the request payload
     * @return the value for the substitution tag
     * @throws Exception
     */
    public String getValue(String o) throws Exception;

    /**
     * Called when the config file is read
     * @param s String containing any parameters supplied to the substitution 
     * in the simulator config file. Multiple parameters will require surrounding
     * in double quotes.
     * @throws Exception
     */
    public void setData(String s) throws Exception;
}
