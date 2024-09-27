package plaid.ast;

import java.util.Objects;

public class RandomExpr implements PreludeExpression {

    private final PreludeExpression e;
    private final IndexValue i;

    public RandomExpr(PreludeExpression e, IndexValue i){
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
        RandomExpr that = (RandomExpr) o;
        return Objects.equals(e, that.e) && Objects.equals(i, that.i);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e, i);
    }
}
