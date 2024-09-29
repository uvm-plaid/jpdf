package plaid.ast;

import java.util.Objects;

public class ExprFunction implements PreludeFunction{
    private final Identifier fname;
    private final Identifier y;
    private final PreludeExpression e;

    public ExprFunction(Identifier fname, Identifier y, PreludeExpression e){
        this.fname = fname;
        this.y = y;
        this.e = e;
    }

    public Identifier getFname() {
        return fname;
    }

    public Identifier getY() {
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
}