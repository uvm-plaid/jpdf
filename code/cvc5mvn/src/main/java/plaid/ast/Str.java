package plaid.ast;

public class Str extends Value {

    private final String str;

    public Str(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
