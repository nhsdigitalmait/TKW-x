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
		DEFAULT=85, ANNOTATION_TEXT=86, SP=87, CST=88, LF=89;
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
		RULE_text_match_type = 28, RULE_text_match_source = 29, RULE_http_header_name = 30, 
		RULE_xpath_arg = 31, RULE_xpath_two_arg_comparison_type = 32, RULE_xpath_two_arg_test = 33, 
		RULE_xpath_two_arg_type = 34, RULE_xpath_multi_arg_test = 35, RULE_xpath_multi_arg_type = 36, 
		RULE_annotation_directive = 37, RULE_subset_statement = 38, RULE_subset_name = 39, 
		RULE_include_statement = 40;
	public static final String[] ruleNames = {
		"input", "validation_header", "validation_header_type", "validate_statement", 
		"service_name", "validate_directives", "validate_directive", "check_directive", 
		"sub_name", "set_directive", "set_type", "if_directive", "then_clause", 
		"else_clause", "endif", "test_statement", "unchecked_test", "unchecked_test_name", 
		"schema_test", "schema_type", "schema_path", "schema_xpath", "hapifhirvalidator_id", 
		"no_arg_test", "xml_match_source", "xpath_one_arg_test", "xpath_one_arg_comparison_type", 
		"xpath_one_arg_type", "text_match_type", "text_match_source", "http_header_name", 
		"xpath_arg", "xpath_two_arg_comparison_type", "xpath_two_arg_test", "xpath_two_arg_type", 
		"xpath_multi_arg_test", "xpath_multi_arg_type", "annotation_directive", 
		"subset_statement", "subset_name", "include_statement"
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
		"DEFAULT", "ANNOTATION_TEXT", "SP", "CST", "LF"
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
			setState(86); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(86);
				switch (_input.LA(1)) {
				case VALIDATION_RULESET_NAME:
				case VALIDATION_RULESET_VERSION:
				case VALIDATION_RULESET_TIMESTAMP:
				case VALIDATION_RULESET_AUTHOR:
					{
					setState(82);
					validation_header();
					}
					break;
				case VALIDATE:
					{
					setState(83);
					validate_statement();
					}
					break;
				case SUBSET:
					{
					setState(84);
					subset_statement();
					}
					break;
				case IF:
				case INCLUDE:
				case SET:
				case CHECK:
				case ANNOTATION:
					{
					setState(85);
					validate_directive();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(88); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IF || _la==INCLUDE || ((((_la - 68)) & ~0x3f) == 0 && ((1L << (_la - 68)) & ((1L << (VALIDATION_RULESET_NAME - 68)) | (1L << (VALIDATION_RULESET_VERSION - 68)) | (1L << (VALIDATION_RULESET_TIMESTAMP - 68)) | (1L << (VALIDATION_RULESET_AUTHOR - 68)) | (1L << (VALIDATE - 68)) | (1L << (SET - 68)) | (1L << (CHECK - 68)) | (1L << (ANNOTATION - 68)) | (1L << (SUBSET - 68)))) != 0) );
			setState(90);
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
			setState(92);
			validation_header_type();
			setState(93);
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
			setState(95);
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
			setState(97);
			match(VALIDATE);
			setState(98);
			service_name();
			setState(99);
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
			setState(101);
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
			setState(106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(103);
					validate_directive();
					}
					} 
				}
				setState(108);
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
			setState(114);
			switch (_input.LA(1)) {
			case CHECK:
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				check_directive();
				}
				break;
			case SET:
				enterOuterAlt(_localctx, 2);
				{
				setState(110);
				set_directive();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(111);
				if_directive();
				}
				break;
			case ANNOTATION:
				enterOuterAlt(_localctx, 4);
				{
				setState(112);
				annotation_directive();
				}
				break;
			case INCLUDE:
				enterOuterAlt(_localctx, 5);
				{
				setState(113);
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
			setState(116);
			match(CHECK);
			setState(120);
			switch (_input.LA(1)) {
			case SUB:
				{
				{
				setState(117);
				match(SUB);
				setState(118);
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
				setState(119);
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
			setState(123); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(122);
				_la = _input.LA(1);
				if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (IDENTIFIER - 78)) | (1L << (PATH - 78)) | (1L << (XPATH - 78)))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				}
				setState(125); 
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
			setState(127);
			match(SET);
			setState(128);
			set_type();
			setState(129);
			((Set_directiveContext)_localctx).VARIABLE = match(VARIABLE);
			setState(130);
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
			setState(133);
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
			setState(135);
			match(IF);
			setState(136);
			test_statement();
			setState(137);
			match(THEN);
			setState(138);
			then_clause();
			setState(141);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(139);
				match(ELSE);
				setState(140);
				else_clause();
				}
			}

			setState(143);
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
			setState(145);
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
			setState(147);
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
			setState(149);
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
			setState(157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(151);
				no_arg_test();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
				schema_test();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(153);
				xpath_one_arg_test();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(154);
				xpath_two_arg_test();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(155);
				xpath_multi_arg_test();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(156);
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
			setState(159);
			match(UNCHECKED);
			setState(160);
			unchecked_test_name();
			setState(161);
			xpath_arg();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CST) {
				{
				{
				setState(162);
				xpath_arg();
				}
				}
				setState(167);
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
			setState(168);
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
			setState(170);
			schema_type();
			setState(171);
			schema_path();
			setState(173);
			_la = _input.LA(1);
			if (_la==PATH || _la==XPATH) {
				{
				setState(172);
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
			setState(175);
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
			setState(177);
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
			setState(179);
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
			setState(181);
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
			setState(192);
			switch (_input.LA(1)) {
			case SIGNATURE:
				enterOuterAlt(_localctx, 1);
				{
				setState(183);
				match(SIGNATURE);
				}
				break;
			case CDA_RENDERER:
				enterOuterAlt(_localctx, 2);
				{
				setState(184);
				match(CDA_RENDERER);
				}
				break;
			case CDA_TEMPLATE_LIST:
				enterOuterAlt(_localctx, 3);
				{
				setState(185);
				match(CDA_TEMPLATE_LIST);
				}
				break;
			case HAPIFHIRVALIDATOR:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(186);
				match(HAPIFHIRVALIDATOR);
				setState(188);
				_la = _input.LA(1);
				if (_la==INTEGER || _la==IDENTIFIER || _la==DOT_SEPARATED_IDENTIFIER) {
					{
					setState(187);
					hapifhirvalidator_id();
					}
				}

				}
				}
				break;
			case FHIRRESOURCEVALIDATOR:
				enterOuterAlt(_localctx, 5);
				{
				setState(190);
				match(FHIRRESOURCEVALIDATOR);
				}
				break;
			case TERMINOLOGYVALIDATOR:
				enterOuterAlt(_localctx, 6);
				{
				setState(191);
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
			setState(194);
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
			setState(196);
			xpath_one_arg_type();
			setState(197);
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
			setState(199);
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
			setState(210);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(202);
				_la = _input.LA(1);
				if (_la==CONTENT || _la==JWT_PAYLOAD) {
					{
					setState(201);
					xml_match_source();
					}
				}

				setState(204);
				xpath_one_arg_comparison_type();
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(205);
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
				setState(207);
				_la = _input.LA(1);
				if (((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & ((1L << (CONTEXT_PATH - 63)) | (1L << (CONTENT - 63)) | (1L << (HTTP_HEADER - 63)) | (1L << (JWT_PAYLOAD - 63)))) != 0)) {
					{
					setState(206);
					text_match_source();
					}
				}

				setState(209);
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
		enterRule(_localctx, 56, RULE_text_match_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
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
		enterRule(_localctx, 58, RULE_text_match_source);
		int _la;
		try {
			setState(217);
			switch (_input.LA(1)) {
			case CONTEXT_PATH:
			case CONTENT:
			case JWT_PAYLOAD:
				enterOuterAlt(_localctx, 1);
				{
				setState(214);
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
				setState(215);
				match(HTTP_HEADER);
				setState(216);
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
		enterRule(_localctx, 60, RULE_http_header_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
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
		enterRule(_localctx, 62, RULE_xpath_arg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
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
		enterRule(_localctx, 64, RULE_xpath_two_arg_comparison_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
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
		enterRule(_localctx, 66, RULE_xpath_two_arg_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			xpath_two_arg_type();
			setState(226);
			xpath_arg();
			setState(228); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(227);
				xpath_arg();
				}
				}
				setState(230); 
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
		enterRule(_localctx, 68, RULE_xpath_two_arg_type);
		int _la;
		try {
			setState(237);
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
				setState(233);
				_la = _input.LA(1);
				if (_la==CONTENT || _la==JWT_PAYLOAD) {
					{
					setState(232);
					xml_match_source();
					}
				}

				setState(235);
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
				setState(236);
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
		enterRule(_localctx, 70, RULE_xpath_multi_arg_test);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(239);
				xml_match_source();
				}
				break;
			}
			setState(242);
			xpath_multi_arg_type();
			setState(243);
			xpath_arg();
			setState(245); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(244);
				xpath_arg();
				}
				}
				setState(247); 
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
		enterRule(_localctx, 72, RULE_xpath_multi_arg_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			_la = _input.LA(1);
			if (_la==CONTENT || _la==JWT_PAYLOAD) {
				{
				setState(249);
				xml_match_source();
				}
			}

			setState(252);
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
		enterRule(_localctx, 74, RULE_annotation_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			match(ANNOTATION);
			setState(255);
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
		enterRule(_localctx, 76, RULE_subset_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			match(SUBSET);
			setState(258);
			subset_name();
			setState(259);
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
		enterRule(_localctx, 78, RULE_subset_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
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
		enterRule(_localctx, 80, RULE_include_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
			match(INCLUDE);
			setState(264);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3[\u010d\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\3\2\3\2"+
		"\3\2\3\2\6\2Y\n\2\r\2\16\2Z\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3"+
		"\5\3\6\3\6\3\7\7\7k\n\7\f\7\16\7n\13\7\3\b\3\b\3\b\3\b\3\b\5\bu\n\b\3"+
		"\t\3\t\3\t\3\t\5\t{\n\t\3\n\6\n~\n\n\r\n\16\n\177\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u0090\n\r\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u00a0\n\21"+
		"\3\22\3\22\3\22\3\22\7\22\u00a6\n\22\f\22\16\22\u00a9\13\22\3\23\3\23"+
		"\3\24\3\24\3\24\5\24\u00b0\n\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\31\3\31\3\31\5\31\u00bf\n\31\3\31\3\31\5\31\u00c3\n\31\3"+
		"\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\5\35\u00cd\n\35\3\35\3\35\3\35"+
		"\5\35\u00d2\n\35\3\35\5\35\u00d5\n\35\3\36\3\36\3\37\3\37\3\37\5\37\u00dc"+
		"\n\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\6#\u00e7\n#\r#\16#\u00e8\3$\5$\u00ec"+
		"\n$\3$\3$\5$\u00f0\n$\3%\5%\u00f3\n%\3%\3%\3%\6%\u00f8\n%\r%\16%\u00f9"+
		"\3&\5&\u00fd\n&\3&\3&\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3*\3*\3*\3*\2\2+\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJL"+
		"NPR\2\20\3\2FI\5\2\5\5PPRS\4\2PPTU\3\2\r\16\4\2\22\22\24\24\3\2TU\5\2"+
		"\5\5PPRR\4\2BBDD\3\2\33\34\3\2\35\"\3\2#(\4\2ABDD\5\2)*\61\64\67<\5\2"+
		"+\60\65\66=?\u0107\2X\3\2\2\2\4^\3\2\2\2\6a\3\2\2\2\bc\3\2\2\2\ng\3\2"+
		"\2\2\fl\3\2\2\2\16t\3\2\2\2\20v\3\2\2\2\22}\3\2\2\2\24\u0081\3\2\2\2\26"+
		"\u0087\3\2\2\2\30\u0089\3\2\2\2\32\u0093\3\2\2\2\34\u0095\3\2\2\2\36\u0097"+
		"\3\2\2\2 \u009f\3\2\2\2\"\u00a1\3\2\2\2$\u00aa\3\2\2\2&\u00ac\3\2\2\2"+
		"(\u00b1\3\2\2\2*\u00b3\3\2\2\2,\u00b5\3\2\2\2.\u00b7\3\2\2\2\60\u00c2"+
		"\3\2\2\2\62\u00c4\3\2\2\2\64\u00c6\3\2\2\2\66\u00c9\3\2\2\28\u00d4\3\2"+
		"\2\2:\u00d6\3\2\2\2<\u00db\3\2\2\2>\u00dd\3\2\2\2@\u00df\3\2\2\2B\u00e1"+
		"\3\2\2\2D\u00e3\3\2\2\2F\u00ef\3\2\2\2H\u00f2\3\2\2\2J\u00fc\3\2\2\2L"+
		"\u0100\3\2\2\2N\u0103\3\2\2\2P\u0107\3\2\2\2R\u0109\3\2\2\2TY\5\4\3\2"+
		"UY\5\b\5\2VY\5N(\2WY\5\16\b\2XT\3\2\2\2XU\3\2\2\2XV\3\2\2\2XW\3\2\2\2"+
		"YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\]\7\2\2\3]\3\3\2\2\2^_\5\6"+
		"\4\2_`\7X\2\2`\5\3\2\2\2ab\t\2\2\2b\7\3\2\2\2cd\7J\2\2de\5\n\6\2ef\5\f"+
		"\7\2f\t\3\2\2\2gh\t\3\2\2h\13\3\2\2\2ik\5\16\b\2ji\3\2\2\2kn\3\2\2\2l"+
		"j\3\2\2\2lm\3\2\2\2m\r\3\2\2\2nl\3\2\2\2ou\5\20\t\2pu\5\24\13\2qu\5\30"+
		"\r\2ru\5L\'\2su\5R*\2to\3\2\2\2tp\3\2\2\2tq\3\2\2\2tr\3\2\2\2ts\3\2\2"+
		"\2u\17\3\2\2\2vz\7L\2\2wx\7\17\2\2x{\5\22\n\2y{\5 \21\2zw\3\2\2\2zy\3"+
		"\2\2\2{\21\3\2\2\2|~\t\4\2\2}|\3\2\2\2~\177\3\2\2\2\177}\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\23\3\2\2\2\u0081\u0082\7K\2\2\u0082\u0083\5\26\f\2\u0083"+
		"\u0084\7Q\2\2\u0084\u0085\7X\2\2\u0085\u0086\b\13\1\2\u0086\25\3\2\2\2"+
		"\u0087\u0088\t\5\2\2\u0088\27\3\2\2\2\u0089\u008a\7\7\2\2\u008a\u008b"+
		"\5 \21\2\u008b\u008c\7\b\2\2\u008c\u008f\5\32\16\2\u008d\u008e\7\t\2\2"+
		"\u008e\u0090\5\34\17\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0091"+
		"\3\2\2\2\u0091\u0092\5\36\20\2\u0092\31\3\2\2\2\u0093\u0094\5\f\7\2\u0094"+
		"\33\3\2\2\2\u0095\u0096\5\f\7\2\u0096\35\3\2\2\2\u0097\u0098\7\n\2\2\u0098"+
		"\37\3\2\2\2\u0099\u00a0\5\60\31\2\u009a\u00a0\5&\24\2\u009b\u00a0\5\64"+
		"\33\2\u009c\u00a0\5D#\2\u009d\u00a0\5H%\2\u009e\u00a0\5\"\22\2\u009f\u0099"+
		"\3\2\2\2\u009f\u009a\3\2\2\2\u009f\u009b\3\2\2\2\u009f\u009c\3\2\2\2\u009f"+
		"\u009d\3\2\2\2\u009f\u009e\3\2\2\2\u00a0!\3\2\2\2\u00a1\u00a2\7@\2\2\u00a2"+
		"\u00a3\5$\23\2\u00a3\u00a7\5@!\2\u00a4\u00a6\5@!\2\u00a5\u00a4\3\2\2\2"+
		"\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8#\3"+
		"\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ab\7Z\2\2\u00ab%\3\2\2\2\u00ac\u00ad"+
		"\5(\25\2\u00ad\u00af\5*\26\2\u00ae\u00b0\5,\27\2\u00af\u00ae\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00b0\'\3\2\2\2\u00b1\u00b2\t\6\2\2\u00b2)\3\2\2\2\u00b3"+
		"\u00b4\7T\2\2\u00b4+\3\2\2\2\u00b5\u00b6\t\7\2\2\u00b6-\3\2\2\2\u00b7"+
		"\u00b8\t\b\2\2\u00b8/\3\2\2\2\u00b9\u00c3\7\25\2\2\u00ba\u00c3\7\26\2"+
		"\2\u00bb\u00c3\7\27\2\2\u00bc\u00be\7\30\2\2\u00bd\u00bf\5.\30\2\u00be"+
		"\u00bd\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c3\3\2\2\2\u00c0\u00c3\7\31"+
		"\2\2\u00c1\u00c3\7\32\2\2\u00c2\u00b9\3\2\2\2\u00c2\u00ba\3\2\2\2\u00c2"+
		"\u00bb\3\2\2\2\u00c2\u00bc\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c1\3\2"+
		"\2\2\u00c3\61\3\2\2\2\u00c4\u00c5\t\t\2\2\u00c5\63\3\2\2\2\u00c6\u00c7"+
		"\58\35\2\u00c7\u00c8\7Z\2\2\u00c8\65\3\2\2\2\u00c9\u00ca\t\n\2\2\u00ca"+
		"\67\3\2\2\2\u00cb\u00cd\5\62\32\2\u00cc\u00cb\3\2\2\2\u00cc\u00cd\3\2"+
		"\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d5\5\66\34\2\u00cf\u00d5\t\13\2\2\u00d0"+
		"\u00d2\5<\37\2\u00d1\u00d0\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3\u00d5\5:\36\2\u00d4\u00cc\3\2\2\2\u00d4\u00cf\3\2\2\2\u00d4"+
		"\u00d1\3\2\2\2\u00d59\3\2\2\2\u00d6\u00d7\t\f\2\2\u00d7;\3\2\2\2\u00d8"+
		"\u00dc\t\r\2\2\u00d9\u00da\7C\2\2\u00da\u00dc\5> \2\u00db\u00d8\3\2\2"+
		"\2\u00db\u00d9\3\2\2\2\u00dc=\3\2\2\2\u00dd\u00de\7P\2\2\u00de?\3\2\2"+
		"\2\u00df\u00e0\7Z\2\2\u00e0A\3\2\2\2\u00e1\u00e2\t\16\2\2\u00e2C\3\2\2"+
		"\2\u00e3\u00e4\5F$\2\u00e4\u00e6\5@!\2\u00e5\u00e7\5@!\2\u00e6\u00e5\3"+
		"\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9"+
		"E\3\2\2\2\u00ea\u00ec\5\62\32\2\u00eb\u00ea\3\2\2\2\u00eb\u00ec\3\2\2"+
		"\2\u00ec\u00ed\3\2\2\2\u00ed\u00f0\5B\"\2\u00ee\u00f0\t\17\2\2\u00ef\u00eb"+
		"\3\2\2\2\u00ef\u00ee\3\2\2\2\u00f0G\3\2\2\2\u00f1\u00f3\5\62\32\2\u00f2"+
		"\u00f1\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f5\5J"+
		"&\2\u00f5\u00f7\5@!\2\u00f6\u00f8\5@!\2\u00f7\u00f6\3\2\2\2\u00f8\u00f9"+
		"\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00faI\3\2\2\2\u00fb"+
		"\u00fd\5\62\32\2\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00fe\3"+
		"\2\2\2\u00fe\u00ff\7E\2\2\u00ffK\3\2\2\2\u0100\u0101\7M\2\2\u0101\u0102"+
		"\7X\2\2\u0102M\3\2\2\2\u0103\u0104\7N\2\2\u0104\u0105\5P)\2\u0105\u0106"+
		"\5\f\7\2\u0106O\3\2\2\2\u0107\u0108\7P\2\2\u0108Q\3\2\2\2\u0109\u010a"+
		"\7\13\2\2\u010a\u010b\7T\2\2\u010bS\3\2\2\2\30XZltz\177\u008f\u009f\u00a7"+
		"\u00af\u00be\u00c2\u00cc\u00d1\u00d4\u00db\u00e8\u00eb\u00ef\u00f2\u00f9"+
		"\u00fc";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}