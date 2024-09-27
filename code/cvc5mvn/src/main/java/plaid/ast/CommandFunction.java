package plaid.ast;

import java.util.Objects;

public class CommandFunction implements PreludeFunction{
    private final Identifier fname;
    private final Identifier y;
    private final PreludeCommand c;

    public CommandFunction(Identifier fname, Identifier y, PreludeCommand c){
        this.fname = fname;
        this.y = y;
        this.c = c;
    }

    public Identifier getFname() {
        return fname;
    }

    public Identifier getY() {
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
}
