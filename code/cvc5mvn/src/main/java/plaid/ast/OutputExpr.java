package plaid.ast;

public class OutputExpr implements PreludeExpression{
    private final Identifier out;


    public OutputExpr(Identifier out){
        this.out = out;
    }

    public Identifier getOut(){
        return out;
    }

}
