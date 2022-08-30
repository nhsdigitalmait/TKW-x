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
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.DataSource;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Linkable;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.Script;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.TestResult;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassFailCheckContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser.AutotestParser.PassfailContext;
 /**
 * Interface implemented by the various classes used to determine whether
 * a test is considered to have passed.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public interface PassFailCheck 
    extends Linkable
{
    /**
     * 
     * @param passfailCtx
     * @throws Exception 
     */
    public void init(PassfailContext passfailCtx) throws Exception;

    /**
     *
     * @param passfailCheckCtx
     * @throws Exception
     */
    public void init(PassFailCheckContext passfailCheckCtx) throws Exception;
    public String getName();
    public String getDescription();
    
    /**
     * Implement to provide the check on a response message.
     * @param s Script content
     * @param inResponse Input stream (e.g. from network or file) from which to read the message
     * @param inRequest request input stream
     * @return TestResult with a status of passed if passed
     * @throws Exception if something goes wrong
     */
    public TestResult passed(Script s, InputStream inResponse, InputStream inRequest) throws Exception;     

    /**
     * allows access to the relevant row of a tdv table
     * @param datasource
     * @param recordid 
     */
    public void setDatasourceDetails(DataSource datasource, String recordid);

    public String getExtractorName();
}
