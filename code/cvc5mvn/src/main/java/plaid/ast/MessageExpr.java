package plaid.ast;

public class MessageExpr implements PreludeExpression{
    private final Identifier m;
    private final PreludeExpression e;

    public MessageExpr(Identifier m, PreludeExpression e){
        this.m = m;
        this.e = e;
    }

    public Identifier getM(){
        return m;
    }

    public PreludeExpression getE(){
        return e;
    }
}
