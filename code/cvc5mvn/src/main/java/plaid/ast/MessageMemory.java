package plaid.ast;

import java.util.Objects;

@Deprecated
public class MessageMemory implements OvertureExpression{
    private final Identifier m;
    private final Str w;

    public MessageMemory(Identifier m, Str w){
        this.m = m;
        this.w = w;
    }

    public Identifier getM(){
        return m;
    }

    public Str getW(){
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageMemory that = (MessageMemory) o;
        return Objects.equals(m, that.m) && Objects.equals(w, that.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m, w);
    }
}
