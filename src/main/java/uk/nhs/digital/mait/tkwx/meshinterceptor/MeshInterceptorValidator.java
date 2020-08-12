/*
  Copyright 2019  Richard Robinson rrobinson@nhs.net

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
package uk.nhs.digital.mait.tkwx.meshinterceptor;

import java.io.File;
import java.util.UUID;
import uk.nhs.digital.mait.tkwx.mesh.MeshControlFile;
import uk.nhs.digital.mait.tkwx.mesh.MeshData;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceManager;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.boot.ToolkitService;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.Utils;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.tkwx.tk.handlers.EvidenceMetaDataHandler;
import uk.nhs.digital.mait.tkwx.tk.internalservices.Reconfigurable;
import uk.nhs.digital.mait.tkwx.tk.internalservices.ValidatorService;
import uk.nhs.digital.mait.tkwx.tk.internalservices.LoggingFileOutputStream;

/**
 *
 * @author riro
 */
public class MeshInterceptorValidator extends Thread {

    private static final Object lock = new Object();
    private MeshRequest meshRequest;
    private String eventCode;
    private final Configurator config;
    private final MeshInterceptHandler handler;
    private final String logDirectoryMailboxId;
    private EvidenceMetaDataHandler evidenceMetaDataHandler = null;
    private String metaDataThreadId = null;

    public MeshInterceptorValidator(Configurator config, MeshInterceptHandler handler, String logDirectoryMailboxId) {
        this.config = config;
        this.handler = handler;
        this.logDirectoryMailboxId = logDirectoryMailboxId;
    }

    /**
     * Thread safe class to execute validations in a separate thread. Determines
     * type of validation required Performs any conversions required before
     * validation can be done
     *
     * @param eventCode
     * @param req MeshRequest
     */
    public void validateRequest(String eventCode, MeshRequest req) {
        meshRequest = req;
        this.eventCode = eventCode;
        LoggingFileOutputStream lfos = meshRequest.getLoggingStream();
        if (lfos != null) {
            this.evidenceMetaDataHandler = lfos.getEvidenceMetaDataHandler();
        }
        if (evidenceMetaDataHandler != null) {
            // register that a subthread has started which will require the EvidenceMetaDataHandler to remain open 
            metaDataThreadId = UUID.randomUUID().toString();
            evidenceMetaDataHandler.subThreadStarted(metaDataThreadId);
        }
        start();
    }

    @Override
    public void run() {
        this.setName("MeshInterceptorValidatorThread");

        try {
            // default to Validator
            String validatorServiceName = "Validator";
            MeshControlFile meshControlFile = meshRequest.getRequestMeshMessage().getControlFile();
            MeshData meshData = meshRequest.getRequestMeshMessage().getDataFile();
            if (eventCode == null) {
                String s = "there is not sufficient data regarding the type of message eventCode is missing or cannot be understood";
                meshRequest.getRequestMeshMessage().log(s, 2, true);
                throw new Exception(s);
            }
            //Schema validator does not need to be set as schema validation is don in Hapi FHIR Validation
            ToolkitService validatorService = ServiceManager.getInstance().getService(validatorServiceName);
            if (validatorService != null) {
                ValidatorService vs = (ValidatorService) validatorService;
                vs.setEvidenceMetaData(evidenceMetaDataHandler);
                System.out.println("Validating message: Workflow Id:" + meshControlFile.getWorkflowId() + " Event Code: " + eventCode + " from MESH mailbox: " + meshControlFile.getFromDTS());
                // No need to reconfigure validator - this would  be problematic as the validator loads in may asset files at initialisation which takes 10s of seconds
                String extractedRequest = new String(meshData.getContent());

                Reconfigurable reconfigurable = (Reconfigurable) validatorService;
                String baseFolder = config.getConfiguration(VALIDATOR_REPORT_PROPERTY);
                String validationReportRoot = null;
                String reportFileName = null;
                synchronized (lock) {
                    if (!Utils.isNullOrEmpty(logDirectoryMailboxId)) {
                        // we can use Reconfigurable to set the dest folder for this service
                        // write the request to the required log file name
                        File destFolder = new File(handler.getSavedMessagesDirectory()
                                + File.separator
                                + logDirectoryMailboxId
                                + File.separator
                                + "validation_reports"
                        );
                        if (!destFolder.exists()) {
                            destFolder.mkdirs();
                        }
                        reconfigurable.reconfigure(VALIDATOR_REPORT_PROPERTY, destFolder.getCanonicalPath());
                        validationReportRoot = destFolder.getCanonicalPath();
                    } else {
                        validationReportRoot = baseFolder;
                    }

                    ServiceResponse sr = validatorService.execute(new String[]{extractedRequest, eventCode});

                    reportFileName = sr.getResponse();
                    if (Utils.isNullOrEmpty(reportFileName)) {
                        reportFileName = "fixme.log";
                    }
                    Utils.writeString2File(validationReportRoot + "/"
                            + reportFileName.replaceFirst("^validation_report_(.*).html", "$1.log"), extractedRequest);
                    // may not be needed but better safe etc
                    reconfigurable.reconfigure(VALIDATOR_REPORT_PROPERTY, baseFolder);
                }

            } else {
                System.err.println("No Validation Service set");
            }
        } catch (Exception e) {
            Logger.getInstance().log("Validate request failed " + e.getMessage());
        }

        // indicate to the EvidecnMetatDataHandler that this subthread has ended and the evidecnce can be attempted to be written
        evidenceMetaDataHandler.subThreadEnded(metaDataThreadId);
    }

}
