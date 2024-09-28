package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.ast.*;

import static plaid.PreludeParser.ConcatExprContext;
import static plaid.PreludeParser.OutputExprContext;
import static plaid.PreludeParser.PlusMinusExprContext;
import static plaid.PreludeParser.SecretExprContext;
import static plaid.PreludeParser.RandomExprContext;
import static plaid.PreludeParser.MessageExprContext;
import static plaid.PreludeParser.PublicExprContext;
import static plaid.PreludeParser.ParenPExprContext;
import static plaid.PreludeParser.FieldSelectExprContext;
import static plaid.PreludeParser.TimesExprContext;
import static plaid.PreludeParser.StrContext;

public class PreludeExpressionVisitor extends PreludeBaseVisitor<PreludeExpression> {

    public static PreludeExpressionVisitor getInstance() {
        return INSTANCE;
    }

    private static final PreludeExpressionVisitor INSTANCE = new PreludeExpressionVisitor();

    private PreludeExpressionVisitor() { /* Empty */ }

    @Override
    public PreludeExpression visitExprFunc(PreludeParser.ExprFuncContext ctx) {
        return super.visitExprFunc(ctx);
    }

    @Override
    public PreludeExpression visitMemExpr(PreludeParser.MemExprContext ctx) {
        return super.visitMemExpr(ctx);
    }

    @Override
    public PreludeExpression visitParenPExpr(ParenPExprContext ctx) {
        return visit(ctx.p_expression());
    }

    @Override
    public PreludeExpression visitEVarExpr(PreludeParser.EVarExprContext ctx) {
        return super.visitEVarExpr(ctx);
    }

    @Override
    public PreludeExpression visitFunctionCallExpr(PreludeParser.FunctionCallExprContext ctx) {
        return super.visitFunctionCallExpr(ctx);
    }

    @Override
    public PreludeExpression visitLetExpr(PreludeParser.LetExprContext ctx) {
        return super.visitLetExpr(ctx);
    }

    @Override
    public PreludeExpression visitAtExpr(PreludeParser.AtExprContext ctx) {
        return super.visitAtExpr(ctx);
    }

    @Override
    public PreludeExpression visitFieldExpr(PreludeParser.FieldExprContext ctx) {
        return super.visitFieldExpr(ctx);
    }

    @Override
    public PreludeExpression visitFieldSelectExpr(FieldSelectExprContext ctx) {
        return null;//new FieldSelectExpr(visit(ctx.p_expression()), visit(ctx.l().));
    }

    @Override
    public SecretExpr visitSecretExpr(SecretExprContext ctx) {
        return null;
    }

    @Override
    public RandomExpr visitRandomExpr(RandomExprContext ctx) {
        return null;
    }

    @Override
    public MessageExpr visitMessageExpr(MessageExprContext ctx) {
        return null;
    }

    @Override
    public PublicExpr visitPublicExpr(PublicExprContext ctx) {
        return new PublicExpr(visit(ctx.index().p_expression()));
    }

    @Override
    public OutputExpr visitOutputExpr(OutputExprContext ctx) {
        return null;
    }

    @Override
    public PreludeExpression visitPlusMinusExpr(PlusMinusExprContext ctx) {
        PreludeExpression e0 = visit(ctx.p_expression(0));
        PreludeExpression e1 = visit(ctx.p_expression(1));
        return ctx.pmop().getText().equals("+") ? new PlusExpr(e0, e1) : new MinusExpr(e0, e1);
    }

    @Override
    public ConcatExpr visitConcatExpr(ConcatExprContext ctx) {
        return new ConcatExpr(visit(ctx.p_expression(0)), visit(ctx.p_expression(1)));
    }

    @Override
    public TimesExpr visitTimesExpr(TimesExprContext ctx) {
        return new TimesExpr(visit(ctx.p_expression(0)), visit(ctx.p_expression(1)));
    }

    @Override
    public Str visitStr(StrContext ctx) {
        return new Str(ctx.getText().replaceAll("\"", ""));
    }

    @Override
    public PreludeExpression visitValueExpr(PreludeParser.ValueExprContext ctx) {
        return super.visitValueExpr(ctx);
    }

    @Override
    public PreludeExpression visitNum(PreludeParser.NumContext ctx) {
        return new Num(Integer.parseInt(ctx.getText()));
    }
}
