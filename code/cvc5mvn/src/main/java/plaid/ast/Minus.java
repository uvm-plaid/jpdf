package plaid.ast;

public class Minus implements OvertureExpression {
    private OvertureExpression e;

    public Minus(OvertureExpression e){
        this.e = e;
    }

    public OvertureExpression getE(){
        return e;
    }
}
