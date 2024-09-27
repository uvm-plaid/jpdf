package plaid.ast;

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
}
