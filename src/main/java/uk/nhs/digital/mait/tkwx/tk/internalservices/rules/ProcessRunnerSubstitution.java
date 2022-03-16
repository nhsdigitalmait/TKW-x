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

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Returns an empty string
 * but starts an asynchronous process running when the payload matches the match string
 *
 * @author simonfarrow
 */
public class ProcessRunnerSubstitution implements SubstitutionValue {
    
    private String matchRegexp;
    private ArrayList<String> params;

    /**
     * public constructor
     */
    public ProcessRunnerSubstitution() {
        params = null;
        matchRegexp = null;
    }

    /**
     * evaluated at test time
     *
     * @param o the payload of the received message
     * @return an empty string so that the tag is removed from the response
     * @throws Exception
     */
    @Override
    public String getValue(String o) throws Exception {
        try {
            if ( params != null) {
                if ( o.matches(matchRegexp) ) {
                    ProcessBuilder pb = new ProcessBuilder(params);
                    Process p = pb.start();
                }
            } else {
                System.err.println("ProcessRunnerSubstitution params not set prior to call getValue");
            }
        } catch ( Exception ex ) {
            System.err.println("ProcessRunnerSubstitution exception "+ex.getMessage());
            throw ex;
        }
        return "";
    }

    /**
     * One time setup for substitution
     *
     * @param s regular expression match on payload followed by params for process builder 
     * @throws Exception
     */
    @Override
    public void setData(String s) throws Exception {
        String[] arr = s.split("\\s");
        matchRegexp = arr[0];
        params = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(arr,1,arr.length)));
    }
}
