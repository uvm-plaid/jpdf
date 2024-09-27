package plaid.ast;
import java.util.List;

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
}
