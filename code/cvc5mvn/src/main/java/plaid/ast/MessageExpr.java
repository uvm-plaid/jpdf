package plaid.ast;

import java.util.Objects;

public class MessageExpr implements PreludeExpression{
    private final PreludeExpression e;
    private final IndexValue i;

    public MessageExpr(PreludeExpression e, IndexValue i){
        this.e = e;
        this.i = i;
    }

    public PreludeExpression getE(){
        return e;
    }

    public IndexValue getI() {
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageExpr that = (MessageExpr) o;
        return Objects.equals(e, that.e);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(e);
    }
}
