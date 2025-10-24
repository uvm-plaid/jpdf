package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser.AssertCommandContext;
import plaid.PreludeParser.AssignCommandContext;
import plaid.PreludeParser.CommandListContext;
import plaid.PreludeParser.FunctionCallCommandContext;
import plaid.PreludeParser.LetCommandContext;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.CommandList;
import plaid.ast.FunctionCallCommand;
import plaid.ast.Identifier;
import plaid.ast.LetCommand;
import plaid.ast.PreludeCommand;

public class CommandVisitor extends PreludeBaseVisitor<PreludeCommand> {

    @Override
    public FunctionCallCommand visitFunctionCallCommand(FunctionCallCommandContext ctx) {
        return new FunctionCallCommand(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(Loader::toExpression).toList());
    }

    @Override
    public AssignCommand visitAssignCommand(AssignCommandContext ctx) {
        return new AssignCommand(
                Loader.toExpression(ctx.expr(0)),
                Loader.toExpression(ctx.expr(1)));
    }

    @Override
    public AssertCommand visitAssertCommand(AssertCommandContext ctx) {
        return new AssertCommand(
                Loader.toExpression(ctx.expr(0)),
                Loader.toExpression(ctx.expr(1)),
                Loader.toExpression(ctx.expr(2)));
    }

    @Override
    public CommandList visitCommandList(CommandListContext ctx) {
        return new CommandList(visit(ctx.command(0)), visit(ctx.command(1)));
    }

    @Override
    public LetCommand visitLetCommand(LetCommandContext ctx) {
        return new LetCommand(
                new Identifier(ctx.ident().getText()),
                Loader.toExpression(ctx.expr()),
                Loader.toCommand(ctx.command()));
    }

}
