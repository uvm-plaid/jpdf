package plaid.ast;

public class RandomMemory implements OvertureExpression{
    private final Identifier r;
    private final Str w;

    public RandomMemory(Identifier r, Str w){
        this.r = r;
        this.w = w;
    }

    public Identifier getR(){
        return r;
    }

    public Str getW(){
        return w;
    }
}
