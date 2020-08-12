/*
 Copyright 2016  Simon Farrow <simon.farrow1@hscic.gov.uk>

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

package uk.nhs.digital.mait.tkwx.tk.internalservices;

/**
 *
 * @author simonfarrow
 */
public class ConditionalCompilationControls {

    // disable raw (unchunked/compressed) logging of responses for consistency with request logging
    public static final boolean LOG_RAW_RESPONSE = false;
}
