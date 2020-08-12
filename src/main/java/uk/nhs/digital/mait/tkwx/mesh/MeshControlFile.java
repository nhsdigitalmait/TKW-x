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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 * Class to encapsulate Mesh control files. Can be used to parse an existing
 * mesh control file or create a new one In the first instance this is for
 * control files in the IN mailbox but this class is intended to be extended to
 * encompass control files for all mailboxes.
 *
 * @author riro
 */
public class MeshControlFile {

    private Path controlFilePath;
    private String version = null;
    private String addressType = null;
    private String messageType = null;
    private String workflowId = null;
    private String localId = null;
    private String fromDTS = null;
    private String toDTS = null;
    private String dtsId = null;
    private String ctlFileContents = null;
    private String ctlTemplate = null;
    private MeshMessage meshMessage = null;
    private MeshControlStatus controlStatus = null;
    private Map<String, String> workflowIdXref = null;

    private static final String WORKFLOWIDXREFFILE = "tks.MeshTransport.workflowidxreffile";
    private static Configurator config;

    protected static final String VERSIONXPATH = "/DTSControl/Version";
    protected static final String ADDRESSTYPEXPATH = "/DTSControl/AddressType";
    protected static final String MESSAGETYPEXPATH = "/DTSControl/MessageType";
    protected static final String WORKFLOWIDXPATH = "/DTSControl/WorkflowId";
    protected static final String FROMDTSXPATH = "/DTSControl/From_DTS";
    protected static final String TODTSXPATH = "/DTSControl/To_DTS";
    protected static final String LOCALIDXPATH = "/DTSControl/LocalId";
    protected static final String DTSIDXPATH = "/DTSControl/DTSId";
    protected static final String CONTROLTEMPLATE = "mesh-control-template.txt";
    private Instant transmissionTime = null;

    public MeshControlFile(MeshMessage mm) throws Exception {
        meshMessage = mm;
        fromDTS = mm.getMeshMailboxId();
        config = Configurator.getConfigurator();

        try {
            String workflowIdXrefFile = config.getConfiguration(WORKFLOWIDXREFFILE);
            if ((workflowIdXrefFile == null) || (workflowIdXrefFile.trim().length() == 0)) {
                throw new Exception("MESH configuration workflowIdxreffile has not been set in the properties");
            }
            Path path = FileSystems.getDefault().getPath(workflowIdXrefFile);
            workflowIdXref = Utils.readColonSeparatedFile2Map(workflowIdXrefFile);
        } catch (Exception e) {
            throw new Exception("Failure to retrieve MESH workflowIdxreffile properties - " + e.toString());
        }

    }

    /**
     * Method used for creating a MESH Control file for MESH responses
     *
     * @param to The toDTS mailbox to use to construct the control file
     * @param li localId
     * @throws Exception
     */
    public void createMeshControlFile(String to, String li) throws Exception {

        version = "1.0";
        addressType = "DTS";
        messageType = "Data";
        toDTS = to;

        localId = li;
        ctlFileContents = updateTemplate();

        transmissionTime = Instant.now();
    }

    /**
     * Method to get a workflowId associated with a request
     *
     * @param requestingWorkflowId Workflow Id from the requesting message
     * @return workflowId for the response
     * @throws Exception
     * @throws IOException
     */
    public String getWorkflowIdFromXref(String requestingWorkflowId) throws Exception, IOException {

        String wfi = workflowIdXref.get(requestingWorkflowId);
        if ((wfi == null) || (wfi.trim().length() == 0)) {
            throw new Exception("Cannot find MESH requesting workflow id (" + requestingWorkflowId + ") in the workflow Id Xref file");
        }
        return wfi;
    }

    private String updateTemplate() throws Exception {

        ctlTemplate = readFile2String(getClass().getResourceAsStream(CONTROLTEMPLATE));
        ctlTemplate = ctlTemplate.replace("__VERSION__", version);
        ctlTemplate = ctlTemplate.replace("__ADDRESSTYPE__", addressType);
        ctlTemplate = ctlTemplate.replace("__MESSAGETYPE__", messageType);
        ctlTemplate = ctlTemplate.replace("__WORKFLOWID__", workflowId);
        ctlTemplate = ctlTemplate.replace("__FROMDTS__", fromDTS);
        ctlTemplate = ctlTemplate.replace("__TODTS__", toDTS);
        ctlTemplate = ctlTemplate.replace("__LOCALID__", localId);
        if (controlStatus != null) {
            ctlTemplate = ctlTemplate.replace("__CONTROLSTATUSTEMPLATE__", controlStatus.updateTemplate());
        } else {
            ctlTemplate = ctlTemplate.replace("__CONTROLSTATUSTEMPLATE__", "");

        }
        return ctlTemplate;

    }

