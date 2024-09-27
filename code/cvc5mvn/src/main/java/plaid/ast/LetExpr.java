package plaid.ast;

public class LetExpr implements PreludeExpression{
    private final Identifier y;
    private final PreludeExpression e1;
    private final PreludeExpression e2;

    public LetExpr(Identifier y, PreludeExpression e1,  PreludeExpression e2){
        this.y = y;
        this.e1 = e1;
        this.e2 = e2;
    }



    public Identifier getY() {
        return y;
    }

    public PreludeExpression getE1() {
        return e1;
    }



    public PreludeExpression getE2() {
        return e2;
    }

}
