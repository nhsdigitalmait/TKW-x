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
package uk.nhs.digital.mait.tkwx.tk.boot;

import java.util.Date;

/**
 *
 * @author simonfarrow
 */
public class TKWFileExtractResult {

    /**
     * "data" class
     */

    private String fileSource;
    private TKWFileType fileType;
    private String requestServiceID;
    private String requestBody;
    private String responseServiceID;
    private String responseBody;
    private String sourceIP;
    private String destIP;
    private Date timestamp;

    /**
     * public constructor
     *
     * @param fileSource
     * @param fileType
     * @param sourceIP
     * @param destIP
     * @param requestServiceID
     * @param timestamp
     * @param requestBody
     */
    public TKWFileExtractResult(String fileSource, TKWFileType fileType, String sourceIP, String destIP, Date timestamp, String requestServiceID, String requestBody) {
        this.fileSource = fileSource;
        this.fileType = fileType;
        this.requestServiceID = requestServiceID;
        this.requestBody = requestBody;
        this.sourceIP = sourceIP;
        this.destIP = destIP;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return fileSource + "," + fileType + (requestServiceID != null ? ("," + requestServiceID) : "");
    }

    /**
     * @return the fileType
     */
    public TKWFileType getFileType() {
        return fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(TKWFileType fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the requestServiceID
     */
    public String getRequestServiceID() {
        return requestServiceID;
    }

    /**
     * @param requestServiceID the requestServiceID to set
     */
    public void setRequestServiceID(String requestServiceID) {
        this.requestServiceID = requestServiceID;
    }

    /**
     * @return the requestBody
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * @param requestBody the requestBody to set
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * @return the responseServiceID
     */
    public String getResponseServiceID() {
        return responseServiceID;
    }

    /**
     * @param responseServiceID the responseServiceID to set
     */
    public void setResponseServiceID(String responseServiceID) {
        this.responseServiceID = responseServiceID;
    }

    /**
     * @return the responseBody
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody the responseBody to set
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * @return the fileSource
     */
    public String getFileSource() {
        return fileSource;
    }

    /**
     * @param fileSource the fileSource to set
     */
    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }

    /**
     * @return the sourceIP
     */
    public String getSourceIP() {
        return sourceIP;
    }

    /**
     * @return the destIP
     */
    public String getDestIP() {
        return destIP;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
}