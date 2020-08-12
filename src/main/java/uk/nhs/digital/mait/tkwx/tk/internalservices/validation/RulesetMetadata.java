/*
  Copyright 2015  Richard Robinson Murphy rrobinson@hscic.gov.uk

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

/**
 * Encapsulates name, timestamp, version and author
 * This is a "data" object containing only Strings
 * 
 * @author riro
 */
public class RulesetMetadata {
    private String name = null;
    private String version = null;
    private String timestamp = null;
    private String author = null;
    
    public String getName(){
        return name;
    }
    public String getVersion(){
        return version;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public String getAuthor(){
        return author;
    }
    
    public void setName(String s){
        name = s;
    }
    public void setVersion(String s){
        version = s;
    }
    public void setTimestamp(String s){
        timestamp = s;
    }
    public void setAuthor(String s){
        author = s;
    }
}
