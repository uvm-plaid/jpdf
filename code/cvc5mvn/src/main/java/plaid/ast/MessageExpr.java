package plaid.ast;

public class MessageExpr implements PreludeExpression{
    private final PreludeExpression e;

    public MessageExpr(PreludeExpression e){
        this.e = e;
    }

    public PreludeExpression getE(){
        return e;
    }
}
