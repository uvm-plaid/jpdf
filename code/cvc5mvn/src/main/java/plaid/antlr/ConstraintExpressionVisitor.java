package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.PreludeParser.FunctionCallConstraintExprContext;
import plaid.ast.*;

public class ConstraintExpressionVisitor extends PreludeBaseVisitor<Constraint> {

    @Override
    public Constraint visitParenConstraintExpr(PreludeParser.ParenConstraintExprContext ctx) {
        return visit(ctx.constraintExpr());
    }

    @Override
    public Constraint visitAndConstraintExpr(PreludeParser.AndConstraintExprContext ctx) {
        return new AndConstraint(visit(ctx.constraintExpr(0)), visit(ctx.constraintExpr(1)));
    }

    @Override
    public Constraint visitNotConstraintExpr(PreludeParser.NotConstraintExprContext ctx) {
        return new NotConstraint(visit(ctx.constraintExpr()));
    }

    @Override
    public Constraint visitEqualConstraintExpr(PreludeParser.EqualConstraintExprContext ctx) {
        return new EqualConstraint(Loader.toExpression(ctx.expr(0)), Loader.toExpression(ctx.expr(1)));
    }

    @Override
    public Constraint visitTrueConstraintExpr(PreludeParser.TrueConstraintExprContext ctx) { return new TrueConstraint();}

    @Override
    public Constraint visitFunctionCallConstraintExpr(FunctionCallConstraintExprContext ctx) {
        return new FunctionCall(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(Loader::toExpression).toList());
    }

}
