package plaid.ast;

public class Plus implements OvertureExpression{

    private final OvertureExpression e1;
    private final OvertureExpression e2;

    public Plus(OvertureExpression e1, OvertureExpression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public OvertureExpression getE1() {
        return e1;
    }

    public OvertureExpression getE2() {
        return e2;
    }
}
