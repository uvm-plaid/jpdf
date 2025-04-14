package plaid.antlr;

import io.github.cvc5.TermManager;
import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.PreludeParser.FunctionCallConstraintExprContext;
import plaid.ast.*;

public class ConstraintExpressionVisitor extends PreludeBaseVisitor<ConstraintExpr> {

    @Override
    public ConstraintExpr visitParenConstraintExpr(PreludeParser.ParenConstraintExprContext ctx) {
        return visit(ctx.constraintExpr());
    }

    @Override
    public ConstraintExpr visitAndConstraintExpr(PreludeParser.AndConstraintExprContext ctx) {
        return new AndConstraintExpr(visit(ctx.constraintExpr(0)), visit(ctx.constraintExpr(1)));
    }

    @Override
    public ConstraintExpr visitNotConstraintExpr(PreludeParser.NotConstraintExprContext ctx) {
        return new NotConstraintExpr(visit(ctx.constraintExpr()));
    }

    @Override
    public ConstraintExpr visitEqualConstraintExpr(PreludeParser.EqualConstraintExprContext ctx) {
        return new EqualConstraintExpr(Loader.toExpression(ctx.expr(0)), Loader.toExpression(ctx.expr(1)));
    }

    @Override
    public ConstraintExpr visitTrueConstraintExpr(PreludeParser.TrueConstraintExprContext ctx) { return new TrueConstraintExpr();}

    @Override
    public ConstraintExpr visitFunctionCallConstraintExpr(FunctionCallConstraintExprContext ctx) {
        return new FunctionCallExpr(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(Loader::toExpression).toList());
    }

}
