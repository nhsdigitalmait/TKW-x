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
// $Id: ValidationLexer.g4 144 2020-01-23 15:28:57Z sfarrow $

lexer grammar ValidationLexer;

options {
    language = Java;
}

@header {
package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser;
}


// context sensitive lexer behaviour - 
@lexer::members {
     private final static boolean DEBUG = false;
     private boolean isInSetDirective = false;
}

/* --------------------------------------------------------------------------------------------------- */
// Lexer token definitions

COMMENT : '#' ~[\r\n]* ( ('\r'? '\n')+ | EOF ) -> skip ; // discard the comments
NL : '\r'? '\n' -> channel(HIDDEN);

fragment DIGIT : [0-9] ;
fragment ALPHA : [a-zA-Z] ;

// for case insensitive tokens
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

fragment LPAREN : '(' ;
fragment RPAREN : ')' ;


INTEGER : '-'? DIGIT+  ; 

DOT : '.'  ; 

//------------------------------------------------------------------------------

// rules (case insensitive)
IF : I F ;
THEN : T H E N ;
ELSE : E L S E ;
// TO DO inconsistent use of this keyword?
ENDIF : E N D [\t  ]* IF ;

//------------------------------------------------------------------------------

INCLUDE : 'INCLUDE' ;

NONE : 'NONE' ;

// qualifiers for the set directive
LITERAL : 'literal' ;  
XPATH_ : X P A T H ;  

fragment HL7 : H L '7_' ; 
fragment EBXML : E B X M L '_' ; 
fragment SOAP : S O A P '_' ; 
fragment NOT : N O T ;
fragment COMPARE : C O M P A R E ;
fragment IGNORE_CASE : I G N O R E C A S E;
fragment IN : I N ;
fragment CDA :  C D A ;
fragment CONFORMANCE : C O N F O R M A N C E ;
fragment EXISTS : E X I S T S ;


SUB : S U B ;

// Validator check types (case insensitive)
// These mostly relate to class names in the tkw.property file prefixed with tks.validator.check
// So maybe they should not be case insensitive ?

//------------------------------------------------------------------------------

ALWAYS : A L W A Y S ;  
NEVER : N E V E R ;  

//------------------------------------------------------------------------------
// schema
SCHEMA : S C H E M A ; 
CONFORMANCE_SCHEMA : CONFORMANCE SCHEMA ;
CDA_CONFORMANCE_SCHEMA : CDA CONFORMANCE SCHEMA ;

//------------------------------------------------------------------------------
// no arg
SIGNATURE : S I G N A T U R E ;
CDA_RENDERER : CDA R E N D E R E R ;
CDA_TEMPLATE_LIST : CDA T E M P L A T E L I S T ;
HAPIFHIRVALIDATOR : H A P I F H I R V A L I D A T O R ;
FHIRRESOURCEVALIDATOR : F H I R R E S O U R C E V A L I D A T O R ;
TERMINOLOGYVALIDATOR : T E R M I N O L O G Y V A L I D A T O R ;

//------------------------------------------------------------------------------
// one arg
XPATHEXISTS : XPATH_ E X I S T S -> mode(CST_MODE);  
XPATHNOTEXISTS : XPATH_ N O T E X I S T S -> mode(CST_MODE);  

HL7_XPATHEXISTS : HL7 XPATHEXISTS -> mode(CST_MODE);  
HL7_XPATHNOTEXISTS : HL7 XPATHNOTEXISTS -> mode(CST_MODE);  

SOAP_XPATHEXISTS : SOAP XPATHEXISTS -> mode(CST_MODE);  
SOAP_XPATHNOTEXISTS : SOAP XPATHNOTEXISTS -> mode(CST_MODE);  

EBXML_XPATHEXISTS : EBXML XPATHEXISTS -> mode(CST_MODE);  
EBXML_XPATHNOTEXISTS : EBXML XPATHNOTEXISTS -> mode(CST_MODE);  

