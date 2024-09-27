package plaid.ast;

import java.util.Objects;

public class OutputExpr implements PreludeExpression {

    private final IndexValue i;

    public OutputExpr(IndexValue i) {
        this.i = i;
    }

    public IndexValue getI() {
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputExpr that = (OutputExpr) o;
        return Objects.equals(i, that.i);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(i);
    }
}
