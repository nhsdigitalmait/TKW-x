/*
  Copyright 2018  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.mesh;

import java.io.InputStream;
import java.util.HashMap;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 * Class to encapsulate the data and operations of the Mesh request.
 *
 * This class is designed to encapsulate a MeshMessage class and allow access to
 * Mesh data from the TKW framework
 *
 * @author Richard Robinson
 */
public class MeshRequest extends Request {

    private MeshMessage requestMeshMessage = null;
    private HashMap<String, MeshMessage> responseMeshMessages = new HashMap<>();
    protected LoggingFileOutputStream logStream = null;
    private String priorityCode = null;
    private String priorityDisplay = null;
    // This is a more general meta data style than HttpRequest
    private HashMap<String, String> hmHeaders = new HashMap<>();

    public MeshRequest() {
    }

    public MeshMessage getRequestMeshMessage() {
        return requestMeshMessage;
    }

    public void setRequestMessage(MeshMessage mm) {
        this.requestMeshMessage = mm;
    }

    public void addResponseMeshMessage(MeshMessage mm) {
        responseMeshMessages.put(mm.getMeshFileName(), mm);
    }

    public LoggingFileOutputStream getLoggingStream() {
        return logStream;
    }

    public void setLoggingStream(LoggingFileOutputStream lfos) {
        this.logStream = lfos;
        requestMeshMessage.setLoggingStream(lfos);
    }

    @Override
    public void setRequestContext(String c) throws Exception {
    }

    @Override
    public void setRequestType(String r) throws Exception {
    }

    @Override
    public void setInputStream(InputStream h) {
    }

    @Override
    public void setContentLength(int c) throws Exception {
    }

    @Override
    public String getField(String h) {
        return hmHeaders.get(h);
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public String getPriorityDisplay() {
        return priorityDisplay;
    }

    public void setPriorityDisplay(String priorityDisplay) {
        this.priorityDisplay = priorityDisplay;
    }

    @Override
    public void setHeader(String h, String v) throws Exception {
        hmHeaders.put(h, v);
    }

    @Override
    public void updateHeader(String h, String v) throws Exception {
        setHeader(h, v);
    }

}
