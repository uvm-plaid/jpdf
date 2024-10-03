package plaid.ast;

import java.util.Objects;

@Deprecated
public class Minus implements OvertureExpression {
    private OvertureExpression e;

    public Minus(OvertureExpression e){
        this.e = e;
    }

    public OvertureExpression getE(){
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Minus minus = (Minus) o;
        return Objects.equals(e, minus.e);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(e);
    }

    @Override
    public Iterable<Node> children() {
        throw new UnsupportedOperationException();
    }
}
