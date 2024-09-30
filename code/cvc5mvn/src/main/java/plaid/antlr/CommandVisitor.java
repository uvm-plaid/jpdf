package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser.AssertCommandContext;
import plaid.PreludeParser.AssignCommandContext;
import plaid.PreludeParser.CommandListContext;
import plaid.PreludeParser.FunctionCallCommandContext;
import plaid.ast.AssertCommand;
import plaid.ast.AssignCommand;
import plaid.ast.CommandList;
import plaid.ast.FunctionCallCommand;
import plaid.ast.Identifier;
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
        ExpressionVisitor visitor = new ExpressionVisitor();
        visitor.setPartyIndex(Loader.toExpression(ctx.expr(2)));
        return new AssertCommand(
                visitor.visit(ctx.expr(0)),
                visitor.visit(ctx.expr(1)));
    }

    @Override
    public CommandList visitCommandList(CommandListContext ctx) {
        // TODO Either unit test or eliminate
        return new CommandList(ctx.command().stream().map(this::visit).toList());
    }

}
