/*
 Copyright 2021  Simon Farrow <simon.farrow1@nhs.net>

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

import ca.uhn.fhir.context.FhirVersionEnum;
import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.xpathExtractor;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.folderExists;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;

/**
 * Inspects a simulator all_evidence folder and extracts a response from the
 * latest log file matching that counterparty If the response was fhir json its
 * converted to fhr xml. The extracted response is placed into the autotest
 * requests folder.
 *
 * @author simonfarrow
 */
public class BARSResponseImporter {

    private final BlockingQueue<MetaData> blockingQueue;

    private static final int TIMEOUT = 60;
    private static final int ONE_SECOND = 1000;
    private static final String METADATA_SUFFIX = ".metadata";

    private final static boolean DEBUG = false;

    /**
     *
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, Exception {
        FHIRJsonXmlAdapter.setFhirVersion(FhirVersionEnum.R4);
        new BARSResponseImporter(args);
    }

    /**
     * public constructor
     *
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws Exception
     */
    public BARSResponseImporter(String[] args) throws IOException, ParserConfigurationException, SAXException, Exception {
        String targetDomain = null;
        String counterParty = null;

        switch (args.length) {
            case 2:
                counterParty = args[1];
            // drop through
            case 1:
                targetDomain = args[0];
                break;
            default:
                System.err.println("usage ResponseImporter <target domain> [<counterparty>]");
                System.exit(-1);
        }

        // assumes defaults
        String evidencePath = System.getenv("TKWROOT") + "/config/" + targetDomain + "/all_evidence";
        String logFolder = System.getenv("TKWROOT") + "/config/" + targetDomain + "/logs";
        Utils.createFolderIfMissing(logFolder);
        Logger.getInstance().setAppName("TKSResponseImporter", logFolder);

        Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Initialised");
        if (DEBUG) {
            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Target domain " + targetDomain);
            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Counterparty " + counterParty);
            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Evidence path " + evidencePath);
        }

        if (!folderExists(evidencePath)) {
            Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), String.format("ResponseImporter Evidence folder %s does not exist", evidencePath));
            throw new IllegalArgumentException(String.format("ResponseImporter Evidence folder %s does not exist", evidencePath));
        }

        // setup an unlimited synchronised queue
        blockingQueue = new LinkedBlockingDeque<>();

        // start the consumer listening on MetaData objects
        new Consumer(blockingQueue, targetDomain);

        // then start the producer blocking on file events in all_evidence
        new Producer(blockingQueue, evidencePath, targetDomain, counterParty);
    }

    private static final String METADATA_ID = "id";
    private static final String METADATA_NAME = "name";
    private static final String METADATA_EVIDENCE = "evidence";
    private static final String METADATA_ADDRESS = "address";
    private static final String METADATA_ACQUIRED_AT = "acquired_at";
    private static final String METADATA_URL = "url";
    private static final String METADATA_INTERACTIONLOG = "interaction-log";
    private static final String METADATA_COUNTERPARTY = "counterparty";
    private static final String METADATA_TYPE = "type";
    private static final String METADATA_ARTICLE = "article";
    private static final String MEATADATA_METADATA_COMPLETE = "metadata_complete";
    private static final String METADATA_TEST_SUPPORT_SYSTEM = "test_support_system";

    /**
     * encapsulates an all_evidence xml metadata file This could be a record at
     * java 14
     */
    private static class MetaData {

        private final String id;
        private final String logfile;
        private final String counterPartyAddress;
        private final String acquiredAt;

        /**
         *
         * @param id
         * @param logfile
         * @param counterParty
         * @param acquiredAt
         */
        public MetaData(String id, String logfile, String counterParty, String acquiredAt) {
            this.id = id;
            this.logfile = logfile;
            this.counterPartyAddress = counterParty;
            this.acquiredAt = acquiredAt;
        }

        /**
         * @return the url
         */
        public String getLogfile() {
            return logfile;
        }

        /**
         * @return the counterPartyAddress
         */
        public String getCounterPartyAddress() {
            return counterPartyAddress;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the acquiredAt
         */
        public String getAcquiredAt() {
            return acquiredAt;
        }
    }

    private static class Producer implements Runnable {

        private final BlockingQueue<MetaData> dataQueue;
        private String lastProcessed;

        private final String evidencePath;
        private final String targetDomain;
        private final String counterParty;

        /**
         *
         * @param dataQueue
         * @param evidencePath
         * @param targetDomain
         * @param counterParty
         */
        public Producer(BlockingQueue<MetaData> dataQueue, String evidencePath, String targetDomain, String counterParty) {
            this.dataQueue = dataQueue;
            this.evidencePath = evidencePath;
            this.targetDomain = targetDomain;
            this.counterParty = counterParty;
            Thread producerThread = new Thread(this::produce);
            producerThread.start();
        }

        @Override
        public void run() {
            produce();
        }

        private void produce() {
            try {
                // wait until a new metadata file appears in the all_evidence folder
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Paths.get(evidencePath);
                path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
                
                while (true) {
                    try {
                        Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Producer Waiting on watchEvidenceFolder");
                        MetaData metaData = watchEvidenceFolder(watchService);
                        if (metaData != null) {
                            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Producer adding MetaData " + metaData.getId() + " to queue");
                            dataQueue.put(metaData);
                        }
                    } catch (IOException ex) {
                        Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Producer IO Exception " + ex.getMessage());
                    } catch (InterruptedException e) {
                        Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Producer Interrupted " + e.getMessage());
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Producer IO Exception " + ex.getMessage());
            }
        }

        /**
         *
         * @param watchService
         * @return MetaData object
         * @throws IOException
         * @throws InterruptedException
         */
        private MetaData watchEvidenceFolder(WatchService watchService) throws IOException, InterruptedException {
            WatchKey key;
            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Producer Waiting on watch service");
            while ((key = watchService.take()) != null) {
                Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Producer Got watch event");
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    if (event.context() != null && event.context().toString().endsWith(METADATA_SUFFIX)) {
                        if (!event.context().toString().equals(lastProcessed)) {
                            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Producer Found metadata file " + event.context().toString());

                            File metaDatafile = new File(evidencePath + "/" + event.context().toString());

                            MetaData metaData = null;
                            try {
                                metaData = parseMetaData(metaDatafile, targetDomain);
                            } catch (FileNotFoundException ex) {
                                Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Producer file not found exception " + ex.getMessage());
                            } catch (SAXException ex) {
                                Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Producer XML Parsing exception " + ex.getMessage());
                            } catch (Exception ex) {
                                Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Producer Geberal exception " + ex.getMessage());
                            }

                            if (metaData != null && (counterParty == null || counterParty.equals(metaData.getCounterPartyAddress()))) {
                                lastProcessed = event.context().toString();
                                key.reset();
                                return metaData;
                            }
                        }
                    } // if event
                } // for events

                // should never get here
                key.reset();
                break;
            }
            // should never get here
            return null;
        } // watch evidence folder

        /**
         * parse a TKW metadata file and create a populated MetaData object
         *
         * @param meta data file
         * @return populated MetaData object
         * @throws ParserConfigurationException
         * @throws FileNotFoundException
         * @throws SAXException
         * @throws IOException
         * @throws Exception
         */
        private MetaData parseMetaData(File metaDataFile, String targetDomain)
                throws ParserConfigurationException, FileNotFoundException, SAXException, IOException, Exception {

            int count = 0;
            String metadataComplete = null;
            Element root = null;
            String id = null;
            while (!"true".equals(metadataComplete) && count++ < TIMEOUT) {
                DocumentBuilderFactory factory
                        = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = null;
                try (FileInputStream fis = new FileInputStream(metaDataFile)) {
                    doc = builder.parse(fis);
                } catch (SAXException ex) {
                    Logger.getInstance().log(WARNING, BARSResponseImporter.class.getName(),
                            String.format("Error parsing metadata file %s\n",
                                    new String(Files.readAllBytes(Paths.get(metaDataFile.getAbsolutePath())))));
                    continue;
                }
                root = doc.getDocumentElement();
                id = root.getAttribute(METADATA_ID);

                NodeList testSupportSystem = root.getElementsByTagName(METADATA_TEST_SUPPORT_SYSTEM);
                String testSupportSystemName = testSupportSystem != null && testSupportSystem.getLength() > 0 ? testSupportSystem.item(0).getAttributes().getNamedItem(METADATA_NAME).getTextContent() : "";
                if (!targetDomain.equals(testSupportSystemName)) {
                    Logger.getInstance().log(WARNING, BARSResponseImporter.class.getName(), String.format("Test Support System name %s does not match target domnain %s", testSupportSystemName, targetDomain));
                    return null;
                }

                NodeList metaDatacompleteNodes = root.getElementsByTagName(MEATADATA_METADATA_COMPLETE);
                metadataComplete = metaDatacompleteNodes != null && metaDatacompleteNodes.getLength() > 0 ? metaDatacompleteNodes.item(0).getTextContent() : "";
                if (!"true".equals(metadataComplete)) {
                    System.out.print("Producer Sleeping waiting on metadata complete " + count++ + "\r");
                    System.out.flush();
                    Thread.sleep(ONE_SECOND);
                } else {
                    //              System.out.println();
                    break;
                }
            }
            if (count >= TIMEOUT) {
                Logger.getInstance().log(WARNING, BARSResponseImporter.class.getName(), "Producer metadata_complete is not true after " + TIMEOUT + "s timeout");
                return null;
            }

            NodeList counterParty = root.getElementsByTagName(METADATA_COUNTERPARTY);
            String counterPartyAddress = counterParty != null && counterParty.getLength() > 0 ? counterParty.item(0).getAttributes().getNamedItem(METADATA_ADDRESS).getTextContent() : "";

            String acquiredAt = root.getElementsByTagName(METADATA_ACQUIRED_AT) != null ? root.getElementsByTagName(METADATA_ACQUIRED_AT).item(0).getTextContent() : "";

            NodeList evidenceSet = root.getElementsByTagName(METADATA_EVIDENCE);
            Node evidence = evidenceSet.item(0);
            NodeList evidenceChildren = evidence.getChildNodes();
            MetaData metaData = null;
            for (int i = 0; i < evidenceChildren.getLength(); i++) {
                if (evidenceChildren.item(i).getNodeName().equals(METADATA_ARTICLE)) {
                    NamedNodeMap articleMap = evidenceChildren.item(i).getAttributes();
                    if (articleMap != null && articleMap.getNamedItem(METADATA_TYPE) != null && articleMap.getNamedItem(METADATA_TYPE).getTextContent().equals(METADATA_INTERACTIONLOG)) {
                        String logfile = articleMap.getNamedItem(METADATA_URL).getTextContent();
                        if (logfile != null) {
                            metaData = new MetaData(id, logfile, counterPartyAddress, acquiredAt);
                            break;
                        }
                    }
                }
            } // for
            return metaData;
        }
    }

    private static class Consumer implements Runnable {

        private final BlockingQueue<MetaData> dataQueue;
        private final String targetDomain;

        /**
         * public constructor
         *
         * @param dataQueue
         * @param targetDomain
         */
        public Consumer(BlockingQueue<MetaData> dataQueue, String targetDomain) {
            this.dataQueue = dataQueue;
            this.targetDomain = targetDomain;
            Thread consumerThread = new Thread(this::consume);
            consumerThread.start();
        }

        @Override
        public void run() {
            consume();
        }

        private void consume() {
            while (true) {
                MetaData metaData = null;
                try {
                    Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Waiting on MetaData queue");
                    metaData = dataQueue.take();
                    Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Consuming " + metaData.getId());
                } catch (InterruptedException e) {
                    Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Loop interrupted");
                    break;
                }

                // Consume value
                String requestsFolder = System.getenv("TKWROOT") + "/config/" + targetDomain + "/autotest_config/requests";
                SynchronousResponseBodyExtractor extractor = new SynchronousResponseBodyExtractor();
                // pick the most recent from the treemap
                String responseBody = null;
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(metaData.getLogfile());
                    responseBody = extractor.getBody(fis, true);
                } catch (Exception ex) {
                    Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Consumer Exception reading body " + ex.getMessage());
                } finally {
                    try {
                        if ( fis != null ) {
                            fis.close();
                        }
                    } catch (IOException ex) {
                    }
                }
                if (responseBody != null && responseBody.trim().length() > 0) {

                    // get the target identifier
                    HttpHeaderManager requestHeaderManager = extractor.getHttpRequestHeaders();
                    String b64 = requestHeaderManager.getHttpHeaderValue("NHSD-Target-Identifier");
                    String json = new String(Base64.getDecoder().decode(b64));
                    String destEndpointId = JsonPath.read(json, "$.value");

                    HttpHeaderManager responseHeaderManager = extractor.getHttpResponseHeaders();
                    String contentType = responseHeaderManager.getHttpHeaderValue("Content-type");

                    // this shouldn't happen because its been alread converted by this point!
                    if (contentType != null && contentType.toLowerCase().contains("json")) {
                        responseBody = FHIRJsonXmlAdapter.fhirConvertJson2Xml(responseBody);
                    }

                    String sourceEndpointId = null;
                    try {
                        sourceEndpointId = xpathExtractor("replace(fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:source/fhir:endpoint/@value,'^.*:','')", responseBody);
                    } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
                        Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Consumer Parse exception " + ex.getMessage());
                    }

                    if (folderExists(requestsFolder)) {
                        if (requestHeaderManager.getFirstLine().startsWith("POST")) {
                            String responseBodyEventCode = null;
                            try {
                                responseBodyEventCode
                                        = xpathExtractor("/fhir:Bundle/fhir:entry/fhir:resource/fhir:MessageHeader/fhir:eventCoding[fhir:system/@value='https://fhir.nhs.uk/CodeSystem/message-events-bars']/fhir:code/@value", responseBody);
                            } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
                                Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Consumer Error extracting response body event code " + ex.getMessage());
                            }
                            if (responseBodyEventCode.equals("servicerequest-request")) {
                                Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), String.format("Consumer Creating response file %s/extracted_response.xml", requestsFolder));
                                //Logger.getInstance().log(INFO,BARSResponseImporter.class.getName(),body);
                                // put the extracted response into the autotest requests folder
                                try (FileWriter fw = new FileWriter(requestsFolder + "/extracted_response.xml")) {
                                    fw.write(responseBody);
                                } catch (IOException ex) {
                                    Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Consumer error writing extracted_response.xml " + ex.getMessage());
                                }

                                // now wait until initial autotest has completed
                                // we dont want two autotest processes running at the same time. The script is not reentrant and breaks if this happens
                                // This piece may not be required if TKW is running in a docker container.
                                Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Waiting on autotest");
                                try {
                                    // TODO in a real scenario autotest would not be running anyway.
                                    waitOnProcessCompletion("run_autotest.sh -s " + destEndpointId + " ValidationRequest_xml_accept");
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), "Consumer Error waiting on autotest " + ex.getMessage());
                                }
                                Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Finished waiting on autotest");

                                // now run autotest  -s <SenderEndpoint> ExtractedValidationResponse_xml_accept
                                List<String> params = Arrays.asList(System.getenv("TKWROOT") + "/config/" + targetDomain
                                        + "/autotest_config/run_autotest.sh",
                                        "-s", sourceEndpointId, "ExtractedValidationResponse_xml_accept");
                                ProcessBuilder pb = new ProcessBuilder(params);
                                Map<String, String> env = pb.environment();
                                env.put("TKWROOT", System.getenv("TKWROOT"));
                                env.put("TKW_BROWSER", "");
                                Process p = null;
                                try {
                                    p = pb.start();
                                } catch (IOException ex) {
                                }
                                InputStream is = p.getInputStream();
                                int i = 0;
                                while (p.isAlive() && i < TIMEOUT) {
                                    try {
                                        Thread.sleep(ONE_SECOND);
                                    } catch (InterruptedException ex) {
                                    }
                                    if (DEBUG) {
                                        Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Sleeping on autotest sending response " + i);
                                    }
                                    System.out.print("Consumer sleeping on autotest sending response " + i + "\r");
                                    System.out.flush();
                                    i++;
                                }
                                System.out.println();

                                if (i < TIMEOUT) {
                                    if (p.exitValue() == 0) {
                                        byte[] response = null;
                                        try {
                                            response = is.readAllBytes();
                                        } catch (IOException ex) {
                                        }
                                        Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer ResponseImporter succeeded " + new String(response));
                                    } else {
                                        Logger.getInstance().log(WARNING, BARSResponseImporter.class.getName(), String.format("Consumer ResponseImporter failed with error code %d\n", p.exitValue()));
                                        // let the caller know that this has failed
                                        // System.exit(1);
                                    }
                                } else {
                                    Logger.getInstance().log(WARNING, BARSResponseImporter.class.getName(), "Consumer Timeout waiting for autotest sending response to complete");
                                    // System.exit(1);
                                }
                            } // if response event code servicerequest-request
                        }  // Request is a POST
                    } else {
                        Logger.getInstance().log(SEVERE, BARSResponseImporter.class.getName(), String.format("Target folder %s does not exist", requestsFolder));
                        throw new IllegalArgumentException(String.format("Target folder %s does not exist", requestsFolder));
                    }
                }
            } // while           
        } // consume

        /**
         * block until a ps -ef does not contain the passed String
         *
         * @param processName
         * @throws IOException
         * @throws InterruptedException
         */
        private void waitOnProcessCompletion(String processName) throws IOException, InterruptedException {
            int i = 0;
            while (true) {
                ProcessBuilder pb = new ProcessBuilder(Arrays.asList("ps", "-ef"));
                Map<String, String> env = pb.environment();
                env.put("TKWROOT", System.getenv("TKWROOT"));
                env.put("TKW_BROWSER", "");
                Process p = pb.start();
                p.waitFor();
                InputStream is1 = p.getInputStream();
                String pstate = new String(is1.readAllBytes());
                if (pstate.contains(processName) && i < TIMEOUT) {
                    System.out.print("Sleeping on " + processName + " " + i++ + "\r");
                    System.out.flush();
                    Thread.sleep(ONE_SECOND);
                } else {
                    if (i < TIMEOUT) {
                        System.out.println();
                        if (DEBUG) {
                            Logger.getInstance().log(INFO, BARSResponseImporter.class.getName(), "Consumer Breaking out before timeout");
                        }
                        break;
                    } else {
                        Logger.getInstance().log(WARNING, BARSResponseImporter.class.getName(), "Consumer Timeout waiting for " + processName + " to complete");
                        break;
                        //System.exit(1);
                    }
                }
            } // while true
        } // waitOnProcessCompletion
    } // Consumer class
}
