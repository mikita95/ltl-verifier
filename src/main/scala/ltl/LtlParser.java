// Generated from /home/nikita/development/ltl-verifier/src/main/antlr4/Ltl.g4 by ANTLR 4.7
package ltl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LtlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, BooleanLiteral=12, ID=13, WS=14;
	public static final int
		RULE_formula = 0;
	public static final String[] ruleNames = {
		"formula"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'!'", "'X'", "'F'", "'G'", "'U'", "'R'", "'&'", "'|'", 
		"'->'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"BooleanLiteral", "ID", "WS"
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
	public String getGrammarFileName() { return "Ltl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LtlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FormulaContext extends ParserRuleContext {
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
	 
		public FormulaContext() { }
		public void copyFrom(FormulaContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NextContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public NextContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterNext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitNext(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitNext(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegationContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public NegationContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterNegation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitNegation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitNegation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConjunctionContext extends FormulaContext {
		public FormulaContext lhs;
		public FormulaContext rhs;
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public ConjunctionContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterConjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitConjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitConjunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DisjunctionContext extends FormulaContext {
		public FormulaContext lhs;
		public FormulaContext rhs;
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public DisjunctionContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterDisjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitDisjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitDisjunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FutureContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public FutureContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterFuture(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitFuture(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitFuture(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GloballyContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public GloballyContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterGlobally(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitGlobally(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitGlobally(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReleaseContext extends FormulaContext {
		public FormulaContext lhs;
		public FormulaContext rhs;
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public ReleaseContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterRelease(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitRelease(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitRelease(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ImplicationContext extends FormulaContext {
		public FormulaContext lhs;
		public FormulaContext rhs;
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public ImplicationContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterImplication(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitImplication(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitImplication(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableContext extends FormulaContext {
		public TerminalNode ID() { return getToken(LtlParser.ID, 0); }
		public VariableContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UntilContext extends FormulaContext {
		public FormulaContext lhs;
		public FormulaContext rhs;
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public UntilContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterUntil(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitUntil(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitUntil(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesisContext extends FormulaContext {
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public ParenthesisContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterParenthesis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitParenthesis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitParenthesis(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanLiteralContext extends FormulaContext {
		public TerminalNode BooleanLiteral() { return getToken(LtlParser.BooleanLiteral, 0); }
		public BooleanLiteralContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LtlListener ) ((LtlListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LtlVisitor ) return ((LtlVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		return formula(0);
	}

	private FormulaContext formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FormulaContext _localctx = new FormulaContext(_ctx, _parentState);
		FormulaContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				{
				_localctx = new ParenthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(3);
				match(T__0);
				setState(4);
				formula(0);
				setState(5);
				match(T__1);
				}
				break;
			case T__2:
				{
				_localctx = new NegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(7);
				match(T__2);
				setState(8);
				formula(11);
				}
				break;
			case T__3:
				{
				_localctx = new NextContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(9);
				match(T__3);
				setState(10);
				formula(10);
				}
				break;
			case T__4:
				{
				_localctx = new FutureContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(11);
				match(T__4);
				setState(12);
				formula(9);
				}
				break;
			case T__5:
				{
				_localctx = new GloballyContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(13);
				match(T__5);
				setState(14);
				formula(8);
				}
				break;
			case ID:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(15);
				match(ID);
				}
				break;
			case BooleanLiteral:
				{
				_localctx = new BooleanLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(16);
				match(BooleanLiteral);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(36);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(34);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new UntilContext(new FormulaContext(_parentctx, _parentState));
						((UntilContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(19);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(20);
						match(T__6);
						setState(21);
						((UntilContext)_localctx).rhs = formula(8);
						}
						break;
					case 2:
						{
						_localctx = new ReleaseContext(new FormulaContext(_parentctx, _parentState));
						((ReleaseContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(22);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(23);
						match(T__7);
						setState(24);
						((ReleaseContext)_localctx).rhs = formula(7);
						}
						break;
					case 3:
						{
						_localctx = new ConjunctionContext(new FormulaContext(_parentctx, _parentState));
						((ConjunctionContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(25);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(26);
						match(T__8);
						setState(27);
						((ConjunctionContext)_localctx).rhs = formula(6);
						}
						break;
					case 4:
						{
						_localctx = new DisjunctionContext(new FormulaContext(_parentctx, _parentState));
						((DisjunctionContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(28);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(29);
						match(T__9);
						setState(30);
						((DisjunctionContext)_localctx).rhs = formula(5);
						}
						break;
					case 5:
						{
						_localctx = new ImplicationContext(new FormulaContext(_parentctx, _parentState));
						((ImplicationContext)_localctx).lhs = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(31);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(32);
						match(T__10);
						setState(33);
						((ImplicationContext)_localctx).rhs = formula(4);
						}
						break;
					}
					} 
				}
				setState(38);
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
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return formula_sempred((FormulaContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean formula_sempred(FormulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 7);
		case 1:
			return precpred(_ctx, 6);
		case 2:
			return precpred(_ctx, 5);
		case 3:
			return precpred(_ctx, 4);
		case 4:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\20*\4\2\t\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\24\n\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2%\n\2\f\2"+
		"\16\2(\13\2\3\2\2\3\2\3\2\2\2\2\63\2\23\3\2\2\2\4\5\b\2\1\2\5\6\7\3\2"+
		"\2\6\7\5\2\2\2\7\b\7\4\2\2\b\24\3\2\2\2\t\n\7\5\2\2\n\24\5\2\2\r\13\f"+
		"\7\6\2\2\f\24\5\2\2\f\r\16\7\7\2\2\16\24\5\2\2\13\17\20\7\b\2\2\20\24"+
		"\5\2\2\n\21\24\7\17\2\2\22\24\7\16\2\2\23\4\3\2\2\2\23\t\3\2\2\2\23\13"+
		"\3\2\2\2\23\r\3\2\2\2\23\17\3\2\2\2\23\21\3\2\2\2\23\22\3\2\2\2\24&\3"+
		"\2\2\2\25\26\f\t\2\2\26\27\7\t\2\2\27%\5\2\2\n\30\31\f\b\2\2\31\32\7\n"+
		"\2\2\32%\5\2\2\t\33\34\f\7\2\2\34\35\7\13\2\2\35%\5\2\2\b\36\37\f\6\2"+
		"\2\37 \7\f\2\2 %\5\2\2\7!\"\f\5\2\2\"#\7\r\2\2#%\5\2\2\6$\25\3\2\2\2$"+
		"\30\3\2\2\2$\33\3\2\2\2$\36\3\2\2\2$!\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'\3"+
		"\2\2\2\'\3\3\2\2\2(&\3\2\2\2\5\23$&";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}