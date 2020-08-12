/*
 Copyright 2019  Richard Robinson robinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;

/**
 * Class to manipulate request and validation evidence for creation of a
 * multipart HTTP message. Work in progress...
 *
 * @author Richard Robinson
 */
public class EvidenceHandler {

    HttpRequest req = null;
    String validationReport = null;
    ServiceResponse sr = null;

    public EvidenceHandler() {
    }

    void addEvidence(HttpRequest req) {
        this.req = req;
    }

    void addEvidence(ServiceResponse r) {
        this.sr = r;
    }
    void addEvidence(String validationReport) {
        this.validationReport = validationReport;
    }

    void sendEvidence() throws IOException {

//        Util.readFile2String(validationReportFile.getCanonicalPath());
        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost uploadFile = new HttpPost("http://postman-echo.com/post");
            uploadFile.addHeader("Content-Type", "application/fhir+xml");
            uploadFile.addHeader("Accept", "application/fhir+xml");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

// This attaches the request:
            byte[] requestEncodedBytes = Base64.getEncoder().encode(req.getBody());
            builder.addPart(
                    "request",
                    StringBody.create(requestEncodedBytes.toString(), "text/plain",Charset.forName("UTF-8")));
// Attach the synchronous part of the response
            if (!req.getResponse().getHttpHeader().isEmpty() && req.getResponse().getHttpHeader()!= null) {
                // This attaches the response:
                byte[] responseEncodedBytes = Base64.getEncoder().encode(req.getResponse().getHttpBuffer());
                builder.addBinaryBody(
                        "syncresponse",
                        responseEncodedBytes
                );
            }
// Attach the asynchronous part of the response
            if (!sr.getResponse().isEmpty() && sr.getResponse()!= null) {
                // This attaches the asynch response:
                byte[] responseEncodedBytes = Base64.getEncoder().encode(sr.getResponse().getBytes());
                builder.addBinaryBody(
                        "asyncresponse",
                        responseEncodedBytes
                );
            }
            if (validationReport != null) {
// This attaches the Validation report:
                builder.addTextBody("validationreport",
                        validationReport
                );
            }

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            CloseableHttpResponse response = httpClient.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();
            StringWriter writer = new StringWriter();
            IOUtils.copy(responseEntity.getContent(), writer, StandardCharsets.UTF_8);
            String s = writer.toString();
            System.out.println(s);

        } catch (Exception e) {
            System.out.println("e:" + e.getLocalizedMessage());
        }
    }

}
