package plaid.ast;

public class RandomExpr implements PreludeExpression {

    private final PreludeExpression e;

    public RandomExpr(PreludeExpression e){
        this.e = e;
    }

    public PreludeExpression getE(){
        return e;
    }
}
