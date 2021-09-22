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
package uk.nhs.digital.mait.tkwx.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;

/**
 * Emergency class for returning Spine errors.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class NaspLastResortReporter extends LastResortReporter {

    private final String HTTP_LINE_SEPARATOR = "\r\n";
    private final String DEFAULTERROR = "System failure to process message";
    private final String INITIAL_HEADERS = String.join(HTTP_LINE_SEPARATOR, List.of(
            "HTTP/1.1 500 OK",
            "Content-Length: "
    ));
    private final String CONTENT_TYPE_HEADER = String.join(HTTP_LINE_SEPARATOR, List.of(
            "",
            "Content-Type: text/xml",
            "",
            ""
    ));
    private final String RESPONSE_START = String.join(HTTP_LINE_SEPARATOR, List.of(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>",
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">",
            "<soap:Body>",
            "<soap:Fault>",
            "<faultcode>soap:Server</faultcode>",
            "<faultstring>System failure to process message</faultstring>",
            "<detail>",
            "<nasp:errorList xmlns:nasp=\"http://national.carerecords.nhs.uk/schema/\">",
            "<nasp:error>",
            "<nasp:codeContext>urn:nhs:names:errors:tms</nasp:codeContext>",
            "<nasp:errorCode>200</nasp:errorCode>",
            "<nasp:severity>Error</nasp:severity>",
            "<nasp:location/>",
            "<nasp:description>"
            ));
    private final String RESPONSE_END = String.join(HTTP_LINE_SEPARATOR, List.of(
            "</nasp:description>",
            "</nasp:error>",
            "</nasp:errorList>",
            "</detail>",
            "</soap:Fault>",
            "</soap:Body>",
            "</soap:Envelope>"
    ));

    @Override
    public void report(String errorMessage, OutputStream out) {
        String error = isNullOrEmpty(errorMessage) ? DEFAULTERROR : errorMessage;
        StringBuilder sb = new StringBuilder(INITIAL_HEADERS);
        sb.append(Long.toString(error.length() + RESPONSE_START.length() + RESPONSE_END.length()));
        sb.append(CONTENT_TYPE_HEADER);
        sb.append(RESPONSE_START);
        sb.append(error);
        sb.append(RESPONSE_END);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out))) {
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            Logger.getInstance().log(SEVERE, NaspLastResortReporter.class.getName(),
                    "Exception in .report(): " + e.getMessage() + "\nGiving up.");
        }
    }

}
