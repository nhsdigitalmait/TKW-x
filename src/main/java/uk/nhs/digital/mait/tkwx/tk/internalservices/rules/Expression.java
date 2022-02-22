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
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.InputSource;
import uk.nhs.digital.mait.commonutils.util.xsltransform.TransformManager;
import java.io.CharArrayReader;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.function.UnaryOperator;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.nhs.digital.mait.tkwx.http.HttpRequest;
import uk.nhs.digital.mait.tkwx.mesh.MeshRequest;
import uk.nhs.digital.mait.tkwx.tk.boot.Request;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.ExpressionContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Expression_one_argContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Expression_two_argContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Expression_xpath_compareContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.JWTParser;
import static uk.nhs.digital.mait.tkwx.tk.internalservices.validation.XpathAssertionValidator.AUTHORIZATION_HEADER;
import uk.nhs.digital.mait.commonutils.util.Logger;
import uk.nhs.digital.mait.commonutils.util.configurator.Configurator;
import static uk.nhs.digital.mait.commonutils.util.xpath.XPathManager.getXpathExtractor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Class_extracted_valueContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Expression_classContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Expression_jsonpath_compareContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser.SimulatorRulesParser.Text_match_sourceContext;
import uk.nhs.digital.mait.tkwx.util.Utils;
import static uk.nhs.digital.mait.tkwx.util.Utils.isNullOrEmpty;
import static uk.nhs.digital.mait.tkwx.util.Utils.isY;
import static uk.nhs.digital.mait.tkwx.util.Utils.replaceTkwroot;
import uk.nhs.digital.mait.tkwx.validator.DomValidator;
import uk.nhs.digital.mait.tkwx.validator.SaxValidator;

/**
 * Encapsulates a rules test expression.
 *
 * @author Damian Murphy murff@warlock.org
 */
public class Expression {
    
    /**
     * Currently only one value but who knows?
     */
    public enum Encoding {

        B64("Base 64",   o -> { return new String(parseBase64Binary(o)); });
        
        /**
         * 
         * @param humanName
         * @param function  lambda for decode
         */
        Encoding(String humanName, UnaryOperator<String> function) {
            this.humanName = humanName;
            this.function = function;
        }
        
        /**
         * do the decode using the lambda
         * @param input
         * @return 
         */
        public String decode(String input){
            return function.apply(input);
        }
        
        @Override
        public String toString(){
            return humanName;
        }
        
        private final String humanName;
        // lambda for decode
        private final UnaryOperator<String> function;
    }

    private enum ExpressionType {
        NONE,
        XPATHEQUALS,
        XPATHNOTEQUALS,
        XSLT,
        CONTAINS,
        NOTCONTAINS,
        ALWAYS,
        NEVER,
        XPATHEXISTS,
        XPATHNOTEXISTS,
        XPATHCOMPARE,
        XPATHNOTCOMPARE,
        XPATHIN,
        XPATHNOTIN,
        XPATHMATCHES,
        XPATHNOTMATCHES,
        SCHEMA,
        MATCHES,
        NOTMATCHES,
        CLASS,
        // added json path expressions
        JSONPATHEQUALS,
        JSONPATHNOTEQUALS,
        JSONPATHEXISTS,
        JSONPATHNOTEXISTS,
        JSONPATHCOMPARE,
        JSONPATHNOTCOMPARE,
        JSONPATHIN,
        JSONPATHNOTIN,
        JSONPATHMATCHES,
        JSONPATHNOTMATCHES,

    }

    /**
     * required to qualify a text match ie one of [not]contains|[not]matches
     * which could be from one of several sources
     */
    public enum MatchSource {
        CONTENT, // typically this is XML
        CONTEXT_PATH, // a string
        HTTP_HEADER, // a string
        JWT_HEADER, // xml conversion of json JWT Header
        JWT_PAYLOAD, // xml conversion of json JWT Payoad
        MESH_CTL, //Mesh control file content
        MESH_DAT, //Mesh data file content
        VARIABLE, // session state variable set by a response
        JWT_HEADER_JSON, // native json JWT Header
        JWT_PAYLOAD_JSON // native json JWT Payoad
    }

