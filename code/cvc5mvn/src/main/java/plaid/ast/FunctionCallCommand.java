package plaid.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionCallCommand implements PreludeCommand{
    private final Identifier fname;
    private final List<PreludeExpression> parameters;

    public FunctionCallCommand(Identifier fname, List<PreludeExpression> parameters){
        this.fname = fname;
        this.parameters = parameters;
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
        FunctionCallCommand that = (FunctionCallCommand) o;
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

    @Override
    public String toString() {
        return "FunctionCallCommand{" +
                "fname=" + fname +
                ", functionCalls=" + parameters +
                '}';
    }

    @Override
    public String prettyPrint(){
        throw new UnsupportedOperationException();
    }
}
