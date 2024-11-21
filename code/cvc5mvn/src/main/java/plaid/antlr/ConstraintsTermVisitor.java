package plaid.antlr;

import plaid.ConstraintsParser;
import plaid.constraints.ast.*;
import plaid.ConstraintsBaseVisitor;

public class ConstraintsTermVisitor extends ConstraintsBaseVisitor<ConstraintsTerm>{
    @Override
    public ConstraintsTerm visitParenConstraintsTerm(ConstraintsParser.ParenConstraintsTermContext ctx) {
        return visit(ctx.constraintsTerm());
    }

    @Override
    public ConstraintsTerm visitMinusConstraintsTerm(ConstraintsParser.MinusConstraintsTermContext ctx) {
        return new MinusConstraintsTerm(visit(ctx.constraintsTerm()));
    }

    @Override
    public ConstraintsTerm visitPlusConstraintsTerm(ConstraintsParser.PlusConstraintsTermContext ctx) {
        return new PlusConstraintsTerm(visit(ctx.constraintsTerm(0)), visit(ctx.constraintsTerm(1)));
    }

    @Override
    public ConstraintsTerm visitTimesConstraintsTerm(ConstraintsParser.TimesConstraintsTermContext ctx) {
        return new TimesConstraintsTerm(visit(ctx.constraintsTerm(0)), visit(ctx.constraintsTerm(1)));
    }

    @Override
    public ConstraintsTerm visitOutputConstraintsTerm(ConstraintsParser.OutputConstraintsTermContext ctx) {
        return new OutputConstraintTerm(Integer.parseInt(ctx.VALUE().getText()));
    }

    @Override
    public ConstraintsTerm visitMessageConstraintsTerm(ConstraintsParser.MessageConstraintsTermContext ctx) {
        return new MessageConstraintsTerm(ctx.STRING().getText().replaceAll("\"", ""), Integer.parseInt(ctx.VALUE().getText()));
    }

    @Override
    public ConstraintsTerm visitSecretConstraintsTerm(ConstraintsParser.SecretConstraintsTermContext ctx) {
        return new SecretConstraintsTerm(ctx.STRING().getText().replaceAll("\"", ""), Integer.parseInt(ctx.VALUE().getText()));
    }

    @Override
    public ConstraintsTerm visitRandomConstraintsTerm(ConstraintsParser.RandomConstraintsTermContext ctx) {
        return new RandomConstraintsTerm(ctx.STRING().getText().replaceAll("\"", ""), Integer.parseInt(ctx.VALUE().getText()));
    }

    @Override
    public ConstraintsTerm visitPublicConstraintsTerm(ConstraintsParser.PublicConstraintsTermContext ctx) {
        return new PublicConstraintsTerm(ctx.STRING().getText().replaceAll("\"", ""));
    }
}

