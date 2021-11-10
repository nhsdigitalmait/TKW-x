/*
 Copyright 2020  Richard Robinson

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
package uk.nhs.digital.mait.tkwx.tk.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import static java.util.logging.Level.SEVERE;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.ISO8601FORMATDATEMASK;
import uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.EVIDENCE_METADATA_ENVRONMENT_PROPERTY;
import static uk.nhs.digital.mait.tkwx.tk.PropertyNameConstants.EVIDENCE_METADATA_URLBASE_PROPERTY;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;

/**
 * Class to generate one metadata file for each set of related interactions. The
 * metatdata file will record important information relating to the exchanges
 * and references to the logged evidence (logs and Validation Reports etc).
 * Recording to metadata file is optionally switched using config and default is
 * not creating metadata
 *
 * @author riro
 */
public class EvidenceMetaDataHandler {

    private static Configurator config;
    private static boolean generateEvidence;
    private static String defaultEvidenceMetadataLocation;
    private ArrayList<String[]> articleList = new ArrayList<>();
    private static String tss_name = "no config name avaiable. This may indicate a fault in the config";
    private static String environment = null;
    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);
    private ArrayList<String> subThreadList = new ArrayList<>();
    private Path metaDataPath = null;
    private String evidence_set_id;
    private String acquiredAtTime;
    private String counterpartyAddress = null;
    private String counterpartyName = null;
    private int sequenceCounter = 0;
    private StringBuilder noteBuilder = new StringBuilder();
    private boolean metaDataComplete = false;
    private String urlBase = null;

    static {
        try {
            config = Configurator.getConfigurator();
            // is the functionality to generate metadata files enabled?
            defaultEvidenceMetadataLocation = config.getConfiguration(PropertyNameConstants.EVIDENCE_METADATA_LOCATION_PROPERTY);
            generateEvidence = isY(
                    config.getConfiguration(
                            PropertyNameConstants.GENERATE_EVIDENCE_METADATA_PROPERTY))
                    && !Utils.isNullOrEmpty(defaultEvidenceMetadataLocation);
            if (generateEvidence) {
                Utils.createFolderIfMissing(defaultEvidenceMetadataLocation);
            }
            tss_name = config.getConfiguration("tks.configname");
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, EvidenceMetaDataHandler.class.getName(), "Failure to retrieve evidence metadata generation property - " + e.toString());
        }

    }

    /**
     * Constructor for EvidenceMetaDatHandler requiring counterparty details
     *
     * @param counterparty Address
     * @param counterparty Name
     */
    public EvidenceMetaDataHandler(String cpAddress, String cpName) {

        if (generateEvidence) {
            counterpartyAddress = cpAddress;
            counterpartyName = cpName;
            evidence_set_id = UUID.randomUUID().toString();
            articleList.clear();
            noteBuilder = new StringBuilder();
            sequenceCounter = 0;
            subThreadList.clear();
            metaDataComplete = false;
            articleList.clear();
            metaDataPath = Paths.get(defaultEvidenceMetadataLocation + File.separator + evidence_set_id + ".metadata");
            ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
            acquiredAtTime = ISO8601FORMATDATE.format(new Date());
            urlBase = System.getProperty("uk.nhs.digital.mait.tkwx.tk.handlers.metadata.urlbase");
            if (Utils.isNullOrEmpty(urlBase)) {
                urlBase = config.getConfiguration(EVIDENCE_METADATA_URLBASE_PROPERTY);
            }
            if (Utils.isNullOrEmpty(urlBase)) {
                urlBase = "urlBase is unset, use tkw.property:" + EVIDENCE_METADATA_URLBASE_PROPERTY + " or system property:uk.nhs.digital.mait.tkwx.tk.handlers.metadata.urlbase";
            }
            environment = System.getProperty("uk.nhs.digital.mait.tkwx.tk.handlers.metadata.environment");
            if (Utils.isNullOrEmpty(environment)) {
                environment = config.getConfiguration(EVIDENCE_METADATA_ENVRONMENT_PROPERTY);
            }
            if (Utils.isNullOrEmpty(environment)) {
                environment = "environment is unset, use tkw.property:" + EVIDENCE_METADATA_ENVRONMENT_PROPERTY + " or system property:uk.nhs.digital.mait.tkwx.tk.handlers.metadata.environment";
            }

            generateMetaDataFile();
        }
    }

    /**
     * Creates a new article entry in the MetaData file
     *
     * @param description
     * @param docurl
     * @param fileLocation
     */
    public synchronized void addMetaData(String type, String description, String docurl, String fileLocation) {
        if (generateEvidence) {
            sequenceCounter++;
            articleList.add(new String[]{Integer.toString(sequenceCounter), type, description, docurl, fileLocation});
            generateMetaDataFile();
        }
    }

    /**
     * Creates a new note entry in the MetaData file. Multiple notes are
     * separated by a ". "
     *
     * @param note
     */
    public synchronized void addNote(String note) {
        if (generateEvidence) {
            if (noteBuilder.length() > 0) {
                noteBuilder.append(". ");
            }
            noteBuilder.append(note);
            generateMetaDataFile();
        }
    }

    private String makeDOMXML() {
        String ret = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("evidence_set");
            doc.appendChild(rootElement);
            // setting attribute to element
            Attr attrId = doc.createAttribute("id");
            attrId.setValue(evidence_set_id);
            rootElement.setAttributeNode(attrId);

            // test_support_system element
            Element testSupportSystem = doc.createElement("test_support_system");
            rootElement.appendChild(testSupportSystem);
            Attr attrName = doc.createAttribute("name");
            attrName.setValue(tss_name);
            testSupportSystem.setAttributeNode(attrName);
            Attr attrEnv = doc.createAttribute("environment");
            attrEnv.setValue(environment);
            testSupportSystem.setAttributeNode(attrEnv);

            // scenario element
            Element scenario = doc.createElement("scenario");
            rootElement.appendChild(scenario);
            Attr attrId2 = doc.createAttribute("id");
            attrId2.setValue("null");
            scenario.setAttributeNode(attrId2);
            scenario.appendChild(doc.createTextNode("No Scenario"));

            // counterparty element
            if (counterpartyAddress != null) {
                Element counterparty = doc.createElement("counterparty");
                rootElement.appendChild(counterparty);
                Attr attrAddr = doc.createAttribute("address");
                attrAddr.setValue(counterpartyAddress);
                counterparty.setAttributeNode(attrAddr);
                if (counterpartyName != null) {
                    Attr attrName1 = doc.createAttribute("name");
                    attrName1.setValue(counterpartyName);
                    counterparty.setAttributeNode(attrName1);
                }
            }

            // MetaData Complete
            Element metaDataCompleteElement = doc.createElement("metadata_complete");
            rootElement.appendChild(metaDataCompleteElement);
            if (metaDataComplete) {
                metaDataCompleteElement.appendChild(doc.createTextNode("true"));
            } else {
                metaDataCompleteElement.appendChild(doc.createTextNode("false"));
            }

            // aquired_at element
            Element acquiredAt = doc.createElement("acquired_at");
            rootElement.appendChild(acquiredAt);
            acquiredAt.appendChild(doc.createTextNode(acquiredAtTime));

            // evidence element
            Element evidence = doc.createElement("evidence");
            rootElement.appendChild(evidence);
            Attr attrUrlBase = doc.createAttribute("urlbase");
            attrUrlBase.setValue(urlBase);
            evidence.setAttributeNode(attrUrlBase);

            if (!articleList.isEmpty()) {
                for (String[] articleAttrs : articleList) {
                    Element article = doc.createElement("article");
                    evidence.appendChild(article);
                    Attr attrSeq = doc.createAttribute("sequence");
                    attrSeq.setValue(articleAttrs[0]);
                    article.setAttributeNode(attrSeq);
                    Attr attrType = doc.createAttribute("type");
                    attrType.setValue(articleAttrs[1]);
                    article.setAttributeNode(attrType);
                    Attr attrDes = doc.createAttribute("description");
                    attrDes.setValue(articleAttrs[2]);
                    article.setAttributeNode(attrDes);
                    Attr attrDocUrl = doc.createAttribute("docurl");
                    if (articleAttrs[3] != null) {
                        attrDocUrl.setValue(articleAttrs[3]);
                    }
                    article.setAttributeNode(attrDocUrl);
                    Attr attrUrl = doc.createAttribute("url");
                    attrUrl.setValue(articleAttrs[4]);
                    article.setAttributeNode(attrUrl);
                }
            }
            // article element

            // notes element
            Element notes = doc.createElement("notes");
            rootElement.appendChild(notes);
            if (noteBuilder.length() > 0) {
                notes.appendChild(doc.createTextNode(noteBuilder.toString()));
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            // Output to console for testing
            transformer.transform(source, result);
            ret = writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * This marks the metatdata file as completed and unless unfinished
     * subthreads are still running will complete the recording of metatdata
     */
    public void mainThreadEnded() {
        if (generateEvidence) {
            System.out.println("mainThreadEnd");
            metaDataComplete = true;
            attemptToWriteToDisk();
        }

    }

    /**
     * This registers that the TKW process has generated a new thread and that
     * this thread may report its logging to EvidenceMetatDataHandler after the
     * main thread has completed. The implication is that the metatdata report
     * needs to be left open until it has completed.
     *
     *
     * @param id should be unique not the Thread name
     */
    synchronized public void subThreadStarted(String id) {
        if (generateEvidence) {
            System.out.println("subThreadStart: " + id);
            subThreadList.add(id);
        }
    }

    /**
     * This registers that the subthread identified by unique Id has now
     * completed and no longer needs to considered when attempting to complete
     * the metadata report
     *
     * @param id
     */
    synchronized public void subThreadEnded(String id) {
        if (generateEvidence) {
            System.out.println("subThreadEnd: " + id);
            int present = subThreadList.indexOf(id);
            if (present != -1) {
                subThreadList.remove(id);
                attemptToWriteToDisk();
            }
        }
    }

    private void attemptToWriteToDisk() {
        System.out.println("write to disk");

        if (metaDataComplete && subThreadList.isEmpty()) {
            System.out.println("MetaData Complete");
            generateMetaDataFile();
        }
    }

    private void generateMetaDataFile() {
        try {
            String domXml = makeDOMXML();
            if (domXml != null) {
                Utils.writeString2File(metaDataPath.toFile(), Utils.xmlReformat(domXml));
            }
        } catch (final IOException ioe) {
            Logger.getInstance().log(SEVERE, EvidenceMetaDataHandler.class.getName(), "Failure to write to path " + metaDataPath.toString() + ioe.toString());
        }
    }
}
