/*
  Copyright 2012  Damian Murphy murff@warlock.org

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
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.CharArrayReader;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;
import static java.util.UUID.randomUUID;
import java.util.logging.Level;
import static java.util.logging.Level.WARNING;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import static uk.nhs.digital.mait.tkwx.tk.GeneralConstants.*;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.internalservices.FHIRJsonXmlAdapter;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.getMatchContent;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;
import uk.nhs.digital.mait.commonutils.util.Logger;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.tkwx.tk.boot.ServiceResponse;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.Encoding;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource.*;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource.CONTEXT_PATH;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource.HTTP_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource.JWT_HEADER;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.rules.Expression.MatchSource.JWT_PAYLOAD;
import static uk.nhs.digital.mait.tkwx.util.Utils.readPropertiesFile;
import static uk.nhs.digital.mait.tkwx.util.Utils.replaceTkwroot;

/**
 * Defines a substitution that may be performed on a response template, to make
 * a message instance. Provides a set of pre-defined substitution types, and
 * supports substitution from a class implementing the SubstitutionValue
 * interface.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Substitution {

    public enum SubstitutionType {
        UUID_LOWER,
        UUID_UPPER,
        HL7DATE,
        ISO8601DATE,
        RFC822DATE,
        XPATH,
        LITERAL,
        PROPERTY,
        CLASS,
        REGEXP,
        JSONPATH;
    }

    /**
     * required to qualify a text match ie one of [not]contains|[not]matches
     * which could be from one of several sources
     */
    private String tag = null;
    private String literalContent = null;
    private SubstitutionType type = null;
    private String[] data = null;
    private int tagLength = 0;
    private SubstitutionValue subsClass = null;
    private XPathExpression xpath = null;
    private MatchSource matchSource = MatchSource.CONTENT;
    private String jsonpath = null;

    private static final SimpleDateFormat HL7FORMATDATE = new SimpleDateFormat(HL7FORMATDATEMASK);
    private static final SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat(ISO8601FORMATDATEMASK);
    private static final SimpleDateFormat RFC822FORMATDATE = new SimpleDateFormat(RFC822FORMATDATEMASK);
    private String httpHeaderName;
    private Encoding encoding = null;

    /**
     * Package private ANTLR parser based constructor
     *
     * @param ctx
     */
    Substitution(SimulatorRulesParser.SubstitutionContext ctx) {

        tag = ctx.substitution_tag().getText();
        tagLength = tag.length();

        if (ctx.substitution_no_arg() != null) {
            handleNoArg(ctx);
        } else if (ctx.substitution_xpath() != null) {
            handleXpath(ctx);
        }  else if (ctx.substitution_jsonpath() != null) {
            handleJsonpath(ctx);
        } else if (ctx.substitution_literal() != null) {
            handleLiteral(ctx);
        } else if (ctx.substitution_property() != null) {
            handleProperty(ctx);
        } else if (ctx.substitution_class() != null) {
            handleClass(ctx);
        } else if (ctx.substitution_regexp() != null) {
            handleRegExp(ctx);
        }
    }
    
    private void handleNoArg(SimulatorRulesParser.SubstitutionContext ctx) {
        if (ctx.substitution_no_arg().UUID_LOWER() != null) {
            type = SubstitutionType.UUID_LOWER;
        } else if (ctx.substitution_no_arg().UUID_UPPER() != null) {
            type = SubstitutionType.UUID_UPPER;
        } else if (ctx.substitution_no_arg().HL7_DATETIME() != null) {
            type = SubstitutionType.HL7DATE;
        } else if (ctx.substitution_no_arg().ISO8601_DATETIME() != null) {
            type = SubstitutionType.ISO8601DATE;
        } else if (ctx.substitution_no_arg().RFC822_DATETIME() != null) {
            type = SubstitutionType.RFC822DATE;
        }
    }

    private void handleXpath(SimulatorRulesParser.SubstitutionContext ctx) {
        // XPath or Xpath
        type = SubstitutionType.XPATH;
        try {
            // xpath returns 1 or 2 args
            switch (ctx.substitution_xpath().xpath_arg().size()) {
                case 1:
                    initialiseXpath(ctx.substitution_xpath().xpath_arg().get(0).getText());
                    break;
                case 2:
                    // optional match source
                    matchSource = MatchSource.valueOf(ctx.substitution_xpath().xpath_arg().get(0).getText().toUpperCase());
                    initialiseXpath(ctx.substitution_xpath().xpath_arg().get(1).getText());
                    break;
            }
        } catch (Exception ex) {
            Logger.getInstance().log(Level.SEVERE, Substitution.class.getName(), ex.getMessage());
        }
    }

    private void handleJsonpath(SimulatorRulesParser.SubstitutionContext ctx) {
        type = SubstitutionType.JSONPATH;
        try {
            // jsonpath returns 1..3 args
            switch (ctx.substitution_jsonpath().xpath_arg().size()) {
                case 1:
                    initialiseJsonpath(ctx.substitution_jsonpath().xpath_arg().get(0).getText());
                    break;
                case 2:
                    // optional match source + jsonpath
                    matchSource = MatchSource.valueOf(ctx.substitution_jsonpath().xpath_arg().get(0).getText().toUpperCase());
                    initialiseJsonpath(ctx.substitution_jsonpath().xpath_arg().get(1).getText());
                    break;
                case 4:
                    // match source + header name + encoding + jsonpath
                    matchSource = MatchSource.valueOf(ctx.substitution_jsonpath().xpath_arg().get(0).getText().toUpperCase());
                    if ( matchSource == HTTP_HEADER ) {
                        encoding = Encoding.valueOf(ctx.substitution_jsonpath().xpath_arg().get(1).getText().toUpperCase());
                        httpHeaderName = ctx.substitution_jsonpath().xpath_arg().get(2).getText();
                        initialiseJsonpath(ctx.substitution_jsonpath().xpath_arg().get(3).getText());
                    }
                    break; 
            }
        } catch (Exception ex) {
            Logger.getInstance().log(Level.SEVERE, Substitution.class.getName(), ex.getMessage());
        }
    }
    
    private void handleLiteral(SimulatorRulesParser.SubstitutionContext ctx) {
        type = SubstitutionType.LITERAL;
        literalContent = ctx.substitution_literal().ANNOTATION_TEXT().getText();
    }

    private void handleProperty(SimulatorRulesParser.SubstitutionContext ctx) {
        type = SubstitutionType.PROPERTY;
        // 0..n of property file name plus a property name
        int propertyFileNameCount = ctx.substitution_property().property_file_name().size();
        data = new String[propertyFileNameCount + 1];
        for (int i = 0; i < propertyFileNameCount; i++) {
            data[i] = replaceTkwroot(ctx.substitution_property().property_file_name(i).getText());
        }
        data[propertyFileNameCount] = ctx.substitution_property().property_name().getText();
    }

    
    private void handleClass(SimulatorRulesParser.SubstitutionContext ctx) {
        type = SubstitutionType.CLASS;
        String className = ctx.substitution_class().DOT_SEPARATED_IDENTIFIER().getText();
        
        if (ctx.substitution_class().text_match_source() != null) {
            matchSource = MatchSource.valueOf(ctx.substitution_class().text_match_source().getChild(0).getText().toUpperCase());
            if (matchSource == MatchSource.HTTP_HEADER) {
                httpHeaderName = ctx.substitution_class().text_match_source().http_header_name().getText();
                if ( ctx.substitution_class().text_match_source().header_encoding() != null ) {
                    encoding = Encoding.valueOf(ctx.substitution_class().text_match_source().header_encoding().getText().toUpperCase());
                }
            }
        } else {
            matchSource = MatchSource.CONTENT;
        }
        
        try {
            if (ctx.substitution_class().IDENTIFIER() != null) {
                initialiseSubstituterClass(className, ctx.substitution_class().IDENTIFIER().getText());
            } else if (ctx.substitution_class().QUOTED_STRING() != null) {
                initialiseSubstituterClass(className, ctx.substitution_class().QUOTED_STRING().getText());
            } else {
                initialiseSubstituterClass(className, null);
            }
        } catch (Exception ex) {
            Logger.getInstance().log(Level.SEVERE, Substitution.class.getName(), ex.getMessage());
        }
    }

    private void handleRegExp(SimulatorRulesParser.SubstitutionContext ctx) {
        type = SubstitutionType.REGEXP;
        if (ctx.substitution_regexp().text_match_source() != null) {
            matchSource = MatchSource.valueOf(ctx.substitution_regexp().text_match_source().getChild(0).getText().toUpperCase());
            if (matchSource == MatchSource.HTTP_HEADER) {
                httpHeaderName = ctx.substitution_regexp().text_match_source().http_header_name().getText();
                if ( ctx.substitution_regexp().text_match_source().header_encoding() != null ) {
                    encoding = Encoding.valueOf(ctx.substitution_regexp().text_match_source().header_encoding().getText().toUpperCase());
                }
            }
        } else {
            matchSource = MatchSource.CONTENT;
        }
        
        // handle multiple sequences of reg exp
        data = new String[3 * ctx.substitution_regexp().regexp().size()];
        Iterator<SimulatorRulesParser.RegexpContext> iter = ctx.substitution_regexp().regexp().iterator();
        int index = 0;
        while (iter.hasNext()) {
            SimulatorRulesParser.RegexpContext regExCtx = iter.next();
            
            data[index++] = regExCtx.QUOTED_STRING(0).getText(); // search reg exp
            data[index++] = regExCtx.QUOTED_STRING(1).getText(); // replace regexp
            if (regExCtx.substitution_regexp_cardinality() != null) {
                data[index] = regExCtx.substitution_regexp_cardinality().getText().toLowerCase();
            } else {
                data[index] = "first";
            }
            index++;
        }
    }

    /**
     * Non ANTLR parser based constructor used by MeshInterceptWorker
     *
     * @param tg tag String
     * @param typ the type of substitution - this constructor only supports
     * types 1-6
     * @param dt data when required
     * @param lc literal content when required
     * @param xp xpath when required
     *
     */
    public Substitution(String tg, SubstitutionType typ, String[] dt, String lc, String xp) {
        if (typ.ordinal() >= SubstitutionType.CLASS.ordinal()) {
            System.out.println(ERROR_MESSAGE);
            throw new RuntimeException(ERROR_MESSAGE);
        }
        tag = tg;
        tagLength = tag.length();
        type = typ;
        data = dt;
        literalContent = lc;
        if (xp != null) {
            try {
                initialiseXpath(xp);
            } catch (Exception ex) {
                Logger.getInstance().log(Level.SEVERE, Substitution.class.getName(), ex.getMessage());
            }
        }
    }
    private static final String ERROR_MESSAGE = "Can only use the non ANTLR constructor for UUID_LOWER,UUID_UPPER,HL7DATE,ISO8601DATE,RFC822DATE,XPATH,LITERAL,PROPERTY";

