package plaid.antlr;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser.AssertCommandContext;
import plaid.PreludeParser.AssignCommandContext;
import plaid.PreludeParser.FunctionCallCommandContext;
import plaid.ast.AssignCommand;
import plaid.ast.PreludeCommand;

public class PreludeCommandVisitor extends PreludeBaseVisitor<PreludeCommand> {

    @Override
    public PreludeCommand visitFunctionCallCommand(FunctionCallCommandContext ctx) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

    @Override
    public AssignCommand visitAssignCommand(AssignCommandContext ctx) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

    @Override
    public PreludeCommand visitAssertCommand(AssertCommandContext ctx) {
        // TODO Implement me
        throw new UnsupportedOperationException();
    }

}