    private static final String SIMULATORSCHEMACHECK = "tks.rules.simulatorschemacheck";

    private String name = null;
    private String expression = null;
    private String matchValue = null;
    private ExpressionType type = ExpressionType.NONE;
    private XPathExpression xpath = null;
    private XPathExpression matchXpath = null;
    private String[] inList = null;
    private Pattern regexPattern = null;
    private boolean simulatorschemacheck = false;
    private MatchSource matchSource = MatchSource.CONTENT;
    private MatchSource matchSource2 = null;
    private String httpHeaderName;
    private Encoding encoding = null;
    private String variableName; // session state variable name

    //  Simulator Expression Class
    private String className = null;
    private Class_extracted_valueContext[] classSourceCtx = null;
    private String[] classArgs = null;

    private ExpressionValue exprClass = null;

    /**
     * Package protected ANTLR Constructor
     *
     * @param ctx The ANTLR context for the parsed expression
     */
    Expression(ExpressionContext ctx) {
        try {
            Configurator config = Configurator.getConfigurator();
            simulatorschemacheck = isY(config.getConfiguration(SIMULATORSCHEMACHECK));
        } catch (Exception e) {
            Logger.getInstance().log(WARNING, Expression.class.getName(), "Simulator Schema check property not loaded");
        }

        name = ctx.expression_name().getText();
        if (ctx.expression_no_arg() != null) {
            type = ExpressionType.valueOf(ctx.expression_no_arg().getChild(0).getText().toUpperCase());
        } else if (ctx.expression_one_arg() != null) {
            handleOneArg(ctx);
        } else if (ctx.expression_two_arg() != null) {
            handleTwoArg(ctx);
        } else if (ctx.expression_xpath_compare() != null) {
            handleXpathCompare(ctx);
        } else if (ctx.expression_jsonpath_compare() != null) {
            handleJsonPathCompare(ctx);
        } else if (ctx.expression_class() != null) {
            handleClass(ctx);
        }
    }

    private void handleOneArg(ExpressionContext ctx) {
        Expression_one_argContext oneArgCtx = ctx.expression_one_arg();
        if (oneArgCtx.text_match_source() != null) {
            if (oneArgCtx.text_match_source().VARIABLE_NAME() != null) {
                matchSource = MatchSource.VARIABLE;
                variableName = oneArgCtx.text_match_source().VARIABLE_NAME().getText();
            } else {
                matchSource = MatchSource.valueOf(oneArgCtx.text_match_source().getChild(0).getText().toUpperCase());
            }
            if (oneArgCtx.text_match_source().http_header_name() != null) {
                httpHeaderName = oneArgCtx.text_match_source().http_header_name().getText();
                if ( oneArgCtx.text_match_source().header_encoding() != null ) {
                    encoding  = Encoding.valueOf(oneArgCtx.text_match_source().header_encoding().getText().toUpperCase());
                }
            }
        } else if (oneArgCtx.xml_match_source() != null) {
            matchSource = MatchSource.valueOf(oneArgCtx.xml_match_source().getText().toUpperCase());
        } else if (oneArgCtx.json_match_source() != null) {
            // the input to this could be a decoded b64 header
            matchSource = MatchSource.valueOf(oneArgCtx.json_match_source().getChild(0).getText().toUpperCase());
            if (oneArgCtx.json_match_source().http_header_name() != null ) {
                httpHeaderName = oneArgCtx.json_match_source().http_header_name().getText();
                if ( oneArgCtx.json_match_source().header_encoding() != null ) {
                    encoding  = Encoding.valueOf(oneArgCtx.json_match_source().header_encoding().getText().toUpperCase());
                }
            }
        } else {
            // default to the xml payload, not strictly necessary since this is the constructor set value
            matchSource = MatchSource.CONTENT;
        }
        
        if (oneArgCtx.match_type() != null) {
            type = ExpressionType.valueOf(oneArgCtx.match_type().getText().toUpperCase());
        } else if (oneArgCtx.xml_match_type() != null) {
            type = ExpressionType.valueOf(oneArgCtx.xml_match_type().getText().toUpperCase());
        }else if (oneArgCtx.json_match_type() != null) {
            type = ExpressionType.valueOf(oneArgCtx.json_match_type().getText().toUpperCase());
        }
        
        if (oneArgCtx.xpath_arg() != null) {
            expression = ctx.expression_one_arg().xpath_arg().getText();
        }
        
        switch (type) {
            case XPATHEXISTS:
            case XPATHNOTEXISTS:
                try {
                    initialiseXpath("count (" + expression + ")", false);
                } catch (Exception ex) {
                    Logger.getInstance().log(SEVERE, Expression.class.getName(), ex.getMessage());
                }
                break;
        }
    }

