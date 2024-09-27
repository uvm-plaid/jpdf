package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.ast.*;

import static plaid.PreludeParser.ConcatExprContext;
import static plaid.PreludeParser.MinusExprContext;
import static plaid.PreludeParser.OutputExprContext;
import static plaid.PreludeParser.PlusExprContext;
import static plaid.PreludeParser.SecretExprContext;
import static plaid.PreludeParser.RandomExprContext;
import static plaid.PreludeParser.MessageExprContext;
import static plaid.PreludeParser.PublicExprContext;
import static plaid.PreludeParser.ParenPExprContext;
import static plaid.PreludeParser.FieldSelectExprContext;
import static plaid.PreludeParser.TimesExprContext;

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
        return new SecretExpr(visit(ctx.index().p_expression()));
    }

    @Override
    public RandomExpr visitRandomExpr(RandomExprContext ctx) {
        return new RandomExpr(visit(ctx.index().p_expression()));
    }

    @Override
    public MessageExpr visitMessageExpr(MessageExprContext ctx) {
        return new MessageExpr(visit(ctx.index().p_expression()));
    }

    @Override
    public PublicExpr visitPublicExpr(PublicExprContext ctx) {
        return new PublicExpr(visit(ctx.index().p_expression()));
    }

    @Override
    public OutputExpr visitOutputExpr(OutputExprContext ctx) {
        return new OutputExpr();
    }

    @Override
    public PlusExpr visitPlusExpr(PlusExprContext ctx) {
        return new PlusExpr(visit(ctx.p_expression(0)), visit(ctx.p_expression(1)));
    }

    @Override
    public MinusExpr visitMinusExpr(MinusExprContext ctx) {
        return new MinusExpr(visit(ctx.p_expression(0)), visit(ctx.p_expression(1)));
    }

    @Override
    public ConcatExpr visitConcatExpr(ConcatExprContext ctx) {
        return new ConcatExpr(visit(ctx.p_expression(0)), visit(ctx.p_expression(1)));
    }

    @Override
    public TimesExpr visitTimesExpr(TimesExprContext ctx) {
        return new TimesExpr(visit(ctx.p_expression(0)), visit(ctx.p_expression(1)));
    }

}
