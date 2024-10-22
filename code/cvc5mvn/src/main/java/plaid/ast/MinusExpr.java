package plaid.ast;

import java.util.List;
import java.util.Objects;

public class MinusExpr implements PreludeExpression {

    private final PreludeExpression e;

    public MinusExpr(PreludeExpression e) {
        this.e = e;
    }

    public PreludeExpression getE() {
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinusExpr minusExpr = (MinusExpr) o;
        return Objects.equals(e, minusExpr.e);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(e);
    }

    @Override
    public Iterable<Node> children() {
        return List.of(e);
    }

    @Override
    public String toString() {
        return "MinusExpr{" +
                "e=" + e +
                '}';
    }

    @Override
    public String prettyPrint(){
        return "(" +  "-" + e.prettyPrint() + ")";
    }
}
