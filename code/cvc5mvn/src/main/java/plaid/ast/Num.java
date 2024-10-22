package plaid.ast;

import java.util.List;
import java.util.Objects;

// value in Overture
public class Num implements OvertureExpression {

    private final int num;

    public Num(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Num num1 = (Num) o;
        return num == num1.num;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(num);
    }

    @Override
    public Iterable<Node> children() {
        return List.of();
    }


    @Override
    public String toString() {
        return "Num{" +
                "num=" + num +
                '}';
    }

    @Override
    public String prettyPrint(){
        return String.valueOf(num);
    }
}
