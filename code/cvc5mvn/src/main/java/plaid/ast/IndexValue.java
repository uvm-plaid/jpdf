package plaid.ast;

public class IndexValue extends Value{
    private final IndexValue i;

    public IndexValue(IndexValue i){
        this.i = i;
    }

    public IndexValue getI(){
        return i;
    }

}
