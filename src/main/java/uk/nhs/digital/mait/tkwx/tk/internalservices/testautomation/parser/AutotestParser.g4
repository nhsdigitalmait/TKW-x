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

parser grammar AutotestParser;

options {
    language = Java;
    tokenVocab = AutotestLexer ;
}

@header {
package uk.nhs.digital.mait.tkwx.tk.internalservices.testautomation.parser;
}


// these declarations appear in the generated parser file AutotestParser.java
@parser::members {
        // introduced from AutotestGrammar.g4
        //java.util.HashSet<String> hs = new java.util.HashSet<>();
        //private boolean isDefined(String s) {if (!hs.contains(s)) {hs.add(s); return true;} else {return false;} };
}

/* --------------------------------------------------------------------------------------------------- */
// Parser - here be syntax

// NB using antlr newlines are not a necessary part of the syntax but we force the requirement so that the
// TKWATM tst merge (which does not use antlr) will work.
input: (line? NL+ )* EOF ; 

/*   overall high level structure of a tst file */
/*   each of these should occur only once within any one file (apart from include) but it's not easy to achieve specifying that in BNF best left to a listener/visitor */
line:   script
	| validator    
	| simulator
	| stop_when_complete
    | schedules 
	| tests 
	| messages  
	| templates 
	| propertysets 
	| httpheaders 
	| datasources 
	| extractors  
	| passfails 
	| include_statement 
	| substitution_tags
;

//------------------------------------------------------------------------------

// this is "magic" and uses the rule element option form The only valid option is 'fail'
// see p 294 of antlr book
//cardinality[String item] : {isDefined($item)}? <fail={"multiple declaration of "+$item+" element"}>  ;
// eg use as script : SCRIPT cardinality[$text] scriptName ;
// For info only, we don't use the above in the g4 file since we want the parser to be as generic as possible

script : SCRIPT scriptName ;
validator: VALIDATOR PATH ;
simulator: SIMULATOR PATH ;
stop_when_complete: STOP_WHEN_COMPLETE ;
schedules: BEGIN_SCHEDULES NL+ schedule* END_SCHEDULES ;
tests: BEGIN_TESTS NL+ test* END_TESTS ;
messages: BEGIN_MESSAGES NL+ message*  END_MESSAGES ;
templates: BEGIN_TEMPLATES NL+ template* END_TEMPLATES ;
propertysets: BEGIN_PROPERTYSETS NL+ namedPropertySet* END_PROPERTYSETS ;
httpheaders: BEGIN_HTTPHEADERS NL+ namedHttpHeaderSet* END_HTTPHEADERS ;
datasources: BEGIN_DATASOURCES NL+ datasource*  END_DATASOURCES ;
extractors: BEGIN_EXTRACTORS NL+ extractor* END_EXTRACTORS ;
passfails: BEGIN_PASSFAIL NL+ passfail* END_PASSFAIL ;
substitution_tags: BEGIN_SUBSTITUTION_TAGS NL+ substitution_tag* END_SUBSTITUTION_TAGS ;

include_statement : INCLUDE PATH ;

//------------------------------------------------------------------------------

scriptName : IDENTIFIER | DOT_SEPARATED_IDENTIFIER ;

//------------------------------------------------------------------------------
// schedules

schedule: scheduleName TESTS ( testName+ | ( LOOP LPAREN testName+ RPAREN ( FOR INTEGER )? )  ) NL+ ; 
scheduleName : IDENTIFIER | DOT_SEPARATED_IDENTIFIER ;

//------------------------------------------------------------------------------
// tests

test: ( 
        testName sendType messageName testArg+ 
|       testName ASYNC passFailCheckName PROMPT QUOTED_STRING testArg+
|       testName CHAIN testSynchronicity passFailCheckName testArg* 
        // Extensible by addition of new property named for function class tks.autotest.testfunction.<functionName>
|       testName FUNCTION testFunctionName functionArg* 
      ) NL+
;

testName : IDENTIFIER | DOT_SEPARATED_IDENTIFIER | PATH ; 
testSynchronicity : SYNC | ASYNC ;
sendType : SEND_TKW | SEND_RAW ;

testArg : testArgPair
| preTransform
| preSubstitute
| preTransformPoints
| preSubstitutionPoints
;

// These are the FUNCTION Tests not the property set/ http header java functions
// FUNCTION Tests must implement the TestFunction interface
testFunctionName : DELAY ; // currently the only defined function is delay

