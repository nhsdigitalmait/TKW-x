/**
 * Copyright 2013 Simon Farrow <simon.farrow1@nhs.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Simon Farrow simon.farrow1@hscic.gov.uk
 */
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.passfailchecks;

import uk.nhs.digital.mait.tkwx.util.bodyextractors.AsynchronousResponseBodyExtractor;

/**
 * Abstract base class for asynchronous pass fail checks looking across the
 * request and response messages. For asynchronous interactions these are
 * contained in different log files, typically in transmitter_sent_messages and
 * simulator _saved_messages.
 *
 * @author SIFA2
 */
public abstract class AbstractAsynchronousRequestResponseComparatorPassFailCheck extends AbstractRequestResponseComparatorPassFailCheck {

    public AbstractAsynchronousRequestResponseComparatorPassFailCheck() {
        responseBodyExtractor = new AsynchronousResponseBodyExtractor();
    }
}
