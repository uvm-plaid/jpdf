package plaid.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ExprFunction implements PreludeFunction{
    private final Identifier fname;
    private final List<Identifier> y;
    private final PreludeExpression e;

    public ExprFunction(Identifier fname, List<Identifier> y, PreludeExpression e){
        this.fname = fname;
        this.y = y;
        this.e = e;
    }

    public Identifier getFname() {
        return fname;
    }

    public List<Identifier> getY() {
        return y;
    }

    public PreludeExpression getE() {
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExprFunction that = (ExprFunction) o;
        return Objects.equals(fname, that.fname) && Objects.equals(y, that.y) && Objects.equals(e, that.e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fname, y, e);
    }

    @Override
    public Iterable<Node> children() {
        Collection<Node> result = new ArrayList<>();
        result.add(fname);
        result.addAll(y);
        result.add(e);
        return result;
    }

    @Override
    public String toString() {
        return "ExprFunction{" +
                "fname=" + fname +
                ", y=" + y +
                ", e=" + e +
                '}';
    }
}
