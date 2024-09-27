package plaid.ast;

public class AssertCommand implements  PreludeCommand{
    private final PreludeExpression e1;
    private final PreludeExpression e2;
    private final PreludeExpression e3;

    public AssertCommand(PreludeExpression e1, PreludeExpression e2, PreludeExpression e3){
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public PreludeExpression getE1() {
        return e1;
    }

    public PreludeExpression getE2() {
        return e2;
    }

    public PreludeExpression getE3() {
        return e3;
    }
}
