/*
 Copyright 2014 Damian Murphy <damian.murphy@hscic.gov.uk>

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
package uk.nhs.digital.mait.tkwx.jsonconverter;

import java.io.CharArrayReader;
import java.io.StringWriter;
import java.util.logging.Level;
import static java.util.logging.Level.SEVERE;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import static uk.nhs.digital.mait.commonutils.util.CfHNamespaceContext.NHS_DIGITAL_JSON;

/**
 *
 * @author damian
 */
public class JsonXmlConverter {

    public static final String JSONSOURCEDXMLNAMESPACE = NHS_DIGITAL_JSON;
    public static final String XMLTOJSONCONVERTERATTRIBUTEMAXLENGTH = "tks.jsonconverter.JsonXmlConverter.attributemaxlength";
    public static int ATTRIBUTEMAXLENGTH = 64;
    private static Configurator config;

    static {
        try {
            config = Configurator.getConfigurator();
            String sp = config.getConfiguration(XMLTOJSONCONVERTERATTRIBUTEMAXLENGTH);
            if ((sp != null) && (sp.trim().length() > 0)) {
                try {
                    ATTRIBUTEMAXLENGTH = Integer.parseInt(sp);
                } catch (NumberFormatException e) {
                    Logger.getInstance().log(SEVERE, JsonXmlConverter.class.getName(), "Attribute Max Length is not an integer - " + sp);
                }
            }
        } catch (Exception e) {
            Logger.getInstance().log(SEVERE, JsonXmlConverter.class.getName(), "Failure to retrieve attribute max length property - " + e.toString());
        }
    }

    public static String jsonToXmlString(char[] buffer)
            throws Exception {
        Document d = jsonToXmlDocument(buffer);
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformManager.getInstance().getTransformerFactory().newTransformer();
        tx.transform(new DOMSource(d), sr);
        return sw.toString();
    }

    public static Document jsonToXmlDocument(char[] buffer)
            throws Exception {
        Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        try (
                CharArrayReader car = new CharArrayReader(buffer);
                JsonParser jp = Json.createParser(car)) {
            Element root = xml.createElementNS(JSONSOURCEDXMLNAMESPACE, "json");
            xml.appendChild(root);
            Element current = root;
            Element newlevel = null;
            String containerKey = null;
            while (jp.hasNext()) {
                // Receive the events. We get "key name" and then get the next event which
                // determines what we're going to do with it.

                JsonParser.Event event = jp.next();
                switch (event) {
                    case KEY_NAME:
                        containerKey = jp.getString();
                        JsonParser.Event content = jp.next();
                        String value = null;
                        switch (content) {
                            case START_ARRAY:
                                newlevel = xml.createElementNS(JSONSOURCEDXMLNAMESPACE, containerKey + "s");
                                current.appendChild(newlevel);
                                current = newlevel;
                                break;

                            case START_OBJECT:
                                newlevel = xml.createElementNS(JSONSOURCEDXMLNAMESPACE, containerKey);
                                current.appendChild(newlevel);
                                current = newlevel;
                                break;

                            case VALUE_STRING:
                                value = jp.getString();
                                // Have a look at this string and see if needs to be stored as an
                                // element, or if it can be done as an attribute (i.e. look for
                                // tabs and vertical whitespace, or length > 64 characters)
                                //
                                boolean maketextnode = false;
                                if (value.length() > ATTRIBUTEMAXLENGTH) {
                                    Logger.getInstance().log(Level.WARNING, JsonXmlConverter.class.getName(), "Attribute Max Length (" + value.length() + ") is greater than the assigned " + ATTRIBUTEMAXLENGTH);
                                }
                                maketextnode = (value.length() > ATTRIBUTEMAXLENGTH)
                                        || value.contains("&")
                                        || value.contains("<")
                                        || value.contains("\"")
                                        || value.contains("\'")
                                        || value.contains("\n")
                                        || value.contains(">")
                                        || value.contains("\t");
                                if (maketextnode) {
                                    Element textContainer = xml.createElementNS(JSONSOURCEDXMLNAMESPACE, containerKey);
                                    current.appendChild(textContainer);
                                    Element text = xml.createElementNS(JSONSOURCEDXMLNAMESPACE, "text");
                                    text.appendChild(xml.createTextNode(value));
                                    textContainer.appendChild(text);
                                } else {
                                    current.setAttribute(containerKey, value);
                                }
                                break;

                            case VALUE_NUMBER:
                                value = jp.getString();
                                current.setAttribute(containerKey, value);
                                break;

                            case VALUE_TRUE:
                                value = "true";
                                current.setAttribute(containerKey, value);
                                break;

                            case VALUE_FALSE:
                                value = "false";
                                current.setAttribute(containerKey, value);
                                break;

                            case VALUE_NULL:
                                value = "";
                                current.setAttribute(containerKey, value);
                                break;

                            default:
                                // This shouldn't happen as it means that there is some
                                // syntax error in the JSON. So throw an exception.
                                //
                                // TODO: Add debugging detail
                                //
                                throw new Exception("Unexpected event parsing JSON");
                        }
                        break;

                    case START_OBJECT:
                        // If we're not at the root element, create an element to contain this
                        // object
                        //
                        if (containerKey != null) {
                            newlevel = xml.createElementNS(JSONSOURCEDXMLNAMESPACE, containerKey);
                            current.appendChild(newlevel);
                            current = newlevel;
                        }
                        break;

                    case END_OBJECT:
                    case END_ARRAY:
                        if (!(current.getParentNode() instanceof org.w3c.dom.Element)) // at root
                        {
                            break;
                        }
                        current = (Element) current.getParentNode();
                        if (current.getParentNode() == null) {
                            containerKey = null;
                        } else {
                            containerKey = current.getNodeName();
                        }
                        break;

                }

            }
        }
        return xml;
    }

}
