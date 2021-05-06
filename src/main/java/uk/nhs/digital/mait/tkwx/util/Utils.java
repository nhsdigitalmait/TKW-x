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
package uk.nhs.digital.mait.tkwx.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import uk.nhs.digital.mait.commonutils.util.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.nhs.digital.mait.commonutils.util.xpath.XPathManager;

/**
 * container for useful static methods
 *
 * @author simonfarrow
 */
public class Utils {

    public static final String FUNCTION_PREFIX = "function:";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * @param st
     * @return boolean
     */
    public static boolean isNullOrEmpty(String st) {
        return st == null || st.trim().isEmpty();
    }

    /**
     * used to invoke static java methods (functions) specified in properties
     * files for setting properties or httpheader values
     *
     * @param value line containing java invocation text
     * function:&lt;fullyqualifiedclassname.method&gt; &lt;parameter&gt;+
     * @return String result of function invocation
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static String invokeJavaMethod(String value) throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        if (value.matches("^" + FUNCTION_PREFIX + ".*$")) {
            value = value.replaceFirst("^" + FUNCTION_PREFIX, "");
        } else {
            throw new IllegalArgumentException("java function invocation line does not start with " + FUNCTION_PREFIX);
        }
        // handle any parameters passed to the function
        String[] params = value.split("\\s+");
        Object[] oa = new Object[params.length - 1];
        Class[] parameterTypes = null;
        if (oa.length > 0) {
            parameterTypes = new Class[oa.length];
        }
        // handle any parameters passed to the function
        int i = 0;
        for (String param : params) {
            // first "parameter" is the fully qualified function name
            if (i > 0) {
                // The convention for the moment is only string parameters
                parameterTypes[i - 1] = String.class;
                oa[i - 1] = param;
            }
            i++;
        }
        String className = params[0].replaceFirst("^(.*)\\..*$", "$1");
        Class c = Class.forName(className);
        String methodName = params[0].replaceFirst("^.*\\.(.*)$", "$1");
        Method function = c.getMethod(methodName, parameterTypes);
        // initial null for static methods
        return (String) function.invoke(null, oa);
    }

    private Utils() {
    }

    /**
     *
     * @param file File object
     * @param s
     * @throws IOException
     */
    public static void writeString2File(File file, String s) throws IOException {
        try ( FileWriter fw = new FileWriter(file)) {
            fw.write(s);
            fw.flush();
        }
    }

    /**
     * overload method
     *
     * @param filepath String path
     * @param s
     * @throws IOException
     */
    public static void writeString2File(String filepath, String s) throws IOException {
        writeString2File(new File(filepath), s);
    }

    /**
     * TODO there must be a better library call for this html escaping text
     *
     * @param s
     * @return the html encoded text
     */
    public static String htmlEncode(String s) {
        String[][] substitutions = {
            {"&", "amp"}, {"\"", "quot"}, // quote the quote
            {"\u201c", "quot"}, // left double quote
            {"\u201d", "quot"}, // right double quote
            {">", "gt"}, // bra
            {"<", "lt"}, // ket
            {"'", "apos"}};   // single quote

        for (String[] sub : substitutions) {
            s = s.replaceAll(sub[0], "&" + sub[1] + ";");
        }
        return s;
    }

