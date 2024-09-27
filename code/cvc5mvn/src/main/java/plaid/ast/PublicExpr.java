package plaid.ast;

public class PublicExpr implements PreludeExpression{
    private final Identifier p;
    private final PreludeExpression e;

    public PublicExpr(Identifier p, PreludeExpression e){
        this.p = p;
        this.e = e;
    }

    public Identifier getP(){
        return p;
    }

    public PreludeExpression getE(){
        return e;
    }
}