    private void handleTwoArg(ExpressionContext ctx) {
        Expression_two_argContext twoArgCtx = ctx.expression_two_arg();
        if (twoArgCtx.xml_match_source() != null) {
            matchSource = MatchSource.valueOf(twoArgCtx.xml_match_source().getText().toUpperCase());
            type = ExpressionType.valueOf(twoArgCtx.getChild(1).getText().toUpperCase());
        } else if (twoArgCtx.json_match_source() != null) {
            matchSource = MatchSource.valueOf(twoArgCtx.json_match_source().getChild(0).getText().toUpperCase());
            if (twoArgCtx.json_match_source().http_header_name() != null ) {
                httpHeaderName = twoArgCtx.json_match_source().http_header_name().getText();
                if ( twoArgCtx.json_match_source().header_encoding() != null ) {
                    encoding  = Encoding.valueOf(twoArgCtx.json_match_source().header_encoding().getText().toUpperCase());
                }
            }
            type = ExpressionType.valueOf(twoArgCtx.getChild(1).getText().toUpperCase());
        } else {
            // default to the xml payload, not strictly necessary since this is the constructor set value
            matchSource = MatchSource.CONTENT;
            type = ExpressionType.valueOf(twoArgCtx.getChild(0).getText().toUpperCase());
        }
        try {
            switch (type) {
                case XPATHMATCHES:
                case XPATHNOTMATCHES:
                    expression = twoArgCtx.xpath_arg(0).getText();
                    matchValue = twoArgCtx.xpath_arg(1).getText();
                    initialiseXpath(expression, false);
                    regexPattern = Pattern.compile(matchValue);
                    break;
                    
                case XPATHEQUALS:
                case XPATHNOTEQUALS:
                    expression = twoArgCtx.xpath_arg(0).getText();
                    matchValue = twoArgCtx.xpath_arg(1).getText();
                    initialiseXpath(expression, false);
                    break;
                    
                case XPATHIN:
                case XPATHNOTIN:
                    expression = twoArgCtx.xpath_arg(0).getText();
                    List<SimulatorRulesParser.Xpath_argContext> xpaths = twoArgCtx.xpath_arg();
                    inList = new String[xpaths.size() - 1];
                    initialiseXpath(expression, false);
                    for (int i = 1; i < xpaths.size(); i++) {
                        inList[i - 1] = xpaths.get(i).getText().replaceFirst("\"(.*)\"$", "$1");
                    }
                    break;
                    
                case SCHEMA:
                    expression = replaceTkwroot(twoArgCtx.PATH().getText());
                    if (twoArgCtx.xpath_arg().size() > 0) {
                        matchValue = twoArgCtx.xpath_arg(0).getText();
                        initialiseXpath(matchValue, false);
                    }
                    break;
                    
                case XSLT:
                    expression = replaceTkwroot(twoArgCtx.xslt_file().getText());
                    matchValue = twoArgCtx.xpath_arg(0).getText();
                    initialiseXslt();
                    break;
                    
                case JSONPATHMATCHES:
                case JSONPATHNOTMATCHES:
                    expression = twoArgCtx.xpath_arg(0).getText();
                    matchValue = twoArgCtx.xpath_arg(1).getText();
                    regexPattern = Pattern.compile(matchValue);
                    break;
                    
                case JSONPATHEQUALS:
                case JSONPATHNOTEQUALS:
                    expression = twoArgCtx.xpath_arg(0).getText();
                    matchValue = twoArgCtx.xpath_arg(1).getText();
                    break;
                    
                case JSONPATHIN:
                case JSONPATHNOTIN:
                    expression = twoArgCtx.xpath_arg(0).getText();
                    xpaths = twoArgCtx.xpath_arg();
                    inList = new String[xpaths.size() - 1];
                    for (int i = 1; i < xpaths.size(); i++) {
                        inList[i - 1] = xpaths.get(i).getText().replaceFirst("\"(.*)\"$", "$1");
                    }
                    break;
                    
                default:
                    throw new Exception("Syntax error: unrecognised expression type: " + type);
            }
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, Expression.class.getName(), ex.getMessage());
        }
    }

    private void handleXpathCompare(ExpressionContext ctx) {
        Expression_xpath_compareContext xpathCompareCtx = ctx.expression_xpath_compare();
        // default to the xml payload, not strictly necessary since this is the constructor set value
        matchSource = MatchSource.CONTENT;
        matchSource2 = null;
        if (xpathCompareCtx.xml_match_source() != null) {
            switch (xpathCompareCtx.xml_match_source().size()) {
                case 0:
                    type = ExpressionType.valueOf(xpathCompareCtx.getChild(0).getText().toUpperCase());
                    break;
                case 1:
                    matchSource = MatchSource.valueOf(xpathCompareCtx.xml_match_source(0).getText().toUpperCase());
                    type = ExpressionType.valueOf(xpathCompareCtx.getChild(1).getText().toUpperCase());
                    break;
                case 2:
                    matchSource = MatchSource.valueOf(xpathCompareCtx.xml_match_source(0).getText().toUpperCase());
                    matchSource2 = MatchSource.valueOf(xpathCompareCtx.xml_match_source(1).getText().toUpperCase());
                    type = ExpressionType.valueOf(xpathCompareCtx.getChild(2).getText().toUpperCase());
                    break;
                default:
                    Logger.getInstance().log(SEVERE, Expression.class.getName(), "Invalid match source count " + xpathCompareCtx.xml_match_source().size());
            }
            
            try {
                expression = xpathCompareCtx.xpath_arg(0).getText();
                matchValue = xpathCompareCtx.xpath_arg(1).getText();
                initialiseXpath(expression, false);
                initialiseXpath(matchValue, true);
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE, Expression.class.getName(), ex.getMessage());
                
            }
        } else {
            Logger.getInstance().log(SEVERE, Expression.class.getName(), "xml match source array context is null");
        }
    }


    private void handleJsonPathCompare(ExpressionContext ctx) {
        Expression_jsonpath_compareContext jsonpathCompareCtx = ctx.expression_jsonpath_compare();
        // default to the xml payload, not strictly necessary since this is the constructor set value
        matchSource = MatchSource.CONTENT;
        matchSource2 = null;
        if (jsonpathCompareCtx.json_match_source() != null) {
            switch (jsonpathCompareCtx.json_match_source().size()) {
                case 0:
                    type = ExpressionType.valueOf(jsonpathCompareCtx.getChild(0).getText().toUpperCase());
                    break;
                case 1:
                    matchSource = MatchSource.valueOf(jsonpathCompareCtx.json_match_source(0).getText().toUpperCase());
                   if (jsonpathCompareCtx.json_match_source(0).http_header_name() != null ) {
                        httpHeaderName = jsonpathCompareCtx.json_match_source(0).http_header_name().getText();
                        if ( jsonpathCompareCtx.json_match_source(0).header_encoding() != null ) {
                            encoding  = Encoding.valueOf(jsonpathCompareCtx.json_match_source(0).header_encoding().getText().toUpperCase());
                        }
                    }
                    type = ExpressionType.valueOf(jsonpathCompareCtx.getChild(1).getText().toUpperCase());
                    break;
                case 2:
                    matchSource = MatchSource.valueOf(jsonpathCompareCtx.json_match_source(0).getText().toUpperCase());
                    if (jsonpathCompareCtx.json_match_source(0).http_header_name() != null ) {
                        httpHeaderName = jsonpathCompareCtx.json_match_source(0).http_header_name().getText();
                        if ( jsonpathCompareCtx.json_match_source(0).header_encoding() != null ) {
                            encoding  = Encoding.valueOf(jsonpathCompareCtx.json_match_source(0).header_encoding().getText().toUpperCase());
                        }
                    }
                    
                    matchSource2 = MatchSource.valueOf(jsonpathCompareCtx.json_match_source(1).getText().toUpperCase());
                    if (jsonpathCompareCtx.json_match_source(1).http_header_name() != null ) {
                        httpHeaderName = jsonpathCompareCtx.json_match_source(1).http_header_name().getText();
                        if ( jsonpathCompareCtx.json_match_source(1).header_encoding() != null ) {
                            encoding  = Encoding.valueOf(jsonpathCompareCtx.json_match_source(1).header_encoding().getText().toUpperCase());
                        }
                    }
                    type = ExpressionType.valueOf(jsonpathCompareCtx.getChild(2).getText().toUpperCase());
                    break;
                default:
                    Logger.getInstance().log(SEVERE, Expression.class.getName(), "Invalid match source count " + jsonpathCompareCtx.json_match_source().size());
            }
            
            try {
                expression = jsonpathCompareCtx.xpath_arg(0).getText();
                matchValue = jsonpathCompareCtx.xpath_arg(1).getText();
            } catch (Exception ex) {
                Logger.getInstance().log(SEVERE, Expression.class.getName(), ex.getMessage());
                
            }
        } else {
            Logger.getInstance().log(SEVERE, Expression.class.getName(), "json match source array context is null");
        }
    }

    private void handleClass(ExpressionContext ctx) {
        Expression_classContext classCtx = ctx.expression_class();
        type = ExpressionType.CLASS;
        
        className = classCtx.DOT_SEPARATED_IDENTIFIER().getText();
        
        classSourceCtx = new Class_extracted_valueContext[classCtx.class_extracted_value().size()];
        for (int i = 0; i < classSourceCtx.length; i++) {
            classSourceCtx[i] = classCtx.class_extracted_value(i);
        }
        
        classArgs = new String[classCtx.class_args().QUOTED_STRING().size()];
        for (int i = 0; i < classArgs.length; i++) {
            classArgs[i] = classCtx.class_args().QUOTED_STRING(i).getText();
        }
    }

    /**
     * do the evaluation
     *
     * @param o primary string to be tested may be content or jwt payload etc
     * @param o2 optional second string to be tested for cross compares where we
     * are comparing eg an xpath in the content with one in the JWT payload
     * @return pass or fail
     * @throws Exception
     */
    boolean evaluate(Request req)
            throws Exception {

        String o = getMatchContent(req, getMatchSource(), matchSource == MatchSource.VARIABLE ? variableName : httpHeaderName, encoding);
        String o2 = null;
        if (getMatchSource2() != null && getMatchSource2() != getMatchSource()) {
            // onyl second match source is for xpath compare which can't use httpHeader as a match source 
            o2 = getMatchContent(req, getMatchSource2(), null, null);
        }

        String x = null;
        String y = null;
        Matcher m = null;
        int nodeCount = 0; // for xml node exists check
        switch (type) {
            case ALWAYS:
                return true;

            case NEVER:
                return false;

            case XPATHEQUALS:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    return x.contentEquals(matchValue);
                } else {
                    return false;
                }

            case XPATHNOTEQUALS:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    return !x.contentEquals(matchValue);
                } else {
                    return true;
                }

            case XSLT:
                TransformManager t = TransformManager.getInstance();
                x = t.doTransform(name, o);
                // NB This is incorrectly(?) returning true on a match, which disagrees with the manual
                return x.contains(matchValue);

            case CONTAINS:
                return o != null && evaluateVariable(expression) != null && o.contains(evaluateVariable(expression));

            case NOTCONTAINS:
                return o == null || evaluateVariable(expression) == null || !o.contains(evaluateVariable(expression));

            case XPATHEXISTS:
            case XPATHNOTEXISTS:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    try {
                        nodeCount = Integer.parseInt(x);
                    } catch (NumberFormatException ex) {
                    }
                } else {
                    nodeCount = 0;
                }
                return type == ExpressionType.XPATHEXISTS ? nodeCount > 0 : nodeCount == 0;

            case XPATHCOMPARE:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    y = evaluateXpath(matchXpath, o2 == null ? o : o2);
                    if ((x.equals("")) || (y.equals(""))) {
                        return false;
                    } else if (x.equals(y)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            case XPATHNOTCOMPARE:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    y = evaluateXpath(matchXpath, o2 == null ? o : o2);
                    if ((x.equals("")) || (y.equals(""))) {
                        return false;
                    } else if (x.equals(y)) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }

            case XPATHIN:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    if (!x.equals("")) {
                        for (String s : inList) {
                            if (x.equals(s)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                } else {
                    return false;
                }

            case XPATHNOTIN:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    if (!x.equals("")) {
                        for (String s : inList) {
                            if (x.equals(s)) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                } else {
                    return true;
                }
            case XPATHMATCHES:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    if (!x.equals("")) {
                        m = regexPattern.matcher(x);
                        return m.find();
                    }
                    return false;
                } else {
                    return false;
                }

            case XPATHNOTMATCHES:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateXpath(xpath, o);
                    if (!x.equals("")) {
                        m = regexPattern.matcher(x);
                        return !m.find();
                    }
                    // NB This was wrong when the elements not there return true
                    return true;
                } else {
                    return true;
                }
            case SCHEMA:
                if (simulatorschemacheck) {
                    StringReader subjectReader = new StringReader(o);
                    StreamSource subjectSource = new StreamSource(subjectReader);
                    File f = new File(expression);
                    StreamSource schemaSource = new StreamSource(f);
                    if (matchValue == null) {
                        InputSource is = new InputSource(subjectReader);
                        SaxValidator sv = new SaxValidator(is, schemaSource);
                        return !sv.validate();
                    } else {
                        return doDomValidation(subjectSource, schemaSource, false);
                    }
                } else {
                    return true;
                }

            case MATCHES:
                return o != null && evaluateVariable(expression) != null && o.matches(evaluateVariable(expression));

            case NOTMATCHES:
                return o == null || evaluateVariable(expression) == null || !o.matches(evaluateVariable(expression));

            case CLASS:
                // assemble an array of text values instantiate the class and invoke with the parameters
                String[] values = new String[classSourceCtx.length];
                int i = 0;
                for (Class_extracted_valueContext classSource : classSourceCtx) {
                    String source = null;
                    if (classSource.data_source().text_match_source() != null) {
                        Text_match_sourceContext textMatchCtx = classSource.data_source().text_match_source();
                        String type = textMatchCtx.HTTP_HEADER() != null ? textMatchCtx.HTTP_HEADER().getText() : textMatchCtx.getText();
                        if (textMatchCtx.header_encoding() != null) {
                            encoding = Encoding.valueOf(textMatchCtx.header_encoding().getText().toUpperCase());
                        }
                        source = getMatchContent(req, MatchSource.valueOf(type.toUpperCase()),
                                textMatchCtx.HTTP_HEADER() != null ? textMatchCtx.http_header_name().IDENTIFIER().getText() : null, encoding);
                    } else if (classSource.data_source().xml_match_source() != null) {
                        source = getMatchContent(req, MatchSource.valueOf(classSource.data_source().xml_match_source().getText().toUpperCase()), null, null);
                    }

                    if (source != null) {
                        values[i++] = source.replaceFirst(classSource.class_reg_exp_from().getText(), classSource.class_reg_exp_replace().getText());
                    }
                }

                return initialiseExpressionClass(className, values, classArgs);

            case JSONPATHEQUALS:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    return x != null && x.contentEquals(matchValue);
                } else {
                    return false;
                }

            case JSONPATHNOTEQUALS:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    return x != null && !x.contentEquals(matchValue);
                } else {
                    return true;
                }
            case JSONPATHEXISTS:
            case JSONPATHNOTEXISTS:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    return type == ExpressionType.JSONPATHEXISTS ? x != null : x == null;
                } else {
                    return type != ExpressionType.JSONPATHEXISTS;
                }

            case JSONPATHCOMPARE:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    y = evaluateJsonpath(matchValue, o2 == null ? o : o2);
                    if (x == null || y == null ) {
                        return false;
                    } else return x.equals(y);
                } else {
                    return false;
                }

            case JSONPATHNOTCOMPARE:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    y = evaluateJsonpath(matchValue, o2 == null ? o : o2);
                    if (x == null || y == null ) {
                        return true;
                    } else return !x.equals(y);
                } else {
                    return true;
                }

            case JSONPATHIN:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    if (x != null) {
                        for (String s : inList) {
                            if (x.equals(s)) {
                                return true;
                            }
                        }
                    }
                } 
                return false;

            case JSONPATHNOTIN:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    if (x != null) {
                        for (String s : inList) {
                            if (x.equals(s)) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                } else {
                    return true;
                }

            case JSONPATHMATCHES:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    if (x != null) {
                        m = regexPattern.matcher(x);
                        return m.find();
                    }
                } 
                return false;

            case JSONPATHNOTMATCHES:
                if (!Utils.isNullOrEmpty(o)) {
                    x = evaluateJsonpath(expression, o);
                    if (x != null) {
                        m = regexPattern.matcher(x);
                        return !m.find();
                    }
                }
                return true;

            default:
                Logger.getInstance().log(SEVERE, Expression.class.getName(), "Unrecognised expression type " + type);
                return false;
        }
    }

    /**
     * return a string containing the constant against which the expression will
     * be evaluated eg content, context path, http header value etc
     *
     * @param req the Http Request object
     * @param matchSource enum indicating the source type
     * @param name of http header or variable
     * @return a string containing the text to be checked against
     * @throws Exception
     */
    public static String getMatchContent(Request req, MatchSource matchSource, String name, Encoding encoding) throws Exception {
        String o = null;
        JWTParser jwtParser;
        HttpRequest httpReq = null;
        MeshRequest meshReq = null;
        if (req instanceof HttpRequest) {
            httpReq = (HttpRequest) req;
        } else if (req instanceof MeshRequest) {
            meshReq = (MeshRequest) req;
        }
        switch (matchSource) {
            case CONTENT:
                if (httpReq != null && httpReq.getBody() != null) {
                    o = new String(httpReq.getBody());
                } else if (meshReq != null && meshReq.getRequestMeshMessage() != null) {
                    o = new String(meshReq.getRequestMeshMessage().getDataFile().getContent());
                } else {
                    o = "";
                }
                break;
            case CONTEXT_PATH:
                o = httpReq.getContext();
                break;
            case HTTP_HEADER:
                o = httpReq.getField(name);
                break;
            case JWT_HEADER:
                jwtParser = new JWTParser(httpReq.getField(AUTHORIZATION_HEADER));
                o = jwtParser.getXmlHeader();
                break;
            case JWT_PAYLOAD:
                jwtParser = new JWTParser(httpReq.getField(AUTHORIZATION_HEADER));
                o = jwtParser.getXmlPayload();
                //System.out.println("JWT Payload = \r\n"+xmlReformat(o));
                break;
            case MESH_CTL:
                o = meshReq.getRequestMeshMessage().getControlFile().getCtlFileContents();
                break;
            case MESH_DAT:
                o = new String(meshReq.getRequestMeshMessage().getDataFile().getContent());
                break;
            case VARIABLE:
                o = SessionStateManager.getInstance().getValue(name);
                break;
                
                // added native json support
            case JWT_HEADER_JSON:
                jwtParser = new JWTParser(httpReq.getField(AUTHORIZATION_HEADER));
                o = jwtParser.getJsonHeader();
                break;
            case JWT_PAYLOAD_JSON:
                jwtParser = new JWTParser(httpReq.getField(AUTHORIZATION_HEADER));
                o = jwtParser.getJsonPayload();
                //System.out.println("JWT Payload = \r\n"+xmlReformat(o));
                break;
            default:
                Logger.getInstance().log(SEVERE, Rule.class.getName(), "unhandled match source " + matchSource);
        }

        if (encoding != null && ! isNullOrEmpty(o)) {
            o = encoding.decode(o);
        }
        
        return o;
    }

    protected boolean doDomValidation(StreamSource toValidate, StreamSource schema, boolean stripHeader)
            throws Exception {
        Node n = getValidationRoot(toValidate, stripHeader);
        DomValidator dv = new DomValidator(n, schema);
        return !dv.validate();
    }

    protected Node getValidationRoot(StreamSource s, boolean stripHeader)
            throws Exception {
        try {
            InputSource is = new InputSource(s.getReader());
            Node n = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            Document doc = dbf.newDocumentBuilder().parse(is);
            if (stripHeader) {
                n = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body").item(0);
                if (n != null) {
                    NodeList nl = n.getChildNodes();
                    for (int i = 0; i < nl.getLength(); i++) {
                        Node x = nl.item(i);
                        if (x.getNodeType() == Node.ELEMENT_NODE) {
                            return x;
                        }
                    }
                }
                throw new Exception("Error evaluating validation root - cannot find SOAP Body content");
            } else if (xpath == null) {
                n = doc.getDocumentElement();
            } else {
                n = (Node) xpath.evaluate(doc, XPathConstants.NODE);
            }
            if (n == null) {
                throw new Exception("Failed to evaluate " + matchValue + " no match");
            }
            return n;
        } catch (Exception e) {
            throw new Exception("Error evaluating validation root: " + e.getMessage());
        }
    }

    private String evaluateXpath(XPathExpression xe, String input)
            throws Exception {
        CharArrayReader car = new CharArrayReader(input.toCharArray());
        InputSource is = new InputSource(car);
        try {
            String s = xe.evaluate(is);
            if (s == null) {
                return "";
            }
            return s;
        } catch (Exception e) {
            throw new Exception("Exception evaluating Xpath rule: " + e.getMessage());
        }
    }

    private String evaluateJsonpath(String jsonpath, String input)
            throws Exception {
        String result = null;
        try {
            DocumentContext jsonContext = JsonPath.parse(input);
            result =  jsonContext.read(jsonpath);    
        } catch (com.jayway.jsonpath.InvalidPathException ex) {
        } 
        return result;
    }

    private void initialiseXpath(String s, boolean isMatchValue)
            throws Exception {
        try {
            if (isMatchValue) {
                matchXpath = getXpathExtractor(s);
            } else {
                xpath = getXpathExtractor(s);
            }
        } catch (XPathExpressionException e) {
            StringBuilder sb = new StringBuilder("XPath expression error: ");
            if (e.getCause() != null) {
                sb.append(e.getCause().getMessage());
            }
            sb.append(" in expression ");
            sb.append(s);
            throw new Exception("Failed to compile XPath expression: " + s + " : " + sb.toString());
        }
    }

    private void initialiseXslt()
            throws Exception {
        TransformManager t = TransformManager.getInstance();

        // expression is a filename
        t.addTransform(name, expression);
    }

    /**
     *
     * @return name of the expression
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return enum type of match eg CONTENT/CONTEXT_PATH/HTTP_HEADER
     */
    public MatchSource getMatchSource() {
        return matchSource;
    }

    /**
     *
     * @return enum type of second match source (used for xpathcompare) eg
     * CONTENT/CONTEXT_PATH/HTTP_HEADER
     */
    public MatchSource getMatchSource2() {
        return matchSource2;
    }

    /**
     * delayed evaluation of variables if its a session state variable get the
     * current value at the time of evaluation
     *
     * @param expr
     * @return variable substituted with value or original value if not a
     * variable
     */
    private String evaluateVariable(String expr) {
        if (expr != null && expr.startsWith("$")) {
            return SessionStateManager.getInstance().getValue(expr);
        } else {
            return expr;
        }
    }

    /**
     * instantiate and invoke an expression class object
     *
     * @param className
     * @param values String array of extracted values to use
     * @param args String array of additional arguments eg comparison operators
     * to apply to a pair of values
     * @return boolean expression result
     * @throws Exception
     */
    private boolean initialiseExpressionClass(String className, String[] values, String[] args)
            throws Exception {
        exprClass = (ExpressionValue) Class.forName(className).newInstance();
        return exprClass.getValue(values, args);
    }

}
