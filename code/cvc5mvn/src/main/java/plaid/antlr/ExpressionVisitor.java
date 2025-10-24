package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser;
import plaid.PreludeParser.FunctionCallExprContext;
import plaid.PreludeParser.MinusExprContext;
import plaid.PreludeParser.PlusExprContext;
import plaid.ast.*;

import java.util.TreeMap;
import java.util.stream.Collectors;

import static plaid.PreludeParser.AtExprContext;
import static plaid.PreludeParser.ConcatExprContext;
import static plaid.PreludeParser.FieldExprContext;
import static plaid.PreludeParser.FieldSelectExprContext;
import static plaid.PreludeParser.IdentExprContext;
import static plaid.PreludeParser.LetExprContext;
import static plaid.PreludeParser.MessageExprContext;
import static plaid.PreludeParser.NumContext;
import static plaid.PreludeParser.OutputExprContext;
import static plaid.PreludeParser.ParenPExprContext;
import static plaid.PreludeParser.PublicExprContext;
import static plaid.PreludeParser.RandomExprContext;
import static plaid.PreludeParser.SecretExprContext;
import static plaid.PreludeParser.StrContext;
import static plaid.PreludeParser.TimesExprContext;
import static plaid.PreludeParser.OTExprContext;

public class ExpressionVisitor extends PreludeBaseVisitor<Expr> {

    @Override
    public Expr visitParenPExpr(ParenPExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public FunctionCallExpr visitFunctionCallExpr(FunctionCallExprContext ctx) {
        return new FunctionCallExpr(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(this::visit).toList());
    }

    @Override
    public LetExpr visitLetExpr(LetExprContext ctx) {
        return new LetExpr(new Identifier(ctx.ident().getText()), visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public AtExpr visitAtExpr(AtExprContext ctx) {
        return new AtExpr(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public FieldExpr visitFieldExpr(FieldExprContext ctx) {
        TreeMap<Identifier, Expr> map = new TreeMap<>(ctx.flddecl().stream().collect(Collectors.toMap(
                x -> new Identifier(x.ident().getText()),
                x -> visit(x.expr()))));
        
        return new FieldExpr(map);
    }

    @Override
    public FieldSelectExpr visitFieldSelectExpr(FieldSelectExprContext ctx) {
        return new FieldSelectExpr(visit(ctx.expr()), new Identifier(ctx.ident().getText()));
    }

    @Override
    public SecretExpr visitSecretExpr(SecretExprContext ctx) {
        return new SecretExpr(visit(ctx.expr()));
    }

    @Override
    public RandomExpr visitRandomExpr(RandomExprContext ctx) {
        return new RandomExpr(visit(ctx.expr()));
    }

    @Override
    public MessageExpr visitMessageExpr(MessageExprContext ctx) {
        return new MessageExpr(visit(ctx.expr()));
    }

    @Override
    public PublicExpr visitPublicExpr(PublicExprContext ctx) {
        return new PublicExpr(visit(ctx.expr()));
    }

    @Override
    public OutputExpr visitOutputExpr(OutputExprContext ctx) {
        return new OutputExpr();
    }

    @Override
    public PlusExpr visitPlusExpr(PlusExprContext ctx) {
        return new PlusExpr(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public MinusExpr visitMinusExpr(MinusExprContext ctx) {
        return new MinusExpr(visit(ctx.expr()));
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
    public Num visitNum(NumContext ctx) {
        return new Num(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Identifier visitIdentExpr(IdentExprContext ctx) {
        return new Identifier(ctx.getText());
    }

    @Override
    public OTExpr visitOTExpr(OTExprContext ctx) {
        return new OTExpr(visit(ctx.expr(0)), visit(ctx.expr(1)), visit(ctx.expr(2)), visit(ctx.expr(3)));
    }

    @Override
    public Expr visitOTFourExpr(PreludeParser.OTFourExprContext ctx) {
        return new OTFourExpr(visit(ctx.expr(0)), visit(ctx.expr(1)), visit(ctx.expr(2)), visit(ctx.expr(3)), visit(ctx.expr(4)), visit(ctx.expr(5)), visit(ctx.expr(6)));
    }
}
