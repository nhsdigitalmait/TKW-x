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

package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

/**
 * Interface implemented by classes implementing java extensions to expressions
 * 
 * @author Simon Farrowsimon.farrow1@nhs.net
 */
public interface ExpressionValue {

    /**
     * Called when the request is received (NB not when the tag is evaluated)
     * @param values String[] of values extracted from the text input used to evaluate the expression
     * @param args String[] of arguments may be null or empty used for eg "<", ">" etc for string or date comparisons
     * @return the boolean result
     * @throws Exception
     */
    public boolean getValue(String[] values, String[] args) throws Exception;
}
