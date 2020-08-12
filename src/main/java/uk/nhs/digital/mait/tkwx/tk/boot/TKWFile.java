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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import static java.util.logging.Level.SEVERE;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.w3c.dom.Document;
import uk.nhs.digital.mait.tkwx.http.HttpHeaderManager;
import uk.nhs.digital.mait.tkwx.jsonconverter.JsonXmlConverter;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.FHIR_MESSAGING_JSON;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.FHIR_MESSAGING_XML;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.FHIR_REST_JSON;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.FHIR_REST_XML;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.ITK_DISTRIBUTION_ENVELOPE_XML;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.ITK_SOAP_ENVELOPE_XML;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.SPINE_SOAP_SYCHRONOUS_XML;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.TKW_SIMULATOR_LOG;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.TKW_TRANSMITTER_LOG;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.UNKNOWN;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.UNKNOWN_JSON;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.UNKNOWN_SOAP_ENVELOPE_XML;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.UNKNOWN_XML;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import uk.nhs.digital.mait.tkwx.tk.internalservices.send.LogMarkers;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.spine.SpineMessage;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.RequestBodyExtractor;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.SynchronousResponseBodyExtractor;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.tkwx.util.bodyextractors.AbstractBodyExtractor;
import static uk.nhs.digital.mait.tkwx.tk.boot.TKWFileType.SPINE_SOAP_ASYCHRONOUS_REQUEST_XML;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter.FHIRCONVERSIONFAILURE;

/**
 *
 * @author simonfarrow
 */
public class TKWFile {

    private String requestService;
    private String responseService;
    private String requestBody;
    private String responseBody;
    private ArrayList<TKWFileExtractResult> results;

    // Constants
    public static final String FHIR_NAMESPACE = "http://hl7.org/fhir";
    private static final String ITK_DISTRIBUTION_ENVELOPE_ITK_HEADER_SERVICE = "/itk:DistributionEnvelope/itk:header/@service";

    private String sourceIP;
    private String destIP;
    private Date timestamp;

    /**
     * @return the results
     */
    public ArrayList<TKWFileExtractResult> getResults() {
        return results;
    }

