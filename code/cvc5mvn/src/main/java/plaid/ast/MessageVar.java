package plaid.ast;

import java.util.Objects;

@Deprecated
public class MessageVar implements OvertureVariable{
    private final Identifier m;
    private final Str w;
    private IndexValue i;

    public MessageVar(Identifier m, Str w, IndexValue i){
        this.m = m;
        this.w = w;
        this.i = i;
    }

    public Identifier getM(){
        return m;
    }

    public Str getW(){
        return w;
    }

    public IndexValue getI(){
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageVar that = (MessageVar) o;
        return Objects.equals(m, that.m) && Objects.equals(w, that.w) && Objects.equals(i, that.i);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m, w, i);
    }
}
