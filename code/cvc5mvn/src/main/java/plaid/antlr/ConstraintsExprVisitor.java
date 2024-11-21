package plaid.antlr;

import plaid.ConstraintsBaseVisitor;
import plaid.ConstraintsParser;
import plaid.constraints.ast.*;

public class ConstraintsExprVisitor extends ConstraintsBaseVisitor<ConstraintsExpr>{

    @Override
    public ConstraintsExpr visitEqualConstraintsExpr(ConstraintsParser.EqualConstraintsExprContext ctx) {
        return new EqualConstraintsExpr(ConstraintsLoader.toTerm(ctx.constraintsTerm(0)), ConstraintsLoader.toTerm(ctx.constraintsTerm(1)));
    }

    @Override
    public ConstraintsExpr visitNotConstraintsExpr(ConstraintsParser.NotConstraintsExprContext ctx) {
        return new NotConstraintsExpr(visit(ctx.constraintsExpr()));
    }

    @Override
    public ConstraintsExpr visitAndConstraintsExpr(ConstraintsParser.AndConstraintsExprContext ctx) {
        return new AndConstraintsExpr(visit(ctx.constraintsExpr(0)), visit(ctx.constraintsExpr(1)));
    }

    @Override
    public ConstraintsExpr visitParenConstraintsExpr(ConstraintsParser.ParenConstraintsExprContext ctx) {
        return visit(ctx.constraintsExpr());
    }
}
