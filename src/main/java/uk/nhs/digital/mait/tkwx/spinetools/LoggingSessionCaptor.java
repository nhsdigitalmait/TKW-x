/*

 Copyright 2014 Health and Social Care Information Centre
 Solution Assurance damian.murphy@hscic.gov.uk

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
package uk.nhs.digital.mait.tkwx.spinetools;

import java.io.UnsupportedEncodingException;
import uk.nhs.digital.mait.spinetools.spine.connection.SessionCaptor;
import uk.nhs.digital.mait.spinetools.spine.logging.SpineToolsLogger;
import uk.nhs.digital.mait.spinetools.spine.messaging.Sendable;

/**
 * Simple implementation of a SessionCaptor that calls the logger
 *
 *
 * @author Damian Murphy damian.murphy@hscic.gov.uk
 */
public class LoggingSessionCaptor
        implements SessionCaptor {

    public LoggingSessionCaptor() {
    }

    @Override
    public void capture(Sendable s) {
        try {
            String outboundMessage = new String(s.getOnTheWireRequest(), "UTF-8");
            String inboundMessage = new String(s.getOnTheWireResponse(), "UTF-8");
                       String originatingMessageId = null;
                        if (outboundMessage.equals("")) {
                            originatingMessageId = null;
                        } else {
                            int start = outboundMessage.indexOf("MessageId>");
                            if (start == -1) {
                                originatingMessageId = null;
                            } else {
                                start += "MessageId>".length();
                                int end = outboundMessage.indexOf("<", start);
                                if (end == -1) {
                                    originatingMessageId = null;
                                } else {
                                    originatingMessageId = outboundMessage.substring(start, end);
                                }
                            }
                        }
            SpineToolsLogger.getInstance().log("uk.nhs.digital.mait.spinetools.spine.messaging.sendable.message", "\r\nON THE WIRE OUTBOUND: \r\n\r\n" + outboundMessage);
            SpineToolsLogger.getInstance().log("uk.nhs.digital.mait.spinetools.spine.messaging.sendable.message", "\r\nReference to Message ID " + originatingMessageId + " - ON THE WIRE SYNC INBOUND: \r\n\r\n" + inboundMessage);
        } catch (UnsupportedEncodingException ex) {
            SpineToolsLogger.getInstance().log("uk.nhs.digital.mait.spinetools.spine.connection.LoggingSessionCaptor", "Could not read on the wire byte stream :" + ex.getMessage());
        }
    }
}
