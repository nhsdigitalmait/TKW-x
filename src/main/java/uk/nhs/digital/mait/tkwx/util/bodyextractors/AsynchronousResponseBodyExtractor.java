/**
 * Copyright 2013 Simon Farrow simon.farrow1@hscic.gov.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * @author SIFA2
 */
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;

/**
 * Retrieve the body of a synchronous request from a log file.
 */
public class AsynchronousResponseBodyExtractor extends AbstractBodyExtractor {

    /**
     * extracts a string containing the xml asynchronous response message
     *
     * @param in
     * @return the extracted xml text as a String
     * @throws java.lang.Exception
     */
    @Override
    public String getBody(InputStream in, boolean getXML)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        boolean body = false;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            // Check for ITK style log content NB this breaks if you pre trim the line
            if ( line.matches("^<([^/].*:)?DistributionEnvelope.*$")){
                body = true;
            }
            if (body) {
                if (line.contains(LogMarkers.END_INBOUND_MARKER)) {
                    break;
                } else {
                    sb.append(line);
                    sb.append("\r\n");
                }
            } else {
                if (line.trim().length() == 0) {
                    body = true;
                }
            }
        }
        return sb.toString();
    }
}
