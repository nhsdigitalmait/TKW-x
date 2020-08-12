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
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import static java.util.logging.Level.SEVERE;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import uk.nhs.digital.mait.tkwx.tk.internalservices.antlrerrorlisteners.VerboseErrorListener;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParserBaseVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationLexer;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser.Unchecked_testContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser.Xpath_multi_arg_testContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser.Xpath_one_arg_testContext;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser.Xpath_two_arg_testContext;
import uk.nhs.digital.mait.commonutils.util.Logger;

/**
 * Main scanner for validation config files
 *
 * @author simonfarrow
 */
public class ValidationGrammarCompilerVisiter extends ValidationParserBaseVisitor {

    private final static boolean DEBUG = false;

    private final ArrayList<RulesetMetadata> rulesetMetadatas;
    private RulesetMetadata rulesetMetadata;
    private boolean metadataAdded;
    private Validation lastValidation;
    private HashMap<String, ValidationSet> validationSets;
    private HashMap<String, ValidationSet> subroutines;

    private ValidationSet currentValidationSet;
    private Validation currentValidation;
    private Properties bootProperties;

    private BaseErrorListener[] customErrorListeners = null;

    /**
     * public constructor
     *
     * @param bootProperties
     */
    public ValidationGrammarCompilerVisiter(Properties bootProperties) {
        rulesetMetadatas = new ArrayList<>();
        rulesetMetadata = new RulesetMetadata();
        metadataAdded = false;
        lastValidation = null;
        this.bootProperties = bootProperties;
        validationSets = new HashMap<>();
        subroutines = new HashMap<>();
        currentValidation = null;
        currentValidationSet = null;
    }

    private ValidationParser getParser(String fileName) throws IOException {

        ValidationLexer lexer
                = new ValidationLexer(new ANTLRFileStream(fileName));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        ValidationParser parser = new ValidationParser(tokens);

        // set up error listeners
        parser.removeErrorListeners();
        parser.addErrorListener(new VerboseErrorListener(fileName));
        if (customErrorListeners != null) {
            for (BaseErrorListener el : customErrorListeners) {
                parser.addErrorListener(el);
            }
        }

        if (DEBUG) {
            dumpTokens(parser, tokens);
        }

        return parser;
    }

    private void dumpTokens(ValidationParser parser, CommonTokenStream tokens) {
        Vocabulary vocab = parser.getVocabulary();

        // this forces some population of the stream .. dont ask.
        tokens.getNumberOfOnChannelTokens();

        // this api doesnt seem to work as expected hence we just give up gracefully when we overrun
        // the token stream
        for (int i = 0;; i++) {
            try {
                Token token = tokens.get(i);
                System.out.println(vocab.getDisplayName(token.getType()) + ":" + token.getText());
            } catch (IndexOutOfBoundsException ex) {
                break;
            }
        }
    }

    /**
     *
     * @param fileName
     * @throws IOException
     */
    public void parse(String fileName) throws IOException {
        ValidationParser parser = getParser(fileName);

        ParseTree pt = parser.input();

        visit(pt);
    }

    /**
     * @return the rulesetMetadatas
     */
    public ArrayList<RulesetMetadata> getMetadata() {
        if (metadataAdded) {
            rulesetMetadatas.add(0, rulesetMetadata);
            metadataAdded = false;
        }
        return rulesetMetadatas;
    }

    public ValidationSet getSubroutine(String r) {
        return subroutines.get(r);
    }

    public void clear() {
        validationSets = null;
        subroutines = null;
        bootProperties = null;
    }

    public ValidationSet getValidationSet(String s)
            throws Exception {
        if (validationSets == null) {
            return null;
        }
        return validationSets.get(s);
    }

    public HashMap<String, ValidationSet> getValidationSets() {
        return validationSets;
    }

    public HashMap<String, ValidationSet> getSubroutines() {
        return subroutines;
    }

