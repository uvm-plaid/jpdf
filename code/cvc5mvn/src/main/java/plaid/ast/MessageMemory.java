package plaid.ast;

public class MessageMemory implements OvertureExpression{
    private final Identifier m;
    private final Str w;

    public MessageMemory(Identifier m, Str w){
        this.m = m;
        this.w = w;
    }

    public Identifier getM(){
        return m;
    }

    public Str getW(){
        return w;
    }
}
