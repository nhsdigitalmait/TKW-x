/*
  Copyright 2021  Vipul

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
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;

/**
 * Emergency class for returning FHIR errors
 *
 * @author vipul
 */
public class FhirLastResortReporter extends LastResortReporter {

    private final String HTTP_LINE_SEPARATOR = "\r\n";
    private final String DEFAULTERROR = "Unexpected internal server error.";
    private final String INITIAL_HEADERS = String.join(HTTP_LINE_SEPARATOR, List.of(
            "HTTP/1.1 500 Internal Server Error",
            "Content-Length: "
    ));
    private final String CONTENT_TYPE_HEADER = String.join(HTTP_LINE_SEPARATOR, List.of(
            "",
            "Content-Type: application/fhir+xml",
            "",
            ""
    ));
    private final String RESPONSE_START = String.join(HTTP_LINE_SEPARATOR, List.of(
            "<OperationOutcome xmlns=\"http://hl7.org/fhir\">",
            "<id value=\"" + UUID.randomUUID().toString() + "\"/>",
            "<meta>",
            "<profile value=\"https://fhir.nhs.uk/STU3/StructureDefinition/Spine-OperationOutcome-1\"/>",
            "</meta>",
            "<issue>",
            "<severity value=\"error\"/>",
            "<code value=\"processing\"/>",
            "<details>",
            "<coding>",
            "<system value=\"https://fhir.nhs.uk/STU3/CodeSystem/Spine-ErrorOrWarningCode-1\"/>",
            "<code value=\"INTERNAL_SERVER_ERROR\"/>",
            "<display value=\"Unexpected internal server error.\"/>",
            "</coding>",
            "</details>",
            "<diagnostics value=\""
    ));
    private final String RESPONSE_END = String.join(HTTP_LINE_SEPARATOR, List.of(
            "\"/>",
            "</issue>",
            "</OperationOutcome>"
    ));

    @Override
    public void report(String errorMessage, OutputStream out) {
        String error = isNullOrEmpty(errorMessage) ? DEFAULTERROR : errorMessage;
//        StringBuilder sb = new StringBuilder(INITIAL_HEADERS);
//        sb.append(Long.toString(RESPONSE_START.length() + error.length() + RESPONSE_END.length()));
//        sb.append(CONTENT_TYPE_HEADER);
        StringBuilder sb = new StringBuilder(RESPONSE_START);
        sb.append(error);
        sb.append(RESPONSE_END);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out))) {
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            Logger.getInstance().log(SEVERE, FhirLastResortReporter.class.getName(),
                    "Exception in .report(): " + e.getMessage() + "\nGiving up.");
        }
    }

}
