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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.AbstractPassFailCheck;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;

/**
 * Synchronous pass/fail check that considers finding non empty content to be a fail.
 * This caters for chunked (and unchunked) responses where explictly checking
 * the http Content-Length header will not suffice.
 * The use may employ an http header check if they wish to explictly examine 
 * that header value as opposed to determining the size of  payload.
 * Does not honour extractors since the content is expected to be empty
 *
 * @author Damian Murphy murff@warlock.org
 */
public class HttpZeroContentLength
        extends AbstractPassFailCheck {

    @Override
    public TestResult passed(Script s, InputStream in, InputStream inRequest)
            throws Exception {
        SynchronousResponseBodyExtractor be = new SynchronousResponseBodyExtractor();
        String body= be.getBody(in);
        int length = body.length();
       
        setDescription(colourString("Expected: ", BLACK));

        if (length == 0) {
            appendDescription(colourString(CONTENT_LENGTH + " 0.", GREEN));
            appendDescription(colourString(" Actual: ", BLACK));
            appendDescription(colourString(CONTENT_LENGTH + " 0.", GREEN));
            return TestResult.PASS;
        }
        appendDescription(colourString(CONTENT_LENGTH + " 0", RED));
        appendDescription(colourString(" Actual: ", BLACK));
        appendDescription(colourString(length+"", RED));
        return TestResult.FAIL;
    }
    
    private final static String CONTENT_LENGTH = "Length of content";
}
