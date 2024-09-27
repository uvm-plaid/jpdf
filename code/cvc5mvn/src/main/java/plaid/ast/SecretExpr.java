package plaid.ast;

public class SecretExpr implements PreludeExpression{
    private final PreludeExpression e;
    private final IndexValue i;

    public SecretExpr(PreludeExpression e, IndexValue i){
        this.e = e;
        this.i = i;
    }

    public PreludeExpression getE(){
        return e;
    }

    public IndexValue getI() {
        return i;
    }

}
