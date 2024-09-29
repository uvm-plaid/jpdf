package plaid.ast;

public class SecretExpr implements PreludeExpression{
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

}