functionArg : IDENTIFIER | INTEGER | PATH | QUOTED_STRING | SUBSTITUTION_TAG ;   

testArgPair : testIntArg INTEGER
| testStringArg testString
| testSynchronicity passFailCheckName
| testURLArg testURL
| testPropertySet
| testHttpHeaderSet
;

testString : IDENTIFIER | PATH | QUOTED_STRING ;
testURL : URL | TAG_URL | QUOTED_STRING ;

testIntArg : TXTIMESTAMPOFFSET
| ASYNCTIMESTAMPOFFSET 
| WAIT
| CORRELATIONCOUNT 
;

testStringArg : TEXT
| PROFILEID 
| CORRELATOR
;

testURLArg : TO 
| FROM 
| REPLYTO 
;

testPropertySet : WITH_PROPERTYSET withPropertySet ;

withPropertySet : ( BASE | propertySetName ) ( PLUS propertySetName ) *
;

testHttpHeaderSet : WITH_HTTPHEADERS withHttpHeaderSet ;

withHttpHeaderSet : httpHeaderSetName ( PLUS httpHeaderSetName ) *
;

// these must either come as a pair or not at all
preTransform : PRETRANSFORM plusDelimPaths ;
preTransformPoints : APPLYPRETRANSFORMTO plusDelimTransformPoints ;

// these must either come as a pair or not at all
preSubstitute : PRESUBSTITUTE substPairs ;
preSubstitutionPoints : APPLYSUBSTITUTIONTO plusDelimTransformPoints ;

// these lists should have the same number of elements for transforms and substitutions
plusDelimPaths : PATH ( PLUS PATH )* ;
substPairs : substPair ( PLUS substPair )* ;
plusDelimTransformPoints : transformPoint ( PLUS transformPoint )* ;

substPair : matchRegexp COMMA substituteRegexp ;
matchRegexp : QUOTED_STRING ;
substituteRegexp : QUOTED_STRING ;

transformPoint : DATA 
| PREBASE64 
| POSTBASE64 
| PRECOMPRESS 
| POSTCOMPRESS 
| PREDISTRIBUTIONENVELOPE 
| POSTDISTRIBUTIONENVELOPE 
| PRESOAP 
| POSTSOAP 
| FINAL
;

//------------------------------------------------------------------------------
// messages

message: messageName messageArg+ NL+ ;
messageName : IDENTIFIER | DOT_SEPARATED_IDENTIFIER ;
messageArg : messageArgSingle  | usingTemplate | withDatasource | messageArgPair;

messageArgSingle : BASE64 
| COMPRESS 
| SOAPWRAP 
| DISTRIBUTIONENVELOPEWRAP 
;

usingTemplate : USING templateName ;

withDatasource : WITH datasourceName ;

messageArgPair : messageStringArg messageString ;

messageStringArg : SOAPACTION 
| MIMETYPE 
| AUDITIDENTITY 
| ID 
;

messageString : IDENTIFIER | PATH | SUBSTITUTION_TAG ;
//------------------------------------------------------------------------------
// templates

template : templateName PATH NL+ ;
templateName :  IDENTIFIER | PATH | DOT_SEPARATED_IDENTIFIER ; // we need to match for several here, PATH is less specific than IDENTIFIER

//------------------------------------------------------------------------------
// propertysets

namedPropertySet : propertySetName NL+ propertySetDirective+ ;

propertySetName : IDENTIFIER ;

propertySetDirective : TAB+ ( // DOT_SEPARATED_IDENTIFIER traps function:
   SET propertyName psArg
 | REMOVE propertyName 
   ) NL+ 
;

propertyName : DOT_SEPARATED_IDENTIFIER ;

// java propertyset functions must be static taking 0..n String parameters and returning a String
// format is SET <propertyName> function:<class.>+method arg1 arg2 etc
psFunctionName :  DOT_SEPARATED_IDENTIFIER ;
psArg : psValue  | ( psFunctionName functionArg* ) ;
psValue : QUOTED_STRING | IDENTIFIER | PATH | INTEGER | IPV4 | SUBSTITUTION_TAG ;

//------------------------------------------------------------------------------
// httpHeaderSets
// java httpheaderset functions must be static taking 0..n String parameters and returning a String
// format is <httpheadername> function:class.method arg1 arg2 etc

namedHttpHeaderSet : httpHeaderSetName NL+ httpHeaderSetDirective+ ;

httpHeaderSetName : IDENTIFIER ;

