// Generated from ValidationParser.g4 by ANTLR 4.5.3

package uk.nhs.digital.mait.tkwx.tk.internalservices.validation.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ValidationParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, NL=2, INTEGER=3, DOT=4, IF=5, THEN=6, ELSE=7, ENDIF=8, INCLUDE=9, 
		NONE=10, LITERAL=11, XPATH_=12, SUB=13, ALWAYS=14, NEVER=15, SCHEMA=16, 
		CONFORMANCE_SCHEMA=17, CDA_CONFORMANCE_SCHEMA=18, SIGNATURE=19, CDA_RENDERER=20, 
		CDA_TEMPLATE_LIST=21, HAPIFHIRVALIDATOR=22, FHIRRESOURCEVALIDATOR=23, 
		TERMINOLOGYVALIDATOR=24, XPATHEXISTS=25, XPATHNOTEXISTS=26, HL7_XPATHEXISTS=27, 
		HL7_XPATHNOTEXISTS=28, SOAP_XPATHEXISTS=29, SOAP_XPATHNOTEXISTS=30, EBXML_XPATHEXISTS=31, 
		EBXML_XPATHNOTEXISTS=32, EQUALS=33, NOTEQUALS=34, MATCHES=35, NOTMATCHES=36, 
		CONTAINS=37, NOTCONTAINS=38, XPATHEQUALS=39, XPATHNOTEQUALS=40, HL7_XPATHEQUALS=41, 
		HL7_XPATHNOTEQUALS=42, EBXML_XPATHEQUALS=43, EBXML_XPATHNOTEQUALS=44, 
		SOAP_XPATHEQUALS=45, SOAP_XPATHNOTEQUALS=46, XPATHEQUALSIGNORECASE=47, 
		XPATHNOTEQUALSIGNORECASE=48, XPATHMATCHES=49, XPATHNOTMATCHES=50, HL7_XPATHMATCHES=51, 
		HL7_XPATHNOTMATCHES=52, XPATHCOMPARE=53, XPATHCONTAINS=54, XPATHNOTCONTAINS=55, 
		XPATHCONTAINSIGNORECASE=56, XPATHNOTCONTAINSIGNORECASE=57, XSLT=58, HL7_XSLT=59, 
		EBXML_XSLT=60, CDA_CONFORMANCE_XSLT=61, UNCHECKED=62, CONTEXT_PATH=63, 
		CONTENT=64, HTTP_HEADER=65, JWT_PAYLOAD=66, XPATHIN=67, VALIDATION_RULESET_NAME=68, 
		VALIDATION_RULESET_VERSION=69, VALIDATION_RULESET_TIMESTAMP=70, VALIDATION_RULESET_AUTHOR=71, 
		VALIDATE=72, SET=73, CHECK=74, ANNOTATION=75, SUBSET=76, DOLLAR=77, IDENTIFIER=78, 
		VARIABLE=79, DOT_SEPARATED_IDENTIFIER=80, URL=81, PATH=82, XPATH=83, SPACES=84, 
		DEFAULT=85, ANNOTATION_TEXT=86, SP=87, CST=88, LF=89, JSONPATHEXISTS=90, 
		JSONPATHNOTEXISTS=91, JSONPATHEQUALS=92, JSONPATHNOTEQUALS=93, JSONPATHEQUALSIGNORECASE=94, 
		JSONPATHNOTEQUALSIGNORECASE=95, JSONPATHMATCHES=96, JSONPATHNOTMATCHES=97, 
		JSONPATHCOMPARE=98, JSONPATHCONTAINS=99, JSONPATHNOTCONTAINS=100, JSONPATHCONTAINSIGNORECASE=101, 
		JSONPATHNOTCONTAINSIGNORECASE=102, JSONPATHIN=103;
	public static final int
		RULE_input = 0, RULE_validation_header = 1, RULE_validation_header_type = 2, 
		RULE_validate_statement = 3, RULE_service_name = 4, RULE_validate_directives = 5, 
		RULE_validate_directive = 6, RULE_check_directive = 7, RULE_sub_name = 8, 
		RULE_set_directive = 9, RULE_set_type = 10, RULE_if_directive = 11, RULE_then_clause = 12, 
		RULE_else_clause = 13, RULE_endif = 14, RULE_test_statement = 15, RULE_unchecked_test = 16, 
		RULE_unchecked_test_name = 17, RULE_schema_test = 18, RULE_schema_type = 19, 
		RULE_schema_path = 20, RULE_schema_xpath = 21, RULE_hapifhirvalidator_id = 22, 
		RULE_no_arg_test = 23, RULE_xml_match_source = 24, RULE_xpath_one_arg_test = 25, 
		RULE_xpath_one_arg_comparison_type = 26, RULE_xpath_one_arg_type = 27, 
		RULE_json_match_source = 28, RULE_jsonpath_one_arg_test = 29, RULE_jsonpath_one_arg_comparison_type = 30, 
		RULE_jsonpath_one_arg_type = 31, RULE_text_match_type = 32, RULE_text_match_source = 33, 
		RULE_http_header_name = 34, RULE_xpath_arg = 35, RULE_xpath_two_arg_comparison_type = 36, 
		RULE_xpath_two_arg_test = 37, RULE_xpath_two_arg_type = 38, RULE_xpath_multi_arg_test = 39, 
		RULE_xpath_multi_arg_type = 40, RULE_jsonpath_arg = 41, RULE_jsonpath_two_arg_comparison_type = 42, 
		RULE_jsonpath_two_arg_test = 43, RULE_jsonpath_two_arg_type = 44, RULE_jsonpath_multi_arg_test = 45, 
		RULE_jsonpath_multi_arg_type = 46, RULE_annotation_directive = 47, RULE_subset_statement = 48, 
		RULE_subset_name = 49, RULE_include_statement = 50;
	public static final String[] ruleNames = {
		"input", "validation_header", "validation_header_type", "validate_statement", 
		"service_name", "validate_directives", "validate_directive", "check_directive", 
		"sub_name", "set_directive", "set_type", "if_directive", "then_clause", 
		"else_clause", "endif", "test_statement", "unchecked_test", "unchecked_test_name", 
		"schema_test", "schema_type", "schema_path", "schema_xpath", "hapifhirvalidator_id", 
		"no_arg_test", "xml_match_source", "xpath_one_arg_test", "xpath_one_arg_comparison_type", 
		"xpath_one_arg_type", "json_match_source", "jsonpath_one_arg_test", "jsonpath_one_arg_comparison_type", 
		"jsonpath_one_arg_type", "text_match_type", "text_match_source", "http_header_name", 
		"xpath_arg", "xpath_two_arg_comparison_type", "xpath_two_arg_test", "xpath_two_arg_type", 
		"xpath_multi_arg_test", "xpath_multi_arg_type", "jsonpath_arg", "jsonpath_two_arg_comparison_type", 
		"jsonpath_two_arg_test", "jsonpath_two_arg_type", "jsonpath_multi_arg_test", 
		"jsonpath_multi_arg_type", "annotation_directive", "subset_statement", 
		"subset_name", "include_statement"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, "'.'", null, null, null, null, "'INCLUDE'", "'NONE'", 
		"'literal'", null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "'VALIDATION-RULESET-NAME'", 
		"'VALIDATION-RULESET-VERSION'", "'VALIDATION-RULESET-TIMESTAMP'", "'VALIDATION-RULESET-AUTHOR'", 
		"'VALIDATE'", "'SET'", "'CHECK'", "'ANNOTATION'", "'SUBSET'", "'$'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "COMMENT", "NL", "INTEGER", "DOT", "IF", "THEN", "ELSE", "ENDIF", 
		"INCLUDE", "NONE", "LITERAL", "XPATH_", "SUB", "ALWAYS", "NEVER", "SCHEMA", 
		"CONFORMANCE_SCHEMA", "CDA_CONFORMANCE_SCHEMA", "SIGNATURE", "CDA_RENDERER", 
		"CDA_TEMPLATE_LIST", "HAPIFHIRVALIDATOR", "FHIRRESOURCEVALIDATOR", "TERMINOLOGYVALIDATOR", 
		"XPATHEXISTS", "XPATHNOTEXISTS", "HL7_XPATHEXISTS", "HL7_XPATHNOTEXISTS", 
		"SOAP_XPATHEXISTS", "SOAP_XPATHNOTEXISTS", "EBXML_XPATHEXISTS", "EBXML_XPATHNOTEXISTS", 
		"EQUALS", "NOTEQUALS", "MATCHES", "NOTMATCHES", "CONTAINS", "NOTCONTAINS", 
		"XPATHEQUALS", "XPATHNOTEQUALS", "HL7_XPATHEQUALS", "HL7_XPATHNOTEQUALS", 
		"EBXML_XPATHEQUALS", "EBXML_XPATHNOTEQUALS", "SOAP_XPATHEQUALS", "SOAP_XPATHNOTEQUALS", 
		"XPATHEQUALSIGNORECASE", "XPATHNOTEQUALSIGNORECASE", "XPATHMATCHES", "XPATHNOTMATCHES", 
		"HL7_XPATHMATCHES", "HL7_XPATHNOTMATCHES", "XPATHCOMPARE", "XPATHCONTAINS", 
		"XPATHNOTCONTAINS", "XPATHCONTAINSIGNORECASE", "XPATHNOTCONTAINSIGNORECASE", 
		"XSLT", "HL7_XSLT", "EBXML_XSLT", "CDA_CONFORMANCE_XSLT", "UNCHECKED", 
		"CONTEXT_PATH", "CONTENT", "HTTP_HEADER", "JWT_PAYLOAD", "XPATHIN", "VALIDATION_RULESET_NAME", 
		"VALIDATION_RULESET_VERSION", "VALIDATION_RULESET_TIMESTAMP", "VALIDATION_RULESET_AUTHOR", 
		"VALIDATE", "SET", "CHECK", "ANNOTATION", "SUBSET", "DOLLAR", "IDENTIFIER", 
		"VARIABLE", "DOT_SEPARATED_IDENTIFIER", "URL", "PATH", "XPATH", "SPACES", 
		"DEFAULT", "ANNOTATION_TEXT", "SP", "CST", "LF", "JSONPATHEXISTS", "JSONPATHNOTEXISTS", 
		"JSONPATHEQUALS", "JSONPATHNOTEQUALS", "JSONPATHEQUALSIGNORECASE", "JSONPATHNOTEQUALSIGNORECASE", 
		"JSONPATHMATCHES", "JSONPATHNOTMATCHES", "JSONPATHCOMPARE", "JSONPATHCONTAINS", 
		"JSONPATHNOTCONTAINS", "JSONPATHCONTAINSIGNORECASE", "JSONPATHNOTCONTAINSIGNORECASE", 
		"JSONPATHIN"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ValidationParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	     private final static boolean DEBUG = false ;

	public ValidationParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class InputContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ValidationParser.EOF, 0); }
		public List<Validation_headerContext> validation_header() {
			return getRuleContexts(Validation_headerContext.class);
		}
		public Validation_headerContext validation_header(int i) {
			return getRuleContext(Validation_headerContext.class,i);
		}
		public List<Validate_statementContext> validate_statement() {
			return getRuleContexts(Validate_statementContext.class);
		}
		public Validate_statementContext validate_statement(int i) {
			return getRuleContext(Validate_statementContext.class,i);
		}
		public List<Subset_statementContext> subset_statement() {
			return getRuleContexts(Subset_statementContext.class);
		}
		public Subset_statementContext subset_statement(int i) {
			return getRuleContext(Subset_statementContext.class,i);
		}
		public List<Validate_directiveContext> validate_directive() {
			return getRuleContexts(Validate_directiveContext.class);
		}
		public Validate_directiveContext validate_directive(int i) {
			return getRuleContext(Validate_directiveContext.class,i);
		}
		public InputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitInput(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputContext input() throws RecognitionException {
		InputContext _localctx = new InputContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(106);
				switch (_input.LA(1)) {
				case VALIDATION_RULESET_NAME:
				case VALIDATION_RULESET_VERSION:
				case VALIDATION_RULESET_TIMESTAMP:
				case VALIDATION_RULESET_AUTHOR:
					{
					setState(102);
					validation_header();
					}
					break;
				case VALIDATE:
					{
					setState(103);
					validate_statement();
					}
					break;
				case SUBSET:
					{
					setState(104);
					subset_statement();
					}
					break;
				case IF:
				case INCLUDE:
				case SET:
				case CHECK:
				case ANNOTATION:
					{
					setState(105);
					validate_directive();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(108); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IF || _la==INCLUDE || ((((_la - 68)) & ~0x3f) == 0 && ((1L << (_la - 68)) & ((1L << (VALIDATION_RULESET_NAME - 68)) | (1L << (VALIDATION_RULESET_VERSION - 68)) | (1L << (VALIDATION_RULESET_TIMESTAMP - 68)) | (1L << (VALIDATION_RULESET_AUTHOR - 68)) | (1L << (VALIDATE - 68)) | (1L << (SET - 68)) | (1L << (CHECK - 68)) | (1L << (ANNOTATION - 68)) | (1L << (SUBSET - 68)))) != 0) );
			setState(110);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Validation_headerContext extends ParserRuleContext {
		public Validation_header_typeContext validation_header_type() {
			return getRuleContext(Validation_header_typeContext.class,0);
		}
		public TerminalNode ANNOTATION_TEXT() { return getToken(ValidationParser.ANNOTATION_TEXT, 0); }
		public Validation_headerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validation_header; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitValidation_header(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Validation_headerContext validation_header() throws RecognitionException {
		Validation_headerContext _localctx = new Validation_headerContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_validation_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			validation_header_type();
			setState(113);
			match(ANNOTATION_TEXT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Validation_header_typeContext extends ParserRuleContext {
		public TerminalNode VALIDATION_RULESET_NAME() { return getToken(ValidationParser.VALIDATION_RULESET_NAME, 0); }
		public TerminalNode VALIDATION_RULESET_VERSION() { return getToken(ValidationParser.VALIDATION_RULESET_VERSION, 0); }
		public TerminalNode VALIDATION_RULESET_TIMESTAMP() { return getToken(ValidationParser.VALIDATION_RULESET_TIMESTAMP, 0); }
		public TerminalNode VALIDATION_RULESET_AUTHOR() { return getToken(ValidationParser.VALIDATION_RULESET_AUTHOR, 0); }
		public Validation_header_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validation_header_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitValidation_header_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Validation_header_typeContext validation_header_type() throws RecognitionException {
		Validation_header_typeContext _localctx = new Validation_header_typeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_validation_header_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			_la = _input.LA(1);
			if ( !(((((_la - 68)) & ~0x3f) == 0 && ((1L << (_la - 68)) & ((1L << (VALIDATION_RULESET_NAME - 68)) | (1L << (VALIDATION_RULESET_VERSION - 68)) | (1L << (VALIDATION_RULESET_TIMESTAMP - 68)) | (1L << (VALIDATION_RULESET_AUTHOR - 68)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Validate_statementContext extends ParserRuleContext {
		public TerminalNode VALIDATE() { return getToken(ValidationParser.VALIDATE, 0); }
		public Service_nameContext service_name() {
			return getRuleContext(Service_nameContext.class,0);
		}
		public Validate_directivesContext validate_directives() {
			return getRuleContext(Validate_directivesContext.class,0);
		}
		public Validate_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validate_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitValidate_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Validate_statementContext validate_statement() throws RecognitionException {
		Validate_statementContext _localctx = new Validate_statementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_validate_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			match(VALIDATE);
			setState(118);
			service_name();
			setState(119);
			validate_directives();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Service_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ValidationParser.IDENTIFIER, 0); }
		public TerminalNode DOT_SEPARATED_IDENTIFIER() { return getToken(ValidationParser.DOT_SEPARATED_IDENTIFIER, 0); }
		public TerminalNode URL() { return getToken(ValidationParser.URL, 0); }
		public TerminalNode INTEGER() { return getToken(ValidationParser.INTEGER, 0); }
		public Service_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_service_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitService_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Service_nameContext service_name() throws RecognitionException {
		Service_nameContext _localctx = new Service_nameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_service_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			_la = _input.LA(1);
			if ( !(_la==INTEGER || ((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (IDENTIFIER - 78)) | (1L << (DOT_SEPARATED_IDENTIFIER - 78)) | (1L << (URL - 78)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Validate_directivesContext extends ParserRuleContext {
		public List<Validate_directiveContext> validate_directive() {
			return getRuleContexts(Validate_directiveContext.class);
		}
		public Validate_directiveContext validate_directive(int i) {
			return getRuleContext(Validate_directiveContext.class,i);
		}
		public Validate_directivesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validate_directives; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitValidate_directives(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Validate_directivesContext validate_directives() throws RecognitionException {
		Validate_directivesContext _localctx = new Validate_directivesContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_validate_directives);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(123);
					validate_directive();
					}
					} 
				}
				setState(128);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Validate_directiveContext extends ParserRuleContext {
		public Check_directiveContext check_directive() {
			return getRuleContext(Check_directiveContext.class,0);
		}
		public Set_directiveContext set_directive() {
			return getRuleContext(Set_directiveContext.class,0);
		}
		public If_directiveContext if_directive() {
			return getRuleContext(If_directiveContext.class,0);
		}
		public Annotation_directiveContext annotation_directive() {
			return getRuleContext(Annotation_directiveContext.class,0);
		}
		public Include_statementContext include_statement() {
			return getRuleContext(Include_statementContext.class,0);
		}
		public Validate_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_validate_directive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitValidate_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Validate_directiveContext validate_directive() throws RecognitionException {
		Validate_directiveContext _localctx = new Validate_directiveContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_validate_directive);
		try {
			setState(134);
			switch (_input.LA(1)) {
			case CHECK:
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				check_directive();
				}
				break;
			case SET:
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				set_directive();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(131);
				if_directive();
				}
				break;
			case ANNOTATION:
				enterOuterAlt(_localctx, 4);
				{
				setState(132);
				annotation_directive();
				}
				break;
			case INCLUDE:
				enterOuterAlt(_localctx, 5);
				{
				setState(133);
				include_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Check_directiveContext extends ParserRuleContext {
		public TerminalNode CHECK() { return getToken(ValidationParser.CHECK, 0); }
		public Test_statementContext test_statement() {
			return getRuleContext(Test_statementContext.class,0);
		}
		public TerminalNode SUB() { return getToken(ValidationParser.SUB, 0); }
		public Sub_nameContext sub_name() {
			return getRuleContext(Sub_nameContext.class,0);
		}
		public Check_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_check_directive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitCheck_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Check_directiveContext check_directive() throws RecognitionException {
		Check_directiveContext _localctx = new Check_directiveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_check_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(CHECK);
			setState(140);
			switch (_input.LA(1)) {
			case SUB:
				{
				{
				setState(137);
				match(SUB);
				setState(138);
				sub_name();
				}
				}
				break;
			case SCHEMA:
			case CDA_CONFORMANCE_SCHEMA:
			case SIGNATURE:
			case CDA_RENDERER:
			case CDA_TEMPLATE_LIST:
			case HAPIFHIRVALIDATOR:
			case FHIRRESOURCEVALIDATOR:
			case TERMINOLOGYVALIDATOR:
			case XPATHEXISTS:
			case XPATHNOTEXISTS:
			case HL7_XPATHEXISTS:
			case HL7_XPATHNOTEXISTS:
			case SOAP_XPATHEXISTS:
			case SOAP_XPATHNOTEXISTS:
			case EBXML_XPATHEXISTS:
			case EBXML_XPATHNOTEXISTS:
			case EQUALS:
			case NOTEQUALS:
			case MATCHES:
			case NOTMATCHES:
			case CONTAINS:
			case NOTCONTAINS:
			case XPATHEQUALS:
			case XPATHNOTEQUALS:
			case HL7_XPATHEQUALS:
			case HL7_XPATHNOTEQUALS:
			case EBXML_XPATHEQUALS:
			case EBXML_XPATHNOTEQUALS:
			case SOAP_XPATHEQUALS:
			case SOAP_XPATHNOTEQUALS:
			case XPATHEQUALSIGNORECASE:
			case XPATHNOTEQUALSIGNORECASE:
			case XPATHMATCHES:
			case XPATHNOTMATCHES:
			case HL7_XPATHMATCHES:
			case HL7_XPATHNOTMATCHES:
			case XPATHCOMPARE:
			case XPATHCONTAINS:
			case XPATHNOTCONTAINS:
			case XPATHCONTAINSIGNORECASE:
			case XPATHNOTCONTAINSIGNORECASE:
			case XSLT:
			case HL7_XSLT:
			case EBXML_XSLT:
			case CDA_CONFORMANCE_XSLT:
			case UNCHECKED:
			case CONTEXT_PATH:
			case CONTENT:
			case HTTP_HEADER:
			case JWT_PAYLOAD:
			case XPATHIN:
				{
				setState(139);
				test_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sub_nameContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(ValidationParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(ValidationParser.IDENTIFIER, i);
		}
		public List<TerminalNode> PATH() { return getTokens(ValidationParser.PATH); }
		public TerminalNode PATH(int i) {
			return getToken(ValidationParser.PATH, i);
		}
		public List<TerminalNode> XPATH() { return getTokens(ValidationParser.XPATH); }
		public TerminalNode XPATH(int i) {
			return getToken(ValidationParser.XPATH, i);
		}
		public Sub_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sub_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSub_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sub_nameContext sub_name() throws RecognitionException {
		Sub_nameContext _localctx = new Sub_nameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_sub_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(142);
				_la = _input.LA(1);
				if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (IDENTIFIER - 78)) | (1L << (PATH - 78)) | (1L << (XPATH - 78)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(145); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( ((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (IDENTIFIER - 78)) | (1L << (PATH - 78)) | (1L << (XPATH - 78)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Set_directiveContext extends ParserRuleContext {
		public Token VARIABLE;
		public Token ANNOTATION_TEXT;
		public TerminalNode SET() { return getToken(ValidationParser.SET, 0); }
		public Set_typeContext set_type() {
			return getRuleContext(Set_typeContext.class,0);
		}
		public TerminalNode VARIABLE() { return getToken(ValidationParser.VARIABLE, 0); }
		public TerminalNode ANNOTATION_TEXT() { return getToken(ValidationParser.ANNOTATION_TEXT, 0); }
		public Set_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_directive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSet_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Set_directiveContext set_directive() throws RecognitionException {
		Set_directiveContext _localctx = new Set_directiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_set_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(SET);
			setState(148);
			set_type();
			setState(149);
			((Set_directiveContext)_localctx).VARIABLE = match(VARIABLE);
			setState(150);
			((Set_directiveContext)_localctx).ANNOTATION_TEXT = match(ANNOTATION_TEXT);
			 if ( DEBUG ) System.out.println("Variable " + (((Set_directiveContext)_localctx).VARIABLE!=null?((Set_directiveContext)_localctx).VARIABLE.getText():null) + " set to " + (((Set_directiveContext)_localctx).ANNOTATION_TEXT!=null?((Set_directiveContext)_localctx).ANNOTATION_TEXT.getText():null));
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Set_typeContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(ValidationParser.LITERAL, 0); }
		public TerminalNode XPATH_() { return getToken(ValidationParser.XPATH_, 0); }
		public Set_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSet_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Set_typeContext set_type() throws RecognitionException {
		Set_typeContext _localctx = new Set_typeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_set_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			_la = _input.LA(1);
			if ( !(_la==LITERAL || _la==XPATH_) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_directiveContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(ValidationParser.IF, 0); }
		public Test_statementContext test_statement() {
			return getRuleContext(Test_statementContext.class,0);
		}
		public TerminalNode THEN() { return getToken(ValidationParser.THEN, 0); }
		public Then_clauseContext then_clause() {
			return getRuleContext(Then_clauseContext.class,0);
		}
		public EndifContext endif() {
			return getRuleContext(EndifContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(ValidationParser.ELSE, 0); }
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public If_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_directive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitIf_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_directiveContext if_directive() throws RecognitionException {
		If_directiveContext _localctx = new If_directiveContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_if_directive);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(IF);
			setState(156);
			test_statement();
			setState(157);
			match(THEN);
			setState(158);
			then_clause();
			setState(161);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(159);
				match(ELSE);
				setState(160);
				else_clause();
				}
			}

			setState(163);
			endif();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Then_clauseContext extends ParserRuleContext {
		public Validate_directivesContext validate_directives() {
			return getRuleContext(Validate_directivesContext.class,0);
		}
		public Then_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_then_clause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitThen_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Then_clauseContext then_clause() throws RecognitionException {
		Then_clauseContext _localctx = new Then_clauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_then_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			validate_directives();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Else_clauseContext extends ParserRuleContext {
		public Validate_directivesContext validate_directives() {
			return getRuleContext(Validate_directivesContext.class,0);
		}
		public Else_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_clause; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitElse_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Else_clauseContext else_clause() throws RecognitionException {
		Else_clauseContext _localctx = new Else_clauseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_else_clause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			validate_directives();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndifContext extends ParserRuleContext {
		public TerminalNode ENDIF() { return getToken(ValidationParser.ENDIF, 0); }
		public EndifContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endif; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitEndif(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndifContext endif() throws RecognitionException {
		EndifContext _localctx = new EndifContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_endif);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(ENDIF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Test_statementContext extends ParserRuleContext {
		public No_arg_testContext no_arg_test() {
			return getRuleContext(No_arg_testContext.class,0);
		}
		public Schema_testContext schema_test() {
			return getRuleContext(Schema_testContext.class,0);
		}
		public Xpath_one_arg_testContext xpath_one_arg_test() {
			return getRuleContext(Xpath_one_arg_testContext.class,0);
		}
		public Xpath_two_arg_testContext xpath_two_arg_test() {
			return getRuleContext(Xpath_two_arg_testContext.class,0);
		}
		public Xpath_multi_arg_testContext xpath_multi_arg_test() {
			return getRuleContext(Xpath_multi_arg_testContext.class,0);
		}
		public Unchecked_testContext unchecked_test() {
			return getRuleContext(Unchecked_testContext.class,0);
		}
		public Test_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_test_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitTest_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Test_statementContext test_statement() throws RecognitionException {
		Test_statementContext _localctx = new Test_statementContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_test_statement);
		try {
			setState(177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(171);
				no_arg_test();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(172);
				schema_test();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(173);
				xpath_one_arg_test();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(174);
				xpath_two_arg_test();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(175);
				xpath_multi_arg_test();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(176);
				unchecked_test();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unchecked_testContext extends ParserRuleContext {
		public TerminalNode UNCHECKED() { return getToken(ValidationParser.UNCHECKED, 0); }
		public Unchecked_test_nameContext unchecked_test_name() {
			return getRuleContext(Unchecked_test_nameContext.class,0);
		}
		public List<Xpath_argContext> xpath_arg() {
			return getRuleContexts(Xpath_argContext.class);
		}
		public Xpath_argContext xpath_arg(int i) {
			return getRuleContext(Xpath_argContext.class,i);
		}
		public Unchecked_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unchecked_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitUnchecked_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unchecked_testContext unchecked_test() throws RecognitionException {
		Unchecked_testContext _localctx = new Unchecked_testContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_unchecked_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			match(UNCHECKED);
			setState(180);
			unchecked_test_name();
			setState(181);
			xpath_arg();
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CST) {
				{
				{
				setState(182);
				xpath_arg();
				}
				}
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unchecked_test_nameContext extends ParserRuleContext {
		public TerminalNode CST() { return getToken(ValidationParser.CST, 0); }
		public Unchecked_test_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unchecked_test_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitUnchecked_test_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unchecked_test_nameContext unchecked_test_name() throws RecognitionException {
		Unchecked_test_nameContext _localctx = new Unchecked_test_nameContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_unchecked_test_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			match(CST);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Schema_testContext extends ParserRuleContext {
		public Schema_typeContext schema_type() {
			return getRuleContext(Schema_typeContext.class,0);
		}
		public Schema_pathContext schema_path() {
			return getRuleContext(Schema_pathContext.class,0);
		}
		public Schema_xpathContext schema_xpath() {
			return getRuleContext(Schema_xpathContext.class,0);
		}
		public Schema_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSchema_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Schema_testContext schema_test() throws RecognitionException {
		Schema_testContext _localctx = new Schema_testContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_schema_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			schema_type();
			setState(191);
			schema_path();
			setState(193);
			_la = _input.LA(1);
			if (_la==PATH || _la==XPATH) {
				{
				setState(192);
				schema_xpath();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Schema_typeContext extends ParserRuleContext {
		public TerminalNode SCHEMA() { return getToken(ValidationParser.SCHEMA, 0); }
		public TerminalNode CDA_CONFORMANCE_SCHEMA() { return getToken(ValidationParser.CDA_CONFORMANCE_SCHEMA, 0); }
		public Schema_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSchema_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Schema_typeContext schema_type() throws RecognitionException {
		Schema_typeContext _localctx = new Schema_typeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_schema_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			_la = _input.LA(1);
			if ( !(_la==SCHEMA || _la==CDA_CONFORMANCE_SCHEMA) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Schema_pathContext extends ParserRuleContext {
		public TerminalNode PATH() { return getToken(ValidationParser.PATH, 0); }
		public Schema_pathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema_path; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSchema_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Schema_pathContext schema_path() throws RecognitionException {
		Schema_pathContext _localctx = new Schema_pathContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_schema_path);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(PATH);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Schema_xpathContext extends ParserRuleContext {
		public TerminalNode PATH() { return getToken(ValidationParser.PATH, 0); }
		public TerminalNode XPATH() { return getToken(ValidationParser.XPATH, 0); }
		public Schema_xpathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema_xpath; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSchema_xpath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Schema_xpathContext schema_xpath() throws RecognitionException {
		Schema_xpathContext _localctx = new Schema_xpathContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_schema_xpath);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_la = _input.LA(1);
			if ( !(_la==PATH || _la==XPATH) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Hapifhirvalidator_idContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ValidationParser.IDENTIFIER, 0); }
		public TerminalNode DOT_SEPARATED_IDENTIFIER() { return getToken(ValidationParser.DOT_SEPARATED_IDENTIFIER, 0); }
		public TerminalNode INTEGER() { return getToken(ValidationParser.INTEGER, 0); }
		public Hapifhirvalidator_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hapifhirvalidator_id; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitHapifhirvalidator_id(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Hapifhirvalidator_idContext hapifhirvalidator_id() throws RecognitionException {
		Hapifhirvalidator_idContext _localctx = new Hapifhirvalidator_idContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_hapifhirvalidator_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			_la = _input.LA(1);
			if ( !(_la==INTEGER || _la==IDENTIFIER || _la==DOT_SEPARATED_IDENTIFIER) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class No_arg_testContext extends ParserRuleContext {
		public TerminalNode SIGNATURE() { return getToken(ValidationParser.SIGNATURE, 0); }
		public TerminalNode CDA_RENDERER() { return getToken(ValidationParser.CDA_RENDERER, 0); }
		public TerminalNode CDA_TEMPLATE_LIST() { return getToken(ValidationParser.CDA_TEMPLATE_LIST, 0); }
		public TerminalNode HAPIFHIRVALIDATOR() { return getToken(ValidationParser.HAPIFHIRVALIDATOR, 0); }
		public Hapifhirvalidator_idContext hapifhirvalidator_id() {
			return getRuleContext(Hapifhirvalidator_idContext.class,0);
		}
		public TerminalNode FHIRRESOURCEVALIDATOR() { return getToken(ValidationParser.FHIRRESOURCEVALIDATOR, 0); }
		public TerminalNode TERMINOLOGYVALIDATOR() { return getToken(ValidationParser.TERMINOLOGYVALIDATOR, 0); }
		public No_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_no_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitNo_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final No_arg_testContext no_arg_test() throws RecognitionException {
		No_arg_testContext _localctx = new No_arg_testContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_no_arg_test);
		int _la;
		try {
			setState(212);
			switch (_input.LA(1)) {
			case SIGNATURE:
				enterOuterAlt(_localctx, 1);
				{
				setState(203);
				match(SIGNATURE);
				}
				break;
			case CDA_RENDERER:
				enterOuterAlt(_localctx, 2);
				{
				setState(204);
				match(CDA_RENDERER);
				}
				break;
			case CDA_TEMPLATE_LIST:
				enterOuterAlt(_localctx, 3);
				{
				setState(205);
				match(CDA_TEMPLATE_LIST);
				}
				break;
			case HAPIFHIRVALIDATOR:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(206);
				match(HAPIFHIRVALIDATOR);
				setState(208);
				_la = _input.LA(1);
				if (_la==INTEGER || _la==IDENTIFIER || _la==DOT_SEPARATED_IDENTIFIER) {
					{
					setState(207);
					hapifhirvalidator_id();
					}
				}

				}
				}
				break;
			case FHIRRESOURCEVALIDATOR:
				enterOuterAlt(_localctx, 5);
				{
				setState(210);
				match(FHIRRESOURCEVALIDATOR);
				}
				break;
			case TERMINOLOGYVALIDATOR:
				enterOuterAlt(_localctx, 6);
				{
				setState(211);
				match(TERMINOLOGYVALIDATOR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xml_match_sourceContext extends ParserRuleContext {
		public TerminalNode CONTENT() { return getToken(ValidationParser.CONTENT, 0); }
		public TerminalNode JWT_PAYLOAD() { return getToken(ValidationParser.JWT_PAYLOAD, 0); }
		public Xml_match_sourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xml_match_source; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXml_match_source(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xml_match_sourceContext xml_match_source() throws RecognitionException {
		Xml_match_sourceContext _localctx = new Xml_match_sourceContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_xml_match_source);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			_la = _input.LA(1);
			if ( !(_la==CONTENT || _la==JWT_PAYLOAD) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_one_arg_testContext extends ParserRuleContext {
		public Xpath_one_arg_typeContext xpath_one_arg_type() {
			return getRuleContext(Xpath_one_arg_typeContext.class,0);
		}
		public TerminalNode CST() { return getToken(ValidationParser.CST, 0); }
		public Xpath_one_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_one_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_one_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_one_arg_testContext xpath_one_arg_test() throws RecognitionException {
		Xpath_one_arg_testContext _localctx = new Xpath_one_arg_testContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_xpath_one_arg_test);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			xpath_one_arg_type();
			setState(217);
			match(CST);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_one_arg_comparison_typeContext extends ParserRuleContext {
		public TerminalNode XPATHEXISTS() { return getToken(ValidationParser.XPATHEXISTS, 0); }
		public TerminalNode XPATHNOTEXISTS() { return getToken(ValidationParser.XPATHNOTEXISTS, 0); }
		public Xpath_one_arg_comparison_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_one_arg_comparison_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_one_arg_comparison_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_one_arg_comparison_typeContext xpath_one_arg_comparison_type() throws RecognitionException {
		Xpath_one_arg_comparison_typeContext _localctx = new Xpath_one_arg_comparison_typeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_xpath_one_arg_comparison_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			_la = _input.LA(1);
			if ( !(_la==XPATHEXISTS || _la==XPATHNOTEXISTS) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_one_arg_typeContext extends ParserRuleContext {
		public Xpath_one_arg_comparison_typeContext xpath_one_arg_comparison_type() {
			return getRuleContext(Xpath_one_arg_comparison_typeContext.class,0);
		}
		public Xml_match_sourceContext xml_match_source() {
			return getRuleContext(Xml_match_sourceContext.class,0);
		}
		public TerminalNode HL7_XPATHEXISTS() { return getToken(ValidationParser.HL7_XPATHEXISTS, 0); }
		public TerminalNode HL7_XPATHNOTEXISTS() { return getToken(ValidationParser.HL7_XPATHNOTEXISTS, 0); }
		public TerminalNode SOAP_XPATHEXISTS() { return getToken(ValidationParser.SOAP_XPATHEXISTS, 0); }
		public TerminalNode SOAP_XPATHNOTEXISTS() { return getToken(ValidationParser.SOAP_XPATHNOTEXISTS, 0); }
		public TerminalNode EBXML_XPATHEXISTS() { return getToken(ValidationParser.EBXML_XPATHEXISTS, 0); }
		public TerminalNode EBXML_XPATHNOTEXISTS() { return getToken(ValidationParser.EBXML_XPATHNOTEXISTS, 0); }
		public Text_match_typeContext text_match_type() {
			return getRuleContext(Text_match_typeContext.class,0);
		}
		public Text_match_sourceContext text_match_source() {
			return getRuleContext(Text_match_sourceContext.class,0);
		}
		public Xpath_one_arg_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_one_arg_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_one_arg_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_one_arg_typeContext xpath_one_arg_type() throws RecognitionException {
		Xpath_one_arg_typeContext _localctx = new Xpath_one_arg_typeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_xpath_one_arg_type);
		int _la;
		try {
			setState(230);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(222);
				_la = _input.LA(1);
				if (_la==CONTENT || _la==JWT_PAYLOAD) {
					{
					setState(221);
					xml_match_source();
					}
				}

				setState(224);
				xpath_one_arg_comparison_type();
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(225);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HL7_XPATHEXISTS) | (1L << HL7_XPATHNOTEXISTS) | (1L << SOAP_XPATHEXISTS) | (1L << SOAP_XPATHNOTEXISTS) | (1L << EBXML_XPATHEXISTS) | (1L << EBXML_XPATHNOTEXISTS))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(227);
				_la = _input.LA(1);
				if (((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & ((1L << (CONTEXT_PATH - 63)) | (1L << (CONTENT - 63)) | (1L << (HTTP_HEADER - 63)) | (1L << (JWT_PAYLOAD - 63)))) != 0)) {
					{
					setState(226);
					text_match_source();
					}
				}

				setState(229);
				text_match_type();
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Json_match_sourceContext extends ParserRuleContext {
		public TerminalNode CONTENT() { return getToken(ValidationParser.CONTENT, 0); }
		public TerminalNode JWT_PAYLOAD() { return getToken(ValidationParser.JWT_PAYLOAD, 0); }
		public Json_match_sourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_json_match_source; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJson_match_source(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Json_match_sourceContext json_match_source() throws RecognitionException {
		Json_match_sourceContext _localctx = new Json_match_sourceContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_json_match_source);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			_la = _input.LA(1);
			if ( !(_la==CONTENT || _la==JWT_PAYLOAD) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_one_arg_testContext extends ParserRuleContext {
		public Jsonpath_one_arg_typeContext jsonpath_one_arg_type() {
			return getRuleContext(Jsonpath_one_arg_typeContext.class,0);
		}
		public TerminalNode CST() { return getToken(ValidationParser.CST, 0); }
		public Jsonpath_one_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_one_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_one_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_one_arg_testContext jsonpath_one_arg_test() throws RecognitionException {
		Jsonpath_one_arg_testContext _localctx = new Jsonpath_one_arg_testContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_jsonpath_one_arg_test);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			jsonpath_one_arg_type();
			setState(235);
			match(CST);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_one_arg_comparison_typeContext extends ParserRuleContext {
		public TerminalNode JSONPATHEXISTS() { return getToken(ValidationParser.JSONPATHEXISTS, 0); }
		public TerminalNode JSONPATHNOTEXISTS() { return getToken(ValidationParser.JSONPATHNOTEXISTS, 0); }
		public Jsonpath_one_arg_comparison_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_one_arg_comparison_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_one_arg_comparison_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_one_arg_comparison_typeContext jsonpath_one_arg_comparison_type() throws RecognitionException {
		Jsonpath_one_arg_comparison_typeContext _localctx = new Jsonpath_one_arg_comparison_typeContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_jsonpath_one_arg_comparison_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			_la = _input.LA(1);
			if ( !(_la==JSONPATHEXISTS || _la==JSONPATHNOTEXISTS) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_one_arg_typeContext extends ParserRuleContext {
		public Jsonpath_one_arg_comparison_typeContext jsonpath_one_arg_comparison_type() {
			return getRuleContext(Jsonpath_one_arg_comparison_typeContext.class,0);
		}
		public Json_match_sourceContext json_match_source() {
			return getRuleContext(Json_match_sourceContext.class,0);
		}
		public Text_match_typeContext text_match_type() {
			return getRuleContext(Text_match_typeContext.class,0);
		}
		public Text_match_sourceContext text_match_source() {
			return getRuleContext(Text_match_sourceContext.class,0);
		}
		public Jsonpath_one_arg_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_one_arg_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_one_arg_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_one_arg_typeContext jsonpath_one_arg_type() throws RecognitionException {
		Jsonpath_one_arg_typeContext _localctx = new Jsonpath_one_arg_typeContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_jsonpath_one_arg_type);
		int _la;
		try {
			setState(247);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(240);
				_la = _input.LA(1);
				if (_la==CONTENT || _la==JWT_PAYLOAD) {
					{
					setState(239);
					json_match_source();
					}
				}

				setState(242);
				jsonpath_one_arg_comparison_type();
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(244);
				_la = _input.LA(1);
				if (((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & ((1L << (CONTEXT_PATH - 63)) | (1L << (CONTENT - 63)) | (1L << (HTTP_HEADER - 63)) | (1L << (JWT_PAYLOAD - 63)))) != 0)) {
					{
					setState(243);
					text_match_source();
					}
				}

				setState(246);
				text_match_type();
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Text_match_typeContext extends ParserRuleContext {
		public TerminalNode MATCHES() { return getToken(ValidationParser.MATCHES, 0); }
		public TerminalNode NOTMATCHES() { return getToken(ValidationParser.NOTMATCHES, 0); }
		public TerminalNode CONTAINS() { return getToken(ValidationParser.CONTAINS, 0); }
		public TerminalNode NOTCONTAINS() { return getToken(ValidationParser.NOTCONTAINS, 0); }
		public TerminalNode EQUALS() { return getToken(ValidationParser.EQUALS, 0); }
		public TerminalNode NOTEQUALS() { return getToken(ValidationParser.NOTEQUALS, 0); }
		public Text_match_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text_match_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitText_match_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Text_match_typeContext text_match_type() throws RecognitionException {
		Text_match_typeContext _localctx = new Text_match_typeContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_text_match_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQUALS) | (1L << NOTEQUALS) | (1L << MATCHES) | (1L << NOTMATCHES) | (1L << CONTAINS) | (1L << NOTCONTAINS))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Text_match_sourceContext extends ParserRuleContext {
		public TerminalNode CONTEXT_PATH() { return getToken(ValidationParser.CONTEXT_PATH, 0); }
		public TerminalNode CONTENT() { return getToken(ValidationParser.CONTENT, 0); }
		public TerminalNode JWT_PAYLOAD() { return getToken(ValidationParser.JWT_PAYLOAD, 0); }
		public TerminalNode HTTP_HEADER() { return getToken(ValidationParser.HTTP_HEADER, 0); }
		public Http_header_nameContext http_header_name() {
			return getRuleContext(Http_header_nameContext.class,0);
		}
		public Text_match_sourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text_match_source; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitText_match_source(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Text_match_sourceContext text_match_source() throws RecognitionException {
		Text_match_sourceContext _localctx = new Text_match_sourceContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_text_match_source);
		int _la;
		try {
			setState(254);
			switch (_input.LA(1)) {
			case CONTEXT_PATH:
			case CONTENT:
			case JWT_PAYLOAD:
				enterOuterAlt(_localctx, 1);
				{
				setState(251);
				_la = _input.LA(1);
				if ( !(((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & ((1L << (CONTEXT_PATH - 63)) | (1L << (CONTENT - 63)) | (1L << (JWT_PAYLOAD - 63)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case HTTP_HEADER:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(252);
				match(HTTP_HEADER);
				setState(253);
				http_header_name();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Http_header_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ValidationParser.IDENTIFIER, 0); }
		public Http_header_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_http_header_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitHttp_header_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Http_header_nameContext http_header_name() throws RecognitionException {
		Http_header_nameContext _localctx = new Http_header_nameContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_http_header_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_argContext extends ParserRuleContext {
		public TerminalNode CST() { return getToken(ValidationParser.CST, 0); }
		public Xpath_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_arg; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_arg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_argContext xpath_arg() throws RecognitionException {
		Xpath_argContext _localctx = new Xpath_argContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_xpath_arg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(CST);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_two_arg_comparison_typeContext extends ParserRuleContext {
		public TerminalNode XPATHEQUALS() { return getToken(ValidationParser.XPATHEQUALS, 0); }
		public TerminalNode XPATHNOTEQUALS() { return getToken(ValidationParser.XPATHNOTEQUALS, 0); }
		public TerminalNode XPATHEQUALSIGNORECASE() { return getToken(ValidationParser.XPATHEQUALSIGNORECASE, 0); }
		public TerminalNode XPATHNOTEQUALSIGNORECASE() { return getToken(ValidationParser.XPATHNOTEQUALSIGNORECASE, 0); }
		public TerminalNode XPATHMATCHES() { return getToken(ValidationParser.XPATHMATCHES, 0); }
		public TerminalNode XPATHNOTMATCHES() { return getToken(ValidationParser.XPATHNOTMATCHES, 0); }
		public TerminalNode XPATHCOMPARE() { return getToken(ValidationParser.XPATHCOMPARE, 0); }
		public TerminalNode XPATHCONTAINS() { return getToken(ValidationParser.XPATHCONTAINS, 0); }
		public TerminalNode XPATHNOTCONTAINS() { return getToken(ValidationParser.XPATHNOTCONTAINS, 0); }
		public TerminalNode XPATHCONTAINSIGNORECASE() { return getToken(ValidationParser.XPATHCONTAINSIGNORECASE, 0); }
		public TerminalNode XPATHNOTCONTAINSIGNORECASE() { return getToken(ValidationParser.XPATHNOTCONTAINSIGNORECASE, 0); }
		public TerminalNode XSLT() { return getToken(ValidationParser.XSLT, 0); }
		public Xpath_two_arg_comparison_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_two_arg_comparison_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_two_arg_comparison_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_two_arg_comparison_typeContext xpath_two_arg_comparison_type() throws RecognitionException {
		Xpath_two_arg_comparison_typeContext _localctx = new Xpath_two_arg_comparison_typeContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_xpath_two_arg_comparison_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << XPATHEQUALS) | (1L << XPATHNOTEQUALS) | (1L << XPATHEQUALSIGNORECASE) | (1L << XPATHNOTEQUALSIGNORECASE) | (1L << XPATHMATCHES) | (1L << XPATHNOTMATCHES) | (1L << XPATHCOMPARE) | (1L << XPATHCONTAINS) | (1L << XPATHNOTCONTAINS) | (1L << XPATHCONTAINSIGNORECASE) | (1L << XPATHNOTCONTAINSIGNORECASE) | (1L << XSLT))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_two_arg_testContext extends ParserRuleContext {
		public Xpath_two_arg_typeContext xpath_two_arg_type() {
			return getRuleContext(Xpath_two_arg_typeContext.class,0);
		}
		public List<Xpath_argContext> xpath_arg() {
			return getRuleContexts(Xpath_argContext.class);
		}
		public Xpath_argContext xpath_arg(int i) {
			return getRuleContext(Xpath_argContext.class,i);
		}
		public Xpath_two_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_two_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_two_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_two_arg_testContext xpath_two_arg_test() throws RecognitionException {
		Xpath_two_arg_testContext _localctx = new Xpath_two_arg_testContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_xpath_two_arg_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			xpath_two_arg_type();
			setState(263);
			xpath_arg();
			setState(265); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(264);
				xpath_arg();
				}
				}
				setState(267); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CST );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_two_arg_typeContext extends ParserRuleContext {
		public Xpath_two_arg_comparison_typeContext xpath_two_arg_comparison_type() {
			return getRuleContext(Xpath_two_arg_comparison_typeContext.class,0);
		}
		public Xml_match_sourceContext xml_match_source() {
			return getRuleContext(Xml_match_sourceContext.class,0);
		}
		public TerminalNode HL7_XPATHEQUALS() { return getToken(ValidationParser.HL7_XPATHEQUALS, 0); }
		public TerminalNode HL7_XPATHNOTEQUALS() { return getToken(ValidationParser.HL7_XPATHNOTEQUALS, 0); }
		public TerminalNode HL7_XPATHMATCHES() { return getToken(ValidationParser.HL7_XPATHMATCHES, 0); }
		public TerminalNode HL7_XPATHNOTMATCHES() { return getToken(ValidationParser.HL7_XPATHNOTMATCHES, 0); }
		public TerminalNode HL7_XSLT() { return getToken(ValidationParser.HL7_XSLT, 0); }
		public TerminalNode EBXML_XSLT() { return getToken(ValidationParser.EBXML_XSLT, 0); }
		public TerminalNode EBXML_XPATHEQUALS() { return getToken(ValidationParser.EBXML_XPATHEQUALS, 0); }
		public TerminalNode EBXML_XPATHNOTEQUALS() { return getToken(ValidationParser.EBXML_XPATHNOTEQUALS, 0); }
		public TerminalNode SOAP_XPATHEQUALS() { return getToken(ValidationParser.SOAP_XPATHEQUALS, 0); }
		public TerminalNode SOAP_XPATHNOTEQUALS() { return getToken(ValidationParser.SOAP_XPATHNOTEQUALS, 0); }
		public TerminalNode CDA_CONFORMANCE_XSLT() { return getToken(ValidationParser.CDA_CONFORMANCE_XSLT, 0); }
		public Xpath_two_arg_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_two_arg_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_two_arg_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_two_arg_typeContext xpath_two_arg_type() throws RecognitionException {
		Xpath_two_arg_typeContext _localctx = new Xpath_two_arg_typeContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_xpath_two_arg_type);
		int _la;
		try {
			setState(274);
			switch (_input.LA(1)) {
			case XPATHEQUALS:
			case XPATHNOTEQUALS:
			case XPATHEQUALSIGNORECASE:
			case XPATHNOTEQUALSIGNORECASE:
			case XPATHMATCHES:
			case XPATHNOTMATCHES:
			case XPATHCOMPARE:
			case XPATHCONTAINS:
			case XPATHNOTCONTAINS:
			case XPATHCONTAINSIGNORECASE:
			case XPATHNOTCONTAINSIGNORECASE:
			case XSLT:
			case CONTENT:
			case JWT_PAYLOAD:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(270);
				_la = _input.LA(1);
				if (_la==CONTENT || _la==JWT_PAYLOAD) {
					{
					setState(269);
					xml_match_source();
					}
				}

				setState(272);
				xpath_two_arg_comparison_type();
				}
				}
				break;
			case HL7_XPATHEQUALS:
			case HL7_XPATHNOTEQUALS:
			case EBXML_XPATHEQUALS:
			case EBXML_XPATHNOTEQUALS:
			case SOAP_XPATHEQUALS:
			case SOAP_XPATHNOTEQUALS:
			case HL7_XPATHMATCHES:
			case HL7_XPATHNOTMATCHES:
			case HL7_XSLT:
			case EBXML_XSLT:
			case CDA_CONFORMANCE_XSLT:
				enterOuterAlt(_localctx, 2);
				{
				setState(273);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HL7_XPATHEQUALS) | (1L << HL7_XPATHNOTEQUALS) | (1L << EBXML_XPATHEQUALS) | (1L << EBXML_XPATHNOTEQUALS) | (1L << SOAP_XPATHEQUALS) | (1L << SOAP_XPATHNOTEQUALS) | (1L << HL7_XPATHMATCHES) | (1L << HL7_XPATHNOTMATCHES) | (1L << HL7_XSLT) | (1L << EBXML_XSLT) | (1L << CDA_CONFORMANCE_XSLT))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_multi_arg_testContext extends ParserRuleContext {
		public Xpath_multi_arg_typeContext xpath_multi_arg_type() {
			return getRuleContext(Xpath_multi_arg_typeContext.class,0);
		}
		public List<Xpath_argContext> xpath_arg() {
			return getRuleContexts(Xpath_argContext.class);
		}
		public Xpath_argContext xpath_arg(int i) {
			return getRuleContext(Xpath_argContext.class,i);
		}
		public Xml_match_sourceContext xml_match_source() {
			return getRuleContext(Xml_match_sourceContext.class,0);
		}
		public Xpath_multi_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_multi_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_multi_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_multi_arg_testContext xpath_multi_arg_test() throws RecognitionException {
		Xpath_multi_arg_testContext _localctx = new Xpath_multi_arg_testContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_xpath_multi_arg_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(276);
				xml_match_source();
				}
				break;
			}
			setState(279);
			xpath_multi_arg_type();
			setState(280);
			xpath_arg();
			setState(282); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(281);
				xpath_arg();
				}
				}
				setState(284); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CST );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Xpath_multi_arg_typeContext extends ParserRuleContext {
		public TerminalNode XPATHIN() { return getToken(ValidationParser.XPATHIN, 0); }
		public Xml_match_sourceContext xml_match_source() {
			return getRuleContext(Xml_match_sourceContext.class,0);
		}
		public Xpath_multi_arg_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath_multi_arg_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitXpath_multi_arg_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Xpath_multi_arg_typeContext xpath_multi_arg_type() throws RecognitionException {
		Xpath_multi_arg_typeContext _localctx = new Xpath_multi_arg_typeContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_xpath_multi_arg_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			_la = _input.LA(1);
			if (_la==CONTENT || _la==JWT_PAYLOAD) {
				{
				setState(286);
				xml_match_source();
				}
			}

			setState(289);
			match(XPATHIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_argContext extends ParserRuleContext {
		public TerminalNode CST() { return getToken(ValidationParser.CST, 0); }
		public Jsonpath_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_arg; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_arg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_argContext jsonpath_arg() throws RecognitionException {
		Jsonpath_argContext _localctx = new Jsonpath_argContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_jsonpath_arg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(CST);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_two_arg_comparison_typeContext extends ParserRuleContext {
		public TerminalNode JSONPATHEQUALS() { return getToken(ValidationParser.JSONPATHEQUALS, 0); }
		public TerminalNode JSONPATHNOTEQUALS() { return getToken(ValidationParser.JSONPATHNOTEQUALS, 0); }
		public TerminalNode JSONPATHEQUALSIGNORECASE() { return getToken(ValidationParser.JSONPATHEQUALSIGNORECASE, 0); }
		public TerminalNode JSONPATHNOTEQUALSIGNORECASE() { return getToken(ValidationParser.JSONPATHNOTEQUALSIGNORECASE, 0); }
		public TerminalNode JSONPATHMATCHES() { return getToken(ValidationParser.JSONPATHMATCHES, 0); }
		public TerminalNode JSONPATHNOTMATCHES() { return getToken(ValidationParser.JSONPATHNOTMATCHES, 0); }
		public TerminalNode JSONPATHCOMPARE() { return getToken(ValidationParser.JSONPATHCOMPARE, 0); }
		public TerminalNode JSONPATHCONTAINS() { return getToken(ValidationParser.JSONPATHCONTAINS, 0); }
		public TerminalNode JSONPATHNOTCONTAINS() { return getToken(ValidationParser.JSONPATHNOTCONTAINS, 0); }
		public TerminalNode JSONPATHCONTAINSIGNORECASE() { return getToken(ValidationParser.JSONPATHCONTAINSIGNORECASE, 0); }
		public TerminalNode JSONPATHNOTCONTAINSIGNORECASE() { return getToken(ValidationParser.JSONPATHNOTCONTAINSIGNORECASE, 0); }
		public Jsonpath_two_arg_comparison_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_two_arg_comparison_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_two_arg_comparison_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_two_arg_comparison_typeContext jsonpath_two_arg_comparison_type() throws RecognitionException {
		Jsonpath_two_arg_comparison_typeContext _localctx = new Jsonpath_two_arg_comparison_typeContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_jsonpath_two_arg_comparison_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			_la = _input.LA(1);
			if ( !(((((_la - 92)) & ~0x3f) == 0 && ((1L << (_la - 92)) & ((1L << (JSONPATHEQUALS - 92)) | (1L << (JSONPATHNOTEQUALS - 92)) | (1L << (JSONPATHEQUALSIGNORECASE - 92)) | (1L << (JSONPATHNOTEQUALSIGNORECASE - 92)) | (1L << (JSONPATHMATCHES - 92)) | (1L << (JSONPATHNOTMATCHES - 92)) | (1L << (JSONPATHCOMPARE - 92)) | (1L << (JSONPATHCONTAINS - 92)) | (1L << (JSONPATHNOTCONTAINS - 92)) | (1L << (JSONPATHCONTAINSIGNORECASE - 92)) | (1L << (JSONPATHNOTCONTAINSIGNORECASE - 92)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_two_arg_testContext extends ParserRuleContext {
		public Xpath_two_arg_typeContext xpath_two_arg_type() {
			return getRuleContext(Xpath_two_arg_typeContext.class,0);
		}
		public List<Xpath_argContext> xpath_arg() {
			return getRuleContexts(Xpath_argContext.class);
		}
		public Xpath_argContext xpath_arg(int i) {
			return getRuleContext(Xpath_argContext.class,i);
		}
		public Jsonpath_two_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_two_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_two_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_two_arg_testContext jsonpath_two_arg_test() throws RecognitionException {
		Jsonpath_two_arg_testContext _localctx = new Jsonpath_two_arg_testContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_jsonpath_two_arg_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			xpath_two_arg_type();
			setState(296);
			xpath_arg();
			setState(298); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(297);
				xpath_arg();
				}
				}
				setState(300); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CST );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_two_arg_typeContext extends ParserRuleContext {
		public Jsonpath_two_arg_comparison_typeContext jsonpath_two_arg_comparison_type() {
			return getRuleContext(Jsonpath_two_arg_comparison_typeContext.class,0);
		}
		public Json_match_sourceContext json_match_source() {
			return getRuleContext(Json_match_sourceContext.class,0);
		}
		public Jsonpath_two_arg_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_two_arg_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_two_arg_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_two_arg_typeContext jsonpath_two_arg_type() throws RecognitionException {
		Jsonpath_two_arg_typeContext _localctx = new Jsonpath_two_arg_typeContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_jsonpath_two_arg_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(303);
			_la = _input.LA(1);
			if (_la==CONTENT || _la==JWT_PAYLOAD) {
				{
				setState(302);
				json_match_source();
				}
			}

			setState(305);
			jsonpath_two_arg_comparison_type();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_multi_arg_testContext extends ParserRuleContext {
		public Jsonpath_multi_arg_typeContext jsonpath_multi_arg_type() {
			return getRuleContext(Jsonpath_multi_arg_typeContext.class,0);
		}
		public List<Jsonpath_argContext> jsonpath_arg() {
			return getRuleContexts(Jsonpath_argContext.class);
		}
		public Jsonpath_argContext jsonpath_arg(int i) {
			return getRuleContext(Jsonpath_argContext.class,i);
		}
		public Json_match_sourceContext json_match_source() {
			return getRuleContext(Json_match_sourceContext.class,0);
		}
		public Jsonpath_multi_arg_testContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_multi_arg_test; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_multi_arg_test(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_multi_arg_testContext jsonpath_multi_arg_test() throws RecognitionException {
		Jsonpath_multi_arg_testContext _localctx = new Jsonpath_multi_arg_testContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_jsonpath_multi_arg_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(307);
				json_match_source();
				}
				break;
			}
			setState(310);
			jsonpath_multi_arg_type();
			setState(311);
			jsonpath_arg();
			setState(313); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(312);
				jsonpath_arg();
				}
				}
				setState(315); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CST );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Jsonpath_multi_arg_typeContext extends ParserRuleContext {
		public TerminalNode JSONPATHIN() { return getToken(ValidationParser.JSONPATHIN, 0); }
		public Json_match_sourceContext json_match_source() {
			return getRuleContext(Json_match_sourceContext.class,0);
		}
		public Jsonpath_multi_arg_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonpath_multi_arg_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitJsonpath_multi_arg_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Jsonpath_multi_arg_typeContext jsonpath_multi_arg_type() throws RecognitionException {
		Jsonpath_multi_arg_typeContext _localctx = new Jsonpath_multi_arg_typeContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_jsonpath_multi_arg_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			_la = _input.LA(1);
			if (_la==CONTENT || _la==JWT_PAYLOAD) {
				{
				setState(317);
				json_match_source();
				}
			}

			setState(320);
			match(JSONPATHIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Annotation_directiveContext extends ParserRuleContext {
		public TerminalNode ANNOTATION() { return getToken(ValidationParser.ANNOTATION, 0); }
		public TerminalNode ANNOTATION_TEXT() { return getToken(ValidationParser.ANNOTATION_TEXT, 0); }
		public Annotation_directiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation_directive; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitAnnotation_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Annotation_directiveContext annotation_directive() throws RecognitionException {
		Annotation_directiveContext _localctx = new Annotation_directiveContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_annotation_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(ANNOTATION);
			setState(323);
			match(ANNOTATION_TEXT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Subset_statementContext extends ParserRuleContext {
		public TerminalNode SUBSET() { return getToken(ValidationParser.SUBSET, 0); }
		public Subset_nameContext subset_name() {
			return getRuleContext(Subset_nameContext.class,0);
		}
		public Validate_directivesContext validate_directives() {
			return getRuleContext(Validate_directivesContext.class,0);
		}
		public Subset_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subset_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSubset_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Subset_statementContext subset_statement() throws RecognitionException {
		Subset_statementContext _localctx = new Subset_statementContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_subset_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			match(SUBSET);
			setState(326);
			subset_name();
			setState(327);
			validate_directives();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Subset_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ValidationParser.IDENTIFIER, 0); }
		public Subset_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subset_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitSubset_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Subset_nameContext subset_name() throws RecognitionException {
		Subset_nameContext _localctx = new Subset_nameContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_subset_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Include_statementContext extends ParserRuleContext {
		public TerminalNode INCLUDE() { return getToken(ValidationParser.INCLUDE, 0); }
		public TerminalNode PATH() { return getToken(ValidationParser.PATH, 0); }
		public Include_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_include_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ValidationParserVisitor ) return ((ValidationParserVisitor<? extends T>)visitor).visitInclude_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Include_statementContext include_statement() throws RecognitionException {
		Include_statementContext _localctx = new Include_statementContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_include_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(INCLUDE);
			setState(332);
			match(PATH);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3i\u0151\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\3\2\3\2\3\2\3\2\6\2m\n\2\r\2\16\2n\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\5"+
		"\3\5\3\5\3\5\3\6\3\6\3\7\7\7\177\n\7\f\7\16\7\u0082\13\7\3\b\3\b\3\b\3"+
		"\b\3\b\5\b\u0089\n\b\3\t\3\t\3\t\3\t\5\t\u008f\n\t\3\n\6\n\u0092\n\n\r"+
		"\n\16\n\u0093\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\5\r\u00a4\n\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3"+
		"\21\3\21\3\21\3\21\5\21\u00b4\n\21\3\22\3\22\3\22\3\22\7\22\u00ba\n\22"+
		"\f\22\16\22\u00bd\13\22\3\23\3\23\3\24\3\24\3\24\5\24\u00c4\n\24\3\25"+
		"\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\5\31\u00d3"+
		"\n\31\3\31\3\31\5\31\u00d7\n\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35"+
		"\5\35\u00e1\n\35\3\35\3\35\3\35\5\35\u00e6\n\35\3\35\5\35\u00e9\n\35\3"+
		"\36\3\36\3\37\3\37\3\37\3 \3 \3!\5!\u00f3\n!\3!\3!\5!\u00f7\n!\3!\5!\u00fa"+
		"\n!\3\"\3\"\3#\3#\3#\5#\u0101\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\6\'\u010c"+
		"\n\'\r\'\16\'\u010d\3(\5(\u0111\n(\3(\3(\5(\u0115\n(\3)\5)\u0118\n)\3"+
		")\3)\3)\6)\u011d\n)\r)\16)\u011e\3*\5*\u0122\n*\3*\3*\3+\3+\3,\3,\3-\3"+
		"-\3-\6-\u012d\n-\r-\16-\u012e\3.\5.\u0132\n.\3.\3.\3/\5/\u0137\n/\3/\3"+
		"/\3/\6/\u013c\n/\r/\16/\u013d\3\60\5\60\u0141\n\60\3\60\3\60\3\61\3\61"+
		"\3\61\3\62\3\62\3\62\3\62\3\63\3\63\3\64\3\64\3\64\3\64\2\2\65\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVX"+
		"Z\\^`bdf\2\22\3\2FI\5\2\5\5PPRS\4\2PPTU\3\2\r\16\4\2\22\22\24\24\3\2T"+
		"U\5\2\5\5PPRR\4\2BBDD\3\2\33\34\3\2\35\"\3\2\\]\3\2#(\4\2ABDD\5\2)*\61"+
		"\64\67<\5\2+\60\65\66=?\3\2^h\u0149\2l\3\2\2\2\4r\3\2\2\2\6u\3\2\2\2\b"+
		"w\3\2\2\2\n{\3\2\2\2\f\u0080\3\2\2\2\16\u0088\3\2\2\2\20\u008a\3\2\2\2"+
		"\22\u0091\3\2\2\2\24\u0095\3\2\2\2\26\u009b\3\2\2\2\30\u009d\3\2\2\2\32"+
		"\u00a7\3\2\2\2\34\u00a9\3\2\2\2\36\u00ab\3\2\2\2 \u00b3\3\2\2\2\"\u00b5"+
		"\3\2\2\2$\u00be\3\2\2\2&\u00c0\3\2\2\2(\u00c5\3\2\2\2*\u00c7\3\2\2\2,"+
		"\u00c9\3\2\2\2.\u00cb\3\2\2\2\60\u00d6\3\2\2\2\62\u00d8\3\2\2\2\64\u00da"+
		"\3\2\2\2\66\u00dd\3\2\2\28\u00e8\3\2\2\2:\u00ea\3\2\2\2<\u00ec\3\2\2\2"+
		">\u00ef\3\2\2\2@\u00f9\3\2\2\2B\u00fb\3\2\2\2D\u0100\3\2\2\2F\u0102\3"+
		"\2\2\2H\u0104\3\2\2\2J\u0106\3\2\2\2L\u0108\3\2\2\2N\u0114\3\2\2\2P\u0117"+
		"\3\2\2\2R\u0121\3\2\2\2T\u0125\3\2\2\2V\u0127\3\2\2\2X\u0129\3\2\2\2Z"+
		"\u0131\3\2\2\2\\\u0136\3\2\2\2^\u0140\3\2\2\2`\u0144\3\2\2\2b\u0147\3"+
		"\2\2\2d\u014b\3\2\2\2f\u014d\3\2\2\2hm\5\4\3\2im\5\b\5\2jm\5b\62\2km\5"+
		"\16\b\2lh\3\2\2\2li\3\2\2\2lj\3\2\2\2lk\3\2\2\2mn\3\2\2\2nl\3\2\2\2no"+
		"\3\2\2\2op\3\2\2\2pq\7\2\2\3q\3\3\2\2\2rs\5\6\4\2st\7X\2\2t\5\3\2\2\2"+
		"uv\t\2\2\2v\7\3\2\2\2wx\7J\2\2xy\5\n\6\2yz\5\f\7\2z\t\3\2\2\2{|\t\3\2"+
		"\2|\13\3\2\2\2}\177\5\16\b\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2"+
		"\2\u0080\u0081\3\2\2\2\u0081\r\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0089"+
		"\5\20\t\2\u0084\u0089\5\24\13\2\u0085\u0089\5\30\r\2\u0086\u0089\5`\61"+
		"\2\u0087\u0089\5f\64\2\u0088\u0083\3\2\2\2\u0088\u0084\3\2\2\2\u0088\u0085"+
		"\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0087\3\2\2\2\u0089\17\3\2\2\2\u008a"+
		"\u008e\7L\2\2\u008b\u008c\7\17\2\2\u008c\u008f\5\22\n\2\u008d\u008f\5"+
		" \21\2\u008e\u008b\3\2\2\2\u008e\u008d\3\2\2\2\u008f\21\3\2\2\2\u0090"+
		"\u0092\t\4\2\2\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091\3\2"+
		"\2\2\u0093\u0094\3\2\2\2\u0094\23\3\2\2\2\u0095\u0096\7K\2\2\u0096\u0097"+
		"\5\26\f\2\u0097\u0098\7Q\2\2\u0098\u0099\7X\2\2\u0099\u009a\b\13\1\2\u009a"+
		"\25\3\2\2\2\u009b\u009c\t\5\2\2\u009c\27\3\2\2\2\u009d\u009e\7\7\2\2\u009e"+
		"\u009f\5 \21\2\u009f\u00a0\7\b\2\2\u00a0\u00a3\5\32\16\2\u00a1\u00a2\7"+
		"\t\2\2\u00a2\u00a4\5\34\17\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00a6\5\36\20\2\u00a6\31\3\2\2\2\u00a7\u00a8\5\f"+
		"\7\2\u00a8\33\3\2\2\2\u00a9\u00aa\5\f\7\2\u00aa\35\3\2\2\2\u00ab\u00ac"+
		"\7\n\2\2\u00ac\37\3\2\2\2\u00ad\u00b4\5\60\31\2\u00ae\u00b4\5&\24\2\u00af"+
		"\u00b4\5\64\33\2\u00b0\u00b4\5L\'\2\u00b1\u00b4\5P)\2\u00b2\u00b4\5\""+
		"\22\2\u00b3\u00ad\3\2\2\2\u00b3\u00ae\3\2\2\2\u00b3\u00af\3\2\2\2\u00b3"+
		"\u00b0\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b2\3\2\2\2\u00b4!\3\2\2\2"+
		"\u00b5\u00b6\7@\2\2\u00b6\u00b7\5$\23\2\u00b7\u00bb\5H%\2\u00b8\u00ba"+
		"\5H%\2\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb"+
		"\u00bc\3\2\2\2\u00bc#\3\2\2\2\u00bd\u00bb\3\2\2\2\u00be\u00bf\7Z\2\2\u00bf"+
		"%\3\2\2\2\u00c0\u00c1\5(\25\2\u00c1\u00c3\5*\26\2\u00c2\u00c4\5,\27\2"+
		"\u00c3\u00c2\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\'\3\2\2\2\u00c5\u00c6\t"+
		"\6\2\2\u00c6)\3\2\2\2\u00c7\u00c8\7T\2\2\u00c8+\3\2\2\2\u00c9\u00ca\t"+
		"\7\2\2\u00ca-\3\2\2\2\u00cb\u00cc\t\b\2\2\u00cc/\3\2\2\2\u00cd\u00d7\7"+
		"\25\2\2\u00ce\u00d7\7\26\2\2\u00cf\u00d7\7\27\2\2\u00d0\u00d2\7\30\2\2"+
		"\u00d1\u00d3\5.\30\2\u00d2\u00d1\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d7"+
		"\3\2\2\2\u00d4\u00d7\7\31\2\2\u00d5\u00d7\7\32\2\2\u00d6\u00cd\3\2\2\2"+
		"\u00d6\u00ce\3\2\2\2\u00d6\u00cf\3\2\2\2\u00d6\u00d0\3\2\2\2\u00d6\u00d4"+
		"\3\2\2\2\u00d6\u00d5\3\2\2\2\u00d7\61\3\2\2\2\u00d8\u00d9\t\t\2\2\u00d9"+
		"\63\3\2\2\2\u00da\u00db\58\35\2\u00db\u00dc\7Z\2\2\u00dc\65\3\2\2\2\u00dd"+
		"\u00de\t\n\2\2\u00de\67\3\2\2\2\u00df\u00e1\5\62\32\2\u00e0\u00df\3\2"+
		"\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e9\5\66\34\2\u00e3"+
		"\u00e9\t\13\2\2\u00e4\u00e6\5D#\2\u00e5\u00e4\3\2\2\2\u00e5\u00e6\3\2"+
		"\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e9\5B\"\2\u00e8\u00e0\3\2\2\2\u00e8"+
		"\u00e3\3\2\2\2\u00e8\u00e5\3\2\2\2\u00e99\3\2\2\2\u00ea\u00eb\t\t\2\2"+
		"\u00eb;\3\2\2\2\u00ec\u00ed\5@!\2\u00ed\u00ee\7Z\2\2\u00ee=\3\2\2\2\u00ef"+
		"\u00f0\t\f\2\2\u00f0?\3\2\2\2\u00f1\u00f3\5:\36\2\u00f2\u00f1\3\2\2\2"+
		"\u00f2\u00f3\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00fa\5> \2\u00f5\u00f7"+
		"\5D#\2\u00f6\u00f5\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8"+
		"\u00fa\5B\"\2\u00f9\u00f2\3\2\2\2\u00f9\u00f6\3\2\2\2\u00faA\3\2\2\2\u00fb"+
		"\u00fc\t\r\2\2\u00fcC\3\2\2\2\u00fd\u0101\t\16\2\2\u00fe\u00ff\7C\2\2"+
		"\u00ff\u0101\5F$\2\u0100\u00fd\3\2\2\2\u0100\u00fe\3\2\2\2\u0101E\3\2"+
		"\2\2\u0102\u0103\7P\2\2\u0103G\3\2\2\2\u0104\u0105\7Z\2\2\u0105I\3\2\2"+
		"\2\u0106\u0107\t\17\2\2\u0107K\3\2\2\2\u0108\u0109\5N(\2\u0109\u010b\5"+
		"H%\2\u010a\u010c\5H%\2\u010b\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010b"+
		"\3\2\2\2\u010d\u010e\3\2\2\2\u010eM\3\2\2\2\u010f\u0111\5\62\32\2\u0110"+
		"\u010f\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0115\5J"+
		"&\2\u0113\u0115\t\20\2\2\u0114\u0110\3\2\2\2\u0114\u0113\3\2\2\2\u0115"+
		"O\3\2\2\2\u0116\u0118\5\62\32\2\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2"+
		"\2\u0118\u0119\3\2\2\2\u0119\u011a\5R*\2\u011a\u011c\5H%\2\u011b\u011d"+
		"\5H%\2\u011c\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u011c\3\2\2\2\u011e"+
		"\u011f\3\2\2\2\u011fQ\3\2\2\2\u0120\u0122\5\62\32\2\u0121\u0120\3\2\2"+
		"\2\u0121\u0122\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0124\7E\2\2\u0124S\3"+
		"\2\2\2\u0125\u0126\7Z\2\2\u0126U\3\2\2\2\u0127\u0128\t\21\2\2\u0128W\3"+
		"\2\2\2\u0129\u012a\5N(\2\u012a\u012c\5H%\2\u012b\u012d\5H%\2\u012c\u012b"+
		"\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u012c\3\2\2\2\u012e\u012f\3\2\2\2\u012f"+
		"Y\3\2\2\2\u0130\u0132\5:\36\2\u0131\u0130\3\2\2\2\u0131\u0132\3\2\2\2"+
		"\u0132\u0133\3\2\2\2\u0133\u0134\5V,\2\u0134[\3\2\2\2\u0135\u0137\5:\36"+
		"\2\u0136\u0135\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0139"+
		"\5^\60\2\u0139\u013b\5T+\2\u013a\u013c\5T+\2\u013b\u013a\3\2\2\2\u013c"+
		"\u013d\3\2\2\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e]\3\2\2\2"+
		"\u013f\u0141\5:\36\2\u0140\u013f\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0142"+
		"\3\2\2\2\u0142\u0143\7i\2\2\u0143_\3\2\2\2\u0144\u0145\7M\2\2\u0145\u0146"+
		"\7X\2\2\u0146a\3\2\2\2\u0147\u0148\7N\2\2\u0148\u0149\5d\63\2\u0149\u014a"+
		"\5\f\7\2\u014ac\3\2\2\2\u014b\u014c\7P\2\2\u014ce\3\2\2\2\u014d\u014e"+
		"\7\13\2\2\u014e\u014f\7T\2\2\u014fg\3\2\2\2 ln\u0080\u0088\u008e\u0093"+
		"\u00a3\u00b3\u00bb\u00c3\u00d2\u00d6\u00e0\u00e5\u00e8\u00f2\u00f6\u00f9"+
		"\u0100\u010d\u0110\u0114\u0117\u011e\u0121\u012e\u0131\u0136\u013d\u0140";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}