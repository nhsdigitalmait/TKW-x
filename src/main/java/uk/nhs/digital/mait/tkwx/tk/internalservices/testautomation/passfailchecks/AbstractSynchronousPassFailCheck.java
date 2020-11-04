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
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;
import java.io.InputStream;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractPassFailCheck;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;
/**
 * The PassFailCheck classes read log files and apply tests to them. This abstract
 * subclass provides a method to extract the XML portion of a synchronous ITK response
 * from the "transmitter log file", as a SAX InputSource for use by XPath checkers.
 * Can also extract response headers
 * 
 * @author Damian Murphy murff@warlock.org
 */
abstract public class AbstractSynchronousPassFailCheck 
    extends AbstractPassFailCheck
{
    public AbstractSynchronousPassFailCheck() {
        responseBodyExtractor = new SynchronousResponseBodyExtractor();
    }
    
    /**
     * Retrieve the body of a synchronous response from a log file.
     * 
     * @param in InputStream opened for reading the log file
     * @return String containing the extracted response.
     * @throws Exception If something goes wrong, most likely java.io.IOException.
     */
    protected String getResponseBody(InputStream in)
            throws Exception
    {
        // true for return xml even if its java
        return responseBodyExtractor.getBody(in, true);
    }
    
    /**
     * Retrieve the http response headers
     * @return HttpHeaderManager for response headers
     * @throws Exception 
     */
    protected HttpHeaderManager getResponseHeaders()
            throws Exception
    {
        // true for return xml even if its java
        return responseBodyExtractor.getHttpResponseHeaders();
    }

    protected AbstractBodyExtractor responseBodyExtractor = null;
}
