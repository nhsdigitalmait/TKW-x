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
package uk.nhs.digital.mait.tkwx.http;

/**
 * Class to hold HTTP header fields.
 * 
 * @author Damian Murphy murff@warlock.org
 */ 
public class HttpFields {
    
    private final String header;
    private final String value;
    
    /** Creates a new instance of HttpFields
     * @param h
     * @param v */
    public HttpFields(String h, String v) {
        header = h;
        value = v;
    }
    
    public String getValue() { return value; }
    public String getActualHeader() { return header; }
}
