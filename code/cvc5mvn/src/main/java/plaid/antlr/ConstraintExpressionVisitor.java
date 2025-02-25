package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.PreludeParser.FunctionCallConstraintExprContext;
import plaid.PreludeParser.IdentConstraintExprContext;
import plaid.ast.AndConstraintExpr;
import plaid.ast.ConstraintExpr;
import plaid.ast.EqualConstraintExpr;
import plaid.ast.FunctionCallExpr;
import plaid.ast.Identifier;
import plaid.ast.NotConstraintExpr;

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
    public Identifier visitIdentConstraintExpr(IdentConstraintExprContext ctx) {
        return new Identifier(ctx.getText());
    }

    @Override
    public ConstraintExpr visitFunctionCallConstraintExpr(FunctionCallConstraintExprContext ctx) {
        return new FunctionCallExpr(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(Loader::toExpression).toList());
    }

}