httpHeaderSetDirective : TAB+
   httpHeaderName psArg 
   NL+ 
;

httpHeaderName : IDENTIFIER;

//------------------------------------------------------------------------------
// datasources

datasource: datasourceName datasourceType PATH NL+ ; 
datasourceName : IDENTIFIER | NULL ;
// Extensible by addition of new property named for datasourcetype property tks.autotest.datasource.<datasourceType>
datasourceType : CIRCULARWRITABLETDV | FLATWRITABLETDV ;

//------------------------------------------------------------------------------
// extractors
    
extractor: extractorName extractorType PATH NL+ ;
extractorName : IDENTIFIER ;
// Extensible by addition of new property named for extractor property tks.autotest.extractor.<extractorType>
extractorType : XPATHEXTRACTOR ;

//------------------------------------------------------------------------------
// passfails
// NB after mods to this revisit ScriptParser.makePassFail, AutotestGrammarCompilerVisitor.makePassFail and also AbstractPassFailCheck.init
// to see if theres an impact
passfail :  passFailCheckName passFailCheck NL+ ;

passFailCheckName : IDENTIFIER | DOT_SEPARATED_IDENTIFIER ;

passFailCheck :  ( // all these are zero argument passfails
                   // Extensible by addition of new property named for passfail class tks.autotest.passfail.<passFailCheck>
                  
                   // http level checks
                   HTTPACCEPTED | // 202
                   HTTPOK | // 200
                   HTTP500 | 
                   ZEROCONTENTLENGTH  | 
                   
                   // async distribution envelope tracking id checks
                   ASYNCMESSAGETRACKINGIDTRACKINGIDREFSMATCH | 
                   ASYNCMESSAGETRACKINGIDTRACKINGIDNOMATCH | 
                   
                   // async distribution envelope timestamp checks
                   ASYNCMESSAGETIMESTAMPINFRASTRUCTURERESPONSETIMESTAMPMATCH | 
                   
                   // various second response (ie bus/infrastructure ack)  distribution envelope tracking id checks
                   SECONDRESPONSESYNCTRACKINGIDSDIFFER | 
                   SECONDRESPONSESYNCTRACKINGIDACKBY2MATCH | 
                   SECONDRESPONSESYNCTRACKINGIDACKBY3MATCH  
                 )
| xPathCheck
| httpHeaderCheck
| httpHeaderCorrelationCheck
| httpStatusCheck
| nullCheck
| xpathCorrelationCheck
                   
/* logical conjunctions */
// NB Current TKW parser implementation will not cope with fully recursive nested expressions: only one level permitted
| ( AND | OR ) bracketedPassfail bracketedPassfail+                  
| NOT bracketedPassfail          
| IMPLIES bracketedPassfail bracketedPassfail                   
;

bracketedPassfail : LPAREN passFailCheck RPAREN ;

httpStatusCheck : HTTPSTATUSCHECK INTEGER ;

xPathCheck : xpathType xpathExpression xpathArg usingExtractor? ;

xpathType : SYNCHRONOUSXPATH
| ASYNCHRONOUSXPATH
| SECONDRESPONSEXPATH
;

xpathExpression : CST ;

xpathArg : xpathTypeNoArg | ( xpathTypeArg matchString ) ;

xpathTypeNoArg : EXISTS 
| DOESNOTEXIST 
| CHECK
;

xpathTypeArg : MATCHES 
| DOESNOTMATCH 
| IN 
;

httpHeaderCheck : HTTPHEADERCHECK httpHeaderName xpathArg ;

httpHeaderCorrelationCheck : HTTPHEADERCORRELATIONCHECK httpHeaderName httpHeaderName ;

// Compares synchronous req resp evaluated xpaths if no second parameter then its the same xpath
xpathCorrelationCheck : XPATHCORRELATIONCHECK xpathExpression xpathExpression ? ;

nullCheck : nullCheckType matchString ;

nullCheckType : NULLREQUEST 
| NULLRESPONSE ;

matchString : QUOTED_STRING | SUBSTITUTION_TAG ;
 
usingExtractor : EXTRACTOR extractorName ;

// These are to be evaluated at Test run time, not substituted on tstp merge. Used for eg time critical tests
// Currently the substitutions are only applied to fromUrl and toUrl attributes of Test
substitution_tag : SUBSTITUTION_TAG ( psArg | (LITERAL QUOTED_STRING ) ) NL+ ;
//------------------------------------------------------------------------------