EQUALS : E Q U A L S -> mode(CST_MODE) ;
NOTEQUALS : NOT EQUALS -> mode(CST_MODE); 

MATCHES : M A T C H E S -> mode(CST_MODE) ;
NOTMATCHES : NOT MATCHES -> mode(CST_MODE); 

CONTAINS : C O N T A I N S -> mode(CST_MODE);  
NOTCONTAINS : N O T C O N T A I N S -> mode(CST_MODE);  

//------------------------------------------------------------------------------

// two arg
XPATHEQUALS : XPATH_ EQUALS -> mode(CST_MODE);  
XPATHNOTEQUALS : XPATH_ NOT EQUALS  -> mode(CST_MODE);  

HL7_XPATHEQUALS : HL7 XPATHEQUALS -> mode(CST_MODE) ;  
HL7_XPATHNOTEQUALS : HL7 XPATHNOTEQUALS  -> mode(CST_MODE);  

EBXML_XPATHEQUALS : EBXML XPATHEQUALS  -> mode(CST_MODE);  
EBXML_XPATHNOTEQUALS : EBXML XPATHNOTEQUALS  -> mode(CST_MODE);  

SOAP_XPATHEQUALS : SOAP XPATHEQUALS  -> mode(CST_MODE);  
SOAP_XPATHNOTEQUALS : SOAP XPATHNOTEQUALS  -> mode(CST_MODE);  

// this pair added for spine validation
XPATHEQUALSIGNORECASE : X P A T H E Q U A L S IGNORE_CASE  -> mode(CST_MODE);  
XPATHNOTEQUALSIGNORECASE : X P A T H N O T E Q U A L S IGNORE_CASE  -> mode(CST_MODE);  

XPATHMATCHES : XPATH_ MATCHES  -> mode(CST_MODE); 
XPATHNOTMATCHES : XPATH_ NOT MATCHES  -> mode(CST_MODE); 

HL7_XPATHMATCHES : HL7 XPATHMATCHES  -> mode(CST_MODE); 
HL7_XPATHNOTMATCHES : HL7 XPATHNOTMATCHES  -> mode(CST_MODE); 

XPATHCOMPARE : XPATH_ COMPARE  -> mode(CST_MODE);  

XPATHCONTAINS : XPATH_ CONTAINS  -> mode(CST_MODE);  
XPATHNOTCONTAINS : XPATH_ NOT CONTAINS  -> mode(CST_MODE);  

// this pair added for spine validation
XPATHCONTAINSIGNORECASE : XPATH_ CONTAINS  IGNORE_CASE -> mode(CST_MODE);  
XPATHNOTCONTAINSIGNORECASE : XPATH_ NOT CONTAINS  IGNORE_CASE  -> mode(CST_MODE);  

XSLT : X S L T  -> mode(CST_MODE);  
HL7_XSLT : HL7 XSLT  -> mode(CST_MODE);  
EBXML_XSLT : EBXML XSLT  -> mode(CST_MODE);  

CDA_CONFORMANCE_XSLT : CDA C O N F O R M A N C E  XSLT  -> mode(CST_MODE);

// mark as additional not syntax checked
UNCHECKED : U N C H E C K E D -> mode(CST_MODE) ;

// match types
CONTEXT_PATH : C O N T E X T '_' P A T H ; // text only
CONTENT : C O N T E N T ;               // xml and text
HTTP_HEADER : H T T P '_' H E A D E R ;  // text only
JWT_PAYLOAD : J W T '_' P A Y L O A D ; // xml and text

//------------------------------------------------------------------------------

// multi arg
XPATHIN : XPATH_ IN  -> mode(CST_MODE); 
//XPATHNOTIN : XPATH_ NOT IN   -> mode(CST_MODE) ;  

//------------------------------------------------------------------------------
// headers

