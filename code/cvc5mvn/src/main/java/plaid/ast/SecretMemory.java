package plaid.ast;

@Deprecated
public class SecretMemory implements OvertureExpression{
    private final Identifier s;
    private final Str w;

    public SecretMemory(Identifier s, Str w){
        this.s = s;
        this.w = w;
    }

    public Identifier getS(){
        return s;
    }

    public Str getW(){
        return w;
    }

    @Override
    public Iterable<Node> children() {
        throw new UnsupportedOperationException();
    }
}
