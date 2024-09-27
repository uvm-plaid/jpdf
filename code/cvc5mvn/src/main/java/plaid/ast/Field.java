package plaid.ast;

public class Field extends Value{
    private final Identifier l;
    private final Value v;

    // constructor
    public Field(Identifier l, Value v){
        this.l = l;
        this.v = v;
    }

    public Identifier getL(){
        return l;
    }

    public Value getV(){
        return v;
    }
}
