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
 
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation;

import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.ExtractorContext;

 /**
 * Interface implemented by classes which extract data from responses.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public interface ResponseExtractor {

    /**
     * A DataSourceListener is a data source which can be updated with
     * values extracted by an implementation of the ResponseExtractor.
     * 
     * @param c 
     */
    public void registerDatasourceListener(DataSource c);
    
    /**
     * 
     * @return the name of the extractor, from the script.
     */
    public String getName();
    
    /**
     * Do the extraction.
     * 
     * @param in String representation of the response to be extracted.
     * @throws Exception 
     */
    public void extract(String in) throws Exception;

    /**
     * Initialisation using a tokenised line from the test automation script.
     * Note that the ResponseExtractor does not extend Linkable because whilst
     * other classes may depend on the extractor, the extractor itself does not
     * depend on any other test automation classes.
     * 
     * @param extractorCtx 
     */
    public void init(ExtractorContext extractorCtx);
            
}
