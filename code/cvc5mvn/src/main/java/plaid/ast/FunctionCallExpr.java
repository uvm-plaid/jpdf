package plaid.ast;
import java.util.List;
import java.util.Objects;

public class FunctionCallExpr implements PreludeExpression{
    private final Identifier fname;
    private final List<PreludeExpression> functionCalls;

    public FunctionCallExpr(Identifier fname, List<PreludeExpression> functionCalls){
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
        FunctionCallExpr that = (FunctionCallExpr) o;
        return Objects.equals(fname, that.fname) && Objects.equals(functionCalls, that.functionCalls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fname, functionCalls);
    }
}