    /**
     * return a precise location for the error
     *
     * @param ctx
     * @return
     */
    private static String getLocus(ParserRuleContext ctx) {
        return "File: " + ctx.getStart().getTokenSource().getSourceName() + " line " + ctx.getStart().getLine();
    }

    private Validation instantiateValidator(String sequenceType) throws Exception {
        Validation v = new Validation(sequenceType);
        lastValidation = v;
        v.setService(currentValidationSet.getServiceName());
        if (currentValidation != null) {
            currentValidation.appendValidation(v);
            v.setVariableProvider(currentValidation);
        } else {
            currentValidationSet.addValidation(v);
            v.setVariableProvider(currentValidationSet);
        }
        return v;
    }

    /**
     * parse a test statement into a Validation object sets type, resource and
     * data (if present)
     *
     * @param testCtx
     * @param v Validation object to populate
     */
    private void handleTestStatement(ValidationParser.Test_statementContext testCtx, Validation v) {
        String xmlSource = " content";
        // test statement
        if (testCtx.no_arg_test() != null) {
            ValidationParser.No_arg_testContext no_arg = testCtx.no_arg_test();
            if (no_arg.hapifhirvalidator_id() != null) {
                v.setType(no_arg.getChild(0).getText() + "." + no_arg.hapifhirvalidator_id().getText());
            } else {
                v.setType(testCtx.getChild(0).getText());
            }

        } else if (testCtx.schema_test() != null) {
            v.setType(testCtx.schema_test().schema_type().getText());
            v.setResource(testCtx.schema_test().schema_path().getText());
            if (testCtx.schema_test().schema_xpath() != null) {
                v.setData(testCtx.schema_test().schema_xpath().getText());
            }

        } else if (testCtx.xpath_one_arg_test() != null) {
            Xpath_one_arg_testContext oneArgCtx = testCtx.xpath_one_arg_test();
            if (oneArgCtx.xpath_one_arg_type().text_match_type() != null) {
                String matchSource = "content";
                if (oneArgCtx.xpath_one_arg_type().text_match_source() != null) {
                    if (oneArgCtx.xpath_one_arg_type().text_match_source().HTTP_HEADER() != null) {
                        String httpHeaderName = oneArgCtx.xpath_one_arg_type().text_match_source().http_header_name().getText();
                        matchSource = oneArgCtx.xpath_one_arg_type().text_match_source().HTTP_HEADER().getText() + " " + httpHeaderName;
                    } else {
                        matchSource = oneArgCtx.xpath_one_arg_type().text_match_source().getText();
                    }
                }
                v.setType(oneArgCtx.xpath_one_arg_type().text_match_type().getText() + " " + matchSource);
            } else if (oneArgCtx.xpath_one_arg_type().xml_match_source() != null) {
                xmlSource = " " + oneArgCtx.xpath_one_arg_type().xml_match_source().getText();
                v.setType(oneArgCtx.xpath_one_arg_type().xpath_one_arg_comparison_type().getText() + xmlSource);
            } else {
                v.setType(oneArgCtx.xpath_one_arg_type().getText());
            }
            v.setResource(oneArgCtx.CST().getText());

        } else if (testCtx.xpath_two_arg_test() != null) {
            Xpath_two_arg_testContext twoArgCtx = testCtx.xpath_two_arg_test();
            if (twoArgCtx.xpath_two_arg_type().xpath_two_arg_comparison_type() != null) {
                if (twoArgCtx.xpath_two_arg_type().xml_match_source() != null) {
                    xmlSource = " " + twoArgCtx.xpath_two_arg_type().xml_match_source().getText();
                }
                v.setType(twoArgCtx.xpath_two_arg_type().xpath_two_arg_comparison_type().getText() + xmlSource);
            } else {
                v.setType(twoArgCtx.xpath_two_arg_type().getText());
            }
            v.setResource(twoArgCtx.xpath_arg().get(0).getText());
            StringBuilder sb = new StringBuilder(twoArgCtx.xpath_arg().get(1).getText());
            int size = twoArgCtx.xpath_arg().size();
            for (int i = 2; i < size; i++) {
                sb.append(" ").append(twoArgCtx.xpath_arg().get(i).getText());
            }
            v.setData(sb.toString());

        } else if (testCtx.xpath_multi_arg_test() != null) {
            Xpath_multi_arg_testContext multiArgCtx = testCtx.xpath_multi_arg_test();
            if (multiArgCtx.xml_match_source() != null) {
                xmlSource = " " + multiArgCtx.xml_match_source().getText();
            }
            v.setType(multiArgCtx.xpath_multi_arg_type().getText() + xmlSource);
            v.setResource(multiArgCtx.xpath_arg(0).getText());
            // the rest goes into data
            StringBuilder sb = new StringBuilder(multiArgCtx.xpath_arg().get(1).getText());
            int size = multiArgCtx.xpath_arg().size();
            for (int i = 2; i < size; i++) {
                sb.append(" ").append(multiArgCtx.xpath_arg().get(i).getText());
            }
            v.setData(sb.toString());
        } else if (testCtx.unchecked_test() != null) {
            // handles additonal validations added at a later date - no syntax checking on arguments
            Unchecked_testContext uncheckedCtx = testCtx.unchecked_test();
            v.setType(uncheckedCtx.unchecked_test_name().getText());
            switch (uncheckedCtx.xpath_arg().size()) {
                default:
                    StringBuilder sb = new StringBuilder(uncheckedCtx.xpath_arg(1).getText());
                    int size = uncheckedCtx.xpath_arg().size();
                    for (int i = 2; i < size; i++) {
                        sb.append(" ").append(uncheckedCtx.xpath_arg(i).getText());
                    }
                    v.setData(sb.toString());
                // drop through
                case 1:
                    v.setResource(uncheckedCtx.xpath_arg(0).getText());
                    break;
                case 0:
                    break;
            }
        }
    }

// --------------------------- Visitor overrides -------------------------------
    @Override
    public Object visitInclude_statement(ValidationParser.Include_statementContext ctx) {
        //System.err.println("Including " + ctx.PATH());
        // This inserts rather than appends to get the same order as before
        if (metadataAdded) {
            rulesetMetadatas.add(0, rulesetMetadata);
            rulesetMetadata = new RulesetMetadata();
            metadataAdded = false;
        }

        try {
            parse(ctx.PATH().getText());
        } catch (FileNotFoundException ex) {
            Logger.getInstance().log(SEVERE, ValidationGrammarCompilerVisiter.class.getName(), "Parser failure "
                    + getLocus(ctx) + " FileNotFoundException " + ex.getMessage() + " including file " + ctx.PATH().getText());
        } catch (IOException ex) {
            Logger.getInstance().log(SEVERE, ValidationGrammarCompilerVisiter.class.getName(), "Parser failure"
                    + getLocus(ctx) + "IOException " + ex.getMessage() + " including file " + ctx.PATH().getText());
        }
        return super.visitInclude_statement(ctx);
    }

