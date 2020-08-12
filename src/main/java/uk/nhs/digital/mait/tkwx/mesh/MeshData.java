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
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Class to encapsulate the MESH data file. Can be used to parse an existing
 * mesh data file or create a new one
 *
 * @author riro
 */
public class MeshData {

    private byte[] content = null;
    private int contentLength = -1;
    private MeshMessage meshMessage = null;
    private Path dataFilePath = null;

    /**
     * Constructor for creating an new Mesh Data file
     *
     * @param mm MeshMessage to use to create MESH Data file
     */
    public MeshData(MeshMessage mm) {
        meshMessage = mm;
    }

    /**
     * Method for creating an new Mesh Data file with path
     *
     * @param message String representation of data file contents
     * @param path Path for which to create the data file
     * @throws Exception
     */
    public void createMeshData(String message, Path path) throws Exception {
        setDataFilePath(path);
        createMeshData(message);
    }

    /**
     * Method for creating an new Mesh Data file
     *
     * @param message String representation of data file contents
     * @throws Exception
     */
    public void createMeshData(String message) throws Exception {

        try {
            validateWellFormed(new InputSource(new StringReader(message)));

        } catch (Exception ioe) {
            throw new MeshDataException("Response message " + meshMessage.getMeshFileName() + ".dat is not well formed: " + ioe.getMessage());
        }
        content = message.getBytes();
        contentLength = content.length;
        if (contentLength <= 0) {
            throw new Exception("No content in Response message " + meshMessage.getMeshFileName() + ".dat");
        }

    }

    /**
     * Method for parsing an existing Mesh Data file
     *
     * @param path The file location to find the message
     * @throws MeshDataException
     */
    public void parseMeshData(Path path) throws MeshDataException {
        String dataFile = meshMessage.getMeshFileName() + ".dat";
        try {
            dataFilePath = Paths.get(path.toString(), dataFile);
            content = Files.readAllBytes(dataFilePath);
            contentLength = content.length;
            if (contentLength <= 0) {
                throw new Exception("No content in the Mesh data file: " + dataFilePath.toString());
            }
            if (containsBOM(content)) {
                throw new Exception("Message contains BOM (Byte Order Marks) in the Mesh data file: " + dataFilePath.toString());
            }
            InputSource is = new InputSource(dataFilePath.toString());
            validateWellFormed(is);

        } catch (Exception ioe) {
            throw new MeshDataException("Can't read the Mesh data file: " + dataFilePath.toString() + ioe.getMessage());
        }
    }

    private boolean containsBOM(byte[] bom) throws IOException {
        /* <p>The
 * <a href="http://www.unicode.org/unicode/faq/utf_bom.html">Unicode FAQ</a>
 * defines 5 types of BOMs:<ul>
 * <li><pre>00 00 FE FF  = UTF-32, big-endian</pre></li>
 * <li><pre>FF FE 00 00  = UTF-32, little-endian</pre></li>
 * <li><pre>FE FF        = UTF-16, big-endian</pre></li>
 * <li><pre>FF FE        = UTF-16, little-endian</pre></li>
 * <li><pre>EF BB BF     = UTF-8</pre></li>
 * </ul></p>        
         */

        if ((bom[0] == (byte) 0xFF)
                && (bom[1] == (byte) 0xFE)
                && (bom[2] == (byte) 0x00)
                && (bom[3] == (byte) 0x00)) {
            return true;
        } else if ((bom[0] == (byte) 0x00)
                && (bom[1] == (byte) 0x00)
                && (bom[2] == (byte) 0xFE)
                && (bom[3] == (byte) 0xFF)) {
            return true;
        } else if ((bom[0] == (byte) 0xEF)
                && (bom[1] == (byte) 0xBB)
                && (bom[2] == (byte) 0xBF)) {
            return true;
        } else if ((bom[0] == (byte) 0xFF)
                && (bom[1] == (byte) 0xFE)) {
            return true;
        } else if ((bom[0] == (byte) 0xFE)
                && (bom[1] == (byte) 0xFF)) {
            return true;
        } else {
            return false;
        }

    }

    private void validateWellFormed(InputSource is) throws IOException, SAXException, ParserConfigurationException {
        // Basic parse of the input data file using SAX for efficiency to test
        // that the content is Well-Formed
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SAXParser parser = factory.newSAXParser();

        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());
        reader.parse(is);
    }

    /**
     * logging of the initial parsing of the file cannot happen until the
     * requestId is known, hence a separate method
     *
     * @throws Exception
     */
    public void logDataFile(String logMarker) throws Exception {
        meshMessage.log("Data File: " + dataFilePath.toString(), 2, false);
        meshMessage.log(new String(content), 1, true);
        meshMessage.log("\r\n" + logMarker, 2, true);
    }

    public byte[] getContent() {
        return content;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Path getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(Path path) {
        dataFilePath = path;

    }

    private class SimpleErrorHandler implements ErrorHandler {

        public void warning(SAXParseException e) throws SAXException {
            System.out.println(e.getMessage());
        }

        public void error(SAXParseException e) throws SAXException {
            System.out.println(e.getMessage());
        }

        public void fatalError(SAXParseException e) throws SAXException {
            System.out.println(e.getMessage());
        }
    }
}
