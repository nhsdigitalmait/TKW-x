# TKW-x

NHS Digital Toolkit Workbench. Supersedes the ITK workbench and sunsets legacy protocols. Positioning for Micro Services/Distributed Execution environments.

Original TKW documentation is avalable at [ITK Documentation](https://digital.nhs.uk/services/interoperability-toolkit/developer-resources/itk-test-centre/itk-testbench)

While detailed and accurate the documentation is somewhat old. This page summarises extensions and enhancments not covered in the original documentation.
For definitive descritpions of the three domain specific languages the user should refer to the appropriate ANTLR Lexer and Parser syntaxes.


## General
TKW-x adds support for FHIR and Restful services including:
* Migrations to ANTLR based grammars and syntaxes for the three domais specific languages
* Integration with Java HapiFhir libraries
* Access to Http headers and context paths
* Construction and testing of Java Web Tokens
* Multiple Http Verbs
* Support for [MESH Transport](https://digital.nhs.uk/services/message-exchange-for-social-care-and-health-mesh)
* Support for creation and consumption of 'on the wire' json payloads driven by http headers Content-type and Accept
* Support for compressed encodings (gzip and zip) and chunking
* Addition of two new modes	 httpInterceptor superseding simulator and autotest

## Sunsetting of legacy protocols/services
* Removal of support for SOAP based legacy transports (ITK1 and ITK2) Support for Synchronous and asynchronous Spine Messaging is retained 
* Removal of support for HL7v2 Pipe and Hat Messaging

## Validation
* Implementation of HapiFhir Validator and many other validation enhancements
* Validation of Http headers and context paths
* Declaration of and reference to variables
* Capability to write bespoke test classes via the addition of unchecked tests
* [ANTLR Lexer](https://github.com/nhsdigitalmait/TKW-x/blob/master/src/main/java/uk/nhs/digital/mait/tkwx/tk/internalservices/validation/parser/ValidationLexer.g4) and [ANTLR Parser](https://github.com/nhsdigitalmait/TKW-x/blob/master/src/main/java/uk/nhs/digital/mait/tkwx/tk/internalservices/validation/parser/ValidationParser.g4)

## Transmitter
* Statically added http header request and response values
* Construction of JWT authentication headers
* Ability to use a full set of Http methods

## Simulator
* Multiple Property file sources
* Expression classes
* Static http headers in responses
* Ability to return Http headers based on substitution tags
* [ANTLR Lexer](https://github.com/nhsdigitalmait/TKW-x/blob/master/src/main/java/uk/nhs/digital/mait/tkwx/tk/internalservices/rules/parser/SimulatorRulesLexer.g4) and [ANTLR Parser](https://github.com/nhsdigitalmait/TKW-x/blob/master/src/main/java/uk/nhs/digital/mait/tkwx/tk/internalservices/rules/parser/SimulatorRulesParser.g4)

## Autotest
* Not documented but present in the TKW5 release, allows the user to construct and send requests to provider systems and to validate and test the expected responses.
* Addition of Http header sets
* Addition of INCLUDE statments to assist with hand crafted test suites
* Substitution tags which are evaluated at test execution time
* [ANTLR Lexer](https://github.com/nhsdigitalmait/TKW-x/blob/master/src/main/java/uk/nhs/digital/mait/tkwx/tk/internalservices/testautomation/parser/AutotestLexer.g4) and [ANTLR Parser](https://github.com/nhsdigitalmait/TKW-x/blob/master/src/main/java/uk/nhs/digital/mait/tkwx/tk/internalservices/testautomation/parser/AutotestParser.g4)
