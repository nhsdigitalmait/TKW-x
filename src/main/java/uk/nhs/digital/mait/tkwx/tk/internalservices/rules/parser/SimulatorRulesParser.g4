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
// $Id: SimulatorRulesParser.g4 144 2020-01-23 15:28:57Z sfarrow $

parser grammar SimulatorRulesParser;

options {
    language = Java;
    tokenVocab = SimulatorRulesLexer;
}

@header {
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser;
}

input: block* EOF ; 

/*   overall high level structure of a simulator rules config file */
block:   responses_block
	| substitutions_block    
	| expressions_block
	| simrules
;

//------------------------------------------------------------------------------

xpath_arg : XPATH | PATH | REGEXP | INTEGER | IDENTIFIER | URL | CST | VARIABLE_NAME ;

responses_block: BEGIN_RESPONSES responses END_RESPONSES ;
substitutions_block: BEGIN_SUBSTITUTIONS substitutions END_SUBSTITUTIONS ;
expressions_block: BEGIN_EXPRESSIONS expressions END_EXPRESSIONS ;
simrules: simrule_block+ ; // simrule rather than rule since it causes a clash (Antlr keyword?)

//------------------------------------------------------------------------------
// responses
responses : response* EOF? ; 
// docs say that reason is not optional but code suggests otherwise
response: ( response_name  response_url  ( reason_code | reason_phrase ) ? response_action ? ) httpheaderresponse ? variable_assignment ? | include_statement ; 
response_name : IDENTIFIER ;
response_url : NONE | DOT_SEPARATED_IDENTIFIER | URI | PATH ; // response template
reason_code : INTEGER ;
reason_phrase : QUOTED_STRING ;
response_action : IDENTIFIER | PATH | URL | INTEGER ; // asynchronous action
httpheaderresponse : WITH_HTTP_HEADERS LPAREN httpheader+ RPAREN ;
httpheader: http_header_name COLON http_header_value;
http_header_value : QUOTED_STRING ;
variable_assignment : VARIABLE_NAME EQUALS ? QUOTED_STRING ? ;

//------------------------------------------------------------------------------
// substitutions
substitutions : substitution*  EOF? ; // second clause for Include
substitution: ( substitution_tag  ( substitution_no_arg | substitution_xpath | substitution_literal | substitution_property | substitution_class | substitution_regexp ) ) | include_statement ; 
substitution_tag : IDENTIFIER ;

property_file_name : PATH ;
property_name : DOT_SEPARATED_IDENTIFIER | IDENTIFIER ;
substitution_no_arg : UUID_UPPER | UUID_LOWER | HL7_DATETIME | ISO8601_DATETIME | RFC822_DATETIME;
// NB this switches to CST Mode 1st param is optional xml match source last is xpath
substitution_xpath : SUBSTITUTION_XPATH  xpath_arg xpath_arg ? ;  
substitution_regexp_cardinality : FIRST | ALL ;
regexp : QUOTED_STRING QUOTED_STRING substitution_regexp_cardinality ? ;
substitution_regexp : SUBSTITUTION_REGEXP  text_match_source?  regexp + ;
substitution_literal : LITERAL ANNOTATION_TEXT ;
// defaults to System properties if no file name provided
// where > 1 property file value is set in list order
substitution_property : PROPERTY property_file_name * property_name  ;

// Any such class must implement the SubstitutionValue interface
// which has the following signature 
// public void setData(String s) throws Exception;  // called on rules file parsing
// public String getValue(String o) throws Exception; // called when substitution is invoked
// multiple parameters must be surrounded by *one* set of double quotes
substitution_class : CLASS  DOT_SEPARATED_IDENTIFIER  ( IDENTIFIER | QUOTED_STRING )? ;

//------------------------------------------------------------------------------
// expressions
expressions : expression* EOF? ;
expression:  ( expression_name  ( expression_no_arg  | expression_one_arg | expression_two_arg | expression_xpath_compare | expression_class ) ) | include_statement ; 

expression_name : IDENTIFIER ;

expression_no_arg : ALWAYS | NEVER ;

expression_one_arg : ( 
        ( xml_match_source ? xml_match_type ) |
        ( text_match_source? match_type ) // match source defaults to content
    ) xpath_arg
;

// extension mechanism for expressions for text
// class must implement ExpressionValue interface which has one method taking one String array of extracted fields and one String array parameter for optional other arguments
// The quoted strings are used as regexp from (needs at least one bracketed capture group) and replace (replace is typically "$1") parameters

data_source : text_match_source | xml_match_source ;
class_reg_exp_from : QUOTED_STRING ;
class_reg_exp_replace : QUOTED_STRING ;
class_extracted_value : data_source class_reg_exp_from class_reg_exp_replace ;
class_args : QUOTED_STRING * ;
expression_class : CLASS DOT_SEPARATED_IDENTIFIER class_extracted_value + class_args ;

xml_match_type : XPATHEXISTS | XPATHNOTEXISTS ;
match_type : MATCHES | NOTMATCHES | CONTAINS | NOTCONTAINS ;

// default match source is content NB which is a change for restful rules which used to default to context path
text_match_source : ( CONTEXT_PATH | CONTENT | JWT_PAYLOAD | JWT_HEADER | MESH_CTL | MESH_DAT | VARIABLE_NAME ) | ( HTTP_HEADER http_header_name )  ;
xml_match_source : CONTENT | JWT_PAYLOAD | JWT_HEADER | MESH_CTL | MESH_DAT ;
http_header_name : IDENTIFIER ;
xslt_file : PATH ;

expression_two_arg : xml_match_source? (
    ( ( XPATHMATCHES | XPATHNOTMATCHES | XPATHEQUALS | XPATHNOTEQUALS )  xpath_arg xpath_arg ) |
    ( ( XPATHIN | XPATHNOTIN )  xpath_arg xpath_arg+ ) |
    ( SCHEMA PATH xpath_arg ? ) |
    ( XSLT xslt_file xpath_arg )
)
;

expression_xpath_compare : (xml_match_source xml_match_source? )? 
    ( XPATHCOMPARE | XPATHNOTCOMPARE )  xpath_arg xpath_arg 
;

//------------------------------------------------------------------------------
// rules
simrule_block: BEGIN_RULE match_rule rule_lines END_RULE ;
match_rule :  IDENTIFIER | REGEXP | PATH | INTEGER;

rule_lines : rule_line + ;
rule_line : if_statement | include_statement ;
            
if_statement : IF?  if_expression  THEN?  true_response+  ( ELSE?  false_response+ ) ?  ;
if_expression :     LPAREN if_expression RPAREN |
                    NOT if_expression |
                    if_expression AND if_expression |
                    if_expression OR if_expression |
                    expression_name ;
true_response : response_name | NEXT ;
false_response : response_name | NEXT ;

//------------------------------------------------------------------------------

include_statement : INCLUDE PATH ;

//------------------------------------------------------------------------------