    @Override
    public Object visitValidation_header(ValidationParser.Validation_headerContext ctx) {
        ValidationParser.Validation_header_typeContext headerTypeCtx = ctx.validation_header_type();
        if (headerTypeCtx.VALIDATION_RULESET_AUTHOR() != null) {
            rulesetMetadata.setAuthor(ctx.ANNOTATION_TEXT().getText());
        } else if (headerTypeCtx.VALIDATION_RULESET_NAME() != null) {
            rulesetMetadata.setName(ctx.ANNOTATION_TEXT().getText());
        } else if (headerTypeCtx.VALIDATION_RULESET_TIMESTAMP() != null) {
            rulesetMetadata.setTimestamp(ctx.ANNOTATION_TEXT().getText());
        } else if (headerTypeCtx.VALIDATION_RULESET_VERSION() != null) {
            rulesetMetadata.setVersion(ctx.ANNOTATION_TEXT().getText());
        }
        metadataAdded = true;
        return super.visitValidation_header(ctx);
    }

    @Override
    public Object visitValidate_statement(ValidationParser.Validate_statementContext ctx) {
        String serviceName = ctx.service_name().getText();

        if ((currentValidationSet = validationSets.get(serviceName)) == null) {
            currentValidationSet = new ValidationSet();
            currentValidationSet.setServiceName(serviceName);
        }
        validationSets.put(serviceName, currentValidationSet);
        return super.visitValidate_statement(ctx);
    }

