package plaid.ast;

public class MessageVar implements OvertureVariable{
    private final Identifier m;
    private final Str w;
    private IndexValue i;

    public MessageVar(Identifier m, Str w, IndexValue i){
        this.m = m;
        this.w = w;
        this.i = i;
    }

    public Identifier getM(){
        return m;
    }

    public Str getW(){
        return w;
    }

    public IndexValue getI(){
        return i;
    }
}
