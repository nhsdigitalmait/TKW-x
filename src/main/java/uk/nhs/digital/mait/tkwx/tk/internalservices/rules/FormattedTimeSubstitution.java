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

import java.security.InvalidParameterException;

/**
 * Returns a formatted time with an optional offset in days and seconds and an optional url encoding boolean
 *
 * Parameters are space separated Strings enclosed in a single set of double quotes 
 * 
 * "&lt;DateFormat&gt; &lt;TimeZone&gt; [&lt;DaysOffset&gt; &lt;SecondsOffset&gt;> [&lt;url encoding&gt;]]"
 * 
 * Utilises the test automation static function getFormattedTime 
 * 
 * @author simonfarrow
 */
public class FormattedTimeSubstitution implements SubstitutionValue {

    private String[] parameters = null;

    /**
     * public constructor
     */
    public FormattedTimeSubstitution() {
    }

    /**
     * evaluated at test time
     * @param o the payload of the received message
     * @return the String containing the value for the substitution tag
     * @throws Exception
     */
    @Override
    public String getValue(String o) throws Exception {
        // getFormattedTime(format, timezone[, daysoffsetStr, secondsoffsetStr[, urlEncodeStr]]);
        switch (parameters.length) {
            case 2:
                return uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.getFormattedTime(parameters[0], parameters[1]);
            case 4:
                return uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.getFormattedTime(parameters[0], parameters[1], parameters[2], parameters[3]);
            case 5:
                return uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.PropertySetFunctions.getFormattedTime(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4]);
            default:
                throw new InvalidParameterException("FormattedTimeSubstitution has invalid number of parameters");
        }
    }

    /**
     * One time setup for substitution
     * @param s
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
        parameters = s.split("\\s");
        switch (parameters.length) {
            case 2:
            case 4:
            case 5:
                break;
            default:
                throw new InvalidParameterException("FormattedTimeSubstitution has invalid number of parameters");
        }
    }
}