    /**
     * NB Prepends an xml PI There is an equivalent method in TKWPE Util which
     * uses different loggers
     *
     * @param xml
     * @return reformatted xml string or null on a failure
     */
    public static String xmlReformat(String xml) {
        try {
            final InputSource src = new InputSource(new StringReader(xml));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
            final Node documentNode = document.getDocumentElement();

            final String encoding = document.getXmlEncoding();
            final Boolean keepDeclaration = xml.startsWith("<?xml");

            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

            LSOutput output = impl.createLSOutput();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            output.setByteStream(out);
            writer.write(documentNode, output);
            String reformattedXML = new String(out.toByteArray());
            if (encoding == null) {
                // remove the encoding pragma
                return reformattedXML.replaceFirst(" *(encoding *= *\").*(\")", "");
            } else if (reformattedXML.matches("(?s)^<\\?.* *encoding *= *.* *\\?>.*$")) {
                // replace the encoding pragma
                // #12 fixed the regexp to match the whole of the first line
                return reformattedXML.replaceFirst("(encoding *= *\").*(\".*$)", "$1" + encoding + "$2");
            } else {
                return reformattedXML;
            }
        } catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException ex) {
            Logger.getInstance().log(SEVERE, Utils.class.getName(), "XML Reformat failed " + ex.getMessage());
            Logger.getInstance().log(FINE, Utils.class.getName(), "Offending non xml:" + xml);
        }
        return xml;
    }

    /**
     *
     * @param json
     * @return reformatted json
     */
    public static String jsonReformat(String json) {
        JsonReader jr = Json.createReader(new StringReader(json));
        JsonObject jobj = jr.readObject();

        StringWriter stringWriter = new StringWriter();
        Map<String, Boolean> config = buildConfig(JsonGenerator.PRETTY_PRINTING);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        try ( javax.json.JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.write(jobj);
        }
        return stringWriter.toString();
    }

    private static Map<String, Boolean> buildConfig(String... options) {
        Map<String, Boolean> config = new HashMap<>();

        if (options != null) {
            for (String option : options) {
                config.put(option, true);
            }
        }
        return config;
    }

    /**
     * perform efficient tag substitution using a StringBuilder These two
     * variations are in place to assist refactoring where there were clones of
     * both flavours throws Exception has been remove from the sig for
     * consistency
     *
     * @param sb the StringBuilder object
     * @param tag the tag name
     * @param value the value to substitute
     * @return whether anything was done
     */
    public static boolean substitute(StringBuilder sb, String tag, String value) {
        return substituteHandleNull(sb, tag, value, false);
    }

    /**
     * perform efficient tag substitution using a StringBuilder
     *
     * @param sb the StringBuilder object
     * @param tag the tag name
     * @param value the value to substitute
     * @return whether anything was done
     */
    public static boolean substituteHandleNull(StringBuilder sb, String tag, String value) {
        return substituteHandleNull(sb, tag, value, true);
    }

    /**
     * perform efficient tag substitution using a StringBuilder if value is null
     * and handle null is true return an empty string
     *
     * @param sb the StringBuilder object
     * @param tag the tag name
     * @param value the value to substitute
     * @return whether anything was done
     */
    private static boolean substituteHandleNull(StringBuilder sb, String tag, String value, boolean handleNull) {
        boolean doneAnything = false;
        int tagPoint = -1;
        int tagLength = tag.length();
        if (handleNull && value == null) {
            value = "";
        }
        while ((tagPoint = sb.indexOf(tag)) != -1) {
            sb.replace(tagPoint, tagPoint + tagLength, value);
            doneAnything = true;
        }
        return doneAnything;
    }

    /**
     * return the full text of a matching Antlr rule including whitespace from
     * the parsed source see
     * https://stackoverflow.com/questions/16343288/how-do-i-get-the-original-text-that-an-antlr4-rule-matched
     *
     * @param ctx an Antlr Parser RuleContext object
     * @return a String
     */
    public static String getTextFromAntlrRule(ParserRuleContext ctx) {
        CharStream charStream = ctx.start.getInputStream();
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        Interval interval = new Interval(a, b);
        return charStream.getText(interval);
    }

    /**
     * overload of readFile2String(BufferedReader)
     *
     * @param file path to file
     * @return string containing contents lines separated by line separator
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String readFile2String(String file) throws Exception {
        return readFile2String(new BufferedReader(new FileReader(file)));
    }

    /**
     *
     * @param dir
     * @param filename
     * @return string containing contents lines separated by line separator
     * @throws IOException
     */
    public static String readFile2String(String dir, String filename) throws Exception {
        return readFile2String(new File(dir, filename).getPath());
    }

    /**
     * overload of readFile2String(BufferedReader)
     *
     * @param is InputStream
     * @return string containing contents lines separated by line separator
     * @throws Exception
     */
    public static String readFile2String(InputStream is) throws Exception {
        return readFile2String(new BufferedReader(new InputStreamReader(is)));
    }

    /**
     * Appends OS dependent line separator to end of each line This is a change
     * not a refactoring
     *
     * @param br BufferedReader
     * @return string containing contents lines separated by line separator
     * @throws Exception
     */
    public static String readFile2String(BufferedReader br) throws Exception {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append(LINE_SEPARATOR);
        }
        br.close();
        return sb.toString();
    }

    /**
     * @param file file location
     * @return a string, string map of the colon separated file values
     * @throws Exception
     */
    public static Map<String, String> readColonSeparatedFile2Map(String file) throws Exception {
        Path path = FileSystems.getDefault().getPath(file);
        Map<String, String> map = Files.lines(path)
                .filter(s -> s.matches("^[A-Za-z0-9_]+:.+"))
                .collect(Collectors.toMap(k -> k.split(":")[0], v -> v.split(":")[1]));
        return map;
    }

    /**
     * @param in InputStream
     * @return a byte array containing the content of the stream
     * @throws Exception
     */
    public static byte[] streamToByteArray(InputStream in) throws Exception {
        // clone the stream
        if (in != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * mainly used for converting potentially null property values into booleans
     * Default behaviour if not set is to return false
     *
     * @param s String containing property value
     * @return boolean
     */
    public static boolean isY(String s) {
        return s != null && s.trim().toUpperCase().startsWith("Y");
    }

    /**
     * mainly used for converting potentially null property values into booleans
     * Default behaviour if not set is to return true
     *
     * @param s String containing property value
     * @return boolean
     */
    public static boolean isYDefaultY(String s) {
        if (s == null) {
            return true;
        } else {
            return s.trim().toUpperCase().startsWith("Y");
        }
    }

    /**
     * read a properties file into a properties object
     *
     * @param propertiesFile
     * @param p
     * @return whether successful
     */
    public static boolean readPropertiesFile(String propertiesFile, Properties p) {
        if (new File(propertiesFile).exists()) {
            try {
                // read the properties file for future use
                try ( FileInputStream fis = new FileInputStream(propertiesFile)) {
                    // read the properties file for future use
                    p.load(fis);
                }
                return true;
            } catch (FileNotFoundException ex) {
                // should never get here
                Logger.getInstance().log(Level.SEVERE, Utils.class.getName(), "Properties File " + propertiesFile + " does not exist: " + ex.getMessage());
            } catch (IOException ex) {
                Logger.getInstance().log(Level.SEVERE, Utils.class.getName(), "Properties File " + propertiesFile + " failed to load: " + ex.getMessage());
            }
        } else {
            Logger.getInstance().log(Level.SEVERE, Utils.class.getName(), "Properties file " + propertiesFile + " does not exist");
        }
        return false;
    }

    /**
     * TODO there must be a better library call for this html escaping text
     *
     * @param s
     * @return the escaped text
     */
    public static String doEscape(String s) {
        // ampersand
        s = s.replaceAll("&", "&amp;");

        // quote the quote
        s = s.replaceAll("\"", "&quot;");

        // left double quote
        s = s.replaceAll("\u201C", "&quot;");

        // right double quote
        s = s.replaceAll("\u201D", "&quot;");

        // >
        s = s.replaceAll(">", "&gt;");

        // <
        s = s.replaceAll("<", "&lt;");

        // apostrophe
        s = s.replaceAll("'", "&apos;");

        return s;
    }

    public static boolean fileExists(String path) {
        File f = new File(path);
        return f.exists() && f.isFile();
    }

    public static boolean folderExists(String path) {
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }

    /**
     * copy file from source to dest folder
     *
     * @param source (fully qualified path and name)
     * @param dest (fully qualified path to dest file )
     * @return success/fail
     */
    public static boolean copyFile(String source, String dest) {
        boolean result = true;
        Path sourceFile = FileSystems.getDefault().getPath(source);
        Path destPath = FileSystems.getDefault().getPath(dest);
        try {
            Files.copy(sourceFile, destPath, REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getInstance().log(Level.SEVERE, Utils.class.getName(), "Failed to copy " + source + " to " + dest + " " + ex.getMessage());
            result = false;
        }
        return result;
    }

    // Binary payload helpers
    
    //private final static String NAMESPACE_NAME = "nhsd";
    //private final static String BINARY_NAMESPACE = "uk.nhs.digital.mait.tkwx.binary";
    private final static String BINARY_TAGNAME = /*BINARY_NAMESPACE + ":"+*/ "bytes";

    /**
     * does the source file need base 64 encoding and wrapping as a binary
     * payload?
     *
     * @param filename
     * @return boolean
     */
    public static boolean isBinarySourceFile(String filename) {
        return !filename.matches("^.*\\.(xml|json|txt|msg)$");
    }

    /**
     * does the payload string contain binary base64 encoded content?
     *
     * @param str
     * @return boolean
     */
    public static boolean isBinaryPayloadString(String str) {
        return str.length() > ("<" + BINARY_TAGNAME).length()  && str.startsWith("<" + BINARY_TAGNAME);
    }

    /**
     * does the incoming payload bytearray contain binary content?
     *
     * @param bytes
     * @return boolean
     */
    public static boolean isBinaryPayload(byte[] bytes) {
        return bytes.length > 0 && (bytes[0] != '<' && bytes[0] != '{' && !(bytes.length>=4 && bytes[0]=='-'&& bytes[1]=='-'&& bytes[2]=='-'&& bytes[3]=='-'));
    }

    /**
     * base64 encode the binary payload and wrap with an xml
     * element
     * 
     * @param bytes
     * @return StringBuilder
     */
    public static StringBuilder wrapBinaryPayload(byte[] bytes) {
        StringBuilder msg = new StringBuilder("<" + BINARY_TAGNAME  + " unencoded_length=\""+bytes.length+"\">\r\n");
        msg.append(Base64.getEncoder().encodeToString(bytes));
        msg.append("\r\n</" + BINARY_TAGNAME + ">");
        return msg;
    }

    /**
     * base64 decode and return binary payload
     *
     * @param str
     * @return byte array
     */
    public static byte[] unwrapBinaryPayload(String str) {
        try {
            String base64 = XPathManager.xpathExtractor("/"+ BINARY_TAGNAME + "/text()", str);
            byte[] bytes = Base64.getDecoder().decode(base64.trim());
            return bytes;
        } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
            Logger.getInstance().log(SEVERE, Utils.class.getName(), "Failed extracting base 64 payload " + ex.getMessage());
        }
        return null;
    }
    
    public static int getUnencodedLength(String str) {
        try {
            int unencodedLength = Integer.parseInt(XPathManager.xpathExtractor("/"+ BINARY_TAGNAME + "/@unencoded_length", str));
            return unencodedLength;
        } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
            Logger.getInstance().log(SEVERE, Utils.class.getName(), "Failed extracting base 64 unencoded length " + ex.getMessage());
        }
        return -1;
    }
}
