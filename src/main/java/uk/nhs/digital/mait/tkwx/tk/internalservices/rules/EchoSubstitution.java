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
 * Used for debugging Class substitution related code
 *
 * @author simonfarrow
 */
public class EchoSubstitution implements SubstitutionValue {

    /**
     * public constructor
     */
    public EchoSubstitution() {
    }

    /**
     *
     * @param o the payload of the received message
     * @return a potentially truncated String containing the 10 first characters in the response
     * @throws Exception
     */
    @Override
    public String getValue(String o) throws Exception {
        final int TRIM_LENGTH = 10;
        String result = o.length() > TRIM_LENGTH ? o.substring(0, TRIM_LENGTH)+".." : o;
        System.out.println(result);
        return result;
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
    }
}
