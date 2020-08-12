/*
 Copyright 2020  Richard Robinson <rrobinson@nhs.net>

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
package uk.nhs.digital.mait.tkwx.tk.internalservices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;

/**
 *
 * @author riro
 *
 * The LoggingFileOutputStream extends FileOutputStream which is used to write
 * bytes to disk for logging. Optionally this class allows metadata regarding
 * those logs to be recorded and written to file when the interaction has fully
 * completed. There is one instance of LoggingFileOutputStream per log/artefact
 * written to disk
 */
public class LoggingFileOutputStream extends FileOutputStream {

    EvidenceMetaDataHandler evidenceMetaDataHandler = null;
    String fileName = null;
    String type = null;
    String description = null;
    String docurl = null;

    
    /**
     * Constructor for a full path String name 
     * @param name
     * @throws FileNotFoundException
     */
    public LoggingFileOutputStream(String name) throws FileNotFoundException {
        super(name);
        fileName = name;
    }

    /**
     * Constructor for a full path File 
    *
     * @param logFile
     * @throws FileNotFoundException
     */
    public LoggingFileOutputStream(File logFile) throws FileNotFoundException {
        super(logFile);
        fileName = logFile.getAbsolutePath();
    }

    /**
     * Set the description for a piece of metadata evidence
     *
     * @param type
     * @param description
     */
    public void setMetaDataDescription(String type, String description) {
        this.type = type;
        this.description = description;
    }

    /**
     * Set the location of documentation for a piece of metadata evidence
     *
     * @param docurl
     */
    public void setMetaDataDocUrl(String docurl) {
        this.docurl = docurl;
    }

    /**
     * Sets the evidenceMeatDataHandler - this is optional dependent on config
     *
     * @param evidenceMetaDataHandler
     */
    public void setEvidenceMetaDataHandler(EvidenceMetaDataHandler evidenceMetaDataHandler) {
        this.evidenceMetaDataHandler = evidenceMetaDataHandler;
    }

    /**
     * Write method to accept Strings and convert to byte
     *
     * @param requestType
     * @throws IOException
     */
    public void write(String requestType) throws IOException {
        super.write(requestType.getBytes());
    }

    /**
     * Calls to endMainThread will mark the metatdata file as completed and
     * unless unfinished subthreads are still running will complete the
     * metatdata
     */
    public void endMainThread() {
        if (evidenceMetaDataHandler != null) {
            // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
            evidenceMetaDataHandler.mainThreadEnded();
        }
    }

    /**
     * Returns evidenceMetadataHandler or null if not used.
     *
     * @return
     */
    public EvidenceMetaDataHandler getEvidenceMetaDataHandler() {
        return evidenceMetaDataHandler;
    }

    /**
     * Returns filename of the Metadata file
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Once the evidence has been written to disk log Complete is called to add
     * the reference to the metatdata
     */
    public void logComplete() {
        if (evidenceMetaDataHandler != null) {
            evidenceMetaDataHandler.addMetaData(type, description, docurl, fileName);
        }
    }

}
