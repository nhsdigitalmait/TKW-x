/*
 Copyright 2012-13  Simon Farrow <simon.farrow1@nhs.net>

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

parser grammar ValidationParser;

options {
    language = Java ;
    // needs the -lib option on the runtime for this to work in build.xml
    // -lib ${src.dir}/org/warlock/tk/internalservices/validation/parser
    tokenVocab = ValidationLexer;
}

@header {
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser;
}

@parser::members {
     private final static boolean DEBUG = false ;
}

/* --------------------------------------------------------------------------------------------------- */

/*   overall high level structure of a validation file */
input: ( validation_header | validate_statement | subset_statement | validate_directive ) + EOF ; 

//------------------------------------------------------------------------------

validation_header : validation_header_type ANNOTATION_TEXT ;

validation_header_type : VALIDATION_RULESET_NAME | 
       VALIDATION_RULESET_VERSION | 
       VALIDATION_RULESET_TIMESTAMP | 
       VALIDATION_RULESET_AUTHOR 
;

//------------------------------------------------------------------------------

validate_statement : VALIDATE service_name validate_directives ;
service_name : IDENTIFIER | DOT_SEPARATED_IDENTIFIER | URL | INTEGER;

validate_directives : validate_directive* ;

validate_directive : check_directive |
                     set_directive |
                     if_directive |
                     annotation_directive |
                     include_statement
;


//  TODO > 1 parm on check sub ?
check_directive : CHECK ( (SUB sub_name ) | test_statement ) ;
sub_name : ( IDENTIFIER | PATH | XPATH ) +  ;
//------------------------------------------------------------------------------

set_directive : SET set_type VARIABLE ANNOTATION_TEXT  
{ if ( DEBUG ) System.out.println("Variable " + $VARIABLE.text + " set to " + $ANNOTATION_TEXT.text);} 
;

set_type : LITERAL | XPATH_ ;
//------------------------------------------------------------------------------

if_directive : IF test_statement THEN then_clause ( ELSE else_clause ) ? endif ;
then_clause : validate_directives ;
else_clause : validate_directives ;
endif : ENDIF ; // This looks redundant but the client code needs to know explicitly when we get to endif

// test statement
test_statement : no_arg_test | 
                 schema_test | 
                 xpath_one_arg_test | 
                 xpath_two_arg_test | 
                 xpath_multi_arg_test |
                 jsonpath_one_arg_test | 
                 jsonpath_two_arg_test | 
                 jsonpath_multi_arg_test |
                 unchecked_test
;

// handles additional validations added later which do not have syntax checking
// mandatory type but everything else is optional
unchecked_test : UNCHECKED unchecked_test_name xpath_arg xpath_arg * ;
unchecked_test_name : CST ;

// The first parameter for test_statement is used as a lookup into the tkw.properties
// file for a property named tks.validation.check.<testtype>
// The returned class name is then instantiated by reflection calls
//
// eg tks.validator.check.sub	uk.nhs.digital.mait.tkwx.tk.internalservices.validation.SubroutineCheck
// check classes must implement the ValidationCheck interface

schema_test : schema_type schema_path schema_xpath ? ;
//                        DN is this used?
//                       <---------------->
schema_type : SCHEMA | /* CONFORMANCE_SCHEMA | */ CDA_CONFORMANCE_SCHEMA ;
schema_path : PATH ;
schema_xpath : PATH | XPATH ;

// just the type required here
hapifhirvalidator_id : IDENTIFIER | DOT_SEPARATED_IDENTIFIER | INTEGER ;
no_arg_test : SIGNATURE | CDA_RENDERER | CDA_TEMPLATE_LIST | ( HAPIFHIRVALIDATOR hapifhirvalidator_id ? ) | FHIRRESOURCEVALIDATOR | TERMINOLOGYVALIDATOR;
// default xml source is content
xml_match_source : CONTENT| JWT_HEADER | JWT_PAYLOAD ;

