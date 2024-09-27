package plaid.ast;

// value in Overture
public class Num implements OvertureExpression {

    private final int num;

    public Num(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
