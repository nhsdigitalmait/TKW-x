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

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Simple SOAP+WS-Addressing asynchronous message correlator. Takes the message
 * id from the request, and looks for a "RelatesTo" in the candidate file.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class BasicSoapMessageIdCorrelator
        implements AsynchronousLogCorrelator {

    @Override
    public boolean correlate(File log, Message request)
            throws Exception {
        return correlates(log, request.getMessageId());
    }

    private boolean correlates(File f, String msgid)
            throws Exception {
        try ( // Fast scan by looking for WS-Addressing "RelatesTo" and the
                // original message id on the same line
                //
            BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("RelatesTo")) {
                    if (line.contains(msgid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
