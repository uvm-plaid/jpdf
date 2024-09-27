package plaid.ast;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionCallCommand that = (FunctionCallCommand) o;
        return Objects.equals(fname, that.fname) && Objects.equals(functionCalls, that.functionCalls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fname, functionCalls);
    }
}
