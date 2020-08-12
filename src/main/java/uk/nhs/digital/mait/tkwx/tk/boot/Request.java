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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.io.InputStream;

/**
 * Abstract class to encapsulate the data and operations around all requests.
 *
 * @author Damian Murphy murff@warlock.org
 */
public abstract class Request {

    protected String sourceId;

    /**
     * Get the value of the given header.
     *
     * @param h The header name, case-insensitive.
     * @return The header value, or null if no such header is present.
     */
    abstract public String getField(String h);
    abstract public void setHeader(String h, String v) throws Exception;
    abstract public void updateHeader(String h, String v)throws Exception;

    String getSourceId() {
        return sourceId;
    }

    abstract public void setRequestContext(String c) throws Exception;

    abstract public void setRequestType(String r) throws Exception;

    abstract public void setInputStream(InputStream h);

    abstract public void setContentLength(int c) throws Exception;
}