xpath_one_arg_test : xpath_one_arg_type CST ;
xpath_one_arg_comparison_type: XPATHEXISTS | XPATHNOTEXISTS ;
xpath_one_arg_type : ( xml_match_source? xpath_one_arg_comparison_type ) | 
                     ( HL7_XPATHEXISTS | HL7_XPATHNOTEXISTS | 
                     SOAP_XPATHEXISTS | SOAP_XPATHNOTEXISTS  | 
                     EBXML_XPATHEXISTS | EBXML_XPATHNOTEXISTS ) |
                     ( text_match_source? text_match_type )
;

json_match_source : CONTENT | JWT_HEADER_JSON | JWT_PAYLOAD_JSON ;
jsonpath_one_arg_test : jsonpath_one_arg_type CST ;
jsonpath_one_arg_comparison_type: JSONPATHEXISTS | JSONPATHNOTEXISTS ;
jsonpath_one_arg_type : ( json_match_source? jsonpath_one_arg_comparison_type ) | 
                     ( text_match_source? text_match_type )
;
text_match_type : MATCHES | NOTMATCHES | CONTAINS | NOTCONTAINS | EQUALS | NOTEQUALS ;

// default match source is content
text_match_source : ( CONTEXT_PATH | CONTENT | JWT_PAYLOAD ) | ( HTTP_HEADER http_header_name ) ;
http_header_name : IDENTIFIER ;

xpath_arg : CST ;

xpath_two_arg_comparison_type : XPATHEQUALS | XPATHNOTEQUALS | 
                     XPATHEQUALSIGNORECASE | XPATHNOTEQUALSIGNORECASE | 
                     XPATHMATCHES | XPATHNOTMATCHES | 
                     XPATHCOMPARE | XPATHNOTCOMPARE |
                     XPATHCONTAINS | XPATHNOTCONTAINS |
                     XPATHCONTAINSIGNORECASE | XPATHNOTCONTAINSIGNORECASE |
                     XSLT ;

xpath_two_arg_test : xpath_two_arg_type xpath_arg xpath_arg + ;
xpath_two_arg_type : (  xml_match_source? xpath_two_arg_comparison_type ) | 
                     ( HL7_XPATHEQUALS | HL7_XPATHNOTEQUALS | 
                     HL7_XPATHMATCHES | HL7_XPATHNOTMATCHES |
                     HL7_XSLT | EBXML_XSLT |
                     EBXML_XPATHEQUALS | EBXML_XPATHNOTEQUALS | SOAP_XPATHEQUALS | SOAP_XPATHNOTEQUALS |
                     CDA_CONFORMANCE_XSLT)
;

xpath_multi_arg_test : xml_match_source? xpath_multi_arg_type  xpath_arg xpath_arg+;

// There is no "not in" test why not?
xpath_multi_arg_type : xml_match_source? XPATHIN /* | XPATHNOTIN */ ;

//------------------------------------------------------------------------------
jsonpath_arg : CST ;

jsonpath_two_arg_comparison_type : JSONPATHEQUALS | JSONPATHNOTEQUALS | 
                     JSONPATHEQUALSIGNORECASE | JSONPATHNOTEQUALSIGNORECASE | 
                     JSONPATHMATCHES | JSONPATHNOTMATCHES | 
                     JSONPATHCOMPARE | JSONPATHNOTCOMPARE |
                     JSONPATHCONTAINS | JSONPATHNOTCONTAINS |
                     JSONPATHCONTAINSIGNORECASE | JSONPATHNOTCONTAINSIGNORECASE;

jsonpath_two_arg_test : jsonpath_two_arg_type jsonpath_arg jsonpath_arg + ;
jsonpath_two_arg_type : (  json_match_source? jsonpath_two_arg_comparison_type ) 
;

jsonpath_multi_arg_test : json_match_source? jsonpath_multi_arg_type  jsonpath_arg jsonpath_arg+;

// There is no "not in" test why not?
jsonpath_multi_arg_type : json_match_source? JSONPATHIN /* | JSONPATHNOTIN */ ;

//------------------------------------------------------------------------------

annotation_directive : ANNOTATION ANNOTATION_TEXT ;

//------------------------------------------------------------------------------

subset_statement : SUBSET subset_name validate_directives;
subset_name : IDENTIFIER ;

//------------------------------------------------------------------------------

include_statement : INCLUDE  PATH ;

//------------------------------------------------------------------------------
