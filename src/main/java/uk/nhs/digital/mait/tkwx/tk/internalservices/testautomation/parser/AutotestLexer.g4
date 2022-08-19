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

lexer grammar AutotestLexer;

options {
    language = Java;
}

@header {
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser;
}

@lexer::members {
     private final static boolean DEBUG = true;
}


// these declarations appear in the generated parser file AutotestParser.java
@parser::members {
        // introduced from AutotestGrammar.g4
        //java.util.HashSet<String> hs = new java.util.HashSet<>();
        //private boolean isDefined(String s) {if (!hs.contains(s)) {hs.add(s); return true;} else {return false;} };
}

/* --------------------------------------------------------------------------------------------------- */
// Lexer token definitions

COMMENT : '#' ~[\r\n]* ( ('\r'? '\n')+ | EOF ) -> channel(HIDDEN) ; // discard the comments

// If we redirect spaces to HIDDEN rather than skip we can get back the whole line including spaces
// by referencing $text
SPACES : [ ]+ -> channel(HIDDEN);

ESCAPED_QUOTE : '\\"' ;
// remove the surrounding quotes and unescape escaped quotes
QUOTED_STRING : '"' ( ESCAPED_QUOTE | ~["] )*? '"' { setText(getText().replaceFirst("^\"(.*)\"$", "$1").replaceAll("\\\\\"","\"")); } ;

fragment BEGIN_TOKEN : 'BEGIN'  ;
fragment END_TOKEN : 'END'  ;
fragment DIGIT : [0-9] ;
fragment ALPHA : [a-zA-Z] ;

LPAREN : '(' ;
RPAREN : ')' ;

TAB : '\t'  ;  // not just whitespace since necessary part of property sets
NL : '\r'? '\n' ; 

INTEGER : '-'? DIGIT+   ; 

DOT : '.'  ; 
COMMA : ',' ;

INCLUDE : 'INCLUDE' ;

//------------------------------------------------------------------------------
// one liners

SCRIPT : 'SCRIPT'  ;
SIMULATOR : 'SIMULATOR'  ;
VALIDATOR : 'VALIDATOR'  ;
STOP_WHEN_COMPLETE : 'STOP' SPACES 'WHEN' SPACES 'COMPLETE'  ;

//------------------------------------------------------------------------------
// Begin end blocks

fragment MESSAGES : 'MESSAGES'  ;
fragment TEMPLATES : 'TEMPLATES'  ;
fragment SCHEDULES : 'SCHEDULES'  ; 
fragment DATASOURCES : 'DATASOURCES'  ;
fragment EXTRACTORS : 'EXTRACTORS'  ;
TESTS : 'TESTS'  ;
fragment PASSFAIL : 'PASSFAIL'  ;
fragment PROPERTYSETS : 'PROPERTYSETS'  ;
fragment HTTPHEADERS : 'HTTPHEADERS'  ;
fragment SUBSTITUTION_TAGS : 'SUBSTITUTION_TAGS'  ;


BEGIN_SCHEDULES : BEGIN_TOKEN SPACES SCHEDULES ;
END_SCHEDULES : END_TOKEN SPACES SCHEDULES ;

BEGIN_TESTS : BEGIN_TOKEN SPACES TESTS ;
END_TESTS : END_TOKEN SPACES TESTS ;

BEGIN_MESSAGES : BEGIN_TOKEN SPACES MESSAGES ;
END_MESSAGES : END_TOKEN SPACES MESSAGES ;

BEGIN_TEMPLATES : BEGIN_TOKEN SPACES TEMPLATES ;
END_TEMPLATES : END_TOKEN SPACES TEMPLATES ;

BEGIN_PROPERTYSETS : BEGIN_TOKEN SPACES PROPERTYSETS ;
END_PROPERTYSETS : END_TOKEN SPACES PROPERTYSETS ;

BEGIN_HTTPHEADERS : BEGIN_TOKEN SPACES HTTPHEADERS ;
END_HTTPHEADERS : END_TOKEN SPACES HTTPHEADERS ;

BEGIN_DATASOURCES : BEGIN_TOKEN SPACES DATASOURCES ;
END_DATASOURCES : END_TOKEN SPACES DATASOURCES ;

BEGIN_EXTRACTORS : BEGIN_TOKEN SPACES EXTRACTORS ;
END_EXTRACTORS : END_TOKEN SPACES EXTRACTORS ;

BEGIN_PASSFAIL : BEGIN_TOKEN SPACES PASSFAIL ;
END_PASSFAIL : END_TOKEN SPACES PASSFAIL ;

BEGIN_SUBSTITUTION_TAGS : BEGIN_TOKEN SPACES SUBSTITUTION_TAGS ;
END_SUBSTITUTION_TAGS : END_TOKEN SPACES SUBSTITUTION_TAGS ;

//------------------------------------------------------------------------------

LOOP : 'LOOP' ;

FOR : 'FOR' ;

SEND_TKW : 'SEND_TKW'  ; 
SEND_RAW : 'SEND_RAW'  ; 
FUNCTION : 'FUNCTION'  ; 
PROMPT : 'PROMPT'  ; 
CHAIN : 'CHAIN'  ; 

TXTIMESTAMPOFFSET : 'TXTIMESTAMPOFFSET'  ; 
ASYNCTIMESTAMPOFFSET : 'ASYNCTIMESTAMPOFFSET'  ; 
WAIT : 'WAIT'  ;  
CORRELATIONCOUNT : 'CORRELATIONCOUNT'  ;  

TEXT : 'TEXT'  ;  
SYNC : 'SYNC'  ;  
ASYNC : 'ASYNC'  ;  
FROM : 'FROM'  ;  
TO : 'TO'  ;  
REPLYTO : 'REPLYTO'  ;  
PROFILEID : 'PROFILEID'  ;  
CORRELATOR : 'CORRELATOR'  ;  

//------------------------------------------------------------------------------
// Property sets 

WITH_PROPERTYSET : 'WITH_PROPERTYSET'  ;  
BASE : 'base'  ;  
PLUS : '+'  ;  
//------------------------------------------------------------------------------
// HttpHeader sets 

WITH_HTTPHEADERS : 'WITH_HTTPHEADERS'  ;  

// Substitution tags
LITERAL : 'Literal' ;

//------------------------------------------------------------------------------
// Transforms 

PRETRANSFORM : 'PRETRANSFORM'  ;  // which transform?
APPLYPRETRANSFORMTO : 'APPLYPRETRANSFORMTO'  ;  // where to apply?

PRESUBSTITUTE : 'PRESUBSTITUTE'  ;  // which reg exp substitution pairs?
APPLYSUBSTITUTIONTO : 'APPLYSUBSTITUTIONTO'  ;  // where to apply?

// specific transform points
DATA : 'data'  ;  

PREBASE64 : 'prebase64'  ;  
POSTBASE64 : 'postbase64'  ;  

PRECOMPRESS : 'precompress'  ;  
POSTCOMPRESS : 'postcompress'  ;  

PREDISTRIBUTIONENVELOPE : 'predistributionenvelope'  ;  
POSTDISTRIBUTIONENVELOPE : 'postdistributionenvelope'  ;  

PRESOAP : 'presoap'  ;  
POSTSOAP : 'postsoap'  ;  

FINAL : 'final' ;

//------------------------------------------------------------------------------

EXTRACTOR : 'EXTRACTOR' ;
XPATHEXTRACTOR : 'xpathextractor'  ;  

BASE64 : 'BASE64'  ;  
COMPRESS : 'COMPRESS'  ;  
SOAPWRAP : 'SOAPWRAP'  ;  
DISTRIBUTIONENVELOPEWRAP : 'DISTRIBUTIONENVELOPEWRAP'  ;  

USING : 'USING'  ;  
SOAPACTION : 'SOAPACTION'  ;  
MIMETYPE : 'MIMETYPE'  ;  
AUDITIDENTITY : 'AUDITIDENTITY'  ;  
WITH : 'WITH'  ;  
ID : 'ID'  ;  
NULL : 'NULL' ;

// data source types
FLATWRITABLETDV : 'flatwritabletdv'  ; 
CIRCULARWRITABLETDV : 'circularwritabletdv'  ; 

// Property set operators
SET : 'SET'  ; 
REMOVE : 'REMOVE'  ; 
                  
// Java Function Tests
DELAY : 'delay' ;

//------------------------------------------------------------------------------
// passfail checks

HTTPACCEPTED : 'httpaccepted'  ; 
HTTPOK : 'httpok'  ; 
HTTP500 : 'http500'  ; 

NULLRESPONSE : 'nullresponse'  ; 
NULLREQUEST : 'nullrequest'  ; 

SYNCHRONOUSXPATH : 'synchronousxpath' -> mode(CST_MODE) ; 
ASYNCHRONOUSXPATH : 'asynchronousxpath' -> mode(CST_MODE) ; 
SECONDRESPONSEXPATH : 'secondresponsexpath' -> mode(CST_MODE) ; 

ASYNCMESSAGETRACKINGIDTRACKINGIDREFSMATCH : 'asyncmessagetrackingidtrackingidrefsmatch'  ; 
ASYNCMESSAGETRACKINGIDTRACKINGIDNOMATCH : 'asyncmessagetrackingidtrackingidnomatch'  ; 
ASYNCMESSAGETIMESTAMPINFRASTRUCTURERESPONSETIMESTAMPMATCH : 'asyncmessagetimestampinfrastructureresponsetimestampmatch'  ; 

ZEROCONTENTLENGTH : 'zerocontentlength'  ; 


SECONDRESPONSESYNCTRACKINGIDSDIFFER : 'secondresponsesynctrackingidsdiffer'  ; 
SECONDRESPONSESYNCTRACKINGIDACKBY2MATCH : 'secondresponsesynctrackingidackby2match'  ; 
SECONDRESPONSESYNCTRACKINGIDACKBY3MATCH : 'secondresponsesynctrackingidackby3match'  ; 

HTTPHEADERCHECK : 'httpheadercheck' ;
HTTPSTATUSCHECK : 'httpstatuscheck' ;
HTTPHEADERCORRELATIONCHECK : 'httpheadercorrelationcheck' ;

XPATHCORRELATIONCHECK : 'xpathcorrelationcheck' -> mode(CST_MODE) ;

// new logical conjunction tests at 3.13
OR : 'or' ;
AND : 'and' ;
NOT : 'not' ;
IMPLIES : 'implies' ;

// Xpath tests
EXISTS : 'exists'  ; 
DOESNOTEXIST : 'doesnotexist'  ;
CHECK : 'check' ;

MATCHES : 'matches'  ; 
DOESNOTMATCH : 'doesnotmatch'  ; 
IN : 'in'  ; 

fragment NOT_SPACE : ~[ ] ;

// currently 0  .. 999 but should be 0..255
fragment OCTET : DIGIT ( DIGIT DIGIT? )? ;

// allow for substitution tags in tstp files, NOT_SPACE is for contiguous trailing parts of the url
TAG_URL : '__' ( 'FROM' | 'TO' ) '_' ( 'URL' | 'ENDPOINT' ) '__' NOT_SPACE*  ;

SUBSTITUTION_TAG : '__' [A-Z0-9_]+ '__' ;

// NB the order of tokens is important here more specific come earlier otherwise text may match to a less specific token type
IPV4 : OCTET DOT OCTET DOT OCTET DOT OCTET ;

/* fix for redmine 2125 allow brackets in test names and redmine 2136 for colons */
IDENTIFIER : ( ALPHA | DIGIT | [_-] | LPAREN | RPAREN | ':' )+   ; 

DOT_SEPARATED_IDENTIFIER : IDENTIFIER ( DOT IDENTIFIER )* ;

fragment PROTOCOL : 'http' | 'https' | 'spine' ;
URL : PROTOCOL '://' EXTENDED_IDENTIFIER + ;

/* Generally a file path but used elsewhere handles optional dos volume prefix */
PATH : (ALPHA ':')? EXTENDED_IDENTIFIER+  ; 

// adds unix file path separator and period and ? for get url queries and $ for fhir urls
// its doubtful if | should be allowed as a valid url character but NRLS seem to be using them as is
// For other dubious characters the url can now be surrounded by double quotes
// apparently + is a valid character interpretetd as space but adding this breaks + separated strings eg for transforms:w

fragment EXTENDED_IDENTIFIER : IDENTIFIER | DOT | [/] | '?' | '$' | '%' | ':' | '-' | '=' | '&' | '|' | ',' ;


//------------------------------------------------------------------------------
// This mode is analagous to the ConfigurationStringTokenizer which honours matching brackets and quotes
mode CST_MODE ;
SP : [ \t]+ -> channel(HIDDEN);
CST : (
      ( ( NOSPACESORDELIMS* (  '(' CSTORSPACE ')' | '[' CSTORSPACE ']' | '\'' ~[\']* '\'' | '"' ~["]* '"' ) + NOSPACESORDELIMS* ) + | 
       NOSPACESORDELIMS+ 
       )
     ) -> mode(DEFAULT_MODE); // unlike the other two lexers this is a "one shot" and returns to default mode after one token
fragment CSTORSPACE : ( CST | ' ' | '\\' )* ; // Bodge to trap \
fragment NOSPACESORDELIMS : ~[\[\"'(\]) \t\r\n] | '\\' ; // Bodge to trap \

LF :  ('\r' | '\n')+ -> channel(HIDDEN), mode(DEFAULT_MODE) ;

//------------------------------------------------------------------------------
