package plaid.ast;

public class PublicExpr implements PreludeExpression {
    private final PreludeExpression e;

    public PublicExpr(PreludeExpression e){
        this.e = e;
    }

    public PreludeExpression getE(){
        return e;
    }
}
