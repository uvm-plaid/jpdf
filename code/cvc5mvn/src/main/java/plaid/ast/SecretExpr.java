package plaid.ast;

public class SecretExpr implements PreludeExpression{
    private final Identifier s;
    private final PreludeExpression e;

    public SecretExpr(Identifier s, PreludeExpression e){
        this.s = s;
        this.e = e;
    }

    public Identifier getS(){
        return s;
    }

    public PreludeExpression getE(){
        return e;
    }
}
