package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.ast.Num;
import plaid.ast.PreludeExpression;
import plaid.ast.PlusExpr;

public class PExpressionVisitor extends PreludeBaseVisitor<PreludeExpression> {

    @Override
    public PreludeExpression visitPlusOp(PreludeParser.PlusOpContext ctx) {
        return new PlusExpr(visit(ctx.getChild(0)), visit(ctx.getChild(1)));
    }

    @Override
    public PreludeExpression visitValueVal(PreludeParser.ValueValContext ctx) {
        return new Num(Integer.parseInt(ctx.getText()));
    }
}
