package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser.AssertCommandContext;
import plaid.PreludeParser.AssignCommandContext;
import plaid.PreludeParser.CommandListContext;
import plaid.PreludeParser.FunctionCallCommandContext;
import plaid.PreludeParser.LetCommandContext;
import plaid.ast.AssertCmd;
import plaid.ast.AssignCmd;
import plaid.ast.ListCmd;
import plaid.ast.CallCmd;
import plaid.ast.Identifier;
import plaid.ast.LetCmd;
import plaid.ast.Cmd;

public class CommandVisitor extends PreludeBaseVisitor<Cmd> {

    @Override
    public CallCmd visitFunctionCallCommand(FunctionCallCommandContext ctx) {
        return new CallCmd(
                new Identifier(ctx.ident().getText()),
                ctx.expr().stream().map(Loader::toExpression).toList());
    }

    @Override
    public AssignCmd visitAssignCommand(AssignCommandContext ctx) {
        return new AssignCmd(
                Loader.toExpression(ctx.expr(0)),
                Loader.toExpression(ctx.expr(1)));
    }

    @Override
    public AssertCmd visitAssertCommand(AssertCommandContext ctx) {
        return new AssertCmd(
                Loader.toExpression(ctx.expr(0)),
                Loader.toExpression(ctx.expr(1)),
                Loader.toExpression(ctx.expr(2)));
    }

    @Override
    public ListCmd visitCommandList(CommandListContext ctx) {
        return new ListCmd(visit(ctx.command(0)), visit(ctx.command(1)));
    }

    @Override
    public LetCmd visitLetCommand(LetCommandContext ctx) {
        return new LetCmd(
                new Identifier(ctx.ident().getText()),
                Loader.toExpression(ctx.expr()),
                Loader.toCommand(ctx.command()));
    }

}
