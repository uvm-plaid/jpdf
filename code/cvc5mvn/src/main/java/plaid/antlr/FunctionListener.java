package plaid.antlr;

import plaid.PreludeBaseListener;
import plaid.PreludeParser.CommandFuncContext;
import plaid.PreludeParser.ConstraintFuncContext;
import plaid.PreludeParser.ExprFuncContext;
import plaid.PreludeParser.IdentContext;
import plaid.PreludeParser.TypedIdentContext;
import plaid.ast.*;
import plaid.ast.CommandFunction;
import plaid.ast.ConstraintFunction;
import plaid.ast.ExprFunction;
import plaid.ast.Identifier;

import java.util.ArrayList;
import java.util.List;

public class FunctionListener extends PreludeBaseListener {

    private final List<ExprFunction> exprFunctions = new ArrayList<>();
    private final List<CommandFunction> commandFunctions = new ArrayList<>();
    private final List<ConstraintFunction> constraintFunctions = new ArrayList<>();

    public List<ExprFunction> getExprFunctions() {
        return exprFunctions;
    }

    public List<CommandFunction> getCommandFunctions() {
        return commandFunctions;
    }

    public List<ConstraintFunction> getConstraintFunctions() {
        return constraintFunctions;
    }

    private List<Identifier> toIdentifiers(List<IdentContext> contexts) {
        return contexts.stream().skip(1).map(x -> new Identifier(x.getText())).toList();
    }

    private List<TypedIdentifier> toTypedIdentifiers(List<TypedIdentContext> typedIdentContexts){
        return typedIdentContexts.stream().map(x -> new TypedIdentifier(new Identifier(x.ident().getText()), Loader.toType(x.type()))).toList();
    }

    @Override
    public void enterExprFunc(ExprFuncContext ctx) {
        exprFunctions.add(new ExprFunction(
                new Identifier(ctx.ident(0).getText()), // fname
                toIdentifiers(ctx.ident()), // parameters
                Loader.toExpression(ctx.expr())));
    }

    @Override
    public void enterCommandFunc(CommandFuncContext ctx) {
        // command function may not have the precondition and postcondition
        // then interpret null constraint as T
       Constraint precondition = ctx.precondsection() == null? null : Loader.toConstraintExpression(ctx.precondsection().constraintExpr());
       Constraint postcondition = ctx.postcondsection() == null? null : Loader.toConstraintExpression(ctx.postcondsection().constraintExpr());     
        
        
        commandFunctions.add(new CommandFunction(
                new Identifier(ctx.ident().getText()), // fname
                toTypedIdentifiers(ctx.typedIdent()),
                Loader.toCommand(ctx.command()),
                precondition,
                postcondition));
    }

    @Override
    public void enterConstraintFunc(ConstraintFuncContext ctx) {
        constraintFunctions.add(new ConstraintFunction(
                new Identifier(ctx.ident(0).getText()),
                toIdentifiers(ctx.ident()),
                Loader.toConstraintExpression(ctx.constraintExpr())));
    }


}
