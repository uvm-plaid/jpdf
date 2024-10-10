package plaid.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CommandFunction implements PreludeFunction{
    private final Identifier fname;
    private final List<Identifier> y;
    private final PreludeCommand c;

    public CommandFunction(Identifier fname, List<Identifier> y, PreludeCommand c){
        this.fname = fname;
        this.y = y;
        this.c = c;
    }

    public Identifier getFname() {
        return fname;
    }

    public List<Identifier> getY() {
        return y;
    }

    public PreludeCommand getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandFunction that = (CommandFunction) o;
        return Objects.equals(fname, that.fname) && Objects.equals(y, that.y) && Objects.equals(c, that.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fname, y, c);
    }

    @Override
    public Iterable<Node> children() {
        Collection<Node> result = new ArrayList<>();
        result.add(fname);
        result.addAll(y);
        result.add(c);
        return result;
    }

    @Override
    public String toString() {
        return "CommandFunction{" +
                "fname=" + fname +
                ", y=" + y +
                ", c=" + c +
                '}';
    }
}
