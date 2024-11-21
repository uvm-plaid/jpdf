// Generated from /home/yyeh/jpdf/code/cvc5mvn/src/main/antlr4/plaid/Constraints.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ConstraintsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ConstraintsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ConstraintsParser#constraints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraints(ConstraintsParser.ConstraintsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotConstraintsExpr(ConstraintsParser.NotConstraintsExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenConstraintsExpr(ConstraintsParser.ParenConstraintsExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AndConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndConstraintsExpr(ConstraintsParser.AndConstraintsExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqualConstraintsExpr}
	 * labeled alternative in {@link ConstraintsParser#constraintsExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualConstraintsExpr(ConstraintsParser.EqualConstraintsExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PublicConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPublicConstraintsTerm(ConstraintsParser.PublicConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MinusConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinusConstraintsTerm(ConstraintsParser.MinusConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SecretConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSecretConstraintsTerm(ConstraintsParser.SecretConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PlusConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlusConstraintsTerm(ConstraintsParser.PlusConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OutputConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputConstraintsTerm(ConstraintsParser.OutputConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MessageConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageConstraintsTerm(ConstraintsParser.MessageConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TimesConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimesConstraintsTerm(ConstraintsParser.TimesConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenConstraintsTerm(ConstraintsParser.ParenConstraintsTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RandomConstraintsTerm}
	 * labeled alternative in {@link ConstraintsParser#constraintsTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRandomConstraintsTerm(ConstraintsParser.RandomConstraintsTermContext ctx);
}