package plaid.ast;

import java.util.List;
import java.util.Objects;

public class MessageExpr implements MemoryExpr {
    private final PreludeExpression e;
    private final PreludeExpression i;

    public MessageExpr(PreludeExpression e, PreludeExpression i){
        this.e = e;
        this.i = i;
    }

    public PreludeExpression getE(){
        return e;
    }

    public PreludeExpression getI() {
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageExpr that = (MessageExpr) o;
        return Objects.equals(e, that.e) && Objects.equals(i, that.i);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e, i);
    }

    @Override
    public Iterable<Node> children() {
        return List.of(e, i);
    }

}
