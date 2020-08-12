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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.DatasourceContext;
 /**
 * Interface describing a data manager for tests.
 * 
 * @author Damian Murphy murff@warlock.org
 */

public interface DataSource 
    extends Linkable
{
    /**
     * Implement to do any finalisation that is needed.
     * @throws Exception if anything goes wrong.
     */
    public void close() throws Exception;
    
    /**
     * 
     * @return The data source name, for reporting.
     */
    public String getName();
    
    /**
     * Return the "next" record id. In a multi-record source this will start at
     * the first record then iterate through them. It is up to the implementation
     * whether it goes back to the beginning or starts returning null. In a 
     * single-record source this should return the record id and it is up to the
     * implementation whether it supports being called more than once.
     * @return 
     */
    public String getNextId();
    
    /**
     * List the tags that this data source supports.
     * @return An iteration over the tags this data source supports 
     */
    public Iterable<String> getTags();
    
    /**
     * List the tags that this data source supports.
     * @return An iteration over the tags this data source supports 
     */
    public Iterable<String> getRecordids();

    /**
     * Return the value identified by id and tag name.
     * @param id "Record" identifier.
     * @param tag "Field" identifier.
     * @return Associated value, which may be null.
     * @throws Exception if either the record or field values are invalid
     */
    public String getValue(String id, String tag) throws Exception;
    
    /**
     * Set the value identified by id and tag name. This need not do anything
     * if the implementation is read-only.
     * @param id "Record" identifier
     * @param tag "Field" identifier
     * @param value Value, which may be null
     * @throws Exception if either the record or field values do not exist.
     */
    public void setValue(String id, String tag, String value) throws Exception;
    
    /**
     * Is this data source implementation read only ?
     * @return True if the data source implementation is read only.
     */
    public boolean isReadOnly();
    
    /**
     * Does this datasource have a record for the given id
     * @param id
     * @return 
     */
    public boolean hasId(String id);
    
    /**
     * Does this datasource have a value for the given id and tag. This allows
     * the datasource to be queried for the item without throwing any exceptions
     * as would be thrown by using "getValue()" as a query.
     * @param id
     * @param value
     * @return 
     */
    public boolean hasValue(String id, String value);

    /**
     * Implement to do per-source initialisations such as loading from a file,
     * connecting to a database and so on.
     * @param datasourceCtx
     */
    public void init(DatasourceContext datasourceCtx);
    
    /**
     * a datasource can have 0..1 extractors associated
     * @param extractorName 
     */
    public void setExtractorName(String extractorName);
}
