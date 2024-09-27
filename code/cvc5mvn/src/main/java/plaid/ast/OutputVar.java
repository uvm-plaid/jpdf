package plaid.ast;

import java.util.Objects;

@Deprecated
public class OutputVar implements OvertureVariable{
    private final Identifier out;
    private IndexValue i;

    public OutputVar(Identifier out, IndexValue i){
        this.out = out;
        this.i = i;
    }

    public Identifier getR(){
        return out;
    }

    public IndexValue getI(){
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputVar outputVar = (OutputVar) o;
        return Objects.equals(out, outputVar.out) && Objects.equals(i, outputVar.i);
    }

    @Override
    public int hashCode() {
        return Objects.hash(out, i);
    }
}
