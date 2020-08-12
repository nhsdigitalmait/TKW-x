/*
 Copyright 2018  Simon Farrow <simon.farrow1@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.util.bodyextractors;

import java.io.InputStream;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;

/**
 * This adapter wraps an HttpRequest so that it can be used as a BodyExtractor within validations
 * @author simonfarrow
 */
public class HttpRequestBodyExtractorAdapter extends AbstractBodyExtractor {

    private final HttpRequest httpRequest;

    /**
     * public constructor taking an HttpRequest
     * @param httpRequest 
     */
    public HttpRequestBodyExtractorAdapter(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * ignores the parameter as its already available
     * @param is
     * @param getXML
     * @return
     * @throws Exception 
     */
    @Override
    public String getBody(InputStream is, boolean getXML) throws Exception {
        return new String(httpRequest.getBody());
    }

    /**
     * @return the httpRequestManager
     */
    @Override
    public HttpHeaderManager getHttpRequestHeaders() {
        return httpRequest.getHeaderManager();
    }

    /**
     * null as a http request by definition has no response data
     * @return the httpResponseHeaders
     */
    @Override
    public HttpHeaderManager getHttpResponseHeaders() {
        return null;
    }
    
    /**
     * return request headers for request be and response headers for response be
     * @return 
     */
    @Override
    public HttpHeaderManager getRelevantHttpHeaders() {
        return httpRequest.getHeaderManager();
    }


    /**
     * @return the httpRequestContextPath
     */
    @Override
    public String getHttpRequestContextPath() {
        return httpRequest.getContext();
    }

    /**
     * eg GET, POST, PUT etc
     * @return the httpRequestMethod
     */
    @Override
    public String getHttpRequestMethod() {
        return httpRequest.getRequestType();
    }

}
