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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
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
public class ResponseImporter {

    private static final int TIMEOUT = 60;
    private static final int ONE_SECOND = 1000;
    private static final String METADATA_SUFFIX = ".metadata";
    
    private static final boolean DEBUG = false;

    /**
     *
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, Exception {
        while (true) { // for debugging purposes
            new ResponseImporter(args);
            if (!DEBUG) {
                break;
            }
        }
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
    public ResponseImporter(String[] args) throws IOException, ParserConfigurationException, SAXException, Exception {
        if (args.length != 2) {
            System.err.println("usage ResponseImporter <target domain> <counterparty>");
            System.exit(-1);
        }
        String targetDomain = args[0];
        String counterParty = args[1];
        // assumes not running in docker
        String evidencePath = System.getenv("TKWROOT") + "/config/" + targetDomain + "/all_evidence";

        if (folderExists(evidencePath)) {
            // wait until a new metadata file appears in the simulator messages folder
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(evidencePath);
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    if (event.context() != null && event.context().toString().endsWith(METADATA_SUFFIX)) {
                        break;
                    }
                }
                key.reset();
                break;
            }

            TreeMap<String, MetaData> metaDataRecords = new TreeMap<>();
            File folder = new File(evidencePath);
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getAbsolutePath().endsWith(METADATA_SUFFIX)) {
                    MetaData metaData = parseMetaData(file, targetDomain);
                    if (metaData != null && counterParty.equals(metaData.getCounterPartyAddress())) {
                        metaDataRecords.put(metaData.getAcquiredAt(), metaData);
                    }
                }
            }

            String requestsFolder = System.getenv("TKWROOT") + "/config/" + targetDomain + "/autotest_config/requests";
            if (!metaDataRecords.keySet().isEmpty()) {
                SynchronousResponseBodyExtractor extractor = new SynchronousResponseBodyExtractor();
                // pick the most recent from the treemap
                String body = extractor.getBody(new FileInputStream(metaDataRecords.get(metaDataRecords.descendingKeySet().first()).getLogfile()), true);
                if (body != null) {
                    HttpHeaderManager headerManager = extractor.getHttpResponseHeaders();
                    String contentType = headerManager.getHttpHeaderValue("Content-type");

                    // autotest requires xml so provide it!
                    if (contentType != null && contentType.toLowerCase().contains("json")) {
                        FHIRJsonXmlAdapter.setFhirVersion(FhirVersionEnum.R4);
                        body = FHIRJsonXmlAdapter.fhirConvertJson2Xml(body);
                    }

                    if (folderExists(requestsFolder)) {
                        System.out.println(String.format("Creating response file %s/extracted_response.xml", requestsFolder));
                        System.out.println(body);
                        // put the extracted response into the autotest requests folder
                        try ( FileWriter fw = new FileWriter(requestsFolder + "/extracted_response.xml")) {
                            fw.write(body);
                        }

                        // now wait until initial autotest has completed 
                        // we dont want two autotest processes running at the same time. The script is not reentrant and breaks if this happens
                        // This piece may not be required if TKW is running in a docker container.
                        waitOnProcessCompletion("run_autotest.sh -s local ValidationRequest_xml_accept");

                        // now run autotest  -s <SenderEndpoint> ExtractedValidationResponse_xml_accept
                        // TODO this wont work in a docker container
                        List<String> params = Arrays.asList(System.getenv("TKWROOT") + "/config/" + targetDomain + "/autotest_config/run_autotest.sh",
                                "-s", "local", "ExtractedValidationResponse_xml_accept");
                        ProcessBuilder pb = new ProcessBuilder(params);
                        Map<String, String> env = pb.environment();
                        env.put("TKWROOT", System.getenv("TKWROOT"));
                        env.put("TKW_BROWSER", "");
                        Process p = pb.start();
                        InputStream is = p.getInputStream();
                        int i = 0;
                        while (p.isAlive() && i < TIMEOUT) {
                            Thread.sleep(ONE_SECOND);
                            System.out.print("Sleeping on autotest sending response " + i + "\r");
                            System.out.flush();
                            i++;
                        }
                        System.out.println();

                        if (i < TIMEOUT) {
                            if (p.exitValue() == 0) {
                                byte[] response = is.readAllBytes();
                                System.out.println("ResponseImporter succeeded " + new String(response));
                            } else {
                                System.err.println(String.format("ResponseImporter failed with error code %d\n", p.exitValue()));
                                // let the caller know that this has failed
                                System.exit(1);
                            }
                        } else {
                            System.err.println("Timeout waiting for autotest sending response to complete");
                            System.exit(1);
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Target folder %s does not exist", requestsFolder));
                    }
                }
            } else {
                System.err.println(String.format("No matching log file found for counterparty %s in folder %s", counterParty, requestsFolder));
                // let the caller know that this has failed
                System.exit(1);
            }
        } else {
            throw new IllegalArgumentException(String.format("ResponseImporter Evidence folder %s does not exist", evidencePath));
        }
    }

    /**
     * block until a ps -ef does not contain the passed String
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
                    break;
                } else {
                    System.err.println("Timeout waiting for " + processName + " to complete");
                    System.exit(1);
                }
            }
        }
    }

    /**
     * parse a TKW metadata file and create a populated MetaData object
     *
     * @param file
     * @return populated MetaData object
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    private MetaData parseMetaData(File file, String targetDomain) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException, Exception {

        int count = 0;
        String metadataComplete = null;
        Element root = null;
        String id = null;
        while (!"true".equals(metadataComplete) && count++ < TIMEOUT) {
            DocumentBuilderFactory factory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new FileInputStream(file));
            root = doc.getDocumentElement();
            id = root.getAttribute(METADATA_ID);

            NodeList testSupportSystem = root.getElementsByTagName(METADATA_TEST_SUPPORT_SYSTEM);
            String testSupportSystemName = testSupportSystem != null && testSupportSystem.getLength() > 0 ? testSupportSystem.item(0).getAttributes().getNamedItem(METADATA_NAME).getTextContent() : "";
            if (!targetDomain.equals(testSupportSystemName)) {
                System.err.println(String.format("Test Support System name %s does not match target domnain %s", testSupportSystemName, targetDomain));
                return null;
            }

            NodeList metaDatacompleteNodes = root.getElementsByTagName(MEATADATA_METADATA_COMPLETE);
            metadataComplete = metaDatacompleteNodes != null && metaDatacompleteNodes.getLength() > 0 ? metaDatacompleteNodes.item(0).getTextContent() : "";
            if (!"true".equals(metadataComplete)) {
                System.out.print("Sleeping waiting on metadata complete " + count++ + "\r");
                System.out.flush();
                Thread.sleep(ONE_SECOND);
            } else {
//              System.out.println();
                break;
            }
        }
        if (count >= TIMEOUT) {
            System.err.println("metadata_complete is not true after " + TIMEOUT + "s timeout");
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
}
