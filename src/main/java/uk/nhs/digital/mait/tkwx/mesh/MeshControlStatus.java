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

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import static uk.nhs.digital.mait.tkwx.util.Utils.readFile2String;

/**
 * Class to encapsulate Mesh control Statuses. Can be used to parse an existing
 * mesh control status block or create a new one
 *
 * @author riro
 */
public class MeshControlStatus {

    private String ctlStatusTemplate = null;
    private String dateTime = null;
    private String event = null;
    private String status = null;
    private String statusCode = null;
    private String description = null;
    private MeshControlFile meshControl = null;

    protected static final String STATUSDATETIMEXPATH = "/DTSControl/StatusRecord/DateTime";
    protected static final String STATUSEVENTXPATH = "/DTSControl/StatusRecord/Event";
    protected static final String STATUSSTATUSXPATH = "/DTSControl/StatusRecord/Status";
    protected static final String STATUSSTATUSCODEXPATH = "/DTSControl/StatusRecord/StatusCode";
    protected static final String STATUSDESCRIPTIONXPATH = "/DTSControl/StatusRecord/Description";
    protected static final String DTSIDXPATH = "/DTSControl/DTSId";
    protected static final String CONTROLSTATUSTEMPLATE = "mesh-control-status-template.txt";

    /**
     * Constructor used for creating MESH Control status
     *
     * @param controlFile The MeshControlFile associated with this status class
     * @throws Exception
     */
    public MeshControlStatus(MeshControlFile controlFile) throws Exception {
        meshControl = controlFile;
    }

    public String createStatus(String dt, String e, String s, String sc, String d) throws Exception {
        dateTime = dt;
        event = e;
        status = s;
        statusCode = sc;
        description = d;
        return updateTemplate();
    }

    protected String updateTemplate() throws Exception {

        ctlStatusTemplate = readFile2String(getClass().getResourceAsStream(CONTROLSTATUSTEMPLATE));
        ctlStatusTemplate = ctlStatusTemplate.replace("__DATETIME__", dateTime);
        ctlStatusTemplate = ctlStatusTemplate.replace("__EVENT__", event);
        ctlStatusTemplate = ctlStatusTemplate.replace("__STATUS__", status);
        ctlStatusTemplate = ctlStatusTemplate.replace("__STATUSCODE__", statusCode);
        ctlStatusTemplate = ctlStatusTemplate.replace("__DESCRIPTION__", description);
        return ctlStatusTemplate;

    }

    /**
     * Method used for parsing a received MESH Control status block
     *
     * @throws Exception
     */
    public void parseStatus() throws Exception {
        try {
            dateTime = XPathManager.xpathExtractor(STATUSDATETIMEXPATH, meshControl.getCtlFileContents());
            event = XPathManager.xpathExtractor(STATUSEVENTXPATH, meshControl.getCtlFileContents());
            status = XPathManager.xpathExtractor(STATUSSTATUSXPATH, meshControl.getCtlFileContents());
            statusCode = XPathManager.xpathExtractor(STATUSSTATUSCODEXPATH, meshControl.getCtlFileContents());
            description = XPathManager.xpathExtractor(STATUSDESCRIPTIONXPATH, meshControl.getCtlFileContents());
        } catch (XPathExpressionException | XPathFactoryConfigurationException e) {
            throw new Exception("Can't parse the Mesh Status control file. Missing: " + e.getMessage());
        }
    }

    /**
     * Method called to return Status Report details from a message in the INbox 
     *
     * @return  the log of the Status Report
     * @throws Exception
     */
    public String logStatusReport() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("Date Time = ").append(dateTime).append("\n");
        sb.append("Event = ").append(event).append("\n");
        sb.append("Status = ").append(status).append("\n");
        sb.append("Status Code = ").append(statusCode).append("\n");
        sb.append("Description = ").append(description);
        return sb.toString();
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getEvent() {
        return event;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }


}
