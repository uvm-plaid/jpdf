package plaid.ast;

import java.util.List;
import java.util.Objects;

public class AtExpr implements PreludeExpression{
    private final PreludeExpression e1;
    private final PreludeExpression e2;

    public AtExpr(PreludeExpression e1, PreludeExpression e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public PreludeExpression getE1() {
        return e1;
    }

    public PreludeExpression getE2() {
        return e2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtExpr atExpr = (AtExpr) o;
        return Objects.equals(e1, atExpr.e1) && Objects.equals(e2, atExpr.e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }

    @Override
    public Iterable<Node> children() {
        return List.of(e1, e2);
    }

    @Override
    public String toString() {
        return "AtExpr{" +
                "e1=" + e1 +
                ", e2=" + e2 +
                '}';
    }

    @Override
    public String prettyPrint(){
        return e1.prettyPrint() + "@" + e2.prettyPrint();
    }
}
