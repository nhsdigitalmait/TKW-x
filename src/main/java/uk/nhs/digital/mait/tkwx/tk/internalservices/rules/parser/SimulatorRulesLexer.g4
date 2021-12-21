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


lexer grammar SimulatorRulesLexer;

options {
    language = Java;
}

// context sensitive lexer behaviour - 
// if in a set directive takes everything after the variable as the ANNOTATION_TEXT token value

@header {
package uk.nhs.digital.mait.tkwx.tk.internalservices.rules.parser;
}

@lexer::members {
     private final static boolean DEBUG = false;
}

/* --------------------------------------------------------------------------------------------------- */
// Lexer token definitions

COMMENT : '#' ~[\r\n]* ( ('\r'? '\n')+ | EOF ) -> channel(HIDDEN) ; // discard the comments
NL : '\r'? '\n' -> channel(HIDDEN); 
// If we redirect spaces to HIDDEN rather than skip we can get back the whole line including spaces
// by referencing $text
SPACES : [ \t]+ -> channel(HIDDEN) ;

ESCAPED_QUOTE : '\\"'  ;
// remove the surrounding quotes and unescape escaped quotes
QUOTED_STRING : '"' ( ESCAPED_QUOTE | ~["] )*? '"'  { setText(getText().replaceFirst("^\"(.*)\"$", "$1").replaceAll("\\\\\"","\"")); } ;

fragment BEGIN_TOKEN : 'BEGIN'  ;
fragment END_TOKEN : 'END'  ;
fragment DIGIT : [0-9] ;
fragment ALPHA : [a-zA-Z] ;

// for case insensive tokens
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');


LPAREN : '(' ;
RPAREN : ')' ;
COLON : ':' ;

INTEGER : '-'? DIGIT+  ; 

DOT : '.'  ; 

// rules (case insensitive)
IF : I F ;
THEN : T H E N ;
ELSE : E L S E ;

INCLUDE : 'INCLUDE' ;

NONE : 'NONE' ;

// Substitution types - case sensitive
UUID_UPPER : 'UUID' ;  
UUID_LOWER : 'uuid' ;  
HL7_DATETIME : 'HL7datetime' ;  
ISO8601_DATETIME : 'ISO8601datetime' ;  
RFC822_DATETIME : 'RFC822datetime' ;  
SUBSTITUTION_XPATH : X P A T H -> mode(CST_MODE) ;  
SUBSTITUTION_JSONPATH : J S O N P A T H -> mode(CST_MODE) ;  
SUBSTITUTION_REGEXP : R E G '_' E X P ;  
LITERAL : 'Literal'  -> mode(ANNOTATION_MODE) ;  
PROPERTY : 'Property' ;  
CLASS : 'Class' ; 

// Expression types (case insensitive)
XPATHEQUALS : X P A T H E Q U A L S -> mode(CST_MODE);  
XPATHNOTEQUALS : X P A T H N O T E Q U A L S -> mode(CST_MODE);  

XSLT : X S L T ;  

CONTAINS : C O N T A I N S -> mode(CST_MODE);  
NOTCONTAINS : N O T C O N T A I N S -> mode(CST_MODE);  

ALWAYS : A L W A Y S ;  
NEVER : N E V E R ;  

XPATHEXISTS : X P A T H E X I S T S  -> mode(CST_MODE);  
XPATHNOTEXISTS : X P A T H N O T E X I S T S   -> mode(CST_MODE) ;  

XPATHCOMPARE : X P A T H C O M P A R E -> mode(CST_MODE);  
XPATHNOTCOMPARE : X P A T H N O T C O M P A R E -> mode(CST_MODE);  

XPATHIN : X P A T H I N -> mode(CST_MODE); 
XPATHNOTIN : X P A T H N O T I N -> mode(CST_MODE); 

XPATHMATCHES : X P A T H M A T C H E S -> mode(CST_MODE); 
XPATHNOTMATCHES : X P A T H N O T M A T C H E S -> mode(CST_MODE); 

SCHEMA : S C H E M A ; 
MATCHES : M A T C H E S ; 
NOTMATCHES : N O T M A T C H E S ; 

// match sources
CONTEXT_PATH : C O N T E X T '_' P A T H ; // text only
CONTENT : C O N T E N T ; // xml and text
HTTP_HEADER : H T T P '_' H E A D E R ; // text only
JWT_HEADER : J W T '_' H E A D E R ; // xml and text
JWT_PAYLOAD : J W T '_' P A Y L O A D ; // xml and text
MESH_CTL : M E S H '_' C T L; // xml and text
MESH_DAT : M E S H '_' D A T; // xml and text

FIRST : F I R S T ;
ALL : A L L ;

//------------------------------------------------------------------------------
// one liners

PLUS : '+' ;
NEXT :  N E X T ;
AND : A N D ;
OR : O R ;
NOT : N O T ;
EQUALS : '=' ;
fragment DOLLAR : '$' ;

WITH_HTTP_HEADERS : W I T H '_' H T T P '_' H E A D E R S ;

//------------------------------------------------------------------------------
// Begin end blocks

fragment RESPONSES : 'RESPONSES'  ;
fragment SUBSTITUTIONS : 'SUBSTITUTIONS'  ;
fragment EXPRESSIONS : 'EXPRESSIONS'  ; 
fragment RULE : 'RULE'  ;

//------------------------------------------------------------------------------

BEGIN_RESPONSES : BEGIN_TOKEN SPACES RESPONSES ;
END_RESPONSES : END_TOKEN SPACES RESPONSES ;

BEGIN_SUBSTITUTIONS : BEGIN_TOKEN SPACES SUBSTITUTIONS ;
END_SUBSTITUTIONS : END_TOKEN SPACES SUBSTITUTIONS ;

BEGIN_EXPRESSIONS : BEGIN_TOKEN SPACES EXPRESSIONS ;
END_EXPRESSIONS : END_TOKEN SPACES EXPRESSIONS ;

BEGIN_RULE : BEGIN_TOKEN SPACES RULE ;
END_RULE : END_TOKEN SPACES RULE ;

//------------------------------------------------------------------------------

VARIABLE_NAME : DOLLAR IDENTIFIER ;

// adding the colon means we can include urns as IDENTIFIER s
IDENTIFIER : ( ALPHA | DIGIT | [_-]  | ':' | '%' ) +   ; 

// adds unix file path separator and period
fragment EXTENDED_IDENTIFIER : IDENTIFIER | DOT | [/]  ;

DOT_SEPARATED_IDENTIFIER : IDENTIFIER ( DOT IDENTIFIER )* ;

fragment PROTOCOL : 'http' | 'https' | 'spine' ;
URL : PROTOCOL '://' EXTENDED_IDENTIFIER+ ;

URI : ( ALPHA | DIGIT | [_-]  | ':' | '%' | '?' | EQUALS | DOT ) +  ;

/* Generally a file path but used elsewhere handles optional dos volume prefix */
PATH : (ALPHA ':')? ( EXTENDED_IDENTIFIER | '\\' ) +   ; 

// we are currently constrained to xpath expressions which do not contain spaces
XPATH : ( EXTENDED_IDENTIFIER | [@'()\[\]*=\"] | LPAREN | RPAREN  ) +   ;

// added & as a valid regexp character
REGEXP :  ( XPATH | '^' | '$' | '\\' | '{' | '}' | '?' | '|' | ',' | PLUS | '&' ) + ;

//  take everything up to the next linefeed
mode ANNOTATION_MODE ;
// Strips leading whitespace before returning the token value
ANNOTATION_TEXT : ( ~ ('\r' | '\n' ) ) + {setText(getText().replaceFirst("^[\t ]*","")); 
                                          if (DEBUG) System.out.println("ANNOTATION_TEXT ="+getText());
                                          } -> mode(DEFAULT_MODE) ;

//------------------------------------------------------------------------------
// This mode is analagous to the ConfigurationStringTokenizer which honours matching brackets and quotes
mode CST_MODE ;
SP : [ \t]+ -> channel(HIDDEN);
CST : (
      ( ( NOSPACESORDELIMS* (  '(' CSTORSPACE ')' | '[' CSTORSPACE ']' | '\'' ~[\']* '\'' | '"' ~["]* '"' ) + NOSPACESORDELIMS* ) + | 
       NOSPACESORDELIMS+ 
       ) {if ( DEBUG ) System.out.println("Returning ["+getText()+"]"); } 
     ) ;
fragment CSTORSPACE : ( CST | ' ' | '\\' )* ; // Bodge to trap \
fragment NOSPACESORDELIMS : ~[\[\"'(\]) \t\r\n] | '\\' ; // Bodge to trap \

LF :  ('\r' | '\n')+ -> channel(HIDDEN), mode(DEFAULT_MODE) ;

//------------------------------------------------------------------------------