    /**
     * Method used for parsing a received MESH Control file
     *
     * @param file The file location to find the message
     * @throws Exception
     */
    public void parseMeshControlFile(Path file) throws Exception {
        controlFilePath = file;
        if (file == null || !file.isAbsolute()) {
            throw new Exception("Path to Mesh Control file is missing or invalid");
        }
        try {
            ctlFileContents = new String(Files.readAllBytes(controlFilePath));
            BasicFileAttributes attr = Files.readAttributes(controlFilePath, BasicFileAttributes.class);
            transmissionTime = attr.creationTime().toInstant();

            version = XPathManager.xpathExtractor(VERSIONXPATH, ctlFileContents);
            addressType = XPathManager.xpathExtractor(ADDRESSTYPEXPATH, ctlFileContents);
            messageType = XPathManager.xpathExtractor(MESSAGETYPEXPATH, ctlFileContents);
            workflowId = XPathManager.xpathExtractor(WORKFLOWIDXPATH, ctlFileContents);
            localId = XPathManager.xpathExtractor(LOCALIDXPATH, ctlFileContents);
            dtsId = XPathManager.xpathExtractor(DTSIDXPATH, ctlFileContents);
            controlStatus = new MeshControlStatus(this);
            controlStatus.parseStatus();
            if (version.equals("")) {
                throw new Exception("version element is missing or empty in Mesh control file: " + controlFilePath.toString());
            } else if (addressType.equals("")) {
                throw new Exception("address type element is missing or empty in Mesh control file: " + controlFilePath.toString());
            } else if (messageType.equals("")) {
                throw new Exception("message type element is missing or empty in Mesh control file: " + controlFilePath.toString());
            }
            toDTS = XPathManager.xpathExtractor(TODTSXPATH, ctlFileContents);
            fromDTS = XPathManager.xpathExtractor(FROMDTSXPATH, ctlFileContents);
        } catch (IOException ioe) {
            throw new Exception("Can't read the Mesh control file: " + ioe.getMessage());
        } catch (XPathExpressionException | XPathFactoryConfigurationException e) {
            throw new Exception("Can't parse the Mesh control file. Missing: " + e.getMessage());
        }
    }

    // logging of the initial parsing of the file cannot happen until the 
    // requestId is known, hence a separate method
    public void logControlFile(String type) throws Exception {
        // Write filename of request
        meshMessage.log(type+" filename: " + controlFilePath.getFileName(), 1, false);
        // Log transmission date and time
        meshMessage.log("Transmission date and time (inferred from the file creation date): " + transmissionTime, 1, false);
        // log transmission sender:
        meshMessage.log("Transmission sender: " + fromDTS, 1, false);
        meshMessage.log("Transmission destination: " + toDTS, 1, true);

        meshMessage.log("Control File: " + controlFilePath.toString(), 2, false);
        meshMessage.log(ctlFileContents, 2, false);
    }

    public MeshControlStatus getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(MeshControlStatus controlStatus) {
        this.controlStatus = controlStatus;
    }

    public String getVersion() {
        return version;
    }

    public String getAddressType() {
        return addressType;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    void setWorkflowId(String s) {
        workflowId = s;
    }

    public String getCtlFileContents() {
        return ctlFileContents;
    }

    public String getFromDTS() {
        return fromDTS;
    }

    public void setFromDTS(String s) {
        fromDTS = s;
    }

    public String getToDTS() {
        return toDTS;
    }

    public String getLocalId() {
        return localId;
    }

    public String getDtsId() {
        return dtsId;
    }

    public Path getControlFilePath() {
        return controlFilePath;
    }

    public void setControlFilePath(Path path) {
        controlFilePath = path;
    }

    public Instant getTransmissionTime() {
        return transmissionTime;
    }
}
