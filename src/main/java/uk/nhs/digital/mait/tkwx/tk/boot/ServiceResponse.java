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
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
/**
 * Class to encapsulate a TKW internal service response. This is just a bunch
 * of fields, and the boilerplate accessor methods for them.
 * 
 * @author Damian Murphy murff@warlock.org
 */ 
public class ServiceResponse {

    // These are coded response codes for when ServiceResponse is used by Validation responses
    public static final int NOTRUN = 0;
    public static final int ALLPASSED = 1;
    public static final int NOTALLPASSED = 2;
    
    private int responseCode = 0;
    private String responseCodePhrase = null;
    private String response = null;
    private String action = null;
    private Object scalar = null;
    private Object[] array = null;
    private HttpHeaderManager httpHeaders;
    

    public ServiceResponse() {}

    public ServiceResponse(int code, String s) {
        responseCode = code;
        response = s;
    }

    public void setAction(String a) { action = a; }
    public void setArray(Object[] a) { array = a; }
    public void setScalar(Object o) { scalar = o; }
    public void setCode(int i) { responseCode = i; }
    public void setCodePhrase(String s) { responseCodePhrase = s; }
    public void setResponse(String s) { response = s; }
    public String getAction() { return action; }
    public int getCode() { return responseCode; }
    public String getResponse() { return response; }
    public String getCodePhrase() { return responseCodePhrase; }
    public Object getScalar() { return scalar; }
    public Object[] getArray() { return array; }

    public void setHttpHeaders(HttpHeaderManager httpHeaders) { this.httpHeaders = httpHeaders;}
    public HttpHeaderManager getHttpHeaders() { return httpHeaders;}

}
