package plaid.ast;

import java.util.Objects;

public class SecretExpr implements MemoryExpr {
    private final PreludeExpression e;
    private final PreludeExpression i;

    public SecretExpr(PreludeExpression e, PreludeExpression i){
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
        SecretExpr that = (SecretExpr) o;
        return Objects.equals(e, that.e) && Objects.equals(i, that.i);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e, i);
    }
}