VALIDATION_RULESET_NAME : 'VALIDATION-RULESET-NAME' -> mode(ANNOTATION_MODE) ;
VALIDATION_RULESET_VERSION : 'VALIDATION-RULESET-VERSION'  -> mode(ANNOTATION_MODE) ;
VALIDATION_RULESET_TIMESTAMP : 'VALIDATION-RULESET-TIMESTAMP'  -> mode(ANNOTATION_MODE) ;
VALIDATION_RULESET_AUTHOR : 'VALIDATION-RULESET-AUTHOR' -> mode(ANNOTATION_MODE) ;

// Validation

VALIDATE : 'VALIDATE' ;
SET : 'SET' { isInSetDirective = true; } ;
CHECK : 'CHECK' ;
// take everything up to the end of line
ANNOTATION : 'ANNOTATION' -> mode(ANNOTATION_MODE) ;
SUBSET : 'SUBSET' ;

DOLLAR : '$' ;

//------------------------------------------------------------------------------

// adding the colon means we can include urns as IDENTIFIER s
// beware the long minus added (also added % and - for validation)
IDENTIFIER : ( ALPHA | DIGIT | [_-] | '–' | ':' | LPAREN | RPAREN | '%' | '-' ) +  {if (DEBUG) System.out.println("IDENTIFIER ="+getText());} ; 

// if the variable is referenced in a set literal context then get all the text up to end of line 
VARIABLE : DOLLAR ( IDENTIFIER | PATH | XPATH ) 
           {if (DEBUG) System.out.println( "VARIABLE =" + getText()+" is ="+isInSetDirective);} 
           {isInSetDirective}? -> mode(ANNOTATION_MODE) ;

// adds unix file path separator and period
fragment EXTENDED_IDENTIFIER : IDENTIFIER | DOT | [/] ;

DOT_SEPARATED_IDENTIFIER : IDENTIFIER ( DOT IDENTIFIER )* ;

fragment PROTOCOL : 'http' | 'https' | 'spine' ;
URL : PROTOCOL '://' EXTENDED_IDENTIFIER+ ;

/* Generally a file path but used elsewhere handles optional dos volume prefix */
PATH : (ALPHA ':')? ( EXTENDED_IDENTIFIER | '\\' ) +  ; 

// we are currently constrained to xpath expressions which do not contain spaces
XPATH : ( EXTENDED_IDENTIFIER | [@'()\[\]*=\"#] | LPAREN | RPAREN  ) +  ;

// If we redirect spaces to HIDDEN rather than skip we can get back the whole line including spaces
// by referencing $text
SPACES : [ \t]+ -> channel(HIDDEN) ;

DEFAULT : . ;

//------------------------------------------------------------------------------

//  take everything up to the next linefeed
mode ANNOTATION_MODE ;
// Strips leading whitespace before returning the token value
ANNOTATION_TEXT : ( ~ ('\r' | '\n' ) ) + {setText(getText().replaceFirst("^[\t ]*","")); 
                                          if (isInSetDirective) isInSetDirective = false;
                                          if (DEBUG) System.out.println("ANNOTATION_TEXT ="+getText());
                                          } -> mode(DEFAULT_MODE) ;

//------------------------------------------------------------------------------
// This mode is analagous to the ConfigurationStringTokenizer which honours matching brackets and quotes
mode CST_MODE ;
SP : [ \t]+ -> channel(HIDDEN) ;
CST : (
      ( ( NOSPACESORDELIMS* (  '(' CSTORSPACE ')' | '[' CSTORSPACE ']' | '\'' ~[\']* '\'' | '"' ~["]* '"' ) + NOSPACESORDELIMS* ) + | 
       NOSPACESORDELIMS+ 
       ) {if ( DEBUG ) System.out.println("Returning ["+getText()+"]"); } 
     ) ;
fragment CSTORSPACE : ( CST | ' ' | '\\' )*  ; // Bodge to trap \
fragment NOSPACESORDELIMS : ~[\["'(\]) \t\r\n] | '\\' ; // Bodge to trap \

LF :  ('\r' | '\n')+ -> channel(HIDDEN), mode(DEFAULT_MODE) ;

//------------------------------------------------------------------------------
