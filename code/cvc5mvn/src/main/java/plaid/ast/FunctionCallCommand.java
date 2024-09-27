package plaid.ast;

import java.util.List;

public class FunctionCallCommand implements PreludeCommand{
    private final Identifier fname;
    private final List<PreludeExpression> functionCalls;

    public FunctionCallCommand(Identifier fname, List<PreludeExpression> functionCalls){
        this.fname = fname;
        this.functionCalls = functionCalls;
    }

    public Identifier getFname() {
        return fname;
    }

    public List<PreludeExpression> getFunctionCalls() {
        return functionCalls;
    }
}
