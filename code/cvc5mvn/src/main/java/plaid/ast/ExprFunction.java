package plaid.ast;

public class ExprFunction implements PreludeFunction{
    private final Identifier fname;
    private final Identifier y;
    private final PreludeExpression e;

    public ExprFunction(Identifier fname, Identifier y, PreludeExpression e){
        this.fname = fname;
        this.y = y;
        this.e = e;
    }

    public Identifier getFname() {
        return fname;
    }

    public Identifier getY() {
        return y;
    }

    public PreludeExpression getE() {
        return e;
    }
}
