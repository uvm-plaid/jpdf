// Generated from /home/yyeh/jpdf/code/cvc5mvn/src/main/antlr4/plaid/Constraints.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConstraintsParser}.
 */
public interface ConstraintsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ConstraintsParser#constraints}.
	 * @param ctx the parse tree
	 */
	void enterConstraints(ConstraintsParser.ConstraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConstraintsParser#constraints}.
	 * @param ctx the parse tree
	 */
	void exitConstraints(ConstraintsParser.ConstraintsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void enterNotConstraintsExpr(ConstraintsParser.NotConstraintsExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void exitNotConstraintsExpr(ConstraintsParser.NotConstraintsExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void enterParenConstraintsExpr(ConstraintsParser.ParenConstraintsExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void exitParenConstraintsExpr(ConstraintsParser.ParenConstraintsExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AndConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndConstraintsExpr(ConstraintsParser.AndConstraintsExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AndConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndConstraintsExpr(ConstraintsParser.AndConstraintsExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqualConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqualConstraintsExpr(ConstraintsParser.EqualConstraintsExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqualConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqualConstraintsExpr(ConstraintsParser.EqualConstraintsExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PublicConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterPublicConstraintsTerm(ConstraintsParser.PublicConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PublicConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitPublicConstraintsTerm(ConstraintsParser.PublicConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MinusConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterMinusConstraintsTerm(ConstraintsParser.MinusConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MinusConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitMinusConstraintsTerm(ConstraintsParser.MinusConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SecretConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterSecretConstraintsTerm(ConstraintsParser.SecretConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SecretConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitSecretConstraintsTerm(ConstraintsParser.SecretConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PlusConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterPlusConstraintsTerm(ConstraintsParser.PlusConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PlusConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitPlusConstraintsTerm(ConstraintsParser.PlusConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OutputConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterOutputConstraintsTerm(ConstraintsParser.OutputConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OutputConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitOutputConstraintsTerm(ConstraintsParser.OutputConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MessageConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterMessageConstraintsTerm(ConstraintsParser.MessageConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MessageConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitMessageConstraintsTerm(ConstraintsParser.MessageConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TimesConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterTimesConstraintsTerm(ConstraintsParser.TimesConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TimesConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitTimesConstraintsTerm(ConstraintsParser.TimesConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterParenConstraintsTerm(ConstraintsParser.ParenConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitParenConstraintsTerm(ConstraintsParser.ParenConstraintsTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RandomConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void enterRandomConstraintsTerm(ConstraintsParser.RandomConstraintsTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RandomConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 */
	void exitRandomConstraintsTerm(ConstraintsParser.RandomConstraintsTermContext ctx);
}