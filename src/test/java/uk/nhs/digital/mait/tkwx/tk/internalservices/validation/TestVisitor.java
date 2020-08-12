/*
 Copyright 2015  Simon Farrow <simon.farrow1@hscic.gov.uk>

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

import java.io.IOException;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationLexer;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParserBaseVisitor;
import uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser.ValidationParser.InputContext;

/**
 * parses a given file and allows us to access parser context objects typically
 * the first instance of each is captured NB Not currently used
 *
 * @author simonfarrow
 */
public class TestVisitor extends ValidationParserBaseVisitor {

    private InputContext start;

    /**
     * public constructor defaults to src/test/resources/validator.conf
     */
    public TestVisitor() throws IOException {
        this("src/test/resources/validator.conf");
    }

    /**
     * public constructor taking a file name
     *
     * @param fileName
     */
    public TestVisitor(String fileName) throws IOException {
        ValidationLexer lexer
                = new ValidationLexer(new ANTLRFileStream(fileName));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        ValidationParser parser = new ValidationParser(tokens);

        start = parser.input();

        init();
    }

    private void init() {
        visit(start);
    }

//    @Override
//    public Object visitInput(ValidationParser.InputContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitValidation_header(ValidationParser.Validation_headerContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitValidate_statement(ValidationParser.Validate_statementContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitValidate_directives(ValidationParser.Validate_directivesContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitValidate_directive(ValidationParser.Validate_directiveContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitCheck_directive(ValidationParser.Check_directiveContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSet_directive(ValidationParser.Set_directiveContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitIf_directive(ValidationParser.If_directiveContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitTest_statement(ValidationParser.Test_statementContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSchema_test(ValidationParser.Schema_testContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSchema_type(ValidationParser.Schema_typeContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSchema_path(ValidationParser.Schema_pathContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSchema_xpath(ValidationParser.Schema_xpathContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitNo_arg_test(ValidationParser.No_arg_testContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_one_arg_test(ValidationParser.Xpath_one_arg_testContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_one_arg_type(ValidationParser.Xpath_one_arg_typeContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_arg(ValidationParser.Xpath_argContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_two_arg_test(ValidationParser.Xpath_two_arg_testContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_two_arg_type(ValidationParser.Xpath_two_arg_typeContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_two_arg_invalid_test(ValidationParser.Xpath_two_arg_invalid_testContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_multi_arg_test(ValidationParser.Xpath_multi_arg_testContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitXpath_multi_arg_type(ValidationParser.Xpath_multi_arg_typeContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitAnnotation_directive(ValidationParser.Annotation_directiveContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubset_statement(ValidationParser.Subset_statementContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitSubset_name(ValidationParser.Subset_nameContext ctx) {
//        return visitChildren(ctx);
//    }
//
//    @Override
//    public Object visitInclude_statement(ValidationParser.Include_statementContext ctx) {
//        return visitChildren(ctx);
//    }
}
