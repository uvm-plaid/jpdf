package plaid.ast;

import java.util.List;
import java.util.Objects;

public class Str implements PreludeExpression {

    private final String str;

    public Str(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Str str1 = (Str) o;
        return Objects.equals(str, str1.str);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(str);
    }

    @Override
    public Iterable<Node> children() {
        return List.of();
    }

    @Override
    public String toString() {
        return "Str{" +
                "str='" + str + '\'' +
                '}';
    }

    @Override
    public String prettyPrint(){
        return getStr();
    }
}
