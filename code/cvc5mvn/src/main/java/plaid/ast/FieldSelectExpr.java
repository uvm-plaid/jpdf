package plaid.ast;

public class FieldSelectExpr implements PreludeExpression{
    private final PreludeExpression e;
    private final Identifier l;

    public FieldSelectExpr(PreludeExpression e, Identifier l){
        this.e = e;
        this.l = l;
    }

    public PreludeExpression getE() {
        return e;
    }

    public Identifier getL() {
        return l;
    }
}
