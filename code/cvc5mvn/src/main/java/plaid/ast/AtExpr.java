package plaid.ast;

public class AtExpr implements PreludeExpression{
    private final PreludeExpression e1;
    private final PreludeExpression e2;

    public AtExpr(PreludeExpression e1, PreludeExpression e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public PreludeExpression getE1() {
        return e1;
    }

    public PreludeExpression getE2() {
        return e2;
    }
}