    @Override
    public Object visitSubset_statement(ValidationParser.Subset_statementContext ctx) {
        String subsetName = ctx.subset_name().getText();
        currentValidationSet = new ValidationSet();
        currentValidationSet.setServiceName(subsetName);
        subroutines.put(subsetName, currentValidationSet);
        return super.visitSubset_statement(ctx);
    }

    @Override
    public Object visitAnnotation_directive(ValidationParser.Annotation_directiveContext ctx) {
        if (lastValidation != null) {
            lastValidation.setAnnotation(ctx.ANNOTATION_TEXT().getText());
        }
        return super.visitAnnotation_directive(ctx);
    }

    @Override
    public Object visitIf_directive(ValidationParser.If_directiveContext ctx) {
        try {
            Validation v = instantiateValidator(ctx.IF().getText());
            handleTestStatement(ctx.test_statement(), v);
            currentValidation = v.initialise(bootProperties);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, ValidationGrammarCompilerVisiter.class.getName(), "visitIf_directive " + getLocus(ctx) + "\r\n" + ex.getMessage());
        }
        return super.visitIf_directive(ctx);
    }

    @Override
    public Object visitThen_clause(ValidationParser.Then_clauseContext ctx) {
        currentValidation.setType("THEN");
        currentValidation = currentValidation.initialise();
        return super.visitThen_clause(ctx);
    }

    @Override
    public Object visitElse_clause(ValidationParser.Else_clauseContext ctx) {
        currentValidation.setType("ELSE");
        currentValidation = currentValidation.initialise();
        return super.visitElse_clause(ctx);
    }

    @Override
    public Object visitEndif(ValidationParser.EndifContext ctx) {
        currentValidation.setType("ENDIF");
        currentValidation = currentValidation.initialise();
        return super.visitEndif(ctx);
    }

    @Override
    public Object visitSet_directive(ValidationParser.Set_directiveContext ctx) {
        try {
            Validation v = instantiateValidator(ctx.SET().getText());
            v.setType(ctx.set_type().getText());
            v.setResource(ctx.VARIABLE().getText());
            v.setData(ctx.ANNOTATION_TEXT().getText());
            currentValidation = v.initialise(bootProperties);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, ValidationGrammarCompilerVisiter.class.getName(), "visitSet_directive " + getLocus(ctx) + "\r\n" + ex.getMessage());
        }
        return super.visitSet_directive(ctx);
    }

    @Override
    public Object visitCheck_directive(ValidationParser.Check_directiveContext ctx) {
        try {
            Validation v = instantiateValidator(ctx.CHECK().getText());
            if (ctx.SUB() != null) {
                v.setType(ctx.SUB().getText());
                v.setResource(ctx.sub_name().getText());
                // data ?
            } else if (ctx.test_statement() != null) {
                handleTestStatement(ctx.test_statement(), v);
            }
            currentValidation = v.initialise(bootProperties);
        } catch (Exception ex) {
            Logger.getInstance().log(SEVERE, ValidationGrammarCompilerVisiter.class.getName(), "visitCheck_directive " + getLocus(ctx) + "\r\n" + ex.getMessage());
        }
        return super.visitCheck_directive(ctx);
    }

    /**
     * allow addition of extra error listeners to parsing process
     *
     * @param customErrorListeners the customErrorListeners to set
     */
    public void setCustomErrorListeners(BaseErrorListener[] customErrorListeners) {
        this.customErrorListeners = customErrorListeners;
    }

    /**
     * allow addition of a single extra error listener to parsing process
     *
     * @param errorListener
     */
    public void setCustomErrorListener(BaseErrorListener errorListener) {
        setCustomErrorListeners(new BaseErrorListener[]{errorListener});
    }

}
