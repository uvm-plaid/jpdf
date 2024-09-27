package plaid.ast;

public class OutputVar implements OvertureVariable{
    private final Identifier out;
    private IndexValue i;

    public OutputVar(Identifier out, IndexValue i){
        this.out = out;
        this.i = i;
    }

    public Identifier getR(){
        return out;
    }

    public IndexValue getI(){
        return i;
    }
}
