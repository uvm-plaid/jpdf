package plaid.ast;

public class SecretExpr implements PreludeExpression{
    private final PreludeExpression e;

    public SecretExpr(PreludeExpression e){
        this.e = e;
    }

    public PreludeExpression getE(){
        return e;
    }
}
