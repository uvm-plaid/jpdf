package plaid.ast;

@Deprecated
public class RandomVar implements OvertureVariable{
    private final Identifier r;
    private final Str w;
    private IndexValue i;

    public RandomVar(Identifier r, Str w, IndexValue i){
        this.r = r;
        this.w = w;
        this.i = i;
    }

    public Identifier getR(){
        return r;
    }

    public Str getW(){
        return w;
    }

    public IndexValue getI(){
        return i;
    }
}
