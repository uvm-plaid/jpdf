package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.ast.ConcatExpr;
import plaid.ast.FieldSelectExpr;
import plaid.ast.Identifier;
import plaid.ast.MessageExpr;
import plaid.ast.MinusExpr;
import plaid.ast.Num;
import plaid.ast.OutputExpr;
import plaid.ast.PlusExpr;
import plaid.ast.PreludeExpression;
import plaid.ast.PublicExpr;
import plaid.ast.RandomExpr;
import plaid.ast.SecretExpr;
import plaid.ast.Str;
import plaid.ast.TimesExpr;

import java.util.Stack;

import static plaid.PreludeParser.AtExprContext;
import static plaid.PreludeParser.FieldExprContext;
import static plaid.PreludeParser.LetExprContext;
import static plaid.PreludeParser.IdentExprContext;
import static plaid.PreludeParser.ConcatExprContext;
import static plaid.PreludeParser.FieldSelectExprContext;
import static plaid.PreludeParser.MessageExprContext;
import static plaid.PreludeParser.OutputExprContext;
import static plaid.PreludeParser.ParenPExprContext;
import static plaid.PreludeParser.PlusMinusExprContext;
import static plaid.PreludeParser.PublicExprContext;
import static plaid.PreludeParser.RandomExprContext;
import static plaid.PreludeParser.SecretExprContext;
import static plaid.PreludeParser.StrContext;
import static plaid.PreludeParser.TimesExprContext;
import static plaid.PreludeParser.NumContext;

public class PreludeExpressionVisitor extends PreludeBaseVisitor<PreludeExpression> {

    private final Stack<PreludeExpression> indexes = new Stack<>();

    @Override
    public PreludeExpression visitParenPExpr(ParenPExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public PreludeExpression visitLetExpr(LetExprContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreludeExpression visitAtExpr(AtExprContext ctx) {
        indexes.push(visit(ctx.expr(1)));
        PreludeExpression result = visit(ctx.expr(0));
        indexes.pop();
        return result;
    }

    @Override
    public PreludeExpression visitFieldExpr(FieldExprContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PreludeExpression visitFieldSelectExpr(FieldSelectExprContext ctx) {
        return new FieldSelectExpr(visit(ctx.expr()), new Identifier(ctx.ident().getText()));
    }

    @Override
    public SecretExpr visitSecretExpr(SecretExprContext ctx) {
        return new SecretExpr(visit(ctx.expr()), indexes.peek());
    }

    @Override
    public RandomExpr visitRandomExpr(RandomExprContext ctx) {
        return new RandomExpr(visit(ctx.expr()), indexes.peek());
    }

    @Override
    public MessageExpr visitMessageExpr(MessageExprContext ctx) {
        return new MessageExpr(visit(ctx.expr()), indexes.peek());
    }

    @Override
    public PublicExpr visitPublicExpr(PublicExprContext ctx) {
        return new PublicExpr(visit(ctx.expr()));
    }

    @Override
    public OutputExpr visitOutputExpr(OutputExprContext ctx) {
        return new OutputExpr(indexes.peek());
    }

    @Override
    public PreludeExpression visitPlusMinusExpr(PlusMinusExprContext ctx) {
        PreludeExpression e0 = visit(ctx.expr(0));
        PreludeExpression e1 = visit(ctx.expr(1));
        return ctx.pmop().getText().equals("+") ? new PlusExpr(e0, e1) : new MinusExpr(e0, e1);
    }

    @Override
    public ConcatExpr visitConcatExpr(ConcatExprContext ctx) {
        return new ConcatExpr(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public TimesExpr visitTimesExpr(TimesExprContext ctx) {
        return new TimesExpr(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public Str visitStr(StrContext ctx) {
        return new Str(ctx.getText().replaceAll("\"", ""));
    }

    @Override
    public PreludeExpression visitNum(NumContext ctx) {
        return new Num(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Identifier visitIdentExpr(IdentExprContext ctx) {
        return new Identifier(ctx.getText());
    }

}
