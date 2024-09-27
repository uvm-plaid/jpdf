package plaid.ast;

@Deprecated
public class PublicMemory implements OvertureExpression{
    private final Identifier p;
    private final Str w;

    public PublicMemory(Identifier p, Str w){
        this.p = p;
        this.w = w;
    }

    public Identifier getP(){
        return p;
    }

    public Str getW(){
        return w;
    }
}
