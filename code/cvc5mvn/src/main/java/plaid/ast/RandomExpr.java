package plaid.ast;

public class RandomExpr implements PreludeExpression{
    private final Identifier r;
    private final PreludeExpression e;

    public RandomExpr(Identifier r, PreludeExpression e){
        this.r = r;
        this.e = e;
    }

    public Identifier getR(){
        return r;
    }

    public PreludeExpression getE(){
        return e;
    }
}
