// Generated from /home/yyeh/jpdf/code/cvc5mvn/src/main/antlr4/plaid/Constraints.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ConstraintsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		STRING=18, VALUE=19, WS=20;
	public static final int
		RULE_constraints = 0, RULE_constraintsExpr = 1, RULE_constraintsTerm = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"constraints", "constraintsExpr", "constraintsTerm"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'constraints:'", "'('", "')'", "'=='", "'AND'", "'NOT'", "'*'", 
			"'+'", "'-'", "'s'", "'['", "']'", "'@'", "'r'", "'m'", "'p'", "'out'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "STRING", "VALUE", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String getGrammarFileName() { return "Constraints.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ConstraintsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstraintsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(ConstraintsParser.EOF, 0); }
		public List<ConstraintsExprContext> constraintsExpr() {
			return getRuleContexts(ConstraintsExprContext.class);
		}
		public ConstraintsExprContext constraintsExpr(int i) {
			return getRuleContext(ConstraintsExprContext.class,i);
		}
		public ConstraintsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraints; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterConstraints(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitConstraints(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitConstraints(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintsContext constraints() throws RecognitionException {
		ConstraintsContext _localctx = new ConstraintsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_constraints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			match(T__0);
			setState(10);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 247364L) != 0)) {
				{
				{
				setState(7);
				constraintsExpr(0);
				}
				}
				setState(12);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(13);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ConstraintsExprContext extends ParserRuleContext {
		public ConstraintsExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintsExpr; }
	 
		public ConstraintsExprContext() { }
		public void copyFrom(ConstraintsExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotConstraintsExprContext extends ConstraintsExprContext {
		public ConstraintsExprContext constraintsExpr() {
			return getRuleContext(ConstraintsExprContext.class,0);
		}
		public NotConstraintsExprContext(ConstraintsExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterNotConstraintsExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitNotConstraintsExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitNotConstraintsExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenConstraintsExprContext extends ConstraintsExprContext {
		public ConstraintsExprContext constraintsExpr() {
			return getRuleContext(ConstraintsExprContext.class,0);
		}
		public ParenConstraintsExprContext(ConstraintsExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterParenConstraintsExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitParenConstraintsExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitParenConstraintsExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AndConstraintsExprContext extends ConstraintsExprContext {
		public List<ConstraintsExprContext> constraintsExpr() {
			return getRuleContexts(ConstraintsExprContext.class);
		}
		public ConstraintsExprContext constraintsExpr(int i) {
			return getRuleContext(ConstraintsExprContext.class,i);
		}
		public AndConstraintsExprContext(ConstraintsExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterAndConstraintsExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitAndConstraintsExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitAndConstraintsExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EqualConstraintsExprContext extends ConstraintsExprContext {
		public List<ConstraintsTermContext> constraintsTerm() {
			return getRuleContexts(ConstraintsTermContext.class);
		}
		public ConstraintsTermContext constraintsTerm(int i) {
			return getRuleContext(ConstraintsTermContext.class,i);
		}
		public EqualConstraintsExprContext(ConstraintsExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterEqualConstraintsExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitEqualConstraintsExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitEqualConstraintsExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintsExprContext constraintsExpr() throws RecognitionException {
		return constraintsExpr(0);
	}

	private ConstraintsExprContext constraintsExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConstraintsExprContext _localctx = new ConstraintsExprContext(_ctx, _parentState);
		ConstraintsExprContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_constraintsExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				_localctx = new ParenConstraintsExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(16);
				match(T__1);
				setState(17);
				constraintsExpr(0);
				setState(18);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new EqualConstraintsExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(20);
				constraintsTerm(0);
				setState(21);
				match(T__3);
				setState(22);
				constraintsTerm(0);
				}
				break;
			case 3:
				{
				_localctx = new NotConstraintsExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(24);
				match(T__5);
				setState(25);
				constraintsExpr(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(33);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AndConstraintsExprContext(new ConstraintsExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_constraintsExpr);
					setState(28);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(29);
					match(T__4);
					setState(30);
					constraintsExpr(3);
					}
					} 
				}
				setState(35);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ConstraintsTermContext extends ParserRuleContext {
		public ConstraintsTermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintsTerm; }
	 
		public ConstraintsTermContext() { }
		public void copyFrom(ConstraintsTermContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PublicConstraintsTermContext extends ConstraintsTermContext {
		public TerminalNode STRING() { return getToken(ConstraintsParser.STRING, 0); }
		public PublicConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterPublicConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitPublicConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitPublicConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MinusConstraintsTermContext extends ConstraintsTermContext {
		public ConstraintsTermContext constraintsTerm() {
			return getRuleContext(ConstraintsTermContext.class,0);
		}
		public MinusConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterMinusConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitMinusConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitMinusConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SecretConstraintsTermContext extends ConstraintsTermContext {
		public TerminalNode STRING() { return getToken(ConstraintsParser.STRING, 0); }
		public TerminalNode VALUE() { return getToken(ConstraintsParser.VALUE, 0); }
		public SecretConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterSecretConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitSecretConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitSecretConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlusConstraintsTermContext extends ConstraintsTermContext {
		public List<ConstraintsTermContext> constraintsTerm() {
			return getRuleContexts(ConstraintsTermContext.class);
		}
		public ConstraintsTermContext constraintsTerm(int i) {
			return getRuleContext(ConstraintsTermContext.class,i);
		}
		public PlusConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterPlusConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitPlusConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitPlusConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OutputConstraintsTermContext extends ConstraintsTermContext {
		public TerminalNode VALUE() { return getToken(ConstraintsParser.VALUE, 0); }
		public OutputConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterOutputConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitOutputConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitOutputConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MessageConstraintsTermContext extends ConstraintsTermContext {
		public TerminalNode STRING() { return getToken(ConstraintsParser.STRING, 0); }
		public TerminalNode VALUE() { return getToken(ConstraintsParser.VALUE, 0); }
		public MessageConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterMessageConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitMessageConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitMessageConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TimesConstraintsTermContext extends ConstraintsTermContext {
		public List<ConstraintsTermContext> constraintsTerm() {
			return getRuleContexts(ConstraintsTermContext.class);
		}
		public ConstraintsTermContext constraintsTerm(int i) {
			return getRuleContext(ConstraintsTermContext.class,i);
		}
		public TimesConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterTimesConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitTimesConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitTimesConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenConstraintsTermContext extends ConstraintsTermContext {
		public ConstraintsTermContext constraintsTerm() {
			return getRuleContext(ConstraintsTermContext.class,0);
		}
		public ParenConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterParenConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitParenConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitParenConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RandomConstraintsTermContext extends ConstraintsTermContext {
		public TerminalNode STRING() { return getToken(ConstraintsParser.STRING, 0); }
		public TerminalNode VALUE() { return getToken(ConstraintsParser.VALUE, 0); }
		public RandomConstraintsTermContext(ConstraintsTermContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).enterRandomConstraintsTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConstraintsListener ) ((ConstraintsListener)listener).exitRandomConstraintsTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ConstraintsVisitor ) return ((ConstraintsVisitor<? extends T>)visitor).visitRandomConstraintsTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintsTermContext constraintsTerm() throws RecognitionException {
		return constraintsTerm(0);
	}

	private ConstraintsTermContext constraintsTerm(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConstraintsTermContext _localctx = new ConstraintsTermContext(_ctx, _parentState);
		ConstraintsTermContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_constraintsTerm, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				_localctx = new ParenConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(37);
				match(T__1);
				setState(38);
				constraintsTerm(0);
				setState(39);
				match(T__2);
				}
				break;
			case T__8:
				{
				_localctx = new MinusConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(41);
				match(T__8);
				setState(42);
				constraintsTerm(6);
				}
				break;
			case T__9:
				{
				_localctx = new SecretConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(43);
				match(T__9);
				setState(44);
				match(T__10);
				setState(45);
				match(STRING);
				setState(46);
				match(T__11);
				setState(47);
				match(T__12);
				setState(48);
				match(VALUE);
				}
				break;
			case T__13:
				{
				_localctx = new RandomConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(49);
				match(T__13);
				setState(50);
				match(T__10);
				setState(51);
				match(STRING);
				setState(52);
				match(T__11);
				setState(53);
				match(T__12);
				setState(54);
				match(VALUE);
				}
				break;
			case T__14:
				{
				_localctx = new MessageConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(55);
				match(T__14);
				setState(56);
				match(T__10);
				setState(57);
				match(STRING);
				setState(58);
				match(T__11);
				setState(59);
				match(T__12);
				setState(60);
				match(VALUE);
				}
				break;
			case T__15:
				{
				_localctx = new PublicConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(61);
				match(T__15);
				setState(62);
				match(T__10);
				setState(63);
				match(STRING);
				setState(64);
				match(T__11);
				}
				break;
			case T__16:
				{
				_localctx = new OutputConstraintsTermContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(65);
				match(T__16);
				setState(66);
				match(T__12);
				setState(67);
				match(VALUE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(78);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(76);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						_localctx = new TimesConstraintsTermContext(new ConstraintsTermContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_constraintsTerm);
						setState(70);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(71);
						match(T__6);
						setState(72);
						constraintsTerm(9);
						}
						break;
					case 2:
						{
						_localctx = new PlusConstraintsTermContext(new ConstraintsTermContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_constraintsTerm);
						setState(73);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(74);
						match(T__7);
						setState(75);
						constraintsTerm(8);
						}
						break;
					}
					} 
				}
				setState(80);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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
		case 1:
			return constraintsExpr_sempred((ConstraintsExprContext)_localctx, predIndex);
		case 2:
			return constraintsTerm_sempred((ConstraintsTermContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean constraintsExpr_sempred(ConstraintsExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean constraintsTerm_sempred(ConstraintsTermContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0014R\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0001\u0000\u0001\u0000\u0005\u0000\t\b\u0000\n\u0000"+
		"\f\u0000\f\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u001b\b\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0005\u0001 \b\u0001\n\u0001\f\u0001#\t\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0003\u0002E\b\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002M\b\u0002\n\u0002"+
		"\f\u0002P\t\u0002\u0001\u0002\u0000\u0002\u0002\u0004\u0003\u0000\u0002"+
		"\u0004\u0000\u0000Z\u0000\u0006\u0001\u0000\u0000\u0000\u0002\u001a\u0001"+
		"\u0000\u0000\u0000\u0004D\u0001\u0000\u0000\u0000\u0006\n\u0005\u0001"+
		"\u0000\u0000\u0007\t\u0003\u0002\u0001\u0000\b\u0007\u0001\u0000\u0000"+
		"\u0000\t\f\u0001\u0000\u0000\u0000\n\b\u0001\u0000\u0000\u0000\n\u000b"+
		"\u0001\u0000\u0000\u0000\u000b\r\u0001\u0000\u0000\u0000\f\n\u0001\u0000"+
		"\u0000\u0000\r\u000e\u0005\u0000\u0000\u0001\u000e\u0001\u0001\u0000\u0000"+
		"\u0000\u000f\u0010\u0006\u0001\uffff\uffff\u0000\u0010\u0011\u0005\u0002"+
		"\u0000\u0000\u0011\u0012\u0003\u0002\u0001\u0000\u0012\u0013\u0005\u0003"+
		"\u0000\u0000\u0013\u001b\u0001\u0000\u0000\u0000\u0014\u0015\u0003\u0004"+
		"\u0002\u0000\u0015\u0016\u0005\u0004\u0000\u0000\u0016\u0017\u0003\u0004"+
		"\u0002\u0000\u0017\u001b\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0006"+
		"\u0000\u0000\u0019\u001b\u0003\u0002\u0001\u0001\u001a\u000f\u0001\u0000"+
		"\u0000\u0000\u001a\u0014\u0001\u0000\u0000\u0000\u001a\u0018\u0001\u0000"+
		"\u0000\u0000\u001b!\u0001\u0000\u0000\u0000\u001c\u001d\n\u0002\u0000"+
		"\u0000\u001d\u001e\u0005\u0005\u0000\u0000\u001e \u0003\u0002\u0001\u0003"+
		"\u001f\u001c\u0001\u0000\u0000\u0000 #\u0001\u0000\u0000\u0000!\u001f"+
		"\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\"\u0003\u0001\u0000"+
		"\u0000\u0000#!\u0001\u0000\u0000\u0000$%\u0006\u0002\uffff\uffff\u0000"+
		"%&\u0005\u0002\u0000\u0000&\'\u0003\u0004\u0002\u0000\'(\u0005\u0003\u0000"+
		"\u0000(E\u0001\u0000\u0000\u0000)*\u0005\t\u0000\u0000*E\u0003\u0004\u0002"+
		"\u0006+,\u0005\n\u0000\u0000,-\u0005\u000b\u0000\u0000-.\u0005\u0012\u0000"+
		"\u0000./\u0005\f\u0000\u0000/0\u0005\r\u0000\u00000E\u0005\u0013\u0000"+
		"\u000012\u0005\u000e\u0000\u000023\u0005\u000b\u0000\u000034\u0005\u0012"+
		"\u0000\u000045\u0005\f\u0000\u000056\u0005\r\u0000\u00006E\u0005\u0013"+
		"\u0000\u000078\u0005\u000f\u0000\u000089\u0005\u000b\u0000\u00009:\u0005"+
		"\u0012\u0000\u0000:;\u0005\f\u0000\u0000;<\u0005\r\u0000\u0000<E\u0005"+
		"\u0013\u0000\u0000=>\u0005\u0010\u0000\u0000>?\u0005\u000b\u0000\u0000"+
		"?@\u0005\u0012\u0000\u0000@E\u0005\f\u0000\u0000AB\u0005\u0011\u0000\u0000"+
		"BC\u0005\r\u0000\u0000CE\u0005\u0013\u0000\u0000D$\u0001\u0000\u0000\u0000"+
		"D)\u0001\u0000\u0000\u0000D+\u0001\u0000\u0000\u0000D1\u0001\u0000\u0000"+
		"\u0000D7\u0001\u0000\u0000\u0000D=\u0001\u0000\u0000\u0000DA\u0001\u0000"+
		"\u0000\u0000EN\u0001\u0000\u0000\u0000FG\n\b\u0000\u0000GH\u0005\u0007"+
		"\u0000\u0000HM\u0003\u0004\u0002\tIJ\n\u0007\u0000\u0000JK\u0005\b\u0000"+
		"\u0000KM\u0003\u0004\u0002\bLF\u0001\u0000\u0000\u0000LI\u0001\u0000\u0000"+
		"\u0000MP\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000"+
		"\u0000\u0000O\u0005\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000"+
		"\u0006\n\u001a!DLN";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}