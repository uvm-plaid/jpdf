package plaid.ast;

@Deprecated
public class SecretVar implements OvertureVariable{
    private final Identifier s;
    private final Str w;
    private IndexValue i;

    public SecretVar(Identifier s, Str w, IndexValue i){
        this.s = s;
        this.w = w;
        this.i = i;
    }

    public Identifier getS(){
        return s;
    }

    public Str getW(){
        return w;
    }

    public IndexValue getI(){
        return i;
    }
}