//    /**
//     * Package private Constructor for NON antlr parsing
//     *
//     * @param t
//     * @param typ
//     * @param d
//     * @throws Exception
//     */
//    @Deprecated
//    Substitution(String t, String typ, String[] d)
//            throws Exception {
//        tag = t;
//        data = d;
//        tagLength = tag.length();
//        if (typ.contentEquals("UUID")) {
//            type = UUID;
//            return;
//        }
//        if (typ.contentEquals("HL7datetime")) {
//            type = HL7DATE;
//            return;
//        }
//        if (typ.contentEquals("ISO8601datetime")) {
//            type = ISO8601DATE;
//            return;
//        }
//        if (typ.contentEquals("Xpath")) {
//            type = XPATH;
//            if ((data == null) || (data.length == 0)) {
//                throw new Exception("Syntax error in substitution: no Xpath expression found for tag " + tag);
//            }
//            initialiseXpath(data[0]);
//            return;
//        }
//        if (typ.contentEquals("XPath")) {
//            type = XPATH;
//            if ((data == null) || (data.length == 0)) {
//                throw new Exception("Syntax error in substitution: no Xpath expression found for tag " + tag);
//            }
//            initialiseXpath(data[0]);
//            return;
//        }
//        if (typ.contentEquals("Literal")) {
//            type = LITERAL;
//            if ((data == null) || (data.length == 0)) {
//                throw new Exception("Syntax error in substitution: no value found for literal tag " + tag);
//            }
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < (data.length - 1); i++) {
//                sb.append(data[i]);
//                sb.append(" ");
//            }
//            sb.append(data[data.length - 1]);
//            literalContent = sb.toString();
//            return;
//        }
//        if (typ.contentEquals("Property")) {
//            type = PROPERTY;
//            if ((data == null) || (data.length == 0)) {
//                throw new Exception("Syntax error in substitution: no property name found for tag " + tag);
//            }
//            return;
//        }
//        if (typ.contentEquals("Class")) {
//            type = CLASS;
//            if ((data == null) || (data.length == 0)) {
//                throw new Exception("Syntax error in substitution: no class name found for tag " + tag);
//            }
//            if (data.length == 1) {
//                initialiseSubstituterClass(data[0], null);
//            } else {
//                initialiseSubstituterClass(data[0], data[1]);
//            }
//            return;
//        }
//        throw new Exception("Unrecognised type : " + typ);
//    }
    // This method may be replaced by the similar method in UTILS but this uses StringBuffer c.f. StringBuilder.
    // StringBuffer is however synchronised so has been kept here
    private boolean subs(StringBuffer sb, String value)
            throws Exception {
        int tagPoint = -1;
        boolean doneAnything = false;
        while ((tagPoint = sb.indexOf(tag)) != -1) {
            sb.replace(tagPoint, tagPoint + tagLength, value);
            doneAnything = true;
        }
        return doneAnything;
    }

    /**
     *
     * @param sb StringBuffer holding response template
     * @param o request string to use for substitution
     * @throws Exception
     */
    public void substitute(StringBuffer sb, String o)
            throws Exception {
        String content = getContent(o);
        subs(sb, content);
    }

    /**
     * Method for passing in a full Service Response and having all the
     * substitution points updated. This is important for values such as UUID
     * where it is generated each time substitution is called
     *
     * @param sr ServiceResponse holding response templates
     * @param o request string to use for substitution
     * @throws Exception
     */
    public void substitute(ServiceResponse sr, String o)
            throws Exception {
        String content = getContent(o);
        StringBuffer response = new StringBuffer(sr.getResponse());
        if (subs(response, content)) {
            sr.setResponse(response.toString());
        }
        if (sr.getHttpHeaders() != null) {
            ArrayList<String> fnames = sr.getHttpHeaders().getFieldNames();
            for (String name : fnames) {
                StringBuffer headerValue = new StringBuffer(sr.getHttpHeaders().getHttpHeaderValue(name));
                if (subs(headerValue, content)) {
                    sr.getHttpHeaders().addHttpHeader(name, headerValue.toString());
                }
            }
        }

    }

    /**
     *
     * @param sb StringBuffer holding response template
     * @param req Request object
     * @throws Exception
     */
    public void substitute(StringBuffer sb, Request req)
            throws Exception {
        substitute(sb, getMatchContent(req, matchSource, httpHeaderName, encoding));
    }

    /**
     * Method for passing in a full Service Response and having all the
     * substitution points updated. This is important for values such as UUID
     * where it is generated each time substitution is called
     *
     * @param sr
     * @param req
     * @throws Exception
     */
    public void substitute(ServiceResponse sr, Request req) throws Exception {
        substitute(sr, getMatchContent(req, matchSource, httpHeaderName, encoding));
    }

    /**
     *
     * @return
     */
    public String getTag() {
        return tag;
    }

    /**
     *
     * @param inputRequest string containing source
     * @return value to substitute
     * @throws Exception
     */
    private String getContent(String inputRequest)
            throws Exception {

        String content = null;
        switch (type) {
            case UUID_LOWER:
                content = randomUUID().toString();
                content = content.toLowerCase();
                break;

            case UUID_UPPER:
                content = randomUUID().toString();
                content = content.toUpperCase();
                break;

            case HL7DATE:
                content = HL7FORMATDATE.format(new Date());
                break;

            case ISO8601DATE:
                ISO8601FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
                content = ISO8601FORMATDATE.format(new Date());
                break;

            case RFC822DATE:
                RFC822FORMATDATE.setTimeZone(TimeZone.getTimeZone("GMT"));
                content = RFC822FORMATDATE.format(new Date());
                break;

            case XPATH:
                if (inputRequest != null) {
                    if (inputRequest.trim().isEmpty() || inputRequest.trim().startsWith("<")) {
                        if (xpath == null) {
                            content = "CONFIGURATION ERROR: Xpath expression not set for tag " + tag;
                        } else {
                            // if we have an empty string this might be a call to doc to insert content
                            // if so it needs some minimal xml to work on
                            if (inputRequest.trim().isEmpty()) {
                                inputRequest = "<a/>";
                            }
                            content = resolveXpath(inputRequest);
                        }

                    } else if (inputRequest.trim().startsWith("{")) {
                        // try fhir json to xml conversion this assumes if its json then its fhir json
                        String xml = FHIRJsonXmlAdapter.fhirConvertJson2Xml(inputRequest);
                        content = resolveXpath(xml);
                    } else {
                        Logger.getInstance().log(WARNING, Substitution.class.getName(),
                                "getContent detected non xml or json request content:\r\n" + inputRequest);
                    }
                }
                break;
            case JSONPATH:
                if (inputRequest != null) {
                    if (inputRequest.trim().isEmpty() || inputRequest.trim().startsWith("{")) {
                        if (jsonpath == null) {
                            content = "CONFIGURATION ERROR: JSONpath expression not set for tag " + tag;
                        } else {
                            // if we have an empty string this might be a call to doc to insert content
                            // if so it needs some minimal json to work on
                            if (inputRequest.trim().isEmpty()) {
                                inputRequest = "{}";
                            }
                            content = resolveJsonpath(inputRequest);
                        }
                    } else {
                        Logger.getInstance().log(WARNING, Substitution.class.getName(),
                                "getContent detected non json request content:\r\n" + inputRequest);
                    }
                } else {
                    inputRequest = "";
                }
                break;

            case LITERAL:
                content = (literalContent == null) ? "NOT SET" : literalContent;
                break;

            case PROPERTY:
                String propertyName = data[data.length - 1];
                if (propertyName == null || propertyName.trim().isEmpty()) {
                    content = "CONFIGURATION ERROR: Property name not set";
                } else {
                    if (data.length == 1) {
                        if (System.getProperties().getProperty(propertyName) == null) {
                            content = "CONFIGURATION ERROR: Property " + propertyName + " not set";
                        } else {
                            content = System.getProperties().getProperty(propertyName);
                        }
                    } else {
                        // these file may not exist so don't flag an error if empty
                        for (int i = 0; i < data.length - 1; i++) {
                            String propertyFileName = data[i];
                            if (new File(propertyFileName).exists()) {
                                Properties properties = new Properties();
                                readPropertiesFile(propertyFileName, properties);
                                if (properties.getProperty(propertyName) != null) {
                                    content = properties.getProperty(propertyName);
                                }
                            }
                        }
                    }
                    if (content == null) {
                        content = "CONFIGURATION ERROR: Property " + propertyName + " not set";
                    }
                }
                break;

            case CLASS:
                if (subsClass == null) {
                    content = "CONFIGURATION ERROR: Substitution class not set";
                } else {
                    if (inputRequest.trim().isEmpty() || 
                            ((matchSource == CONTENT || matchSource == JWT_HEADER || matchSource == JWT_PAYLOAD || matchSource == MESH_CTL) && inputRequest.trim().startsWith("<"))) {
                    } else if (matchSource == CONTEXT_PATH || matchSource == HTTP_HEADER || matchSource == MESH_DAT || matchSource == VARIABLE)  {
                    } else if (matchSource == CONTENT && inputRequest.trim().startsWith("{")) {
                        // try fhir json to xml conversion this assumes if its json then its fhir json
                        inputRequest = FHIRJsonXmlAdapter.fhirConvertJson2Xml(inputRequest);
                    } else {
                        Logger.getInstance().log(WARNING, Substitution.class.getName(),
                                "getContent detected non xml or json request content:\r\n" + inputRequest);
                    }
                    content = subsClass.getValue(inputRequest);
                }
                break;

            case REGEXP:
                if (inputRequest != null) {
                    content = inputRequest;
                    for (int offset = 0; offset < data.length; offset += 3) {
                        switch (data[offset+2]) {
                            case "all":
                                content = content.replaceAll(data[offset], data[offset+1]);
                                break;
                            case "first":
                            default:
                                content = content.replaceFirst(data[offset], data[offset+1]);
                                break;
                        }
                    }
                } else {
                    content = null;
                }
                break;

            default:
                content = "";
                break;
        }
        return content;
    }

    private String resolveXpath(String input)
            throws Exception {
        CharArrayReader car = new CharArrayReader(input.toCharArray());
        InputSource is = new InputSource(car);
        String s = xpath.evaluate(is);
        if (s == null) {
            return "";
        }
        return s;
    }

    private String resolveJsonpath(String input)
            throws Exception {
        DocumentContext jsonContext = JsonPath.parse(input);
        return  jsonContext.read(jsonpath);
    }

    private void initialiseXpath(String s)
            throws Exception {
        try {
            // handle any embedded TKW_ROOT expressions
            s = s.replaceAll("TKW_ROOT", System.getenv("TKWROOT") != null ? System.getenv("TKWROOT"):"TKW_ROOT");
            xpath = getXpathExtractor(s);
        } catch (XPathExpressionException e) {
            StringBuilder sb = new StringBuilder("XPath expression error: ");
            if (e.getCause() != null) {
                sb.append(e.getCause().getMessage());
            }
            sb.append(" in expression ");
            sb.append(s);
            throw new Exception("Failed to compile XPath expression: " + s + " - " + sb.toString() + "for tagName " + tag);
        }
    }

    private void initialiseJsonpath(String s)
            throws Exception {
            // handle any embedded TKW_ROOT expressions
            s = s.replaceAll("TKW_ROOT", System.getenv("TKWROOT") != null ? System.getenv("TKWROOT"):"TKW_ROOT");
            jsonpath = s;
    }

    private void initialiseSubstituterClass(String c, String a)
            throws Exception {
        subsClass = (SubstitutionValue) Class.forName(c).newInstance();
        if (a != null) {
            subsClass.setData(a);
        }
    }
}
