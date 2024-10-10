package plaid.ast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionCallExpr implements PreludeExpression{
    private final Identifier fname;
    private final List<PreludeExpression> parameters;

    public FunctionCallExpr(Identifier fname, List<PreludeExpression> functionCalls){
        this.fname = fname;
        this.parameters = functionCalls;
    }

    public Identifier getFname() {
        return fname;
    }

    public List<PreludeExpression> getParameters() {
        return parameters;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionCallExpr that = (FunctionCallExpr) o;
        return Objects.equals(fname, that.fname) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fname, parameters);
    }

    @Override
    public Iterable<Node> children() {
        List<Node> result = new ArrayList<>();
        result.add(fname);
        result.addAll(parameters);
        return result;
    }

}
