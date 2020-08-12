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

package uk.nhs.digital.mait.tkwx.tk.internalservices.send;

import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 * Encapsulates a request to send a message. The message may be sent either
 * via a SOAP web service or written to a queue.
 * 
 * @author Damian Murphy murff@warlock.org
 */
public class SenderRequest {

    private String ods = null;
    private String asid = null;
    private String partyKey = null;
    private String originalFileName = null;
    private String payload = null;
    private String wrapperTemplate = null;
    private String address = null;
    private String action = null;
    private String relatestomessageid = null;
    private int chunkSize = 0;
    private String httpMethod;
    private EvidenceMetaDataHandler evidenceMetaDataHandler = null;
    private String loggingSubDir = null;
    
    /**
     * public constructor
     * @param p
     * @param w
     * @param a 
     */
    public SenderRequest(String p, String w, String a, String httpMethod) {
        payload = p;
        wrapperTemplate = w;
        address = a;
        this.httpMethod = httpMethod;
    }

    /**
     * public constructor
     * @param p
     * @param w
     * @param a 
     */
    public SenderRequest(String p, String w, String a) {
        payload = p;
        wrapperTemplate = w;
        address = a;
    }

    /**
     *  Constructor for SpineTools messaging
     * @param a svcId "SvcIA" service-qualified interaction id
     * @param p payload directory location
     */
    public SenderRequest(String a, String p) {
        action = a;
        payload = p;
    }

    public void setODSCode(String s) { ods = s; }
    public String getODSCode() { return ods; }

    public void setAsid(String s) { asid = s; }
    public String getAsid() { return asid; }

    public void setPartyKey(String s) { partyKey = s; }
    public String getPartyKey() { return partyKey; }

    public void setOriginalFileName(String s) { originalFileName = s; }
    public String getOriginalFileName() { return originalFileName; }
    
    public void setChunkSize(int c) { chunkSize = c; }
    public void setRelatesTo(String r) { relatestomessageid = r; }
    public void setAction(String a) { action = a; }
    public String getAction() { return action; }
    public String getRelatesTo() { return relatestomessageid; }

    public String getAddress() { return address; }
    public String getPayload() { return payload; }
    public String getWrapperTemplate() { return wrapperTemplate; }
    public int chunkSize() { return chunkSize; }

    /**
     * defaults to POST if not explcitly set
     * @return the httpMethod
     */
    public String getHttpMethod() {
        return httpMethod == null  ? "POST" : httpMethod ;
    }

    /**
     * @param httpMethod the httpMethod to set
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setEvidenceMetaDataHandler(EvidenceMetaDataHandler emdh) {
        evidenceMetaDataHandler = emdh;
    }

    EvidenceMetaDataHandler getEvidenceMetaDataHandler() {
        return evidenceMetaDataHandler;
    }

    String getLoggingSubDir() {
        return loggingSubDir;
    }

    public void setLoggingSubDir(String subDir) {
        loggingSubDir = subDir;
    }
}
