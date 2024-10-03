package plaid.ast;

import java.util.Objects;

public class IndexValue extends Value{
    private final int i;

    public IndexValue(int i){
        this.i = i;
    }

    public int getI(){
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexValue that = (IndexValue) o;
        return i == that.i;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(i);
    }

    @Override
    public Iterable<Node> children() {
        throw new UnsupportedOperationException();
    }
}
