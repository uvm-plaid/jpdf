package plaid.ast;

import java.util.Objects;

public class PublicExpr implements MemoryExpr {
    private final PreludeExpression e;

    public PublicExpr(PreludeExpression e){
        this.e = e;
    }

    public PreludeExpression getE(){
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicExpr that = (PublicExpr) o;
        return Objects.equals(e, that.e);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(e);
    }
}
