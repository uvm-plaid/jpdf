package plaid.ast;

public class PublicVar implements OvertureVariable{
    private final Identifier p;
    private final Str w;
    private IndexValue i;

    public PublicVar(Identifier p, Str w, IndexValue i){
        this.p = p;
        this.w = w;
        this.i = i;
    }

    public Identifier getP(){
        return p;
    }

    public Str getW(){
        return w;
    }

    public IndexValue getI(){
        return i;
    }

}
