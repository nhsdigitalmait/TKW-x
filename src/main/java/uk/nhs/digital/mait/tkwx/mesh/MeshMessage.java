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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 * Class to encapsulate the data and operations of the Mesh request.
 *
 * This class is designed to hold references to the control and data classes
 * that describe a MESH message and allow operations at an interaction level
 *
 * @author Richard Robinson
 */
public class MeshMessage {

    protected Path outbox = null;
    protected Path temp = null;
    protected String meshFileName = null;
    private MeshControlFile control;
    private MeshData data;
    private String siteId = null;
    private static final String INTERMEDIARYTEMPDIR = "tks.MeshTransport.tkwintermediarytempdir";
    private static final String MESHSITEID = "tks.MeshTransport.siteid";
    private static Configurator config;
    private LoggingFileOutputStream logStream = null;
    private String originatingRequestId = null;
    private String meshMailboxId = null;

    /**
     * Constructor to configure class
     *
     * @param m mailbox id
     * @throws Exception
     */
    public MeshMessage(String m) throws Exception {
        meshMailboxId = m;
        control = new MeshControlFile(this);
        try {
            config = Configurator.getConfigurator();
            String s = config.getConfiguration(INTERMEDIARYTEMPDIR);
            if ((s != null) && (s.trim().length() > 0)) {
                temp = Paths.get(s);
            } else {
                System.err.println("MESH configuration " + INTERMEDIARYTEMPDIR + " has not been set in the properties");
            }
            
            String meshOutDir = "tks.MeshTransport." + meshMailboxId + ".out";
            s = config.getConfiguration(meshOutDir);
            if ((s != null) && (s.trim().length() > 0)) {
                outbox = Paths.get(s);
            } else {
                System.err.println("MESH configuration " + meshOutDir + " has not been set in the properties");
            }
            s = config.getConfiguration(MESHSITEID);
            if ((s != null) && (s.trim().length() > 0)) {
                siteId = s;
            } else {
                System.err.println("MESH configuration " + MESHSITEID + " has not been set in the properties");
            }
        } catch (Exception e) {
            System.err.println("Failure to convert MESH path property to a path - check that the path is correct - " + e.toString());
        }
    }

    /**
     * Method which takes a path reference to an incoming Mesh Request and
     * parses it into MeshControl class
     *
     * @param cfile
     * @throws Exception
     */
    public void parseControl(Path cfile) throws Exception {
        meshFileName = cfile.getFileName().toString().replace(".ctl", "");
        control.parseMeshControlFile(cfile);

    }

    /**
     * Method which parses existing references gained when parsing the control
     * file into a MeshData class
     *
     * @throws uk.nhs.digital.mait.tkwx.mesh.MeshDataException
     */
    public void parseData() throws MeshDataException {
        Path meshFilePath = control.getControlFilePath().getParent();
        data = new MeshData(this);
        data.parseMeshData(meshFilePath);
    }

    /**
     * Method to create Mesh Response from message. The files are initially
     * created in the temp file then transferred to the Outbox
     *
     * @param toDTS
     * @param message
     * @param requestingWorkflowId
     * @param localId
     * @throws Exception
     */
    public void createResponse(String toDTS, String message, String requestingWorkflowId, String localId) throws Exception {
        meshFileName = siteId + UUID.randomUUID().toString();
        String wfi;
        try {
            wfi = control.getWorkflowIdFromXref(requestingWorkflowId);
        } catch (Exception e) {
            throw new Exception(e.getMessage() + " for id = " + localId);
        }
        if (wfi.equals("NO_RESPONSE_WORKFLOWID")) {
            throw new Exception("This MESH requesting workflow id (" + requestingWorkflowId + ") is a vaild receiving workflow if but does not have a corresponding response");
        }
        control.setWorkflowId(wfi);
        control.createMeshControlFile(toDTS, localId);
        control.setControlFilePath(saveToTempDirectory(control.getCtlFileContents(), ".ctl"));
        control.logControlFile("Response");
        data = new MeshData(this);
        data.createMeshData(message, saveToTempDirectory(message, ".dat"));
        data.logDataFile(LogMarkers.END_OUTBOUND_MARKER);
        Thread.sleep(1000);
        moveToOutboxDirectory(data.getDataFilePath());
        moveToOutboxDirectory(control.getControlFilePath());
    }

