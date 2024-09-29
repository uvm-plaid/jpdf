package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser.AssertCommandContext;
import plaid.PreludeParser.AssignCommandContext;
import plaid.PreludeParser.FunctionCallCommandContext;
import plaid.ast.AssignCommand;
import plaid.ast.FunctionCallCommand;
import plaid.ast.Identifier;
import plaid.ast.PreludeCommand;

public class PreludeCommandVisitor extends PreludeBaseVisitor<PreludeCommand> {

    @Override
    public FunctionCallCommand visitFunctionCallCommand(FunctionCallCommandContext ctx) {
        return new FunctionCallCommand(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(PreludeLoader::toExpression).toList());
    }

    @Override
    public AssignCommand visitAssignCommand(AssignCommandContext ctx) {
        return new AssignCommand(
                PreludeLoader.toExpression(ctx.expr(0)),
                PreludeLoader.toExpression(ctx.expr(1)));
    }

    @Override
    public PreludeCommand visitAssertCommand(AssertCommandContext ctx) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

}
