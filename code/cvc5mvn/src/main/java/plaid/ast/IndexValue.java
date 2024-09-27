package plaid.ast;

public class IndexValue extends Value{
    private final int i;

    public IndexValue(int i){
        this.i = i;
    }

    public int getI(){
        return i;
    }

}
