package plaid.antlr;

import plaid.PreludeBaseListener;
import plaid.PreludeParser.CommandFuncContext;
import plaid.PreludeParser.ExprFuncContext;
import plaid.PreludeParser.IdentContext;
import plaid.ast.CommandFunction;
import plaid.ast.ExprFunction;
import plaid.ast.Identifier;

import java.util.ArrayList;
import java.util.List;

public class FunctionListener extends PreludeBaseListener {

    private final List<ExprFunction> exprFunctions = new ArrayList<>();
    private final List<CommandFunction> commandFunctions = new ArrayList<>();

    public List<ExprFunction> getExprFunctions() {
        return exprFunctions;
    }

    public List<CommandFunction> getCommandFunctions() {
        return commandFunctions;
    }

    private List<Identifier> toIdentifiers(List<IdentContext> contexts) {
        return contexts.stream().skip(1).map(x -> new Identifier(x.getText())).toList();
    }
    @Override
    public void enterExprFunc(ExprFuncContext ctx) {
        exprFunctions.add(new ExprFunction(
                new Identifier(ctx.ident(0).getText()),
                toIdentifiers(ctx.ident()),
                Loader.toExpression(ctx.expr())));
    }

    @Override
    public void enterCommandFunc(CommandFuncContext ctx) {
        commandFunctions.add(new CommandFunction(
                new Identifier(ctx.ident(0).getText()),
                toIdentifiers(ctx.ident()),
                Loader.toCommand(ctx.command())));
    }

}
