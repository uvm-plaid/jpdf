package plaid.ast;

import java.util.List;
import java.util.Objects;

public class FieldSelectExpr implements PreludeExpression{
    private final PreludeExpression e;
    private final Identifier l;

    public FieldSelectExpr(PreludeExpression e, Identifier l){
        this.e = e;
        this.l = l;
    }

    public PreludeExpression getE() {
        return e;
    }

    public Identifier getL() {
        return l;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldSelectExpr that = (FieldSelectExpr) o;
        return Objects.equals(e, that.e) && Objects.equals(l, that.l);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e, l);
    }

    @Override
    public Iterable<Node> children() {
        return List.of(e, l);
    }

    @Override
    public String toString() {
        return "FieldSelectExpr{" +
                "e=" + e +
                ", l=" + l +
                '}';
    }

    @Override
    public String prettyPrint(){
        throw new UnsupportedOperationException();
    }
}
