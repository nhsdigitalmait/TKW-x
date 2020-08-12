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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static java.util.logging.Level.SEVERE;

/**
 * Emergency class for returning Spine errors.
 *
 * @author Damian Murphy murff@warlock.org
 */
class LastResortReporter {

    private static final String f1 = "HTTP/1.1 500 OK\r\nContent-Length: ";
    private static final String f2 = "\r\nContent-Type: text/xml\r\n\r\n";
    private static final String f3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n<soap:Body>\r\n<soap:Fault>\r\n<faultcode>soap:Server</faultcode>\r\n<faultstring>System failure to process message</faultstring>\r\n<detail>\r\n<nasp:errorList xmlns:nasp=\"http://national.carerecords.nhs.uk/schema/\">\r\n<nasp:error>\r\n<nasp:codeContext>urn:nhs:names:errors:tms</nasp:codeContext>\r\n<nasp:errorCode>200</nasp:errorCode>\r\n<nasp:severity>Error</nasp:severity>\r\n<nasp:location/>\r\n<nasp:description>";
    private static final String f4 = "</nasp:description>\r\n</nasp:error>\r\n</nasp:errorList>\r\n</detail>\r\n</soap:Fault>\r\n</soap:Body>\r\n</soap:Envelope>";
    private static final String DEFAULTERROR = "System failure to process message";

    static void report(String errorMessage, OutputStream out) {
        try {
            String error;
            if ((errorMessage == null) || (errorMessage.trim().length() == 0)) {
                error = DEFAULTERROR;
            } else {
                error = errorMessage;
            }
            StringBuilder sb = new StringBuilder(f1);
            sb.append(Long.toString(error.length() + f3.length() + f4.length()));
            sb.append(f2);
            sb.append(f3);
            sb.append(error);
            sb.append(f4);
//            System.out.println("prelastresortbufferwrite");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
//            System.out.println("postlastresortbufferwrite" + sb.toString()+","+out.toString());
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, LastResortReporter.class.getName(),
                    "Exception in .report(): " + e.getMessage() + "\nGiving up.");
        }
    }
}