    /**
     * required for the xml parsing part
     */
    private static class SimpleErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
        }
    }

    /**
     * main entry point
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            new TKWFile(args);
        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, TKWFile.class.getName(), "Error instantiating TKWFile " + ex.getMessage());
        }
    }

    /**
     * public constructor
     *
     * @param args string array containing list of paths
     * @throws java.io.IOException
     */
    public TKWFile(String[] args) throws IOException {
        requestService = null;
        responseService = null;
        requestBody = null;
        responseBody = null;

        results = new ArrayList<>();
        for (String path : args) {
            walkFiles(path, results);
        }
        for (TKWFileExtractResult er : results) {
            System.out.println(er.toString());
        }
    }

    /**
     * recursively process files and folders ,also handles zip files
     *
     * @param path
     * @throws IOException
     */
    private void walkFiles(String path, ArrayList<TKWFileExtractResult> al) throws IOException {
        File file = new File(path);
        try {
            if (file.exists()) {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        walkFiles(f.getAbsolutePath(), al);
                    }
                } else if (path.toLowerCase().endsWith(".zip")) {
                    handleZipFile(path, al);
                } else if (path.toLowerCase().endsWith("tar.bz2")) {
                    handleTarBz2File(path, al);
                } else if (path.toLowerCase().endsWith("tar.gz")) {
                    handleTarGzFile(path, al);
                } else {
                    TKWFileType ft = fileType(file);
                    al.add(new TKWFileExtractResult(path, ft, sourceIP, destIP, timestamp, requestService, requestBody));
                }
            } else {
                Logger.getInstance().log(SEVERE, TKWFile.class.getName(), "File " + path + " does not exist");
            }
        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, TKWFile.class.getName(), "Error walking files" + ex.getMessage());
        }
    }

    /**
     * unzip a collection of files
     *
     * @param path
     * @param al
     * @throws IOException
     */
    private void handleZipFile(String path, ArrayList<TKWFileExtractResult> al) throws IOException {
        ZipFile zipFile = new ZipFile(path);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String content = extractEntry(zipFile.getInputStream(entry));
            TKWFileType ft = fileType(content);
            al.add(new TKWFileExtractResult(entry.getName(), ft, sourceIP, destIP, timestamp, requestService, requestBody));
        }
    }

    /**
     * uncompress and process a bzipped tar
     *
     * @param path
     * @param al
     * @throws IOException
     */
    private void handleTarBz2File(String path, ArrayList<TKWFileExtractResult> al) throws IOException {
        FileInputStream in = new FileInputStream(path);
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(bzIn)) {
            handleTar(tarIn, al);
        }
    }

    /**
     * uncompress and process a gzipped tar
     *
     * @param path
     * @param al
     * @throws IOException
     */
    private void handleTarGzFile(String path, ArrayList<TKWFileExtractResult> al) throws IOException {
        FileInputStream in = new FileInputStream(path);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn)) {
            handleTar(tarIn, al);
        }
    }

    /**
     * process an uncompressed tar stream
     *
     * @param tarIn
     * @param al
     * @throws IOException
     */
    private void handleTar(final TarArchiveInputStream tarIn, ArrayList<TKWFileExtractResult> al) throws IOException {
        ArchiveEntry entry = null;
        while (null != (entry = tarIn.getNextEntry())) {
            if (entry.getSize() < 1) {
                continue;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[10240];
            int n = 0;
            while (-1 != (n = tarIn.read(buffer))) {
                bos.write(buffer, 0, n);
            }
            TKWFileType ft = fileType(bos.toString());
            al.add(new TKWFileExtractResult(entry.getName(), ft, sourceIP, destIP, timestamp, requestService, requestBody));
        }
    }

    /**
     * @return the service
     */
    public String getService() {
        return requestService;
    }

    /**
     * get the contents of a stream and return the resulting byte array as a
     * string
     *
     * @param inputStream
     * @return uncompressed file contents
     */
    private String extractEntry(InputStream inputStream) {
        int length;
        byte[] buf = new byte[10240];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((length = inputStream.read(buf, 0, buf.length)) >= 0) {
                bos.write(buf, 0, length);
            }
            bos.close();
            return bos.toString();
        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, TKWFile.class.getName(), "Error extracting entry " + ex.getMessage());
        }
        return null;
    }

    /**
     * overload with File object argument
     *
     * @param file
     * @return TKWFileType enum
     * @throws IOException
     */
    public TKWFileType fileType(File file) throws IOException {
        return fileType(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
    }

    /**
     * determines the derived service, and other items
     *
     * @param fileContents
     * @return TKW file type may or may not include setting a service depending
     * on the type
     * @throws java.io.IOException
     */
    public TKWFileType fileType(String fileContents) throws IOException {
        // try to strip a Damian new proxy style header from the start
        if (fileContents.matches("(?s)^[^ ]+ +From +[^ ]+ +To +[^ ]+ +.*$")) {
            Pattern p = Pattern.compile("(?s)^[^ ]+ +From +([^ ]+) +To +([^ ]+) +\r?\n +([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\+[0-9]{2}:[0-9]{2})?)(.*)$");
            Matcher m = p.matcher(fileContents);
            if (m.matches()) {
                // indexes are by order of opening left bracket so 5 will be 5 even if 4 is null (non DST)
                sourceIP = m.group(1);
                destIP = m.group(2);
                try {
                    SimpleDateFormat df = new SimpleDateFormat(ISOFORMATDATEMASK_NOZ);
                    df.setTimeZone(TimeZone.getTimeZone("GMT"));
                    timestamp = df.parse(m.group(3));
                } catch (ParseException ex) {
                    Logger.getInstance().log(SEVERE, TKWFile.class.getName(), "Failed to parse timestamp " + ex.getMessage());
                }
                fileContents = m.group(5);
            }
        }

        requestService = null;
        AbstractBodyExtractor be = null;
        if (fileContents.contains(LogMarkers.END_INBOUND_MARKER)) {
            be = new RequestBodyExtractor();
            try {
                requestBody = be.getBody(new ByteArrayInputStream(fileContents.getBytes()));
                processBody(be, requestBody);
            } catch (Exception ex) {
            }
            return TKW_SIMULATOR_LOG;
        } else if (fileContents.contains(LogMarkers.END_REQUEST_MARKER)) {
            be = new SynchronousResponseBodyExtractor();
            try {
                responseBody = be.getBody(new ByteArrayInputStream(fileContents.getBytes()));
                processBody(be, responseBody);
            } catch (Exception ex) {
            }
            return TKW_TRANSMITTER_LOG;
        } else if (isXml(fileContents)) {
            return processXML(fileContents);
        } else if (isJson(fileContents)) {
            return processJson(fileContents);
        } else if (fileContents.contains("MIME-Boundary")) {
            try {
                // NB there's no guaranetee this will throw an exception if it isn't a spine message
                // log containing mime message
                SpineMessage sm = new SpineMessage(fileContents);
                // TODO may not be request

                // TODO This value has already been parsed in httpheaders SOAPAction
                requestService = fileContents.replaceFirst("(?i)(?s)^.*?SOAPAction *:", "");
                requestService = requestService.replaceFirst("(?s)\r?\n.*$", "").trim();

                // this is the unqualified SPINE service name eg Just PORX..... not a urn
                //requestService = sm.getService();
                requestBody = sm.getHL7Part();
                if (requestBody != null) {
                    return SPINE_SOAP_ASYCHRONOUS_REQUEST_XML;
                }
            } catch (Exception ex) {
            }
        }
        return UNKNOWN;
    }

    private void processBody(AbstractBodyExtractor be, String body) {
        HttpHeaderManager headers = be.getHttpRequestHeaders();
        String interactionID = headers.getHttpHeaderValue(SSP_INTERACTION_ID_HEADER);
        if (interactionID != null && !interactionID.trim().isEmpty()) {
            requestService = interactionID;
        } else if (isXml(body)) {
            processXML(body);
        } else if (isJson(body)) {
            processJson(body);
        }
    }

    /**
     *
     * @param content
     * @return
     */
    private TKWFileType processJson(String content) {
        if (content.contains(FHIR_NAMESPACE)) {
            // fhir?
            // convert to xml
            String xml = FHIRJsonXmlAdapter.fhirConvertJson2Xml(content);
            if (!xml.contains(FHIRCONVERSIONFAILURE)) {
                processXML(xml);
                if (isFHirMessaging(xml)) {
                    return FHIR_MESSAGING_JSON;
                } else {
                    return FHIR_REST_JSON;
                }
            }
        }
        return UNKNOWN_JSON;
    }

    /**
     * determine the file type for an xml payload
     *
     * @param content
     * @return TKWFileType
     */
    private TKWFileType processXML(String content) {
        try {
            // non spine soap action ?
            String action = XPathManager.xpathExtractor(SOAP_HEADER_WSA_ACTION, content);
            if (!action.trim().isEmpty()) {
                requestService = action;
                if (action.startsWith("urn:nhs-itk")) {
                    // soap ns "http://schemas.xmlsoap.org/soap/envelope/"
                    requestBody = content;
                    return ITK_SOAP_ENVELOPE_XML;
                } else {
                    return UNKNOWN_SOAP_ENVELOPE_XML;
                }
            }
        } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
        }

        try {
            // spine soap action ?
            String action = XPathManager.xpathExtractor(SOAP_HEADER_WSASPINE_ACTION, content);
            if (!action.trim().isEmpty()) {
                requestService = action;
                if (action.startsWith("urn:nhs:names:services")) {
                    requestBody = content;
                    return SPINE_SOAP_SYCHRONOUS_XML;
                } else {
                    return UNKNOWN_SOAP_ENVELOPE_XML;
                }
            }
        } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
        }

        try {
            // de ?
            String action = XPathManager.xpathExtractor(ITK_DISTRIBUTION_ENVELOPE_ITK_HEADER_SERVICE, content);
            if (!action.trim().isEmpty()) {
                if (action.startsWith("urn:nhs-itk")) {
                    requestService = action;
                    requestBody = content;
                    // soap ns "http://schemas.xmlsoap.org/soap/envelope/"
                    return ITK_DISTRIBUTION_ENVELOPE_XML;
                }
            }
        } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
        }

        // SOAP Asynch Spine
        // TODO
        if (content.contains(FHIR_NAMESPACE)) {
            if (isFHirMessaging(content)) {
                return FHIR_MESSAGING_XML;
            } else {
                requestBody = content;
                return FHIR_REST_XML;
            }
        }
        return UNKNOWN_XML;
    }

    /**
     * try to extract the interaction id
     *
     * @param xml
     * @return
     */
    private boolean isFHirMessaging(String xml) {
        // fhir messaging
        try {
            String action = XPathManager.xpathExtractor(FHIR_SERVICE_LOCATION, xml);
            if (!action.isEmpty()) {
                requestBody = xml;
                requestService = action;
                return true;
            }
        } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
        }
        return false;
    }

    /**
     * try xml parsing the content
     *
     * @param content
     * @return boolean
     */
    private boolean isXml(String content) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setErrorHandler(new SimpleErrorHandler());
            // the "parse" method also validates XML, will throw an exception if misformatted
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(content.getBytes())));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
        }
        return false;
    }

    /**
     * try json converting content
     *
     * @param content
     * @return boolean
     */
    private boolean isJson(String content) {
        try {
            String xml = JsonXmlConverter.jsonToXmlString(content.toCharArray());
            return true;
        } catch (Exception ex) {
        }
        return false;
    }
}
