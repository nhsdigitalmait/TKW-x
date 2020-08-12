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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Simple asynchronous message correlator, based on the request trackingId.
 * Takes the tracking id from the request, and looks for an instance of it in
 * the candidate file.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class BasicTrackingIdCorrelator
        implements AsynchronousLogCorrelator {

    @Override
    public boolean correlate(File log, Message request)
            throws Exception {
        return correlates(log, request.getTrackingId());
    }

    private boolean correlates(File f, String trackingId)
            throws Exception {
        try ( // Just look for an instance of the given tracking id. This MAY
                // be vulnerable to false positives, however in the HL7v3 based 
                // business ack, the reference to the tracking id of the original
                // message is just called "id".
                //
            BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains(trackingId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
