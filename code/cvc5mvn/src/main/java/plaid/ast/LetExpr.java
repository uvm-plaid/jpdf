package plaid.ast;

import java.util.List;
import java.util.Objects;

public class LetExpr implements PreludeExpression{
    private final Identifier y;
    private final PreludeExpression e1;
    private final PreludeExpression e2;

    public LetExpr(Identifier y, PreludeExpression e1,  PreludeExpression e2){
        this.y = y;
        this.e1 = e1;
        this.e2 = e2;
    }



    public Identifier getY() {
        return y;
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
        LetExpr letExpr = (LetExpr) o;
        return Objects.equals(y, letExpr.y) && Objects.equals(e1, letExpr.e1) && Objects.equals(e2, letExpr.e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, e1, e2);
    }

    @Override
    public Iterable<Node> children() {
        return List.of(y, e1, e2);
    }

    @Override
    public String toString() {
        return "LetExpr{" +
                "y=" + y +
                ", e1=" + e1 +
                ", e2=" + e2 +
                '}';
    }

    @Override
    public String prettyPrint(){
        throw new UnsupportedOperationException();
    }
}
