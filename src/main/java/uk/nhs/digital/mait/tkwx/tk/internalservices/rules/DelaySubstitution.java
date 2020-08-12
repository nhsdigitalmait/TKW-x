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

import static java.util.logging.Level.SEVERE;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Implements a delay ( hack for SSP hard coded check on specific nhs number )
 *
 * @author simonfarrow
 */
public class DelaySubstitution implements SubstitutionValue {

    private int delayMs;
    private String key;

    /**
     * public constructor
     */
    public DelaySubstitution() {
        delayMs = 0;
        key = null;
    }

    /**
     *
     * @param o the payload of the received message
     * @return the String containing the value for the substitution tag
     * @throws Exception
     */
    @Override
    public String getValue(String o) throws Exception {
        if (key != null && o != null && o.contains(key)) {
            System.out.println("Sleeping for " + delayMs + " milliseconds ..");
            Thread.sleep(delayMs);
            System.out.println("Awake");
        }
        return "";
    }

    /**
     *
     * @param s string containing any extra parameters to the substitution takes
     * 1 or two parms surrounded by double quotes eg "9999999999 20". 
     * Parm 1 is the string to match on and parm 2 is the delay in
     * seconds (defaults to the tks.synchronousreply.delay value)
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
        // default delay
        String delay = System.getProperty(SYNCHRONOUSRESPONSEDELAY_PROPERTY);
        if (delay != null) {
            delayMs = Integer.parseInt(delay) * 1000;
        }

        if (s != null) {
            // if there's more than one param they need to be double quoted
            s = s.replaceFirst("^\"(.*)\"$", "$1");
            String[] params = s.split("\\s+");
            switch (params.length) {
                case 2:
                    delayMs = Integer.parseInt(params[1]) * 1000;
                // deliberate drop through
                case 1:
                    key = params[0];
                    break;
                default:
                    Logger.getInstance().log(SEVERE,DelaySubstitution.class.getName(),"Invalid number of parameters (" + params.length + ")");
            }
        }
    }
}