    /**
     * Method to create Mesh Request from forwarding mesh message. The files are
     * initially created in the temp file then transferred to the Outbox
     *
     * @param id
     * @throws Exception
     */
    public void createRequest(String id) throws Exception {
        // In order to take the MESH message and forward it we will need to 
        // recreate it, substituting in any values that require substituting
        //Substitute any required information
        control.setFromDTS(getMeshMailboxId());
        control.setControlStatus(null);
        control.createMeshControlFile(control.getToDTS(), id);
        control.logControlFile("Forwarding");
        control.setControlFilePath(saveToTempDirectory(control.getCtlFileContents(), ".ctl"));

        data.setDataFilePath(saveToTempDirectory(new String(data.getContent()), ".dat"));
        data.logDataFile(LogMarkers.END_FORWARDING_MARKER);

        Thread.sleep(1000);
        moveToOutboxDirectory(data.getDataFilePath());
        moveToOutboxDirectory(control.getControlFilePath());

    }

    /**
     * Protected method which allows the Control and Data classes to save to a
     * temp directory. This folder is a temporary directory to use so that
     * processes which take a bit of time can complete before the MESH client
     * tries to handle them. The MESH spec says that the MESH temp directory is
     * private to the MESH client and so should not be used for this purpose
     *
     * @param content
     * @param extension
     * @return
     * @throws Exception
     */
    protected Path saveToTempDirectory(String content, String extension) throws Exception {
        return saveToDirectory(temp, content, extension);
    }

    public void moveToOutboxDirectory(Path p) throws Exception {
        Files.move(p, Paths.get(outbox.toString(), p.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);

    }

    private Path saveToDirectory(Path path, String content, String extension) throws Exception {
        Path p = Paths.get(path.toString(), meshFileName + extension);
        return Files.write(p, content.getBytes());

    }

    public void deleteFile(Path filePath) throws Exception {
        Files.deleteIfExists(filePath);
    }

    public String getMeshFileName() {
        return meshFileName;
    }

    public MeshControlFile getControlFile() {
        return control;
    }

    public MeshData getDataFile() {
        return data;
    }

    public void setData(MeshData data) {
        this.data = data;
    }

    public void setLoggingStream(LoggingFileOutputStream lfos) {
        logStream = lfos;
    }

    /**
     * Method which allows classes in this package to call log
     *
     * @param s
     * @param numberOfNewLines
     * @param flush
     */
    public synchronized void log(String s, int numberOfNewLines, boolean flush) {
        try {
            if (logStream != null) {

                while (numberOfNewLines > 0) {
                    s = s + String.format("%n");
                    numberOfNewLines--;
                }
                logStream.write(s);
                if (flush) {
                    logStream.flush();
                }
            }
        } catch (IOException ioe) {
            Logger l = Logger.getInstance();
            l.log(s + ioe.toString());

        }
    }

    public void setOriginatingRequestId(String requestId) {
        originatingRequestId = requestId;
    }

    public String getOriginatingRequestId() {
        return originatingRequestId;
    }

    public String getMeshMailboxId() {
        return meshMailboxId;
    }

    public LoggingFileOutputStream getLoggingStream() {
        return logStream;
    }

    // indicate to the evidenceMetaDataHandler that the interaction metadata can now be written unless there are unended subthreads
    void closeLog() {
        try {
            logStream.logComplete();
            logStream.close();
            logStream.endMainThread();
        } catch (IOException ioe) {
            Logger l = Logger.getInstance();
            l.log("attempt to close log: " + ioe.toString());
        }
    }

}
